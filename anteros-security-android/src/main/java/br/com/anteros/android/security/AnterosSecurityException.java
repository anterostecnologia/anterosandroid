package br.com.anteros.android.security;

public class AnterosSecurityException extends Exception {

	public AnterosSecurityException() {
	}

	public AnterosSecurityException(String detailMessage) {
		super(detailMessage);
	}

	public AnterosSecurityException(Throwable throwable) {
		super(throwable);
	}

	public AnterosSecurityException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
