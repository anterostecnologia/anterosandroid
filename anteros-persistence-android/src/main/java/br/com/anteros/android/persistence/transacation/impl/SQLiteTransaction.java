package br.com.anteros.android.persistence.transacation.impl;

import br.com.anteros.android.persistence.sql.jdbc.SQLiteConnection;
import br.com.anteros.core.log.Logger;
import br.com.anteros.core.log.LoggerProvider;
import br.com.anteros.persistence.session.context.SQLPersistenceContext;
import br.com.anteros.persistence.transaction.AnterosSynchronization;
import br.com.anteros.persistence.transaction.Transaction;
import br.com.anteros.persistence.transaction.TransactionSatus;
import br.com.anteros.persistence.transaction.impl.TransactionException;

/**
 * Classe de transação no banco de dados
 * 
 * @author Edson Martins - Anteros
 * 
 */
public class SQLiteTransaction implements Transaction {

	private static Logger log = LoggerProvider.getInstance().getLogger(SQLiteTransaction.class);

	private SQLiteConnection connection;
	private SQLPersistenceContext context;

	protected TransactionSatus status = TransactionSatus.NOT_ACTIVE;

	public SQLiteTransaction(SQLiteConnection connection, SQLPersistenceContext context) {
		this.connection = connection;
		this.context = context;
	}

	/**
	 * Inicia transação no banco de dados
	 */
	public void begin() throws Exception {
		if (status == TransactionSatus.ACTIVE) {
			throw new TransactionException("transações aninhadas não são suportadas");
		}
		if (status == TransactionSatus.FAILED_COMMIT) {
			throw new TransactionException("não foi possível reiniciar a transação após o commit ter falhado");
		}

		log.debug("begin");

		getConnection().getDatabase().beginTransaction();

		status = TransactionSatus.ACTIVE;
	}

	private SQLiteConnection getConnection() {
		return connection;
	}

	/**
	 * Finaliza transação no banco de dados e grava os dados.
	 */
	public void commit() throws Exception {
		if (status != TransactionSatus.ACTIVE) {
			throw new TransactionException("A transação não foi iniciada");
		}

		log.debug("commit");
		getPersistenceContext().onBeforeExecuteCommit(getConnection());
		try {
			connection.getDatabase().setTransactionSuccessful();
			connection.getDatabase().endTransaction();
			status = TransactionSatus.COMMITTED;
			getPersistenceContext().onAfterExecuteCommit(getConnection());
		} catch (Exception e) {
			log.error("JDBC commit failed", e);
			status = TransactionSatus.FAILED_COMMIT;
			throw new TransactionException("commit failed", e);
		}
	}

	/**
	 * Finaliza transação no banco de dados e não grava dados.
	 */
	public void rollback() throws Exception {
		if (status != TransactionSatus.ACTIVE && status != TransactionSatus.FAILED_COMMIT) {
			throw new TransactionException("Transação não foi iniciada");
		}

		log.debug("rollback");

		if (status != TransactionSatus.FAILED_COMMIT) {
			getPersistenceContext().onBeforeExecuteCommit(getConnection());
			try {
				connection.getDatabase().endTransaction();
				status = TransactionSatus.ROLLED_BACK;
				getPersistenceContext().onAfterExecuteRollback(getConnection());
			} catch (Exception e) {
				throw new TransactionException("rollback failed", e);
			}
		}
	}

	private SQLPersistenceContext getPersistenceContext() {
		return context;
	}

	@Override
	public boolean isActive() throws Exception {
		return getConnection().getDatabase().inTransaction();
	}

	@Override
	public void registerSynchronization(AnterosSynchronization synchronization) throws Exception {
	}
}
