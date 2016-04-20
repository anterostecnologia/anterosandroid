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

package br.com.anteros.android.ui.controls.map;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;


/**
 * Classe adapter para criar o Objeto {@link GeoPoint} que representa Latitude e Longitude usada no {@link MapView} do Android.
 * 
 *
 */
public class MapGeoPoint {
	private GeoPoint geoPoint;

	public MapGeoPoint(double latitude, double longitude) {
		this.geoPoint = new GeoPoint((int) (latitude * 1E6),
				(int) (longitude * 1E6));
	}

	public MapGeoPoint(int latitude, int longitude) {
		this.geoPoint = new GeoPoint((int) (latitude * 1E6),
				(int) (longitude * 1E6));
	}

	/**
	 * Retorna o Objeto {@link GeoPoint}, que representa Latitude e Longitude no MapView do Android.
	 *
	 * @return {@link GeoPoint}
	 */
	public GeoPoint getGeoPoint() {
		return this.geoPoint;
	}

}
