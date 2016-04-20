package br.com.anteros.android.synchronism.listener;


import br.com.anteros.android.synchronism.communication.protocol.MobileAction;
import br.com.anteros.android.synchronism.communication.protocol.MobileResponse;
import br.com.anteros.persistence.session.SQLSession;

/**
 *
 * @author Edson Martins
 */
public interface SynchronismExportListener {

    public String getApplicationName();

    public MobileProcessDataListener getMobileProcessDataListener();

    public MobileSendDataListener getMobileSendDataListener();

    public SQLSession getSQLSession();

    public String[] getTableListToExport();

    public void onExportedTable(String tableNameMobile);

    public String getParamValue(String tableName, String paramName, String paramValue);
    
    public MobileAction getMobileActionStartTransactionOnServer(long currentTransactionId) throws Exception;
    
    public MobileAction getMobileActionVerifyTransactionOnServer(long currentTransactionId) throws Exception;
    
    public MobileAction getMobileActionEndTransactionOnServer(long currentTransactionId) throws Exception;
    
    public void transactionFinished(MobileResponse mobileResponse) throws Exception;

}
