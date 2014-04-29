/*
 * ESP Copyright (C) 2013 - 2014 Burton Alexander
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
package com.github.mrstampy.esp.multiconnectionsocket;

import java.util.Arrays;

public class MovingWindowBuffer {

	private double[] buffer;

	private int idx;

	private int capacity;

	public MovingWindowBuffer(int capacity) {
		this.capacity = capacity;
		buffer = new double[capacity];
		idx = 0;
	}

	public void addAll(double... values) {
		if (idx > capacity - values.length) {
			int offset = values.length - (capacity - idx);

			shift(offset);
			idx -= offset;
		}

		System.arraycopy(values, 0, buffer, idx, values.length);
		idx += values.length;
	}

	public double[] snapshot() {
		double[] snap = new double[capacity];
		System.arraycopy(buffer, 0, snap, 0, capacity);

		return snap;
	}

	public void resize(int newSize) {
		double[] newBuf = new double[newSize];

		int offset = newSize < capacity ? capacity - newSize : 0;

		System.arraycopy(buffer, offset, newBuf, 0, Math.min(capacity, newSize));

		synchronized (buffer) {
			buffer = newBuf;
		}
		
		capacity = newSize;

		idx = Math.min(idx, newSize - 1);
	}
	
	public void clear() {
		Arrays.fill(buffer, 0);
	}

	private void shift(int howMuch) {
		double[] newBuf = new double[capacity];

		System.arraycopy(buffer, howMuch, newBuf, 0, capacity - howMuch);

		buffer = newBuf;
	}

}
