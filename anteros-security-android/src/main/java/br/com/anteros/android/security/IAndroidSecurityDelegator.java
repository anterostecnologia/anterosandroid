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
