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

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import br.com.anteros.android.core.util.AndroidFileUtils;
import br.com.anteros.android.core.util.FileUtil;
import br.com.anteros.persistence.session.SQLSession;

public class ImportDatabaseTask extends AsyncTask<Void, Void, String> {

	public static int TABLES_RECREATED = 876363;

	private final ProgressDialog dialog;
	private final SQLSession session;
	private Activity activity;
	private String databaseName;
	private File importDatabaseFile;

	public ImportDatabaseTask(Activity activity, File importDatabaseFile,
							  String databaseName, SQLSession session) {
		this.importDatabaseFile = importDatabaseFile;
		this.activity = activity;
		this.dialog = new ProgressDialog(this.activity);
		this.dialog.setCancelable(false);
		this.databaseName = databaseName;
		this.session = session;
	}

	@Override
	protected void onPreExecute() {
		dialog.setMessage("Importando banco de dados...");
		dialog.show();
	}

	@Override
	protected String doInBackground(final Void... args) {

		if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			throw new BackupException("Não foi possível executar a tarefa de importar o banco de dados pois você não possuí permissão para isto. Verifique se solicitou permissão no manifesto ou requisitou a permissão caso esteja usando Android(Marshmallow) 6 ou acima.");
		}


		try {
			session.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		File dbBackupFile = importDatabaseFile;
		if (!dbBackupFile.exists()) {
			return "Arquivo de backup não foi encontrado. Não foi possível importar.";
		} else if (!dbBackupFile.canRead()) {
			return "Arquivo de backup não pode ser lido. Não foi possível importar.";
		}

		File dbFile = new File(databaseName);

		if (dbFile.exists()) {
			dbFile.delete();
		}

		try {
			AndroidFileUtils.copyFile(dbBackupFile, dbFile);
			return "OK";
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

		AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
		dialog.setTitle("Aviso");
		if (errMsg.equals("OK")) {
			dialog.setMessage("Importação realizada com sucesso!");
			Toast.makeText(activity, "Importação realizada com sucesso!", Toast.LENGTH_SHORT).show();
		} else {
			dialog.setMessage("Importação falhou - " + errMsg);
			Toast.makeText(activity, "Importação falhou - " + errMsg, Toast.LENGTH_LONG).show();
		}
		dialog.setCancelable(false);
		dialog.setPositiveButton("Ok", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		AlertDialog dlg = dialog.create();
		dlg.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				activity.setResult(ImportDatabaseTask.TABLES_RECREATED);
				activity.finish();
			}
		});
		dlg.show();
	}
}
