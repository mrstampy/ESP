package com.github.mrstampy.esp.dsp;

import java.math.BigDecimal;

public class SignalUtilitiesTest extends EspSignalUtilities {

	public SignalUtilitiesTest() {
		super(null);
	}

	@Override
	protected int getFFTSize() {
		return 64;
	}

	@Override
	protected double getSampleRate() {
		return 100;
	}

	@Override
	protected BigDecimal getRawSignalBreadth() {
		return BigDecimal.TEN;
	}

}
