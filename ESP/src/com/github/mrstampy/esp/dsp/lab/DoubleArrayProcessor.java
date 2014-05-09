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

import com.github.mrstampy.esp.dsp.EspSignalUtilities;

// TODO: Auto-generated Javadoc
/**
 * The Interface DoubleArrayProcessor.
 */
public interface DoubleArrayProcessor {

	/**
	 * Process.
	 *
	 * @param array the array
	 * @return the double[]
	 */
	double[] process(double[] array);
	
	/**
	 * Sets the utilities.
	 *
	 * @param utilities the new utilities
	 */
	void setUtilities(EspSignalUtilities utilities);
}
