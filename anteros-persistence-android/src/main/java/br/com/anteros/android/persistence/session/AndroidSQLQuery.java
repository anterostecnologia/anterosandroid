package br.com.anteros.android.persistence.session;

import br.com.anteros.persistence.session.SQLSession;
import br.com.anteros.persistence.session.impl.SQLQueryImpl;

public class AndroidSQLQuery<T> extends SQLQueryImpl<T> {

	public AndroidSQLQuery(SQLSession session) {
		super(session);
	}

}
