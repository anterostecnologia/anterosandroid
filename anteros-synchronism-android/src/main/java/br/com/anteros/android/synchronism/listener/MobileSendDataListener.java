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
