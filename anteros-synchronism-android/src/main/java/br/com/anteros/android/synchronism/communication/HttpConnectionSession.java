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
