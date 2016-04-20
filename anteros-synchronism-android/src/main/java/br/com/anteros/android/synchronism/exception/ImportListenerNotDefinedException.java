package br.com.anteros.android.synchronism.exception;

/**
 *
 * @author Edson Martins
 */
@SuppressWarnings("serial")
public class ImportListenerNotDefinedException extends Exception {
 private String message;

    public ImportListenerNotDefinedException(String message){
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}

