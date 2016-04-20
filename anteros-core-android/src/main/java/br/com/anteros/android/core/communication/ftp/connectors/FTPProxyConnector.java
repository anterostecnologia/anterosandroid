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
package br.com.anteros.android.core.communication.ftp.connectors;

import java.io.IOException;
import java.net.Socket;

import br.com.anteros.android.core.communication.ftp.FTPCommunicationChannel;
import br.com.anteros.android.core.communication.ftp.FTPConnector;
import br.com.anteros.android.core.communication.ftp.FTPIllegalReplyException;
import br.com.anteros.android.core.communication.ftp.FTPReply;

/**
 * This one connects a remote host via a FTP proxy which supports the SITE or
 * the OPEN proxy method.
 * 
 * The connector's default value for the
 * <em>useSuggestedAddressForDataConnections</em> flag is <em>true</em>.
 * 
 * @author Carlo Pelliccia
 */
public class FTPProxyConnector extends FTPConnector {

	/**
	 * Requires the connection to the remote host through a SITE command after
	 * proxy authentication. Default one.
	 */
	public static int STYLE_SITE_COMMAND = 0;

	/**
	 * Requires the connection to the remote host through a OPEN command without
	 * proxy authentication.
	 */
	public static int STYLE_OPEN_COMMAND = 1;

	/**
	 * The proxy host name.
	 */
	private String proxyHost;

	/**
	 * The proxy port.
	 */
	private int proxyPort;

	/**
	 * The proxyUser for proxy authentication.
	 */
	private String proxyUser;

	/**
	 * The proxyPass for proxy authentication.
	 */
	private String proxyPass;

	/**
	 * The style used by the proxy.
	 */
	public int style = STYLE_SITE_COMMAND;

	/**
	 * Builds the connector.
	 * 
	 * Default value for the style is STYLE_SITE_COMMAND.
	 * 
	 * @param proxyHost
	 *            The proxy host name.
	 * @param proxyPort
	 *            The proxy port.
	 * @param proxyUser
	 *            The username for proxy authentication.
	 * @param proxyPass
	 *            The password for proxy authentication.
	 */
	public FTPProxyConnector(String proxyHost, int proxyPort, String proxyUser,
			String proxyPass) {
		super(true);
		this.proxyHost = proxyHost;
		this.proxyPort = proxyPort;
		this.proxyUser = proxyUser;
		this.proxyPass = proxyPass;
	}

	/**
	 * Builds the connector.
	 * 
	 * Default value for the style is STYLE_SITE_COMMAND.
	 * 
	 * @param proxyHost
	 *            The proxy host name.
	 * @param proxyPort
	 *            The proxy port.
	 */
	public FTPProxyConnector(String proxyHost, int proxyPort) {
		this(proxyHost, proxyPort, "anonymous", "ftp4j");
	}

	/**
	 * Sets the style used by the proxy.
	 * 
	 * {@link FTPProxyConnector#STYLE_SITE_COMMAND} - Requires the connection to
	 * the remote host through a SITE command after proxy authentication.
	 * 
	 * {@link FTPProxyConnector#STYLE_OPEN_COMMAND} - Requires the connection to
	 * the remote host through a OPEN command without proxy authentication.
	 * 
	 * Default value for the style is STYLE_SITE_COMMAND.
	 * 
	 * @param style
	 *            The style.
	 * @see FTPProxyConnector#STYLE_SITE_COMMAND
	 * @see FTPProxyConnector#STYLE_OPEN_COMMAND
	 */
	public void setStyle(int style) {
		if (style != STYLE_OPEN_COMMAND && style != STYLE_SITE_COMMAND) {
			throw new IllegalArgumentException("Invalid style");
		}
		this.style = style;
	}

	public Socket connectForCommunicationChannel(String host, int port)
			throws IOException {
		Socket socket = tcpConnectForCommunicationChannel(proxyHost, proxyPort);
		FTPCommunicationChannel communication = new FTPCommunicationChannel(
				socket, "ASCII");
		// Welcome message.
		FTPReply r;
		try {
			r = communication.readFTPReply();
		} catch (FTPIllegalReplyException e) {
			throw new IOException("Invalid proxy response");
		}
		// Does this reply mean "ok"?
		if (r.getCode() != 220) {
			// Mmmmm... it seems no!
			throw new IOException("Invalid proxy response");
		}
		if (style == STYLE_SITE_COMMAND) {
			// Usefull flags.
			boolean passwordRequired;
			// Send the user and read the reply.
			communication.sendFTPCommand("USER " + proxyUser);
			try {
				r = communication.readFTPReply();
			} catch (FTPIllegalReplyException e) {
				throw new IOException("Invalid proxy response");
			}
			switch (r.getCode()) {
			case 230:
				// Password isn't required.
				passwordRequired = false;
				break;
			case 331:
				// Password is required.
				passwordRequired = true;
				break;
			default:
				// User validation failed.
				throw new IOException("Proxy authentication failed");
			}
			// Password.
			if (passwordRequired) {
				// Send the password.
				communication.sendFTPCommand("PASS " + proxyPass);
				try {
					r = communication.readFTPReply();
				} catch (FTPIllegalReplyException e) {
					throw new IOException("Invalid proxy response");
				}
				if (r.getCode() != 230) {
					// Authentication failed.
					throw new IOException("Proxy authentication failed");
				}
			}
			communication.sendFTPCommand("SITE " + host + ":" + port);
		} else if (style == STYLE_OPEN_COMMAND) {
			communication.sendFTPCommand("OPEN " + host + ":" + port);
		}
		return socket;
	}

	public Socket connectForDataTransferChannel(String host, int port)
			throws IOException {
		return tcpConnectForDataTransferChannel(host, port);
	}

}
