package br.com.anteros.android.persistence.transacation.impl;

import java.sql.Connection;

import br.com.anteros.android.persistence.sql.jdbc.SQLiteConnection;
import br.com.anteros.persistence.session.context.SQLPersistenceContext;
import br.com.anteros.persistence.transaction.Transaction;
import br.com.anteros.persistence.transaction.TransactionFactory;
import br.com.anteros.persistence.transaction.impl.TransactionException;

public class SQLiteTransactionFactory implements TransactionFactory {

	@Override
	public Transaction createTransaction(Connection connection, SQLPersistenceContext context)
			throws TransactionException {
		return new SQLiteTransaction((SQLiteConnection) connection, context);
	}

}
