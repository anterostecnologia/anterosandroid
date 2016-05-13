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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.anteros.android.core.util.FileUtil;


public class ExportDatabaseTask extends AsyncTask<Void, Void, String> {
	private final static int maxBackupFiles = 12;
	private static File ultimoBackup;
	private ProgressDialog dialog;
	private Context context;
	private boolean runByService;
	private String databasePath;
	private String databaseName;
	private SharedPreferences preferences;

	public ExportDatabaseTask(Context context, String absolutPathDb, String databaseName,
			boolean runByService, SharedPreferences preferences) {
		this.context = context;
		this.databasePath = absolutPathDb;
		this.databaseName = databaseName;
		this.runByService = runByService;
		if (runByService == false) {
			this.dialog = new ProgressDialog(context);
		}
		this.preferences = preferences;
	}

	public ExportDatabaseTask(Context context, String absolutPathDb, String databaseName, SharedPreferences preferences) {
		this(context, absolutPathDb, databaseName, false, preferences);
	}

	public static String executarBackup(String databaseName, String databasePath, SharedPreferences preferences,
										Context context) {

		if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			throw new BackupException("Não foi possível executar a tarefa de backup pois você não possuí permissão para isto. Verifique se solicitou permissão no manifesto ou requisitou a permissão caso esteja usando Android(Marshmallow) 6 ou acima.");
		}

		File dbFile = new File(databasePath);

		File exportDir = new File(Environment.getExternalStorageDirectory(),
				"backup");

		if (!exportDir.exists()) {
			exportDir.mkdirs();
		}

		if (countBackupFiles(exportDir, dbFile.getName()) >= maxBackupFiles) {
			deleteOldBackup(exportDir, dbFile.getName());
		}

		ultimoBackup = new File(exportDir, generateBackupName(dbFile));
		try {
			ultimoBackup.createNewFile();
			FileUtil.copyFile(dbFile, ultimoBackup);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putLong(BackupService.DATE_TIME_LAST_BACKUP, new Date().getTime());
			editor.commit();
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}

		SQLiteDatabase db = null;
		try {
			db = context.openOrCreateDatabase(databaseName, SQLiteDatabase.OPEN_READWRITE, null);
			db.execSQL("vacuum;");
			db.execSQL("reindex;");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				db.close();
			} catch (Exception e2) {
			}
		}

		return "OK";
	}

	/**
	 * Deleta arquivo de backup mais antigo de dentro da pasta "backup" no
	 * cartão de memória.
	 *
	 * @param exportDir
	 */
	private static void deleteOldBackup(File exportDir, String bdFileName) {
		File oldFile = null;
		for (File f : exportDir.listFiles(new BackupFileFilter(bdFileName))) {
			if (oldFile == null || oldFile.lastModified() > f.lastModified()) {
				oldFile = f;
			}
		}
		if (oldFile != null && oldFile.exists()) {
			oldFile.delete();
		}
	}

	/**
	 * Retorna quantidade de arquivos de backup de dentro da pasta "backup" no
	 * cartão de memória
	 *
	 * @param exportDir
	 * @return
	 */
	private static int countBackupFiles(File exportDir, String bdFileName) {
		if (exportDir != null && exportDir.exists()) {
			File[] arqs = exportDir.listFiles(new BackupFileFilter(bdFileName));
			return arqs != null ? arqs.length : 0;
		}
		return 0;
	}

	private static String generateBackupName(File dbFile) {
		String name = "";

		name = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss-").format(new Date())
				+ dbFile.getName();

		return name;
	}

	@Override
	protected void onPreExecute() {
		if (runByService == false) {
			dialog.setMessage("Exportando banco de dados...");
			dialog.show();
		}
	}

	@Override
	protected String doInBackground(final Void... args) {
		return executarBackup(databaseName, databasePath, preferences, context);
	}

	@Override
	protected void onPostExecute(final String success) {
		if (runByService == false) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			if (success.equals("OK")) {
				Toast.makeText(this.context,
						"Exportação realizada com sucesso!", Toast.LENGTH_SHORT)
						.show();
			} else {
				Toast.makeText(this.context, "Export falhou - " + success,
						Toast.LENGTH_LONG).show();
			}
		}
	}

}
