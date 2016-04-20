package br.com.anteros.android.synchronism.exception;

/**
 *
 * @author Edson Martins
 */
@SuppressWarnings("serial")
public class MobileRequestNoActionsFound  extends Exception {

    private String message;

    public MobileRequestNoActionsFound(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
