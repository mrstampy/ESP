package com.github.mrstampy.esp.mutliconnectionsocket;

import com.lmax.disruptor.EventFactory;

public class MessageEventFactory<MESSAGE> implements EventFactory<MessageEvent<MESSAGE>> {

	@Override
	public MessageEvent<MESSAGE> newInstance() {
		return new MessageEvent<MESSAGE>();
	}

}
