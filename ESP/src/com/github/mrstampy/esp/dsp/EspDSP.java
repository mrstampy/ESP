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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Scheduler;
import rx.Scheduler.Inner;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import com.github.mrstampy.esp.multiconnectionsocket.AbstractMultiConnectionSocket;
import com.github.mrstampy.esp.multiconnectionsocket.ConnectionEvent;
import com.github.mrstampy.esp.multiconnectionsocket.ConnectionEventListener;

/**
 * Abstract template for a DSP implementation. When connected the EspDSP will
 * schedule a job to get the snapshot from the
 * {@link RawSignalAggregator#getCurrentSecondOfSampledData()}, process it and
 * notify any {@link RawProcessedListener}s that the last cycle has finished.
 * 
 * The number of cycles / second defaults to 5 and by default all aggregated
 * elements are processed (the number of samples ~ equals the sample rate).
 * 
 * If the {@link #setNumSamplesPerCycle(int)} is set to a smaller value
 * performance will be improved at the expense of accuracy ie. setting the value
 * to {@link #sampleRate} / 2 will reduce the number of samples processed per
 * cycle by half. The {@link #numCyclesPerSecond} value has a similar effect on
 * performance.
 * 
 * The logPowers are used for calculations. Should another type of processing be
 * required the {@link #process()} method can be overridden appropriately.
 * 
 * @author burton
 * 
 * @param <SOCKET>
 */
public abstract class EspDSP<SOCKET extends AbstractMultiConnectionSocket<?>> {
	private static final Logger log = LoggerFactory.getLogger(EspDSP.class);

	protected SOCKET socket;

	protected ConnectionEventListener connectionEventListener;
	protected double[] frequencies;

	protected int numSamplesPerCycle;
	protected int numCyclesPerSecond = 5;

	protected Scheduler scheduler = Schedulers.executor(Executors.newScheduledThreadPool(5));
	protected Subscription subscription;

	protected List<RawProcessedListener> listeners = new ArrayList<RawProcessedListener>();

	protected int sampleRate;

	protected List<ArrayBlockingQueue<Double>> queues = new ArrayList<ArrayBlockingQueue<Double>>();

	/**
	 * Instantiate with the socket, sample rate and frequencies of interest
	 * 
	 * @param socket
	 * @param sampleRate
	 * @param frequencies
	 */
	protected EspDSP(SOCKET socket, int sampleRate, double... frequencies) {
		assert socket != null;
		assert sampleRate > 0;
		assert frequencies != null && frequencies.length > 0;

		this.socket = socket;
		this.frequencies = frequencies;
		this.sampleRate = sampleRate;
		setNumSamplesPerCycle(sampleRate);

		initSocket(socket);
		createQueues();
	}

	/**
	 * Implement any takedown logic here
	 */
	protected abstract void destroyImpl();

	/**
	 * Return the aggregator for the raw signal.
	 * 
	 * @return
	 */
	protected abstract RawSignalAggregator getAggregator();

	/**
	 * Return the utilities for the implementation
	 * 
	 * @return
	 */
	protected abstract EspSignalUtilities getUtilities();

	/**
	 * Returns the snapshot of the current processed raw data
	 * 
	 * @return
	 */
	public Map<Double, Double> getSnapshot() {
		Map<Double, Double> map = new HashMap<Double, Double>();

		for (int i = 0; i < frequencies.length; i++) {
			ArrayBlockingQueue<Double> queue = queues.get(i);
			Double[] snap = queue.toArray(new Double[] {});
			double[] shot = new double[snap.length];
			for (int j = 0; j < snap.length; j++) {
				shot[j] = snap[j];
			}

			double wms = getUtilities().wma(shot);
			map.put(frequencies[i], wms);
		}

		return map;
	}

	public void addProcessedListener(RawProcessedListener l) {
		listeners.add(l);
	}

	public void removeProcessedListener(RawProcessedListener l) {
		listeners.remove(l);
	}

	public void clearProcessedListeners() {
		listeners.clear();
	}

	public final void destroy() {
		socket.removeConnectionEventListener(connectionEventListener);
		destroyImpl();
	}

	/**
	 * The default implementation uses the
	 * {@link EspSignalUtilities#getLogPowersFor(double[], double...)} to
	 * transform the signal into the frequency domain. Log powers can be
	 * manipulated linearly to compute averages etc. Should another type of
	 * processing be required then this method can be overridden.
	 */
	protected void process() {
		RawSignalAggregator aggregator = getAggregator();
		double[][] sampled = getNumSamplesPerCycle() == sampleRate ? aggregator.getCurrentSecondOfSampledData()
				: aggregator.getCurrentSecondOfSampledData(getNumSamplesPerCycle());

		List<Map<Double, Double>> list = new ArrayList<Map<Double, Double>>();
		try {
			for (int i = 0; i < sampled.length; i++) {
				Map<Double, Double> map = getUtilities().getLogPowersFor(sampled[i], frequencies);
				list.add(map);
			}

			processSamples(list);

			notifyListeners();
		} catch (Exception e) {
			log.error("Could not process current sample", e);
		}
	}

	/**
	 * Applies the snapshot to the queue.
	 * 
	 * @param list
	 */
	protected void processSamples(List<Map<Double, Double>> list) {
		for (int i = 0; i < frequencies.length; i++) {
			double[] powers = getPowers(frequencies[i], list);
			double wmad = getUtilities().wma(powers);

			ArrayBlockingQueue<Double> queue = queues.get(i);
			if (queue.remainingCapacity() == 0) queue.remove();
			queue.add(wmad);
		}
	}

	/**
	 * Notify listeners that the current sample has been processed
	 * 
	 * @see #process()
	 */
	protected void notifyListeners() {
		for (RawProcessedListener l : listeners) {
			l.signalProcessed();
		}
	}

	/**
	 * Convenience method to return an array of powers for the specified frequency
	 * 
	 * @param frequency
	 * @param list
	 * @return
	 */
	protected double[] getPowers(Double frequency, List<Map<Double, Double>> list) {
		double[] powers = new double[list.size()];

		for (int i = 0; i < list.size(); i++) {
			Map<Double, Double> map = list.get(i);
			powers[i] = map.get(frequency);
		}

		return powers;
	}

	/**
	 * Invoked when the {@link #numCyclesPerSecond} value changed.
	 */
	protected void numCyclesChanged() {
		createQueues();
	}

	/**
	 * Invoked when the {@link #numSamplesPerCycle} value changed;
	 */
	protected void numSamplesPerCycleChanged() {
		// override appropriately
	}

	public final void finalize() {
		destroy();
	}

	public int getNumSamplesPerCycle() {
		return numSamplesPerCycle;
	}

	public void setNumSamplesPerCycle(int numSamplesPerCycle) {
		this.numSamplesPerCycle = numSamplesPerCycle;
		numSamplesPerCycleChanged();
	}

	public int getNumCyclesPerSecond() {
		return numCyclesPerSecond;
	}

	public void setNumCyclesPerSecond(int numCyclesPerSecond) {
		this.numCyclesPerSecond = numCyclesPerSecond;
		numCyclesChanged();
	}

	private void createQueues() {
		queues.clear();
		for (int i = 0; i < frequencies.length; i++) {
			queues.add(new ArrayBlockingQueue<Double>(getNumCyclesPerSecond()));
		}
	}

	private void initSocket(SOCKET socket) {
		connectionEventListener = new ConnectionEventListener() {

			@Override
			public void connectionEventPerformed(ConnectionEvent e) {
				switch (e.getState()) {
				case STARTED:
					startAggregationProcessing();
					break;
				case STOPPED:
					stopAggregationProcessing();
					break;
				default:
					break;
				}
			}
		};

		socket.addConnectionEventListener(connectionEventListener);
	}

	private void stopAggregationProcessing() {
		if (subscription != null) subscription.unsubscribe();
	}

	private void startAggregationProcessing() {
		int numCycles = getNumCyclesPerSecond();

		TimeUnit tu = null;
		int numerator = 0;
		if (numCycles <= AbstractDSPValues.ONE_THOUSAND) {
			numerator = AbstractDSPValues.ONE_THOUSAND;
			tu = TimeUnit.MILLISECONDS;
		} else if (numCycles <= AbstractDSPValues.ONE_MILLION) {
			numerator = AbstractDSPValues.ONE_MILLION;
			tu = TimeUnit.MICROSECONDS;
		} else {
			numerator = AbstractDSPValues.ONE_BILLION;
			tu = TimeUnit.NANOSECONDS;
		}

		long snooze = calculateSleep(numCycles, numerator);

		subscription = scheduler.schedulePeriodically(new Action1<Scheduler.Inner>() {

			@Override
			public void call(Inner t1) {
				process();
			}
		}, snooze * 4, snooze, tu);
	}

	private long calculateSleep(int numCycles, int numerator) {
		return new BigDecimal(numerator).divide(new BigDecimal(numCycles), 0, RoundingMode.HALF_UP).longValue();
	}
}
