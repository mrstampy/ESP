/**
 * Copyright 2006 DFKI GmbH.
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
 * The Class DynamicWindow.
 *
 * @author Marc Schr&ouml;der
 */
public class DynamicWindow implements InlineDataProcessor {
	
	/** The window type. */
	protected int windowType;

	/**
	 * An inline data processor applying a window of the requested type to the
	 * data. The window length will always be equal to the data length.
	 *
	 * @param windowType the window type
	 */
	public DynamicWindow(int windowType) {
		this.windowType = windowType;
	}

	/**
	 * Values.
	 *
	 * @param len the len
	 * @return the double[]
	 */
	public double[] values(int len) {
		Window w = Window.get(windowType, len);
		return w.window;
	}

	/**
	 * apply a window of the specified type, with length len, to the data.
	 *
	 * @param data the data
	 * @param off the off
	 * @param len the len
	 */
	public void applyInline(double[] data, int off, int len) {
		Window w = Window.get(windowType, len);
		w.applyInline(data, off, len);
	}

}
