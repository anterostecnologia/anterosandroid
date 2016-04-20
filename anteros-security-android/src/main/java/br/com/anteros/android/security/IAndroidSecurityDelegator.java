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

package br.com.anteros.android.security;

import java.util.Set;

import br.com.anteros.security.model.Action;
import br.com.anteros.security.model.Resource;
import br.com.anteros.security.model.System;

public interface IAndroidSecurityDelegator {
	
	public System getSystemByName(String systemName);

	public Resource getResourceByName(System system, String resouceName);

	public Resource addResource(System system, String resourceName, String resourceDescription);

	public Action addAction(System system, Resource resource, String actionName, String actionCategory,
			String actionDescription, String actionVersion);

	public void saveAction(Action action);

	public Resource refreshResource(Resource resource);
	
	public void removeActionByAllUsers(Action action);

	public System addSystem(String systemName, String description);

	public Set<Resource> getResourcesBySystem(System system);
	
	public boolean isActionEnabled(String actionName);

}
