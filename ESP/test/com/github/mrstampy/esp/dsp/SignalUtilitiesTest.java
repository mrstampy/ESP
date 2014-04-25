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

}
