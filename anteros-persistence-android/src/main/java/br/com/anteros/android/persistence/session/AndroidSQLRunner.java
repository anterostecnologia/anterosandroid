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

package br.com.anteros.android.persistence.session;

import java.sql.Connection;

import br.com.anteros.android.persistence.sql.jdbc.SQLiteConnection;
import br.com.anteros.android.core.resource.messages.AnterosAndroidMessages;
import br.com.anteros.core.resource.messages.AnterosBundle;
import br.com.anteros.core.resource.messages.AnterosResourceBundle;
import br.com.anteros.core.utils.SQLFormatter;
import br.com.anteros.persistence.session.configuration.AnterosPersistenceProperties;
import br.com.anteros.persistence.session.impl.SQLQueryRunner;
import br.com.anteros.persistence.session.query.ShowSQLType;

public class AndroidSQLRunner extends SQLQueryRunner {
	
	private static AnterosBundle MESSAGES = AnterosResourceBundle.getBundle(AnterosPersistenceProperties.ANTEROS_ANDROID,AnterosAndroidMessages.class);

	@Override
	public void executeDDL(Connection connection, String ddl, ShowSQLType[] showSql, boolean formatSql, String clientId) throws Exception {
		
		if (ShowSQLType.contains(showSql, ShowSQLType.ALL))
			log.debug(MESSAGES.getMessage(AndroidSQLRunner.class.getSimpleName() + ".executeDDL", (formatSql == true ? SQLFormatter.format(ddl) : ddl), clientId));
		((SQLiteConnection) connection).getDatabase().execSQL(ddl);
	}

}
