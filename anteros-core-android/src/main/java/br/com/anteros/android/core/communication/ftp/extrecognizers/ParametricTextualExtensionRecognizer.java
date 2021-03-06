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

package br.com.anteros.android.core.communication.ftp.extrecognizers;

import java.util.ArrayList;

import br.com.anteros.android.core.communication.ftp.FTPClient;
import br.com.anteros.android.core.communication.ftp.FTPTextualExtensionRecognizer;

/**
 * A textual extension recognizer with parametric extensions, which can be added
 * or removed at runtime.
 * 
 * @author Carlo Pelliccia
 * @see FTPClient#setTextualExtensionRecognizer(FTPTextualExtensionRecognizer)
 */
public class ParametricTextualExtensionRecognizer implements
		FTPTextualExtensionRecognizer {

	/**
	 * Extension list.
	 */
	private ArrayList exts = new ArrayList();

	/**
	 * It builds the recognizer with an empty extension list.
	 */
	public ParametricTextualExtensionRecognizer() {
		;
	}

	/**
	 * It builds the recognizer with an initial extension list.
	 * 
	 * @param exts
	 *            The initial extension list.
	 */
	public ParametricTextualExtensionRecognizer(String[] exts) {
		for (int i = 0; i < exts.length; i++) {
			addExtension(exts[i]);
		}
	}

	/**
	 * It builds the recognizer with an initial extension list.
	 * 
	 * @param exts
	 *            The initial extension list.
	 */
	public ParametricTextualExtensionRecognizer(ArrayList exts) {
		int size = exts.size();
		for (int i = 0; i < size; i++) {
			Object aux = exts.get(i);
			if (aux instanceof String) {
				String ext = (String) aux;
				addExtension(ext);
			}
		}
	}

	/**
	 * This method adds an extension to the recognizer.
	 * 
	 * @param ext
	 *            The extension.
	 */
	public void addExtension(String ext) {
		synchronized (exts) {
			ext = ext.toLowerCase();
			exts.add(ext);
		}
	}

	/**
	 * This method removes an extension to the recognizer.
	 * 
	 * @param ext
	 *            The extension to be removed.
	 */
	public void removeExtension(String ext) {
		synchronized (exts) {
			ext = ext.toLowerCase();
			exts.remove(ext);
		}
	}

	/**
	 * This method returns the recognized extension list.
	 * 
	 * @return The list with all the extensions recognized to be for textual
	 *         files.
	 */
	public String[] getExtensions() {
		synchronized (exts) {
			int size = exts.size();
			String[] ret = new String[size];
			for (int i = 0; i < size; i++) {
				ret[i] = (String) exts.get(i);
			}
			return ret;
		}
	}

	public boolean isTextualExt(String ext) {
		synchronized (exts) {
			return exts.contains(ext);
		}
	}

}
