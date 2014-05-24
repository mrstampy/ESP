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

import java.io.Serializable;

import com.github.mrstampy.esp.dsp.lab.Lab;

/**
 * The Class EspChannel represents a channel of raw data available for
 * processing.
 * 
 * @see Lab
 */
public class EspChannel implements Serializable {
	private static final long serialVersionUID = -8204523312170181461L;

	private int channelNumber;
	private String description;
	private String identifier;

	/**
	 * Instantiates a new esp channel.
	 */
	public EspChannel() {
		this(1);
	}

	/**
	 * Instantiates a new esp channel.
	 *
	 * @param channelNumber
	 *          the channel number
	 */
	public EspChannel(int channelNumber) {
		this(channelNumber, null);
	}

	/**
	 * Instantiates a new esp channel.
	 *
	 * @param channelNumber
	 *          the channel number
	 * @param description
	 *          the description
	 */
	public EspChannel(int channelNumber, String description) {
		this(channelNumber, description, null);
	}

	/**
	 * Instantiates a new esp channel.
	 *
	 * @param channelNumber
	 *          the channel number
	 * @param description
	 *          the description
	 * @param identifier
	 *          the identifier
	 */
	public EspChannel(int channelNumber, String description, String identifier) {
		setChannelNumber(channelNumber);
		setDescription(description);
		setIdentifier(identifier);
	}

	/**
	 * Gets the channel number.
	 *
	 * @return the channel number
	 */
	public int getChannelNumber() {
		return channelNumber;
	}

	/**
	 * Sets the channel number.
	 *
	 * @param channelNumber
	 *          the new channel number
	 */
	public void setChannelNumber(int channelNumber) {
		this.channelNumber = channelNumber;
	}

	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Sets the description.
	 *
	 * @param description
	 *          the new description
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Gets the identifier.
	 *
	 * @return the identifier
	 */
	public String getIdentifier() {
		return identifier;
	}

	/**
	 * Sets the identifier.
	 *
	 * @param identifier
	 *          the new identifier
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	
	@Override
	public String toString() {
		return getDescription();
	}

}
