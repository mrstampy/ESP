package com.github.mrstampy.esp.dsp;

import java.util.EventListener;

/**
 * Processed listeners are notified when the {@link EspDSP} has finished processing the 
 * current cycle.
 * 
 * @author burton
 * @see EspDSP#getSnapshot()
 *
 */
public interface RawProcessedListener extends EventListener {

	void signalProcessed();
}
