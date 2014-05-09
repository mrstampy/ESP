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

import de.dfki.lt.signalproc.filter.BandPassFilter;
import de.dfki.lt.signalproc.filter.HighPassFilter;
import de.dfki.lt.signalproc.filter.LowPassFilter;

// TODO: Auto-generated Javadoc
/**
 * The Class PreFFTProcessor.
 */
class PreFFTProcessor extends AbstractFFTProcessor {

	private HighPassFilter hpf;
	private LowPassFilter lpf;
	private BandPassFilter bpf;

	private FrequencyKey key;

	private PassFilter passFilter = PassFilter.no_pass_filter;

	private boolean normalizeSignal;
	
	/**
	 * Instantiates a new pre fft processor.
	 */
	public PreFFTProcessor() {
		super();
		setLowFrequency(1);
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.DoubleArrayProcessor#process(double[])
	 */
	@Override
	public double[] process(double[] array) {
		double[] n = normalize(array);
		double[] d = applyPassFilter(n);
		
		double[] copy = new double[array.length];
		System.arraycopy(d, 0, copy, 0, copy.length);

		return copy;
	}

	private double[] normalize(double[] array) {
		return isNormalizeSignal() ? getUtilities().normalize(array) : array;
	}

	private double[] applyPassFilter(double[] array) {
		keyCheck();

		switch (passFilter) {
		case band_pass:
			return bpf.apply(array);
		case high_pass:
			return hpf.apply(array);
		case low_pass:
			return lpf.apply(array);
		default:
			break;
		}

		return array;
	}

	private void keyCheck() {
		if (key != null && key.isSame()) return;

		key = new FrequencyKey(getLowFrequency(), getHighFrequency());

		int factor = getUtilities().getDSPValues().getSampleRate() / 20;
		bpf = getUtilities().createBandPassFilter(getLowFrequency() / factor, getHighFrequency() / factor);
		lpf = getUtilities().createLowPassFilter(getHighFrequency() / factor);
		hpf = getUtilities().createHighPassFilter(getLowFrequency() / factor);
	}

	private class FrequencyKey {
		double lowKey;
		double highKey;

		public FrequencyKey(double lowKey, double highKey) {
			this.lowKey = lowKey;
			this.highKey = highKey;
		}

		boolean isSame() {
			return lowKey == getLowFrequency() && highKey == getHighFrequency();
		}
	}

	/**
	 * Gets the pass filter.
	 *
	 * @return the pass filter
	 */
	public PassFilter getPassFilter() {
		return passFilter;
	}

	/**
	 * Sets the pass filter.
	 *
	 * @param passFilter the new pass filter
	 */
	public void setPassFilter(PassFilter passFilter) {
		this.passFilter = passFilter;
	}

	/**
	 * Checks if is normalize signal.
	 *
	 * @return true, if is normalize signal
	 */
	public boolean isNormalizeSignal() {
		return normalizeSignal;
	}

	/**
	 * Sets the normalize signal.
	 *
	 * @param normalize the new normalize signal
	 */
	public void setNormalizeSignal(boolean normalize) {
		this.normalizeSignal = normalize;
	}

}
