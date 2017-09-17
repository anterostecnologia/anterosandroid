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

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import br.com.anteros.android.core.util.Convert;
import br.com.anteros.android.core.util.DateFormatter;
import br.com.anteros.android.synchronism.communication.protocol.MobileAction;
import br.com.anteros.android.synchronism.communication.protocol.MobileRequest;
import br.com.anteros.android.synchronism.communication.protocol.MobileResponse;
import br.com.anteros.android.synchronism.exception.ExportListenerNotDefinedException;
import br.com.anteros.android.synchronism.exception.ImportListenerNotDefinedException;
import br.com.anteros.android.synchronism.exception.LoadTableDataToExportException;
import br.com.anteros.android.synchronism.listener.MobileSendDataListener;
import br.com.anteros.android.synchronism.listener.SynchronismExportListener;
import br.com.anteros.android.synchronism.listener.SynchronismImportListener;
import br.com.anteros.android.synchronism.listener.SynchronismTransactionListener;
import br.com.anteros.core.utils.Base64;
import br.com.anteros.core.utils.StringUtils;
import br.com.anteros.persistence.metadata.EntityCache;
import br.com.anteros.persistence.metadata.EntityCacheManager;
import br.com.anteros.persistence.metadata.descriptor.DescriptionColumn;
import br.com.anteros.persistence.metadata.descriptor.DescriptionField;
import br.com.anteros.persistence.metadata.descriptor.ParamDescription;
import br.com.anteros.persistence.metadata.descriptor.type.ConnectivityType;
import br.com.anteros.persistence.metadata.descriptor.type.FieldType;
import br.com.anteros.persistence.parameter.NamedParameter;
import br.com.anteros.persistence.session.SQLSession;
import br.com.anteros.persistence.sql.command.Select;

/**
 * @author Edson Martins
 */
public class SynchronismManager implements SynchronismTransactionListener, MobileSendDataListener {

    private static final String END_TRANSACTION = "EndTransaction";
    private static final String START_TRANSACTION = "StartTransaction";
    private SynchronismImportListener importListener;
    private SynchronismExportListener exportListener;
    private MobileSendData mobileSendData;
    private MobileProcessData mobileProcessData;
    private List<TableExport> exportTables;
    private SQLSession session;
    private SynchronismTransactionControl transactionControl;

    private String urlConnectionHost;
    private String clientId;
    private EntityCacheManager entityCacheManager;
    private int maxRecordBlockExport;
    private int maxRecordBlockTransaction;
    private String applicationName;
    private boolean executeImport = false;
    private Context context;
    private boolean awayImportExportData;

    public SynchronismManager(Context context, SQLSession session, SynchronismImportListener importListener,
                              SynchronismExportListener exportListener, String applicationName,
                              String urlConnectionHost, String clientId, int maxRecordBlockExport, int maxRecordBlockTransaction,
                              boolean awayImportExportData) {
        this.session = session;
        this.importListener = importListener;
        this.exportListener = exportListener;
        this.urlConnectionHost = urlConnectionHost;
        this.clientId = clientId;
        this.entityCacheManager = session.getEntityCacheManager();
        this.maxRecordBlockExport = maxRecordBlockExport;
        this.maxRecordBlockTransaction = maxRecordBlockTransaction;
        this.applicationName = applicationName;
        this.context = context;
        this.transactionControl = new SynchronismTransactionControl(context);
        this.awayImportExportData = awayImportExportData;
    }

    public void executeAction(String requestName, MobileAction[] mobileAction)
            throws ExportListenerNotDefinedException, ImportListenerNotDefinedException {

        isListenersConfigured();

		/*
         * Cria objetos para a Importação das Tabelas
		 */
        mobileSendData = new MobileSendData(this.getUrlConnectionHost(), MobileSendData.POST, transactionControl);
        mobileProcessData = new MobileProcessData(mobileSendData, getImportListener().getMobileProcessDataListener(),
                getImportListener().getSQLSession(), maxRecordBlockTransaction, MobileProcessDataType.FULL);

        for (MobileAction action : mobileAction) {
            MobileRequest mobileRequest = new MobileRequest(requestName);
            mobileRequest.setRequestMode(MobileRequest.ACTION_EXECUTE_IMMEDIATE);
            mobileRequest.setApplication(getImportListener().getApplicationName());
            mobileRequest.setClientId(this.getClientId());
            mobileRequest.setRequestId(requestName);
            mobileRequest.addAction(action);
            this.getSendData().addMobileRequest(mobileRequest);
        }

        this.getSendData().addListener(this.getProcessData());
        this.getSendData().addListener(getImportListener().getMobileSendDataListener());
        this.getSendData().execute();
    }

    public long getCurrentTranctionId() {
        return transactionControl.getStoredId();
    }

    public MobileResponse executeActionAndResponse(String applicationName, MobileAction... mobileAction) {
        return executeActionAndResponse(applicationName, MobileRequest.ACTION_EXECUTE_IMMEDIATE, mobileAction);
    }

    public MobileResponse executeActionAndResponse(String applicationName, String executionType, MobileAction... mobileAction) {
        MobileSendData msd = new MobileSendData(getUrlConnectionHost(), MobileSendData.POST, transactionControl);

        MobileRequest mobileRequest = new MobileRequest("");
        mobileRequest.setRequestMode(executionType);
        mobileRequest.setApplication(applicationName);
        mobileRequest.setClientId(this.getClientId());
        mobileRequest.setRequestId("");

        for (MobileAction action : mobileAction) {
            mobileRequest.addAction(action);
        }

        return msd.executeRequestImmediate(mobileRequest);
    }


    public MobileSendData executeActionAndProcessData(String applicationName,
                                                      MobileProcessDataType processDataType, MobileSendDataListener listener, MobileAction... mobileAction) {
        /*
		 * Cria objetos para a Importação das Tabelas
		 */
        MobileSendData mobileSendData = new MobileSendData(this.getUrlConnectionHost(), MobileSendData.POST,
                transactionControl);

        MobileProcessData mobileProcessData = new MobileProcessData(mobileSendData, getImportListener()
                .getMobileProcessDataListener(), getImportListener().getSQLSession(), maxRecordBlockTransaction,
                processDataType);

        mobileSendData.addListener(mobileProcessData);
        mobileSendData.addListener(listener);

        for (MobileAction action : mobileAction) {
            MobileRequest mobileRequest = new MobileRequest("" + action.getTableNameMobile());
            mobileRequest.setRequestMode(MobileRequest.ACTION_EXECUTE_IMMEDIATE);
            mobileRequest.setApplication(getImportListener().getApplicationName());
            mobileRequest.setClientId(this.getClientId());
            mobileRequest.addAction(action);
            mobileSendData.addMobileRequest(mobileRequest);
        }

        mobileSendData.execute();

        return mobileSendData;
    }

    public void importTableData() throws Exception {

        // Debug.startMethodTracing("importTableData");
        isListenersConfigured();

		/*
		 * Cria objetos para a Importação das Tabelas
		 */
        mobileSendData = new MobileSendData(this.getUrlConnectionHost(), MobileSendData.POST, transactionControl);
        mobileProcessData = new MobileProcessData(mobileSendData, getImportListener().getMobileProcessDataListener(),
                getImportListener().getSQLSession(), maxRecordBlockTransaction, MobileProcessDataType.FULL);

		/*
		 * Empacota as Actions em MobileRequest's para o Envio para o Servidor
		 * para a importação das Tabelas
		 */
        String[] tableListToImport = getImportListener().getTableListToImport();
        if (tableListToImport == null)
            tableListToImport = this.getTableListToImport();

        for (String tableName : tableListToImport) {
            MobileAction action = this.getMobileActionImportTable(tableName);
            if (action != null) {
                MobileRequest mobileRequest = new MobileRequest("" + tableName);
                mobileRequest.setRequestMode(MobileRequest.ACTION_EXECUTE_IMMEDIATE);
                mobileRequest.setApplication(getImportListener().getApplicationName());
                mobileRequest.setClientId(this.getClientId());
                mobileRequest.addAction(action);
                this.getSendData().addMobileRequest(mobileRequest);
            }
        }

		/*
		 * Processa os MobileRequest's e importa os dados para as tabelas
		 */
        this.getSendData().addListener(this.getProcessData());
        this.getSendData().addListener(getImportListener().getMobileSendDataListener());
        this.getSendData().execute();

        // Debug.stopMethodTracing();
    }

    public void exportAndImportTableData() throws Exception {
        exportTableData(true, null);
    }

    public void exportTableData() throws Exception {
        exportTableData(false, null);
    }

    public void exportAndImportTableData(List<TableExport> exportTables) throws Exception {
        exportTableData(true, exportTables);
    }

    public void exportTableData(List<TableExport> exportTables) throws Exception {
        exportTableData(false, exportTables);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public void exportTableData(boolean executeImport, List<TableExport> exportTables) throws Exception {

        this.executeImport = executeImport;
        isListenersConfigured();
        /*
		 * Cria objetos para Exportação das Tabelas
		 */
        mobileSendData = new MobileSendData(this.getUrlConnectionHost(), MobileSendData.POST, transactionControl);
        mobileSendData.setTransactionListener(this);
        mobileProcessData = new MobileProcessData(mobileSendData, getExportListener().getMobileProcessDataListener(),
                getExportListener().getSQLSession(), maxRecordBlockTransaction, MobileProcessDataType.FULL);

        this.getSendData().addListener(this.getProcessData());
        this.getSendData().addListener(getExportListener().getMobileSendDataListener());

        if (exportTables != null)
            this.exportTables = exportTables;
        else {
            this.exportTables = new ArrayList();

            MobileAction action = getExportListener().getMobileActionStartTransactionOnServer(-1);
            if (action != null) {
                TableExport tableExport = new TableExport(START_TRANSACTION, START_TRANSACTION);
                this.exportTables.add(tableExport);
            }

            String[] tableListToExport = getExportListener().getTableListToExport();
            if (tableListToExport == null)
                tableListToExport = this.getTableListToExport();

            if (tableListToExport != null) {
                for (String tableName : tableListToExport)
                    this.exportTables.add(new TableExport(tableName, tableName));
            }

            action = getExportListener().getMobileActionEndTransactionOnServer(-1);
            if (action != null) {
                TableExport tableExport = new TableExport(END_TRANSACTION, END_TRANSACTION);
                this.exportTables.add(tableExport);
            }
        }

        this.getSendData().execute();
    }

    public MobileRequest startTransaction() throws Exception {
        /*
		 * Cria uma Requisição para ser enviada ao Servidor para verificar se um
		 * número de transação foi bem sucedido.
		 */
        MobileAction mobileAction = getExportListener().getMobileActionVerifyTransactionOnServer(
                transactionControl.getCurrentTransactionId());
        MobileRequest mobileRequest = new MobileRequest("CheckTransaction", "Verificando Transação...",
                getExportListener().getApplicationName(), "", this.getClientId(),
                MobileRequest.ACTION_EXECUTE_IMMEDIATE);
        mobileRequest.addAction(mobileAction);

        return mobileRequest;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void fillExportTables(TableExport tableExport, long transactionId) throws Exception {
        try {
            if (tableExport != null) {
                if (tableExport.requests == null) {
                    tableExport.requests = new ArrayList();
                    switch (tableExport.tableName) {
                        /*
					     * Se for MobileAction para iniciar Transação no servidor
					     */
                        case START_TRANSACTION: {
                            MobileAction action = getExportListener().getMobileActionStartTransactionOnServer(
                                    transactionControl.getCurrentTransactionId());
                            if (action != null) {
                                MobileRequest mobileRequest = new MobileRequest("Iniciando Transação...");
                                mobileRequest.setRequestId(START_TRANSACTION);
                                mobileRequest.setRequestMode(MobileRequest.ACTION_QUEUE);
                                mobileRequest.setApplication(getExportListener().getApplicationName());
                                mobileRequest.setClientId(this.getClientId());
                                mobileRequest.addAction(action);
                                tableExport.requests.add(mobileRequest);
                            }
                            break;
                        }
                        /*
						 * Se for MobileAction para finalizar Transação no
						 * servidor
						 */
                        case END_TRANSACTION: {
                            MobileAction action = getExportListener().getMobileActionEndTransactionOnServer(
                                    transactionControl.getCurrentTransactionId());
                            if (action != null) {
                                MobileRequest mobileRequest = new MobileRequest("Finalizando Transação...");
                                mobileRequest.setRequestId(END_TRANSACTION);
                                mobileRequest.setRequestMode(MobileRequest.ACTION_QUEUE);
                                mobileRequest.setApplication(getExportListener().getApplicationName());
                                mobileRequest.setClientId(this.getClientId());
                                mobileRequest.addAction(action);
                                tableExport.requests.add(mobileRequest);
                            }
                            break;
                        }
                        /*
                         * Demais ações
						 */
                        default:
                            List<MobileAction> actionsList = this.getMobileActionExportTable(tableExport.tableMobileName,
                                    transactionId);
                            if (actionsList.size() > 0) {
                                for (MobileAction action : actionsList) {
                                    if (action.getParameters().size() > 0) {
                                        MobileRequest mobileRequest = new MobileRequest(tableExport.tableMobileName);
                                        mobileRequest.setRequestMode(MobileRequest.ACTION_QUEUE);
                                        mobileRequest.setApplication(getExportListener().getApplicationName());
                                        mobileRequest.setClientId(this.getClientId());
                                        mobileRequest.addAction(action);
                                        tableExport.requests.add(mobileRequest);
                                    }
                                }
                            }
                            break;
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new LoadTableDataToExportException(
                    "Erro carregando dados para Envio da Tabela "
                            + (tableExport != null ? tableExport.tableName + "(" + tableExport.tableMobileName + ") " : "")
                            + ex.getMessage());
        }
    }

    private MobileRequest nextRequest(long transactionId) throws Exception {
        for (Iterator<TableExport> iter = exportTables.iterator(); iter.hasNext(); ) {
            TableExport tableExport = iter.next();
            fillExportTables(tableExport, transactionId);
            if (tableExport.requests.size() > 0) {
                MobileRequest mobileRequest = (MobileRequest) tableExport.requests.get(0);
                tableExport.requests.remove(mobileRequest);
                return mobileRequest;
            }
            iter.remove();
        }
        return null;
    }

    private boolean hasNextRequest(long transactionId) throws Exception {
        for (Iterator<TableExport> iter = exportTables.iterator(); iter.hasNext(); ) {
            TableExport tableExport = iter.next();
            fillExportTables(tableExport, transactionId);
            if (tableExport.requests.size() > 0) {
                return true;
            }
            iter.remove();
        }
        return false;
    }

    public MobileRequest nextRequestInTransaction(long transactionId) throws Exception {
        MobileRequest mr = nextRequest(transactionId);
        if (mr != null) {
            if (!hasNextRequest(transactionId)) {
                mr.setRequestMode(MobileRequest.ACTION_EXECUTE_QUEUE);
            }
        }

        return mr;
    }

    public MobileRequest endTransaction() throws Exception {
        MobileAction mobileAction = getExportListener().getMobileActionVerifyTransactionOnServer(
                transactionControl.getCurrentTransactionId());
        MobileRequest mobileRequest = new MobileRequest("CheckTransaction", "Checando Transação", getExportListener()
                .getApplicationName(), "", this.getClientId(),
                MobileRequest.ACTION_EXECUTE_IMMEDIATE);
        mobileRequest.addAction(mobileAction);

        return mobileRequest;
    }

    public void transactionFinished(MobileResponse mobileResponse) throws Exception {
        if (mobileResponse.getStatus().startsWith(MobileResponse.OK))
            transactionControl.resetTransaction();
        exportListener.transactionFinished(mobileResponse);
    }

    public void clear() {
        mobileSendData = null;
        mobileProcessData = null;
        exportTables = null;
    }

    /**
     * Retorna a Ação necessária para realizar a importação dos dados remoto de
     * uma tabela.
     *
     * @param tableName Nome da tabela
     * @return Ação
     */
    private MobileAction getMobileActionImportTable(String tableName) throws Exception {
        EntityCache entityCache = entityCacheManager.getEntityCacheByTableName(tableName);
        if (entityCache != null) {
            MobileAction action = new MobileAction(entityCache.getMobileActionImport());
            if (entityCache.getImportParams().size() > 0) {
                String[] params = new String[entityCache.getImportParams().size()];
                for (ParamDescription paramDescription : entityCache.getImportParams().values()) {
                    if (importListener != null)
                        params[paramDescription.getParamOrder() - 1] = importListener.getParamValue(tableName,
                                paramDescription.getParamName(), paramDescription.getParamValue());
                    else
                        params[paramDescription.getParamOrder() - 1] = paramDescription.getParamValue();
                }
                action.addParameter(params);
            }
            action.setTableNameMobile(tableName);
            return action;
        } else {
            for (EntityCache tempEntityCache : entityCacheManager.getEntities().values()) {
                for (DescriptionField descriptionField : tempEntityCache.getDescriptionFields()) {
                    if (tableName.equals(descriptionField.getTableName())) {
                        MobileAction action = new MobileAction(descriptionField.getMobileActionImport());
                        if (descriptionField.getImportParams().size() > 0) {
                            String[] params = new String[descriptionField.getImportParams().size()];
                            for (ParamDescription paramDescription : descriptionField.getImportParams().values()) {
                                if (importListener != null)
                                    params[paramDescription.getParamOrder() - 1] = importListener.getParamValue(
                                            tableName, paramDescription.getParamName(),
                                            paramDescription.getParamValue());
                                else
                                    params[paramDescription.getParamOrder() - 1] = paramDescription.getParamValue();
                            }
                            action.addParameter(params);
                        }
                        action.setTableNameMobile(tableName);
                        return action;
                    }
                }
            }
            throw new Exception(
                    "Erro gerando MobileAction de importação. Não foi encontrada uma EntityCache para a tabela "
                            + tableName);
        }
    }

    /**
     * Retorna uma lista de Ações necessárias para realizar a exportação dos
     * dados divido em blocos conforme o atributo maxRecordBlockExport.
     *
     * @param tableName     Nome da tabela
     * @param transactionId ID. Sincronismo
     * @return Lista de Ações
     * @throws Exception
     */
    @SuppressWarnings("rawtypes")
    private List<MobileAction> getMobileActionExportTable(String tableName, long transactionId) throws Exception {

        EntityCache entityCache = entityCacheManager.getEntityCacheByTableName(tableName);
        List<MobileAction> result = new ArrayList<MobileAction>();
        MobileAction action = null;
        String[] parameters = null;

        int recordIndex = maxRecordBlockExport;
        if (entityCache != null) {

            if (entityCache.getMaxRecordBlockExport() > 0)
                maxRecordBlockExport = entityCache.getMaxRecordBlockExport();
            else
                maxRecordBlockExport = 0;
            recordIndex = maxRecordBlockExport;

            DescriptionColumn columnSynchronism = entityCache.getColumnIdSynchronism();
            if (columnSynchronism == null)
                throw new SynchronismException(
                        "Não foi encontrado nenhum Campo com a configuração IdSynchronism. Esta configuração é necessária para o sincronismo. Tabela "
                                + entityCache.getTableName());

            SQLSession session = exportListener.getSQLSession();

			/*
			 * Marca os registros com o ID do Sincronismo
			 */
            session.getTransaction().begin();
            session.update("UPDATE " + tableName + " SET " + columnSynchronism.getColumnName()
                            + "=:PTRANSACTION_ID WHERE " + columnSynchronism.getColumnName() + "= -1",
                    new NamedParameter[]{new NamedParameter("PTRANSACTION_ID", String.valueOf(transactionId))});
            session.getTransaction().commit();

            ResultSet rs = session.createQuery("SELECT * FROM " + tableName + " WHERE "
                    + columnSynchronism.getColumnName() + " = " + transactionId).executeQuery();

            if (rs.next()) {
                if (maxRecordBlockExport == 0) {
                    action = new MobileAction(entityCache.getMobileActionExport());
                    action.setTableNameMobile(tableName);
                    result.add(action);
                }
                int numberOfColumns = entityCache.exportColumnsCount();
                do {

                    if ((recordIndex >= maxRecordBlockExport) && (maxRecordBlockExport > 0)) {
                        recordIndex = 0;
                        action = new MobileAction(entityCache.getMobileActionExport());
                        action.setTableNameMobile(tableName);
                        result.add(action);
                    }
                    parameters = new String[numberOfColumns];
                    int columnIndex = 0;
                    DescriptionColumn column;

                    for (String columnName : entityCache.getExportColumns()) {
                        if (columnName.contains("${")) {
                            columnName = StringUtils.replace(columnName, "${", "");
                            columnName = StringUtils.replace(columnName, "}", "");
                            parameters[columnIndex] = getExportListener().getParamValue(tableName, columnName, "");
                        } else {
                            column = entityCache.getColumnDescription(columnName);
                            if (column == null)
                                throw new SynchronismException("A coluna " + columnName
                                        + " não foi encontrada na Classe " + entityCache.getEntityClass().getName());

                            Object value = null;
                            try {
                                value = rs.getObject(column.getColumnName());
                            } catch (Exception ex) {
                                if ((ex.getMessage() + "").contains("BLOB")) {
                                    value = rs.getBytes(column.getColumnName());
                                } else {
                                    throw ex;
                                }
                            }

                            if (value != null) {
                                if ((value.getClass() == Integer.class) || (value.getClass() == Double.class)
                                        || (value.getClass() == Float.class) || (value.getClass() == Long.class)
                                        || (value.getClass() == BigDecimal.class)
                                        || (value.getClass() == BigInteger.class)) {
                                    parameters[columnIndex] = String.valueOf(value);
                                } else if (value.getClass() == Date.class) {
                                    DateFormatter fmt = new DateFormatter(column.getTemporalType());
                                    parameters[columnIndex] = fmt.format((Date) value);
                                } else if (column.getField().getType() == byte[].class)
                                    parameters[columnIndex] = Base64.encodeBytes((byte[]) value);
                                else if (column.getField().getType() == Byte[].class)
                                    parameters[columnIndex] = Base64.encodeBytes(Convert
                                            .toPrimitiveByteArray((Byte[]) value));
                                else if (column.isExternalFile()) {
                                    File imagefile = new File(String.valueOf(value));
                                    FileInputStream fos = new FileInputStream(imagefile);
                                    byte[] bytes = new byte[fos.available()];

                                    fos.read(bytes);
                                    fos.close();

                                    parameters[columnIndex] = Base64.encodeBytes(bytes);
                                } else if ((value.getClass() == Boolean.class) && (column.isBoolean())) {
                                    parameters[columnIndex] = (String) column.getDescriptionField().getBooleanValue(
                                            (Boolean) value);
                                } else if ((column.getField().getType() == boolean.class) && (column.isBoolean())) {
                                    parameters[columnIndex] = (String) column.getDescriptionField().getBooleanValue(
                                            (Boolean) value);
                                } else
                                    parameters[columnIndex] = String.valueOf(value);
                            } else
                                parameters[columnIndex] = null;
                        }
                        columnIndex++;
                    }
                    action.addParameter(parameters);
                    recordIndex++;
                } while (rs.next());
            }
        } else {
            for (EntityCache ownerEntityCache : entityCacheManager.getEntities().values()) {
                for (DescriptionField descriptionField : ownerEntityCache.getDescriptionFields()) {
                    if (tableName.equals(descriptionField.getTableName())) {

                        if (ownerEntityCache.getMaxRecordBlockExport() > 0)
                            maxRecordBlockExport = ownerEntityCache.getMaxRecordBlockExport();
                        else
                            maxRecordBlockExport = 0;
                        recordIndex = maxRecordBlockExport;


                        DescriptionColumn columnSynchronism = ownerEntityCache.getColumnIdSynchronism();
                        if (columnSynchronism == null)
                            throw new SynchronismException(
                                    "Não foi encontrado nenhum Field com a configuração IdSynchronism. Esta configuração é necessária para o sincronismo. Tabela "
                                            + tableName
                                            + " (campo "
                                            + descriptionField.getName()
                                            + " da classe "
                                            + ownerEntityCache.getEntityClass().getName() + ")");

                        if (!(descriptionField.isAnyCollectionOrMap() || descriptionField.isJoinTable())) {
                            throw new SynchronismException(
                                    "O campo configurado para sincronização não é uma Coleção. Somente relacionamentos MUITOS para MUITOS, coleções de elementos e mapas de elementos podem ser sincronizados. Tabela "
                                            + tableName
                                            + " (campo "
                                            + descriptionField.getName()
                                            + " da classe "
                                            + ownerEntityCache.getEntityClass().getName() + ")");
                        }

                        String select = "";
                        if (descriptionField.isMapTable() || descriptionField.isCollectionEntity())
                            select = buildSelectFromElementCollection(descriptionField, transactionId + "");
                        else if (descriptionField.isJoinTable())
                            select = buildSelectFromJoinTable(descriptionField, transactionId + "");

                        SQLSession session = exportListener.getSQLSession();

                        ResultSet rs = session.createQuery(select).executeQuery();

                        if (rs.next()) {
                            if (maxRecordBlockExport == 0) {
                                action = new MobileAction(descriptionField.getMobileActionExport());
                                action.setTableNameMobile(tableName);
                                result.add(action);
                            }
                            int numberOfColumns = descriptionField.exportColumnsCount();

                            do {

                                parameters = new String[numberOfColumns];
                                int columnIndex = 0;
                                if ((recordIndex >= maxRecordBlockExport) && (maxRecordBlockExport > 0)) {
                                    recordIndex = 0;
                                    action = new MobileAction(descriptionField.getMobileActionExport());
                                    action.setTableNameMobile(tableName);
                                    result.add(action);
                                }
                                for (String columnName : descriptionField.getExportColumns()) {
                                    if (columnName.contains("${")) {
                                        columnName = StringUtils.replace(columnName, "${", "");
                                        columnName = StringUtils.replace(columnName, "}", "");
                                        parameters[columnIndex] = getExportListener().getParamValue(tableName,
                                                columnName, "");
                                    } else {

                                        Object value = null;
                                        try {
                                            value = rs.getObject(columnName);
                                        } catch (Exception ex) {
                                            if ((ex.getMessage() + "").contains("BLOB")) {
                                                value = rs.getBytes(columnName);
                                            } else {
                                                throw ex;
                                            }
                                        }
                                        DescriptionColumn column = descriptionField
                                                .getDescriptionColumnByName(columnName);

                                        if (value != null) {
                                            if ((value.getClass() == Integer.class)
                                                    || (value.getClass() == Double.class)
                                                    || (value.getClass() == Float.class)
                                                    || (value.getClass() == Long.class)
                                                    || (value.getClass() == BigDecimal.class)
                                                    || (value.getClass() == BigInteger.class)) {
                                                parameters[columnIndex] = String.valueOf(value);
                                            } else if (value.getClass() == Date.class) {
                                                DateFormatter fmt = new DateFormatter(column.getTemporalType());
                                                parameters[columnIndex] = fmt.format((Date) value);
                                            } else if (column.getField().getType() == byte[].class)
                                                parameters[columnIndex] = Base64.encodeBytes((byte[]) value);
                                            else if (column.getField().getType() == Byte[].class)
                                                parameters[columnIndex] = Base64.encodeBytes(Convert
                                                        .toPrimitiveByteArray((Byte[]) value));
                                            else if (column.isExternalFile()) {
                                                File file = new File(value + "");
                                                InputStream in = null;
                                                in = new BufferedInputStream(new FileInputStream(file));
                                                String data = StringUtils.convertStreamToString(in);
                                                parameters[columnIndex] = Base64.encodeBytes(data.getBytes());
                                            } else
                                                parameters[columnIndex] = String.valueOf(value);
                                        } else
                                            parameters[columnIndex] = null;
                                    }
                                    columnIndex++;
                                }
                                action.addParameter(parameters);
                                recordIndex++;
                            } while (rs.next());
                        }
                    }
                }
            }
        }
        return result;
    }

    private String buildSelectFromJoinTable(final DescriptionField descriptionFieldOwner, String transactionId)
            throws Exception {
        EntityCache fromEntityCache = session.getEntityCacheManager().getEntityCache(
                descriptionFieldOwner.getField().getDeclaringClass());

		/*
		 * Adiciona todas colunas da Entidade alvo
		 */
        Select select = new Select(session.getDialect());

		/*
		 * Gera os aliases para as tabelas
		 */
        fromEntityCache.generateAliasTableName();
        descriptionFieldOwner.generateAliasTableName();

        select.addTableName(fromEntityCache.getTableName() + " " + fromEntityCache.getAliasTableName());
        select.addTableName(descriptionFieldOwner.getTableName() + " " + descriptionFieldOwner.getAliasTableName());

        boolean appendOperator = false;

        for (DescriptionColumn column : descriptionFieldOwner.getDescriptionColumns())
            select.addColumn(descriptionFieldOwner.getAliasTableName() + "." + column.getColumnName());

		/*
		 * Adiciona condição transactionId
		 */
        select.addCondition(fromEntityCache.getAliasTableName() + "."
                + fromEntityCache.getColumnIdSynchronism().getColumnName(), "=", transactionId);
        select.and();

		/*
		 * Adiciona no WHERE colunas da entidade de Origem
		 */
        DescriptionColumn referencedColumn;
        for (DescriptionColumn column : fromEntityCache.getPrimaryKeyColumns()) {
            if (appendOperator)
                select.and();
            referencedColumn = descriptionFieldOwner.getDescriptionColumnByReferencedColumnName(column.getColumnName());
            select.addWhereToken(fromEntityCache.getAliasTableName() + "." + column.getColumnName() + " = "
                    + descriptionFieldOwner.getAliasTableName() + "." + referencedColumn.getColumnName());

            appendOperator = true;
        }

		/*
		 * Se possuir @Order, adiciona SELECT
		 */
        if (descriptionFieldOwner.hasOrderByClause()) {
            select.setOrderByClause(descriptionFieldOwner.getOrderByClause());
        }
        return select.toStatementString();
    }

    private String buildSelectFromElementCollection(final DescriptionField descriptionFieldOwner, String transactionId)
            throws Exception {
        /*
		 * Se for um ELEMENT_COLLETION
		 */

        if (descriptionFieldOwner.getFieldType() == FieldType.COLLECTION_TABLE) {
            Select select = new Select(session.getDialect());
            select.addTableName(descriptionFieldOwner.getTableName());
            boolean appendOperator = false;

            EntityCache mappedByEntityCache = descriptionFieldOwner.getTargetEntity();

			/*
			 * Adiciona condição transactionId
			 */
            select.addCondition(mappedByEntityCache.getColumnIdSynchronism().getColumnName(), "=", transactionId);
            select.and();

            for (DescriptionColumn descriptionColumn : mappedByEntityCache.getPrimaryKeyColumns()) {
                if (appendOperator)
                    select.and();
                String columnName = (descriptionColumn.getReferencedColumnName() == null
                        || "".equals(descriptionColumn.getReferencedColumnName()) ? descriptionColumn
                        .getColumnName() : descriptionColumn.getReferencedColumnName());
                select.addCondition(descriptionColumn.getColumnName(), "=", columnName);
                appendOperator = true;
            }
            if (descriptionFieldOwner.hasOrderByClause())
                select.setOrderByClause(descriptionFieldOwner.getOrderByClause());

            return select.toStatementString();

        } else if (descriptionFieldOwner.getFieldType() == FieldType.COLLECTION_MAP_TABLE) {

            Select select = new Select(session.getDialect());
            select.addTableName(descriptionFieldOwner.getTableName());

			/*
			 * Adiciona condição transactionId
			 */
            select.addCondition(descriptionFieldOwner.getEntityCache().getColumnIdSynchronism().getColumnName(), "=",
                    transactionId);
            select.and();

            boolean appendOperator = false;

            for (DescriptionColumn descriptionColumn : descriptionFieldOwner.getPrimaryKeys()) {
                if (descriptionColumn.isForeignKey()) {
                    if (appendOperator)
                        select.and();
                    select.addCondition(descriptionColumn.getColumnName(), "=", descriptionColumn
                            .getReferencedColumnName());
                    appendOperator = true;
                }
            }
            if (descriptionFieldOwner.hasOrderByClause())
                select.setOrderByClause(descriptionFieldOwner.getOrderByClause());

            return select.toStatementString();

        }
        return "";
    }

    public MobileSendData getSendData() {
        return mobileSendData;
    }

    public MobileProcessData getProcessData() {
        return mobileProcessData;
    }

    public void stop() {
        if (mobileSendData != null) {
            mobileSendData.stopSendData();
        }
    }

    public SynchronismImportListener getImportListener() {
        return importListener;
    }

    public void setImportListener(SynchronismImportListener importListener) {
        this.importListener = importListener;
    }

    public SynchronismExportListener getExportListener() {
        return exportListener;
    }

    public void setExportListener(SynchronismExportListener exportListener) {
        this.exportListener = exportListener;
    }

    protected void isListenersConfigured() throws ExportListenerNotDefinedException, ImportListenerNotDefinedException {
        if (this.getExportListener() == null)
            throw new ExportListenerNotDefinedException("Classe Listener de Exportação não definida !");

        if (this.getImportListener() == null)
            throw new ImportListenerNotDefinedException("Classe Listener de Importação não definida !");
    }

    public String getUrlConnectionHost() {
        return urlConnectionHost;
    }

    public void setUrlConnectionHost(String urlConnectionHost) {
        this.urlConnectionHost = urlConnectionHost;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public int getMaxRecordBlockExport() {
        return maxRecordBlockExport;
    }

    public void setMaxRecordBlockExport(int maxRecordBlockExport) {
        this.maxRecordBlockExport = maxRecordBlockExport;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public int getMaxRecordBlockTransaction() {
        return maxRecordBlockTransaction;
    }

    public void setMaxRecordBlockTransaction(int maxRecordBlockTransaction) {
        this.maxRecordBlockTransaction = maxRecordBlockTransaction;
    }

    public String[] getTableListToExport() throws SynchronismException {
        Map<Integer, String> result = new TreeMap<Integer, String>();

        boolean fastConnection = Connectivity.isConnectedFast(context);
        boolean wifiConnection = Connectivity.isConnectedViaWIFI(context);

        for (EntityCache entityCache : entityCacheManager.getEntities().values()) {
            if (entityCache.isExportTable()) {
                if (!awayImportExportData) {
                    if (entityCache.getExportConnectivityType() != ConnectivityType.ALL_CONNECTION) {
                        if ((entityCache.getExportConnectivityType() == ConnectivityType.FAST_MOBILE_CONNECTION)
                                && !(fastConnection || wifiConnection))
                            continue;
                        if ((entityCache.getExportConnectivityType() == ConnectivityType.WIFI_CONNECTION)
                                && !(wifiConnection))
                            continue;
                    }
                }
                if (result.containsKey(entityCache.getExportOrderToSendData()))
                    throw new SynchronismException(
                            "Existem mais de uma Entidade com o parâmetro ExportOrderToSendData igual a "
                                    + entityCache.getExportOrderToSendData());
                result.put(entityCache.getExportOrderToSendData(), entityCache.getTableName());
            }

            for (DescriptionField descriptionField : entityCache.getDescriptionFields()) {
                if (descriptionField.isMapTable() || descriptionField.isCollectionTable()
                        || descriptionField.isJoinTable()) {
                    if (descriptionField.isExportTable()) {
                        if (!awayImportExportData) {
                            if (descriptionField.getExportConnectivityType() != ConnectivityType.ALL_CONNECTION) {
                                if ((descriptionField.getExportConnectivityType() == ConnectivityType.FAST_MOBILE_CONNECTION)
                                        && !(fastConnection || wifiConnection))
                                    continue;
                                if ((descriptionField.getExportConnectivityType() == ConnectivityType.WIFI_CONNECTION)
                                        && !(wifiConnection))
                                    continue;
                            }
                        }
                        if (result.containsKey(descriptionField.getExportOrderToSendData()))
                            throw new SynchronismException(
                                    "Existem mais de uma Entidade com o parâmetro ExportOrderToSendData igual a "
                                            + descriptionField.getExportOrderToSendData());
                        result.put(descriptionField.getExportOrderToSendData(), descriptionField.getTableName());
                    }
                }
            }
        }
        return result.values().toArray(new String[]{});
    }

    public String[] getTableListToImport() {
        boolean fastConnection = Connectivity.isConnectedFast(context);
        boolean wifiConnection = Connectivity.isConnectedViaWIFI(context);

        List<String> result = new ArrayList<String>();
        for (EntityCache entityCache : entityCacheManager.getEntities().values()) {
            if (entityCache.isImportTable()) {
                if (!awayImportExportData) {
                    if (entityCache.getExportConnectivityType() != ConnectivityType.ALL_CONNECTION) {
                        if ((entityCache.getExportConnectivityType() == ConnectivityType.FAST_MOBILE_CONNECTION)
                                && !(fastConnection || wifiConnection))
                            continue;
                        if ((entityCache.getExportConnectivityType() == ConnectivityType.WIFI_CONNECTION)
                                && !(wifiConnection))
                            continue;
                    }
                }
                result.add(entityCache.getTableName());
            }

            for (DescriptionField descriptionField : entityCache.getDescriptionFields()) {
                if (descriptionField.isMapTable() || descriptionField.isCollectionTable()
                        || descriptionField.isJoinTable())
                    if (descriptionField.isImportTable()) {
                        if (!awayImportExportData) {
                            if (descriptionField.getExportConnectivityType() != ConnectivityType.ALL_CONNECTION) {
                                if ((descriptionField.getExportConnectivityType() == ConnectivityType.FAST_MOBILE_CONNECTION)
                                        && !(fastConnection || wifiConnection))
                                    continue;
                                if ((descriptionField.getExportConnectivityType() == ConnectivityType.WIFI_CONNECTION)
                                        && !(wifiConnection))
                                    continue;
                            }
                        }
                        result.add(descriptionField.getTableName());
                    }
            }
        }
        return result.toArray(new String[]{});
    }

    public SQLSession getSession() {
        return session;
    }

    public void setSession(SQLSession session) {
        this.session = session;
    }

    public void onStartSendData(int countRequests) {
    }

    public void onStartRequest(MobileRequest mobileRequest) {
    }

    public void onEndRequest(MobileRequest mobileRequest, MobileResponse mobileResponse) {
    }

    public void onReceiveResponse(MobileResponse mobileResponse) {
    }

    public void onFinishedSendData() throws Exception {
        if (executeImport)
            importTableData();
    }

    public void onInterruptedSendData(String errorMessage) {
    }

    public void onWaitServer() {
    }

    public void onEndServer() {
    }

    public void onStatusConnectionServer(String status) {
    }

    public void onDebugMessage(String message) {
    }

    public SynchronismTransactionControl getTransactionControl() {
        return transactionControl;
    }

    @Override
    public void onErrorSendRequest(String errorMessage) {
    }

}
