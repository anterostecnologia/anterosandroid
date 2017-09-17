package br.com.anteros.android.core.communication.http.types;

/**
 * @author Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Data: 06/07/16.
 */

public enum HttpMethod {
    DELETE {
        @Override
        public String value() {
            return "DELETE";
        }
    },

    GET {
        @Override
        public String value() {
            return "GET";
        }
    },

    HEAD {
        @Override
        public String value() {
            return "HEAD";
        }
    },

    OPTIONS {
        @Override
        public String value() {
            return "OPTIONS";
        }
    },

    PATCH {
        @Override
        public String value() {
            return "PATCH";
        }
    },

    POST {
        @Override
        public String value() {
            return "POST";
        }
    },

    PUT {
        @Override
        public String value() {
            return "PUT";
        }
    },

    TRACE {
        @Override
        public String value() {
            return "TRACE";
        }
    };

    public abstract String value();
}
