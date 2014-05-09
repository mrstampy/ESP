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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.mrstampy.esp.dsp.EspSignalUtilities;
import com.github.mrstampy.esp.multiconnectionsocket.RawEspConnection;

// TODO: Auto-generated Javadoc
/**
 * The Class DefaultLab.
 */
public class DefaultLab implements Lab {

	private PreFFTProcessor preFFTProcessor = new PreFFTProcessor();
	private PostFFTProcessor postFFTProcessor = new PostFFTProcessor();
	private RawEspConnection connection;
	private FFTType fftType;
	private EspSignalUtilities utilities;

	private AtomicBoolean calcBaseline = new AtomicBoolean(false);
	private volatile double baseline = 0;
	private double baselineCandidate = 0;

	private int numBands;

	/**
	 * Instantiates a new default lab.
	 *
	 * @param numBands the num bands
	 */
	public DefaultLab(int numBands) {
		setNumBands(numBands);
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#process(double[][])
	 */
	@Override
	public double[] process(double[][] signal) {
		if (signal.length == 0) return new double[] {};

		double[] wmad = new double[getNumBands()];

		List<double[]> samples = new ArrayList<double[]>();
		for (int i = 0; i < signal.length; i++) {
			double[] d = preFFT(signal[i]);
			double[] fftd = applyFft(d);
			double[] val = postFFT(fftd);

			samples.add(val);
		}

		for (int i = 0; i < wmad.length; i++) {
			wmad[i] = utilities.wma(getPowers(samples, i));
		}

		if (calcBaseline.get()) setMinValue(wmad);

		return baseline == 0 ? wmad : applyBaseline(wmad);
	}

	private double[] applyBaseline(double[] wmad) {
		double[] bld = new double[wmad.length];

		for (int i = 0; i < wmad.length; i++) {
			bld[i] = wmad[i] - baseline;
		}

		return bld;
	}

	private void setMinValue(double[] wmad) {
		double val = baselineCandidate;
		for (double d : wmad) {
			val = Math.min(d, val);
		}
		baselineCandidate = val;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#stopCalculateBaseline()
	 */
	@Override
	public void stopCalculateBaseline() {
		calcBaseline.set(false);
		baseline = baselineCandidate;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#calculateBaseline()
	 */
	@Override
	public void calculateBaseline() {
		baselineCandidate = Integer.MAX_VALUE;
		calcBaseline.set(true);
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#resetBaseline()
	 */
	@Override
	public void resetBaseline() {
		baseline = 0;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#getConnection()
	 */
	@Override
	public RawEspConnection getConnection() {
		return connection;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#setConnection(com.github.mrstampy.esp.multiconnectionsocket.RawEspConnection)
	 */
	@Override
	public void setConnection(RawEspConnection connection) {
		this.connection = connection;
		setUtilities(connection.getUtilities());
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#getFftType()
	 */
	@Override
	public FFTType getFftType() {
		return fftType;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#setFftType(com.github.mrstampy.esp.dsp.lab.FFTType)
	 */
	@Override
	public void setFftType(FFTType fftType) {
		this.fftType = fftType;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#getNumBands()
	 */
	@Override
	public int getNumBands() {
		return numBands;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#setNumBands(int)
	 */
	@Override
	public void setNumBands(int numBands) {
		this.numBands = numBands;
	}

	private PreFFTProcessor getPreFFTProcessor() {
		return preFFTProcessor;
	}

	private PostFFTProcessor getPostFFTProcessor() {
		return postFFTProcessor;
	}

	private void setUtilities(EspSignalUtilities utilities) {
		this.utilities = utilities;
		preFFTProcessor.setUtilities(utilities);
		postFFTProcessor.setUtilities(utilities);
	}

	private double[] applyFft(double[] d) {
		assert getFftType() != null;

		switch (getFftType()) {
		case log_fft:
			return utilities.fftLogPowerSpectrum(d);
		case real_fft:
			return utilities.fftRealSpectrum(d);
		default:
			return d;
		}
	}

	private double[] getPowers(List<double[]> samples, int idx) {
		double[] powers = new double[samples.size()];

		for (int i = 0; i < samples.size(); i++) {
			powers[i] = samples.get(i)[idx];
		}

		return powers;
	}

	private double[] postFFT(double[] fftd) {
		return postFFTProcessor.process(fftd);
	}

	private double[] preFFT(double[] sample) {
		return preFFTProcessor.process(sample);
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#setHighPassFrequency(double)
	 */
	@Override
	public void setHighPassFrequency(double band) {
		getPreFFTProcessor().setHighFrequency(band);
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#setLowPassFrequency(double)
	 */
	@Override
	public void setLowPassFrequency(double band) {
		getPreFFTProcessor().setLowFrequency(band);
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#isAbsoluteValues()
	 */
	public boolean isAbsoluteValues() {
		return getPostFFTProcessor().isAbsoluteValues();
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#setAbsoluteValues(boolean)
	 */
	public void setAbsoluteValues(boolean absoluteValues) {
		getPostFFTProcessor().setAbsoluteValues(absoluteValues);
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#getHighPassFrequency()
	 */
	@Override
	public double getHighPassFrequency() {
		return getPreFFTProcessor().getHighFrequency();
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#getLowPassFrequency()
	 */
	@Override
	public double getLowPassFrequency() {
		return getPreFFTProcessor().getLowFrequency();
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#getPassFilter()
	 */
	@Override
	public PassFilter getPassFilter() {
		return getPreFFTProcessor().getPassFilter();
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#setPassFilter(com.github.mrstampy.esp.dsp.lab.PassFilter)
	 */
	@Override
	public void setPassFilter(PassFilter filter) {
		getPreFFTProcessor().setPassFilter(filter);
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#isNormalizeSignal()
	 */
	@Override
	public boolean isNormalizeSignal() {
		return getPreFFTProcessor().isNormalizeSignal();
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#setNormalizeSignal(boolean)
	 */
	@Override
	public void setNormalizeSignal(boolean normalize) {
		getPreFFTProcessor().setNormalizeSignal(normalize);
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#isNormalizeFft()
	 */
	@Override
	public boolean isNormalizeFft() {
		return getPostFFTProcessor().isNormalizeFft();
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#setNormalizeFft(boolean)
	 */
	@Override
	public void setNormalizeFft(boolean normalize) {
		getPostFFTProcessor().setNormalizeFft(normalize);
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#setHighNormalizeFftFrequency(double)
	 */
	@Override
	public void setHighNormalizeFftFrequency(double freq) {
		getPostFFTProcessor().setHighFrequency(freq);
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#getHighNormalizeFftFrequency()
	 */
	@Override
	public double getHighNormalizeFftFrequency() {
		return getPostFFTProcessor().getHighFrequency();
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#setLowNormalizeFftFrequency(double)
	 */
	@Override
	public void setLowNormalizeFftFrequency(double freq) {
		getPostFFTProcessor().setLowFrequency(freq);
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#getLowNormalizeFftFrequency()
	 */
	@Override
	public double getLowNormalizeFftFrequency() {
		return getPostFFTProcessor().getLowFrequency();
	}
}
