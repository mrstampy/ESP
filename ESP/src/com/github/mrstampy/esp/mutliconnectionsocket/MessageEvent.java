package com.github.mrstampy.esp.mutliconnectionsocket;

import com.lmax.disruptor.EventFactory;

/**
 * Disruptor event to pass EEG device messages from the device to the
 * {@link MultiConnectionSocket}'s message processing.
 * 
 * @author burton
 * 
 */
public class MessageEvent {

	private String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static final EventFactory<MessageEvent> EVENT_FACTORY = new EventFactory<MessageEvent>() {

		public MessageEvent newInstance() {
			return new MessageEvent();
		}
	};

}
