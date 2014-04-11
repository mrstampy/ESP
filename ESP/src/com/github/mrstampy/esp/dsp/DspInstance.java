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

import java.io.Serializable;

/**
 * Convenience superclass.
 * 
 * @author burton
 * 
 */
public abstract class DspInstance implements Serializable {
  private static final long serialVersionUID = -426610299873937636L;

  private final double sampleRate;

  private final long nanos;

  protected DspInstance(long nanos, double sampleRate) {
    this.nanos = nanos;
    this.sampleRate = sampleRate;
  }

  public double getSampleRate() {
    return sampleRate;
  }

  public long getNanos() {
    return nanos;
  }

  /**
   * Ensures that the frequencies are in the range 0 < frequency <=
   * {@link #getSampleRate()} / 2.
   * 
   * @param frequencies
   * @throws IllegalArgumentException
   *           if a frequency is out of range.
   */
  protected void frequencyCheck(double... frequencies) {
    double maxFrequency = getSampleRate() / 2;
    int idx = 0;
    for (double f : frequencies) {
      if (f > maxFrequency || f <= 0) {
        throw new IllegalArgumentException(f + " at " + idx + " is invalid.  0 < frequency <= " + maxFrequency);
      }
      idx++;
    }
  }

}
