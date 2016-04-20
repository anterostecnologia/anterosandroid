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

package br.com.anteros.android.synchronism.view;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import br.com.anteros.android.synchronism.communication.protocol.MobileRequest;
import br.com.anteros.android.synchronism.communication.protocol.MobileResponse;
import br.com.anteros.android.synchronism.listener.MobileProcessDataListener;
import br.com.anteros.android.synchronism.listener.MobileSendDataListener;
import br.com.anteros.android.synchronism.listener.SynchronismImportListener;
import br.com.anteros.persistence.session.SQLSession;

public abstract class SyncronismImportHandler extends Handler implements SynchronismImportListener,
		MobileProcessDataListener, MobileSendDataListener {
	public static final int ON_IMPORTED_TABLE = 18;
	private static final int ON_END_REQUEST = 19;
	public static final int ON_ERROR_PROCESSING_RESPONSE = 20;
	public static final int ON_ERROR_TABLE_NAME = 21;
	public static final int ON_FINISHED_PROCESSING_RESPONSE = 22;
	public static final int ON_PROCESSING_RECORD = 23;
	public static final int ON_DEBUG_MESSAGE = 24;
	public static final int ON_END_SERVER = 25;
	public static final int ON_FINISHED_SEND_DATA = 26;
	public static final int ON_INTERRUPTED_SEND_DATA = 27;
	public static final int ON_START_REQUEST = 28;
	public static final int ON_WAIT_SERVER = 29;
	public static final int ON_STATUS_CONNECTION_SERVER = 30;
	private static final int ON_START_SEND_DATA = 31;
	private static final int ON_START_PROCESSING_RESPONSE = 32;
	private static final int ON_RECEIVE_RESPONSE = 33;
	private static final int ON_INTERRUPTED_PROCESSING_RESPONSE = 34;
	public static final String MOBILE_RESPONSE = "MOBILE_RESPONSE";
	public static final String MOBILE_REQUEST = "MOBILE_REQUEST";

	@SuppressWarnings("unchecked")
	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case ON_IMPORTED_TABLE:
			String tableName = (String) msg.obj;
			handleImportedTable(tableName);
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
		case ON_START_SEND_DATA:
			Bundle bundle = msg.getData();
			handleStartSendData(bundle.getInt("countRequests"));
			break;
		case ON_RECEIVE_RESPONSE:
			handleReceiveResponse((MobileResponse) msg.obj);
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
		super.handleMessage(msg);
	}

	public MobileProcessDataListener getMobileProcessDataListener() {
		return this;
	}

	public MobileSendDataListener getMobileSendDataListener() {
		return this;
	}

	public void onImportedTable(String tableNameMobile) {
		Message msg = Message.obtain();
		msg.what = ON_IMPORTED_TABLE;
		msg.obj = tableNameMobile;

		sendMessage(msg);
	}

	public void onErrorProcessingResponse(String errorMessage) {
		Message msg = obtainMessage();
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
		msg.what = ON_FINISHED_PROCESSING_RESPONSE;//262628051
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
		msg.obj = errorMessage;

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
		Bundle bundle = new Bundle();
		bundle.putInt("countRequests", countRequests);
		msg.setData(bundle);
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

	public abstract String getParamValue(String tableName, String paramName, String paramValue);

	public abstract String[] getTableListToImport();

	public abstract String getApplicationName();

	public abstract SQLSession getSQLSession();

	public abstract void handleImportedTable(String tableName);

	public abstract void handleErrorProcessingResponse(String errorMessage);

	public abstract void handleFinishedProcessingResponse(String tableNameMobile);

	public abstract void handleProcessingRecord(String tableNameMobile, int recno);

	public abstract void handleErrorTableName(String errorMessage);

	public abstract void handleStartProcessingResponse(String tableNameMobile, int totalRecords);

	public abstract void handleDebugMessage(String message);

	public abstract void handleStartSendData(int countRequests);

	public abstract void handleInterruptedSendData(String errorMessage);

	public abstract void handleFinishedSendData();

	public abstract void handleEndServer();

	public abstract void handleStatusConnectionServer(String status);

	public abstract void handleWaitServer();

	public abstract void handleStartRequest(MobileRequest mobileRequest);

	public abstract void handleInterruptedProcessingResponse(String tableNameMobile);

	public abstract void handleEndRequest(MobileRequest request, MobileResponse response);

	public abstract void handleReceiveResponse(MobileResponse mobileResponse);
}
