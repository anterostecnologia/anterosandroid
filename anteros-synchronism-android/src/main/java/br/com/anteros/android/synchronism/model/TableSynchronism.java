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
