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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractDSPValues.
 */
public abstract class AbstractDSPValues {

	/** The Constant ONE_THOUSAND. */
	public static final int ONE_THOUSAND = 1000;
	
	/** The Constant ONE_MILLION. */
	public static final int ONE_MILLION = 1000000;
	
	/** The Constant ONE_BILLION. */
	public static final int ONE_BILLION = 1000000000;

	private int sampleRate;
	private TimeUnit sampleRateUnits;

	private int sampleSize;

	private List<DSPValueListener> listeners = new ArrayList<DSPValueListener>();

	/**
	 * Call this constructor from subclasses.
	 */
	protected AbstractDSPValues() {
		initialize();
	}

	/**
	 * Initialize.
	 */
	protected abstract void initialize();

	/**
	 * Adds the dsp value listener.
	 *
	 * @param l the l
	 */
	public void addDSPValueListener(DSPValueListener l) {
		if (l != null && !listeners.contains(l)) listeners.add(l);
	}

	/**
	 * Removes the dsp value listener.
	 *
	 * @param l the l
	 */
	public void removeDSPValueListener(DSPValueListener l) {
		listeners.remove(l);
	}

	/**
	 * Gets the sample rate.
	 *
	 * @return the sample rate
	 */
	public int getSampleRate() {
		return sampleRate;
	}

	/**
	 * Sets the sample rate.
	 *
	 * @param sampleRate the new sample rate
	 */
	public void setSampleRate(int sampleRate) {
		if (sampleRate <= 0) {
			throw new IllegalArgumentException("Sample rate must be greater than zero: " + sampleRate);
		}
		this.sampleRate = sampleRate;
		setSampleRateUnits();

		notifySampleRateChange();
	}

	private void notifySampleRateChange() {
		for (DSPValueListener l : listeners) {
			l.sampleRateChanged();
		}
	}

	/**
	 * Gets the sample rate units.
	 *
	 * @return the sample rate units
	 */
	public TimeUnit getSampleRateUnits() {
		return sampleRateUnits;
	}

	private void setSampleRateUnits() {
		if (sampleRate <= ONE_THOUSAND) {
			setSampleRateUnits(TimeUnit.MILLISECONDS);
		} else if (sampleRate > ONE_THOUSAND && sampleRate <= ONE_MILLION) {
			setSampleRateUnits(TimeUnit.MICROSECONDS);
		} else if (sampleRate > ONE_MILLION) {
			setSampleRateUnits(TimeUnit.NANOSECONDS); // good luck!
		}
	}

	private void setSampleRateUnits(TimeUnit sampleRateUnits) {
		this.sampleRateUnits = sampleRateUnits;
	}

	/**
	 * Gets the sample size.
	 *
	 * @return the sample size
	 */
	public int getSampleSize() {
		return sampleSize;
	}

	/**
	 * Sets the sample size.
	 *
	 * @param sampleSize the new sample size
	 */
	public void setSampleSize(int sampleSize) {
		if ((sampleSize & (sampleSize - 1)) != 0) {
			throw new IllegalArgumentException("Sample size must be a power of two: " + sampleSize);
		}

		this.sampleSize = sampleSize;
		notifySampleSizeChange();
	}

	private void notifySampleSizeChange() {
		for (DSPValueListener l : listeners) {
			l.sampleSizeChanged();
		}
	}

	/**
	 * Gets the sample rate sleep time.
	 *
	 * @return the sample rate sleep time
	 */
	public long getSampleRateSleepTime() {
		TimeUnit tu = getSampleRateUnits();

		if (tu == TimeUnit.MILLISECONDS) return getSleepTime(ONE_THOUSAND);

		if (tu == TimeUnit.MICROSECONDS) return getSleepTime(ONE_MILLION);

		return getSleepTime(ONE_BILLION); // good luck!
	}

	private long getSleepTime(int numerator) {
		return new BigDecimal(numerator).divide(new BigDecimal(getSampleRate()), 0, RoundingMode.HALF_UP).longValue();
	}
}
