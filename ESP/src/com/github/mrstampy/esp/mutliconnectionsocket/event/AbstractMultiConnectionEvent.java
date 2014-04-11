package com.github.mrstampy.esp.mutliconnectionsocket.event;

import java.io.Serializable;

import com.github.mrstampy.esp.neurosky.event.AbstractThinkGearEvent;

public abstract class AbstractMultiConnectionEvent<E extends Enum<E>> implements Serializable {
	private static final long serialVersionUID = 8285549904986718450L;

	private final long createTimestamp = System.currentTimeMillis();

	private final long nanoTime = System.nanoTime();

	private final E eventType;

	protected AbstractMultiConnectionEvent(E type) {
		this.eventType = type;
	}

	public E getEventType() {
		return eventType;
	}

	/**
	 * Returns the time this event was created, use in lieu of received time as
	 * network latency may skew any time-based calculations.
	 * 
	 * @return The current timestamp this event was created
	 */
	public long getCreateTimestamp() {
		return createTimestamp;
	}

	/**
	 * "This method can only be used to measure elapsed time and is not related to
	 * any other notion of system or wall-clock time." Use only in comparison to
	 * other {@link AbstractThinkGearEvent#nanoTime}s
	 * 
	 * @return The nano time this event was created
	 * @see System#nanoTime()
	 */
	public long getNanoTime() {
		return nanoTime;
	}

}
