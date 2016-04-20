package br.com.anteros.android.synchronism.view;

import java.util.HashMap;
import java.util.Map;
import android.os.Handler;
import android.os.Message;
import br.com.anteros.android.synchronism.communication.protocol.MobileRequest;
import br.com.anteros.android.synchronism.communication.protocol.MobileResponse;
import br.com.anteros.android.synchronism.listener.MobileProcessDataListener;
import br.com.anteros.android.synchronism.listener.MobileSendDataListener;
import br.com.anteros.android.synchronism.listener.SynchronismExportListener;

public abstract class SyncronismExportHandler extends Handler implements SynchronismExportListener,
		MobileProcessDataListener, MobileSendDataListener {

	public static final int ON_EXPORTED_TABLE = 0;
	private static final int ON_END_REQUEST = 1;
	public static final int ON_ERROR_PROCESSING_RESPONSE = 2;
	public static final int ON_ERROR_TABLE_NAME = 3;
	public static final int ON_FINISHED_PROCESSING_RESPONSE = 4;
	public static final int ON_PROCESSING_RECORD = 5;
	public static final int ON_DEBUG_MESSAGE = 6;
	public static final int ON_END_SERVER = 7;
	public static final int ON_FINISHED_SEND_DATA = 8;
	public static final int ON_INTERRUPTED_SEND_DATA = 9;
	public static final int ON_START_REQUEST = 10;
	public static final int ON_WAIT_SERVER = 11;
	public static final int ON_STATUS_CONNECTION_SERVER = 12;
	private static final int ON_START_SEND_DATA = 13;
	private static final int ON_START_PROCESSING_RESPONSE = 14;
	private static final int ON_RECEIVE_RESPONSE = 15;
	private static final int ON_INTERRUPTED_PROCESSING_RESPONSE = 16;
	public static final String MOBILE_RESPONSE = "MOBILE_RESPONSE";
	public static final String MOBILE_REQUEST = "MOBILE_REQUEST";
	private static final int TRANSACTION_FINISHED = 17;

	@SuppressWarnings("unchecked")
	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case ON_EXPORTED_TABLE:
			String tableName = (String) msg.obj;
			handleExportedTable(tableName);
			break;
		case ON_ERROR_PROCESSING_RESPONSE:
			handleErrorProcessingResponse((String) msg.obj);
			break;
		case ON_ERROR_TABLE_NAME:
			handleErrorTableName((String) msg.obj);
			break;
		case ON_FINISHED_PROCESSING_RESPONSE:
			handleFinishedProcessingResponse((String) msg.obj);
			break;
		case ON_PROCESSING_RECORD:
			handleProcessingRecord((String) msg.obj, msg.arg2);
			break;
		case ON_START_PROCESSING_RESPONSE:
			handleStartProcessingResponse((String) msg.obj, msg.arg2);
			break;
		case ON_DEBUG_MESSAGE:
			handleDebugMessage((String) msg.obj);
			break;
		case ON_END_SERVER:
			handleEndServer();
			break;
		case ON_FINISHED_SEND_DATA:
			handleFinishedSendData();
			break;
		case ON_INTERRUPTED_SEND_DATA:
			handleInterruptedSendData((String) msg.obj);
			break;
		case ON_INTERRUPTED_PROCESSING_RESPONSE:
			handleInterruptedProcessingResponse((String) msg.obj);
			break;
		case ON_START_REQUEST:
			handleStartRequest((MobileRequest) msg.obj);
			break;
		case ON_WAIT_SERVER:
			handleWaitServer();
			break;
		case ON_STATUS_CONNECTION_SERVER:
			handleStatusConnectionServer((String) msg.obj);
			break;
		case ON_END_REQUEST:
			Map<String, Object> map = (Map<String, Object>) msg.obj;
			MobileRequest request = (MobileRequest) map.get(MOBILE_REQUEST);
			MobileResponse response = (MobileResponse) map.get(MOBILE_RESPONSE);
			handleEndRequest(request, response);
			break;

		default:
			break;
		}
	}

	public void onExportedTable(String tableNameMobile) {
		Message msg = Message.obtain();
		msg.what = ON_EXPORTED_TABLE;
		msg.obj = tableNameMobile;

		sendMessage(msg);
	}
	
	

	public void onErrorProcessingResponse(String errorMessage) {
		Message msg = Message.obtain();
		msg.what = ON_ERROR_PROCESSING_RESPONSE;
		msg.obj = errorMessage;

		sendMessage(msg);
	}

	public void onErrorTableName(String errorMessage) {
		Message msg = Message.obtain();
		msg.what = ON_ERROR_TABLE_NAME;
		msg.obj = errorMessage;

		sendMessage(msg);
	}

	public void onFinishedProcessingResponse(String tableNameMobile) {
		Message msg = Message.obtain();
		msg.what = ON_FINISHED_PROCESSING_RESPONSE;
		msg.obj = tableNameMobile;

		sendMessage(msg);
	}

	public void onInterruptedProcessingResponse(String tableNameMobile) {
		Message msg = Message.obtain();
		msg.what = ON_INTERRUPTED_PROCESSING_RESPONSE;
		msg.obj = tableNameMobile;

		sendMessage(msg);
	}

	public void onProcessingRecord(String tableNameMobile, int recno) {
		Message msg = Message.obtain();
		msg.what = ON_PROCESSING_RECORD;
		msg.arg2 = recno;
		msg.obj = tableNameMobile;

		sendMessage(msg);
	}

	public void onStartProcessingResponse(String tableNameMobile, int totalRecords) {
		Message msg = Message.obtain();
		msg.what = ON_START_PROCESSING_RESPONSE;
		msg.arg2 = totalRecords;
		msg.obj = tableNameMobile;

		sendMessage(msg);
	}

	public void onDebugMessage(String message) {
		Message msg = Message.obtain();
		msg.what = ON_DEBUG_MESSAGE;
		msg.obj = message;

		sendMessage(msg);
	}

	public void onEndRequest(MobileRequest mobileRequest, MobileResponse mobileResponse) {
		Message msg = Message.obtain();
		msg.what = ON_END_REQUEST;

		Map<String, Object> params = new HashMap<String, Object>();
		params.put(MOBILE_RESPONSE, mobileResponse);
		params.put(MOBILE_REQUEST, mobileResponse);

		msg.obj = params;

		sendMessage(msg);
	}

	public void onEndServer() {
		Message msg = Message.obtain();
		msg.what = ON_END_SERVER;

		sendMessage(msg);
	}

	public void onFinishedSendData() {
		Message msg = Message.obtain();
		msg.what = ON_FINISHED_SEND_DATA;

		sendMessage(msg);
	}

	public void onInterruptedSendData(String errorMessage) {
		Message msg = Message.obtain();
		msg.what = ON_INTERRUPTED_SEND_DATA;

		sendMessage(msg);
	}

	public void onReceiveResponse(MobileResponse mobileResponse) {
		Message msg = Message.obtain();
		msg.what = ON_RECEIVE_RESPONSE;
		msg.obj = mobileResponse;

		sendMessage(msg);
	}

	public void onStartRequest(MobileRequest mobileRequest) {
		Message msg = Message.obtain();
		msg.what = ON_START_REQUEST;
		msg.obj = mobileRequest;

		sendMessage(msg);
	}

	public void onStartSendData(int countRequests) {
		Message msg = Message.obtain();
		msg.what = ON_START_SEND_DATA;
		msg.what = countRequests;

		sendMessage(msg);
	}

	public void onStatusConnectionServer(String status) {
		Message msg = Message.obtain();
		msg.what = ON_STATUS_CONNECTION_SERVER;
		msg.obj = status;

		sendMessage(msg);
	}

	public void onWaitServer() {
		Message msg = Message.obtain();
		msg.what = ON_WAIT_SERVER;

		sendMessage(msg);
	}

	public MobileProcessDataListener getMobileProcessDataListener() {
		return this;
	}

	public MobileSendDataListener getMobileSendDataListener() {
		return this;
	}
	

	public abstract void handleExportedTable(String tableName);

	public abstract void handleErrorProcessingResponse(String errorMessage);

	public abstract void handleErrorTableName(String errorMessage);

	public abstract void handleFinishedProcessingResponse(String tableNameMobile);

	public abstract void handleInterruptedProcessingResponse(String tableNameMobile);

	public abstract void handleProcessingRecord(String tableNameMobile, int recno);

	public abstract void handleStartProcessingResponse(String tableNameMobile, int totalRecords);

	public abstract void handleDebugMessage(String message);

	public abstract void handleEndRequest(MobileRequest mobileRequest, MobileResponse mobileResponse);

	public abstract void handleEndServer();

	public abstract void handleFinishedSendData();

	public abstract void handleInterruptedSendData(String errorMessage);

	public abstract void handleReceiveResponse(MobileResponse mobileResponse);

	public abstract void handleStartRequest(MobileRequest mobileRequest);

	public abstract void handleStartSendData(int countRequests);

	public abstract void handleStatusConnectionServer(String status);

	public abstract void handleWaitServer();

}
