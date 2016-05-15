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

package br.com.anteros.android.core.communication.ws;

import android.os.AsyncTask;

import com.google.gson.Gson;

import br.com.anteros.android.core.communication.http.HttpHelper;

/**
 * Created by edson on 12/05/16.
 */
public class PostmonWebService extends AsyncTask<String,String,PostmonResponse> {

    @Override
    protected PostmonResponse doInBackground(String... params) {
        PostmonResponse result = null;
        try {
            String response = HttpHelper.getJSON("http://api.postmon.com.br/v1/cep/" + params[0], null, 10000, HttpHelper.GET, HttpHelper.CONTENT_TYPE_JSON);
            if (response!=null){
                result = new Gson().fromJson(response, PostmonResponse.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}
