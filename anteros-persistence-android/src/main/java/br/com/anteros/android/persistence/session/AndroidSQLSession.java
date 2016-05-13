/*
 * Copyright 2016 Anteros Tecnologia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.anteros.android.persistence.session;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import br.com.anteros.android.persistence.sql.jdbc.SQLiteConnection;
import br.com.anteros.core.utils.ReflectionUtils;
import br.com.anteros.persistence.handler.EntityHandler;
import br.com.anteros.persistence.metadata.EntityCache;
import br.com.anteros.persistence.metadata.EntityCacheManager;
import br.com.anteros.persistence.metadata.EntityManaged;
import br.com.anteros.persistence.metadata.annotation.type.CallableType;
import br.com.anteros.persistence.metadata.descriptor.DescriptionColumn;
import br.com.anteros.persistence.metadata.descriptor.DescriptionField;
import br.com.anteros.persistence.metadata.identifier.Identifier;
import br.com.anteros.persistence.metadata.identifier.IdentifierGenerator;
import br.com.anteros.persistence.metadata.identifier.IdentifierGeneratorFactory;
import br.com.anteros.persistence.metadata.identifier.IdentifierPostInsert;
import br.com.anteros.persistence.parameter.NamedParameter;
import br.com.anteros.persistence.proxy.LazyLoadFactory;
import br.com.anteros.persistence.proxy.SimpleLazyLoadFactory;
import br.com.anteros.persistence.session.SQLPersister;
import br.com.anteros.persistence.session.SQLSession;
import br.com.anteros.persistence.session.SQLSessionFactory;
import br.com.anteros.persistence.session.SQLSessionListener;
import br.com.anteros.persistence.session.cache.Cache;
import br.com.anteros.persistence.session.context.SQLPersistenceContext;
import br.com.anteros.persistence.session.exception.SQLSessionException;
import br.com.anteros.persistence.session.impl.SQLPersistenceContextImpl;
import br.com.anteros.persistence.session.impl.SQLQueryImpl;
import br.com.anteros.persistence.session.lock.LockOptions;
import br.com.anteros.persistence.session.query.AbstractSQLRunner;
import br.com.anteros.persistence.session.query.ExpressionFieldMapper;
import br.com.anteros.persistence.session.query.SQLQuery;
import br.com.anteros.persistence.session.query.SQLQueryAnalyserAlias;
import br.com.anteros.persistence.session.query.ShowSQLType;
import br.com.anteros.persistence.session.query.TypedSQLQuery;
import br.com.anteros.persistence.sql.command.BatchCommandSQL;
import br.com.anteros.persistence.sql.command.CommandSQL;
import br.com.anteros.persistence.sql.dialect.DatabaseDialect;
import br.com.anteros.persistence.transaction.Transaction;
import br.com.anteros.persistence.transaction.TransactionFactory;

public class AndroidSQLSession implements SQLSession {

	private EntityCacheManager entityCacheManager;
	private DatabaseDialect dialect;
	private ShowSQLType[] showSql = { ShowSQLType.NONE };
	private boolean formatSql;
	private SQLSessionFactory sessionFactory;
	private SQLPersister persister;
	public final int DEFAULT_CACHE_SIZE = 200;
	private SQLPersistenceContext persistenceContext;
	private List<CommandSQL> commandQueue = new ArrayList<CommandSQL>();
	private Map<Object, Map<DescriptionColumn, IdentifierPostInsert>> cacheIdentifier = new LinkedHashMap<Object, Map<DescriptionColumn, IdentifierPostInsert>>();
	private Context context;
	private Transaction transaction;
	private List<SQLSessionListener> listeners = new ArrayList<SQLSessionListener>();
	public static int FIRST_RECORD = 0;
	private String clientId;
	private Connection connection;
	private AbstractSQLRunner queryRunner;
	private LazyLoadFactory lazyLoadFactory = new SimpleLazyLoadFactory();
	private final TransactionFactory transactionFactory;
	private int batchSize = 0;
	private int currentBatchSize;
	private Boolean validationActive=true;
	private Map<String, NextValControl> cacheSequenceNumbers = new HashMap<String, NextValControl>();

	public AndroidSQLSession(Context context, SQLSessionFactory sessionFactory, Connection connection,
			EntityCacheManager entityCacheManager,
			AbstractSQLRunner queryRunner, DatabaseDialect dialect, ShowSQLType[] showSql, boolean formatSql,
			TransactionFactory transactionFactory)
			throws Exception {
		if (!(connection instanceof SQLiteConnection))
			throw new SQLException("Objeto Connection não suportado. Use um objeto do tipo SQLiteConnection.");
		this.entityCacheManager = entityCacheManager;
		this.connection = connection;
		this.queryRunner = queryRunner;
		this.dialect = dialect;
		this.showSql = showSql;
		this.formatSql = formatSql;
		this.sessionFactory = sessionFactory;
		this.transactionFactory = transactionFactory;
		this.persistenceContext = new SQLPersistenceContextImpl(this, entityCacheManager);
		this.persister = new AndroidSQLPersister();
		this.context = context;
	}

	public void setEntityCacheManager(EntityCacheManager entityCacheManager) {
		this.entityCacheManager = entityCacheManager;
	}

	public void setPersistenceContext(SQLPersistenceContext persistenceContext) {
		this.persistenceContext = persistenceContext;
	}

	public <T> Identifier<T> getIdentifier(T owner) throws Exception {
		return Identifier.create(this, owner);
	}

	@Override
	public <T> Identifier<T> createIdentifier(Class<T> clazz) throws Exception {
		return Identifier.create(this, clazz);
	}

	@Override
	public Object save(Object object) throws Exception {
		return persister.save(this, object);
	}

	@Override
	public void save(Object[] object) throws Exception {
		for (Object obj : object)
			persister.save(this, obj);
	}

	@Override
	public void save(Class<?> clazz, String[] columns, String[] values) throws Exception {
		persister.save(this, clazz, columns, values);
	}

	@Override
	public void remove(Object object) throws Exception {
		persister.remove(this, object);
	}

	@Override
	public void remove(Object[] object) throws Exception {
		for (Object obj : object)
			persister.remove(this, obj);
	}

	public void removeAll(Class<?> clazz) {
		EntityCache entityCache = entityCacheManager.getEntityCache(clazz);
		((SQLiteConnection) connection).getDatabase().delete(entityCache.getTableName(), null, null);
	}

	public void flush() throws Exception {
		synchronized (commandQueue) {
			if (getCurrentBatchSize() > 0) {
				if (commandQueue.size() > 0)
					new BatchCommandSQL(this, commandQueue.toArray(new CommandSQL[] {}), getCurrentBatchSize(), showSql)
							.execute();
			} else {
				for (CommandSQL command : commandQueue) {
					try {
						command.execute();
					} catch (SQLException ex) {
						throw this.getDialect().convertSQLException(ex, "Erro enviando comando sql.", command.getSql());
					}
				}
			}
			commandQueue.clear();
		}
	}

	@Override
	public void forceFlush(Set<String> tableNames) throws Exception {
		if (tableNames != null) {
			synchronized (commandQueue) {
				List<CommandSQL> commandsToRemove = new ArrayList<CommandSQL>();
				for (CommandSQL command : commandQueue) {
					if (tableNames.contains(command.getTargetTableName().toUpperCase())) {
						command.execute();
						commandsToRemove.add(command);
					}
				}
				for (CommandSQL command : commandsToRemove) {
					commandQueue.remove(command);
				}
			}
		}
	}

	@Override
	public void close() throws Exception {
		synchronized (commandQueue) {
			commandQueue.clear();
		}
		currentBatchSize = 0;
		getConnection().close();
	}

	@Override
	public void onBeforeExecuteCommit(Connection connection) throws Exception {
		if (this.getConnection() == connection)
			flush();
	}

	@Override
	public void onBeforeExecuteRollback(Connection connection) throws Exception {
		if (this.getConnection() == connection) {
			synchronized (commandQueue) {
				commandQueue.clear();
			}
		}
	}

	@Override
	public void onAfterExecuteCommit(Connection connection) throws Exception {
		currentBatchSize = 0;
	}

	@Override
	public void onAfterExecuteRollback(Connection connection) throws Exception {
		currentBatchSize = 0;
	}

	@Override
	public EntityCacheManager getEntityCacheManager() {
		return entityCacheManager;
	}

	@Override
	public DatabaseDialect getDialect() {
		return dialect;
	}

	@Override
	public Connection getConnection() {
		return connection;
	}

	@Override
	public AbstractSQLRunner getRunner() throws Exception {
		return queryRunner;
	}

	@Override
	public SQLPersistenceContext getPersistenceContext() {
		return persistenceContext;
	}

	@Override
	public void addListener(SQLSessionListener listener) {
		if (!listeners.contains(listener))
			listeners.add(listener);
	}

	@Override
	public void removeListener(SQLSessionListener listener) {
		if (listeners.contains(listener))
			listeners.remove(listener);
	}

	public List<SQLSessionListener> getListeners() {
		return listeners;
	}

	@Override
	public List<CommandSQL> getCommandQueue() {
		return commandQueue;
	}

	@Override
	public Map<Object, Map<DescriptionColumn, IdentifierPostInsert>> getCacheIdentifier() {
		return cacheIdentifier;
	}

	@Override
	public void setFormatSql(boolean sql) {
		this.formatSql = sql;

	}

	@Override
	public void setShowSql(ShowSQLType... showSql) {
		this.showSql = showSql;
	}

	@Override
	public boolean isShowSql() {
		return showSql != null;
	}

	@Override
	public String clientId() {
		return clientId;
	}

	@Override
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	@Override
	public boolean isFormatSql() {
		return formatSql;
	}

	@Override
	public long update(String sql) throws Exception {
		return queryRunner.update(this.getConnection(), sql, listeners);
	}

	@Override
	public long update(String sql, Object[] params) throws Exception {
		return queryRunner.update(this.getConnection(), sql, params, listeners);
	}

	@Override
	public long update(String sql, NamedParameter[] params) throws Exception {
		return queryRunner.update(this.getConnection(), sql, params, listeners);
	}

	@Override
	public void removeTable(String tableName) throws Exception {
		((SQLiteConnection) connection).getDatabase().delete(tableName, null, null);

	}

	public SQLSessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public Context getContext() {
		return context;
	}

	@Override
	public void executeDDL(String ddl) throws Exception {
		getRunner().executeDDL(connection, ddl, showSql, formatSql, "");
	}

	public EntityHandler createNewEntityHandler(Class<?> resultClass,
			List<ExpressionFieldMapper> expressionsFieldMapper,
			Map<SQLQueryAnalyserAlias, Map<String, String[]>> columnAliases, Cache transactionCache,
			boolean allowDuplicateObjects,
			Object objectToRefresh, int firstResult, int maxResults, boolean readOnly, LockOptions lockOptions)
			throws Exception {
		EntityHandler handler = new EntityHandler(lazyLoadFactory, resultClass, getEntityCacheManager(),
				expressionsFieldMapper, columnAliases, this,
				transactionCache, allowDuplicateObjects, firstResult, maxResults, readOnly, lockOptions);
		handler.setObjectToRefresh(objectToRefresh);
		return handler;
	}

	@Override
	public boolean isProxyObject(Object object) throws Exception {
		return lazyLoadFactory.isProxyObject(object);
	}

	@Override
	public boolean proxyIsInitialized(Object object) throws Exception {
		return lazyLoadFactory.proxyIsInitialized(object);
	}

	@Override
	public void rollbackToSavePoint(final String savepoint) throws Exception {
		connection.rollback(new Savepoint() {
			@Override
			public String getSavepointName() throws SQLException {
				return savepoint;
			}

			@Override
			public int getSavepointId() throws SQLException {
				return 0;
			}
		});
	}

	@Override
	public void savePoint(String savepoint) throws Exception {
		connection.setSavepoint(savepoint);
	}

	@Override
	public <T> T cloneEntityManaged(Object object) throws Exception {
		if (!(object instanceof Cloneable)) {
			throw new Exception("O objeto a ser clonado não implementa a interface Cloneable.");
		}
		EntityManaged entityManaged = persistenceContext.getEntityManaged(object);
		if (entityManaged == null) {
			throw new Exception("O objeto a ser clonado não está sendo gerenciado.");
		}
		T clone = (T) object.getClass().getMethod("clone").invoke(object);

		EntityManaged newEntityManaged = persistenceContext.createEmptyEntityManaged(clone);
		newEntityManaged.setStatus(entityManaged.getStatus());
		newEntityManaged.setFieldsForUpdate(entityManaged.getFieldsForUpdate());
		newEntityManaged.setNewEntity(entityManaged.isNewEntity());
		for (DescriptionField descriptionField : entityManaged.getEntityCache().getDescriptionFields())
			newEntityManaged.addLastValue(descriptionField.getFieldEntityValue(this, clone));

		return clone;
	}

	@Override
	public void evict(Class class0) {
		persistenceContext.evict(class0);
	}

	@Override
	public void evictAll() {
		persistenceContext.evictAll();
	}

	@Override
	public boolean isClosed() throws Exception {
		return getConnection() == null || getConnection().isClosed();
	}

	@Override
	public void setClientInfo(String clientInfo) throws SQLException {
	}

	@Override
	public String getClientInfo() throws SQLException {
		return "";
	}

	@Override
	public Transaction getTransaction() throws Exception {
		if (transaction == null) {
			transaction = transactionFactory.createTransaction((SQLiteConnection) connection, persistenceContext);
		}
		return transaction;
	}

	@Override
	public SQLSessionFactory getSQLSessionFactory() {
		return sessionFactory;
	}

	@Override
	public void clear() throws Exception {
		persistenceContext.evictAll();
		persistenceContext.clearCache();
	}

	@Override
	public <T> T find(Class<T> entityClass, Object id, boolean readOnly) throws Exception {
		EntityCache entityCache = entityCacheManager.getEntityCache(entityClass);
		if (entityCache == null) {
			throw new SQLSessionException("Classe não foi encontrada na lista de entidades gerenciadas. "
					+ entityClass.getName());
		}
		if (id instanceof Identifier) {
			if (!((Identifier<?>) id).getClazz().equals(entityClass)) {
				throw new SQLSessionException("Objeto ID é do tipo Identifier porém de uma classe diferente da classe "
						+ entityClass.getName());
			} else
				return find((Identifier<T>) id, readOnly);
		}
		Identifier<T> identifier = Identifier.create(this, entityClass);
		identifier.setIdIfPossible(id);
		return find(identifier, readOnly);
	}

	@Override
	public <T> T find(Class<T> entityClass, Object id, Map<String, Object> properties, boolean readOnly)
			throws Exception {
		EntityCache entityCache = entityCacheManager.getEntityCache(entityClass);
		if (entityCache == null) {
			throw new SQLSessionException("Classe não foi encontrada na lista de entidades gerenciadas. "
					+ entityClass.getName());
		}
		T result = find(entityClass, id, readOnly);
		entityCache.setObjectValues(result, properties);
		return null;
	}

	@Override
	public <T> T find(Class<T> entityClass, Object id, LockOptions lockOptions, boolean readOnly) throws Exception {
		throw new Exception("Método não implementado. Falta implementar Lock.");
	}

	@Override
	public <T> T find(Class<T> entityClass, Object id, LockOptions lockOptions, Map<String, Object> properties,
			boolean readOnly) throws Exception {
		throw new Exception("Método não implementado. Falta implementar Lock.");
	}

	@Override
	public <T> T find(Identifier<T> id, boolean readOnly) throws Exception {
		SQLQuery query = createQuery("");
		query.setReadOnly(readOnly);
		return (T) query.identifier(id).getSingleResult();
	}

	@Override
	public <T> T find(Identifier<T> id, LockOptions lockOptions, boolean readOnly) throws Exception {
		throw new Exception("Método não implementado. Falta implementar Lock.");
	}

	@Override
	public <T> T find(Identifier<T> id, Map<String, Object> properties, boolean readOnly) throws Exception {
		T result = find(id, readOnly);
		id.getEntityCache().setObjectValues(result, properties);
		return null;
	}

	@Override
	public <T> T find(Identifier<T> id, Map<String, Object> properties, LockOptions lockOptions, boolean readOnly)
			throws Exception {
		throw new Exception("Método não implementado. Falta implementar Lock.");
	}

	@Override
	public void refresh(Object entity) throws Exception {
		if (entity == null)
			return;

		persistenceContext.detach(entity);
		EntityCache entityCache = entityCacheManager.getEntityCache(entity.getClass());
		if (entityCache == null) {
			throw new SQLSessionException("Classe não foi encontrada na lista de entidades gerenciadas. "
					+ entity.getClass().getName());
		}
		Identifier<Object> identifier = Identifier.create(this, entity, true);
		find(identifier);
	}

	@Override
	public void refresh(Object entity, Map<String, Object> properties) throws Exception {
		if (entity == null)
			return;

		persistenceContext.detach(entity);
		EntityCache entityCache = entityCacheManager.getEntityCache(entity.getClass());
		if (entityCache == null) {
			throw new SQLSessionException("Classe não foi encontrada na lista de entidades gerenciadas. "
					+ entity.getClass().getName());
		}
		Identifier<Object> identifier = Identifier.create(this, entity, true);
		find(identifier);
	}

	@Override
	public void refresh(Object entity, LockOptions lockOptions) throws Exception {
		throw new RuntimeException("Método não implementado. Falta implementar Lock.");
	}

	@Override
	public void refresh(Object entity, LockOptions lockOptions, Map<String, Object> properties) throws Exception {
		throw new RuntimeException("Método não implementado. Falta implementar Lock.");
	}

	@Override
	public void detach(Object entity) {
		if (entity == null)
			return;

		persistenceContext.detach(entity);
	}

	@Override
	public SQLQuery createQuery(String sql) throws Exception {
		SQLQuery result = new SQLQueryImpl(this);
		result.sql(sql);
		result.showSql(showSql);
		result.formatSql(formatSql);
		return result;
	}

	@Override
	public SQLQuery createQuery(String sql, Object parameters) throws Exception {
		SQLQuery result = new SQLQueryImpl(this);
		result.sql(sql);
		result.setParameters(parameters);
		result.showSql(showSql);
		result.formatSql(formatSql);
		return result;
	}

	@Override
	public <T> TypedSQLQuery<T> createQuery(String sql, Class<T> resultClass) throws Exception {
		TypedSQLQuery<T> result = new SQLQueryImpl<T>(this, resultClass);
		result.sql(sql);
		result.showSql(showSql);
		result.formatSql(formatSql);
		return result;
	}

	@Override
	public <T> TypedSQLQuery<T> createQuery(String sql, Class<T> resultClass, Object parameters) throws Exception {
		TypedSQLQuery<T> result = new SQLQueryImpl<T>(this, resultClass);
		result.sql(sql);
		result.setParameters(parameters);
		result.showSql(showSql);
		result.formatSql(formatSql);
		return result;
	}

	@Override
	public SQLQuery createQuery(String sql, LockOptions lockOptions) throws Exception {
		return createQuery(sql);
	}

	@Override
	public SQLQuery createQuery(String sql, Object parameters, LockOptions lockOptions) throws Exception {
		return createQuery(sql,parameters);
	}

	@Override
	public <T> TypedSQLQuery<T> createQuery(String sql, Class<T> resultClass, LockOptions lockOptions) throws Exception {
		return createQuery(sql,resultClass);
	}

	@Override
	public <T> TypedSQLQuery<T> createQuery(String sql, Class<T> resultClass, Object parameters, LockOptions lockOptions)
			throws Exception {
		return createQuery(sql,resultClass,parameters);
	}

	@Override
	public SQLQuery createNamedQuery(String name) throws Exception {
		SQLQuery result = new SQLQueryImpl(this);
		result.namedQuery(name);
		result.showSql(showSql);
		result.formatSql(formatSql);
		return result;
	}

	@Override
	public SQLQuery createNamedQuery(String name, Object parameters) throws Exception {
		SQLQuery result = new SQLQueryImpl(this);
		result.namedQuery(name);
		result.setParameters(parameters);
		result.showSql(showSql);
		result.formatSql(formatSql);
		return result;
	}

	@Override
	public <T> TypedSQLQuery<T> createNamedQuery(String name, Class<T> resultClass) throws Exception {
		return new SQLQueryImpl<T>(this, resultClass).namedQuery(name).showSql(showSql).formatSql(formatSql);
	}

	@Override
	public <T> TypedSQLQuery<T> createNamedQuery(String name, Class<T> resultClass, Object parameters) throws Exception {
		return new SQLQueryImpl<T>(this, resultClass).setParameters(parameters).namedQuery(name).showSql(showSql)
				.formatSql(formatSql);
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey) throws Exception {
		return find(entityClass, primaryKey, false);
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey, Map<String, Object> properties) throws Exception {
		return find(entityClass, primaryKey, properties, false);
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey, LockOptions lockOptions) throws Exception {
		return find(entityClass, primaryKey, lockOptions, false);
	}

	@Override
	public <T> T find(Class<T> entityClass, Object primaryKey, LockOptions lockOptions, Map<String, Object> properties)
			throws Exception {
		return find(entityClass, primaryKey, lockOptions, properties, false);
	}

	@Override
	public <T> T find(Identifier<T> id) throws Exception {
		return find(id, false);
	}

	@Override
	public <T> T find(Identifier<T> id, LockOptions lockOptions) throws Exception {
		return find(id, lockOptions, false);
	}

	@Override
	public <T> T find(Identifier<T> id, Map<String, Object> properties) throws Exception {
		return find(id, properties, false);
	}

	@Override
	public <T> T find(Identifier<T> id, Map<String, Object> properties, LockOptions lockOptions) throws Exception {
		return find(id, properties, lockOptions, false);
	}

	@Override
	public SQLQuery createStoredProcedureQuery(String procedureName, CallableType type) throws Exception {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public SQLQuery createStoredProcedureQuery(String procedureName, CallableType type, Object parameters)
			throws Exception {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public <T> TypedSQLQuery<T> createStoredProcedureQuery(String procedureName, CallableType type, Class<T> resultClass)
			throws Exception {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public <T> TypedSQLQuery<T> createStoredProcedureQuery(String procedureName, CallableType type,
			Class<T> resultClass, Object[] parameters) throws Exception {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public SQLQuery createStoredProcedureNamedQuery(String name) throws Exception {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public SQLQuery createStoredProcedureNamedQuery(String name, Object parameters) throws Exception {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public <T> TypedSQLQuery<T> createStoredProcedureNamedQuery(String name, Class<T> resultClass) throws Exception {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public <T> TypedSQLQuery<T> createStoredProcedureNamedQuery(String name, Class<T> resultClass, Object[] parameters)
			throws Exception {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void lock(Object entity, LockOptions lockOptions) throws Exception {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void lockAll(Collection<?> entities, LockOptions lockOptions) throws Exception {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void lockAll(Object[] entities, LockOptions lockOptions) throws Exception {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public String applyLock(String sql, Class<?> resultClass, LockOptions lockOptions) throws Exception {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void saveInBatchMode(Object object, int batchSize) throws Exception {
		this.currentBatchSize = batchSize;
		persister.save(this, object, batchSize);
	}

	@Override
	public void saveInBatchMode(Object[] object, int batchSize) throws Exception {
		this.currentBatchSize = batchSize;
		for (Object obj : object)
			persister.save(this, obj, batchSize);
	}

	@Override
	public int getBatchSize() {
		return batchSize;
	}

	@Override
	public void batchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	private int getCurrentBatchSize() {
		if (batchSize > 0)
			return batchSize;
		return currentBatchSize;
	}

	@Override
	public boolean validationIsActive() {
		return validationActive;
	}

	@Override
	public void activateValidation() {
		this.validationActive = true;
	}

	@Override
	public void deactivateValidation() {
		this.validationActive = false;
	}

	@Override
	public boolean hasNextValFromCacheSequence(String sequenceName) {
		if (!cacheSequenceNumbers.containsKey(sequenceName))
			return false;
		return (cacheSequenceNumbers.get(sequenceName).hasNextVal());
	}

	@Override
	public void storeNextValToCacheSession(String sequenceName, Long firstValue, Long lastValue) {
		cacheSequenceNumbers.put(sequenceName, new NextValControl(firstValue - 1, lastValue));
	}

	@Override
	public Long getNextValFromCacheSequence(String sequenceName) {
		if (!cacheSequenceNumbers.containsKey(sequenceName))
			return null;
		return (cacheSequenceNumbers.get(sequenceName).getNextVal());
	}

	private class NextValControl {

		private Long lastValue;
		private Long value;

		public NextValControl(Long firstValue, Long lastValue) {
			this.lastValue = lastValue;
			this.value = firstValue;
		}

		public boolean hasNextVal() {
			return (value + 1 <= lastValue);
		}

		public Long getNextVal() {
			value++;
			return value;
		}
	}

	@Override
	public void forceGenerationIdentifier(Object entity) throws Exception {
		if (entity == null)
			return;
		EntityCache entityCache = this.getEntityCacheManager().getEntityCache(entity.getClass());
		if (entityCache == null) {
			throw new SQLSessionException(
					"Objeto não pode ser salvo pois a classe " + entity.getClass().getName() + " não foi localizada no cache de Entidades.");
		}

		if (!this.getIdentifier(entity).hasIdentifier()) {
			for (DescriptionField descriptionField : entityCache.getDescriptionFields()) {
				if (!descriptionField.isAnyCollectionOrMap() && !descriptionField.isVersioned() && !descriptionField.isJoinTable()) {
					for (DescriptionColumn columnModified : descriptionField.getDescriptionColumns()) {
						if (columnModified.isPrimaryKey() && columnModified.hasGenerator()) {
							IdentifierGenerator identifierGenerator = IdentifierGeneratorFactory.createGenerator(this, columnModified);
							if (!(identifierGenerator instanceof IdentifierPostInsert)) {
								/*
								 * Gera o próximo número da sequência e seta na entidade
								 */
								ReflectionUtils.setObjectValueByFieldName(entity, columnModified.getField().getName(), identifierGenerator.generate());
							}
						}
					}
				}
			}
		}
	}

	@Override
	public ShowSQLType[] getShowSql() {
		return showSql;
	}
}
