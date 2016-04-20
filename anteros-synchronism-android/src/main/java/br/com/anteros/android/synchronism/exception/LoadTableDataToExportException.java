package br.com.anteros.android.synchronism.exception;

/**
 *
 * @author Edson Martins
 */
@SuppressWarnings("serial")
public class LoadTableDataToExportException extends Exception {
 private String message;

    public LoadTableDataToExportException(String message){
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
