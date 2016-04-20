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

package br.com.anteros.android.core.log.impl;

import android.util.Log;



import br.com.anteros.core.log.LogLevel;
import br.com.anteros.core.log.Logger;


/**
 * 
 * Implementação de Logger responsável por enviar a mensagem de Log a biblioteca
 * android.util.Log.
 * 
 * @author Douglas Junior (nassifrroma@gmail.com)
 * 
 */
public class LogCatLogger extends Logger {

	private static final long serialVersionUID = 6392533244800799003L;
	private static final String TAG = "Anteros-Android";
	private final LogLevel level;

	public LogCatLogger(String name, LogLevel level) {
		super(name);
		this.level = level;
	}

	@Override
	public boolean isEnabled(LogLevel level) {
		if (this.level == null)
			return false;
		return level.getIndex() >= this.level.getIndex() && Log.isLoggable(TAG, translateLevel(level));
	}

	@Override
	protected void doLog(LogLevel level, Object message, Throwable t) {
		if (isEnabled(level)) {
			Log.println(translateLevel(level), TAG, message + "");
		}
	}

	private int translateLevel(LogLevel level) {
		if (level == null)
			return 0;
		switch (level) {
		case VERBOSE:
			return Log.VERBOSE;
		case DEBUG:
			return Log.DEBUG;
		case INFO:
			return Log.INFO;
		case WARN:
			return Log.WARN;
		case ERROR:
			return Log.ERROR;
		default:
			return Log.ASSERT;
		}
	}

}
