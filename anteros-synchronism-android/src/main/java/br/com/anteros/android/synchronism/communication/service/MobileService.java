package br.com.anteros.android.synchronism.communication.service;

import br.com.anteros.android.synchronism.communication.SynchronismConfiguration;
import br.com.anteros.android.synchronism.communication.SynchronismManager;
import br.com.anteros.android.synchronism.communication.protocol.MobileAction;
import br.com.anteros.android.synchronism.communication.protocol.MobileRequest;
import br.com.anteros.android.synchronism.communication.protocol.MobileResponse;
import br.com.anteros.android.synchronism.listener.MobileProcessDataListener;
import br.com.anteros.android.synchronism.listener.MobileSendDataListener;
import br.com.anteros.android.synchronism.listener.SynchronismExportListener;
import br.com.anteros.android.synchronism.listener.SynchronismImportListener;
import br.com.anteros.android.core.util.DeviceUtils;

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
		this.currentListener = serviceListener;
		try {
			MobileResponse response = manager.executeActionAndResponse(configurable.getApplicationName(),
					new MobileAction[] { mobileAction });
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
