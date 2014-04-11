package com.github.mrstampy.esp.neurosky.event;

/**
 * The current raw EEG output. Must be aggregated and processed by interested
 * subscribers.
 * 
 * @author burton
 */
public class RawEEGThinkGearEvent extends AbstractThinkGearEvent {
	private static final long serialVersionUID = 4004466312317196421L;
	
	private double rawData;

	public RawEEGThinkGearEvent() {
		super(EventType.rawEeg);
	}

	/**
	 * Returns the the current raw signal sample [-32768, 32767].
	 * 
	 * @return the raw data value
	 */
	public double getRawData() {
		return rawData;
	}

	public void setRawData(double rawData) {
		this.rawData = rawData;
	}

}
