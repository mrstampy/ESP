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

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * This class is a convenience class to assist in aggregation of the power of
 * the {@link SignalInstance}s produced by the {@link SignalProcessor} over
 * time. It is of use when monitoring a set of frequencies. Only power
 * information for the specified frequencies is extracted. Should another
 * frequency be specified an exception will be thrown.
 * 
 * @author burton
 * 
 */
public class PowerInstance extends DspInstance {
  private static final long serialVersionUID = -5379475359721093457L;

  private double[] frequencies;
  private SignalInstance signalInstance;

  private ConcurrentNavigableMap<Double, Double> powers;

  /**
   * Creates an instance for the specified {@link SignalInstance} and extracts
   * the power of the signal at the specified frequencies.
   * 
   * @param signalInstance
   * @param frequencies
   */
  public PowerInstance(SignalInstance signalInstance, double... frequencies) {
    super(signalInstance.getNanos(), signalInstance.getSampleRate());

    this.frequencies = frequencies;
    this.signalInstance = signalInstance;

    init();
  }

  /**
   * Powers are calculated as a weighted average of powers in the PowerInstance
   * list supplied. The last (most recent) PowerInstance has a weight of
   * list.size, the next last a weight of list.size - 1 etc. It is assumed that
   * the same array of frequencies was used to create all instances in the list. An
   * exception is thrown if otherwise.
   * 
   * @param nanos
   * @param sampleRate
   * @param instances
   * @throws IllegalArgumentException
   *           if a key specified in one instance does not exist for all
   *           instances
   */
  public PowerInstance(long nanos, double sampleRate, List<PowerInstance> instances) {
    super(nanos, sampleRate);

    if (instances != null && !instances.isEmpty()) createMap(instances);
  }

  /**
   * Returns the map of frequency:power pairs
   * 
   * @return
   */
  public ConcurrentNavigableMap<Double, Double> getPowers() {
    if (powers == null) createMap();
    return powers;
  }

  /**
   * Returns the power at the specified frequency, or throws an exception if a
   * frequency different from one used to create this instance is specified.
   * 
   * @param frequency
   * @return
   */
  public Double getPowerAt(double frequency) {
    Double power = powers.get(frequency);

    if (power == null) throw new IllegalArgumentException("No power for frequency " + frequency);

    return power;
  }

  private void createMap(List<PowerInstance> instances) {
    Set<Double> frequencies = instances.get(0).getPowers().keySet();

    createMap();

    int arraySize = getWeightedAverageArraySize(instances.size());

    for (Double freq : frequencies) {
      powers.put(freq, aggregatePower(freq, instances, arraySize));
    }
  }

  private Double aggregatePower(Double freq, List<PowerInstance> instances, int arraySize) {
    double[] powers = new double[arraySize];

    int powersIdx = 0;
    for (int i = instances.size() - 1; i >= 0; i--) {
      Double val = instances.get(i).getPowers().get(freq);
      if (val == null) throw new IllegalArgumentException("Value missing for frequency " + freq);
      powersIdx = populateArray(powers, val, powersIdx, i + 1);
    }

    double rms = getRMS(powers);

    return rms;
  }

  private int populateArray(double[] powers, Double val, int powersIdx, int weighting) {
    int idx = powersIdx;

    for (int i = 0; i < weighting; i++) {
      powers[idx] = val;
      idx++;
    }

    return idx;
  }

  private int getWeightedAverageArraySize(int listSize) {
    int size = 0;

    for (int i = 1; i <= listSize; i++) {
      size += i;
    }

    return size;
  }

  private double getRMS(double... vals) {
    double sum = 0;
    for (double d : vals) {
      sum += d * d;
    }

    return Math.sqrt(sum / vals.length);
  }

  private void init() {
    check();

    createMap();

    signalInstance.setNormalizeAgainst(frequencies);

    for (double freq : frequencies) {
      powers.put(freq, signalInstance.getPowerAt(freq));
    }
  }

  private void createMap() {
    powers = new ConcurrentSkipListMap<Double, Double>();
  }

  private void check() {
    if (signalInstance == null) throw new IllegalArgumentException("No SignalInstance specified");
    if (frequencies == null || frequencies.length == 0) throw new IllegalArgumentException("No frequencies specified");

    frequencyCheck(frequencies);
  }

}
