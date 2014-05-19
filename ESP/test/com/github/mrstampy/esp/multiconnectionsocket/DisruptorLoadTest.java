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

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

import rx.Observable;
import rx.Scheduler;
import rx.Scheduler.Inner;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import com.github.mrstampy.esp.multiconnectionsocket.ConnectionEvent.State;

public class DisruptorLoadTest {

	private static final int NUM_BUFFERS = 32;

	private Map<Integer, TestDataBuffer> buffers = new ConcurrentHashMap<Integer, TestDataBuffer>();

	private Subscription subscription;
	private Subscription sampleSub;

	private volatile State state;

	@Before
	public void before() throws Exception {
		if (!buffers.isEmpty()) return;

		for (int i = 1; i <= NUM_BUFFERS; i++) {
			buffers.put(i, new TestDataBuffer(1024, 512));
		}
	}

	/**
	 * Integration test which runs up 32 {@link RawDataSampleBuffer}s with a
	 * buffer size of 1024, fft size of 512 sampled every 2 milliseconds and added
	 * to every 1 milliseconds, for 20 seconds.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLoad() throws Exception {
		fakeEvent(new ConnectionEvent(this, State.STARTED));

		scheduleFill();

		scheduleSampling();

		scheduleStop();

		while (state == State.STARTED) {
			Thread.sleep(20);
		}

		double[] sample = buffers.get(1).getSnapshot();
		System.out.println(sample.length);

		System.out.println(sample[sample.length - 1]);
	}

	private void scheduleStop() {
		Schedulers.computation().schedule(new Action1<Scheduler.Inner>() {

			@Override
			public void call(Inner t1) {
				fakeEvent(new ConnectionEvent(this, State.STOPPED));
			}
		}, 20, TimeUnit.SECONDS);
	}

	private void scheduleSampling() {
		sampleSub = Schedulers.computation().schedulePeriodically(new Action1<Scheduler.Inner>() {

			@Override
			public void call(Inner t1) {
				switch (state) {
				case STOPPED:
					sampleSub.unsubscribe();
					break;
				default:
					sample();
				}
			}
		}, 10, 2, TimeUnit.MILLISECONDS);
	}

	private void scheduleFill() {
		subscription = Schedulers.computation().schedulePeriodically(new Action1<Scheduler.Inner>() {

			@Override
			public void call(Inner t1) {
				switch (state) {
				case STOPPED:
					subscription.unsubscribe();
					break;
				default:
					fillBuffers();
				}
			}
		}, 0, 1, TimeUnit.MILLISECONDS);
	}

	private void sample() {
		Schedulers.computation().schedule(new Action1<Scheduler.Inner>() {
			
			@Override
			public void call(Inner t1) {
				for (int i = 1; i <= NUM_BUFFERS; i++) {
					buffers.get(i).getSnapshot();
				}
			}
		});
	}

	private void fillBuffers() {
		double[] d = new double[5];
		Arrays.fill(d, 1);

		Observable.just(d).subscribe(new Action1<double[]>() {

			@Override
			public void call(double[] t1) {
				for (int i = 1; i <= NUM_BUFFERS; i++) {
					buffers.get(i).addSample(t1);
				}
			}
		});
	}

	private void fakeEvent(ConnectionEvent e) {
		for (int i = 1; i <= NUM_BUFFERS; i++) {
			buffers.get(i).connectionEventPerformed(e);
		}
		this.state = e.getState();
	}

	private class TestDataBuffer extends RawDataSampleBuffer<double[]> {

		protected TestDataBuffer(int bufferSize, int fftSize) {
			super(bufferSize, fftSize);
		}

		@Override
		public void addSample(double[] sample) {
			addSampleImpl(sample);
		}

	}
}
