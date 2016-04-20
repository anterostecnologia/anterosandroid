package br.com.anteros.android.synchronism.listener;

import br.com.anteros.android.synchronism.communication.protocol.MobileRequest;
import br.com.anteros.android.synchronism.communication.protocol.MobileResponse;

/**
 *
 * @author Edson Martins
 */
public interface SynchronismTransactionListener {

    public MobileRequest startTransaction() throws Exception;

    public MobileRequest nextRequestInTransaction(long transactionId) throws Exception;

    public MobileRequest endTransaction() throws Exception;

    public void transactionFinished(MobileResponse mobileResponse) throws Exception;

}
