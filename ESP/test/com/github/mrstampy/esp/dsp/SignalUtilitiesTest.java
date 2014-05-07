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
package com.github.mrstampy.esp.dsp;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Test;

import ddf.minim.analysis.HammingWindow;

public class SignalUtilitiesTest extends EspSignalUtilities {

	private static final double FREQ = 7.83;

	private static final double FREQ2 = 10.8;

	public SignalUtilitiesTest() {
		super(new HammingWindow());
	}

	@Test
	public void testFFT() {
		double[] sample = getSampleArray();
		long start = System.nanoTime();
		for (int i = 0; i < 10; i++) {
			double[] fftd = fftLogPowerSpectrum(sample);
			int y = 0;
		}
		long end = System.nanoTime();

		System.out.println("FFT took " + (end - start) + " ns");
	}

	private double[] getSampleArray() {
		double[] array = new double[getFFTSize()];

		BigDecimal tau = new BigDecimal(2).multiply(new BigDecimal(Math.PI));

		BigDecimal multiplier = new BigDecimal(FREQ).multiply(tau).divide(new BigDecimal(getFFTSize()), 3,
				RoundingMode.HALF_UP);

		BigDecimal multiplier2 = new BigDecimal(FREQ2).multiply(tau).divide(new BigDecimal(getFFTSize()), 3,
				RoundingMode.HALF_UP);

		for (int i = 0; i < array.length; i++) {
			array[i] = Math.sin(multiplier.multiply(new BigDecimal(i)).doubleValue())
					+ Math.sin(multiplier2.multiply(new BigDecimal(i)).doubleValue());
		}

		return array;
	}

	@Override
	protected int getFFTSize() {
		return 512;
	}

	@Override
	protected double getSampleRate() {
		return 100;
	}

	@Override
	protected BigDecimal getRawSignalBreadth() {
		return BigDecimal.TEN;
	}

	@Override
	public AbstractDSPValues getDSPValues() {
		return null;
	}

}
