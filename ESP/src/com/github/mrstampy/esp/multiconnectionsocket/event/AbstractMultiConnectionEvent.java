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
package com.github.mrstampy.esp.multiconnectionsocket.event;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractMultiConnectionEvent.
 *
 * @param <E> the element type
 */
public abstract class AbstractMultiConnectionEvent<E extends Enum<E>> implements Serializable {
	private static final long serialVersionUID = 8285549904986718450L;

	private final long createTimestamp = System.currentTimeMillis();

	private final long nanoTime = System.nanoTime();

	private final E eventType;

	/**
	 * Instantiates a new abstract multi connection event.
	 *
	 * @param type the type
	 */
	protected AbstractMultiConnectionEvent(E type) {
		this.eventType = type;
	}

	/**
	 * Gets the event type.
	 *
	 * @return the event type
	 */
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
	 * other {@link AbstractMultiConnectionEvent#nanoTime}s
	 * 
	 * @return The nano time this event was created
	 * @see System#nanoTime()
	 */
	public long getNanoTime() {
		return nanoTime;
	}

}
