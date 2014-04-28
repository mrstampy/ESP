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

import static java.text.MessageFormat.format;

import java.net.InetSocketAddress;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.MdcInjectionFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.functions.Action1;

import com.github.mrstampy.esp.multiconnectionsocket.event.AbstractMultiConnectionEvent;
import com.github.mrstampy.esp.multiconnectionsocket.subscription.MultiConnectionSubscriptionRequest;

/**
 * This class connects to the {@link AbstractMultiConnectionSocket} and receives
 * {@link AbstractMultiConnectionEvent} updates. Many instances of this class
 * running in separate processes can connect to the single instance of
 * {@link AbstractMultiConnectionSocket}, allowing separately running programs
 * to deal with Neurosky output simultaneously. <br>
 * <br>
 * 
 * @author burton
 */
public abstract class AbstractSocketConnector<E extends Enum<E>> {
	private static final Logger log = LoggerFactory.getLogger(AbstractSocketConnector.class);

	/**
	 * Set the system property 'socket.broadcaster.port' on startup to change the
	 * broadcaster port from the default '12345' ie.
	 * -Dsocket.broadcaster.port=54321
	 */
	public static final String SOCKET_BROADCASTER_KEY = "socket.broadcaster.port";

	public static int BROADCASTER_PORT;
	static {
		try {
			String val = System.getProperty(SOCKET_BROADCASTER_KEY, Integer.toString(12345));
			BROADCASTER_PORT = Integer.parseInt(val);
		} catch (Throwable e) {
			BROADCASTER_PORT = 12345;
		}
	}

	protected NioSocketConnector connector;
	private String socketBroadcasterHost;

	/**
	 * Instantiate with the name of the host that the
	 * {@link AbstractMultiConnectionSocket} is running on.
	 * 
	 * @param socketBroadcasterHost
	 */
	public AbstractSocketConnector(String socketBroadcasterHost) {
		this.socketBroadcasterHost = socketBroadcasterHost;
	}

	/**
	 * Returns true if connected to a {@link AbstractMultiConnectionSocket}
	 * instance.
	 * 
	 * @return true if connected
	 */
	public boolean isConnected() {
		return connector != null && connector.isActive();
	}

	/**
	 * Connects to the {@link AbstractMultiConnectionSocket} instance. The
	 * listeners will not be notified of events until
	 * {@link #subscribe(EventType...)} or {@link #subscribeAll()} has been
	 * called.
	 * 
	 * @throws MultiConnectionSocketException
	 *           if unable to connect to a {@link AbstractMultiConnectionSocket}
	 */
	public void connect() throws MultiConnectionSocketException {
		if (isConnected()) {
			log.error("Already connected");
			return;
		}
		initConnector();

		ConnectFuture cf = connector.connect(new InetSocketAddress(socketBroadcasterHost, BROADCASTER_PORT));
		cf.awaitUninterruptibly(2000);

		if (cf.isConnected()) {
			log.info("Connected to AbstractMultiConnectionSocket on host {} and port {}", socketBroadcasterHost,
					BROADCASTER_PORT);
		} else {
			connector.dispose(true);

			String msg = format("Could not connect to AbstractMultiConnectionSocket on host {0} and port {1}",
					socketBroadcasterHost, BROADCASTER_PORT);

			log.error(msg);

			throw new MultiConnectionSocketException(msg);
		}
	}

	/**
	 * Disconnects from the {@link AbstractMultiConnectionSocket} instance.
	 */
	public void disconnect() {
		if (!isConnected()) {
			log.debug("Already disconnected");
			return;
		}
		connector.dispose(true);
		connector = null;

		log.info("Disconnected from AbstractMultiConnectionSocket on host {} and port {}", socketBroadcasterHost,
				BROADCASTER_PORT);
	}

	/**
	 * Subscribes to the specified event types, all registered listeners will
	 * receive updates for these event types. Returns true if successful.
	 * 
	 * @param types
	 */
	protected boolean subscribe(MultiConnectionSubscriptionRequest<?> request) {
		if (!isConnected()) {
			log.error("Cannot subscribe to {}, not connected", request);
			return false;
		}

		connector.broadcast(request);

		log.info("Subscribed to {}", request);

		return true;
	}

	/**
	 * Subscribes to all {@link EventType}s, barring
	 * {@link EventType#signalProcessed}.
	 */
	public abstract boolean subscribeAll();

	private void initConnector() {
		connector = new NioSocketConnector();

		DefaultIoFilterChainBuilder chain = connector.getFilterChain();

		MdcInjectionFilter mdcInjectionFilter = new MdcInjectionFilter();
		chain.addLast("mdc", mdcInjectionFilter);
		chain.addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));

		connector.setHandler(new IoHandlerAdapter() {
			@SuppressWarnings("unchecked")
			public void messageReceived(IoSession session, Object message) throws Exception {
				if (message instanceof AbstractMultiConnectionEvent<?>) {
					AbstractMultiConnectionEvent<E> event = (AbstractMultiConnectionEvent<E>) message;
					Observable.from(event).subscribe(new Action1<AbstractMultiConnectionEvent<E>>() {

						@Override
						public void call(AbstractMultiConnectionEvent<E> t1) {
							processEvent(t1);
						}
					});
				}
			}
		});
	}

	protected abstract void processEvent(AbstractMultiConnectionEvent<E> message);

}
