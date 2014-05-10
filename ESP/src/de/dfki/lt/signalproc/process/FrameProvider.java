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

package de.dfki.lt.signalproc.process;

import java.util.Arrays;

import de.dfki.lt.signalproc.util.DoubleDataSource;

// TODO: Auto-generated Javadoc
/**
 * Cut frames out of a given signal, and provide them one by one, optionally
 * applying a processor to the frame. This base implementation provides frames
 * of a fixed length with a fixed shift.
 * 
 * @author Marc Schr&ouml;der
 * @see PitchFrameProvider
 */
public class FrameProvider {
	
	/** The signal. */
	protected DoubleDataSource signal;
	
	/** The processor. */
	protected InlineDataProcessor processor;

	/**
	 * The sampling rate.
	 */
	protected int samplingRate;

	/**
	 * The start time of the currently analysed frame.
	 */
	protected long frameStart;
	
	/** The next frame start. */
	protected long nextFrameStart;
	
	/** The total read. */
	protected int totalRead = 0;
	
	/** The frame. */
	protected double[] frame;
	
	/** The valid samples in frame. */
	protected int validSamplesInFrame;

	/** The frame shift. */
	protected int frameShift;
	
	/** The frame length. */
	protected int frameLength;
	/**
	 * The part of the original signal to remember for the next overlapping frame.
	 */
	private double[] memory;
	// If !stopWhenTouchingEnd, we must continue reading from memory,
	// padding the respective frames with zeroes. This is the current
	// reading position:
	private int posInMemory;
	private boolean memoryFilled;

	/**
	 * Whether or not this frame provider stops when the first frame touches the
	 * last input sample.
	 */
	private boolean stopWhenTouchingEnd;

	/**
	 * Initialise a FrameProvider.
	 * 
	 * @param signal
	 *          the signal source to read from
	 * @param processor
	 *          an optional data processor to apply to the source signal. If null,
	 *          the original data will be returned.
	 * @param frameLength
	 *          the number of samples in one frame.
	 * @param frameShift
	 *          the number of samples by which to shift the window from one frame
	 *          analysis to the next; if this is smaller than window.getLength(),
	 *          frames will overlap.
	 * @param samplingRate
	 *          the number of samples in one second.
	 * @param stopWhenTouchingEnd
	 *          whether or not this frame provider stops when the first frame
	 *          touches the last input sample. When this is set to true, the last
	 *          frame will be the first one including the last sample; when this
	 *          is set to false, the last frame will be the last that still
	 *          contains any data.
	 */
	public FrameProvider(DoubleDataSource signal, InlineDataProcessor processor, int frameLength, int frameShift,
			int samplingRate, boolean stopWhenTouchingEnd) {
		this.signal = signal;
		this.processor = processor;
		this.frameShift = frameShift;
		this.frameLength = frameLength;
		this.samplingRate = samplingRate;
		this.frame = new double[frameLength];
		this.frameStart = -1;
		this.nextFrameStart = 0;
		validSamplesInFrame = 0;
		// We keep the previous frame in memory (we'll need this if frameShift <
		// frameLength):
		this.memory = new double[frameLength];
		posInMemory = memory.length; // "empty"
		memoryFilled = false;
		this.stopWhenTouchingEnd = stopWhenTouchingEnd;
	}

	/**
	 * Start position of current frame, in seconds.
	 *
	 * @return the start time of the last frame returned by getNextFrame(), or a
	 *         small negative number if no frame has been served yet.
	 */
	public double getFrameStartTime() {
		return (double) frameStart / samplingRate;
	}

	/**
	 * Start position of current frame, in samples.
	 *
	 * @return the start position of the last frame returned by getNextFrame(), or
	 *         -1 if no frame has been served yet.
	 */
	public long getFrameStartSamples() {
		return frameStart;
	}

	/**
	 * Gets the sampling rate.
	 *
	 * @return the sampling rate
	 */
	public int getSamplingRate() {
		return samplingRate;
	}

	/**
	 * The amount of time by which one frame is shifted against the next.
	 *
	 * @return the frame shift time
	 */
	public double getFrameShiftTime() {
		return (double) frameShift / samplingRate;
	}

	/**
	 * The number of samples by which one frame is shifted against the next.
	 *
	 * @return the frame shift samples
	 */
	public int getFrameShiftSamples() {
		return frameShift;
	}

	/**
	 * The time length of a frame.
	 *
	 * @return the frame length time
	 */
	public double getFrameLengthTime() {
		return (double) getFrameLengthSamples() / samplingRate;
	}

	/**
	 * The number of samples in the current frame.
	 *
	 * @return the frame length samples
	 */
	public int getFrameLengthSamples() {
		return frameLength;
	}

	/**
	 * Whether or not this frame provider stops when the first frame touches the
	 * last input sample. When this returns true, the last frame will be the first
	 * one including the last sample; when this returns false, the last frame will
	 * be the last that still contains any data. Defaults to true.
	 *
	 * @return true, if successful
	 */
	public boolean stopWhenTouchingEnd() {
		return stopWhenTouchingEnd;
	}

	/**
	 * Whether or not this frameprovider can provide another frame.
	 *
	 * @return true, if successful
	 */
	public boolean hasMoreData() {
		return signal.hasMoreData() || !stopWhenTouchingEnd && memoryFilled && posInMemory < memory.length;
	}

	/**
	 * This tells how many valid samples have been read into the current frame
	 * (before applying the optional data processor!).
	 *
	 * @return the int
	 */
	public int validSamplesInFrame() {
		return validSamplesInFrame;
	}

	/**
	 * Fill the internal double array with the next frame of data. The last frame,
	 * if only partially filled with the rest of the signal, is filled up with
	 * zeroes. If stopWhenTouchingEnd() returns true, this method will provide not
	 * more than a single zero-padded frame at the end of the signal.
	 * 
	 * @return the next frame on success, null on failure.
	 */
	public double[] getNextFrame() {
		frameStart = nextFrameStart;
		if (!hasMoreData()) {
			validSamplesInFrame = 0;
			return null;
		}
		// A frame is composed from two sources:
		// 1. memory and 2. newly read signal.
		int nFromMemory;
		// 1. Prepend some memory?
		if (memoryFilled && posInMemory < memory.length) {
			nFromMemory = memory.length - posInMemory;
			// System.err.println("Reusing " + nFromMemory +
			// " samples from previous frame");
			System.arraycopy(memory, posInMemory, frame, 0, nFromMemory);
		} else {
			nFromMemory = 0;
			// System.err.println("No data to reuse from previous frame.");
		}
		// 2. Read new bit of signal:
		int read = getData(nFromMemory);
		totalRead += read;
		// At end of input signal, we are unable to fill the frame completely:
		if (nFromMemory + read < frameLength) { // zero-pad last frame(s)
			assert !signal.hasMoreData();
			// Pad with zeroes.
			Arrays.fill(frame, nFromMemory + read, frame.length, 0);
		}
		validSamplesInFrame = nFromMemory + read; // = frame.length except for last
																							// frame
		// OK, the frame is filled.

		// For overlapping frames,
		// remember the frame data in order to reuse part of it for next frame
		int amountToRemember = frameLength - frameShift;
		if (validSamplesInFrame < frameLength) {
			amountToRemember = validSamplesInFrame - frameShift;
		}
		if (amountToRemember > 0) {
			if (memory.length < amountToRemember) {
				memory = new double[amountToRemember];
			}
			System.arraycopy(frame, validSamplesInFrame - amountToRemember, memory, memory.length - amountToRemember,
					amountToRemember);
			posInMemory = memory.length - amountToRemember;
			memoryFilled = true;
		} else {
			posInMemory = memory.length;
			memoryFilled = false;
		}

		// Apply the processor to the data:
		if (processor != null)
			processor.applyInline(frame, 0, frameLength);

		nextFrameStart = frameStart + frameShift;
		// System.err.println("FrameProvider: Frame "+" (" +
		// frameStartTime+"-"+(frameStartTime+getFrameLengthTime()) + "): read " +
		// read + "(total "+totalRead+"), posInMemory " + posInMemory +
		// ", memory.length " + memory.length + ", valid " + validSamplesInFrame);

		return frame;
	}

	/**
	 * Gets the current frame.
	 *
	 * @return the current frame
	 */
	public double[] getCurrentFrame() {
		return frame;
	}

	/**
	 * Read data from input signal into current frame. This base implementation
	 * will attempt to fill the frame from the position given in nPrefilled
	 * onwards.
	 * 
	 * @param nPrefilled
	 *          number of valid values at the beginning of frame. These should not
	 *          be lost or overwritten.
	 * @return the number of new values read into frame at position nPrefilled.
	 */
	protected int getData(int nPrefilled) {
		return signal.getData(frame, nPrefilled, frame.length - nPrefilled);
	}

	/**
	 * Reset the internal time stamp to 0.
	 */
	public void resetInternalTimer() {
		this.frameStart = -1;
		this.nextFrameStart = 0;
		this.totalRead = 0;
	}
}
