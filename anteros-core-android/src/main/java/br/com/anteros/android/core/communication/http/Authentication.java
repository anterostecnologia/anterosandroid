package br.com.anteros.android.core.communication.http;

import android.util.Base64;

/**
 * @author Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Data: 06/07/16.
 */

public class Authentication {

    private String credentials;


    public Authentication(String user, String password) {
        if (user == null)
            throw new RuntimeException("O usuário informado não pode ser nulo");
        if (password == null)
            throw new RuntimeException("A senha informada não pode ser nula");

        credentials = user + ":" + password;
    }

    public String getBasicAuthenticatorCredentials() {
        return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.DEFAULT).replace("\n", "");
    }
}
