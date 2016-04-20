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

import java.util.Vector;

import br.com.anteros.android.synchronism.communication.protocol.MobileRequest;
import br.com.anteros.android.synchronism.communication.protocol.MobileResponse;
import br.com.anteros.android.synchronism.listener.MobileSendDataListener;
import br.com.anteros.android.synchronism.listener.SynchronismTransactionListener;
import br.com.anteros.android.core.util.Convert;
import br.com.anteros.persistence.sql.parser.node.LiteralNode;

/**
 * 
 * @author Edson Martins
 */
public class MobileSendData extends Thread {

	public static final String POST = "POST";
	public static final String GET = "GET";
	private boolean execute = false;
	private boolean stopped = false;
	private Vector<MobileRequest> requests;
	private MobileRequest mobileRequest;
	private MobileResponse mobileResponse;
	private HttpConnectionClient connnection;
	private Vector<MobileSendDataListener> listeners;
	private String url;
	private String mode;
	private SynchronismTransactionListener transactionListener;
	private long transactionId;
	private SynchronismTransactionControl transactionControl;

	public MobileSendData(String url, String mode,
			SynchronismTransactionControl transactionControl) {
		this.url = url;
		this.mode = mode;
		requests = new Vector<MobileRequest>();
		listeners = new Vector<MobileSendDataListener>();
		this.transactionControl = transactionControl;
	}

	public MobileSendData(String url, String mode,
			MobileSendDataListener listener,
			SynchronismTransactionControl transactionControl) {
		this(url, mode, transactionControl);
		this.addListener(listener);
	}

	public MobileSendData(String url, String mode,
			MobileSendDataListener listener,
			SynchronismTransactionListener transactionListener,
			SynchronismTransactionControl transactionControl) {
		this(url, mode, transactionControl);
		this.addListener(listener);
		this.transactionListener = transactionListener;
	}

	public void addListener(MobileSendDataListener listener) {
		if (listener != null)
			this.getListeners().addElement(listener);
	}

	public void removeListener(MobileSendDataListener listener) {
		this.getListeners().removeElement(listener);
	}

	public void addMobileRequest(MobileRequest mobileRequest) {
		requests.addElement(mobileRequest);
	}

	public void setExecute(boolean execute) {
		this.execute = execute;
	}

	public void execute() {
		if (!this.execute) {
			this.stopped = false;
			this.execute = true;
			this.start();
		}
	}

	public void run() {
		if (getListeners().size() > 0) {
			for (MobileSendDataListener listener : this.getListeners())
				listener.onStartSendData(this.requests.size());
		}
		/*
		 * Inicia Transação caso o listener TransactionListener tenha sido
		 * informado
		 */
		if (transactionListener != null) {
			try {
				mobileRequest = transactionListener.startTransaction();
				if (mobileRequest != null) {
					executeRequest(mobileRequest);
					transactionId = 0;
					if (mobileResponse.getStatus().startsWith(MobileResponse.OK))
						transactionId = transactionControl
								.getNextTransactionId();
					else if (mobileResponse.getStatus().equals(
							MobileResponse.NOT))
						transactionId = transactionControl
								.getCurrentTransactionId();
					else {
						if (getListeners().size() > 0) {
							for (MobileSendDataListener listener : this
									.getListeners()) {
								listener.onErrorSendRequest(mobileResponse.getStatus());
								listener.onInterruptedSendData(mobileResponse.getStatus());
							}
							execute = false;
							stopped = true;
						}
					}
					mobileRequest = null;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

		/*
		 * Executa requisições no Servidor
		 */
		if (!stopped) {
			while (execute) {
				try {
					this.nextRequest();
				} catch (Exception ex) {
					ex.printStackTrace();
					if (getListeners().size() > 0) {
						for (MobileSendDataListener listener : this
								.getListeners()) {
							listener.onErrorSendRequest(ex.getMessage());
							listener.onInterruptedSendData("Erro obtendo próxima requisição - "
									+ ex.getMessage());

						}
					}
					stopped = true;
					mobileRequest = null;
				}
				if (mobileRequest == null)
					execute = false;
				else {
					executeRequest(mobileRequest);
					if (!stopped) {
						if (getListeners().size() > 0) {
							for (MobileSendDataListener listener : this
									.getListeners()) {
								listener.onReceiveResponse(mobileResponse);
								listener.onEndRequest(mobileRequest,
										mobileResponse);
							}
						}
					}
					mobileRequest = null;
					mobileResponse = null;
				}
			}
		}
		if (!stopped) {
			/*
			 * Verifica se a Transação foi bem sucedida. Caso o listener
			 * TransactionListener tenha sido informado obtém um MobileRequest
			 * envia para o servidor para verificar e repassa a resposta para o
			 * listener para processar o fim da transação.
			 */
			if (transactionListener != null) {
				try {
					mobileRequest = transactionListener.endTransaction();
					if (mobileRequest != null) {
						executeRequest(mobileRequest);
						transactionListener.transactionFinished(mobileResponse);
						mobileRequest = null;
						mobileResponse = null;
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					if (getListeners().size() > 0) {
						for (MobileSendDataListener listener : this
								.getListeners()) {
							listener.onErrorSendRequest(ex.getMessage());
							listener.onInterruptedSendData("Erro verificando se a transação foi concluída - "
									+ ex.getMessage());
						}
					}
				}
			}
			if (getListeners().size() > 0) {
				if ((mobileRequest == null) && (requests.size() == 0)) {
					for (MobileSendDataListener listener : this.getListeners()) {
						try {
							listener.onFinishedSendData();
						} catch (Exception e) {
							e.printStackTrace();
							if (getListeners().size() > 0) {
								for (MobileSendDataListener listener2 : this
										.getListeners()) {
									listener2.onErrorSendRequest(e.getMessage());
									listener2.onInterruptedSendData("Erro finalizando o envio - "
											+ e.getMessage());
								}
							}
						}

					}
				}
			}
		}

		Convert.clearAndNUllVector(requests);

		interrupt();
	}

	public void stopSendData() {
		execute = false;
		stopped = true;
		if (getListeners().size() > 0) {
			for (MobileSendDataListener listener : this.getListeners())
				listener.onInterruptedSendData("");
		}
	}

	private void executeRequest(MobileRequest mobileRequest) {
		if (validateMobileRequest(mobileRequest)) {
			if (connnection == null)
				connnection = new HttpConnectionClient(url, mode);
			else
				connnection.clear();

			if (getListeners().size() > 0) {
				for (MobileSendDataListener listener : this.getListeners())
					listener.onStartRequest(mobileRequest);
			}
			connnection.setSendData(this);
			mobileResponse = connnection.sendReceiveData(mobileRequest);
			mobileResponse.setRequestId(mobileRequest.getRequestId());
		}

	}

	public MobileResponse executeRequestImmediate(MobileRequest mobileRequest) {
		if (connnection == null)
			connnection = new HttpConnectionClient(url, mode);
		else
			connnection.clear();

		MobileResponse mr = connnection.sendReceiveData(mobileRequest);
		mr.setRequestId(mobileRequest.getRequestId());

		return mr;

	}

	private void nextRequest() throws Exception {
		mobileRequest = null;
		if (transactionListener != null)
			mobileRequest = transactionListener
					.nextRequestInTransaction(transactionId);
		else {
			if (requests.size() > 0) {
				mobileRequest = (MobileRequest) requests.firstElement();
				requests.removeElement(mobileRequest);
			}
		}

	}

	public void clear() {
		requests.removeAllElements();
		mobileRequest = null;
		mobileResponse = null;
	}

	private boolean validateMobileRequest(MobileRequest mobileRequest) {
		if (mobileRequest != null) {
			if (mobileRequest.getActions().size() == 0) {
				if (getListeners().size() > 0) {
					for (MobileSendDataListener listener : this.getListeners())
						listener.onInterruptedSendData("MobileRequest "
								+ mobileRequest.getRequestId()
								+ " não configurada corretamente - Não possuí Ações !");
				}
				stopSendData();
				return false;
			}
			/*
			 * for (int i = 0; i < mobileRequest.getActions().size(); i++) {
			 * MobileAction mobileAction = (MobileAction)
			 * mobileRequest.getActions().elementAt(i); if
			 * (mobileAction.getParameters().size() == 0) { if
			 * (getListeners().size() > 0) { for (MobileSendDataListener
			 * listener : this.getListeners()) {
			 * listener.onInterruptedSendData("MobileAction " +
			 * mobileAction.getName() + " Table Mobile Name=" +
			 * mobileAction.getTableNameMobile() +
			 * " não possuí parâmetros definidos !"); } } stopSendData(); return
			 * false; }
			 * 
			 * }
			 */
		}
		return true;
	}

	public SynchronismTransactionListener getTransactionListener() {
		return transactionListener;
	}

	public void setTransactionListener(
			SynchronismTransactionListener transactionListener) {
		this.transactionListener = transactionListener;
	}

	public boolean isStopped() {
		return stopped;
	}

	public void setStopped(boolean stoped) {
		this.stopped = stoped;
	}

	public Vector<MobileSendDataListener> getListeners() {
		return listeners;
	}
}
