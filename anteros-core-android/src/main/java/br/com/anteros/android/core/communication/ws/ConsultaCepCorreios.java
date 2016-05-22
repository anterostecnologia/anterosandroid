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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import br.com.anteros.android.core.R;

/**
 * Created by edson on 21/05/16.
 */

public class ConsultaCepCorreios extends AsyncTask<String,String,EnderecoCorreio> {

    private static final String METHOD_NAME = "consultaCEP";
    private static final String NAMESPACE = "http://cliente.bean.master.sigep.bsb.correios.com.br/";
    private static final String URL = "https://apps.correios.com.br/SigepMasterJPA/AtendeClienteService/AtendeCliente";
    public static final String CEP = "cep";

    public ConsultaCepCorreios(){
    }

    @Override
    protected EnderecoCorreio doInBackground(String... params) {
        SoapObject cliente = new SoapObject(NAMESPACE, METHOD_NAME);
        cliente.addProperty(CEP, params[0]);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.bodyOut = cliente;
        HttpTransportSE httpTransportSE = new HttpTransportSE(URL);
        try {
            httpTransportSE.call("", envelope);
            SoapObject response = (SoapObject) envelope
                    .getResponse();
            return EnderecoCorreio.of(response);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
    }

    @Override
    protected void onPostExecute(EnderecoCorreio endereco) {
    }
}