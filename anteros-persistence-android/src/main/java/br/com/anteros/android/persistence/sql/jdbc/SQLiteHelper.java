package br.com.anteros.android.persistence.sql.jdbc;


import br.com.anteros.core.log.Logger;
import br.com.anteros.core.log.LoggerProvider;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteHelper extends SQLiteOpenHelper {
	
	Logger log = LoggerProvider.getInstance().getLogger(SQLiteHelper.class);

	private String databaseName;
	private Context context;

	public SQLiteHelper(Context context, String databaseName) {
		super(context, databaseName, null, 1);
		this.context = context;
		this.databaseName = databaseName;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	public void dropAndCreateTables() throws Exception {
		close();
		context.deleteDatabase(databaseName);
		onCreate(this.getWritableDatabase());
	}

	protected void close(Cursor cursor) throws Exception {
		if (cursor != null) {
			cursor.close();
		}
	}
	
	@Override
	public void onOpen(SQLiteDatabase db) {
	    super.onOpen(db);
	    if (!db.isReadOnly()) {
	        /*
	         *  Habilita o uso de chaves estrangeiras
	         */
	        //db.execSQL("PRAGMA foreign_keys=ON;");
	    }
	}

}
