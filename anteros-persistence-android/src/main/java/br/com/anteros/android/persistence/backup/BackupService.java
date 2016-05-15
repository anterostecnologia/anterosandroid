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

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import java.util.Date;

import br.com.anteros.core.utils.DateUtil;
import br.com.anteros.core.utils.StringUtils;


public class BackupService extends Service {

	private static final int BACKUP_INTERVAL = 2;// horas
	private static final int IDDLE_TIME = 5; // minutos
	public static final String DATE_TIME_LAST_BACKUP = "DATE_TIME_LAST_BACKUP";
	public static final String PREFERENCES_NAME = "Backup-Service";
	public static final String PERSISTENCE_DATABASE_NAME = "br.com.anteros.android.persistence.DatabaseName";
	private boolean serviceRunning = false;
	private boolean screenOff = false;
	private String databaseName = "";
	private Handler handler;

	private SharedPreferences preferences;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		ApplicationInfo ai = null;
		try {
			ai = this.getApplicationContext().getPackageManager().getApplicationInfo(this.getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
		} catch (PackageManager.NameNotFoundException e) {
			stopSelf();
			throw new BackupServiceException(e);
		}
		databaseName = (String)ai.metaData.get(PERSISTENCE_DATABASE_NAME);
		if (StringUtils.isEmpty(databaseName)){
			throw new BackupServiceException("Não foi encontrado o nome do banco de dados para realizar o backup. Crie um metadata contendo o nome para 'DatabaseName' na sua aplicação.");
		}

		preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);

		handler = new Handler() {
			public void handleMessage(Message msg) {
				try {
					ExportDatabaseTask.executarBackup(BackupService.this.databaseName, BackupService.this
							.getApplication().getDatabasePath(BackupService.this.databaseName)
							.getAbsolutePath(), preferences, BackupService.this);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			};
		};

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		BroadcastReceiver mReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
					screenOff = true;
					try {
						new Thread(new BackupThread()).start();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
					screenOff = false;
				}
			}
		};
		registerReceiver(mReceiver, filter);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
		try {
			new Thread(new BackupThread()).start();
			serviceRunning = true;
		} catch (Exception e) {
			e.printStackTrace();
			serviceRunning = false;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		serviceRunning = false;
	}

	private class BackupThread implements Runnable {

		@Override
		public void run() {
			try {
				if (screenOff) {
					try {
						Thread.sleep(IDDLE_TIME * 60000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (screenOff && DateUtil.getIntervalInHours(
							new Date(preferences.getLong(DATE_TIME_LAST_BACKUP, 0)), new Date()) > BACKUP_INTERVAL) {
						handler.sendMessage(new Message());
						return;
					}
					if (serviceRunning) {
						new Thread(new BackupThread()).start();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}