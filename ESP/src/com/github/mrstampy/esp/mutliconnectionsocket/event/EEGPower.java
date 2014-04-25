package com.github.mrstampy.esp.mutliconnectionsocket.event;

import java.io.Serializable;

/**
 * Class encapsulating the power of an EEG frequency band.
 * 
 * @author burton
 * 
 */
public class EEGPower implements Serializable {
	private static final long serialVersionUID = 5383056951326171921L;

	private double power;
	private double fromFrequency;
	private double toFrequency;

	public EEGPower() {
		super();
	}

	public EEGPower(double power, double fromFrequency, double toFrequency) {
		setPower(power);
		setFromFrequency(fromFrequency);
		setToFrequency(toFrequency);
	}

	public double getPower() {
		return power;
	}

	public void setPower(double power) {
		this.power = power;
	}

	public double getFromFrequency() {
		return fromFrequency;
	}

	public void setFromFrequency(double fromFrequency) {
		this.fromFrequency = fromFrequency;
	}

	public double getToFrequency() {
		return toFrequency;
	}

	public void setToFrequency(double toFrequency) {
		this.toFrequency = toFrequency;
	}
	
	public boolean isInRange(double targetFrequency) {
	  return getFromFrequency() <= targetFrequency && targetFrequency <= getToFrequency();
	}

}
