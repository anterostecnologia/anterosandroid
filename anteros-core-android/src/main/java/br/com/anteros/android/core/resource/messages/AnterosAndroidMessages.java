package br.com.anteros.android.core.resource.messages;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import br.com.anteros.core.resource.messages.AnterosBundle;

public class AnterosAndroidMessages implements AnterosBundle {

	private final Map<String, String> messages = new HashMap<String, String>();

	public AnterosAndroidMessages() {
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
