/*
 * ESP Copyright (C) 2013 - 2014 Burton Alexander
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 * 
 */
package com.github.mrstampy.esp.multiconnectionsocket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.Executors;

import javolution.util.FastList;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.filter.logging.MdcInjectionFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.esp.multiconnectionsocket.ConnectionEvent.State;
import com.github.mrstampy.esp.multiconnectionsocket.event.AbstractMultiConnectionEvent;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

// TODO: Auto-generated Javadoc
/**
 * Abstract implementation of a {@link MultiConnectionSocket}. This superclass
 * uses Disruptor to pull EEG device messages off a socket and pass them to an
 * Executor for processing.
 * 
 * Subclasses type the format of the raw data messages. Should multiple types
 * ie. double and byte arrays be received from the device then a serializable
 * wrapper can be created to contain the data.
 *
 * @author burton
 * @param <MESSAGE> the generic type
 */
public abstract class AbstractMultiConnectionSocket<MESSAGE> implements MultiConnectionSocket {
	private static final Logger log = LoggerFactory.getLogger(AbstractMultiConnectionSocket.class);

	private NioSocketAcceptor socketBroadcaster;

	private List<ConnectionEventListener> connectionListeners = new FastList<ConnectionEventListener>();

	private final boolean broadcasting;

	private EventHandler<MessageEvent<MESSAGE>> messageEventHandler;

	private Disruptor<MessageEvent<MESSAGE>> disruptor;

	private RingBuffer<MessageEvent<MESSAGE>> rb;
	
	private int numChannels;

	/**
	 * Instantiates a new abstract multi connection socket.
	 *
	 * @param broadcasting the broadcasting
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected AbstractMultiConnectionSocket(boolean broadcasting) throws IOException {
		this.broadcasting = broadcasting;
		initMessageEventHandler();

		if (broadcasting) initBroadCaster();
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocket#start()
	 */
	@Override
	public final void start() throws MultiConnectionSocketException {
		if (isConnected()) {
			log.error("AbstractMultiConnectionSocket already connected");
			return;
		}

		log.info("Starting AbstractMultiConnectionSocket {}", getClass());

		initDisruptor();
		rb = disruptor.start();

		try {
			startImpl();
			notifyConnectionEventListeners(State.STARTED);
		} catch (MultiConnectionSocketException e) {
			disruptor.shutdown();
			throw e;
		}

		log.info("AbstractMultiConnectionSocket started");
	}

	/**
	 * Implement to create a connection to the EEG device. Messages retrieved from
	 * the device should be passed to the
	 * {@link AbstractMultiConnectionSocket#publishMessage(String)} method.
	 *
	 * @throws MultiConnectionSocketException the multi connection socket exception
	 * @see AbstractMultiConnectionSocket#publishMessage(String)
	 */
	protected abstract void startImpl() throws MultiConnectionSocketException;

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocket#stop()
	 */
	public void stop() {
		if (!isConnected()) {
			log.error("AbstractMultiConnectionSocket already disconnected");
			return;
		}

		log.info("Stopping AbstractMultiConnectionSocket {}", getClass());

		disruptor.shutdown();
		stopImpl();

		log.info("AbstractMultiConnectionSocket stopped");

		notifyConnectionEventListeners(State.STOPPED);
	}

	/**
	 * Implement to close the connection to the EEG device.
	 */
	protected abstract void stopImpl();

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocket#addConnectionEventListener(com.github.mrstampy.esp.multiconnectionsocket.ConnectionEventListener)
	 */
	@Override
	public void addConnectionEventListener(ConnectionEventListener listener) {
		connectionListeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocket#removeConnectionEventListener(com.github.mrstampy.esp.multiconnectionsocket.ConnectionEventListener)
	 */
	@Override
	public void removeConnectionEventListener(ConnectionEventListener listener) {
		connectionListeners.remove(listener);
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocket#clearConnectionEventListeners()
	 */
	@Override
	public void clearConnectionEventListeners() {
		connectionListeners.clear();
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocket#bindBroadcaster()
	 */
	@Override
	public void bindBroadcaster() throws IOException {
		if (!canBroadcast()) {
			log.warn("AbstractMultiConnectionSocket is not broadcasting events, cannot bind broadcaster");
			return;
		}

		if (socketBroadcaster.isActive()) {
			log.warn("AbstractMultiConnectionSocket is already bound");
			return;
		}

		socketBroadcaster.bind(new InetSocketAddress(AbstractSocketConnector.BROADCASTER_PORT));

		notifyConnectionEventListeners(State.BOUND);
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocket#unbindBroadcaster()
	 */
	@Override
	public void unbindBroadcaster() {
		if (!canBroadcast()) {
			log.warn("AbstractMultiConnectionSocket is not broadcasting events, cannot unbind broadcaster");
			return;
		}

		if (!socketBroadcaster.isActive()) {
			log.warn("AbstractMultiConnectionSocket is already unbound");
			return;
		}

		socketBroadcaster.unbind();

		notifyConnectionEventListeners(State.UNBOUND);
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocket#canBroadcast()
	 */
	@Override
	public boolean canBroadcast() {
		return broadcasting;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.neurosky.MultiConnectionSocket#isBound()
	 */
	@Override
	public boolean isBound() {
		return canBroadcast() && socketBroadcaster.isActive();
	}

	/**
	 * Notify connection event listeners.
	 *
	 * @param state the state
	 */
	protected void notifyConnectionEventListeners(State state) {
		if (connectionListeners.isEmpty()) return;

		ConnectionEvent e = new ConnectionEvent(this, state);

		for (ConnectionEventListener listener : connectionListeners) {
			listener.connectionEventPerformed(e);
		}
	}

	/**
	 * Initialization of the broadcaster, to send
	 * {@link AbstractMultiConnectionEvent}s to remote processes which have
	 * registered for subscriptions. Invoke during object creation.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void initBroadCaster() throws IOException {
		socketBroadcaster = new NioSocketAcceptor();

		DefaultIoFilterChainBuilder chain = socketBroadcaster.getFilterChain();
		MdcInjectionFilter mdcInjectionFilter = new MdcInjectionFilter();
		chain.addLast("mdc", mdcInjectionFilter);
		if (log.isDebugEnabled()) chain.addLast("logger", new LoggingFilter());
		chain.addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));

		socketBroadcaster.setHandler(getHandlerAdapter());
		socketBroadcaster.setReuseAddress(true);

		bindBroadcaster();
	}

	/**
	 * Gets the handler adapter.
	 *
	 * @return the handler adapter
	 */
	protected abstract IoHandler getHandlerAdapter();

	/**
	 * EEG device messages are passed to this method which uses Disruptor to pass
	 * the message asynchronously for processing by subclasses.
	 *
	 * @param message the message
	 * @see AbstractMultiConnectionSocket#parseMessage(String)
	 */
	protected void publishMessage(MESSAGE message) {
		long seq = rb.next();
		log.trace("Publishing message {} for sequence {}", message, seq);
		MessageEvent<MESSAGE> be = rb.get(seq);
		be.setMessage(message);
		rb.publish(seq);
	}

	@SuppressWarnings("unchecked")
	private void initDisruptor() {
		disruptor = new Disruptor<MessageEvent<MESSAGE>>(new MessageEventFactory<MESSAGE>(), 16,
				Executors.newCachedThreadPool());

		disruptor.handleEventsWith(messageEventHandler);
	}

	private void initMessageEventHandler() {
		messageEventHandler = new EventHandler<MessageEvent<MESSAGE>>() {

			@Override
			public void onEvent(final MessageEvent<MESSAGE> event, long sequence, boolean endOfBatch) throws Exception {
				parseMessage(event.getMessage());
			}
		};
	}

	/**
	 * Invoked via Disruptor's onEvent processing, implement to notify event
	 * listeners and any multi connection subscribers.
	 *
	 * @param message the message
	 */
	protected abstract void parseMessage(MESSAGE message);

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocket#getNumChannels()
	 */
	public int getNumChannels() {
		return numChannels;
	}

	/**
	 * Sets the num channels.
	 *
	 * @param numChannels the new num channels
	 */
	public void setNumChannels(int numChannels) {
		this.numChannels = numChannels;
	}

}
