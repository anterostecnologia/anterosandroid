package br.com.anteros.android.persistence.sql.dialect;

import java.sql.Blob;
import java.sql.Connection;

import br.com.anteros.android.persistence.sql.jdbc.SQLiteBlob;
import br.com.anteros.persistence.sql.dialect.SQLiteDialect;

public class AndroidSQLiteDialect extends SQLiteDialect {

	@Override
	public Blob createTemporaryBlob(Connection connection, byte[] bytes) throws Exception {
		SQLiteBlob blob = new SQLiteBlob(bytes);
		return blob;
	}
	

}
