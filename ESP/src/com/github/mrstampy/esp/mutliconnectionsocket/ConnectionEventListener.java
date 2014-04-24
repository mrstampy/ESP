package com.github.mrstampy.esp.mutliconnectionsocket;

import java.util.EventListener;

/**
 * Implement and add to
 * {@link AbstractMultiConnectionSocket#addConnectionEventListener(ConnectionEventListener)}
 * to receive connected state events
 * 
 * @author burton
 */
public interface ConnectionEventListener extends EventListener {

	/**
	 * Invoked when the connected state of the
	 * {@link AbstractMultiConnectionSocket} changes
	 * 
	 * @param e
	 */
	void connectionEventPerformed(ConnectionEvent e);

}
