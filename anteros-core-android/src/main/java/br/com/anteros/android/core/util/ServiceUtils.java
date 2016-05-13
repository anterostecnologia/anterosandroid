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

package br.com.anteros.android.core.util;


import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;


public class ServiceUtils {
	/**
	 * Verifica se o serviço já está em execução.
	 * 
	 * @param context
	 * @param classz
	 * @return
	 */
	public static boolean isServiceRunning(Context context,
			Class<? extends Service> classz) {
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> services = activityManager
				.getRunningServices(Integer.MAX_VALUE);
		Log.d(ServiceUtils.class.getSimpleName(), "Procurando Serviços");
		for (int i = 0; i < services.size(); i++) {
			if (services.get(i).service.getClassName().equals(classz.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Verifica se o serviço já está em execução e em seguida cria uma thread
	 * para chamar o serviço desejado
	 * 
	 * @param context
	 * @param classz
	 */
	public static void startService(final Context context,
			final Class<? extends Service> classz) {
		new Thread() {
			public void run() {
				if (!ServiceUtils.isServiceRunning(context, classz)) {
					Intent intentService = new Intent(context, classz);
					context.startService(intentService);
				}
			};
		}.start();
	}
}
