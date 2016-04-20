package br.com.anteros.android.synchronism.listener;

/**
 *
 * @author Edson Martins
 */
public interface MobileProcessDataListener {

    public void onStartProcessingResponse(String tableNameMobile, int totalRecords);

    public void onProcessingRecord(String tableNameMobile,int recno);

    public void onFinishedProcessingResponse(String tableNameMobile);

    public void onInterruptedProcessingResponse(String tableNameMobile);

    public void onErrorProcessingResponse(String errorMessage);

    public void onErrorTableName(String errorMessage);

}
