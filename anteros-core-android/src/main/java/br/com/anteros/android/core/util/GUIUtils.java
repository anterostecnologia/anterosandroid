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

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;

import java.math.BigDecimal;

public class GUIUtils {

    /**
     * Método para verificar se o disposítivo é um tablet ou não
     * Se o tamanho da tela for maior que 7" e a densidade não for alta, é considerado tablet
     */
    public static boolean isTablet(Activity activity) {
        if (activity == null)
            return false;

        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int widthPixels = displayMetrics.widthPixels;
        int heightPixels = displayMetrics.heightPixels;

        float widthDpi = displayMetrics.xdpi;
        float heightDpi = displayMetrics.ydpi;

        float widthInches = widthPixels / widthDpi;
        float heightInches = heightPixels / heightDpi;

        double diagonalInches = Math.sqrt((widthInches * widthInches) + (heightInches * heightInches));

        BigDecimal bd = new BigDecimal(Double.toString(diagonalInches));
        bd = bd.setScale(0, BigDecimal.ROUND_CEILING);

        return !(Math.round(diagonalInches) < 7 || (Math.round(diagonalInches) >= 7 && (displayMetrics.densityDpi >= DisplayMetrics.DENSITY_HIGH)));

    }

    /**
     * Método para verificar se o dispositivo está conectado na internet, por meio de Wifi ou conexões móveis
     *
     * @return <i>true</i> se estiver conectado ou <i>false</i> caso contrário
     */
    public static boolean isOnline(Context context) {
        if (context == null)
            return false;
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo netInfo = connectivity.getActiveNetworkInfo();
            if (netInfo == null) return false;
            int netType = netInfo.getType();
            // Verifica se a conexão é do tipo WiFi ou Mobile e
            // retorna
            return (netType == ConnectivityManager.TYPE_WIFI || netType == ConnectivityManager.TYPE_MOBILE) && netInfo.isConnected();
        } else
            return false;
    }
}
