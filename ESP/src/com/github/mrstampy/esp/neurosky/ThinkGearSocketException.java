package com.github.mrstampy.esp.neurosky;

import com.github.mrstampy.esp.mutliconnectionsocket.MultiConnectionSocketException;

/**
 * Simple extension of exception to type exceptions from the
 * {@link MultiConnectionThinkGearSocket}.
 * 
 * @author burton
 */
public class ThinkGearSocketException extends MultiConnectionSocketException {

	private static final long serialVersionUID = 2810694669291339544L;

	public ThinkGearSocketException() {
		super();
	}

	public ThinkGearSocketException(String arg0) {
		super(arg0);
	}

	public ThinkGearSocketException(Throwable arg0) {
		super(arg0);
	}

	public ThinkGearSocketException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
