package br.com.anteros.android.synchronism.view;

import br.com.anteros.android.synchronism.communication.protocol.MobileRequest;
import br.com.anteros.android.synchronism.communication.protocol.MobileResponse;
import br.com.anteros.android.synchronism.listener.MobileProcessDataListener;
import br.com.anteros.android.synchronism.listener.MobileSendDataListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SynchronismListenerViewUpdate extends Handler implements MobileProcessDataListener, MobileSendDataListener {

	private MobileProcessDataListener processDataListener;
	private MobileSendDataListener sendDataListener;
	private int countRequests;
	private MobileRequest mobileRequest;
	private MobileResponse mobileResponse;
	private String errorMessage;
	private String status;
	private String tableNameMobile;
	private int totalRecords;
	private int recno;
	private String debugMessage;

	public SynchronismListenerViewUpdate(MobileProcessDataListener processDataListener, MobileSendDataListener sendDataListener) {
		this.sendDataListener = sendDataListener;
		this.processDataListener = processDataListener;
	}

	@Override
	public void handleMessage(Message msg) {
		Bundle data = msg.getData();
		int command = data.getInt("command");
		switch (command) {
		case SynchronismConstants.ON_END_REQUEST:
			sendDataListener.onEndRequest(mobileRequest, mobileResponse);
			break;
		case SynchronismConstants.ON_ERROR_PROCESSING_RESPONSE:
			processDataListener.onErrorProcessingResponse((String) msg.obj);
			break;
		case SynchronismConstants.ON_ERROR_TABLE_NAME:
			processDataListener.onErrorTableName(errorMessage);
			break;
		case SynchronismConstants.ON_FINISHED_PROCESSING_RESPONSE:
			processDataListener.onFinishedProcessingResponse((String) msg.obj);
			break;
		case SynchronismConstants.ON_PROCESSING_RECORD:
			processDataListener.onProcessingRecord(tableNameMobile, recno);
			break;
		case SynchronismConstants.ON_DEBUG_MESSAGE:
			sendDataListener.onDebugMessage(debugMessage);
			break;
		case SynchronismConstants.ON_END_SERVER:
			sendDataListener.onEndServer();
			break;
		case SynchronismConstants.ON_FINISHED_SEND_DATA:
			try {
				sendDataListener.onFinishedSendData();
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case SynchronismConstants.ON_INTERRUPTED_SEND_DATA:
			sendDataListener.onInterruptedSendData(errorMessage);
			break;
		case SynchronismConstants.ON_START_REQUEST:
			sendDataListener.onStartRequest(mobileRequest);
			break;
		case SynchronismConstants.ON_WAIT_SERVER:
			sendDataListener.onWaitServer();
			break;
		case SynchronismConstants.ON_STATUS_CONNECTION_SERVER:
			sendDataListener.onStatusConnectionServer(status);
			break;
		case SynchronismConstants.ON_START_SEND_DATA:
			sendDataListener.onStartSendData(countRequests);
			break;
		case SynchronismConstants.ON_START_PROCESSING_RESPONSE:
			processDataListener.onStartProcessingResponse(tableNameMobile, totalRecords);
			break;
		case SynchronismConstants.ON_RECEIVE_RESPONSE:
			sendDataListener.onReceiveResponse(mobileResponse);
			break;
		case SynchronismConstants.ON_INTERRUPTED_PROCESSING_RESPONSE:
			processDataListener.onInterruptedProcessingResponse(tableNameMobile);
			break;
		case SynchronismConstants.ON_ERROR_SEND_REQUEST:
			sendDataListener.onErrorSendRequest(errorMessage);
			break;	
		default:
			break;
		}
	}

	@Override
	public void onStartSendData(int countRequests) {
		this.countRequests = countRequests;
		Message message = this.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putInt("command", SynchronismConstants.ON_START_SEND_DATA);
		message.setData(bundle);
		this.sendMessage(message);
	}

	@Override
	public void onStartRequest(MobileRequest mobileRequest) {
		this.mobileRequest = mobileRequest;
		Message message = this.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putInt("command", SynchronismConstants.ON_START_REQUEST);
		message.setData(bundle);
		this.sendMessage(message);
	}

	@Override
	public void onEndRequest(MobileRequest mobileRequest, MobileResponse mobileResponse) {
		this.mobileRequest = mobileRequest;
		this.mobileResponse = mobileResponse;
		Message message = this.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putInt("command", SynchronismConstants.ON_END_REQUEST);
		message.setData(bundle);
		this.sendMessage(message);
	}

	@Override
	public void onReceiveResponse(MobileResponse mobileResponse) {
		this.mobileResponse = mobileResponse;
		Message message = this.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putInt("command", SynchronismConstants.ON_RECEIVE_RESPONSE);
		message.setData(bundle);
		this.sendMessage(message);
	}

	@Override
	public void onFinishedSendData() {
		Message message = this.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putInt("command", SynchronismConstants.ON_FINISHED_SEND_DATA);
		message.setData(bundle);
		this.sendMessage(message);
	}

	@Override
	public void onInterruptedSendData(String errorMessage) {
		this.errorMessage = errorMessage;
		Message message = this.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putInt("command", SynchronismConstants.ON_INTERRUPTED_SEND_DATA);
		message.setData(bundle);
		this.sendMessage(message);
	}

	@Override
	public void onWaitServer() {
		Message message = this.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putInt("command", SynchronismConstants.ON_WAIT_SERVER);
		message.setData(bundle);
		this.sendMessage(message);
	}

	@Override
	public void onEndServer() {
		Message message = this.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putInt("command", SynchronismConstants.ON_END_SERVER);
		message.setData(bundle);
		this.sendMessage(message);
	}

	@Override
	public void onStatusConnectionServer(String status) {
		this.status = status;
		Message message = this.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putInt("command", SynchronismConstants.ON_STATUS_CONNECTION_SERVER);
		message.setData(bundle);
		this.sendMessage(message);
	}

	@Override
	public void onDebugMessage(String message) {
		debugMessage = message;
		Message msg = this.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putInt("command", SynchronismConstants.ON_DEBUG_MESSAGE);
		msg.setData(bundle);
		this.sendMessage(msg);
	}

	@Override
	public void onStartProcessingResponse(String tableNameMobile, int totalRecords) {
		this.tableNameMobile = tableNameMobile;
		this.totalRecords = totalRecords;
		Message message = this.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putInt("command", SynchronismConstants.ON_START_PROCESSING_RESPONSE);
		message.setData(bundle);
		this.sendMessage(message);
	}

	@Override
	public void onProcessingRecord(String tableNameMobile, int recno) {
		this.tableNameMobile = tableNameMobile;
		this.recno = recno;

		Message message = this.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putInt("command", SynchronismConstants.ON_PROCESSING_RECORD);
		message.setData(bundle);
		this.sendMessage(message);
	}

	@Override
	public void onFinishedProcessingResponse(String tableNameMobile) {
		this.tableNameMobile = tableNameMobile;
		Message message = this.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putInt("command", SynchronismConstants.ON_FINISHED_PROCESSING_RESPONSE);
		message.obj = tableNameMobile;
		message.setData(bundle);
		this.sendMessage(message);
	}

	@Override
	public void onInterruptedProcessingResponse(String tableNameMobile) {
		this.tableNameMobile = tableNameMobile;
		Message message = this.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putInt("command", SynchronismConstants.ON_INTERRUPTED_PROCESSING_RESPONSE);
		message.setData(bundle);
		this.sendMessage(message);
	}

	@Override
	public void onErrorProcessingResponse(String errorMessage) {
		Message message = this.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putInt("command", SynchronismConstants.ON_ERROR_PROCESSING_RESPONSE);
		message.setData(bundle);
		message.obj = errorMessage;
		this.sendMessage(message);
	}

	@Override
	public void onErrorTableName(String errorMessage) {
		this.errorMessage = errorMessage;
		Message message = this.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putInt("command", SynchronismConstants.ON_ERROR_TABLE_NAME);
		message.setData(bundle);
		this.sendMessage(message);
	}

	@Override
	public void onErrorSendRequest(String errorMessage) {
		this.errorMessage = errorMessage;
		Message message = this.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putInt("command", SynchronismConstants.ON_ERROR_SEND_REQUEST);
		message.setData(bundle);
		this.sendMessage(message);
	}
}
