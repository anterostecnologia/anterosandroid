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
import java.sql.DriverManager;

import javax.sql.DataSource;

import android.content.Context;
import br.com.anteros.android.persistence.session.context.AndroidSessionContext;
import br.com.anteros.android.persistence.sql.jdbc.SQLiteConnection;
import br.com.anteros.android.persistence.sql.jdbc.SQLiteDriver;
import br.com.anteros.android.persistence.transacation.impl.SQLiteTransactionFactory;
import br.com.anteros.persistence.metadata.EntityCacheManager;
import br.com.anteros.persistence.session.AbstractSQLSessionFactoryBase;
import br.com.anteros.persistence.session.SQLSession;
import br.com.anteros.persistence.session.configuration.AnterosPersistenceProperties;
import br.com.anteros.persistence.session.configuration.SessionFactoryConfiguration;
import br.com.anteros.persistence.session.context.CurrentSQLSessionContext;
import br.com.anteros.persistence.session.exception.SQLSessionException;
import br.com.anteros.persistence.transaction.TransactionFactory;

public class AndroidSQLSessionFactory extends AbstractSQLSessionFactoryBase {

	protected Context context;
	private Connection connection;
	private SQLSession session;
	private TransactionFactory transactionFactory;

	public AndroidSQLSessionFactory(Context context, EntityCacheManager entityCacheManager, DataSource dataSource,
			SessionFactoryConfiguration configuration) throws Exception {
		super(entityCacheManager, dataSource, configuration);
		this.context = context;
	}

	public AndroidSQLSessionFactory(EntityCacheManager entityCacheManager, DataSource dataSource,
			SessionFactoryConfiguration configuration)
			throws Exception {
		super(entityCacheManager, dataSource, configuration);
		throw new Exception("Não é permitido usar este construtor no Android.");
	}

	@Override
	public void generateDDL() throws Exception {
		super.generateDDL();
	}

	@Override
	public SQLSession getCurrentSession() throws Exception {
		if (currentSessionContext == null) {
			throw new SQLSessionException("No CurrentSessionContext configured!");
		}
		return currentSessionContext.currentSession();
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public void beforeGenerateDDL(SQLSession session) throws Exception {
		String databaseDDLGeneration = configuration.getPropertyDef(
				AnterosPersistenceProperties.DATABASE_DDL_GENERATION,
				AnterosPersistenceProperties.NONE);
		databaseDDLGeneration = databaseDDLGeneration.toLowerCase();
		if (databaseDDLGeneration.equals(AnterosPersistenceProperties.DROP_AND_CREATE))
			((SQLiteConnection) getCurrentSession().getConnection()).dropAndCreateDatabase();
		session.getTransaction().begin();
	}

	@Override
	public void afterGenerateDDL(SQLSession session) throws Exception {
		session.getTransaction().commit();
	}

	@Override
	public SQLSession openSession() throws Exception {
		SQLiteDriver driver = new SQLiteDriver(context);
		driver.setContext(context);
		DriverManager.registerDriver(driver);
		connection = DriverManager.getConnection(getConfiguration().getProperty(AnterosPersistenceProperties.JDBC_URL));
		return openSession(connection);
	}

	@Override
	public SQLSession openSession(Connection connection) throws Exception {
		if (connection instanceof SQLiteConnection) {
			((SQLiteConnection) connection).setShowSql(isShowSql());
			((SQLiteConnection) connection).setFormatSql(isFormatSql());
		}
		return new AndroidSQLSession(context, this, connection, getEntityCacheManager(), new AndroidSQLRunner(),
				getDialect(), this.getShowSql(),
				isFormatSql(), getTransactionFactory());
	}

	@Override
	protected TransactionFactory getTransactionFactory() {
		if (transactionFactory == null) {
			transactionFactory = new SQLiteTransactionFactory();
		}
		return transactionFactory;
	}


	@Override
	protected CurrentSQLSessionContext buildCurrentSessionContext() throws Exception {
		return new AndroidSessionContext(this);
	}
}
