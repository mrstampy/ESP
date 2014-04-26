/**
 * Copyright 2004-2006 DFKI GmbH.
 * All Rights Reserved.  Use is subject to license terms.
 * 
 * Permission is hereby granted, free of charge, to use and distribute
 * this software and its documentation without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of this work, and to
 * permit persons to whom this work is furnished to do so, subject to
 * the following conditions:
 * 
 * 1. The code must retain the above copyright notice, this list of
 *    conditions and the following disclaimer.
 * 2. Any modifications must be clearly marked as such.
 * 3. Original authors' names are not deleted.
 * 4. The authors' names are not used to endorse or promote products
 *    derived from this software without specific prior written
 *    permission.
 *
 * DFKI GMBH AND THE CONTRIBUTORS TO THIS WORK DISCLAIM ALL WARRANTIES WITH
 * REGARD TO THIS SOFTWARE, INCLUDING ALL IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS, IN NO EVENT SHALL DFKI GMBH NOR THE
 * CONTRIBUTORS BE LIABLE FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL
 * DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 * PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS
 * ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF
 * THIS SOFTWARE.
 */

package de.dfki.lt.signalproc.filter;

import de.dfki.lt.signalproc.util.MathUtils;
import de.dfki.lt.signalproc.window.BlackmanWindow;
import de.dfki.lt.signalproc.window.Window;

/**
 * @author Marc Schr&ouml;der
 */
public class LowPassFilter extends FIRFilter {
	public static double DEFAULT_TRANSITIONBANDWIDTH = 0.01;

	/**
	 * Create a new lowpass filter with the given normalised cutoff frequency and
	 * a default transition band width.
	 * 
	 * @param normalisedCutoffFrequency
	 *          the cutoff frequency of the lowpass filter, expressed as a
	 *          fraction of the sampling rate. It must be in the range ]0, 0.5[.
	 *          For example, with a sampling rate of 16000 Hz and a desired cutoff
	 *          frequency of 4000 Hz, the normalisedCutoffFrequency would have to
	 *          be 0.25.
	 */
	public LowPassFilter(double normalisedCutoffFrequency) {
		this(normalisedCutoffFrequency, DEFAULT_TRANSITIONBANDWIDTH);
	}

	/**
	 * Create a new lowpass filter with the given normalised cutoff frequency and
	 * the given normalised transition band width.
	 * 
	 * @param normalisedCutoffFrequency
	 *          the cutoff frequency of the lowpass filter, expressed as a
	 *          fraction of the sampling rate. It must be in the range ]0, 0.5[.
	 *          For example, with a sampling rate of 16000 Hz and a desired cutoff
	 *          frequency of 4000 Hz, the normalisedCutoffFrequency would have to
	 *          be 0.25.
	 * @param normalisedTransitionBandwidth
	 *          indicates the desired quality of the filter. The smaller the
	 *          bandwidth, the more abrupt the cutoff at the cutoff frequency, but
	 *          also the larger the filter kernel (impulse response) and
	 *          computationally costly the filter. Usual range of this parameter
	 *          is [0.002, 0.2].
	 */
	public LowPassFilter(double normalisedCutoffFrequency, double normalisedTransitionBandwidth) {
		this(normalisedCutoffFrequency, bandwidth2kernelLength(normalisedTransitionBandwidth));
	}

	/**
	 * Create a new lowpass filter with the given normalised cutoff frequency and
	 * the given length of the filter kernel.
	 * 
	 * @param normalisedCutoffFrequency
	 *          the cutoff frequency of the lowpass filter, expressed as a
	 *          fraction of the sampling rate. It must be in the range ]0, 0.5[.
	 *          For example, with a sampling rate of 16000 Hz and a desired cutoff
	 *          frequency of 4000 Hz, the normalisedCutoffFrequency would have to
	 *          be 0.25.
	 * @param kernelLength
	 *          length of the filter kernel (the impulse response). The kernel
	 *          length must be an odd number. The longer the kernel, the sharper
	 *          the cutoff, i.e. the narrower the transition band. Typical lengths
	 *          are in the range of 10-1000.
	 * @throws IllegalArgumentException
	 *           if the kernel length is not a positive, odd number, or if
	 *           normalisedCutoffFrequency is not in the range between 0 and 0.5.
	 */
	public LowPassFilter(double normalisedCutoffFrequency, int kernelLength) {
		super();
		if (kernelLength <= 0 || kernelLength % 2 == 0) {
			throw new IllegalArgumentException("Kernel length must be an odd positive number, got " + kernelLength);
		}
		if (normalisedCutoffFrequency <= 0 || normalisedCutoffFrequency >= 0.5) {
			throw new IllegalArgumentException("Normalised cutoff frequency must be between 0 and 0.5, got "
					+ normalisedCutoffFrequency);
		}
		double[] kernel = getKernel(normalisedCutoffFrequency, kernelLength);
		// determine the length of the slices by which the signal will be consumed:
		// this is the distance to the second next power of two, so that the slice
		// will be at least as long as the kernel.
		sliceLength = MathUtils.closestPowerOfTwoAbove(2 * kernelLength) - kernelLength;
		initialise(kernel, sliceLength);
	}

	/**
	 * For a given sampling rate, return the width of the transition band for this
	 * filter, in Hertz.
	 * 
	 * @param samplingRate
	 *          the sampling rate, in Hertz.
	 */
	public double getTransitionBandWidth(int samplingRate) {
		return samplingRate * kernelLength2bandwidth(impulseResponseLength);
	}

	/**
	 * Compute the low-pass filter kernel, using a Blackman window.
	 */
	protected static double[] getKernel(double normalisedCutoffFrequency, int kernelLength) {
		double[] kernel = new double[kernelLength];
		int m = (kernelLength - 1) / 2;
		double fc = normalisedCutoffFrequency;
		double sum = 0.;
		Window window = new BlackmanWindow(kernelLength);
		for (int i = 0; i < m; i++) {
			kernel[i] = Math.sin(2 * Math.PI * fc * (i - m)) / (i - m) * window.value(i);
			kernel[kernelLength - i - 1] = kernel[i];
			sum += 2 * kernel[i];
		}
		kernel[m] = 2 * Math.PI * fc;
		sum += kernel[m];
		// Normalise to area 1:
		for (int i = 0; i < kernelLength; i++) {
			kernel[i] /= sum;
		}
		return kernel;
	}

	/**
	 * Convert from normalisedTransitionBandwidth to filter kernel length, using
	 * the approximate formula l = 4/bw.
	 * 
	 * @param normalisedTransitionBandwidth
	 * @return the corresponding filter kernel length (guaranteed to be an odd
	 *         number).
	 */
	protected static int bandwidth2kernelLength(double normalisedTransitionBandwidth) {
		int l = (int) (4 / normalisedTransitionBandwidth);
		// kernel length must be odd:
		if (l % 2 == 0)
			l++;
		return l;
	}

	/**
	 * Convert from filter kernel length to normalisedTransitionBandwidth, using
	 * the approximate formula l = 4/bw.
	 * 
	 * @param kernelLength
	 * @return the corresponding normalised transition bandwidth.
	 */
	protected static double kernelLength2bandwidth(int kernelLength) {
		double bw = (double) 4 / kernelLength;
		return bw;
	}

	public String toString() {
		return "Lowpass filter";
	}
}
