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

/**
 * Created by edson on 21/05/16.
 */

import java.util.Arrays;
import java.util.Hashtable;

import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;

public final class EnderecoCorreio extends SoapObject {
    private String bairro;

    private String cep;

    private String cidade;

    private String complemento;

    private String complemento2;

    private String end;

    private long id;

    private String uf;

    private EnderecoCorreio() {
        super("", "");
    }

    public EnderecoCorreio(SoapObject response) {
        int len = response.getPropertyCount();
        for (int i = 0; i < len; i++) {
            if (!(response.getProperty(i).toString().equals("anyType{}"))) {
                this.setProperty(i, response.getProperty(i));
            }
        }
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getBairro() {
        return this.bairro;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCep() {
        return this.cep;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getCidade() {
        return this.cidade;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getComplemento() {
        return this.complemento;
    }

    public void setComplemento2(String complemento2) {
        this.complemento2 = complemento2;
    }

    public String getComplemento2() {
        return this.complemento2;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getEnd() {
        return this.end;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getId(long id) {
        return this.id;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getUf() {
        return this.uf;
    }

    public int getPropertyCount() {
        return 9;
    }

    public Object getProperty(int index) {
        switch (index) {
            case 0:
                return bairro;
            case 1:
                return cep;
            case 2:
                return cidade;
            case 3:
                return complemento;
            case 4:
                return complemento2;
            case 5:
                return end;
            case 6:
                return new Long(id);
            case 7:
                return uf;
        }
        return null;
    }

    public void setProperty(int index, Object obj) {
        if (obj==null || obj.toString().equals("anyType{}"))
            return;

        switch (index) {
            case 0:
                bairro = obj.toString();
                break;
            case 1:
                cep = obj.toString();
                break;
            case 2:
                cidade = obj.toString();
                break;
            case 3:
                complemento = obj.toString();
                break;
            case 4:
                complemento2 = obj.toString();
                break;
            case 5:
                end = obj.toString();
                break;
            case 6:
                id = Long.parseLong(obj.toString());
                break;
            case 7:
                uf = obj.toString();
                break;
        }
    }

    @Override
    public String toString() {
        return "EnderecoCorreio{" +
                "bairro='" + bairro + '\'' +
                ", cep='" + cep + '\'' +
                ", cidade='" + cidade + '\'' +
                ", complemento='" + complemento + '\'' +
                ", complemento2='" + complemento2 + '\'' +
                ", end='" + end + '\'' +
                ", id=" + id +
                ", uf='" + uf + '\'' +
                '}';
    }

    public void getPropertyInfo(int index, Hashtable table, PropertyInfo info) {
        switch (index) {
            case 0:
                info.name = "bairro";
                info.type = String.class;
                break;
            case 1:
                info.name = "cep";
                info.type = String.class;
                break;
            case 2:
                info.name = "cidade";
                info.type = String.class;
                break;
            case 3:
                info.name = "complemento";
                info.type = String.class;
                break;
            case 4:
                info.name = "complemento2";
                info.type = String.class;
                break;
            case 5:
                info.name = "end";
                info.type = String.class;
                break;
            case 6:
                info.name = "id";
                info.type = Long.class;
                break;
            case 7:
                info.name = "uf";
                info.type = String.class;
                break;
        }
    }


    public static EnderecoCorreio of(SoapObject response) {
        return new EnderecoCorreio(response);
    }



}

