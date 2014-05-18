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
package com.github.mrstampy.esp.dsp.lab;

import com.github.mrstampy.esp.dsp.AbstractDSPValues;
import com.github.mrstampy.esp.dsp.EspSignalUtilities;
import com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocket;

// TODO: Auto-generated Javadoc
/**
 * Interface defining the encapsulation of a {@link MultiConnectionSocket} and
 * associated ESP objects for raw signal processing.
 * 
 * @author burton
 *
 */
public interface RawEspConnection extends MultiConnectionSocket {

	/**
	 * Return the utilities.
	 *
	 * @return the utilities
	 */
	EspSignalUtilities getUtilities();

	/**
	 * Return the window function.
	 *
	 * @return the window function
	 * @see EspSignalUtilities#setWindow(ddf.minim.analysis.WindowFunction)
	 */
	EspWindowFunction getWindowFunction();

	/**
	 * Set the window function.
	 *
	 * @param function the new window function
	 * @see EspSignalUtilities#setWindow(ddf.minim.analysis.WindowFunction)
	 */
	void setWindowFunction(EspWindowFunction function);

	/**
	 * Return the DSP values.
	 *
	 * @return the DSP values
	 */
	AbstractDSPValues getDSPValues();

	/**
	 * Return the current second's worth of samples.
	 *
	 * @return the current
	 */
	double[][] getCurrent();

	/**
	 * Return the specified number of the current second's worth of samples.
	 *
	 * @param numSamples the num samples
	 * @return the current
	 */
	double[][] getCurrent(int numSamples);

	/**
	 * Return the current second's worth of samples for the specified channel.
	 *
	 * @return the current
	 */
	double[][] getCurrentFor(int channelNumber);

	/**
	 * Return the specified number of the current second's worth of samples for the specified channel.
	 *
	 * @param numSamples the num samples
	 * @return the current
	 */
	double[][] getCurrentFor(int numSamples, int channelNumber);
	
	/**
	 * Returns the number of channels
	 * @return
	 */
	int getNumChannels();

	/**
	 * Return a name identifier.
	 *
	 * @return the name
	 */
	String getName();
}
