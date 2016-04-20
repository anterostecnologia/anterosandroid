package br.com.anteros.android.synchronism.listener;

import br.com.anteros.android.synchronism.communication.protocol.MobileRequest;
import br.com.anteros.android.synchronism.communication.protocol.MobileResponse;


/**
 *
 * @author Edson Martins
 */
public interface MobileSendDataListener {

    public void onStartSendData(int countRequests);

    public void onStartRequest(MobileRequest mobileRequest);

    public void onEndRequest(MobileRequest mobileRequest, MobileResponse mobileResponse);

    public void onReceiveResponse(MobileResponse mobileResponse);

    public void onFinishedSendData() throws Exception;

    public void onInterruptedSendData(String errorMessage);

    public void onWaitServer();

    public void onEndServer();

    public void onStatusConnectionServer(String status);

    public void onDebugMessage(String message);
    
    public void onErrorSendRequest(String errorMessage);       
    
}
