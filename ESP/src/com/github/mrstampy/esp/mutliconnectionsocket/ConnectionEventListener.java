package com.github.mrstampy.esp.mutliconnectionsocket;

import java.util.EventListener;

import com.github.mrstampy.esp.neurosky.MultiConnectionThinkGearSocket;

/**
 * Implement and add to
 * {@link MultiConnectionThinkGearSocket#addConnectionEventListener(ConnectionEventListener)}
 * to receive connected state events
 * 
 * @author burton
 */
public interface ConnectionEventListener extends EventListener {

	/**
	 * Invoked when the connected state of the
	 * {@link MultiConnectionThinkGearSocket} changes
	 * 
	 * @param e
	 */
	void connectionEventPerformed(ConnectionEvent e);

}
