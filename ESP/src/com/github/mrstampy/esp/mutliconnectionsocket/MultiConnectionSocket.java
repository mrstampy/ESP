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