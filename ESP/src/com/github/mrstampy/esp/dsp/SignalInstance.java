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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ddf.minim.analysis.BartlettHannWindow;
import ddf.minim.analysis.BartlettWindow;
import ddf.minim.analysis.BlackmanWindow;
import ddf.minim.analysis.CosineWindow;
import ddf.minim.analysis.FFT;
import ddf.minim.analysis.GaussWindow;
import ddf.minim.analysis.HammingWindow;
import ddf.minim.analysis.HannWindow;
import ddf.minim.analysis.LanczosWindow;
import ddf.minim.analysis.RectangularWindow;
import ddf.minim.analysis.TriangularWindow;
import ddf.minim.analysis.WindowFunction;

/**
 * A signal instance is the current time - one second's worth of signal data.
 * 
 * @author burton
 * 
 */
public class SignalInstance extends DspInstance {
  private static final long serialVersionUID = 3609327634526974284L;

  private final double[] signal;

  private FFT fft = null;

  private BigDecimal scaleFactor;

  private Lock lock = new ReentrantLock();

  private double[] normalizeAgainst = null;

  public enum Window {
    BARTLETT_HANN_WINDOW, BARTLETT_WINDOW, BLACKMAN_WINDOW, COSINE_WINDOW, GAUSS_WINDOW, HAMMING_WINDOW, HANN_WINDOW, LANCZOS_WINDOW, RECTANGULAR_WINDOW, TRIANGULAR_WINDOW;
  }

  private static Window WINDOW = Window.HAMMING_WINDOW;

  private WindowFunction windowFunction;

  public SignalInstance(long nanos, double sampleRate, double[] signal) {
    super(nanos, sampleRate);
    this.signal = signal;
  }

  /**
   * Returns the normalized power at the specified frequency.
   * 
   * @param frequency
   * @return
   * @see #setNormalizeAgainst(double[])
   */
  public double getPowerAt(double frequency) {
    frequencyCheck(frequency);

    lock.lock();
    try {
      if (fft == null) createFFT();
    } finally {
      lock.unlock();
    }

    return new BigDecimal(getPower(frequency)).multiply(scaleFactor).doubleValue();
  }

  private double getPower(double frequency) {
    BigDecimal bd = new BigDecimal(frequency).multiply(new BigDecimal(fft.getBandWidth()));

    return fft.getFreq(bd.doubleValue());
  }

  private void createFFT() {
    fft = new FFT(getSignal().length, getSampleRate());

    fft.window(getWindowFunction());
    fft.forward(getSignal());

    scaleFactor = getScaleFactor();
  }

  private BigDecimal getScaleFactor() {
    double max = normalizeAgainst() ? frequencyMax() : spectrumMax();

    return BigDecimal.ONE.divide(new BigDecimal(max), 10, RoundingMode.HALF_UP);
  }

  private boolean normalizeAgainst() {
    return normalizeAgainst != null && normalizeAgainst.length > 0;
  }

  private double frequencyMax() {
    double max = Double.MIN_VALUE;
    for (int i = 0; i < normalizeAgainst.length; i++) {
      double p = getPower(normalizeAgainst[i]);
      if (p > max) max = p;
    }
    return max;
  }

  private double spectrumMax() {
    double max = Double.MIN_VALUE;
    for (double d = 1; d <= getSampleRate() / 2; d += 0.1) {
      double p = getPower(d);
      if (p > max) max = p;
    }
    return max;
  }

  public double[] getSignal() {
    return signal;
  }

  public double[] getNormalizeAgainst() {
    return normalizeAgainst;
  }

  /**
   * An array of frequencies of interest against which the value returned by
   * {@link #getPowerAt(double)} is normalized. If null or zero length the
   * frequencies across the spectrum are used for normalization. If required it
   * must be invoked prior to the first call to {@link #getPowerAt(double)}.
   * 
   * @param normalizeAgainst
   * @throws IllegalArgumentException
   *           if a frequency is out of range.
   * 
   */
  public void setNormalizeAgainst(double[] normalizeAgainst) {
    this.normalizeAgainst = normalizeAgainst;
    if (normalizeAgainst == null || normalizeAgainst.length == 0) return;

    frequencyCheck(normalizeAgainst);
  }

  /**
   * Sets the windowing function to be used for all instances. Defaults to the
   * {@link HammingWindow}. Default constructors are used. Use the
   * {@link #setWindowFunction(WindowFunction)} method to override. If required
   * it must be invoked prior to the first call to {@link #getPowerAt(double)}.
   * 
   * @param window
   */
  public static void setWindow(Window window) {
    SignalInstance.WINDOW = window;
  }

  public WindowFunction getWindowFunction() {
    if (windowFunction == null) windowFunction = getWindow();
    return windowFunction;
  }

  private WindowFunction getWindow() {
    switch (WINDOW) {
    case BARTLETT_HANN_WINDOW:
      return new BartlettHannWindow();
    case BARTLETT_WINDOW:
      return new BartlettWindow();
    case BLACKMAN_WINDOW:
      return new BlackmanWindow();
    case COSINE_WINDOW:
      return new CosineWindow();
    case GAUSS_WINDOW:
      return new GaussWindow();
    case HAMMING_WINDOW:
      return new HammingWindow();
    case HANN_WINDOW:
      return new HannWindow();
    case LANCZOS_WINDOW:
      return new LanczosWindow();
    case RECTANGULAR_WINDOW:
      return new RectangularWindow();
    case TRIANGULAR_WINDOW:
      return new TriangularWindow();
    default:
      return null;
    }
  }

  /**
   * Sets the {@link WindowFunction} to use in the FFT calculation for this
   * instance. Must be invoked prior to the first call to
   * {@link #getPowerAt(double)} to be effective, else it is ignored.
   * 
   * @param windowFunction
   */
  public void setWindowFunction(WindowFunction windowFunction) {
    this.windowFunction = windowFunction;
  }

}
