package br.com.anteros.android.synchronism.listener;


import java.util.Vector;

import br.com.anteros.android.synchronism.communication.protocol.MobileAction;
import br.com.anteros.persistence.session.SQLSession;

/**
 *
 * @author Edson Martins
 */
@SuppressWarnings("rawtypes")
public interface SynchronismListener {

    public String getApplicationName();

    public MobileProcessDataListener getMobileProcessDataListener();

    public MobileSendDataListener getMobileSendDataListener();

    public SQLSession getSQLSession();
    /*
     * Importação de Tabelas
     */

    
	public Vector getTableListToImport();

    public Vector getTableListToExport();

    public void onImportedTable(String tableNameMobile);

    /*
     * Exportação de Tabelas
     */
    public void onExportedTable(String tableNameMobile);

    public int getCurrentTransactionId();

    public int getNextTransactionId();

    public MobileAction getMobileActionCheckTransactionOnServer();
    
}
