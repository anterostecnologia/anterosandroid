package br.com.anteros.android.synchronism.communication.service;

import br.com.anteros.android.synchronism.communication.protocol.MobileResponse;

public interface MobileServiceListener {

    public void onSuccess(MobileResponse mobileResponse);

    public void onFailure(String message);

}
