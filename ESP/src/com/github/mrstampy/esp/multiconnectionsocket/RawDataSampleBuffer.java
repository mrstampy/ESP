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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

/**
 * This class uses Disruptor internally. Add as a
 * {@link ConnectionEventListener} to the {@link AbstractMultiConnectionSocket}.
 * 
 * @author burton
 * 
 * @param <SAMPLE>
 */
public abstract class RawDataSampleBuffer<SAMPLE> implements ConnectionEventListener {
	private static final Logger log = LoggerFactory.getLogger(RawDataSampleBuffer.class);

	private int bufferSize;
	private int fftSize;
	private volatile MovingWindowBuffer movingWindow;

	private AtomicInteger totalForTuning = new AtomicInteger();

	private long startTimeTuning;

	private volatile boolean tuning;

	private Disruptor<MessageEvent<double[]>> disruptor;

	private RingBuffer<MessageEvent<double[]>> rb;

	private CountDownLatch latch = new CountDownLatch(1);

	protected RawDataSampleBuffer(int bufferSize, int fftSize) {
		setBufferSize(bufferSize);
		setFftSize(fftSize);
		movingWindow = new MovingWindowBuffer(bufferSize);
	}

	/**
	 * Implementation starts/stops disruptor
	 */
	public void connectionEventPerformed(ConnectionEvent e) {
		switch (e.getState()) {
		case STARTED:
			initDisruptor();
			latch.countDown();
			break;
		case STOPPED:
			disruptor.shutdown();
			latch = new CountDownLatch(1);
			break;
		default:
			break;
		}
	}

	public abstract void addSample(SAMPLE sample);

	protected void addSampleImpl(double... sample) {
		try {
			latch.await();
		} catch (InterruptedException e) {
		}

		long seq = rb.next();
		MessageEvent<double[]> be = rb.get(seq);
		be.setMessage(sample);
		rb.publish(seq);
	}

	public double[] getSnapshot() {
		double[] snap = movingWindow.snapshot();

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
		movingWindow.clear();
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

		movingWindow.resize(newBufSize);
	}

	@SuppressWarnings("unchecked")
	private void initDisruptor() {
		disruptor = new Disruptor<MessageEvent<double[]>>(new MessageEventFactory<double[]>(), 16,
				Executors.newCachedThreadPool());

		disruptor.handleEventsWith(new EventHandler<MessageEvent<double[]>>() {

			@Override
			public void onEvent(MessageEvent<double[]> event, long sequence, boolean endOfBatch) throws Exception {
				double[] t1 = event.getMessage();
				if (tuning) totalForTuning.addAndGet(t1.length);

				movingWindow.addAll(t1);
			}

		});

		rb = disruptor.start();
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
