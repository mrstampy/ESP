package com.github.mrstampy.esp.mutliconnectionsocket;

import java.io.IOException;

import com.github.mrstampy.esp.neurosky.subscription.ThinkGearSocketConnector;

public interface MultiConnectionSocket {

	/**
	 * System property key to control whether broadcasting has been enabled. False
	 * by default. Use '-Dbroadcast.messages=true' to enable socket broadcasting.
	 */
	public static final String BROADCAST_MESSAGES = "broadcast.messages";

	public abstract void addConnectionEventListener(ConnectionEventListener listener);

	public abstract void removeConnectionEventListener(ConnectionEventListener listener);

	public abstract void clearConnectionEventListeners();

	/**
	 * Connects to the ThinkGear socket
	 * 
	 * @throws MultiConnectionSocketException
	 *           if already connected or an unexpected error occurs
	 */
	public abstract void start() throws MultiConnectionSocketException;

	/**
	 * Closes the connection to the ThinkGear socket.
	 */
	public abstract void stop();

	/**
	 * Returns true if connected to the ThinkGear socket
	 * 
	 * @return true if connected
	 */
	public abstract boolean isConnected();

	/**
	 * Binds the broadcaster to the local host and the broadcaster port in
	 * {@link ThinkGearSocketConnector}
	 * 
	 * @throws IOException
	 * @see ThinkGearSocketConnector
	 */
	public abstract void bindBroadcaster() throws IOException;

	/**
	 * Unbinds the broadcaster.
	 * 
	 * @see ThinkGearSocketConnector
	 */
	public abstract void unbindBroadcaster();

	/**
	 * Returns true if the broadcaster is bound.
	 * 
	 * @return
	 * @see ThinkGearSocketConnector
	 */
	public abstract boolean isBound();

	/**
	 * Returns true if this instance has been configured for broadcasting.
	 * 
	 * @return
	 */
	public abstract boolean canBroadcast();

}