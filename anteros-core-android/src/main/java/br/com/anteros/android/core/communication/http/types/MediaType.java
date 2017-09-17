package br.com.anteros.android.core.communication.http.types;

/**
 * @author Eduardo Albertini (albertinieduardo@hotmail.com)
 *         Data: 06/07/16.
 */

public enum MediaType {

    ALL {
        @Override
        public String value() {
            return "*/*";
        }
    },

    APPLICATION_ATOM_XML {
        @Override
        public String value() {
            return "application/atom+xml";
        }
    },

    APPLICATION_FORM_URLENCODED {
        @Override
        public String value() {
            return "application/x-www-form-urlencoded";
        }
    },

    APPLICATION_JSON {
        @Override
        public String value() {
            return "application/json";
        }
    },

    APPLICATION_OCTET_STREAM {
        @Override
        public String value() {
            return "application/octet-stream";
        }
    },

    APPLICATION_XHTML_XML {
        @Override
        public String value() {
            return "application/xhtml+xml";
        }
    },

    APPLICATION_XML {
        @Override
        public String value() {
            return "application/xml";
        }
    },

    IMAGE_GIF {
        @Override
        public String value() {
            return "image/gif";
        }
    },

    IMAGE_JPEG {
        @Override
        public String value() {
            return "image/jpeg";
        }
    },

    IMAGE_PNG {
        @Override
        public String value() {
            return "image/png";
        }
    },

    MULTIPART_FORM_DATA {
        @Override
        public String value() {
            return "multipart/form-data";
        }
    },

    TEXT_HTML {
        @Override
        public String value() {
            return "text/html";
        }
    },

    TEXT_PLAIN {
        @Override
        public String value() {
            return "text/plain";
        }
    },

    TEXT_XML {
        @Override
        public String value() {
            return "text/xml";
        }
    };

    public abstract String value();
}
