/*
 *  Copyright (c) 2007 - 2008 by Damien Di Fede <ddf@compartmental.net>
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package ddf.minim.analysis;


// TODO: Auto-generated Javadoc
/**
 * FFT stands for Fast Fourier Transform. It is an efficient way to calculate
 * the Complex Discrete Fourier Transform. There is not much to say about this
 * class other than the fact that when you want to analyze the spectrum of an
 * audio buffer you will almost always use this class. One restriction of this
 * class is that the audio buffers you want to analyze must have a length that
 * is a power of two. If you try to construct an FFT with a
 * <code>timeSize</code> that is not a power of two, an IllegalArgumentException
 * will be thrown.
 *
 * @author Damien Di Fede
 * @see FourierTransform
 * @see <a href="http://www.dspguide.com/ch12.htm">The Fast Fourier
 *      Transform</a>
 */
public class FFT extends FourierTransform {
  /**
   * Constructs an FFT that will accept sample buffers that are
   * <code>timeSize</code> long and have been recorded with a sample rate of
   * <code>sampleRate</code>. <code>timeSize</code> <em>must</em> be a power of
   * two. This will throw an exception if it is not.
   * 
   * @param timeSize
   *          the length of the sample buffers you will be analyzing
   * @param sampleRate
   *          the sample rate of the audio you will be analyzing
   */
  public FFT(int timeSize, double sampleRate) {
    super(timeSize, sampleRate);
    if ((timeSize & (timeSize - 1)) != 0) throw new IllegalArgumentException("FFT: timeSize must be a power of two.");
    buildReverseTable();
    buildTrigTables();
  }

  /* (non-Javadoc)
   * @see ddf.minim.analysis.FourierTransform#allocateArrays()
   */
  protected void allocateArrays() {
    spectrum = new double[timeSize / 2 + 1];
    real = new double[timeSize];
    imag = new double[timeSize];
  }

  /* (non-Javadoc)
   * @see ddf.minim.analysis.FourierTransform#scaleBand(int, double)
   */
  public void scaleBand(int i, double s) {
    if (s < 0) {
      throw new RuntimeException("Can't scale a frequency band by a negative value.");
    }

    real[i] *= s;
    imag[i] *= s;
    spectrum[i] *= s;

    if (i != 0 && i != timeSize / 2) {
      real[timeSize - i] = real[i];
      imag[timeSize - i] = -imag[i];
    }
  }

  /* (non-Javadoc)
   * @see ddf.minim.analysis.FourierTransform#setBand(int, double)
   */
  public void setBand(int i, double a) {
    if (a < 0) {
      throw new RuntimeException("Can't set a frequency band to a negative value.");
    }
    if (real[i] == 0 && imag[i] == 0) {
      real[i] = a;
      spectrum[i] = a;
    } else {
      real[i] /= spectrum[i];
      imag[i] /= spectrum[i];
      spectrum[i] = a;
      real[i] *= spectrum[i];
      imag[i] *= spectrum[i];
    }
    if (i != 0 && i != timeSize / 2) {
      real[timeSize - i] = real[i];
      imag[timeSize - i] = -imag[i];
    }
  }

  // performs an in-place fft on the data in the real and imag arrays
  // bit reversing is not necessary as the data will already be bit reversed
  private void fft() {
    for (int halfSize = 1; halfSize < real.length; halfSize *= 2) {
      // double k = -(double)Math.PI/halfSize;
      // phase shift step
      // double phaseShiftStepR = (double)Math.cos(k);
      // double phaseShiftStepI = (double)Math.sin(k);
      // using lookup table
      double phaseShiftStepR = cos(halfSize);
      double phaseShiftStepI = sin(halfSize);
      // current phase shift
      double currentPhaseShiftR = 1.0f;
      double currentPhaseShiftI = 0.0f;
      for (int fftStep = 0; fftStep < halfSize; fftStep++) {
        for (int i = fftStep; i < real.length; i += 2 * halfSize) {
          int off = i + halfSize;
          double tr = (currentPhaseShiftR * real[off]) - (currentPhaseShiftI * imag[off]);
          double ti = (currentPhaseShiftR * imag[off]) + (currentPhaseShiftI * real[off]);
          real[off] = real[i] - tr;
          imag[off] = imag[i] - ti;
          real[i] += tr;
          imag[i] += ti;
        }
        double tmpR = currentPhaseShiftR;
        currentPhaseShiftR = (tmpR * phaseShiftStepR) - (currentPhaseShiftI * phaseShiftStepI);
        currentPhaseShiftI = (tmpR * phaseShiftStepI) + (currentPhaseShiftI * phaseShiftStepR);
      }
    }
  }

  /* (non-Javadoc)
   * @see ddf.minim.analysis.FourierTransform#forward(double[])
   */
  public void forward(double[] buffer) {
    if (buffer.length != timeSize) {
      throw new RuntimeException("FFT.forward: The length of the passed sample buffer must be equal to timeSize().");
    }
    doWindow(buffer);
    // copy samples to real/imag in bit-reversed order
    bitReverseSamples(buffer, 0);
    // perform the fft
    fft();
    // fill the spectrum buffer with amplitudes
    fillSpectrum();
  }

  /* (non-Javadoc)
   * @see ddf.minim.analysis.FourierTransform#forward(double[], int)
   */
  @Override
  public void forward(double[] buffer, int startAt) {
    if (buffer.length - startAt < timeSize) {
      throw new RuntimeException("FourierTransform.forward: not enough samples in the buffer between " + startAt
          + " and " + buffer.length + " to perform a transform.");
    }

    windowFunction.apply(buffer, startAt, timeSize);
    bitReverseSamples(buffer, startAt);
    fft();
    fillSpectrum();
  }

  /**
   * Performs a forward transform on the passed buffers.
   * 
   * @param buffReal
   *          the real part of the time domain signal to transform
   * @param buffImag
   *          the imaginary part of the time domain signal to transform
   */
  public void forward(double[] buffReal, double[] buffImag) {
    if (buffReal.length != timeSize || buffImag.length != timeSize) {
      throw new RuntimeException("FFT.forward: The length of the passed buffers must be equal to timeSize().");
    }
    setComplex(buffReal, buffImag);
    bitReverseComplex();
    fft();
    fillSpectrum();
  }

  /* (non-Javadoc)
   * @see ddf.minim.analysis.FourierTransform#inverse(double[])
   */
  public void inverse(double[] buffer) {
    if (buffer.length > real.length) {
      throw new RuntimeException("FFT.inverse: the passed array's length must equal FFT.timeSize().");
    }
    // conjugate
    for (int i = 0; i < timeSize; i++) {
      imag[i] *= -1;
    }
    bitReverseComplex();
    fft();
    // copy the result in real into buffer, scaling as we do
    for (int i = 0; i < buffer.length; i++) {
      buffer[i] = real[i] / real.length;
    }
  }

  private int[] reverse;

  private void buildReverseTable() {
    int N = timeSize;
    reverse = new int[N];

    // set up the bit reversing table
    reverse[0] = 0;
    for (int limit = 1, bit = N / 2; limit < N; limit <<= 1, bit >>= 1)
      for (int i = 0; i < limit; i++)
        reverse[i + limit] = reverse[i] + bit;
  }

  // copies the values in the samples array into the real array
  // in bit reversed order. the imag array is filled with zeros.
  private void bitReverseSamples(double[] samples, int startAt) {
    for (int i = 0; i < timeSize; ++i) {
      real[i] = samples[startAt + reverse[i]];
      imag[i] = 0.0f;
    }
  }

  // bit reverse real[] and imag[]
  private void bitReverseComplex() {
    double[] revReal = new double[real.length];
    double[] revImag = new double[imag.length];
    for (int i = 0; i < real.length; i++) {
      revReal[i] = real[reverse[i]];
      revImag[i] = imag[reverse[i]];
    }
    real = revReal;
    imag = revImag;
  }

  // lookup tables

  private double[] sinlookup;
  private double[] coslookup;

  private double sin(int i) {
    return sinlookup[i];
  }

  private double cos(int i) {
    return coslookup[i];
  }

  private void buildTrigTables() {
    int N = timeSize;
    sinlookup = new double[N];
    coslookup = new double[N];
    for (int i = 0; i < N; i++) {
      sinlookup[i] = (double) Math.sin(-(double) Math.PI / i);
      coslookup[i] = (double) Math.cos(-(double) Math.PI / i);
    }
  }
}
