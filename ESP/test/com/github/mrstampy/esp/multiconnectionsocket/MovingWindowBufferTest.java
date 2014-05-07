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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.github.mrstampy.esp.multiconnectionsocket.MovingWindowBuffer;

public class MovingWindowBufferTest {

	private MovingWindowBuffer buf = new MovingWindowBuffer(5);
	
	@Before
	public void before() throws Exception {
		buf = new MovingWindowBuffer(5);
		
		double[] first = {1,2,3,4};
		buf.addAll(first);
		double[] snap = buf.snapshot();

		confirm(first, snap);
	}
	
	@Test
	public void testBuffer() {
		double[] second = {5, 6, 7};
		double[] expected = {3, 4, 5, 6, 7};
		buf.addAll(second);
		double[] snap = buf.snapshot();
		
		confirm(expected, snap);
		
		double[] third = {8};
		double[] expected2 = {4, 5, 6, 7, 8};
		buf.addAll(third);
		snap = buf.snapshot();
		
		confirm(expected2, snap);
	}
	
	@Test
	public void testResizeSmaller() {
		buf.resize(3);
		
		double[] second = {5};
		double[] expected = {3, 4, 5};
		buf.addAll(second);
		double[] snap = buf.snapshot();
		
		confirm(expected, snap);
	}
	
	@Test
	public void testResizeLarger() {
		buf.resize(8);
		double[] expected = {2, 3, 4, 5, 6, 7, 8, 9};
		buf.addAll(5, 6, 7, 8, 9);
		double[] snap = buf.snapshot();
		
		confirm(expected, snap);
	}
	
	private void confirm(double[] expected, double[] snap) {
		for(int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], snap[i], 0);
		}
	}
}
