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

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Interface LabValues.
 */
public interface LabValues extends Serializable {

	/**
	 * Gets the fft type.
	 *
	 * @return the fft type
	 */
	FFTType getFftType();

	/**
	 * Sets the fft type.
	 *
	 * @param fftType the new fft type
	 */
	void setFftType(FFTType fftType);

	/**
	 * Gets the num bands.
	 *
	 * @return the num bands
	 */
	int getNumBands();

	/**
	 * Sets the num bands.
	 *
	 * @param numBands the new num bands
	 */
	void setNumBands(int numBands);

	/**
	 * Sets the high pass frequency.
	 *
	 * @param freq the new high pass frequency
	 */
	void setHighPassFrequency(double freq);

	/**
	 * Gets the high pass frequency.
	 *
	 * @return the high pass frequency
	 */
	double getHighPassFrequency();

	/**
	 * Sets the low pass frequency.
	 *
	 * @param freq the new low pass frequency
	 */
	void setLowPassFrequency(double freq);

	/**
	 * Gets the low pass frequency.
	 *
	 * @return the low pass frequency
	 */
	double getLowPassFrequency();

	/**
	 * Gets the pass filter.
	 *
	 * @return the pass filter
	 */
	PassFilter getPassFilter();

	/**
	 * Sets the pass filter.
	 *
	 * @param filter the new pass filter
	 */
	void setPassFilter(PassFilter filter);

	/**
	 * Checks if is absolute values.
	 *
	 * @return true, if is absolute values
	 */
	boolean isAbsoluteValues();

	/**
	 * Sets the absolute values.
	 *
	 * @param absolute the new absolute values
	 */
	void setAbsoluteValues(boolean absolute);

	/**
	 * Checks if is normalize signal.
	 *
	 * @return true, if is normalize signal
	 */
	boolean isNormalizeSignal();

	/**
	 * Sets the normalize signal.
	 *
	 * @param normalize the new normalize signal
	 */
	void setNormalizeSignal(boolean normalize);

	/**
	 * Checks if is normalize fft.
	 *
	 * @return true, if is normalize fft
	 */
	boolean isNormalizeFft();

	/**
	 * Sets the normalize fft.
	 *
	 * @param normalize the new normalize fft
	 */
	void setNormalizeFft(boolean normalize);

	/**
	 * Sets the high normalize fft frequency.
	 *
	 * @param freq the new high normalize fft frequency
	 */
	void setHighNormalizeFftFrequency(double freq);

	/**
	 * Gets the high normalize fft frequency.
	 *
	 * @return the high normalize fft frequency
	 */
	double getHighNormalizeFftFrequency();

	/**
	 * Sets the low normalize fft frequency.
	 *
	 * @param freq the new low normalize fft frequency
	 */
	void setLowNormalizeFftFrequency(double freq);

	/**
	 * Gets the low normalize fft frequency.
	 *
	 * @return the low normalize fft frequency
	 */
	double getLowNormalizeFftFrequency();
	
	/**
	 * Sets the baseline.
	 *
	 * @param baseline the new baseline
	 */
	void setBaseline(double baseline);
	
	/**
	 * Gets the baseline.
	 *
	 * @return the baseline
	 */
	double getBaseline();

	/**
	 * Sets the low pass filter factor.
	 *
	 * @param factor the new low pass filter factor
	 */
	void setLowPassFilterFactor(double factor);
	
	/**
	 * Gets the low pass filter factor.
	 *
	 * @return the low pass filter factor
	 */
	double getLowPassFilterFactor();
	
	/**
	 * Sets the high pass filter factor.
	 *
	 * @param factor the new high pass filter factor
	 */
	void setHighPassFilterFactor(double factor);
	
	/**
	 * Gets the high pass filter factor.
	 *
	 * @return the high pass filter factor
	 */
	double getHighPassFilterFactor();
}
