package br.com.anteros.android.synchronism.exception;

/**
 *
 * @author Edson Martins
 */
@SuppressWarnings("serial")
public class ExportListenerNotDefinedException extends Exception {
 private String message;

    public ExportListenerNotDefinedException(String message){
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
