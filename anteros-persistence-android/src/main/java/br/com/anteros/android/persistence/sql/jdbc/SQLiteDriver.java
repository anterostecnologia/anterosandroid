package br.com.anteros.android.persistence.sql.jdbc;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.util.Properties;

import android.content.Context;

public class SQLiteDriver implements Driver {
	
	public static String anterosPrefix = "jdbc:anteros:";
	private Context context; 
	
	public SQLiteDriver(Context context) {
		this.context = context;
	}
	

	@Override
	public boolean acceptsURL(String url) throws SQLException {
		 if (url.startsWith(anterosPrefix))
	            return true;

	        return false;
	}

	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		 return new SQLiteConnection(context,url, info);
	}

	@Override
	public int getMajorVersion() {
		return 0;
	}

	@Override
	public int getMinorVersion() {
		return 0;
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		return null;
	}

	@Override
	public boolean jdbcCompliant() {
		return false;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

}
