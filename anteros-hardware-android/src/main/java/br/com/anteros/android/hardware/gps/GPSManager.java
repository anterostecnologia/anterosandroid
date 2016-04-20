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

package br.com.anteros.android.hardware.gps;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

/**
 * Classe para utilizacao do GPS
 * 
 * @author turim
 * 
 */
public class GPSManager {

	private LocationManager locationManager;
	private String provider;
	private static GPSManager manager;
	/**
	 * Utilizar Servico de GPS da Rede.
	 */
	public static final String NETWORK_SERVICE = LocationManager.NETWORK_PROVIDER;
	/**
	 * Utilizar Servico de GPS via satelite
	 */
	public static final String GPS_SERVICE = LocationManager.GPS_PROVIDER;

	/**
	 * Iniciar GPSManager, por padrao define automaticamente o melhor provedor
	 * de servico de GPS, levan em conta bateria, sinal de rede, etc.
	 * 
	 * @param context
	 */
	private GPSManager(Context context) {
		locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);

		provider = getBestProvider(criteria);
	}

	/**
	 * Retorna o nome do fornecedor que melhor preenche os critérios.
	 * 
	 * @param criteria
	 * @return
	 */
	public String getBestProvider(Criteria criteria) {
		return locationManager.getBestProvider(criteria, false);
	}

	public static GPSManager getManager(Context context) {
		if (manager == null)
			manager = new GPSManager(context);
		return manager;
	}

	public void defineProvider(String provider) {
		this.provider = provider;
	}

	public void setListener(LocationListener listener, int timeToUpdate,
			int distanceToUpdate) {
		locationManager.requestLocationUpdates(provider, timeToUpdate,
				distanceToUpdate, listener);
	}

	public void removeListener(LocationListener listener) {
		locationManager.removeUpdates(listener);
	}

	public Location getLasKnowLocation() {
		return locationManager.getLastKnownLocation(provider);
	}

}
