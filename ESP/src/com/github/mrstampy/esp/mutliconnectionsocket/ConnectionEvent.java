package com.github.mrstampy.esp.mutliconnectionsocket;

import java.util.EventObject;

import com.github.mrstampy.esp.neurosky.MultiConnectionThinkGearSocket;
import com.github.mrstampy.esp.neurosky.subscription.ThinkGearSocketConnector;

/**
 * Event object created when the connected state of the
 * {@link MultiConnectionThinkGearSocket} changes.
 * 
 * @author burton
 * @see ConnectionEventListener
 * @see MultiConnectionThinkGearSocket
 */
public class ConnectionEvent extends EventObject {
	private static final long serialVersionUID = -1126025476117888892L;

	public enum State {
		STARTED,
		STOPPED,
		BOUND,
		UNBOUND;
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
	 * Returns true if {@link MultiConnectionThinkGearSocket#start()} was executed
	 * and completed successfully ie. a connection to the ThinkGear socket was
	 * created.
	 * 
	 * @return true if started
	 */
	public boolean isStarted() {
		return State.STARTED.equals(getState());
	}

	/**
	 * Returns true if {@link MultiConnectionThinkGearSocket#stop()} was executed
	 * and completed successfully ie. a connection to the ThinkGear socket was
	 * closed.
	 * 
	 * @return true if stopped
	 */
	public boolean isStopped() {
		return State.STOPPED.equals(getState());
	}

	/**
	 * Returns true if {@link MultiConnectionThinkGearSocket#bindBroadcaster()}
	 * was executed and completed successfully ie. the
	 * {@link MultiConnectionThinkGearSocket} is ready to broadcast messages to
	 * connected {@link ThinkGearSocketConnector}s.
	 * 
	 * @return true if bound
	 */
	public boolean isBound() {
		return State.BOUND.equals(getState());
	}

	/**
	 * Returns true if {@link MultiConnectionThinkGearSocket#unbindBroadcaster()}
	 * was executed and completed successfully ie. the
	 * {@link MultiConnectionThinkGearSocket} can no longer broadcast messages to
	 * connected {@link ThinkGearSocketConnector}s.
	 * 
	 * @return true if unbound
	 */
	public boolean isUnbound() {
		return State.UNBOUND.equals(getState());
	}

}
