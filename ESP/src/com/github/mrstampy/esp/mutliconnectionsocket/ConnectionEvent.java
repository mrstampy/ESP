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
