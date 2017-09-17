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

package br.com.anteros.android.core.communication.http;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import br.com.anteros.android.core.communication.http.types.HttpMethod;
import br.com.anteros.android.core.communication.http.types.MediaType;
import br.com.anteros.persistence.sql.command.RuntimeSqlException;

/**
 * @author Edson Martins     (edsonmartions2005@gmail.com)
 *         Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Data: 03/05/16.
 */
public class HttpHelper {

    public static String getJSON(String url, String data, int timeout, HttpMethod method, MediaType mediaType, Authentication authentication) throws Exception {
        HttpURLConnection connection = null;
        try {

            URL u = new URL(url);
            connection = (HttpURLConnection) u.openConnection();


            if (authentication != null) {
                connection.setRequestProperty("Authorization", authentication.getBasicAuthenticatorCredentials());
            }

            connection.setRequestMethod(method.value());

            //set the sending type and receiving type to json
            connection.setRequestProperty("Content-Type", mediaType.value());
            connection.setRequestProperty("Accept", MediaType.APPLICATION_JSON.value());

            connection.setAllowUserInteraction(false);
            connection.setConnectTimeout(timeout);
            connection.setReadTimeout(timeout);

            if (data != null) {
                //set the content length of the body
                connection.setRequestProperty("Content-length", data.getBytes().length + "");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);

                //send the json as body of the request
                OutputStream outputStream = connection.getOutputStream();
                outputStream.write(data.getBytes("UTF-8"));
                outputStream.close();
            }

            //Connect to the server
            connection.connect();

            int status = connection.getResponseCode();
            Logger.getLogger(HttpHelper.class.getName()).log(Level.INFO, "HTTP Client", "HTTP status code : " + status);
            switch (status) {
                case 200:
                case 201:
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    bufferedReader.close();
                    Logger.getLogger(HttpHelper.class.getName()).log(Level.INFO, "HTTP Client", "Received String : " + sb.toString());
                    //return received string
                    return sb.toString();
                default:
                    throw new RuntimeException("Ocorreu um problema com a requisição. Status code: " + status, new RuntimeSqlException(connection.getResponseMessage()));
            }
        } finally {
            if (connection != null) {
                try {
                    connection.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(HttpHelper.class.getName()).log(Level.SEVERE, "HTTP Client", "Error in http connection" + ex.toString());
                }
            }
        }
    }

    public static String getJSON(String url, String data, int timeout, HttpMethod method, MediaType mediaType) throws Exception {
        return getJSON(url, data, timeout, method, mediaType, null);
    }
}
