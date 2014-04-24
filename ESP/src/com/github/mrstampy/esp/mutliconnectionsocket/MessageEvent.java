package com.github.mrstampy.esp.mutliconnectionsocket;

/**
 * Disruptor event to pass EEG device messages from the device to the
 * {@link MultiConnectionSocket}'s message processing.
 * 
 * @author burton
 * 
 */
public class MessageEvent<MESSAGE> {

	private MESSAGE message;

	public MESSAGE getMessage() {
		return message;
	}

	public void setMessage(MESSAGE message) {
		this.message = message;
	}

}
