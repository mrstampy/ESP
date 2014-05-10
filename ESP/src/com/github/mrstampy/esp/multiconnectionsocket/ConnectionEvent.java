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

import java.util.EventObject;

// TODO: Auto-generated Javadoc
/**
 * Event object created when the connected state of the
 * {@link AbstractMultiConnectionSocket} changes.
 * 
 * @author burton
 * @see ConnectionEventListener
 * @see AbstractMultiConnectionSocket
 */
public class ConnectionEvent extends EventObject {
	private static final long serialVersionUID = 4998624451690389607L;

	/**
	 * The Enum State.
	 */
	public enum State {
		
		/** The started. */
		STARTED, 
 /** The stopped. */
 STOPPED, 
 /** The bound. */
 BOUND, 
 /** The unbound. */
 UNBOUND, 
 /** The error stopped. */
 ERROR_STOPPED, 
 /** The error unbound. */
 ERROR_UNBOUND;
	}

	private final State state;

	/**
	 * Instantiates a new connection event.
	 *
	 * @param source the source
	 * @param state the state
	 */
	public ConnectionEvent(Object source, State state) {
		super(source);

		this.state = state;
	}

	/**
	 * Gets the state.
	 *
	 * @return the state
	 */
	public State getState() {
		return state;
	}

}
