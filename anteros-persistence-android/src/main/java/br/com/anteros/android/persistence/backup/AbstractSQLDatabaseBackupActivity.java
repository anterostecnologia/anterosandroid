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

package br.com.anteros.android.persistence.backup;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import br.com.anteros.android.persistence.R;
import br.com.anteros.android.persistence.sql.jdbc.SQLiteConnection;
import br.com.anteros.android.core.util.FileUtil;
import br.com.anteros.persistence.session.SQLSession;

public abstract class AbstractSQLDatabaseBackupActivity extends Activity {

	public static final String BACKUP_PARAMETER_NAME = "BACKUP_PARAMETER_NAME";

	private Button exportDbToSdButton;
	private Button importDbFromSdButton;
	private String databaseNameWithAbsolutePath;
	private String databaseName;
	private SQLiteConnection connection;

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			connection = ((SQLiteConnection) getSQLSession().getConnection());
		} catch (Exception e) {
			e.printStackTrace();
		}
		databaseName = connection.getDatbaseName();
		databaseNameWithAbsolutePath = getApplicationContext().getDatabasePath(databaseName).getAbsolutePath();

		setContentView(R.layout.databasebackup);

		exportDbToSdButton = (Button) findViewById(R.id.exportdbtosdbutton);
		exportDbToSdButton.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				new AlertDialog.Builder(AbstractSQLDatabaseBackupActivity.this).setMessage("Tem certeza que deseja exportar o banco de dados ?")
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								if (isExternalStorageAvail()) {
									new ExportDatabaseTask().execute();
								} else {
									Toast.makeText(AbstractSQLDatabaseBackupActivity.this,
											"Cartão externo não foi encontrado. Não será possível exportar os dados.", Toast.LENGTH_SHORT).show();
								}
							}
						}).setNegativeButton("No", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
							}
						}).show();
			}
		});

		importDbFromSdButton = (Button) findViewById(R.id.importdbfromsdbutton);
		importDbFromSdButton.setOnClickListener(new OnClickListener() {
			public void onClick(final View v) {
				new AlertDialog.Builder(AbstractSQLDatabaseBackupActivity.this).setMessage("Tem certeza que deseja importar o banco de dados ?")
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								if (isExternalStorageAvail()) {
									new ImportDatabaseTask().execute();
									SystemClock.sleep(500);
								} else {
									Toast.makeText(AbstractSQLDatabaseBackupActivity.this,
											"Cartão externo não foi encontrado. Não será possível importar os dados.", Toast.LENGTH_SHORT).show();
								}
							}
						}).setNegativeButton("No", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
							}
						}).show();
			}
		});
	}

	private boolean isExternalStorageAvail() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	private class ExportDatabaseTask extends AsyncTask<Void, Void, Boolean> {
		private final ProgressDialog dialog = new ProgressDialog(AbstractSQLDatabaseBackupActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Exportando banco de dados...");
			dialog.show();
		}

		@Override
		protected Boolean doInBackground(final Void... args) {

			File dbFile = new File(databaseNameWithAbsolutePath);

			File exportDir = new File(Environment.getExternalStorageDirectory(), "backup");
			if (!exportDir.exists()) {
				exportDir.mkdirs();
			}
			File file = new File(exportDir, dbFile.getName());

			try {
				file.createNewFile();
				FileUtil.copyFile(dbFile, file);
				return true;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			if (success) {
				Toast.makeText(AbstractSQLDatabaseBackupActivity.this, "Exportação realizada com sucesso!", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(AbstractSQLDatabaseBackupActivity.this, "Export falhou - ", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private class ImportDatabaseTask extends AsyncTask<Void, Void, String> {
		private final ProgressDialog dialog = new ProgressDialog(AbstractSQLDatabaseBackupActivity.this);

		@Override
		protected void onPreExecute() {
			dialog.setMessage("Importando banco de dados...");
			dialog.show();
		}

		@Override
		protected String doInBackground(final Void... args) {

			File dbBackupFile = new File(Environment.getExternalStorageDirectory() + "/backup/" + databaseName);
			if (!dbBackupFile.exists()) {
				return "Arquivo de backup não foi encontrado. Não foi possível importar.";
			} else if (!dbBackupFile.canRead()) {
				return "Arquivo de backup não pode ser lido. Não foi possível importar.";
			}

			File dbFile = new File(databaseNameWithAbsolutePath + "/" + databaseName);
			if (dbFile.exists()) {
				dbFile.delete();
			}

			try {
				dbFile.createNewFile();
				FileUtil.copyFile(dbBackupFile, dbFile);
				connection.getDatabase().close();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return e.getMessage();
			}
		}

		@Override
		protected void onPostExecute(final String errMsg) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			if (errMsg == null) {
				Toast.makeText(AbstractSQLDatabaseBackupActivity.this, "Importação realizada com sucesso!", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(AbstractSQLDatabaseBackupActivity.this, "Importação falhou - " + errMsg, Toast.LENGTH_SHORT).show();
			}
		}
	}

	public abstract SQLSession getSQLSession();
}