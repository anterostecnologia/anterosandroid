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

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import br.com.anteros.android.core.resource.messages.AnterosAndroidMessages;
import br.com.anteros.core.log.Logger;
import br.com.anteros.core.log.LoggerProvider;
import br.com.anteros.core.resource.messages.AnterosBundle;
import br.com.anteros.core.resource.messages.AnterosResourceBundle;
import br.com.anteros.core.utils.IOUtils;
import br.com.anteros.core.utils.StringUtils;
import br.com.anteros.persistence.metadata.annotation.type.TemporalType;
import br.com.anteros.persistence.parameter.NamedParameter;
import br.com.anteros.persistence.session.configuration.AnterosPersistenceProperties;
import br.com.anteros.persistence.sql.binder.LobParameterBinding;
import br.com.anteros.persistence.sql.binder.ParameterBinding;
import br.com.anteros.persistence.sql.dialect.type.SQLiteDate;
import br.com.anteros.persistence.sql.statement.NamedParameterStatement;

/**
 * 
 * @author Edson Martins - Anteros
 * 
 */

public class SQLitePreparedStatement implements PreparedStatement, ParameterMetaData {

	private static Logger LOG = LoggerProvider.getInstance().getLogger(SQLitePreparedStatement.class.getName());

	private static AnterosBundle MESSAGES = AnterosResourceBundle.getBundle(AnterosPersistenceProperties.ANTEROS_ANDROID,AnterosAndroidMessages.class);

	protected SQLiteStatement statement;
	private Connection connection;
	private String sql;
	private Map<Integer, Object> parameters = new LinkedHashMap<Integer, Object>();
	private boolean showSql;
	private boolean formatSql;
	private String generatedKeyTableName;
	private List<NamedParameter> parsedParameters;

	public SQLitePreparedStatement(Connection connection) throws Exception {
		if (!(connection instanceof SQLiteConnection))
			throw new SQLException("Objeto Connection não suportado. Use um objeto do tipo SQLiteConnection.");
		this.connection = connection;
		this.parsedParameters = new ArrayList<NamedParameter>();
	}

	public SQLitePreparedStatement(Connection connection, String sql, boolean showSql, boolean formatSql)
			throws Exception {
		if (!(connection instanceof SQLiteConnection))
			throw new SQLException("Objeto Connection não suportado. Use um objeto do tipo SQLiteConnection.");
		this.showSql = showSql;
		this.formatSql = formatSql;
		this.connection = connection;
		this.sql = sql;
		this.parsedParameters = NamedParameterStatement.parse(sql, null).getNamedParameters();
	}

	public SQLitePreparedStatement(Connection connection, String sql, Object[] values, boolean showSql,
			boolean formatSql) throws Exception {
		if (!(connection instanceof SQLiteConnection))
			throw new SQLException("Objeto Connection não suportado. Use um objeto do tipo SQLiteConnection.");
		this.showSql = showSql;
		this.formatSql = formatSql;
		this.connection = connection;
		this.sql = sql;
		this.parsedParameters = NamedParameterStatement.parse(sql, null).getNamedParameters();
		setObjects(values);
	}

	public SQLitePreparedStatement(Connection connection, String sql, NamedParameter[] values, boolean showSql,
			boolean formatSql) throws Exception {
		if (!(connection instanceof SQLiteConnection))
			throw new SQLException("Objeto Connection não suportado. Use um objeto do tipo SQLiteConnection.");
		this.showSql = showSql;
		this.formatSql = formatSql;
		this.connection = connection;
		this.sql = sql;
		this.parsedParameters = NamedParameterStatement.parse(sql, null).getNamedParameters();
		setObjects(values);
	}

	public void setObjects(Object[] values) throws Exception {
		parameters.clear();
		int parameterIndex = 1;
		for (Object param : values) {
			if (param instanceof NamedParameter) {
				Object paramValue = ((NamedParameter) param).getValue();
				TemporalType temporalType = ((NamedParameter) param).getTemporalType();
				if ((temporalType != null) && (paramValue instanceof Date)) {
					Date dateValue = (Date) paramValue;
					parameters.put(parameterIndex, new SQLiteDate(dateValue, temporalType));
				} else if (paramValue instanceof ParameterBinding) {
					((ParameterBinding) paramValue).bindValue(this, parameterIndex);
				} else
					parameters.put(parameterIndex, ((NamedParameter) param).getValue());
			} else {
				if (param instanceof ParameterBinding)
					((ParameterBinding) param).bindValue(this, parameterIndex);
				else
					parameters.put(parameterIndex, param);
			}
			parameterIndex++;
		}
	}

	@Override
	public void addBatch(String sql) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void cancel() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void clearBatch() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void clearWarnings() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void close() throws SQLException {
		if (statement != null)
			statement.close();
	}

	@Override
	public boolean execute(String sql) throws SQLException {
		this.parsedParameters = NamedParameterStatement.parse(sql, null).getNamedParameters();
		if (showSql) {
			LOG.info(MESSAGES.getMessage(SQLitePreparedStatement.class.getSimpleName() + ".showSql", sql));
			if ((parameters != null) && (parameters.size() > 0)) {
				LOG.info(MESSAGES.getMessage(SQLitePreparedStatement.class.getSimpleName() + ".showParameters"));
				for (Object p : parameters.values()) {
					LOG.info(p + "");
				}
			}
		}

		if (statement == null)
			this.statement = getInternalConnection().getDatabase().compileStatement(sql);

		for (Integer parameterIndex : parameters.keySet())
			bindParameter(parameterIndex, parameters.get(parameterIndex));
		this.statement.execute();
		return true;
	}

	private SQLiteConnection getInternalConnection() {
		return (SQLiteConnection) connection;
	}

	@Override
	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public boolean execute(String sql, String[] columnNames) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public int[] executeBatch() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		this.parsedParameters = NamedParameterStatement.parse(sql, null).getNamedParameters();
		if (showSql) {
			LOG.info(MESSAGES.getMessage(SQLitePreparedStatement.class.getSimpleName()+  ".showSql", sql));
			if ((parameters != null) && (parameters.size() > 0)) {
				LOG.info(MESSAGES.getMessage(SQLitePreparedStatement.class.getSimpleName() + ".showParameters"));
				for (Object p : parameters.values()) {
					LOG.info(p + "");
				}
			}
		}
		return new SQLiteResultSet(getInternalConnection().getDatabase().rawQuery(sql, convertParametersToString()),
				this);
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {
		this.parsedParameters = NamedParameterStatement.parse(sql, null).getNamedParameters();
		if (showSql) {
			LOG.info(MESSAGES.getMessage(SQLitePreparedStatement.class.getSimpleName()+  ".showSql", sql));
			if ((parameters != null) && (parameters.size() > 0)) {
				LOG.info(MESSAGES.getMessage(SQLitePreparedStatement.class.getSimpleName() + ".showParameters"));
				for (Object p : parameters.values()) {
					LOG.info(p + "");
				}
			}
		}

		if (statement == null)
			this.statement = getInternalConnection().getDatabase().compileStatement(sql);

		for (Integer parameterIndex : parameters.keySet())
			bindParameter(parameterIndex, parameters.get(parameterIndex));
		this.statement.execute();
		return 1;
	}

	@Override
	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public int executeUpdate(String sql, String[] columnNames) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public Connection getConnection() throws SQLException {
		return connection;
	}

	@Override
	public int getFetchDirection() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public int getFetchSize() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public ResultSet getGeneratedKeys() throws SQLException {
		if (StringUtils.isEmpty(generatedKeyTableName)) {
			Cursor cursor = ((SQLiteConnection) connection).getDatabase().query("sqlite_sequence",
					new String[] { "seq" }, "name = ?", new String[] { generatedKeyTableName }, null, null, null, null);
			return new SQLiteResultSet(cursor, this);
		} else
			return null;
	}

	@Override
	public int getMaxFieldSize() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public int getMaxRows() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public boolean getMoreResults() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public boolean getMoreResults(int current) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public int getQueryTimeout() throws SQLException {
		return 0;
	}

	@Override
	public ResultSet getResultSet() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public int getResultSetConcurrency() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public int getResultSetType() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public int getUpdateCount() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void setCursorName(String name) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void setEscapeProcessing(boolean enable) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void setFetchDirection(int direction) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void setFetchSize(int rows) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void setMaxFieldSize(int max) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void setMaxRows(int max) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void setQueryTimeout(int seconds) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public boolean isClosed() throws SQLException {
		return false;
	}

	@Override
	public void setPoolable(boolean poolable) throws SQLException {

	}

	@Override
	public boolean isPoolable() throws SQLException {
		return false;
	}

	@Override
	public void addBatch() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void clearParameters() throws SQLException {
		parameters.clear();
	}

	@Override
	public boolean execute() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public ResultSet executeQuery() throws SQLException {
		return executeQuery(sql);
	}

	@Override
	public int executeUpdate() throws SQLException {
		return executeUpdate(sql);
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public ParameterMetaData getParameterMetaData() throws SQLException {
		return this;
	}

	@Override
	public void setArray(int parameterIndex, Array theArray) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream inputStream, int arg2) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void setBigDecimal(int parameterIndex, BigDecimal theBigDecimal) throws SQLException {
		parameters.put(parameterIndex, theBigDecimal);
	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream inputStream, int length) throws SQLException {
		try {
			parameters.put(parameterIndex, IOUtils.toByteArray(inputStream));
		} catch (IOException e) {
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public void setBlob(int parameterIndex, Blob blob) throws SQLException {
		try {
			parameters.put(parameterIndex, IOUtils.toByteArray(blob.getBinaryStream()));
		} catch (IOException e) {
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public void setBoolean(int parameterIndex, boolean theBoolean) throws SQLException {
		parameters.put(parameterIndex, theBoolean);

	}

	@Override
	public void setByte(int parameterIndex, byte theByte) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void setBytes(int parameterIndex, byte[] theBytes) throws SQLException {
		parameters.put(parameterIndex, theBytes);
	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
		try {
			StringBuffer sb = new StringBuffer();
			char[] cbuf = new char[8192];
			int cnt;
			while ((cnt = reader.read(cbuf)) > 0) {
				sb.append(cbuf, 0, cnt);
			}

			parameters.put(parameterIndex, sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
			throw new SQLException("Cannot read from character stream, exception message: " + e.getMessage());
		}

	}

	@Override
	public void setClob(int parameterIndex, Clob theClob) throws SQLException {
		try {
			parameters.put(parameterIndex, new String(IOUtils.toByteArray(theClob.getAsciiStream())));
		} catch (IOException e) {
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public void setDate(int parameterIndex, Date theDate) throws SQLException {
		parameters.put(parameterIndex, new SQLiteDate(theDate, TemporalType.DATE));
	}

	@Override
	public void setDate(int parameterIndex, Date theDate, Calendar cal) throws SQLException {
		parameters.put(parameterIndex, new SQLiteDate(theDate, TemporalType.DATE));
	}

	@Override
	public void setDouble(int parameterIndex, double theDouble) throws SQLException {
		parameters.put(parameterIndex, theDouble);
	}

	@Override
	public void setFloat(int parameterIndex, float theFloat) throws SQLException {
		parameters.put(parameterIndex, theFloat);
	}

	@Override
	public void setInt(int parameterIndex, int theInt) throws SQLException {
		parameters.put(parameterIndex, theInt);
	}

	@Override
	public void setLong(int parameterIndex, long theLong) throws SQLException {
		parameters.put(parameterIndex, theLong);
	}

	@Override
	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		parameters.put(parameterIndex, null);
	}

	@Override
	public void setNull(int paramIndex, int sqlType, String typeName) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void setObject(int parameterIndex, Object theObject) throws SQLException {
		parameters.put(parameterIndex, theObject);
	}

	@Override
	public void setObject(int parameterIndex, Object theObject, int targetSqlType) throws SQLException {
		if (Types.BINARY == targetSqlType)
			parameters.put(parameterIndex, theObject);
		else
			throw new SQLException("Método não suportado no Anteros Persistence para Android.");

	}

	@Override
	public void setObject(int parameterIndex, Object theObject, int targetSqlType, int scale) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void setRef(int parameterIndex, Ref theRef) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void setShort(int parameterIndex, short theShort) throws SQLException {
		parameters.put(parameterIndex, theShort);
	}

	@Override
	public void setString(int parameterIndex, String theString) throws SQLException {
		parameters.put(parameterIndex, theString);
	}

	@Override
	public void setTime(int parameterIndex, Time theTime) throws SQLException {
		parameters.put(parameterIndex, new SQLiteDate(theTime, TemporalType.TIME));
	}

	@Override
	public void setTime(int parameterIndex, Time theTime, Calendar cal) throws SQLException {
		parameters.put(parameterIndex, new SQLiteDate(theTime, TemporalType.TIME));
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp theTimestamp) throws SQLException {
		parameters.put(parameterIndex, new SQLiteDate(theTimestamp, TemporalType.DATE_TIME));
	}

	@Override
	public void setTimestamp(int parameterIndex, Timestamp theTimestamp, Calendar cal) throws SQLException {
		parameters.put(parameterIndex, new SQLiteDate(theTimestamp, TemporalType.DATE_TIME));
	}

	@Override
	public void setURL(int parameterIndex, URL theURL) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void setRowId(int parameterIndex, RowId theRowId) throws SQLException {

	}

	@Override
	public void setNString(int parameterIndex, String theString) throws SQLException {

	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {

	}

	@Override
	public void setNClob(int parameterIndex, NClob value) throws SQLException {

	}

	@Override
	public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {

	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {

	}

	@Override
	public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {

	}

	@Override
	public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {

	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream inputStream, long length) throws SQLException {

	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream inputStream, long length) throws SQLException {

	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {

	}

	@Override
	public void setAsciiStream(int parameterIndex, InputStream inputStream) throws SQLException {

	}

	@Override
	public void setBinaryStream(int parameterIndex, InputStream inputStream) throws SQLException {

	}

	@Override
	public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {

	}

	@Override
	public void setNCharacterStream(int parameterIndex, Reader reader) throws SQLException {

	}

	@Override
	public void setClob(int parameterIndex, Reader reader) throws SQLException {

	}

	@Override
	public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {

	}

	@Override
	public void setNClob(int parameterIndex, Reader reader) throws SQLException {

	}

	@Override
	public void setUnicodeStream(int parameterIndex, InputStream theInputStream, int length) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	public Map<Integer, Object> getParameters() {
		return parameters;
	}

	public void setParameters(Map<Integer, Object> parameters) {
		this.parameters = parameters;
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

	protected void bindParameter(Integer parameterIndex, Object value) {
		if (value == null) {
			statement.bindNull(parameterIndex);
		} else if ((value instanceof Long) || (value.getClass() == long.class)) {
			statement.bindLong(parameterIndex, (Long) value);
		} else if ((value instanceof Integer) || (value.getClass() == int.class)) {
			statement.bindLong(parameterIndex, (Integer) value);
		} else if ((value instanceof Double) || (value.getClass() == double.class)) {
			statement.bindDouble(parameterIndex, (Double) value);
		} else if ((value instanceof Float) || (value.getClass() == float.class)) {
			statement.bindDouble(parameterIndex, (Float) value);
		} else if (value instanceof BigDecimal) {
			statement.bindDouble(parameterIndex, ((BigDecimal) value).doubleValue());
		} else if (value instanceof String) {
			statement.bindString(parameterIndex, (String) value);
		} else if (value instanceof SQLiteDate) {
			statement.bindString(parameterIndex, ((SQLiteDate) value).getFormatted());
		} else if (value instanceof Date) {
			statement.bindString(parameterIndex, new SQLiteDate((Date) value, TemporalType.DATE).getFormatted());
		} else if (value instanceof Boolean) {
			statement.bindLong(parameterIndex, (((Boolean) value).booleanValue() == true ? 0 : 1));
		} else if (value.getClass() == boolean.class) {
			statement.bindLong(parameterIndex, (((Boolean) value).booleanValue() == true ? 0 : 1));
		} else if (value.getClass() == byte[].class) {
			statement.bindBlob(parameterIndex, (byte[]) value);
		} else if (value.getClass() == Byte[].class) {
			Byte[] bytes = (Byte[]) value;
			byte[] b = new byte[bytes.length];
			for (int i = 0; i < b.length; i++)
				b[i] = bytes[i].byteValue();
			statement.bindBlob(parameterIndex, b);
		}
	}

	protected String[] convertParametersToString() {
		String[] result = new String[parameters.keySet().size()];

		for (Integer parameterIndex : parameters.keySet()) {
			Object value = parameters.get(parameterIndex);
			if (value == null) {
				result[parameterIndex - 1] = null;
			} else if (value instanceof BigDecimal) {
				result[parameterIndex - 1] = ((BigDecimal) value).doubleValue() + "";
			} else if (value instanceof SQLiteDate) {
				result[parameterIndex - 1] = ((SQLiteDate) value).getFormatted();
			} else if (value instanceof Date) {
				result[parameterIndex - 1] = new SQLiteDate((Date) value, TemporalType.DATE).getFormatted();
			} else if (value instanceof Boolean || value.getClass() == boolean.class) {
				result[parameterIndex - 1] = (((Boolean) value).booleanValue() == true ? "0" : "1");
			} else if (value.getClass() == Byte[].class) {
				result[parameterIndex - 1] = new String((byte[]) value);
			} else
				result[parameterIndex - 1] = value.toString();
		}
		return result;
	}

	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}

	public String getGeneratedKeyTableName() {
		return generatedKeyTableName;
	}

	public void setGeneratedKeyTableName(String generatedKeyTableName) {
		this.generatedKeyTableName = generatedKeyTableName;
	}

	public String getSql() {
		return sql;
	}

	@Override
	public String getParameterClassName(int arg0) throws SQLException {
		return "java.lang.String";
	}

	@Override
	public int getParameterCount() throws SQLException {
		if (StringUtils.isEmpty(sql))
			return 0;
		else if (parsedParameters.size() == 0)
			return StringUtils.countOccurrencesOf(sql, "?");
		else
			return parsedParameters.size();
	}

	@Override
	public int getParameterMode(int paramIndex) throws SQLException {
		return 0;
	}

	@Override
	public int getParameterType(int paramIndex) throws SQLException {
		Integer key = new Integer(paramIndex);
		if (parameters.containsKey(key)) {
			Object parameter = parameters.get(key);
			if (parameter instanceof NamedParameter) {
				Object paramValue = ((NamedParameter) parameter).getValue();
				TemporalType temporalType = ((NamedParameter) parameter).getTemporalType();
				if (temporalType != null) {
					if (temporalType == TemporalType.DATE)
						return Types.DATE;
					else if (temporalType == TemporalType.DATE_TIME)
						return Types.TIMESTAMP;
					else if (temporalType == TemporalType.TIME)
						return Types.TIME;
				} else if (paramValue instanceof LobParameterBinding) {
					return Types.BLOB;
				} else {
					if (paramValue instanceof LobParameterBinding) {
						return Types.BLOB;
					}
				}
			}
		}
		return Types.VARCHAR;
	}

	@Override
	public String getParameterTypeName(int paramIndex) throws SQLException {
		int type = getParameterType(paramIndex);
		switch (type) {
		case Types.DATE:
			return "DATE";
		case Types.TIMESTAMP:
			return "TIMESTAMP";
		case Types.TIME:
			return "TIME";
		case Types.BLOB:
			return "BLOB";
		default:
			break;
		}
		return "VARCHAR";
	}

	@Override
	public int getPrecision(int paramIndex) throws SQLException {
		return 0;
	}

	@Override
	public int getScale(int paramIndex) throws SQLException {
		return 0;
	}

	@Override
	public int isNullable(int paramIndex) throws SQLException {
		return 0;
	}

	@Override
	public boolean isSigned(int paramIndex) throws SQLException {
		return false;
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
