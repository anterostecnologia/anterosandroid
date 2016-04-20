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

/**
 * 
 * @author Edson Martins
 */
public class HttpConnectionSession {

	private String sessionId = "";

	private static HttpConnectionSession session;

	public static HttpConnectionSession getInstance() {
		if (session == null) {
			session = new HttpConnectionSession();
		}
		return session;
	}

	public static void destroyCurrentSession() {
		session = null;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}
