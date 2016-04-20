package br.com.anteros.android.synchronism.communication;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;
import br.com.anteros.android.core.util.Convert;

public class SynchronismTransactionControl {
	public final String fileName = "synchronism_control.dat";
	private Context context;

	public SynchronismTransactionControl(Context context) {
		this.context = context;
	}

	public long getCurrentTransactionId() throws Exception {
		String value = readFileContent();
		if (value == null || "".equals(value) || "0".equals(value))
			return getNextTransactionId();
		return Convert.parseLong(value, 0);
	}

	public long getNextTransactionId() throws Exception {
		long result = System.currentTimeMillis();
		writeFileContent(String.valueOf(result));
		return result;
	}

	private String readFileContent() {
		try {
			InputStream instream = context.openFileInput(fileName);
			if (instream != null) {
				InputStreamReader inputreader = new InputStreamReader(instream);
				BufferedReader buffreader = new BufferedReader(inputreader);
				String value = buffreader.readLine();
				instream.close();
				return value;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0";
	}

	private void writeFileContent(String content) {
		FileOutputStream fOut = null;
		OutputStreamWriter osw = null;
		try {
			fOut = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			osw = new OutputStreamWriter(fOut);
			osw.write(content);
			osw.flush();
			osw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void resetTransaction() {
		writeFileContent("0");
	}

	public void resetTransaction(String newTransactionId) {
		writeFileContent(newTransactionId);
	}

	public long getStoredId() {
		return Convert.parseLong(readFileContent(), 0);
	}
}
