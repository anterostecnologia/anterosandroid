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

package br.com.anteros.android.synchronism.communication.service;

import java.util.ArrayList;
import java.util.List;

import br.com.anteros.android.core.util.DeviceUtils;
import br.com.anteros.android.synchronism.communication.SynchronismConfiguration;
import br.com.anteros.android.synchronism.communication.SynchronismManager;
import br.com.anteros.android.synchronism.communication.protocol.MobileAction;
import br.com.anteros.android.synchronism.communication.protocol.MobileRequest;
import br.com.anteros.android.synchronism.communication.protocol.MobileResponse;
import br.com.anteros.android.synchronism.listener.MobileProcessDataListener;
import br.com.anteros.android.synchronism.listener.MobileSendDataListener;
import br.com.anteros.android.synchronism.listener.SynchronismExportListener;
import br.com.anteros.android.synchronism.listener.SynchronismImportListener;
import br.com.anteros.core.utils.ArrayUtils;

public abstract class MobileService implements SynchronismExportListener,
		SynchronismImportListener, MobileProcessDataListener,
		MobileSendDataListener {

	private Configurable configurable;
	private MobileServiceListener currentListener;
	private SynchronismManager manager;
	private MobileResponse response;
	private ProcessListener window;

	public MobileService(Configurable configurable) throws Exception {
		this.configurable = configurable;
		this.manager = new SynchronismConfiguration(
				configurable.getApplication(), configurable.getSQLSession())
				.urlConnectionHost(configurable.getServerUrl())
				.applicationName(configurable.getApplicationName())
				.exportListener(this)
				.importListener(this)
				.clientId(
						DeviceUtils.getUniqueID(configurable.getApplication()))
				.build();
	}

	public MobileService(SynchronismManager manager) {
		this.manager = manager;
	}

	public void setWindowListener(ProcessListener listener) {
		this.window = listener;
	}

	public String getApplicationName() {
		return configurable.getApplicationName();
	}

	public MobileProcessDataListener getMobileProcessDataListener() {
		return this;
	}

	public MobileSendDataListener getMobileSendDataListener() {
		return this;
	}

	public String getPassword() {
		return configurable.getPassword();
	}

	public String getUrlHost() {
		return configurable.getServerUrl();
	}

	public String getUser() {
		return configurable.getUser();
	}

	public String[] getTableListToExport() {
		return null;
	}

	public void onExportedTable(String arg0) {
	}

	public long getCurrentTransactionId() throws Exception {
		return configurable.getCurrentTransactionId();
	}

	public long getNextTransactionId() throws Exception {
		return configurable.getNextTransactionId();
	}

	public void transactionFinished(MobileResponse response) throws Exception {
	}

	public void onStartProcessingResponse(String string, int arg1) {
	}

	public void onProcessingRecord(String arg0, int arg1) {
	}

	public void onFinishedProcessingResponse(String arg0) {
	}

	public void onInterruptedProcessingResponse(String message) {
		this.currentListener.onFailure(message);
	}

	public void onErrorProcessingResponse(String message) {
		this.currentListener.onFailure(message);
	}

	public void onErrorTableName(String message) {
	}

	public void onStartSendData(int arg0) {
		if (this.window != null) {
			this.window.onStart();
		}
	}

	public void onStartRequest(MobileRequest request) {
	}

	public void onEndRequest(MobileRequest request, MobileResponse response) {
	}

	public void onReceiveResponse(MobileResponse response) {
		this.currentListener.onSuccess(response);
		this.response = response;
	}

	public void onFinishedSendData() {
		if (this.window != null) {
			this.window.onFinish(response);
		}
	}

	public void onInterruptedSendData(String meessage) {
		if (this.window != null) {
			window.onInterrupt();
		}
	}

	public void onWaitServer() {
	}

	public void onEndServer() {
	}

	public void onStatusConnectionServer(String arg0) {
	}

	public void onDebugMessage(String arg0) {
	}

	public String[] getTableListToImport() {
		return null;
	}

	public void onImportedTable(String arg0) {
	}

	public synchronized void executeAction(MobileAction mobileAction,
                                           MobileServiceListener serviceListener) {
        executeActions(new MobileAction[]{mobileAction}, 0, serviceListener);
    }

    public synchronized void executeActions(MobileAction[] mobileActions,
                                            MobileServiceListener serviceListener) {
        executeActions(mobileActions, 0, serviceListener);
    }

    public synchronized void executeActions(MobileAction[] mobileActions, int limit,
                                            MobileServiceListener serviceListener) {
        int count;
        List<MobileAction> actionsToSend = new ArrayList<>();
        List<MobileAction> actionsTemp = ArrayUtils.asList(mobileActions);
        this.currentListener = serviceListener;
        try {
            MobileResponse response = null;
            while (!actionsTemp.isEmpty()) {
                count = 0;
                while ((count < limit || limit == 0) && (!actionsTemp.isEmpty())) {
                    actionsToSend.add(actionsTemp.get(0));
                    actionsTemp.remove(0);
                    count++;
                }

                response = manager.executeActionAndResponse(configurable.getApplicationName(),
                        (actionsTemp.isEmpty() ? MobileRequest.ACTION_EXECUTE_QUEUE : MobileRequest.ACTION_QUEUE),
                        actionsToSend.toArray(new MobileAction[]{}));
            }
            if (currentListener != null) {
                currentListener.onSuccess(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (currentListener != null)
                this.currentListener.onFailure(e.getMessage());
        }
    }

	public void interrupt() {
		manager.stop();
	}
}
