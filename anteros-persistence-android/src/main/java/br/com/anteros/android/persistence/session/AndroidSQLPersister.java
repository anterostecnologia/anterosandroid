package br.com.anteros.android.persistence.session;

import java.util.Arrays;

import android.content.ContentValues;
import br.com.anteros.android.persistence.sql.jdbc.SQLiteConnection;
import br.com.anteros.persistence.metadata.EntityCache;
import br.com.anteros.persistence.session.SQLSession;
import br.com.anteros.persistence.session.exception.SQLSessionException;
import br.com.anteros.persistence.session.impl.SQLPersisterImpl;

public class AndroidSQLPersister extends SQLPersisterImpl {

	@Override
	public void save(SQLSession session, Class<?> clazz, String[] columns, String[] values) throws Exception {
		EntityCache cache = session.getEntityCacheManager().getEntityCache(clazz);
		ContentValues contentValues = new ContentValues();
		int i = 0;
		for (String column : columns) {
			contentValues.put(column, values[i]);
			i++;
		}
		long result = ((SQLiteConnection) session.getConnection()).getDatabase().insert(cache.getTableName(), null,
				contentValues);
		if (result == -1)
			throw new SQLSessionException("Ocorreu um erro salvando registro na tabela " + cache.getTableName()
					+ " colunas=" + Arrays.toString(columns) + " valores=" + Arrays.toString(values));
	}
}
