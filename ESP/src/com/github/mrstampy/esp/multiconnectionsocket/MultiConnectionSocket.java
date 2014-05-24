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

import java.io.IOException;
import java.util.List;

// TODO: Auto-generated Javadoc
/**
 * The Interface MultiConnectionSocket.
 */
public interface MultiConnectionSocket {

	/**
	 * Adds the connection event listener.
	 *
	 * @param listener
	 *          the listener
	 */
	void addConnectionEventListener(ConnectionEventListener listener);

	/**
	 * Removes the connection event listener.
	 *
	 * @param listener
	 *          the listener
	 */
	void removeConnectionEventListener(ConnectionEventListener listener);

	/**
	 * Clear connection event listeners.
	 */
	void clearConnectionEventListeners();

	/**
	 * Connects to the socket.
	 *
	 * @throws MultiConnectionSocketException
	 *           if already connected or an unexpected error occurs
	 */
	void start() throws MultiConnectionSocketException;

	/**
	 * Closes the connection to the {@link AbstractMultiConnectionSocket} socket.
	 */
	void stop();

	/**
	 * Returns true if connected to the {@link AbstractMultiConnectionSocket}
	 * socket.
	 *
	 * @return true if connected
	 */
	boolean isConnected();

	/**
	 * Binds the broadcaster to the local host and the broadcaster port in
	 * {@link AbstractSocketConnector}.
	 *
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 * @see AbstractSocketConnector
	 */
	void bindBroadcaster() throws IOException;

	/**
	 * Unbinds the broadcaster.
	 * 
	 * @see AbstractSocketConnector
	 */
	void unbindBroadcaster();

	/**
	 * Returns true if the broadcaster is bound.
	 *
	 * @return true, if is bound
	 * @see AbstractSocketConnector
	 */
	boolean isBound();

	/**
	 * Returns true if this instance has been configured for broadcasting.
	 *
	 * @return true, if successful
	 */
	boolean canBroadcast();

	/**
	 * Returns the number of channels.
	 *
	 * @return the num channels
	 */
	int getNumChannels();

	/**
	 * Returns the {@link EspChannel} representations processed by the
	 * implementation.
	 *
	 * @return the channels
	 */
	List<EspChannel> getChannels();

	/**
	 * Returns the {@link EspChannel} representation specified by the channel
	 * number.
	 *
	 * @param channelNumber the channel number
	 * @return the channel
	 */
	EspChannel getChannel(int channelNumber);

}