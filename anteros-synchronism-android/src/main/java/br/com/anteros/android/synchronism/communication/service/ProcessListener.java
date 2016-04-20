package br.com.anteros.android.synchronism.communication.service;

import br.com.anteros.android.synchronism.communication.protocol.MobileResponse;

public interface ProcessListener {

    public void onStart();

    public void onFinish(MobileResponse response);

    public void onInterrupt();
}
