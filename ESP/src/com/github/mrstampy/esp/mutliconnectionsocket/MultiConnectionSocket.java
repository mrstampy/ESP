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
package com.github.mrstampy.esp.mutliconnectionsocket;

import java.io.IOException;

public interface MultiConnectionSocket {

	public abstract void addConnectionEventListener(ConnectionEventListener listener);

	public abstract void removeConnectionEventListener(ConnectionEventListener listener);

	public abstract void clearConnectionEventListeners();

	/**
	 * Connects to the socket
	 * 
	 * @throws MultiConnectionSocketException
	 *           if already connected or an unexpected error occurs
	 */
	public abstract void start() throws MultiConnectionSocketException;

	/**
	 * Closes the connection to the {@link AbstractMultiConnectionSocket} socket.
	 */
	public abstract void stop();

	/**
	 * Returns true if connected to the {@link AbstractMultiConnectionSocket}
	 * socket
	 * 
	 * @return true if connected
	 */
	public abstract boolean isConnected();

	/**
	 * Binds the broadcaster to the local host and the broadcaster port in
	 * {@link AbstractSocketConnector}
	 * 
	 * @throws IOException
	 * @see AbstractSocketConnector
	 */
	public abstract void bindBroadcaster() throws IOException;

	/**
	 * Unbinds the broadcaster.
	 * 
	 * @see AbstractSocketConnector
	 */
	public abstract void unbindBroadcaster();

	/**
	 * Returns true if the broadcaster is bound.
	 * 
	 * @return
	 * @see AbstractSocketConnector
	 */
	public abstract boolean isBound();

	/**
	 * Returns true if this instance has been configured for broadcasting.
	 * 
	 * @return
	 */
	public abstract boolean canBroadcast();

}