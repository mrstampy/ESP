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

package de.dfki.lt.signalproc.window;

// TODO: Auto-generated Javadoc
/**
 * The Class GaussWindow.
 *
 * @author Marc Schr&ouml;der
 */
public class GaussWindow extends Window {
	
	/** The Constant DEFAULT_SIGMA. */
	public static final double DEFAULT_SIGMA = 100;
	
	/** The sigma. */
	protected double sigma;
	
	/** The sigmasquare. */
	protected double sigmasquare;

	/**
	 * Crate a Gauss window with the given length and a default sigma.
	 * 
	 * @param length
	 *          the length of the window, in samples (must be an odd number)
	 */
	public GaussWindow(int length) {
		this(length, DEFAULT_SIGMA, 1.);
	}

	/**
	 * Crate a Gauss window with the given length and a default sigma, and apply a
	 * prescaling factor to each sample in the window.
	 *
	 * @param length          the length of the window, in samples (must be an odd number)
	 * @param prescalingFactor the prescaling factor
	 */
	public GaussWindow(int length, double prescalingFactor) {
		this(length, DEFAULT_SIGMA, prescalingFactor);
	}

	/**
	 * Create a Gauss window with the given length and sigma.
	 *
	 * @param length          the length of the window, in samples (should be an odd number)
	 * @param sigma          the sigma coefficient in the Gauss curve. A good starting point is
	 *          100.
	 * @param prescalingFactor the prescaling factor
	 */
	public GaussWindow(int length, double sigma, double prescalingFactor) {
		window = new double[length];
		this.sigma = sigma;
		this.sigmasquare = sigma * sigma;
		this.prescalingFactor = prescalingFactor;
		initialise();
	}

	/* (non-Javadoc)
	 * @see de.dfki.lt.signalproc.window.Window#initialise()
	 */
	protected void initialise() {
		int mid = window.length / 2 + 1;
		for (int i = 0; i < window.length; i++) {
			int dist = i - mid;
			window[i] = Math.exp(-0.5 * dist * dist / sigmasquare);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Gauss window";
	}

}
