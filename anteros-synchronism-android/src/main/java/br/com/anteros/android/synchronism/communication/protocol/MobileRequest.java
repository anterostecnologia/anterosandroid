package br.com.anteros.android.synchronism.communication.protocol;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import br.com.anteros.core.log.Logger;
import br.com.anteros.core.log.LoggerProvider;

public class MobileRequest {

	private static Logger LOG = LoggerProvider.getInstance().getLogger(MobileRequest.class.getName());

	public static final String ACTION_QUEUE = "AQ";
	public static final String ACTION_EXECUTE_QUEUE = "AE";
	public static final String ACTION_EXECUTE_IMMEDIATE = "AI";
	private String application;
	private String userAgent = "Android";
	private String clientId;
	private String requestMode = ACTION_EXECUTE_IMMEDIATE;
	private List<MobileAction> actions = new ArrayList<MobileAction>();
	private String name;
	private String requestId = "unknow";
	private StringBuffer sb = new StringBuffer();

	public MobileRequest(String requestId, String name, String application, String userAgent, String clientId,
			String requestMode) {
		this(name);
		this.application = application;
		this.userAgent = userAgent;
		this.clientId = clientId;
		this.requestMode = requestMode;
		this.requestId = requestId;
	}

	public MobileRequest(String name, String application, String userAgent, String clientId, String requestMode) {
		this(name);
		this.application = application;
		this.userAgent = userAgent;
		this.clientId = clientId;
		this.requestMode = requestMode;
	}

	public MobileRequest(String name) {
		this.application = "";
		this.clientId = "";
		this.requestMode = "";
		this.name = name;
	}

	public void setFormattedHeader(String header) {
		StringTokenizer token = new StringTokenizer(header, "|");

		String[] result = new String[token.countTokens() + 1];
		int x = 0;
		while (token.hasMoreTokens()) {
			result[x] = token.nextToken();
			x++;
		}

		if (result.length == 1)
			setApplication(result[0]);
		if (result.length == 2)
			setUserAgent(result[1]);
		if (result.length == 3)
			setClientId(result[2]);
		setRequestMode(result[3]);

		token = null;
		result = null;

	}

	public String getFormattedHeader() {
		sb.delete(0, sb.length());
		sb.append(getApplication());
		sb.append("|");
		sb.append(getUserAgent());
		sb.append("|");
		sb.append(getClientId());
		sb.append("|");
		sb.append(getRequestMode());

		String result = sb.toString();
		sb.delete(0, sb.length());

		return result;
	}

	public List<MobileAction> getActions() {
		return actions;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}

	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public void addAction(MobileAction mobileAction) {
		actions.add(mobileAction);
	}

	public void removeMobileAction(MobileAction mobileAction) {
		actions.remove(mobileAction);
	}

	public MobileAction getMobileAction(String name) {
		for (int i = 0; i < actions.size(); i++) {
			MobileAction mobileAction = (MobileAction) actions.get(i);
			if (mobileAction.getName().equals(name))
				return mobileAction;
		}
		return null;

	}

	public MobileAction getMobileAction(int key) {
		return (MobileAction) actions.get(key);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public List getFormatedActions() {
		List vector = new ArrayList();
		for (int i = 0; i < actions.size(); i++) {
			MobileAction action = (MobileAction) actions.get(i);
			List actionFields = action.getFormatedParameters();
			for (int j = 0; j < actionFields.size(); j++)
				vector.add(actionFields.get(j));
		}

		return vector;
	}

	@SuppressWarnings("rawtypes")
	public void setFormattedActions(List vector) {
		String[] params;
		for (int i = 0; i < vector.size(); i++) {
			String[] action = (String[]) vector.get(i);
			if (!(action == null)) {
				MobileAction mobileAction = new MobileAction(action[0]);
				params = null;
				params = new String[action.length - 1];
				for (int j = 1; j < action.length; j++)
					params[j - 1] = action[j];
				mobileAction.addParameter(params);
				actions.add(mobileAction);
			}
		}

	}

	public void showDetails() {

		LOG.info("Application= " + getApplication());
		LOG.info("User Agent= " + getUserAgent());
		LOG.info("ClientId= " + getClientId());
		LOG.info("Request Mode= " + getRequestMode());
		for (int x = 0; x < this.getActions().size(); x++) {
			MobileAction mobileAction = (MobileAction) this.getActions().get(x);
			mobileAction.showDetails();
		}
	}

	public String getRequestMode() {
		return requestMode;
	}

	public void setRequestMode(String requestMode) {
		this.requestMode = requestMode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}
}
