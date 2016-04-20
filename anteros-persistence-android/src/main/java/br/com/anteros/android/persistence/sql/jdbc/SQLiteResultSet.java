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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

import android.database.Cursor;
import android.database.CursorWindow;
import android.database.sqlite.SQLiteCursor;
import br.com.anteros.core.utils.ObjectUtils;
import br.com.anteros.persistence.metadata.annotation.type.TemporalType;
import br.com.anteros.persistence.sql.dialect.type.SQLiteDate;

/**
 * 
 * @author Edson Martins - Anteros
 * 
 */

public class SQLiteResultSet implements ResultSet {

	public static final int FIELD_TYPE_BLOB = 4;
	public static final int FIELD_TYPE_FLOAT = 2;
	public static final int FIELD_TYPE_INTEGER = 1;
	public static final int FIELD_TYPE_NULL = 0;
	public static final int FIELD_TYPE_STRING = 3;

	private final Cursor cursor;
	private final Statement statement;
	private SQLiteDate sqLiteDate;

	public SQLiteResultSet(Cursor cursor, Statement statement) {
		this.cursor = cursor;
		this.statement = statement;

	}

	@Override
	public boolean absolute(int row) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void afterLast() throws SQLException {
		cursor.moveToLast();
		cursor.moveToNext();
	}

	@Override
	public void beforeFirst() throws SQLException {
		cursor.moveToFirst();
		cursor.moveToPrevious();
	}

	@Override
	public void cancelRowUpdates() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");

	}

	@Override
	public void clearWarnings() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");

	}

	@Override
	public void close() throws SQLException {
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
	}

	@Override
	public void deleteRow() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");

	}

	@Override
	public int findColumn(String columnName) throws SQLException {
		int index = cursor.getColumnIndex(columnName);
		if (index == -1)
			index = cursor.getColumnIndex(columnName.toUpperCase());
		if (index == -1)
			index = cursor.getColumnIndex(columnName.toLowerCase());
		if (index != -1)
			index = index + 1;
		return index;
	}

	@Override
	public boolean first() throws SQLException {
		return cursor.moveToFirst();
	}

	@Override
	public Array getArray(int columnIndex) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public Array getArray(String colName) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public InputStream getAsciiStream(int columnIndex) throws SQLException {
		// Converte indice coluna JDBC (de um) para sqlite (zero)
		return new ByteArrayInputStream(cursor.getString(columnIndex - 1).getBytes());
	}

	@Override
	public InputStream getAsciiStream(String columnName) throws SQLException {
		int index = cursor.getColumnIndex(columnName);
		return new ByteArrayInputStream(cursor.getString(index).getBytes());
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
		try {
			return (BigDecimal) ObjectUtils.convert(cursor.getString(columnIndex - 1), BigDecimal.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public BigDecimal getBigDecimal(String columnName) throws SQLException {
		try {
			int index = cursor.getColumnIndex(columnName);
			return (BigDecimal) ObjectUtils.convert(cursor.getString(index), BigDecimal.class);
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		}
	}

	@Override
	public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public BigDecimal getBigDecimal(String columnName, int scale) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public InputStream getBinaryStream(int columnIndex) throws SQLException {
		return new ByteArrayInputStream(cursor.getString(columnIndex - 1).getBytes());
	}

	@Override
	public InputStream getBinaryStream(String columnName) throws SQLException {
		int index = cursor.getColumnIndex(columnName);
		return new ByteArrayInputStream(cursor.getString(index).getBytes());
	}

	@Override
	public Blob getBlob(int columnIndex) throws SQLException {
		byte[] b = cursor.getBlob(columnIndex - 1);
		return new SQLiteBlob(b);
	}

	@Override
	public Blob getBlob(String columnName) throws SQLException {
		int index = cursor.getColumnIndex(columnName);
		byte[] b = cursor.getBlob(index);
		return new SQLiteBlob(b);
	}

	@Override
	public boolean getBoolean(int columnIndex) throws SQLException {
		return cursor.getInt(columnIndex - 1) != 0;
	}

	@Override
	public boolean getBoolean(String columnName) throws SQLException {
		int index = cursor.getColumnIndex(columnName);
		return cursor.getInt(index) != 0;
	}

	@Override
	public byte getByte(int columnIndex) throws SQLException {
		return (byte) cursor.getShort(columnIndex - 1);
	}

	@Override
	public byte getByte(String columnName) throws SQLException {
		int index = cursor.getColumnIndex(columnName);
		return (byte) cursor.getShort(index);
	}

	@Override
	public byte[] getBytes(int columnIndex) throws SQLException {
		return cursor.getBlob(columnIndex - 1);
	}

	@Override
	public byte[] getBytes(String columnName) throws SQLException {
		int index = cursor.getColumnIndex(columnName);
		return cursor.getBlob(index);
	}

	@Override
	public Reader getCharacterStream(int columnIndex) throws SQLException {
		return new StringReader(cursor.getString(columnIndex - 1));
	}

	@Override
	public Reader getCharacterStream(String columnName) throws SQLException {
		int index = cursor.getColumnIndex(columnName);
		return new StringReader(cursor.getString(index));
	}

	@Override
	public Clob getClob(int columnIndex) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public Clob getClob(String colName) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public int getConcurrency() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public String getCursorName() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public Date getDate(int columnIndex) throws SQLException {
		sqLiteDate = new SQLiteDate(cursor.getString(columnIndex), TemporalType.DATE);
		return new Date(sqLiteDate.getDate().getTime());
	}

	@Override
	public Date getDate(String columnName) throws SQLException {
		int index = cursor.getColumnIndex(columnName);
		sqLiteDate = new SQLiteDate(cursor.getString(index), TemporalType.DATE);
		return new Date(sqLiteDate.getDate().getTime());
	}

	@Override
	public Date getDate(int columnIndex, Calendar cal) throws SQLException {
		sqLiteDate = new SQLiteDate(cursor.getString(columnIndex - 1), TemporalType.DATE);
		return new Date(sqLiteDate.getDate().getTime());
	}

	@Override
	public Date getDate(String columnName, Calendar cal) throws SQLException {
		int index = cursor.getColumnIndex(columnName);
		sqLiteDate = new SQLiteDate(cursor.getString(index), TemporalType.DATE);
		return new Date(sqLiteDate.getDate().getTime());
	}

	@Override
	public double getDouble(int columnIndex) throws SQLException {
		return cursor.getDouble(columnIndex - 1);
	}

	@Override
	public double getDouble(String columnName) throws SQLException {
		int index = cursor.getColumnIndex(columnName);
		return cursor.getDouble(index);
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
	public float getFloat(int columnIndex) throws SQLException {
		return cursor.getFloat(columnIndex - 1);
	}

	@Override
	public float getFloat(String columnName) throws SQLException {
		int index = cursor.getColumnIndex(columnName);
		return cursor.getFloat(index);
	}

	@Override
	public int getInt(int columnIndex) throws SQLException {
		return cursor.getInt(columnIndex - 1);
	}

	@Override
	public int getInt(String columnName) throws SQLException {
		int index = cursor.getColumnIndex(columnName);
		return cursor.getInt(index);
	}

	@Override
	public long getLong(int columnIndex) throws SQLException {
		return cursor.getLong(columnIndex - 1);
	}

	@Override
	public long getLong(String columnName) throws SQLException {
		int index = cursor.getColumnIndex(columnName);
		return cursor.getLong(index);
	}

	@Override
	public ResultSetMetaData getMetaData() throws SQLException {
		return new SQLiteResultSetMetadata(cursor);
	}

	@Override
	public Object getObject(int columnIndex) throws SQLException {
		switch (getDataType((SQLiteCursor) cursor, columnIndex - 1)) {
		case FIELD_TYPE_INTEGER:
			long val = getLong(columnIndex);
			if (val > Integer.MAX_VALUE || val < Integer.MIN_VALUE) {
				return new Long(val);
			}
			else {
				return new Integer((int) val);
			}
		case FIELD_TYPE_FLOAT:
			return new Double(getDouble(columnIndex));
		case FIELD_TYPE_BLOB:
			return getBytes(columnIndex);
		case FIELD_TYPE_NULL:
			return null;
		case FIELD_TYPE_STRING:
		default:
			return getString(columnIndex);
		}
	}

	public static int getDataType(SQLiteCursor _cursor, int column) throws SQLException {
		CursorWindow cursorWindow = _cursor.getWindow();
		int pos = _cursor.getPosition();
		int type = -1;
		if (cursorWindow.isNull(pos, column)) {
			type = FIELD_TYPE_NULL;
		} else if (cursorWindow.isLong(pos, column)) {
			type = FIELD_TYPE_INTEGER;
		} else if (cursorWindow.isFloat(pos, column)) {
			type = FIELD_TYPE_FLOAT;
		} else if (cursorWindow.isString(pos, column)) {
			type = FIELD_TYPE_STRING;
		} else if (cursorWindow.isBlob(pos, column)) {
			type = FIELD_TYPE_BLOB;
		}
		return type;
	}

	@Override
	public Object getObject(String columnName) throws SQLException {
		int index = findColumn(columnName);
		return getObject(index);
	}

	@Override
	public Object getObject(int columnIndex, Map<String, Class<?>> arg1) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public Object getObject(String columnIndex, Map<String, Class<?>> arg1) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public Ref getRef(int columnIndex) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public Ref getRef(String columnName) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public int getRow() throws SQLException {
		return cursor.getPosition() + 1;
	}

	@Override
	public short getShort(int columnIndex) throws SQLException {
		return cursor.getShort(columnIndex - 1);
	}

	@Override
	public short getShort(String columnName) throws SQLException {
		int index = cursor.getColumnIndex(columnName);
		return cursor.getShort(index);
	}

	@Override
	public Statement getStatement() throws SQLException {
		return statement;
	}

	@Override
	public String getString(int columnIndex) throws SQLException {
		return cursor.getString(columnIndex - 1);
	}

	@Override
	public String getString(String columnName) throws SQLException {
		int index = cursor.getColumnIndex(columnName);
		return cursor.getString(index);
	}

	@Override
	public Time getTime(int columnIndex) throws SQLException {
		sqLiteDate = new SQLiteDate(cursor.getString(columnIndex - 1), TemporalType.TIME);
		return new Time(sqLiteDate.getDate().getTime());
	}

	@Override
	public Time getTime(String columnName) throws SQLException {
		int index = cursor.getColumnIndex(columnName);
		sqLiteDate = new SQLiteDate(cursor.getString(index), TemporalType.TIME);
		return new Time(sqLiteDate.getDate().getTime());
	}

	@Override
	public Time getTime(int columnIndex, Calendar cal) throws SQLException {
		sqLiteDate = new SQLiteDate(cursor.getString(columnIndex - 1), TemporalType.TIME);
		return new Time(sqLiteDate.getDate().getTime());
	}

	@Override
	public Time getTime(String columnName, Calendar cal) throws SQLException {
		int index = cursor.getColumnIndex(columnName);
		sqLiteDate = new SQLiteDate(cursor.getString(index), TemporalType.TIME);
		return new Time(sqLiteDate.getDate().getTime());
	}

	@Override
	public Timestamp getTimestamp(int columnIndex) throws SQLException {
		sqLiteDate = new SQLiteDate(cursor.getString(columnIndex - 1), TemporalType.DATE_TIME);
		return new Timestamp(sqLiteDate.getDate().getTime());
	}

	@Override
	public Timestamp getTimestamp(String columnName) throws SQLException {
		int index = cursor.getColumnIndex(columnName);
		sqLiteDate = new SQLiteDate(cursor.getString(index), TemporalType.DATE_TIME);
		return new Timestamp(sqLiteDate.getDate().getTime());
	}

	@Override
	public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
		sqLiteDate = new SQLiteDate(cursor.getString(columnIndex - 1), TemporalType.DATE_TIME);
		return new Timestamp(sqLiteDate.getDate().getTime());
	}

	@Override
	public Timestamp getTimestamp(String columnName, Calendar cal) throws SQLException {
		int index = cursor.getColumnIndex(columnName);
		sqLiteDate = new SQLiteDate(cursor.getString(index), TemporalType.DATE_TIME);
		return new Timestamp(sqLiteDate.getDate().getTime());
	}

	@Override
	public int getType() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public URL getURL(int columnIndex) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public URL getURL(String columnName) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public InputStream getUnicodeStream(int columnIndex) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public InputStream getUnicodeStream(String columnName) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public SQLWarning getWarnings() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void insertRow() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public boolean isAfterLast() throws SQLException {
		return cursor.isAfterLast();
	}

	@Override
	public boolean isBeforeFirst() throws SQLException {
		return cursor.isBeforeFirst();
	}

	@Override
	public boolean isFirst() throws SQLException {
		return cursor.isFirst();
	}

	@Override
	public boolean isLast() throws SQLException {
		return cursor.isLast();
	}

	@Override
	public boolean last() throws SQLException {
		return cursor.moveToLast();
	}

	@Override
	public void moveToCurrentRow() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void moveToInsertRow() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public boolean next() throws SQLException {
		return cursor.moveToNext();
	}

	@Override
	public boolean previous() throws SQLException {
		return cursor.moveToPrevious();
	}

	@Override
	public void refreshRow() throws SQLException {
		cursor.requery();
	}

	@Override
	public boolean relative(int rows) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public boolean rowDeleted() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public boolean rowInserted() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public boolean rowUpdated() throws SQLException {
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
	public void updateArray(int columnIndex, Array x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateArray(String columnName, Array x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateBlob(int columnIndex, Blob x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateBlob(String columnName, Blob x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateBoolean(int columnIndex, boolean x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateBoolean(String columnName, boolean x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateByte(int columnIndex, byte x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateByte(String columnName, byte x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateBytes(int columnIndex, byte[] x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateBytes(String columnName, byte[] x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateCharacterStream(String columnName, Reader reader, int length) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateClob(int columnIndex, Clob x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateClob(String columnName, Clob x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateDate(int columnIndex, Date x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateDate(String columnName, Date x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateDouble(int columnIndex, double x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateDouble(String columnName, double x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateFloat(int columnIndex, float x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateFloat(String columnName, float x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateInt(int columnIndex, int x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateInt(String columnName, int x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateLong(int columnIndex, long x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateLong(String columnName, long x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateNull(int columnIndex) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateNull(String columnName) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateObject(int columnIndex, Object x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateObject(String columnName, Object x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateObject(int columnIndex, Object x, int scale) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateObject(String columnName, Object x, int scale) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateRef(int columnIndex, Ref x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateRef(String columnName, Ref x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateRow() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateShort(int columnIndex, short x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateShort(String columnName, short x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateString(int columnIndex, String x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateString(String columnName, String x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateTime(int columnIndex, Time x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateTime(String columnName, Time x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public void updateTimestamp(String columnName, Timestamp x) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public boolean wasNull() throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public RowId getRowId(int columnIndex) throws SQLException {
		return null;
	}

	@Override
	public RowId getRowId(String columnLabel) throws SQLException {
		return null;
	}

	@Override
	public void updateRowId(int columnIndex, RowId value) throws SQLException {

	}

	@Override
	public void updateRowId(String columnLabel, RowId value) throws SQLException {

	}

	@Override
	public int getHoldability() throws SQLException {
		return 0;
	}

	@Override
	public boolean isClosed() throws SQLException {
		return false;
	}

	@Override
	public void updateNString(int columnIndex, String nString) throws SQLException {

	}

	@Override
	public void updateNString(String columnLabel, String nString) throws SQLException {

	}

	@Override
	public void updateNClob(int columnIndex, NClob nClob) throws SQLException {

	}

	@Override
	public void updateNClob(String columnLabel, NClob nClob) throws SQLException {

	}

	@Override
	public NClob getNClob(int columnIndex) throws SQLException {
		return null;
	}

	@Override
	public NClob getNClob(String columnLabel) throws SQLException {
		return null;
	}

	@Override
	public SQLXML getSQLXML(int columnIndex) throws SQLException {
		return null;
	}

	@Override
	public SQLXML getSQLXML(String columnLabel) throws SQLException {
		return null;
	}

	@Override
	public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {

	}

	@Override
	public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {

	}

	@Override
	public String getNString(int columnIndex) throws SQLException {
		return null;
	}

	@Override
	public String getNString(String columnLabel) throws SQLException {
		return null;
	}

	@Override
	public Reader getNCharacterStream(int columnIndex) throws SQLException {
		return null;
	}

	@Override
	public Reader getNCharacterStream(String columnLabel) throws SQLException {
		return null;
	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {

	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {

	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {

	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {

	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {

	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {

	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {

	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException {

	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {

	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {

	}

	@Override
	public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {

	}

	@Override
	public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {

	}

	@Override
	public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {

	}

	@Override
	public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {

	}

	@Override
	public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {

	}

	@Override
	public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException {

	}

	@Override
	public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {

	}

	@Override
	public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {

	}

	@Override
	public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {

	}

	@Override
	public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {

	}

	@Override
	public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {

	}

	@Override
	public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException {

	}

	@Override
	public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {

	}

	@Override
	public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {

	}

	@Override
	public void updateClob(int columnIndex, Reader reader) throws SQLException {

	}

	@Override
	public void updateClob(String columnLabel, Reader reader) throws SQLException {

	}

	@Override
	public void updateNClob(int columnIndex, Reader reader) throws SQLException {

	}

	@Override
	public void updateNClob(String columnLabel, Reader reader) throws SQLException {

	}

	public Cursor getCursor() {
		return cursor;
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
