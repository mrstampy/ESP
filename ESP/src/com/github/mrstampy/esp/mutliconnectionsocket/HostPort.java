package com.github.mrstampy.esp.mutliconnectionsocket;

import java.io.Serializable;

/**
 * Convenience class to identify connected subscribers.
 * 
 * @author burton
 * @see AbstractSocketConnector
 * @see AbstractSubscriptionHandlerAdapter
 */
public class HostPort implements Serializable {

	private static final long serialVersionUID = -5042181782429276288L;

	private final String host;
	private final int port;

	public HostPort(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public boolean equals(Object o) {
		if (!(o instanceof HostPort)) return false;

		HostPort that = (HostPort) o;

		return that.port == port && that.host.equals(host);
	}

	public int hashCode() {
		return host.hashCode() + (17 * port);
	}

	public String toString() {
		return host + ":" + port;
	}

}
