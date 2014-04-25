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

import java.util.EventObject;

/**
 * Event object created when the connected state of the
 * {@link AbstractMultiConnectionSocket} changes.
 * 
 * @author burton
 * @see ConnectionEventListener
 * @see AbstractMultiConnectionSocket
 */
public class ConnectionEvent extends EventObject {
	private static final long serialVersionUID = -1126025476117888892L;

	public enum State {
		STARTED, STOPPED, BOUND, UNBOUND;
	}

	private final State state;

	public ConnectionEvent(Object source, State state) {
		super(source);

		this.state = state;
	}

	public State getState() {
		return state;
	}

	/**
	 * Returns true if {@link AbstractMultiConnectionSocket#start()} was executed
	 * and completed successfully ie. a connection to the socket was
	 * created.
	 * 
	 * @return true if started
	 */
	public boolean isStarted() {
		return State.STARTED.equals(getState());
	}

	/**
	 * Returns true if {@link AbstractMultiConnectionSocket#stop()} was executed
	 * and completed successfully ie. a connection to the socket was
	 * closed.
	 * 
	 * @return true if stopped
	 */
	public boolean isStopped() {
		return State.STOPPED.equals(getState());
	}

	/**
	 * Returns true if {@link AbstractMultiConnectionSocket#bindBroadcaster()}
	 * was executed and completed successfully ie. the
	 * {@link AbstractMultiConnectionSocket} is ready to broadcast messages to
	 * connected {@link AbstractSocketConnector}s.
	 * 
	 * @return true if bound
	 */
	public boolean isBound() {
		return State.BOUND.equals(getState());
	}

	/**
	 * Returns true if {@link AbstractMultiConnectionSocket#unbindBroadcaster()}
	 * was executed and completed successfully ie. the
	 * {@link AbstractMultiConnectionSocket} can no longer broadcast messages to
	 * connected {@link AbstractSocketConnector}s.
	 * 
	 * @return true if unbound
	 */
	public boolean isUnbound() {
		return State.UNBOUND.equals(getState());
	}

}
