package br.com.anteros.android.security;

import java.lang.reflect.Method;
import java.util.List;

import br.com.anteros.android.security.annotation.ActionSecured;
import br.com.anteros.android.security.annotation.ResourceSecured;
import br.com.anteros.core.utils.ReflectionUtils;
import br.com.anteros.security.model.Action;
import br.com.anteros.security.model.Resource;
import br.com.anteros.security.model.System;

public class AndroidSecurityManager {

	protected String systemName;
	protected String description;
	protected String version;
	protected IAndroidSecurityDelegator delegator;
	private System system;

	public AndroidSecurityManager(String systemName, String description, String version,
			IAndroidSecurityDelegator delegator, Class<?>... classes) throws AnterosSecurityException {
		this.systemName = systemName;
		this.description = description;
		this.version = version;
		this.delegator = delegator;

		loadSecuredResourcesAndActions(classes);
	}

	public void enableActions(IResourceSecured resource) {
		if (system.getRecursos() == null)
			system.setRecursos(delegator.getResourcesBySystem(system));

		for (Resource res : system.getRecursos()) {
			if (res.getNome().equals(resource.getResourceName())) {
				if (res.getAcoes() != null) {
					for (Action action : res.getAcoes()) {
						resource.enableAction(action);
					}
				}
			}
		}
	}

	protected void loadSecuredResourcesAndActions(Class<?>... classes) throws AnterosSecurityException {
		try {
			Action action = null;
			Resource resource = null;
			system = delegator.getSystemByName(systemName);
			if (system == null) {
				system = delegator.addSystem(systemName, description);
			}
			for (Class<?> cl : classes) {
				if (cl.isAnnotationPresent(ResourceSecured.class)) {
					ResourceSecured resourceSecured = cl.getAnnotation(ResourceSecured.class);

					resource = delegator.getResourceByName(system, resourceSecured.resourceName());
					if (resource == null) {
						resource = delegator.addResource(system, resourceSecured.resourceName(),
								resourceSecured.description());
						delegator.refreshResource(resource);
					}

					/*
					 * Verifica ações declaradas e não salvas ou inativas
					 */
					Method[] methods = ReflectionUtils.getAllDeclaredMethods(cl);

					for (Method method : methods) {
						if (method.isAnnotationPresent(ActionSecured.class)) {
							boolean found = false;
							boolean active = false;
							action = null;
							ActionSecured actionSecured = method.getAnnotation(ActionSecured.class);
							if (resource.getAcoes() != null) {
								for (Action act : resource.getAcoes()) {
									if (act.getNome().equalsIgnoreCase(actionSecured.actionName())) {
										found = true;
										active = act.getAtiva();
										action = act;
										break;
									}
								}
							}

							if (!found) {
								action = delegator.addAction(system, resource, actionSecured.actionName(),
										actionSecured.category(), actionSecured.description(), version);
							} else {
								if (action != null) {
									boolean save = false;
									if (!active) {
										action.setAtiva(true);
										save = true;
									}
									if (!(action.getCategoria().equalsIgnoreCase(actionSecured.category()))) {
										action.setCategoria(actionSecured.category());
										save = true;
									}
									if (save) {
										delegator.saveAction(action);
									}
								}
							}
						}
					}

					/*
					 * Verifica ações salvas e não existentes mais no recurso.
					 */
					resource = delegator.refreshResource(resource);

					if (resource.getAcoes() != null) {
						for (Action act : resource.getAcoes()) {
							boolean found = false;
							for (Method method : methods) {
								if (method.isAnnotationPresent(ActionSecured.class)) {
									ActionSecured actionSecured = method.getAnnotation(ActionSecured.class);
									if (actionSecured.actionName().equalsIgnoreCase(act.getNome())) {
										found = true;
										break;
									}
								}
							}

							if (!found) {
								if (act.getVersao().compareTo(version) <= 0) {
									delegator.removeActionByAllUsers(act);
								}
							}
						}
					}
				}
			}
		} catch (Exception e) {
			throw new AnterosSecurityException(e);
		}
	}

}
