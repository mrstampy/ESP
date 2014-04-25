package com.github.mrstampy.esp.dsp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.esp.mutliconnectionsocket.AbstractMultiConnectionSocket;
import com.github.mrstampy.esp.mutliconnectionsocket.ConnectionEvent;
import com.github.mrstampy.esp.mutliconnectionsocket.ConnectionEventListener;

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

	protected ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
	protected ScheduledFuture<?> sf;

	protected List<RawProcessedListener> listeners = new ArrayList<RawProcessedListener>();

	protected int sampleRate;

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
	}

	/**
	 * Implement to return the map snapshot representing the approximate powers
	 * (the values) of the frequencies specified (the keys).
	 * 
	 * @return
	 * 
	 * @see RawProcessedListener
	 */
	public abstract Map<Double, Double> getSnapshot();

	/**
	 * Implement any takedown logic here
	 */
	protected abstract void destroyImpl();

	/**
	 * Process the current batch of samples for use with {@link #getSnapshot()}
	 * 
	 * @param list
	 */
	protected abstract void processSamples(List<Map<Double, Double>> list);

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
		// override appropriately
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
		if (sf != null) sf.cancel(true);
	}

	private void startAggregationProcessing() {
		long snooze = 1000 / getNumCyclesPerSecond();

		sf = scheduledExecutorService.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				process();
			}
		}, snooze * 4, snooze, TimeUnit.MILLISECONDS);
	}
}
