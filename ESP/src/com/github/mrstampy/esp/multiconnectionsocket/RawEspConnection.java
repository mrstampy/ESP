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

import com.github.mrstampy.esp.dsp.AbstractDSPValues;
import com.github.mrstampy.esp.dsp.EspSignalUtilities;

/**
 * Interface defining the encapsulation of a {@link MultiConnectionSocket} and
 * associated ESP objects for raw signal processing.
 * 
 * @author burton
 *
 */
public interface RawEspConnection extends MultiConnectionSocket {

	/**
	 * Return the utilities
	 * 
	 * @return
	 */
	EspSignalUtilities getUtilities();

	/**
	 * Return the window function
	 * 
	 * @return
	 * @see EspSignalUtilities#setWindow(ddf.minim.analysis.WindowFunction)
	 */
	EspWindowFunction getWindowFunction();

	/**
	 * Set the window function
	 * 
	 * @param function
	 * @see EspSignalUtilities#setWindow(ddf.minim.analysis.WindowFunction)
	 */
	void setWindowFunction(EspWindowFunction function);

	/**
	 * Return the DSP values
	 * 
	 * @return
	 */
	AbstractDSPValues getDSPValues();

	/**
	 * Return the current second's worth of samples
	 * 
	 * @return
	 */
	double[][] getCurrent();

	/**
	 * Return the specified number of the current second's worth of samples
	 * 
	 * @param numSamples
	 * @return
	 */
	double[][] getCurrent(int numSamples);

	/**
	 * Return a name identifier
	 * 
	 * @return
	 */
	String getName();
}
