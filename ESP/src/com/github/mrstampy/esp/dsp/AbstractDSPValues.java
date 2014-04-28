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
import java.util.concurrent.TimeUnit;

public abstract class AbstractDSPValues {

	public static final int ONE_THOUSAND = 1000;
	public static final int ONE_MILLION = 1000000;
	public static final int ONE_BILLION = 1000000000;

	private int sampleRate;
	private TimeUnit sampleRateUnits;

	private int sampleSize;

	/**
	 * Call this constructor from subclasses
	 */
	protected AbstractDSPValues() {
		initialize();
	}

	protected abstract void initialize();

	public int getSampleRate() {
		return sampleRate;
	}

	public void setSampleRate(int sampleRate) {
		if (sampleRate <= 0) {
			throw new IllegalArgumentException("Sample rate must be greater than zero: " + sampleRate);
		}
		this.sampleRate = sampleRate;
		setSampleRateUnits();
	}

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

	public int getSampleSize() {
		return sampleSize;
	}

	public void setSampleSize(int sampleSize) {
		if ((sampleSize & (sampleSize - 1)) != 0) {
			throw new IllegalArgumentException("Sample size must be a power of two: " + sampleSize);
		}
		
		this.sampleSize = sampleSize;
	}

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
