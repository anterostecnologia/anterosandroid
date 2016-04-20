package br.com.anteros.android.synchronism.exception;

import java.io.FileNotFoundException;

@SuppressWarnings("serial")
public class ExternalStorageStateException extends FileNotFoundException {

	public ExternalStorageStateException(String message) {
		super(message);
	}

}
