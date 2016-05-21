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

package br.com.anteros.android.ui.controls.autocomplete;


import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by edson on 17/04/16.
 */

public class AnterosGoogleAutoCompleteSearch {

    private AnterosGoogleAutoCompleteSearch() {
    }

    private AnterosGoogleAutoCompleteListener listener = null;

    private String language = "pt_BR";

    public static AnterosGoogleAutoCompleteSearch getInstance(AnterosGoogleAutoCompleteListener listener, String language) {
        AnterosGoogleAutoCompleteSearch finder = null;
        if (listener != null && language != null && language.trim().length() > 0) {
            finder = new AnterosGoogleAutoCompleteSearch();
            finder.listener = listener;
            finder.language = language.trim();
        }
        return finder;
    }

    private String createUrl(String search) {
        String url = "http://suggestqueries.google.com/complete/search?client=firefox&hl=" + language;
        try {
            url += "&q=" + Uri.encode(search);
        } catch (Exception e) {
        }
        return url;
    }

    public void start(String search) {
        destroy();
        task = new AutoCompleteTask(search);
        task.execute(new Object[]{});
    }

    private AutoCompleteTask task = null;

    public void destroy() {
        if (task != null) {
            task.cancel(true);
            task = null;
        }
    }

    class AutoCompleteTask extends AsyncTask<Object, Object, Object> {

        private String search = null;

        AutoCompleteTask(String search) {
            this.search = search;
        }

        private List<String> parse(String json) {
            List<String> list = new ArrayList<String>();
            try {
                json = decodeArray(json)[1];
                String[] array = decodeArray(json);
                if (array != null && array.length >= 2) {
                    for (int i = 0; i < array.length; i++) {
                        if (array[i] != null && array[i].length() > 0) list.add(array[i]);
                    }
                }
            } catch (Exception e) {
            }
            return list;
        }

        @Override
        protected Object doInBackground(Object... params) {
            Object result = null;
            try {
                String urlService = createUrl(search);

                URL url = new URL(urlService);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setInstanceFollowRedirects(true);
                conn.setConnectTimeout(1000 * 60);
                conn.setReadTimeout(1000 * 60);
                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setRequestMethod("GET");

                StringBuilder content = new StringBuilder();

                int code = conn.getResponseCode();
                String json=null;
                if (code == 200) {
                    json = readFullyAsString(conn.getInputStream(),"ISO-8859-1");
                }
                if (json != null && json.length() > 0) result = parse(json);
            } catch (Exception e) {
                result = null;
            }
            return result;
        }

        public String readFullyAsString(InputStream inputStream, String encoding)
                throws IOException {
            return readFully(inputStream).toString(encoding);
        }

        public byte[] readFullyAsBytes(InputStream inputStream)
                throws IOException {
            return readFully(inputStream).toByteArray();
        }

        private ByteArrayOutputStream readFully(InputStream inputStream)
                throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            return baos;
        }


        protected void onPostExecute(Object result) {
            if (listener != null) {
                if (result != null && result instanceof List) {
                    listener.success(search, (List<String>) result);
                } else {
                    listener.error("Unknown data");
                }
            }
        }

    }

    public String[] decodeArray(String source) {
        try {
            JSONArray o = new JSONArray(source);
            int length = o.length();
            if (length > 0) {
                String[] array = new String[length];
                for (int i = 0; i < length; i++) {
                    array[i] = o.getString(i);
                }
                return array;
            }
        } catch (Exception e) {
        }
        return null;
    }

}