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

package br.com.anteros.android.persistence.sql.jdbc;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * @author Edson Martins - Anteros
 * 
 */
public class SQLiteConnection implements Connection {

	private boolean autoCommit = true;
	private SQLiteDatabase database;
	private String catalog;
	private boolean showSql;
	private boolean formatSql;
	private String url;
	private Properties info;
	private SQLiteHelper helper;
	private Context context;
	private String datbaseName;

	public SQLiteConnection(Context context, String url, Properties info) throws SQLException {
		this.context = context;
		this.url = url;
		this.info = info;

		datbaseName = url.substring(SQLiteDriver.anterosPrefix.length());
		helper = new SQLiteHelper(this.context, datbaseName);
		// helper.getReadableDatabase();
		database = helper.getWritableDatabase();

		if (database == null) {
			throw new SQLException("Timeout - Não foi possivel abrir o banco de dados " + url);
		}
	}

	@Override
	public void clearWarnings() throws SQLException {
		throw new SQLException("clearWarnings() - Método não suportado no Anteros Persistence para Android.");

	}

	@Override
	public void close() throws SQLException {
		if (database != null && database.isOpen()) {
			database.close();
			database = null;
		}
	}

	@Override
	public void commit() throws SQLException {
		if (autoCommit)
			throw new SQLException("database in auto-commit mode");
		database.setTransactionSuccessful();
		database.endTransaction();
		database.beginTransaction();
	}

	@Override
	public Statement createStatement() throws SQLException {
		try {
			return new SQLiteStatement(this);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		return autoCommit;
	}

	@Override
	public String getCatalog() throws SQLException {
		return "";
	}

	@Override
	public int getHoldability() throws SQLException {
		return 0;
	}

	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return new SQLiteDatabaseMetadata(this);
	}

	@Override
	public int getTransactionIsolation() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public Map<String, Class<?>> getTypeMap() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public boolean isClosed() throws SQLException {
		return !database.isOpen();
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return database.isReadOnly();
	}

	@Override
	public String nativeSQL(String sql) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public CallableStatement prepareCall(String sql) throws SQLException {
		throw new SQLException("prepareCall - Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		throw new SQLException("prepareCall - Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		throw new SQLException("prepareCall - Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public PreparedStatement prepareStatement(String sql) throws SQLException {
		try {
			return new SQLitePreparedStatement(this, sql, showSql, formatSql);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
		if (autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS) {
			try {
				SQLitePreparedStatement result = new SQLitePreparedStatement(this, sql, showSql, formatSql);
				result.setGeneratedKeyTableName(extractTableName(sql));
				return result;
			} catch (Exception e) {
				e.printStackTrace();
				throw new SQLException(e.getMessage());
			}
		} else
			throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	protected String extractTableName(String sql) {
		String result = sql;
		if (result.toUpperCase().indexOf("INSERT INTO") != -1) {
			int index = result.toUpperCase().indexOf("INSERT INTO");
			result = result.substring(index + "INSERT INTO".length()).trim();
			index = result.indexOf(" ");
			if (index == -1)
				index = result.indexOf("(");
			if (index != -1)
				result = result.substring(0, index);
		} else if (result.toUpperCase().indexOf("INSERT") != 1) {
			int index = result.toUpperCase().indexOf("INSERT");
			result = result.substring(index + "INSERT".length()).trim();
			index = result.indexOf(" ");
			if (index == -1)
				index = result.indexOf("(");
			if (index != -1)
				result = result.substring(0, index);
		} else
			return "";
		return result;
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void rollback() throws SQLException {
		if (autoCommit)
			throw new SQLException("database in auto-commit mode");
		database.endTransaction();
		database.beginTransaction();
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		database.execSQL("rollback to " + savepoint.getSavepointName());
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		this.autoCommit = autoCommit;
	}

	@Override
	public void setCatalog(String catalog) throws SQLException {
		this.catalog = catalog;
	}

	@Override
	public void setHoldability(int holdability) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void setReadOnly(boolean readOnly) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public Savepoint setSavepoint() throws SQLException {
		return setSavepoint("anteros");
	}

	@Override
	public Savepoint setSavepoint(final String name) throws SQLException {
		database.execSQL("savepoint " + name);
		return new Savepoint() {
			@Override
			public String getSavepointName() throws SQLException {
				return name;
			}

			@Override
			public int getSavepointId() throws SQLException {
				return 0;
			}
		};
	}

	@Override
	public void setTransactionIsolation(int level) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void setTypeMap(Map<String, Class<?>> arg0) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public Clob createClob() throws SQLException {
		return null;
	}

	@Override
	public Blob createBlob() throws SQLException {
		return null;
	}

	@Override
	public NClob createNClob() throws SQLException {
		return null;
	}

	@Override
	public SQLXML createSQLXML() throws SQLException {
		return null;
	}

	@Override
	public boolean isValid(int timeout) throws SQLException {
		return false;
	}

	@Override
	public void setClientInfo(String name, String value) throws SQLClientInfoException {

	}

	@Override
	public void setClientInfo(Properties properties) throws SQLClientInfoException {

	}

	@Override
	public String getClientInfo(String name) throws SQLException {
		return null;
	}

	@Override
	public Properties getClientInfo() throws SQLException {
		return null;
	}

	@Override
	public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
		return null;
	}

	@Override
	public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
		return null;
	}

	public String getUrl() {
		return url;
	}

	public Properties getInfo() {
		return info;
	}

	public boolean isShowSql() {
		return showSql;
	}

	public void setShowSql(boolean showSql) {
		this.showSql = showSql;
	}

	public boolean isFormatSql() {
		return formatSql;
	}

	public void setFormatSql(boolean formatSql) {
		this.formatSql = formatSql;
	}

	public SQLiteDatabase getDatabase() {
		return database;
	}

	public void dropAndCreateDatabase() throws Exception {
		close();
		context.deleteDatabase(datbaseName);
		database = helper.getWritableDatabase();
	}

	public String getDatbaseName() {
		return datbaseName;
	}

	public SQLiteHelper getHelper() {
		return helper;
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return null;
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return false;
	}
}
