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
