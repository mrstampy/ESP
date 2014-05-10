/*
 * ESP Copyright (C) 2013 - 2014 Burton Alexander
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 */
package com.github.mrstampy.esp.multiconnectionsocket;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
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

	/**
	 * Instantiates a new host port.
	 *
	 * @param host the host
	 * @param port the port
	 */
	public HostPort(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * Gets the host.
	 *
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (!(o instanceof HostPort)) return false;

		HostPort that = (HostPort) o;

		return that.port == port && that.host.equals(host);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return host.hashCode() + (17 * port);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return host + ":" + port;
	}

}
