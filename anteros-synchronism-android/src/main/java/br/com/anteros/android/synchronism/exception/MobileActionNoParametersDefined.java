package br.com.anteros.android.synchronism.exception;

/**
 *
 * @author Edson Martins
 */
@SuppressWarnings("serial")
public class MobileActionNoParametersDefined extends Exception {

    private String message;

    public MobileActionNoParametersDefined(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
