package br.com.anteros.android.security;

import br.com.anteros.security.model.Action;

public interface IResourceSecured {

	public String getResourceName();

	public void enableAction(Action action);

}
