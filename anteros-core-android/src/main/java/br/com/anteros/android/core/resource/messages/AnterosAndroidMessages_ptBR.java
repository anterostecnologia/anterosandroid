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

package br.com.anteros.android.core.resource.messages;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import br.com.anteros.core.resource.messages.AnterosBundle;

public class AnterosAndroidMessages_ptBR implements AnterosBundle {

	private final Map<String, String> messages = new HashMap<String, String>();

	public AnterosAndroidMessages_ptBR() {
		messages.put("AndroidSQLRunner.executeDDL", "DDL -> {0}");
		messages.put("SQLitePreparedStatement.showSql", "SQL-> {0} ");
		messages.put("SQLitePreparedStatement.showParameters", "Parâmetros: ");
	}

	@Override
	public String getMessage(String key) {
		return messages.get(key);
	}

	@Override
	public String getMessage(String key, Object... parameters) {
		return MessageFormat.format(getMessage(key), parameters);
	}
	
	@Override
	public Enumeration<String> getKeys() {
		return new Vector<String>(messages.keySet()).elements();
	}

}
