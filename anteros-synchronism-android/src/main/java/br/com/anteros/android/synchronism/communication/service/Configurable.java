package br.com.anteros.android.synchronism.communication.service;

import br.com.anteros.persistence.session.SQLSession;
import android.app.Application;


public interface Configurable {

    String getUser();

    String getPassword();

    String getServerUrl();

    String getApplicationName();

    long getCurrentTransactionId() throws Exception;

    long getNextTransactionId() throws Exception;
    
    Application getApplication();
    
    SQLSession getSQLSession();
}
