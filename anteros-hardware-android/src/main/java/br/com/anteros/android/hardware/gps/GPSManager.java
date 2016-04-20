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
