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


@SuppressWarnings("serial")
public class ConversionException extends RuntimeException {

    public ConversionException(String message) {
        super(message);
    }


    public ConversionException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }


    public ConversionException(Throwable cause) {
        super(cause.getMessage());
        this.cause = cause;
    }


    protected Throwable cause = null;

    public Throwable getCause() {
        return (this.cause);
    }


}
