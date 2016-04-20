package br.com.anteros.android.synchronism.model;

import java.util.Date;


public class TableSynchronism {
	
	public String tableName;
	
	public String displayName;
	
	public Date dhSynchronismClient;
	
	public Date dhSynchronismServer;
	
	public TableSynchronism(String tableName, String displayName, Date dhSynchronismClient, Date dhSynchronismServer) {
		this.tableName = tableName;
		this.displayName = displayName;
		this.dhSynchronismClient = dhSynchronismClient;
		this.dhSynchronismServer = dhSynchronismServer;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public Date getDhSynchronismClient() {
		return dhSynchronismClient;
	}

	public void setDhSynchronismClient(Date dhSynchronismClient) {
		this.dhSynchronismClient = dhSynchronismClient;
	}

	public Date getDhSynchronismServer() {
		return dhSynchronismServer;
	}

	public void setDhSynchronismServer(Date dhSynchronismServer) {
		this.dhSynchronismServer = dhSynchronismServer;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	

}
