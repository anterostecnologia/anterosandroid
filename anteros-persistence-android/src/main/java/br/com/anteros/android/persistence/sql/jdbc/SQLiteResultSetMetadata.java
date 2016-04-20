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

import java.lang.reflect.Method;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import android.database.Cursor;
import br.com.anteros.core.utils.ReflectionUtils;

/**
 * 
 * @author Edson Martins - Anteros
 *
 */
public class SQLiteResultSetMetadata implements ResultSetMetaData {

	public static final int FIELD_TYPE_NULL = 0;
	public static final int FIELD_TYPE_INTEGER = 1;
	public static final int FIELD_TYPE_FLOAT = 2;
	public static final int FIELD_TYPE_STRING = 3;
	public static final int FIELD_TYPE_BLOB = 4;

	private Cursor cursor;

	public SQLiteResultSetMetadata(Cursor cursor) {
		if (cursor == null) {
			throw new RuntimeException("Informe um cursor para o Metadata.");
		}
		this.cursor = cursor;
	}

	@Override
	public String getCatalogName(int columnIndex) throws SQLException {
		return "";
	}

	@Override
	public String getColumnClassName(int columnIndex) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public int getColumnCount() throws SQLException {
		return cursor.getColumnCount();
	}

	@Override
	public int getColumnDisplaySize(int columnIndex) throws SQLException {
		throw new SQLException("Método não suportado no Anteros Persistence para Android.");
	}

	@Override
	public String getColumnLabel(int columnIndex) throws SQLException {
		return cursor.getColumnName(columnIndex - 1);
	}

	@Override
	public String getColumnName(int columnIndex) throws SQLException {
		return cursor.getColumnName(columnIndex - 1);
	}

	@Override
	public int getColumnType(int columnIndex) throws SQLException {
		int oldPos = cursor.getPosition();
		boolean moved = false;
		if (cursor.isBeforeFirst() || cursor.isAfterLast()) {
			boolean resultSetEmpty = cursor.getCount() == 0 || cursor.isAfterLast();
			if (resultSetEmpty) {
				return Types.NULL;
			}
			cursor.moveToFirst();
			moved = true;
		}

		Method[] allMethods = ReflectionUtils.getAllMethods(cursor.getClass());
		Method methodGetType = null;
		for (Method mt : allMethods) {
			if (mt.getName().equalsIgnoreCase("getType")) {
				methodGetType = mt;
				break;
			}
		}
		int type = Types.NULL;
		if (methodGetType != null) {
			int nativeType;
			try {
				nativeType = ((Integer) ReflectionUtils.invokeMethod(cursor, "getType",
						new Object[] { columnIndex - 1 })).intValue();
			} catch (Exception e) {
				return type;
			}

			switch (nativeType) {
			case FIELD_TYPE_NULL:
				type = Types.NULL;
				break;
			case FIELD_TYPE_INTEGER:
				type = Types.INTEGER;
				break;
			case FIELD_TYPE_FLOAT:
				type = Types.FLOAT;
				break;
			case FIELD_TYPE_STRING:
				type = Types.VARCHAR;
				break;
			case FIELD_TYPE_BLOB:
				type = Types.BLOB;
				break;
			default:
				type = Types.NULL;
				break;
			}
		}
		if (moved) {
			cursor.moveToPosition(oldPos);
		}
		return type;
	}

	@Override
	public String getColumnTypeName(int columnIndex) throws SQLException {
		int type = getColumnType(columnIndex);
		String result = "";
		switch (type) {
		case FIELD_TYPE_NULL:
			result = "NULL";
			break;
		case FIELD_TYPE_INTEGER:
			result = "INTEGER";
			break;
		case FIELD_TYPE_FLOAT:
			result = "FLOAT";
			break;
		case FIELD_TYPE_STRING:
			result = "VARCHAR";
			break;
		case FIELD_TYPE_BLOB:
			result = "BLOB";
			break;
		default:
			result = "NULL";
			break;
		}
		return result;
	}

	@Override
	public int getPrecision(int columnIndex) throws SQLException {
		return 0;
	}

	@Override
	public int getScale(int columnIndex) throws SQLException {
		return 0;
	}

	@Override
	public String getSchemaName(int columnIndex) throws SQLException {
		return "";
	}

	@Override
	public String getTableName(int columnIndex) throws SQLException {
		return "";
	}

	@Override
	public boolean isAutoIncrement(int columnIndex) throws SQLException {
		return false;
	}

	@Override
	public boolean isCaseSensitive(int columnIndex) throws SQLException {
		return false;
	}

	@Override
	public boolean isCurrency(int columnIndex) throws SQLException {
		return false;
	}

	@Override
	public boolean isDefinitelyWritable(int columnIndex) throws SQLException {
		return false;
	}

	@Override
	public int isNullable(int columnIndex) throws SQLException {
		return 0;
	}

	@Override
	public boolean isReadOnly(int columnIndex) throws SQLException {
		return false;
	}

	@Override
	public boolean isSearchable(int columnIndex) throws SQLException {
		return false;
	}

	@Override
	public boolean isSigned(int columnIndex) throws SQLException {
		return false;
	}

	@Override
	public boolean isWritable(int columnIndex) throws SQLException {
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
