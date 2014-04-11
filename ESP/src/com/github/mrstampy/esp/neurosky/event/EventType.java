package com.github.mrstampy.esp.neurosky.event;

import com.github.mrstampy.esp.mutliconnectionsocket.event.EEGPower;
import com.github.mrstampy.esp.neurosky.subscription.ThinkGearSubscriptionRequest;

/**
 * Enum of Neurosky event types.
 * 
 * @author burton
 */
public enum EventType {
	eSense, 
	poorSignalLevel, 
	blinkStrength, 
	eegPower, 
	rawEeg, 
	signalProcessed;
	
	private EEGPower[] signalProcessedPowers;

	/**
	 * Only applicable for {@link SignalProcessedThinkGearEvent} subscriptions.
	 */
	public EEGPower[] getSignalProcessedPowers() {
		return signalProcessedPowers;
	}

	/**
	 * Only applicable for {@link SignalProcessedThinkGearEvent} subscriptions, set
	 * the {@link EEGPower} array to the powers of interest prior creating a {@link ThinkGearSubscriptionRequest}.
	 * 
	 * @param signalProcessedPowers
	 */
	public void setSignalProcessedPowers(EEGPower... signalProcessedPowers) {
		this.signalProcessedPowers = signalProcessedPowers;
	}
}
