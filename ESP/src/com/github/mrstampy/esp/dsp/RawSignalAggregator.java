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
import java.util.concurrent.ArrayBlockingQueue;

import rx.Observable;
import rx.functions.Action1;

/**
 * Abstract superclass for aggregating raw signal arrays (ultimately for FFT'ing).
 * 
 * @author burton
 *
 */
public abstract class RawSignalAggregator {

	protected ArrayBlockingQueue<double[]> queue;

	private final int sampleRate;

	protected RawSignalAggregator(int sampleRate) {
		queue = new ArrayBlockingQueue<double[]>(sampleRate);
		this.sampleRate = sampleRate;
	}

	public void addSample(double... sample) {
		Observable.just(sample).subscribe(new Action1<double[]>() {

			@Override
			public void call(double[] t1) {
				if (queue.remainingCapacity() == 0) queue.remove();
				
				queue.add(t1);
			}
		});
	}

	public void clear() {
		queue.clear();
	}

	/**
	 * Returns a snapshot of ~ sampleRate samples, each of length fftSize
	 * representing the current seconds' raw signal sampled.
	 * 
	 * @return
	 * @see EspSignalUtilities
	 */
	public double[][] getCurrentSecondOfSampledData() {
		return queue.toArray(new double[0][]);
	}

	/**
	 * Returns the requested number of samples from the current one seconds' worth
	 * of samples (~ {@link #sampleRate}), evenly spaced over the size of the
	 * samples.
	 * 
	 * @param numSamples
	 *          a fraction of the {@link #sampleRate} current seconds' samples, >
	 *          0 && < {@link #sampleRate} ie. 50, 25, 10
	 * @return
	 * @see EspSignalUtilities
	 */
	public double[][] getCurrentSecondOfSampledData(int numSamples) {
		double[][] oneSecond = getCurrentSecondOfSampledData();

		return processOneSecondOfData(oneSecond, numSamples);
	}

	private double[][] processOneSecondOfData(double[][] oneSecond, int numSamples) {
		assert numSamples > 0 && numSamples < oneSecond.length;

		BigDecimal interval = new BigDecimal(sampleRate).divide(new BigDecimal(numSamples), 5, RoundingMode.HALF_UP);

		double erval = interval.doubleValue();

		double[][] processed = new double[numSamples][];

		int j = 0;
		for (double i = 0; i < oneSecond.length; i += erval) {
			processed[j] = oneSecond[(int) i];
			j++;
		}

		return processed;
	}

}
