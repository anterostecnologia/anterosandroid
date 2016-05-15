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
 * Created by edson on 12/05/16.
 */
public class PostmonResponse {


    /**
     * cep : 87260000
     * cidade : Araruna
     * cidade_info : {"area_km2":"493,19","codigo_ibge":"4101705"}
     * estado : PR
     * estado_info : {"area_km2":"199.307,945","codigo_ibge":"41","nome":"Paraná"}
     */

    private String cep;
    private String cidade;
    /**
     * area_km2 : 493,19
     * codigo_ibge : 4101705
     */

    private CidadeInfoBean cidade_info;
    private String estado;
    /**
     * area_km2 : 199.307,945
     * codigo_ibge : 41
     * nome : Paraná
     */

    private EstadoInfoBean estado_info;

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public CidadeInfoBean getCidade_info() {
        return cidade_info;
    }

    public void setCidade_info(CidadeInfoBean cidade_info) {
        this.cidade_info = cidade_info;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public EstadoInfoBean getEstado_info() {
        return estado_info;
    }

    public void setEstado_info(EstadoInfoBean estado_info) {
        this.estado_info = estado_info;
    }

    public static class CidadeInfoBean {
        private String area_km2;
        private String codigo_ibge;

        public String getArea_km2() {
            return area_km2;
        }

        public void setArea_km2(String area_km2) {
            this.area_km2 = area_km2;
        }

        public String getCodigo_ibge() {
            return codigo_ibge;
        }

        public void setCodigo_ibge(String codigo_ibge) {
            this.codigo_ibge = codigo_ibge;
        }
    }

    public static class EstadoInfoBean {
        private String area_km2;
        private String codigo_ibge;
        private String nome;

        public String getArea_km2() {
            return area_km2;
        }

        public void setArea_km2(String area_km2) {
            this.area_km2 = area_km2;
        }

        public String getCodigo_ibge() {
            return codigo_ibge;
        }

        public void setCodigo_ibge(String codigo_ibge) {
            this.codigo_ibge = codigo_ibge;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }
    }
}
