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

import de.dfki.lt.signalproc.FFT;
import de.dfki.lt.signalproc.process.FrameProvider;
import de.dfki.lt.signalproc.util.BlockwiseDoubleDataSource;
import de.dfki.lt.signalproc.util.BufferedDoubleDataSource;
import de.dfki.lt.signalproc.util.DoubleDataSource;
import de.dfki.lt.signalproc.util.MathUtils;
import de.dfki.lt.signalproc.util.SequenceDoubleDataSource;

// TODO: Auto-generated Javadoc
/**
 * The Class FIRFilter.
 *
 * @author Marc Schr&ouml;der A filter corresponding to a
 *         finite-impulse-response LTI system. The filtering of the input signal
 *         corresponds to a convolution with the impulse response of the system.
 */
public class FIRFilter {
	
	/** The transformed ir. */
	protected double[] transformedIR;
	
	/** The impulse response length. */
	protected int impulseResponseLength;
	
	/** The slice length. */
	protected int sliceLength;

	/**
	 * Create a new, uninitialised FIR filter. Subclasses need to call
	 * 
	 * @see #initialise() separately.
	 */
	protected FIRFilter() {

	}

	/**
	 * Create a new Finite Impulse Response filter.
	 * 
	 * @param impulseResponse
	 *          the impulse response signal
	 */
	public FIRFilter(double[] impulseResponse) {
		int sliceLen = MathUtils.closestPowerOfTwoAbove(2 * impulseResponse.length) - impulseResponse.length;
		initialise(impulseResponse, sliceLen);
	}

	/**
	 * Instantiates a new FIR filter.
	 *
	 * @param impulseResponse the impulse response
	 * @param len the len
	 */
	public FIRFilter(double[] impulseResponse, int len) {
		initialise(impulseResponse, len);
	}

	/**
	 * Initialise the Finite Impulse Response filter.
	 *
	 * @param impulseResponse          the impulse response signal
	 * @param sliceLen the slice len
	 */
	protected void initialise(double[] impulseResponse, int sliceLen) {
		if (!MathUtils.isPowerOfTwo(impulseResponse.length + sliceLen))
			throw new IllegalArgumentException("Impulse response length plus slice length must be a power of two");
		this.impulseResponseLength = impulseResponse.length;
		this.sliceLength = sliceLen;
		transformedIR = new double[sliceLen + impulseResponse.length];
		System.arraycopy(impulseResponse, 0, transformedIR, 0, impulseResponse.length);
		FFT.realTransform(transformedIR, false);
		// This means, we are not actually saving the impulseResponse, but only
		// its complex FFT transform.
	}

	/**
	 * Apply this filter to the given input signal. The input signal is filtered
	 * piece by piece, as it is read from the data source returned by this method.
	 * This is the recommended way to filter longer signals.
	 * 
	 * @param signal
	 *          the signal to which this filter should be applied
	 * @return a DoubleDataSource from which the data can be read
	 */
	public DoubleDataSource apply(DoubleDataSource signal) {
		return new FIROutput(signal);
	}

	/**
	 * Apply this filter to the given input signal. This method filters the entire
	 * signal, and returns the entire filtered signal. For long signals, it is
	 * better to use apply(DoubleDataSource).
	 * 
	 * @param signal
	 *          the signal to which this filter should be applied
	 * @return the filtered signal.
	 */
	public double[] apply(double[] signal) {
		return new FIROutput(new BufferedDoubleDataSource(signal)).getAllData();
	}

	/**
	 * The Class FIROutput.
	 */
	public class FIROutput extends BlockwiseDoubleDataSource {
		
		/** The frame provider. */
		protected FrameProvider frameProvider;

		/**
		 * Instantiates a new FIR output.
		 *
		 * @param inputSource the input source
		 */
		public FIROutput(DoubleDataSource inputSource) {
			super(null, sliceLength);
			int frameLength = sliceLength + impulseResponseLength;
			assert MathUtils.isPowerOfTwo(frameLength);
			// Need to start with zero padding of length impulseResponseLength:
			DoubleDataSource padding = new BufferedDoubleDataSource(new double[impulseResponseLength]);
			DoubleDataSource paddedSource = new SequenceDoubleDataSource(new DoubleDataSource[] { padding, inputSource });
			this.frameProvider = new FrameProvider(paddedSource, null, frameLength, sliceLength, 1, false);
		}

		/* (non-Javadoc)
		 * @see de.dfki.lt.signalproc.util.BufferedDoubleDataSource#hasMoreData()
		 */
		public boolean hasMoreData() {
			return frameProvider.hasMoreData();
		}

		/**
		 * Try to get a block of getBlockSize() doubles from this DoubleDataSource,
		 * and copy them into target, starting from targetPos.
		 *
		 * @param target          the double array to write into
		 * @param targetPos          position in target where to start writing
		 * @return the amount of data actually delivered, which will be either
		 *         length or 0. If 0 is returned, all further calls will also return
		 *         0 and not copy anything.
		 */
		protected int readBlock(double[] target, int targetPos) {
			double[] frame = frameProvider.getNextFrame();
			if (frame == null) { // already finished processing
				return 0;
			}
			assert blockSize <= frameProvider.getFrameLengthSamples();
			assert blockSize == frameProvider.getFrameShiftSamples();
			// Now do the convolution:
			double[] convResult = FFT.convolve_FD(frame, transformedIR);
			int toCopy = blockSize;
			if (frameProvider.validSamplesInFrame() < blockSize)
				toCopy = frameProvider.validSamplesInFrame();
			// System.err.println("Copying " + toCopy);
			// Overlap-save approach:
			// always ignore the first "impulseResponseLength" samples in convResult,
			// because they are contaminated due to circular convolution:
			System.arraycopy(convResult, impulseResponseLength, target, targetPos, toCopy);
			return toCopy;
		}
	}

	/**
	 * Apply inline.
	 *
	 * @param data the data
	 * @param off the off
	 * @param len the len
	 */
	public void applyInline(double[] data, int off, int len) {
		double[] dataOut = apply(data);

		System.arraycopy(dataOut, 0, data, 0, len);
	}
}
