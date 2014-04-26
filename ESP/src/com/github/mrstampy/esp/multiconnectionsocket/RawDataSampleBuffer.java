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
package com.github.mrstampy.esp.multiconnectionsocket;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class RawDataSampleBuffer<SAMPLE> {
	private static final Logger log = LoggerFactory.getLogger(RawDataSampleBuffer.class);

	private int bufferSize;
	private int fftSize;
	private ArrayBlockingQueue<Double> queue;

	private AtomicInteger totalForTuning = new AtomicInteger();

	private long startTimeTuning;

	private volatile boolean tuning;

	protected RawDataSampleBuffer(int bufferSize, int fftSize) {
		setBufferSize(bufferSize);
		setFftSize(fftSize);
		queue = new ArrayBlockingQueue<Double>(bufferSize);
	}

	public abstract void addSample(SAMPLE sample);
	
	protected void addSampleImpl(double... sample) {
		if (tuning) totalForTuning.addAndGet(sample.length);
		
		for (int i = 0; i < sample.length; i++) {
			if (queue.remainingCapacity() == 0) queue.remove();
			
			queue.add(sample[i]);
		}
	}

	public double[] getSnapshot() {
		Double[] snap = queue.toArray(new Double[] {});

		double[] shot = new double[getFftSize()];

		double factor = new BigDecimal(getBufferSize()).divide(new BigDecimal(getFftSize()), 5, RoundingMode.HALF_UP)
				.doubleValue();

		int j = 0;
		for (double i = 0; i < snap.length; i += factor) {
			shot[j] = snap[(int) i];
			j++;
		}

		return shot;
	}

	public void clear() {
		queue.clear();
	}

	/**
	 * When invoked the number of samples will be counted. When
	 * {@link #stopTuning()} is invoked the time taken to process the total number
	 * of samples taken during that time will be used to resize the sample buffer.
	 */
	public void tune() {
		if (tuning) return;

		totalForTuning.set(0);
		tuning = true;
		startTimeTuning = System.nanoTime();
	}

	/**
	 * Invoked after a period of time after invoking tune(). Will resize the queue
	 * to represent ~ 1 second's worth of data based upon the total number of
	 * samples received during tuning.
	 */
	public void stopTuning() {
		if (!tuning) return;

		tuning = false;

		long diff = System.nanoTime() - startTimeTuning;
		int total = totalForTuning.get();

		BigDecimal seconds = new BigDecimal(diff).divide(new BigDecimal(1000000000), 10, RoundingMode.HALF_UP);

		int newBufSize = new BigDecimal(total).divide(seconds, 3, RoundingMode.HALF_UP).intValue();

		log.info("Resizing buffer to {}", newBufSize);

		setBufferSize(newBufSize);

		synchronized (queue) {
			queue = new ArrayBlockingQueue<Double>(newBufSize);
		}
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public int getFftSize() {
		return fftSize;
	}

	public void setFftSize(int fftSize) {
		this.fftSize = fftSize;
	}

}
