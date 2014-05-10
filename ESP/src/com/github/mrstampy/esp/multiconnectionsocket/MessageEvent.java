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

// TODO: Auto-generated Javadoc
/**
 * Disruptor event to pass EEG device messages from the device to the
 * {@link MultiConnectionSocket}'s message processing.
 *
 * @author burton
 * @param <MESSAGE> the generic type
 */
public class MessageEvent<MESSAGE> {

	private MESSAGE message;

	/**
	 * Gets the message.
	 *
	 * @return the message
	 */
	public MESSAGE getMessage() {
		return message;
	}

	/**
	 * Sets the message.
	 *
	 * @param message the new message
	 */
	public void setMessage(MESSAGE message) {
		this.message = message;
	}

}
