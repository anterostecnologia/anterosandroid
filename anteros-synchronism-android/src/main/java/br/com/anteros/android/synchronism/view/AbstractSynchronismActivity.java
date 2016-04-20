package br.com.anteros.android.synchronism.view;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import br.com.anteros.android.synchronism.R;
import br.com.anteros.android.synchronism.communication.SynchronismManager;
import br.com.anteros.android.synchronism.communication.protocol.MobileAction;
import br.com.anteros.android.synchronism.communication.protocol.MobileRequest;
import br.com.anteros.android.synchronism.communication.protocol.MobileResponse;
import br.com.anteros.android.synchronism.listener.MobileProcessDataListener;
import br.com.anteros.android.synchronism.listener.MobileSendDataListener;
import br.com.anteros.android.synchronism.listener.SynchronismExportListener;
import br.com.anteros.android.synchronism.listener.SynchronismImportListener;
import br.com.anteros.android.ui.controls.ErrorAlert;
import br.com.anteros.core.log.Logger;
import br.com.anteros.core.log.LoggerProvider;
import br.com.anteros.persistence.metadata.EntityCache;
import br.com.anteros.persistence.metadata.descriptor.DescriptionField;
import br.com.anteros.persistence.session.SQLSession;

public abstract class AbstractSynchronismActivity extends Activity implements
		SynchronismImportListener, SynchronismExportListener,
		MobileProcessDataListener, MobileSendDataListener {

	private static Logger LOG = LoggerProvider.getInstance().getLogger(AbstractSynchronismActivity.class.getName());

	private final int bufferProgress = 100;
	public static final int NONE = 0;
	public static final int IMPORT = 1;
	public static final int EXPORT = 2;
	private String tableDisplayLabel;
	protected AbstractSynchronismActivity context;
	protected TextView lbTables;
	protected ProgressBar pbTables;
	protected ProgressBar pbActions;
	protected ImageView btnInterrupt;
	protected ImageView btnSynchronize;
	protected ImageView btnClose;
	protected ImageView btnDetails;
	protected ImageView btnConfig;
	protected TextView lbActions;
	protected TextView lbStatus;
	protected SynchronismListenerViewUpdate listenerUpdateView;
	protected int synchronizeMode = NONE;

	protected AnimationDrawable anSyncronism;
	protected boolean syncronized = false;
	protected boolean finishAfterSynchronismOrError = false;
	protected boolean showMessageOnFinishSynchronism = true;
	protected boolean onlyExportData = false;
	protected boolean executeImport = true;
	protected boolean autoExecute = false;
	protected boolean synchronizing = false;

	private Handler showErrorHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			try {
				new ErrorAlert(AbstractSynchronismActivity.this, "Atenção", msg.obj + "", new ErrorAlert.ErrorListener() {
					public void onOkClick() {
						if (synchronizing)
							setSynchronizing(false);
						if (finishAfterSynchronismOrError) {
							setResult(SynchronismConstants.NOT_SYNCHRONIZED);
							finish();
						}
					}
				}).show();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		};
	};

	public int getSynchronizeMode() {
		return synchronizeMode;
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (autoExecute) {
			onSyncronizeButtonClick();
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.synchronism_view);
		this.context = this;

		onlyExportData = getIntent().getBooleanExtra(
				SynchronismConstants.ONLY_EXPORT_DATA, false);
		if (onlyExportData)
			executeImport = false;

		finishAfterSynchronismOrError = getIntent().getBooleanExtra(
				SynchronismConstants.FINISH_AFTER_SYNCHRONISM_OR_ERROR, false);
		showMessageOnFinishSynchronism = getIntent().getBooleanExtra(
				SynchronismConstants.SHOW_MESSAGE_ON_FINISH_SYNCHRONISM, true);
		autoExecute = getIntent().getBooleanExtra(
				SynchronismConstants.AUTO_EXECUTE_SYNCHRONISM, false);

		ImageView imgSyncronism = (ImageView) findViewById(R.id.syncronism_view_anSyncronism);
		anSyncronism = (AnimationDrawable) imgSyncronism.getDrawable();

		listenerUpdateView = new SynchronismListenerViewUpdate(this, this);

		synchronismManager().setExportListener(this);
		synchronismManager().setImportListener(this);

		lbTables = (TextView) findViewById(R.id.syncronism_view_lbTables);
		pbTables = (ProgressBar) findViewById(R.id.syncronism_view_pb_tables);

		lbActions = (TextView) findViewById(R.id.syncronism_view_lbActions);
		pbActions = (ProgressBar) findViewById(R.id.syncronism_view_pb_actions);

		lbStatus = (TextView) findViewById(R.id.syncronism_view_lb_status);

		btnInterrupt = (ImageView) findViewById(R.id.syncronism_view_img_interrupt);
		btnInterrupt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				synchronismManager().stop();
			}
		});

		btnSynchronize = (ImageView) findViewById(R.id.syncronism_view_img_synchronism);
		btnSynchronize.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				synchronizeMode = EXPORT;
				onSyncronizeButtonClick();
			}
		});

		btnClose = (ImageView) findViewById(R.id.syncronism_view_img_cancel);
		btnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btnDetails = (ImageView) findViewById(R.id.syncronism_view_img_details);
		btnDetails.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onDetailsButtonClick();
			}
		});

		btnConfig = (ImageView) findViewById(R.id.syncronism_view_img_config);
		btnConfig.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onConfigButtonClick();
			}
		});
	}

	protected abstract void onDetailsButtonClick();

	protected abstract void onConfigButtonClick();

	public abstract void onSynchronized();

	public abstract SynchronismManager synchronismManager();

	public void onSyncronizeButtonClick() {
		setSynchronizing(true);
	}

	public abstract String[] getTableListToExport();

	public abstract MobileAction getMobileActionStartTransactionOnServer(
			long currentTransactionId) throws Exception;

	public abstract MobileAction getMobileActionVerifyTransactionOnServer(
			long currentTransactionId) throws Exception;

	public abstract MobileAction getMobileActionEndTransactionOnServer(
			long currentTransactionId) throws Exception;

	public abstract void transactionFinished(MobileResponse mobileResponse)
			throws Exception;

	public abstract String getApplicationName();

	public abstract void onFinishedProcessingResponse(String tableName);

	public abstract SQLSession getSQLSession();

	public abstract String[] getTableListToImport();

	public abstract String getParamValue(String tableName, String paramName,
			String paramValue);

	public MobileProcessDataListener getMobileProcessDataListener() {
		return listenerUpdateView;
	}

	public MobileSendDataListener getMobileSendDataListener() {
		return listenerUpdateView;
	}

	@Override
	public void onErrorProcessingResponse(String errorMessage) {
		if (errorMessage.startsWith("ER;"))
			showError(errorMessage.substring(3));
		else
			showError(errorMessage);
		LOG.error(errorMessage);
	}

	@Override
	public void onProcessingRecord(String tableNameMobile, int recno) {
		lbTables.setText(tableDisplayLabel + " " + String.valueOf(recno) + "/" + String.valueOf(pbTables.getMax()));

		if (recno % bufferProgress == 0 || recno == pbTables.getMax()) {
			pbTables.setProgress(recno);
		}
	}

	public String getDisplayLabel(String tableNameMobile) throws Exception {
		EntityCache entityCache = getSQLSession()
				.getEntityCacheManager()
				.getEntityCacheByTableName(tableNameMobile);
		if (entityCache != null)
			return entityCache.getDisplayLabel();
		else {
			for (EntityCache ownerEntityCache : getSQLSession()
					.getEntityCacheManager().getEntities().values()) {
				for (DescriptionField descriptionField : ownerEntityCache
						.getDescriptionFields()) {
					if (tableNameMobile.equals(descriptionField.getTableName()))
						return descriptionField.getDisplayLabel();
				}
			}
		}
		return tableNameMobile;
	}

	@Override
	public void onErrorTableName(String errorMessage) {
		showError(errorMessage);
	}

	@Override
	public void onStartProcessingResponse(String tableNameMobile,
			int totalRecords) {
		anSyncronism.stop();
		lbTables.setText(tableDisplayLabel);
		pbTables.setMax(totalRecords);
		pbTables.setProgress(0);
	}

	@Override
	public void onDebugMessage(String message) {

	}

	@Override
	public void onStartSendData(int countRequests) {
		lbStatus.setTextColor(Color.BLACK);
		lbActions.setText("Processando requisições");
		pbActions.setMax(countRequests);
		pbActions.setProgress(0);
	}

	@Override
	public void onInterruptedSendData(String errorMessage) {
		lbStatus.setText("Processamento interrompido.");
		lbStatus.setTextColor(Color.RED);
	}

	@Override
	public void onFinishedSendData() {
		if ((showMessageOnFinishSynchronism && (synchronizeMode == IMPORT))
				|| (showMessageOnFinishSynchronism && onlyExportData)) {
			finishSynchronism();
		} else {
			if (executeImport) {
				try {
					executeImport = false;
					synchronizeMode = IMPORT;
					synchronismManager().importTableData();
				} catch (Exception e) {
					e.printStackTrace();
					showError(e.getMessage());
				}
			} else {
				finishSynchronism();
			}
		}
	}

	private void finishSynchronism() {
		pbTables.setProgress(0);
		lbStatus.setText("Sincronismo concluído");
		pbActions.setProgress(0);
		lbActions.setText("Ações");
		lbTables.setText("Tabelas");
		lbStatus.setTextColor(Color.BLACK);
		syncronized = true;
		setSynchronizing(false);
		onSynchronized();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			setResult(syncronized ? SynchronismConstants.SYNCHRONIZED
					: SynchronismConstants.NOT_SYNCHRONIZED);
			finish();
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onEndServer() {
		anSyncronism.stop();
	}

	@Override
	public void onStatusConnectionServer(String status) {
		lbStatus.setText(status);
	}

	@Override
	public void onWaitServer() {
		anSyncronism.start();
	}

	@Override
	public void onStartRequest(MobileRequest mobileRequest) {
		try {
			tableDisplayLabel = getDisplayLabel(mobileRequest.getName());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		lbTables.setText(tableDisplayLabel);
		pbTables.setProgress(0);
		lbActions.setText(tableDisplayLabel);
	}

	@Override
	public void onInterruptedProcessingResponse(String tableNameMobile) {
		showError("Processamento da resposta interrompido ! - Tabela "
				+ tableNameMobile);
	}

	@Override
	public void onEndRequest(MobileRequest mobileRequest,
			MobileResponse mobileResponse) {
		int pos = pbActions.getProgress();
		pbActions.setProgress(pos + 1);
	}

	@Override
	public void onReceiveResponse(MobileResponse mobileResponse) {
	}

	protected void showError(String errorMessage) {
		anSyncronism.stop();
		LOG.error(errorMessage);

		Message msg = new Message();
		msg.obj = errorMessage + "";

		showErrorHandler.sendMessage(msg);
	}

	public void onExportedTable(String tableNameMobile) {

	}

	public SynchronismListenerViewUpdate getListenerUpdateView() {
		return listenerUpdateView;
	}

	public boolean isExecuteImport() {
		return executeImport;
	}

	public void setExecuteImport(boolean executeImport) {
		this.executeImport = executeImport;
	}

	public void enabledActions() {
		btnClose.setEnabled(!synchronizing);
		btnInterrupt.setEnabled(synchronizing);
		btnDetails.setEnabled(!synchronizing);
		btnSynchronize.setEnabled(!synchronizing);
		btnConfig.setEnabled(!synchronizing);
		btnConfig.invalidate();
		btnClose.invalidate();
		btnDetails.invalidate();
		btnInterrupt.invalidate();
		btnSynchronize.invalidate();
	}

	public boolean isSynchronizing() {
		return synchronizing;
	}

	public void setSynchronizing(boolean synchronizing) {
		this.synchronizing = synchronizing;
		enabledActions();
	}
}
