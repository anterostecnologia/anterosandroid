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

package br.com.anteros.android.synchronism.communication;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import br.com.anteros.android.synchronism.communication.protocol.MobileRequest;
import br.com.anteros.android.synchronism.communication.protocol.MobileResponse;
import br.com.anteros.android.synchronism.listener.MobileSendDataListener;

@SuppressWarnings({"rawtypes", "unused", "unchecked"})
public class HttpConnectionClient {

    private static final int TIMEOUT_CONNECTION = 20000;
    private static final int TIMEOUT_SOCKET = 120000;
    private static DefaultHttpClient httpClient;
    public String sessionId;
    private String url;
    private String urlComplement;
    private int numOption;
    private String typeRequest;
    private List opPOST;
    private MobileSendData mobileSendData;

    public HttpConnectionClient(String url, String typeRequest) {
        this.typeRequest = typeRequest;
        this.url = url;
        urlComplement = "";
        numOption = 0;
        opPOST = new ArrayList();
    }

    public void clear() {
        opPOST.clear();
        numOption = 0;
        urlComplement = "";
    }

    public void add(String option, List records) {
        numOption++;
        String data = "";
        if (records != null) {
            StringBuffer sb = new StringBuffer();
            String campos[];
            for (int i = 0; i < records.size(); i++) {
                campos = (String[]) records.get(i);
                int len = campos.length;
                for (int j = 0; j < len; j++) {
                    sb.append(campos[j].replace("$", "").replace("#", "").replace("|", ""));
                    if (j < len - 1) {
                        sb.append("$");
                        continue;
                    }
                    if (i < records.size() - 1)
                        sb.append("|");
                }
                campos = null;
            }
            data = sb.toString();
        } else {
            data = null;
        }

        if (data != null) {
            if (typeRequest.equalsIgnoreCase("GET")) {
                StringBuffer sb = new StringBuffer();
                sb.append(urlComplement);
                sb.append("&opcao");
                sb.append(numOption);
                sb.append("=");
                sb.append(option);
                sb.append("&msg");
                sb.append(numOption);
                sb.append("=");
                sb.append(data);
                urlComplement = sb.toString();
            } else {
                String opcoes[] = {option, data};
                opPOST.add(opcoes);
            }
        } else if (typeRequest.equalsIgnoreCase("GET")) {
            StringBuffer sb = new StringBuffer();
            sb.append(urlComplement);
            sb.append("&opcao");
            sb.append(numOption);
            sb.append("=");
            sb.append(option);
            sb.append("&msg");
            sb.append(numOption);
            sb.append("=nothing");
            urlComplement = sb.toString();
        } else {
            String opcoes[] = {option, "nothing"};
            opPOST.add(opcoes);
        }
    }

    public MobileResponse sendReceiveData(MobileRequest mobileRequest) {
        sessionId = HttpConnectionSession.getInstance().getSessionId();

        add(mobileRequest.getFormattedHeader(), mobileRequest.getFormatedActions());
        MobileResponse mobileResponse = new MobileResponse();
        try {
            if (this.getSendData() != null) {
                for (MobileSendDataListener listener : this.getSendData().getListeners())
                    listener.onWaitServer();
            }

            if (this.getSendData() != null) {
                for (MobileSendDataListener listener : this.getSendData().getListeners())
                    listener.onStatusConnectionServer("Conectando Servidor...");
            }

			/*
             * Define url e estabelece conexão
			 */

            HttpPost httpPost = new HttpPost(url);

            HttpParams httpParameters = new BasicHttpParams();
            // Set the timeout in milliseconds until a connection is
            // established.
            // The default value is zero, that means the timeout is not used.
            HttpConnectionParams.setConnectionTimeout(httpParameters, TIMEOUT_CONNECTION);
            // Set the default socket timeout (SO_TIMEOUT)
            // in milliseconds which is the timeout for waiting for data.
            HttpConnectionParams.setSoTimeout(httpParameters, TIMEOUT_SOCKET);

            if (httpClient == null)
                httpClient = new DefaultHttpClient(httpParameters);

            //

			/*
			 * Setar o cookie da sessão
			 */
            if ((sessionId != null) && (!"".equals(sessionId))) {
                httpPost.setHeader("Cookie", "JSESSIONID=" + sessionId);
            }
            httpPost.setHeader("User-Agent", "Android");
            httpPost.setHeader("Accept-Encoding", "gzip");

            if (this.getSendData() != null) {
                for (MobileSendDataListener listener : this.getSendData().getListeners())
                    listener.onStatusConnectionServer("Enviando requisição...");
            }

            //
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(baos);
            //

			/*
			 * Escrever no output
			 */
            out.writeInt(numOption);
            String aux[];
            for (int i = 0; i < opPOST.size(); i++) {
                aux = (String[]) opPOST.get(i);

                out.writeUTF(aux[0]);

                byte[] b = aux[1].getBytes();
                out.writeInt(b.length);
                out.write(b);

                aux = null;
            }
            out.flush();

            ByteArrayEntity entity = new ByteArrayEntity(baos.toByteArray());
            entity.setContentEncoding("UTF-8");
            httpPost.setEntity(entity);
            httpPost.addHeader("Connection", "Keep-Alive");
            httpPost.addHeader("Keep-Alive", "timeout=120000");

            out.close();
            //

            if (this.getSendData() != null) {
                for (MobileSendDataListener listener : this.getSendData().getListeners())
                    listener.onStatusConnectionServer("Recebendo dados...");
            }

            if (this.getSendData() != null) {
                for (MobileSendDataListener listener : this.getSendData().getListeners())
                    listener.onDebugMessage("Recebendo dados conexão");
            }

			/*
			 * Aguardar resposta
			 */
            HttpResponse httpResponse = httpClient.execute(httpPost);
            List result = null;
            StatusLine statusLine = httpResponse.getStatusLine();
            int code = statusLine.getStatusCode();
            if (code != 200) {
                String msg = "Erro RECEBENDO resposta do Servidor " + url
                        + " - Código do Erro HTTP " + code + "-"
                        + statusLine.getReasonPhrase();
                mobileResponse.setStatus(msg);
            } else {
                if (this.getSendData() != null) {
                    for (MobileSendDataListener listener : this.getSendData().getListeners())
                        listener.onStatusConnectionServer("Resposta OK !");
                }

				/*
				 * Ler cookie
				 */
                String tmpSessionId = null;

                for (Cookie c : httpClient.getCookieStore().getCookies()) {
                    if ("JSESSIONID".equals(c.getName())) {
                        tmpSessionId = c.getValue();
                    }
                }

                if (tmpSessionId != null) {
                    sessionId = tmpSessionId;
                    HttpConnectionSession.getInstance().setSessionId(sessionId);
                }
                //

                if (this.getSendData() != null) {
                    for (MobileSendDataListener listener : this.getSendData().getListeners())
                        listener.onStatusConnectionServer("Lendo dados...");
                }

				/*
				 * Le os dados
				 */
                HttpEntity entityResponse = httpResponse.getEntity();
                InputStream in = AndroidHttpClient.getUngzippedContent(entityResponse);

                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

                String content = null;

                content = reader.readLine();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    content += line;
                }
                line = "";

                reader.close();
                reader = null;
                in.close();
                in = null;
                entityResponse.consumeContent();
                entityResponse = null;
                //

                StringTokenizer messagePart = new StringTokenizer(content, "#");
                content = null;

                if (this.getSendData() != null) {
                    for (MobileSendDataListener listener : this.getSendData().getListeners())
                        listener.onDebugMessage("RECEBEU dados conexão");
                }

                if (this.getSendData() != null) {
                    for (MobileSendDataListener listener : this.getSendData().getListeners())
                        listener.onStatusConnectionServer("Processando resposta... ");
                }

                if (this.getSendData() != null) {
                    for (MobileSendDataListener listener : this.getSendData().getListeners())
                        listener.onDebugMessage("Converteu string dados conexão");
                }

                while (messagePart.hasMoreTokens()) {
                    String resultData = messagePart.nextToken();
                    resultData = resultData.substring(resultData.indexOf("*") + 1, resultData.length());
                    if (result == null)
                        result = formatData(resultData);
                    else
                        result.addAll(formatData(resultData));
                }
                messagePart = null;
            }

            if (result != null) {
                mobileResponse.setFormattedParameters(result);
                result.clear();
                result = null;
            }

            if (this.getSendData() != null) {
                for (MobileSendDataListener listener : this.getSendData().getListeners())
                    listener.onEndServer();
            }

        } catch (SocketTimeoutException exTimeout) {
            exTimeout.printStackTrace();
            wrapException(mobileResponse, "Não foi possível CONECTAR ao Servidor " + url
                    + ". Verifique sua conexão e se o servidor está em funcionamento.");
        } catch (Exception e) {
            e.printStackTrace();
            if ((e.getMessage() + "").contains("unreachable"))
                wrapException(mobileResponse,
                        "Você está sem acesso a internet. Verifique sua conexão. Não foi possível conectar ao servidor  "
                                + url);
            else
                wrapException(mobileResponse, "Não foi possivel CONECTAR ao Servidor " + url + " " + e.getMessage());
        }
        return mobileResponse;
    }

    protected void wrapException(MobileResponse mobileResponse, String message) {
        List resultToReturn;
        resultToReturn = new ArrayList();
        resultToReturn.add(message);
        mobileResponse.setStatus(message);

        if (this.getSendData() != null) {
            for (MobileSendDataListener listener : this.getSendData().getListeners()) {
                listener.onEndServer();
            }
        }
    }

    private List<String[]> formatData(String dadosRetorno) {
        List<String[]> result = new ArrayList<String[]>();
        int i = 0;
        String[] fieldsToReturn;
        StringTokenizer fields;
        for (StringTokenizer records = new StringTokenizer(dadosRetorno, "|"); records.hasMoreTokens(); result
                .add(fieldsToReturn)) {
            fields = new StringTokenizer(records.nextToken(), "$");
            fieldsToReturn = new String[fields.countTokens()];
            for (i = 0; fields.hasMoreTokens(); i++) {
                fieldsToReturn[i] = fields.nextToken();
                if (fieldsToReturn[i].equals("_"))
                    fieldsToReturn[i] = "";
            }
        }
        fields = null;
        fieldsToReturn = null;

        return result;
    }

    public MobileSendData getSendData() {
        return mobileSendData;
    }

    public void setSendData(MobileSendData sendData) {
        this.mobileSendData = sendData;
    }
}