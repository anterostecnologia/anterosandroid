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

package br.com.anteros.android.synchronism.communication;

import android.content.Context;
import br.com.anteros.android.synchronism.listener.SynchronismExportListener;
import br.com.anteros.android.synchronism.listener.SynchronismImportListener;
import br.com.anteros.persistence.session.SQLSession;

/**
 * Classe para configuração do SynchronismManager
 * 
 */
public class SynchronismConfiguration {

	private SQLSession session;
	private String urlConnectionHost;
	private String clientId;
	private String userAgent;
	private SynchronismImportListener importListener;
	private SynchronismExportListener exportListener;
	private int maxRecordBlockExport;
	private String applicationName;
	private int maxRecordBlockTransaction = 2000;
	private Context context;
	private boolean alwaysImportExportData;

	public SynchronismConfiguration(Context context, SQLSession session) {
		this.session = session;
		this.context = context;
	}

	/**
	 * Finaliza configuração e retorna SynchronismManager
	 * 
	 * @return
	 * @throws Exception
	 */
	public SynchronismManager build()
			throws Exception {
		return new SynchronismManager(context, session, importListener, exportListener, applicationName,
				urlConnectionHost, clientId, maxRecordBlockExport, maxRecordBlockTransaction, alwaysImportExportData);
	}

	public String getUrlConnectionHost() {
		return urlConnectionHost;
	}

	public SynchronismConfiguration urlConnectionHost(String urlConnectionHost) {
		this.urlConnectionHost = urlConnectionHost;
		return this;
	}

	public String getClientId() {
		return clientId;
	}

	public SynchronismConfiguration clientId(String clientId) {
		this.clientId = clientId;
		return this;
	}

	public SynchronismImportListener getImportListener() {
		return importListener;
	}

	public SynchronismConfiguration importListener(SynchronismImportListener importListener) {
		this.importListener = importListener;
		return this;
	}

	public SynchronismExportListener getExportListener() {
		return exportListener;
	}

	public SynchronismConfiguration exportListener(SynchronismExportListener exportListener) {
		this.exportListener = exportListener;
		return this;
	}

	public int getMaxRecordBlockExport() {
		return maxRecordBlockExport;
	}

	public SynchronismConfiguration maxRecordBlockExport(int maxRecordBlockExport) {
		this.maxRecordBlockExport = maxRecordBlockExport;
		return this;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public SynchronismConfiguration applicationName(String applicationName) {
		this.applicationName = applicationName;
		return this;
	}

	public int getMaxRecordBlockTransaction() {
		return maxRecordBlockTransaction;
	}

	public SynchronismConfiguration maxRecordBlockTransaction(int maxRecordBlockTransaction) {
		this.maxRecordBlockTransaction = maxRecordBlockTransaction;
		return this;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public SynchronismConfiguration userAgent(String userAgent) {
		this.userAgent = userAgent;
		return this;
	}

	public boolean isAlwaysImportExportData() {
		return alwaysImportExportData;
	}

	public SynchronismConfiguration setAlwaysImportExportData(boolean awayImportExportData) {
		this.alwaysImportExportData = awayImportExportData;
		return this;
	}

}
