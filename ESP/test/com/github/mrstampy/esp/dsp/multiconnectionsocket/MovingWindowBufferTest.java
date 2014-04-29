package com.github.mrstampy.esp.dsp.multiconnectionsocket;

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
