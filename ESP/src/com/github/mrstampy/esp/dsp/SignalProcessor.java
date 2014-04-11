/*
 * Copyright (C) 2008 - 2013 Burton Alexander
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
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class aggregates signal values and notifies listeners at a rate of
 * {@link #getSampleRate()} one second's worth of signal values, an array of
 * {@link #getSampleSize()} in length. A thread reading data from a device
 * invokes one of the {@link #addSignal(Number)} or
 * {@link #addSignal(long, Number)} methods while a trigger thread started when
 * the {@link #start()} method was called reads the data and notifies listeners
 * at the sample rate.
 * <br><br>
 * Recommended for use with sample sizes of < 1000 samples per second.  For greater
 * sample sizes primitive arrays should be used.
 * 
 * @author burton
 * 
 */
public class SignalProcessor {

  protected static final long ONE_SECOND_NANOS = 1000000000;

  protected static BigDecimal NANOS_PER_SECOND = new BigDecimal(ONE_SECOND_NANOS);

  protected ConcurrentSkipListMap<Long, Double> rawSignal = new ConcurrentSkipListMap<Long, Double>();

  private TriggerThread triggerThread;

  private ExecutorService processorService = Executors.newSingleThreadExecutor();
  private ExecutorService notifierService = Executors.newSingleThreadExecutor();

  private List<SignalProcessorListener> listeners = new ArrayList<SignalProcessorListener>();

  private int sampleSize = 256;
  private int sampleRate = 50; // Hz
  protected boolean started = false;

  /**
   * {@link SignalProcessorListener}s receive notification of the current signal
   * at a rate of {@link #getSampleRate()} Hz.
   * 
   * @param listener
   */
  public void addListener(SignalProcessorListener listener) {
    listeners.add(listener);
  }

  public void removeListener(SignalProcessorListener listener) {
    listeners.remove(listener);
  }

  /**
   * Call to start processing the signal
   */
  public void start() {
    if (isStarted()) return;

    initTrigger();
    setStarted(true);
  }

  /**
   * Call to stop processing the signal
   */
  public void stop() {
    if (!isStarted()) return;

    triggerThread.setRun(false);

    setStarted(false);
  }

  /**
   * Adds the signal specified
   * 
   * @param value
   */
  public void addSignal(Number value) {
    addSignal(System.nanoTime(), value);
  }

  /**
   * Adds the signal specified
   * 
   * @param nanos
   * @param value
   */
  public void addSignal(long nanos, Number value) {
    if (!isStarted()) return;
    if (value == null) return;

    rawSignal.put(nanos, value.doubleValue());
  }

  public boolean isStarted() {
    return started;
  }

  private void setStarted(boolean started) {
    this.started = started;
  }

  /**
   * Defaults to 50 Hz
   * 
   * @return
   */
  public int getSampleRate() {
    return sampleRate;
  }

  public void setSampleRate(int sampleRate) {
    if (isStarted()) throw new IllegalStateException("Cannot set sample rate when started");

    this.sampleRate = sampleRate;
  }

  /**
   * Defaults to 256. Must be a power of 2.
   * 
   * @return
   */
  public int getSampleSize() {
    return sampleSize;
  }

  public void setSampleSize(int sampleSize) {
    if (!isPowerOfTwo(sampleSize)) throw new IllegalArgumentException("Sample size must be a power of 2");

    this.sampleSize = sampleSize;
  }

  private boolean isPowerOfTwo(int sampleSize) {
    return (sampleSize & (sampleSize - 1)) == 0;
  }

  private void initTrigger() {
    triggerThread = new TriggerThread();
    triggerThread.start();
  }

  private void triggerProcessSignal() {
    processorService.execute(new Runnable() {

      @Override
      public void run() {
        processSignal(System.nanoTime());
      }
    });
  }

  protected void processSignal(long nanoTime) {
    if (rawSignal.size() < getSampleSize()) return;

    Long from = rawSignal.floorKey(nanoTime - ONE_SECOND_NANOS);
    if (from == null) return;

    ConcurrentNavigableMap<Long, Double> map = rawSignal.tailMap(from);

    if (map.size() < getSampleSize()) {
      cleanMap(from);
      return;
    }

    if (map.lastKey() > nanoTime + getBuffer()) {
      cleanMap(from);
      return;
    }

    double[] signal = createSignal(map);

    notifyListeners(nanoTime, signal);

    cleanMap(from);
  }

  // five ms.
  private long getBuffer() {
    return 5000000;
  }

  private double[] createSignal(ConcurrentNavigableMap<Long, Double> map) {
    double[] signal = new double[getSampleSize()];

    long interval = getInterval();

    Long key = map.firstKey();

    for (int i = 0; i < getSampleSize(); i++) {
      signal[i] = key == null ? 0 : map.get(key);

      Long next = key + interval;
      Long low = map.floorKey(next);
      Long high = map.ceilingKey(next);

      if (low != null && low > key) {
        key = low;
      } else if (high != null && high > key) {
        key = high;
      }
    }

    return signal;
  }

  private long getInterval() {
    BigDecimal perNanos = NANOS_PER_SECOND.divide(new BigDecimal(getSampleSize()), 0, BigDecimal.ROUND_HALF_UP);

    return perNanos.longValue();
  }

  private void cleanMap(Long lastKey) {
    rawSignal.headMap(lastKey).clear();
  }

  private void notifyListeners(final long nanos, final double[] signal) {
    notifierService.execute(new Runnable() {

      @Override
      public void run() {
        SignalInstance signalInstance = new SignalInstance(nanos, getSampleRate(), signal);
        for (SignalProcessorListener l : listeners) {
          l.signalProcessed(signalInstance);
        }
      }
    });
  }

  public class TriggerThread extends Thread {
    private volatile boolean run = true;

    private long millis;
    private int nanos;

    public TriggerThread() {
      super("Trigger Thread");
      setPriority(MAX_PRIORITY);
      calculateMillisAndNanos();
    }

    private void calculateMillisAndNanos() {
      BigDecimal oneThousand = new BigDecimal(1000);
      BigDecimal snoozeTime = oneThousand.divide(new BigDecimal(getSampleRate()), 3, RoundingMode.HALF_UP);

      millis = snoozeTime.longValue();

      BigDecimal nanoTime = snoozeTime.subtract(new BigDecimal(millis)).multiply(oneThousand);

      nanos = nanoTime.intValue();
    }

    public void run() {
      while (run) {
        try {
          sleep(millis, nanos);
        } catch (InterruptedException e) {
        }
        if (run) triggerProcessSignal();
      }
    }

    public void setRun(boolean run) {
      this.run = run;
    }

  }

}
