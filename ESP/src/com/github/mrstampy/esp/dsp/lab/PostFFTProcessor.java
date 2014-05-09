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

// TODO: Auto-generated Javadoc
/**
 * The Class PostFFTProcessor.
 */
class PostFFTProcessor extends AbstractFFTProcessor {

	private boolean normalizeFft;
	private boolean absoluteValues;

	/**
	 * Instantiates a new post fft processor.
	 */
	public PostFFTProcessor() {
		super();
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.DoubleArrayProcessor#process(double[])
	 */
	@Override
	public double[] process(double[] array) {
		double[] n = normalize(array);
		double[] a = absolute(n);

		return a;
	}

	private double[] absolute(double[] array) {
		return isAbsoluteValues() ? getUtilities().absolute(array) : array;
	}

	private double[] normalize(double[] array) {
		return isNormalizeFft() ? getUtilities().normalize(array, (int) getLowFrequency(), (int) getHighFrequency())
				: array;
	}

	/**
	 * Checks if is normalize fft.
	 *
	 * @return true, if is normalize fft
	 */
	public boolean isNormalizeFft() {
		return normalizeFft;
	}

	/**
	 * Sets the normalize fft.
	 *
	 * @param normalizeFft the new normalize fft
	 */
	public void setNormalizeFft(boolean normalizeFft) {
		this.normalizeFft = normalizeFft;
	}

	/**
	 * Checks if is absolute values.
	 *
	 * @return true, if is absolute values
	 */
	public boolean isAbsoluteValues() {
		return absoluteValues;
	}

	/**
	 * Sets the absolute values.
	 *
	 * @param absoluteValues the new absolute values
	 */
	public void setAbsoluteValues(boolean absoluteValues) {
		this.absoluteValues = absoluteValues;
	}

}
