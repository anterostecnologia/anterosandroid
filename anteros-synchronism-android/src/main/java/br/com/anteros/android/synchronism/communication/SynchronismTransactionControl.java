/*
 * Copyright 2016 Anteros Tecnologia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
