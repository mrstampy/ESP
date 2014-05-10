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
 * The Class AbstractFFTProcessor.
 */
public abstract class AbstractFFTProcessor implements DoubleArrayProcessor {

	private double lowFrequency = 0;
	private double highFrequency = 40;

	private EspSignalUtilities utilities;

	/**
	 * Gets the low frequency.
	 *
	 * @return the low frequency
	 */
	public double getLowFrequency() {
		return lowFrequency;
	}

	/**
	 * Sets the low frequency.
	 *
	 * @param lowFrequency the new low frequency
	 */
	public void setLowFrequency(double lowFrequency) {
		if (this.lowFrequency == lowFrequency) return;
		this.lowFrequency = lowFrequency;
	}

	/**
	 * Gets the high frequency.
	 *
	 * @return the high frequency
	 */
	public double getHighFrequency() {
		return highFrequency;
	}

	/**
	 * Sets the high frequency.
	 *
	 * @param highFrequency the new high frequency
	 */
	public void setHighFrequency(double highFrequency) {
		if (this.highFrequency == highFrequency) return;
		this.highFrequency = highFrequency;
	}

	/**
	 * Gets the utilities.
	 *
	 * @return the utilities
	 */
	public EspSignalUtilities getUtilities() {
		return utilities;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.DoubleArrayProcessor#setUtilities(com.github.mrstampy.esp.dsp.EspSignalUtilities)
	 */
	public void setUtilities(EspSignalUtilities utilities) {
		this.utilities = utilities;
		utilitiesSet();
	}
	
	protected void utilitiesSet() {
		
	}

}
