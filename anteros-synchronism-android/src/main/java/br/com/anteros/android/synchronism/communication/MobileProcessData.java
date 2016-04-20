package br.com.anteros.android.synchronism.communication;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import br.com.anteros.android.persistence.exception.AndroidInsertException;
import br.com.anteros.android.persistence.session.AndroidInsertHelper;
import br.com.anteros.android.persistence.sql.jdbc.SQLiteConnection;
import br.com.anteros.android.synchronism.communication.protocol.MobileRequest;
import br.com.anteros.android.synchronism.communication.protocol.MobileResponse;
import br.com.anteros.android.synchronism.listener.MobileProcessDataListener;
import br.com.anteros.android.synchronism.listener.MobileSendDataListener;
import br.com.anteros.core.utils.StringUtils;
import br.com.anteros.persistence.session.SQLSession;

/**
 * 
 * @author Edson Martins
 */
public class MobileProcessData implements MobileSendDataListener {

	private MobileSendData sendData;
	private final MobileProcessDataListener listener;
	private SQLSession session;
	protected String sessionId = "";
	private int maxRecordBlockTransaction;
	private final MobileProcessDataType processDataType;

	public MobileProcessData(MobileSendData sendData, MobileProcessDataListener listener, SQLSession session,
			int maxRecordBlockTransaction, MobileProcessDataType processDataType) {
		this.listener = listener;
		this.sendData = sendData;
		this.session = session;
		this.processDataType = processDataType;
	}

	public void onReceiveResponse(MobileResponse mobileResponse) {
		if (!(mobileResponse.getStatus().startsWith(MobileResponse.OK))) {
			if (listener != null) {
				listener.onErrorProcessingResponse(mobileResponse.getStatus());
			}
			sendData.stopSendData();
		} else {
			if ((!StringUtils.isEmpty(mobileResponse.getTableName())) && (mobileResponse.getData().size() > 0)) {
				try {
					if (listener != null)
						listener.onStartProcessingResponse(mobileResponse.getTableName(), mobileResponse.getData()
								.size());
					if (session != null) {
						SQLiteDatabase database = ((SQLiteConnection) session.getConnection()).getHelper()
								.getWritableDatabase();

						try {
							if (processDataType == MobileProcessDataType.FULL)
								database.delete(mobileResponse.getTableName(), null, null);
						} catch (Exception ex) {
							ex.printStackTrace();

							if (listener != null)
								listener.onErrorProcessingResponse(ex.getMessage());
							sendData.stopSendData();
							return;
						}

						String deleteClause = "";
						List<String> pkNames = null;

						// se for INCREMENTAL então identifica as PKs da tabela
						// e monta a clausula WHERE
						if (processDataType == MobileProcessDataType.INCREMENTAL) {
							pkNames = new ArrayList<String>();
							Cursor tableInfo = database.rawQuery("PRAGMA table_info('"
									+ mobileResponse.getTableName().toUpperCase() + "')", null);
							tableInfo.moveToFirst();
							while (!tableInfo.isAfterLast()) {
								int pk = tableInfo.getInt(tableInfo.getColumnIndex("pk"));
								if (pk > 0) {
									String columnName = tableInfo.getString(tableInfo.getColumnIndex("name"));
									pkNames.add(columnName);
									if (!StringUtils.isEmpty(deleteClause))
										deleteClause += " AND ";
									deleteClause += columnName + " = ? ";
								}
								tableInfo.moveToNext();
							}
						}

						String[] values = null;
						int recordCount = 0;

						database.beginTransaction();
						AndroidInsertHelper ih = new AndroidInsertHelper(database, mobileResponse.getTableName());
						try {
							database.setLockingEnabled(false);
							String[] fields = mobileResponse.getFields();
							int[] indexes = new int[fields.length];
							for (int i = 0; i < fields.length; i++) {
								indexes[i] = ih.getColumnIndex(fields[i]);
							}
							for (int i = 0; i < mobileResponse.getData().size(); i++) {
								try {
									values = (String[]) mobileResponse.getData().get(i);

									// se for INCREMENTAL então identifica os
									// valores das PKs e executa o DELETE
									if (processDataType == MobileProcessDataType.INCREMENTAL) {
										String[] pkValues = new String[pkNames.size()];

										for (int j = 0; j < pkNames.size(); j++) {
											String pkName = pkNames.get(j);
											for (int k = 0; k < fields.length; k++) {
												if (pkName.equalsIgnoreCase(fields[k])) {
													pkValues[j] = values[k];
													break;
												}
											}
										}

										database.delete(mobileResponse.getTableName(), deleteClause, pkValues);
									}

									ih.prepareForInsert();
									for (int j = 0; j < fields.length; j++) {
										if (mobileResponse.isBase64(values[j])) {
											values[j] = new String(mobileResponse.decodeBase64(values[j]));
										}
										ih.bind(indexes[j], values[j]);
									}
									ih.execute();

									if (listener != null)
										listener.onProcessingRecord(mobileResponse.getTableName(), i + 1);
									values = null;

									if (maxRecordBlockTransaction > 0) {
										if (recordCount >= maxRecordBlockTransaction) {
											recordCount = 0;
											database.setTransactionSuccessful();
											database.endTransaction();
										}
									}
									recordCount++;
								} catch (AndroidInsertException ex1) {
									ex1.printStackTrace();
									String error = "Erro processando registro " + mobileResponse.getTableName() + " - "
											+ ex1.getMessage();
									if ((ex1.getMessage() + "").contains("error code 19: constraint failed"))
										error = "Há registros duplicadas nos dados recebidos para inserir na tabela "
												+ mobileResponse.getTableName();

									session.getTransaction().rollback();
									if (listener != null)
										listener.onErrorProcessingResponse(error);
									sendData.stopSendData();
									return;
								} catch (Exception ex2) {
									ex2.printStackTrace();
									session.getTransaction().rollback();
									if (listener != null)
										listener.onErrorProcessingResponse("Erro processando registro "
												+ mobileResponse.getTableName() + " - " + ex2.getMessage());
									sendData.stopSendData();
									return;
								}
							}
							database.setTransactionSuccessful();
							database.endTransaction();
						} finally {
							if (ih != null)
								ih.close();
							database.setLockingEnabled(true);
						}
					} else {
						if (listener != null) {
							listener.onErrorTableName("Tabela " + mobileResponse.getTableName() + " não encontrada !");
						}
						sendData.stopSendData();
						return;
					}
					if (listener != null)
						listener.onFinishedProcessingResponse(mobileResponse.getTableName());
				} catch (Exception ex) {
					ex.printStackTrace();
					if (listener != null)
						listener.onErrorProcessingResponse(ex.getMessage());
					sendData.stopSendData();
					return;
				}
			} else if (!StringUtils.isEmpty(mobileResponse.getTableName())) {
				try {
					if (session != null) {
						try {
							if (processDataType == MobileProcessDataType.FULL)
								((SQLiteConnection) session.getConnection()).getHelper().getWritableDatabase()
										.delete(mobileResponse.getTableName(), null, null);
						} catch (Exception ex) {
							ex.printStackTrace();
							if (listener != null)
								listener.onErrorProcessingResponse(ex.getMessage());
							sendData.stopSendData();
							return;
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					if (listener != null)
						listener.onErrorProcessingResponse(ex.getMessage());
					sendData.stopSendData();
					return;
				}

				if (listener != null)
					listener.onFinishedProcessingResponse(mobileResponse.getTableName());
			}
		}
	}

	public void onFinishedSendData() {
	}

	public void onStartRequest(MobileRequest mobileRequest) {
	}

	public void onEndRequest(MobileRequest mobileRequest, MobileResponse mobileResponse) {
	}

	public void onStartSendData(int countRequests) {
	}

	public void onInterruptedSendData(String errorMessage) {
	}

	public void onWaitServer() {
	}

	public void onEndServer() {
	}

	public void onDebugMessage(String message) {
	}

	public void onStatusConnectionServer(String status) {
	}

	@Override
	public void onErrorSendRequest(String errorMessage) {
	}
}
