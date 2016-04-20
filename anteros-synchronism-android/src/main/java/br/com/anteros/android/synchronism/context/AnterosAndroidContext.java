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

package br.com.anteros.android.synchronism.context;

import android.app.Application;

import java.io.Serializable;

import br.com.anteros.android.synchronism.communication.SynchronismManager;
import br.com.anteros.persistence.session.SQLSession;
import br.com.anteros.persistence.session.repository.SQLRepository;
import br.com.anteros.persistence.session.repository.impl.GenericSQLRepository;

public abstract class AnterosAndroidContext extends Application {

	private SQLSession session;
	private SynchronismManager synchronismManager;

	@Override
	public void onCreate() {
		super.onCreate();

		try {

			session = onDatabaseConfiguration();
			synchronismManager = onSyncronismConfiguration();

			afterCreate();
		} catch (Exception e) {
			e.printStackTrace();
			onError(e.getMessage());
		}

	}

	public <T extends Serializable, ID extends Serializable> SQLRepository<T, ID> getDao(Class<T> clazz) {
		return new GenericSQLRepository<T, ID>(session, clazz);
	}

	public SQLSession getSession() {
		return session;
	}

	public SynchronismManager getSynchronismManager() {
		return synchronismManager;
	}

	public abstract void onError(String message);

	public abstract void afterCreate();

	public abstract SynchronismManager onSyncronismConfiguration() throws Exception;

	public abstract SQLSession onDatabaseConfiguration() throws Exception;

}
