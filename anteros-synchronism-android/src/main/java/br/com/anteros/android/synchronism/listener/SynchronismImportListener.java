package br.com.anteros.android.synchronism.listener;

import br.com.anteros.persistence.session.SQLSession;

/**
 * 
 * @author Edson Martins
 */
public interface SynchronismImportListener {

	public String getApplicationName();

	public MobileProcessDataListener getMobileProcessDataListener();

	public MobileSendDataListener getMobileSendDataListener();

	public SQLSession getSQLSession();

	public String[] getTableListToImport();

	public String getParamValue(String tableName, String paramName, String paramValue);

}
