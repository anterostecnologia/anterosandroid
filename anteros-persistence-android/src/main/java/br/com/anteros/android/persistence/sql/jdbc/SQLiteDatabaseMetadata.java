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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.RowIdLifetime;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Edson Martins - Anteros
 * 
 */

public class SQLiteDatabaseMetadata implements DatabaseMetaData {

	protected final static Pattern PK_UNNAMED = Pattern.compile(".* primary +key *\\((.*?,+.*?)\\).*", Pattern.CASE_INSENSITIVE);

	protected final static Pattern PK_NAMED = Pattern.compile(".* constraint +(.*?) +primary +key *\\((.*?)\\).*", Pattern.CASE_INSENSITIVE);

	private PreparedStatement getTableTypes = null, getTypeInfo = null, getCatalogs = null, getSchemas = null, getUDTs = null,
			getColumnsTblName = null, getSuperTypes = null, getSuperTables = null, getTablePrivileges = null, 
			getProcedures = null, getProcedureColumns = null, getAttributes = null, getBestRowIdentifier = null, getVersionColumns = null,
			getColumnPrivileges = null;

	private SQLiteConnection connection;

	public SQLiteDatabaseMetadata(SQLiteConnection connection) {
		this.connection = connection;
	}

	@Override
	public boolean allProceduresAreCallable() throws SQLException {
		return false;
	}

	@Override
	public boolean allTablesAreSelectable() throws SQLException {
		return true;
	}

	@Override
	public boolean dataDefinitionCausesTransactionCommit() throws SQLException {
		return false;
	}

	@Override
	public boolean dataDefinitionIgnoredInTransactions() throws SQLException {
		return false;
	}

	@Override
	public boolean deletesAreDetected(int arg0) throws SQLException {
		return false;
	}

	@Override
	public boolean doesMaxRowSizeIncludeBlobs() throws SQLException {
		return false;
	}

	@Override
	public ResultSet getAttributes(String catalog, String schemaPattern, String typeNamePattern, String attributeNamePattern) throws SQLException {
		if (getAttributes == null) {
			getAttributes = connection.prepareStatement("select " + "null as TYPE_CAT, " + "null as TYPE_SCHEM, " + "null as TYPE_NAME, "
					+ "null as ATTR_NAME, " + "null as DATA_TYPE, " + "null as ATTR_TYPE_NAME, " + "null as ATTR_SIZE, " + "null as DECIMAL_DIGITS, "
					+ "null as NUM_PREC_RADIX, " + "null as NULLABLE, " + "null as REMARKS, " + "null as ATTR_DEF, " + "null as SQL_DATA_TYPE, "
					+ "null as SQL_DATETIME_SUB, " + "null as CHAR_OCTET_LENGTH, " + "null as ORDINAL_POSITION, " + "null as IS_NULLABLE, "
					+ "null as SCOPE_CATALOG, " + "null as SCOPE_SCHEMA, " + "null as SCOPE_TABLE, " + "null as SOURCE_DATA_TYPE limit 0;");
		}
		return getAttributes.executeQuery();
	}

	@Override
	public ResultSet getBestRowIdentifier(String catalog, String schema, String table, int scope, boolean nullable) throws SQLException {
		if (getBestRowIdentifier == null) {
			getBestRowIdentifier = connection.prepareStatement("select " + "null as SCOPE, " + "null as COLUMN_NAME, " + "null as DATA_TYPE, "
					+ "null as TYPE_NAME, " + "null as COLUMN_SIZE, " + "null as BUFFER_LENGTH, " + "null as DECIMAL_DIGITS, "
					+ "null as PSEUDO_COLUMN limit 0;");
		}
		return getBestRowIdentifier.executeQuery();
	}

	@Override
	public String getCatalogSeparator() throws SQLException {
		return ".";
	}

	@Override
	public String getCatalogTerm() throws SQLException {
		return "catalog";
	}

	@Override
	public ResultSet getCatalogs() throws SQLException {
		if (getCatalogs == null) {
			getCatalogs = connection.prepareStatement("select null as TABLE_CAT limit 0;");
		}
		getCatalogs.clearParameters();
		return getCatalogs.executeQuery();
	}

	@Override
	public ResultSet getColumnPrivileges(String catalog, String schema, String table, String columnNamePattern) throws SQLException {
		if (getColumnPrivileges == null) {
			getColumnPrivileges = connection.prepareStatement("select " + "null as TABLE_CAT, " + "null as TABLE_SCHEM, " + "null as TABLE_NAME, "
					+ "null as COLUMN_NAME, " + "null as GRANTOR, " + "null as GRANTEE, " + "null as PRIVILEGE, " + "null as IS_GRANTABLE limit 0;");
		}
		return getColumnPrivileges.executeQuery();
	}

	@Override
	public ResultSet getColumns(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) throws SQLException {
		Statement stat = connection.createStatement();
		ResultSet rs;
		String sql;

		checkOpen();

		if (getColumnsTblName == null) {
			getColumnsTblName = connection.prepareStatement("select tbl_name from sqlite_master where tbl_name like ?;");
		}

		getColumnsTblName.setString(1, tableNamePattern);
		rs = getColumnsTblName.executeQuery();
		if (!rs.next()) {
			return rs;
		}
		tableNamePattern = rs.getString(1);
		rs.close();

		sql = "select " + "null as TABLE_CAT, " + "null as TABLE_SCHEM, " + "'" + escape(tableNamePattern) + "' as TABLE_NAME, "
				+ "cn as COLUMN_NAME, " + "ct as DATA_TYPE, " + "tn as TYPE_NAME, " + "2000000000 as COLUMN_SIZE, " + "2000000000 as BUFFER_LENGTH, "
				+ "10   as DECIMAL_DIGITS, " + "10   as NUM_PREC_RADIX, " + "colnullable as NULLABLE, " + "null as REMARKS, "
				+ "colDefault as COLUMN_DEF, " + "0    as SQL_DATA_TYPE, " + "0    as SQL_DATETIME_SUB, " + "2000000000 as CHAR_OCTET_LENGTH, "
				+ "ordpos as ORDINAL_POSITION, " + "(case colnullable when 0 then 'NO' when 1 then 'YES' else '' end)" + "    as IS_NULLABLE, "
				+ "null as SCOPE_CATLOG, " + "null as SCOPE_SCHEMA, " + "null as SCOPE_TABLE, " + "null as SOURCE_DATA_TYPE from (";

		rs = stat.executeQuery("pragma table_info ('" + escape(tableNamePattern) + "');");

		boolean colFound = false;
		for (int i = 0; rs.next(); i++) {
			String colName = rs.getString(2);
			String colType = rs.getString(3);
			String colNotNull = rs.getString(4);
			String colDefault = rs.getString(5);

			int colNullable = 2;
			if (colNotNull != null) {
				colNullable = colNotNull.equals("0") ? 1 : 0;
			}
			if (colFound) {
				sql += " union all ";
			}
			colFound = true;

			colType = colType == null ? "TEXT" : colType.toUpperCase();
			int colJavaType = -1;
			if (colType.matches(".*(INT|BOOL).*")) {
				colJavaType = Types.INTEGER;
			} else if (colType.matches(".*(CHAR|CLOB|TEXT|BLOB).*")) {
				colJavaType = Types.VARCHAR;
			} else if (colType.matches(".*(REAL|FLOA|DOUB|DEC|NUM).*")) {
				colJavaType = Types.FLOAT;
			} else {
				colJavaType = Types.VARCHAR;
			}

			sql += "select " + i + " as ordpos, " + colNullable + " as colnullable, '" + colJavaType + "' as ct, '" + escape(colName) + "' as cn, '"
					+ escape(colType) + "' as tn, " + quote(colDefault == null ? null : escape(colDefault)) + " as colDefault";

			if (columnNamePattern != null) {
				sql += " where upper(cn) like upper('" + escape(columnNamePattern) + "')";
			}
		}
		sql += colFound ? ");" : "select null as ordpos, null as colnullable, null as cn, null as tn, null as colDefault) limit 0;";
		rs.close();

		return stat.executeQuery(sql);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return connection;
	}

	@Override
	public ResultSet getCrossReference(String primaryCatalog, String primarySchema, String primaryTable, String foreignCatalog, String foreignSchema,
			String foreignTable) throws SQLException {
		if (primaryTable == null) {
			return getExportedKeys(foreignCatalog, foreignSchema, foreignTable);
		}
		if (foreignTable == null) {
			return getImportedKeys(primaryCatalog, primarySchema, primaryTable);
		}

		StringBuilder query = new StringBuilder();
		query.append(String.format("select %s as PKTABLE_CAT, %s as PKTABLE_SCHEM, %s as PKTABLE_NAME, ", quote(primaryCatalog),
				quote(primarySchema), quote(primaryTable))
				+ "'' as PKCOLUMN_NAME, "
				+ String.format("%s as FKTABLE_CAT, %s as FKTABLE_SCHEM,  %s as FKTABLE_NAME, ", quote(foreignCatalog), quote(foreignSchema),
						quote(foreignTable))
				+ "'' as FKCOLUMN_NAME, -1 as KEY_SEQ, 3 as UPDATE_RULE, "
				+ "3 as DELETE_RULE, '' as FK_NAME, '' as PK_NAME, " + Integer.toString(importedKeyInitiallyDeferred) + " as DEFERRABILITY limit 0;");
		return connection.createStatement().executeQuery(query.toString());
	}

	@Override
	public int getDatabaseMajorVersion() throws SQLException {
		return 0;
	}

	@Override
	public int getDatabaseMinorVersion() throws SQLException {
		return 0;
	}

	@Override
	public String getDatabaseProductName() throws SQLException {
		return "SQLite";
	}

	@Override
	public String getDatabaseProductVersion() throws SQLException {
		return "";
	}

	@Override
	public int getDefaultTransactionIsolation() throws SQLException {
		return Connection.TRANSACTION_SERIALIZABLE;
	}

	@Override
	public int getDriverMajorVersion() {
		return 0;
	}

	@Override
	public int getDriverMinorVersion() {
		return 0;
	}

	@Override
	public String getDriverName() throws SQLException {
		return "Anteros";
	}

	@Override
	public String getDriverVersion() throws SQLException {
		return "";
	}

	@Override
	public ResultSet getExportedKeys(String catalog, String schema, String table) throws SQLException {
		StringBuilder exportedKeysQuery = new StringBuilder();
		exportedKeysQuery.append(String.format("select %s as PKTABLE_CAT, %s as PKTABLE_SCHEM, %s as PKTABLE_NAME, ", quote(catalog), quote(schema),
				quote(table))
				+ String.format("pcn as PKCOLUMN_NAME, %s as FKTABLE_CAT, %s as FKTABLE_SCHEM, ", quote(catalog), quote(schema))
				+ "fkn as FKTABLE_NAME, fcn as FKCOLUMN_NAME, "
				+ "ks as KEY_SEQ, "
				+ "ur as UPDATE_RULE, "
				+ "dr as DELETE_RULE, "
				+ "'' as FK_NAME, " + "'' as PK_NAME, " + Integer.toString(importedKeyInitiallyDeferred) + " as DEFERRABILITY from (");

		String tableListQuery = String.format("select name from sqlite_master where type = 'table'");
		Statement stat = connection.createStatement();
		ResultSet rs = stat.executeQuery(tableListQuery);
		ArrayList<String> tableList = new ArrayList<String>();
		while (rs.next()) {
			tableList.add(rs.getString(1));
		}
		rs.close();

		int count = 0;
		for (String targetTable : tableList) {
			String foreignKeyQuery = String.format("pragma foreign_key_list('%s');", escape(targetTable));

			try {
				ResultSet fk = stat.executeQuery(foreignKeyQuery);
				for (; fk.next();) {
					int keySeq = fk.getInt(2) + 1;
					String PKTabName = fk.getString(3);
					String FKColName = fk.getString(4);
					String PKColName = fk.getString(5);
					String updateRule = fk.getString(6);
					String deleteRule = fk.getString(7);

					if (PKTabName == null || !PKTabName.equals(table)) {
						continue;
					}

					if (count > 0) {
						exportedKeysQuery.append(" union all ");
					}

					exportedKeysQuery.append("select " + Integer.toString(keySeq) + " as ks," + "'" + escape(targetTable) + "' as fkn," + "'"
							+ escape(FKColName) + "' as fcn," + "'" + escape(PKColName) + "' as pcn,"
							+ String.format("case '%s' ", escape(updateRule)) + String.format("when 'NO ACTION' then %d ", importedKeyNoAction)
							+ String.format("when 'CASCADE' then %d ", importedKeyCascade)
							+ String.format("when 'RESTRICT' then %d  ", importedKeyRestrict)
							+ String.format("when 'SET NULL' then %d  ", importedKeySetNull)
							+ String.format("when 'SET DEFAULT' then %d  ", importedKeySetDefault) + "end as ur,"
							+ String.format("case '%s' ", escape(deleteRule)) + String.format("when 'NO ACTION' then %d ", importedKeyNoAction)
							+ String.format("when 'CASCADE' then %d ", importedKeyCascade)
							+ String.format("when 'RESTRICT' then %d  ", importedKeyRestrict)
							+ String.format("when 'SET NULL' then %d  ", importedKeySetNull)
							+ String.format("when 'SET DEFAULT' then %d  ", importedKeySetDefault) + "end as dr");

					count++;
				}

				fk.close();
			} catch (SQLException e) {
				e.printStackTrace();
				// continue
			}
		}

		exportedKeysQuery.append(");");

		String sql = (count > 0) ? exportedKeysQuery.toString() : (String.format(
				"select %s as PKTABLE_CAT, %s as PKTABLE_SCHEM, %s as PKTABLE_NAME, ", quote(catalog), quote(schema), quote(table))
				+ "'' as PKCOLUMN_NAME, "
				+ String.format("%s as FKTABLE_CAT, %s as FKTABLE_SCHEM, ", quote(catalog), quote(schema))
				+ "'' as FKTABLE_NAME, "
				+ "'' as FKCOLUMN_NAME, "
				+ "-1 as KEY_SEQ, "
				+ "3 as UPDATE_RULE, "
				+ "3 as DELETE_RULE, "
				+ "'' as FK_NAME, " + "'' as PK_NAME, " + "5 as DEFERRABILITY limit 0;");
		return stat.executeQuery(sql);
	}

	@Override
	public String getExtraNameCharacters() throws SQLException {
		return "";
	}

	@Override
	public String getIdentifierQuoteString() throws SQLException {
		return " ";
	}

	@Override
	public ResultSet getImportedKeys(String catalog, String schema, String table) throws SQLException {
		String sql;
		ResultSet rs = null;
		Statement stat = connection.createStatement();

		sql = String.format("select %s as PKTABLE_CAT, %s as PKTABLE_SCHEM, ", quote(catalog), quote(schema))
				+ String.format("ptn as PKTABLE_NAME, pcn as PKCOLUMN_NAME, %s as FKTABLE_CAT, %s as FKTABLE_SCHEM, %s as FKTABLE_NAME, ",
						quote(catalog), quote(schema), quote(table)) + "fcn as FKCOLUMN_NAME, " + "ks as KEY_SEQ, " + "ur as UPDATE_RULE, "
				+ "dr as DELETE_RULE, " + "'' as FK_NAME, " + "'' as PK_NAME, " + Integer.toString(importedKeyInitiallyDeferred)
				+ " as DEFERRABILITY from (";

		try {
			rs = stat.executeQuery("pragma foreign_key_list('" + escape(table) + "');");
			boolean found = false;
			int i;
			for (i = 0; rs.next(); i++) {
				found = true;
				int keySeq = rs.getInt(2) + 1;
				String PKTabName = rs.getString(3);
				String FKColName = rs.getString(4);
				String PKColName = rs.getString(5);
				String updateRule = rs.getString(6);
				String deleteRule = rs.getString(7);

				if (i > 0) {
					sql += " union all ";
				}

				sql += String.format("select %d as ks,", keySeq)
						+ String.format("'%s' as ptn, '%s' as fcn, '%s' as pcn,", escape(PKTabName), escape(FKColName), escape(PKColName))
						+ String.format("case '%s' ", escape(updateRule)) + String.format("when 'NO ACTION' then %d ", importedKeyNoAction)
						+ String.format("when 'CASCADE' then %d ", importedKeyCascade)
						+ String.format("when 'RESTRICT' then %d  ", importedKeyRestrict)
						+ String.format("when 'SET NULL' then %d  ", importedKeySetNull)
						+ String.format("when 'SET DEFAULT' then %d  ", importedKeySetDefault) + "end as ur,"
						+ String.format("case '%s' ", escape(deleteRule)) + String.format("when 'NO ACTION' then %d ", importedKeyNoAction)
						+ String.format("when 'CASCADE' then %d ", importedKeyCascade)
						+ String.format("when 'RESTRICT' then %d  ", importedKeyRestrict)
						+ String.format("when 'SET NULL' then %d  ", importedKeySetNull)
						+ String.format("when 'SET DEFAULT' then %d  ", importedKeySetDefault) + "end as dr";
			}
			sql += ");";
			rs.close();
			if (!found)
				// se não cotem FK então retorna resultset vazio
				return stat.executeQuery("select null where 1=0;");
		} catch (SQLException e) {
			e.printStackTrace();
			sql += "select -1 as ks, '' as ptn, '' as fcn, '' as pcn, " + importedKeyNoAction + " as ur, " + importedKeyNoAction + " as dr) limit 0;";
		}

		return stat.executeQuery(sql);
	}

	@Override
	public ResultSet getIndexInfo(String catalog, String schema, String table, boolean unique, boolean approximate) throws SQLException {
		String sql;
		ResultSet rs = null;
		Statement stat = connection.createStatement();

		sql = "select " + "null as TABLE_CAT, " + "null as TABLE_SCHEM, " + "'" + escape(table) + "' as TABLE_NAME, " + "un as NON_UNIQUE, "
				+ "null as INDEX_QUALIFIER, " + "n as INDEX_NAME, " + Integer.toString(tableIndexOther) + " as TYPE, " + "op as ORDINAL_POSITION, "
				+ "cn as COLUMN_NAME, " + "null as ASC_OR_DESC, " + "0 as CARDINALITY, " + "0 as PAGES, " + "null as FILTER_CONDITION from (";

		try {
			ArrayList<ArrayList<Object>> indexList = new ArrayList<ArrayList<Object>>();
			String pragma = "pragma index_list('" + escape(table) + "')";
			rs = stat.executeQuery(pragma);
			while (rs.next()) {
				indexList.add(new ArrayList<Object>());
				indexList.get(indexList.size() - 1).add(rs.getString(2));
				indexList.get(indexList.size() - 1).add(rs.getInt(3));
			}
			rs.close();

			if (!indexList.isEmpty()) {
				String subSql = "";
				int i = 0;
				Iterator<ArrayList<Object>> indexIterator = indexList.iterator();
				ArrayList<Object> currentIndex;
				while (indexIterator.hasNext()) {
					currentIndex = indexIterator.next();
					String indexName = currentIndex.get(0).toString();
					int unq = (Integer) currentIndex.get(1);

					rs = stat.executeQuery("pragma index_info('" + escape(indexName) + "');");
					for (; rs.next(); i++) {

						int ordinalPosition = rs.getInt(1) + 1;
						String colName = rs.getString(3);

						if (i > 0) {
							sql += " union all ";
						}

						sql += "select " + Integer.toString(1 - unq) + " as un," + "'" + escape(indexName) + "' as n,"
								+ Integer.toString(ordinalPosition) + " as op," + "'" + escape(colName) + "' as cn";
						i++;
					}
					rs.close();
				}
				sql += subSql + ");";
			}else{
				sql += "select null as un, null as n, null as op, null as cn) limit 0;";
			}
		} catch (SQLException e) {
			e.printStackTrace();
			sql += "select null as un, null as n, null as op, null as cn) limit 0;";
		}

		return stat.executeQuery(sql);
	}

	@Override
	public int getJDBCMajorVersion() throws SQLException {
		return 0;
	}

	@Override
	public int getJDBCMinorVersion() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxBinaryLiteralLength() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxCatalogNameLength() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxCharLiteralLength() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxColumnNameLength() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxColumnsInGroupBy() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxColumnsInIndex() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxColumnsInOrderBy() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxColumnsInSelect() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxColumnsInTable() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxConnections() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxCursorNameLength() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxIndexLength() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxProcedureNameLength() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxRowSize() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxSchemaNameLength() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxStatementLength() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxStatements() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxTableNameLength() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxTablesInSelect() throws SQLException {
		return 0;
	}

	@Override
	public int getMaxUserNameLength() throws SQLException {
		return 0;
	}

	@Override
	public String getNumericFunctions() throws SQLException {
		return "";
	}

	@Override
	public ResultSet getPrimaryKeys(String catalog, String schema, String table) throws SQLException {
		String[] columnNames = null;
		String pkName = null;
		StringBuilder sql = new StringBuilder(512);
		sql.append("select null as TABLE_CAT, null as TABLE_SCHEM, '").append(escape(table))
				.append("' as TABLE_NAME, cn as COLUMN_NAME, ks as KEY_SEQ, pk as PK_NAME from (");

		Statement stat = connection.createStatement();
		ResultSet rs = stat.executeQuery("select sql from sqlite_master where" + " upper(name) = upper('" + escape(table) + "')");
		rs.next();

		Matcher matcher = PK_NAMED.matcher(rs.getString(1));
		if (matcher.find()) {
			pkName = '\'' + escape(matcher.group(1)) + '\'';
			columnNames = matcher.group(2).split(",");
		} else {
			matcher = PK_UNNAMED.matcher(rs.getString(1));
			if (matcher.find()) {
				columnNames = matcher.group(1).split(",");
			}
		}
		rs.close();

		if (columnNames != null) {
			for (int i = 0; i < columnNames.length; i++) {
				if (i > 0)
					sql.append(" union ");
				sql.append("select ").append(pkName).append(" as pk, '").append(escape(columnNames[i].trim())).append("' as cn, ").append(i)
						.append(" as ks");
			}

			return stat.executeQuery(sql.append(") order by cn;").toString());
		}

		rs = stat.executeQuery("pragma table_info('" + escape(table) + "');");
		int i;
		for (i = 0; rs.next(); i++) {
			String colName = rs.getString(2);

			if (!rs.getBoolean(6)) {
				i--;
				continue;
			}
			if (i > 0) {
				sql.append(" union all ");
			}

			sql.append("select null as pk, 0 as ks, '").append(escape(colName)).append("' as cn");
		}
		sql.append(i == 0 ? "select null as cn, null as pk, 0 as ks) order by cn limit 0;" : ") order by cn;");
		rs.close();

		return stat.executeQuery(sql.toString());
	}

	@Override
	public ResultSet getProcedureColumns(String catalog, String schemaPattern, String procedureNamePattern, String columnNamePattern)
			throws SQLException {
		if (getProcedures == null) {
			getProcedureColumns = connection.prepareStatement("select " + "null as PROCEDURE_CAT, " + "null as PROCEDURE_SCHEM, "
					+ "null as PROCEDURE_NAME, " + "null as COLUMN_NAME, " + "null as COLUMN_TYPE, " + "null as DATA_TYPE, " + "null as TYPE_NAME, "
					+ "null as PRECISION, " + "null as LENGTH, " + "null as SCALE, " + "null as RADIX, " + "null as NULLABLE, "
					+ "null as REMARKS limit 0;");
		}
		return getProcedureColumns.executeQuery();
	}

	@Override
	public String getProcedureTerm() throws SQLException {
		return "not_implemented";
	}

	@Override
	public ResultSet getProcedures(String catalog, String schemaPattern, String procedureNamePattern) throws SQLException {
		if (getProcedures == null) {
			getProcedures = connection.prepareStatement("select " + "null as PROCEDURE_CAT, " + "null as PROCEDURE_SCHEM, "
					+ "null as PROCEDURE_NAME, " + "null as UNDEF1, " + "null as UNDEF2, " + "null as UNDEF3, " + "null as REMARKS, "
					+ "null as PROCEDURE_TYPE limit 0;");
		}
		return getProcedures.executeQuery();
	}

	@Override
	public int getResultSetHoldability() throws SQLException {
		return ResultSet.CLOSE_CURSORS_AT_COMMIT;
	}

	@Override
	public String getSQLKeywords() throws SQLException {
		return "";
	}

	@Override
	public int getSQLStateType() throws SQLException {
		return sqlStateSQL99;
	}

	@Override
	public String getSchemaTerm() throws SQLException {
		return "schema";
	}

	@Override
	public ResultSet getSchemas() throws SQLException {
		if (getSchemas == null) {
			getSchemas = connection.prepareStatement("select " + "null as TABLE_SCHEM, " + "null as TABLE_CATALOG " + "limit 0;");
		}
		getSchemas.clearParameters();
		return getSchemas.executeQuery();
	}

	@Override
	public String getSearchStringEscape() throws SQLException {
		return "";
	}

	@Override
	public String getStringFunctions() throws SQLException {
		return "";
	}

	@Override
	public ResultSet getSuperTables(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
		if (getSuperTables == null) {
			getSuperTables = connection.prepareStatement("select " + "null as TABLE_CAT, " + "null as TABLE_SCHEM, " + "null as TABLE_NAME, "
					+ "null as SUPERTABLE_NAME limit 0;");
		}
		return getSuperTables.executeQuery();
	}

	@Override
	public ResultSet getSuperTypes(String catalog, String schemaPattern, String typeNamePattern) throws SQLException {
		if (getSuperTypes == null) {
			getSuperTypes = connection.prepareStatement("select " + "null as TYPE_CAT, " + "null as TYPE_SCHEM, " + "null as TYPE_NAME, "
					+ "null as SUPERTYPE_CAT, " + "null as SUPERTYPE_SCHEM, " + "null as SUPERTYPE_NAME limit 0;");
		}
		return getSuperTypes.executeQuery();
	}

	@Override
	public String getSystemFunctions() throws SQLException {
		return "";
	}

	@Override
	public ResultSet getTablePrivileges(String catalog, String schemaPattern, String tableNamePattern) throws SQLException {
		if (getTablePrivileges == null) {
			getTablePrivileges = connection.prepareStatement("select " + "null as TABLE_CAT, " + "null as TABLE_SCHEM, " + "null as TABLE_NAME, "
					+ "null as GRANTOR, " + "null as GRANTEE, " + "null as PRIVILEGE, " + "null as IS_GRANTABLE limit 0;");
		}
		return getTablePrivileges.executeQuery();
	}

	@Override
	public ResultSet getTableTypes() throws SQLException {
		checkOpen();
		if (getTableTypes == null) {
			getTableTypes = connection.prepareStatement("select 'TABLE' as TABLE_TYPE" + " union select 'VIEW' as TABLE_TYPE;");
		}
		getTableTypes.clearParameters();
		return getTableTypes.executeQuery();
	}

	@Override
	public ResultSet getTables(String catalog, String schemaPattern, String tableNamePattern, String[] types) throws SQLException {
		checkOpen();

		tableNamePattern = (tableNamePattern == null || "".equals(tableNamePattern)) ? "%" : tableNamePattern.toUpperCase();

		String sql = "select" + " null as TABLE_CAT," + " null as TABLE_SCHEM," + " name as TABLE_NAME," + " upper(type) as TABLE_TYPE,"
				+ " null as REMARKS," + " null as TYPE_CAT," + " null as TYPE_SCHEM," + " null as TYPE_NAME," + " null as SELF_REFERENCING_COL_NAME,"
				+ " null as REF_GENERATION" + " from (select name, type from sqlite_master union all"
				+ "       select name, type from sqlite_temp_master)" + " where TABLE_NAME like '" + escape(tableNamePattern) + "'";

		if (types != null) {
			sql += " and TABLE_TYPE in (";
			for (int i = 0; i < types.length; i++) {
				if (i > 0) {
					sql += ", ";
				}
				sql += "'" + types[i].toUpperCase() + "'";
			}
			sql += ")";
		}

		sql += ";";

		return connection.createStatement().executeQuery(sql);
	}

	@Override
	public String getTimeDateFunctions() throws SQLException {
		return "";
	}

	@Override
	public ResultSet getTypeInfo() throws SQLException {
		if (getTypeInfo == null) {
			getTypeInfo = connection.prepareStatement("select " + "tn as TYPE_NAME, " + "dt as DATA_TYPE, " + "0 as PRECISION, "
					+ "null as LITERAL_PREFIX, " + "null as LITERAL_SUFFIX, " + "null as CREATE_PARAMS, "
					+ typeNullable
					+ " as NULLABLE, "
					+ "1 as CASE_SENSITIVE, "
					+ typeSearchable
					+ " as SEARCHABLE, "
					+ "0 as UNSIGNED_ATTRIBUTE, "
					+ "0 as FIXED_PREC_SCALE, "
					+ "0 as AUTO_INCREMENT, "
					+ "null as LOCAL_TYPE_NAME, "
					+ "0 as MINIMUM_SCALE, "
					+ "0 as MAXIMUM_SCALE, "
					+ "0 as SQL_DATA_TYPE, "
					+ "0 as SQL_DATETIME_SUB, "
					+ "10 as NUM_PREC_RADIX from ("
					+ "    select 'BLOB' as tn, "
					+ Types.BLOB
					+ " as dt union"
					+ "    select 'NULL' as tn, "
					+ Types.NULL
					+ " as dt union"
					+ "    select 'REAL' as tn, "
					+ Types.REAL
					+ " as dt union"
					+ "    select 'TEXT' as tn, "
					+ Types.VARCHAR
					+ " as dt union"
					+ "    select 'INTEGER' as tn, " + Types.INTEGER + " as dt" + ") order by TYPE_NAME;");
		}

		getTypeInfo.clearParameters();
		return getTypeInfo.executeQuery();
	}

	@Override
	public ResultSet getUDTs(String catalog, String schemaPattern, String typeNamePattern, int[] types) throws SQLException {
		if (getUDTs == null) {
			getUDTs = connection.prepareStatement("select " + "null as TYPE_CAT, " + "null as TYPE_SCHEM, " + "null as TYPE_NAME, "
					+ "null as CLASS_NAME, " + "null as DATA_TYPE, " + "null as REMARKS, " + "null as BASE_TYPE " + "limit 0;");
		}

		getUDTs.clearParameters();
		return getUDTs.executeQuery();
	}

	@Override
	public String getURL() throws SQLException {
		return "";
	}

	@Override
	public String getUserName() throws SQLException {
		return "";
	}

	@Override
	public ResultSet getVersionColumns(String catalog, String schema, String table) throws SQLException {
		if (getVersionColumns == null) {
			getVersionColumns = connection.prepareStatement("select " + "null as SCOPE, " + "null as COLUMN_NAME, " + "null as DATA_TYPE, "
					+ "null as TYPE_NAME, " + "null as COLUMN_SIZE, " + "null as BUFFER_LENGTH, " + "null as DECIMAL_DIGITS, "
					+ "null as PSEUDO_COLUMN limit 0;");
		}
		return getVersionColumns.executeQuery();
	}

	@Override
	public boolean insertsAreDetected(int type) throws SQLException {
		return false;
	}

	@Override
	public boolean isCatalogAtStart() throws SQLException {
		return true;
	}

	@Override
	public boolean isReadOnly() throws SQLException {
		return connection.isReadOnly();
	}

	@Override
	public boolean locatorsUpdateCopy() throws SQLException {
		return false;
	}

	@Override
	public boolean nullPlusNonNullIsNull() throws SQLException {
		return true;
	}

	@Override
	public boolean nullsAreSortedAtEnd() throws SQLException {
		return !nullsAreSortedAtStart();
	}

	@Override
	public boolean nullsAreSortedAtStart() throws SQLException {
		return true;
	}

	@Override
	public boolean nullsAreSortedHigh() throws SQLException {
		return true;
	}

	@Override
	public boolean nullsAreSortedLow() throws SQLException {
		return !nullsAreSortedHigh();
	}

	@Override
	public boolean othersDeletesAreVisible(int type) throws SQLException {
		return false;
	}

	@Override
	public boolean othersInsertsAreVisible(int type) throws SQLException {
		return false;
	}

	@Override
	public boolean othersUpdatesAreVisible(int type) throws SQLException {
		return false;
	}

	@Override
	public boolean ownDeletesAreVisible(int type) throws SQLException {
		return false;
	}

	@Override
	public boolean ownInsertsAreVisible(int type) throws SQLException {
		return false;
	}

	@Override
	public boolean ownUpdatesAreVisible(int type) throws SQLException {
		return false;
	}

	@Override
	public boolean storesLowerCaseIdentifiers() throws SQLException {
		return true;
	}

	@Override
	public boolean storesLowerCaseQuotedIdentifiers() throws SQLException {
		return false;
	}

	@Override
	public boolean storesMixedCaseIdentifiers() throws SQLException {
		return true;
	}

	@Override
	public boolean storesMixedCaseQuotedIdentifiers() throws SQLException {
		return false;
	}

	@Override
	public boolean storesUpperCaseIdentifiers() throws SQLException {
		return false;
	}

	@Override
	public boolean storesUpperCaseQuotedIdentifiers() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsANSI92EntryLevelSQL() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsANSI92FullSQL() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsANSI92IntermediateSQL() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsAlterTableWithAddColumn() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsAlterTableWithDropColumn() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsBatchUpdates() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsCatalogsInDataManipulation() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCatalogsInIndexDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCatalogsInPrivilegeDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCatalogsInProcedureCalls() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCatalogsInTableDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsColumnAliasing() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsConvert() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsConvert(int fromType, int toType) throws SQLException {
		return false;
	}

	@Override
	public boolean supportsCoreSQLGrammar() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsCorrelatedSubqueries() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsDataDefinitionAndDataManipulationTransactions() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsDataManipulationTransactionsOnly() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsDifferentTableCorrelationNames() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsExpressionsInOrderBy() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsExtendedSQLGrammar() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsFullOuterJoins() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsGetGeneratedKeys() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsGroupBy() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsGroupByBeyondSelect() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsGroupByUnrelated() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsIntegrityEnhancementFacility() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsLikeEscapeClause() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsLimitedOuterJoins() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsMinimumSQLGrammar() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsMixedCaseIdentifiers() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsMixedCaseQuotedIdentifiers() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsMultipleOpenResults() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsMultipleResultSets() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsMultipleTransactions() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsNamedParameters() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsNonNullableColumns() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsOpenCursorsAcrossCommit() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOpenCursorsAcrossRollback() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOpenStatementsAcrossCommit() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOpenStatementsAcrossRollback() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOrderByUnrelated() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsOuterJoins() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsPositionedDelete() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsPositionedUpdate() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsResultSetConcurrency(int type, int concurrency) throws SQLException {
		return type == ResultSet.TYPE_FORWARD_ONLY && concurrency == ResultSet.CONCUR_READ_ONLY;
	}

	@Override
	public boolean supportsResultSetHoldability(int holdability) throws SQLException {
		return holdability == ResultSet.CLOSE_CURSORS_AT_COMMIT;
	}

	@Override
	public boolean supportsResultSetType(int type) throws SQLException {
		return type == ResultSet.TYPE_FORWARD_ONLY;
	}

	@Override
	public boolean supportsSavepoints() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSchemasInDataManipulation() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSchemasInIndexDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSchemasInPrivilegeDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSchemasInProcedureCalls() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSchemasInTableDefinitions() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSelectForUpdate() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsStatementPooling() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsStoredProcedures() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSubqueriesInComparisons() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsSubqueriesInExists() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsSubqueriesInIns() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsSubqueriesInQuantifieds() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsTableCorrelationNames() throws SQLException {
		return false;
	}

	@Override
	public boolean supportsTransactionIsolationLevel(int level) throws SQLException {
		return level == Connection.TRANSACTION_SERIALIZABLE;
	}

	@Override
	public boolean supportsTransactions() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsUnion() throws SQLException {
		return true;
	}

	@Override
	public boolean supportsUnionAll() throws SQLException {
		return true;
	}

	@Override
	public boolean updatesAreDetected(int type) throws SQLException {
		return false;
	}

	@Override
	public boolean usesLocalFilePerTable() throws SQLException {
		return false;
	}

	@Override
	public boolean usesLocalFiles() throws SQLException {
		return true;
	}

	@Override
	public boolean autoCommitFailureClosesAllResultSets() throws SQLException {
		return false;
	}

	@Override
	public ResultSet getClientInfoProperties() throws SQLException {
		return null;
	}

	@Override
	public ResultSet getFunctionColumns(String catalog, String schemaPattern, String functionNamePattern, String columnNamePattern) throws SQLException {
		return null;
	}

	@Override
	public ResultSet getFunctions(String catalog, String schemaPattern, String functionNamePattern) throws SQLException {
		return null;
	}

	@Override
	public RowIdLifetime getRowIdLifetime() throws SQLException {
		return null;
	}

	@Override
	public ResultSet getSchemas(String catalog, String schemaPattern) throws SQLException {
		return null;
	}

	@Override
	public boolean supportsStoredFunctionsUsingCallSyntax() throws SQLException {
		return false;
	}

	void checkOpen() throws SQLException {
		if (connection == null) {
			throw new SQLException("connection closed");
		}
	}

	private String escape(final String val) {
		int len = val.length();
		StringBuilder buf = new StringBuilder(len);
		for (int i = 0; i < len; i++) {
			if (val.charAt(i) == '\'') {
				buf.append('\'');
			}
			buf.append(val.charAt(i));
		}
		return buf.toString();
	}

	private static String quote(String tableName) {
		if (tableName == null) {
			return "null";
		} else {
			return String.format("'%s'", tableName);
		}
	}

	synchronized void close() throws SQLException {
		if (connection == null) {
			return;
		}

		try {
			if (getTableTypes != null) {
				getTableTypes.close();
			}
			if (getTypeInfo != null) {
				getTypeInfo.close();
			}
			if (getCatalogs != null) {
				getCatalogs.close();
			}
			if (getSchemas != null) {
				getSchemas.close();
			}
			if (getUDTs != null) {
				getUDTs.close();
			}
			if (getColumnsTblName != null) {
				getColumnsTblName.close();
			}
			if (getSuperTypes != null) {
				getSuperTypes.close();
			}
			if (getSuperTables != null) {
				getSuperTables.close();
			}
			if (getTablePrivileges != null) {
				getTablePrivileges.close();
			}
			if (getProcedures != null) {
				getProcedures.close();
			}
			if (getProcedureColumns != null) {
				getProcedureColumns.close();
			}
			if (getAttributes != null) {
				getAttributes.close();
			}
			if (getBestRowIdentifier != null) {
				getBestRowIdentifier.close();
			}
			if (getVersionColumns != null) {
				getVersionColumns.close();
			}
			if (getColumnPrivileges != null) {
				getColumnPrivileges.close();
			}

			getTableTypes = null;
			getTypeInfo = null;
			getCatalogs = null;
			getSchemas = null;
			getUDTs = null;
			getColumnsTblName = null;
			getSuperTypes = null;
			getSuperTables = null;
			getTablePrivileges = null;
			getProcedures = null;
			getProcedureColumns = null;
			getAttributes = null;
			getBestRowIdentifier = null;
			getVersionColumns = null;
			getColumnPrivileges = null;
		} finally {
			connection = null;
		}
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
