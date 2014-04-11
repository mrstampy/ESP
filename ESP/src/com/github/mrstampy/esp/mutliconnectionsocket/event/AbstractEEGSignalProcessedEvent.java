package com.github.mrstampy.esp.mutliconnectionsocket.event;

public abstract class AbstractEEGSignalProcessedEvent<E extends Enum<E>> extends AbstractMultiConnectionEvent<E> {
	private static final long serialVersionUID = -3422417020699719352L;
	
	private final EEGPower[] eegPowers;
	
	public AbstractEEGSignalProcessedEvent(E type, EEGPower...eegPowers) {
		super(type);
		this.eegPowers = eegPowers;
	}
	
	public EEGPower[] getEEGPowers() {
		return eegPowers;
	}

}
