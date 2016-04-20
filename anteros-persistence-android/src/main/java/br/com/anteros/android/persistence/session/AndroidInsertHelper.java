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

import java.util.HashMap;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import br.com.anteros.android.persistence.exception.AndroidInsertException;
import br.com.anteros.core.log.Logger;
import br.com.anteros.core.log.LoggerProvider;

public class AndroidInsertHelper {

	private static Logger LOG = LoggerProvider.getInstance().getLogger(AndroidInsertHelper.class.getName());

	private final SQLiteDatabase mDb;
	private final String mTableName;
	private HashMap<String, Integer> mColumns;
	private String mInsertSQL = null;
	private SQLiteStatement mInsertStatement = null;
	private SQLiteStatement mReplaceStatement = null;
	private SQLiteStatement mPreparedStatement = null;
	private static final boolean LOCAL_LOGV = false;
	private static final String TAG = "AnterosMobile";

	public static final int TABLE_INFO_PRAGMA_COLUMNNAME_INDEX = 1;
	public static final int TABLE_INFO_PRAGMA_DEFAULT_INDEX = 4;

	public AndroidInsertHelper(SQLiteDatabase db, String tableName) {
		mDb = db;
		mTableName = tableName;
	}

	private void buildSQL() throws SQLException {
		StringBuilder sb = new StringBuilder(128);
		sb.append("INSERT INTO ");
		sb.append(mTableName);
		sb.append(" (");

		StringBuilder sbv = new StringBuilder(128);
		sbv.append("VALUES (");

		int i = 1;
		Cursor cur = null;
		try {
			cur = mDb.rawQuery("PRAGMA table_info(" + mTableName + ")", null);
			mColumns = new HashMap<String, Integer>(cur.getCount());
			while (cur.moveToNext()) {
				String columnName = cur.getString(TABLE_INFO_PRAGMA_COLUMNNAME_INDEX);
				String defaultValue = cur.getString(TABLE_INFO_PRAGMA_DEFAULT_INDEX);

				mColumns.put(columnName, i);
				sb.append("'");
				sb.append(columnName);
				sb.append("'");

				if (defaultValue == null) {
					sbv.append("?");
				} else {
					sbv.append("COALESCE(?, ");
					sbv.append(defaultValue);
					sbv.append(")");
				}

				sb.append(i == cur.getCount() ? ") " : ", ");
				sbv.append(i == cur.getCount() ? ");" : ", ");
				++i;
			}
		} finally {
			if (cur != null)
				cur.close();
		}

		sb.append(sbv);

		mInsertSQL = sb.toString();
		if (LOCAL_LOGV)
			LOG.verbose("insert statement is " + mInsertSQL);
	}

	private SQLiteStatement getStatement(boolean allowReplace) throws SQLException {
		if (allowReplace) {
			if (mReplaceStatement == null) {
				if (mInsertSQL == null)
					buildSQL();
				String replaceSQL = "INSERT OR REPLACE" + mInsertSQL.substring(6);
				mReplaceStatement = mDb.compileStatement(replaceSQL);
			}
			return mReplaceStatement;
		} else {
			if (mInsertStatement == null) {
				if (mInsertSQL == null)
					buildSQL();
				mInsertStatement = mDb.compileStatement(mInsertSQL);
			}
			return mInsertStatement;
		}
	}

	private synchronized long insertInternal(ContentValues values, boolean allowReplace) {
		try {
			SQLiteStatement stmt = getStatement(allowReplace);
			stmt.clearBindings();
			if (LOCAL_LOGV)
				LOG.verbose("--- inserting in table " + mTableName);
			for (Map.Entry<String, Object> e : values.valueSet()) {
				final String key = e.getKey();
				int i = getColumnIndex(key);
				DatabaseUtils.bindObjectToProgram(stmt, i, e.getValue());
				if (LOCAL_LOGV) {
					LOG.verbose("binding " + e.getValue() + " to column " + i + " (" + key + ")");
				}
			}
			return stmt.executeInsert();
		} catch (SQLException e) {
			e.printStackTrace();
			LOG.error("Error inserting " + values + " into table  " + mTableName, e);
			return -1;
		}
	}

	public int getColumnIndex(String key) {
		getStatement(false);
		final Integer index = mColumns.get(key);
		if (index == null) {
			throw new IllegalArgumentException("column '" + key + "' is invalid into table '" + mTableName + "'");
		}
		return index;
	}

	public void bind(int index, double value) {
		mPreparedStatement.bindDouble(index, value);
	}

	public void bind(int index, float value) {
		mPreparedStatement.bindDouble(index, value);
	}

	public void bind(int index, long value) {
		mPreparedStatement.bindLong(index, value);
	}

	public void bind(int index, int value) {
		mPreparedStatement.bindLong(index, value);
	}

	public void bind(int index, boolean value) {
		mPreparedStatement.bindLong(index, value ? 1 : 0);
	}

	public void bindNull(int index) {
		mPreparedStatement.bindNull(index);
	}

	public void bind(int index, byte[] value) {
		if (value == null) {
			mPreparedStatement.bindNull(index);
		} else {
			mPreparedStatement.bindBlob(index, value);
		}
	}

	public void bind(int index, String value) {
		if (value == null) {
			mPreparedStatement.bindNull(index);
		} else {
			mPreparedStatement.bindString(index, value);
		}
	}

	public long insert(ContentValues values) {
		return insertInternal(values, false);
	}

	public long execute() throws Exception {
		if (mPreparedStatement == null) {
			throw new IllegalStateException("you must prepare this inserter before calling " + "execute");
		}
		try {
			if (LOCAL_LOGV)
				LOG.verbose("--- doing insert or replace in table " + mTableName);
			return mPreparedStatement.executeInsert();
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Error executing InsertHelper with table " + mTableName, e);
			throw new AndroidInsertException(e.getMessage());
		} finally {
			mPreparedStatement = null;
		}
	}

	public void prepareForInsert() {
		mPreparedStatement = getStatement(false);
		mPreparedStatement.clearBindings();
	}

	public void prepareForReplace() {
		mPreparedStatement = getStatement(true);
		mPreparedStatement.clearBindings();
	}

	public long replace(ContentValues values) {
		return insertInternal(values, true);
	}

	public void close() {
		if (mInsertStatement != null) {
			mInsertStatement.close();
			mInsertStatement = null;
		}
		if (mReplaceStatement != null) {
			mReplaceStatement.close();
			mReplaceStatement = null;
		}
		mInsertSQL = null;
		mColumns = null;
	}
}