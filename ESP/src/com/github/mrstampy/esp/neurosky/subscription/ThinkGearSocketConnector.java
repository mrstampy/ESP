package com.github.mrstampy.esp.neurosky.subscription;

import static java.text.MessageFormat.format;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

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

import com.github.mrstampy.esp.neurosky.MultiConnectionThinkGearSocket;
import com.github.mrstampy.esp.neurosky.ThinkGearSocketException;
import com.github.mrstampy.esp.neurosky.event.AbstractThinkGearEvent;
import com.github.mrstampy.esp.neurosky.event.EventType;

/**
 * This class connects to the {@link MultiConnectionThinkGearSocket} and
 * receives {@link AbstractThinkGearEvent} updates. Many instances of this class
 * running in separate processes can connect to the single instance of
 * {@link MultiConnectionThinkGearSocket}, allowing separately running programs
 * to deal with Neurosky output simultaneously. <br>
 * <br>
 * Use the
 * {@link MultiConnectionThinkGearSocket#addListener(ThinkGearEventListener)}
 * method in preference to this class if running in the same JVM as the
 * {@link MultiConnectionThinkGearSocket}.
 * 
 * @author burton
 */
public class ThinkGearSocketConnector {
	private static final Logger log = LoggerFactory.getLogger(ThinkGearSocketConnector.class);

	/**
	 * Set the system property 'socket.broadcaster.port' on startup to change the
	 * broadcaster port from the default '12345' ie.
	 * -Dsocket.broadcaster.port=54321
	 */
	public static final String SOCKET_BROADCASTER_KEY = "socket.broadcaster.port";

	public static int THINK_GEAR_SOCKET_BROADCASTER_PORT;
	static {
		try {
			String val = System.getProperty(SOCKET_BROADCASTER_KEY, Integer.toString(12345));
			THINK_GEAR_SOCKET_BROADCASTER_PORT = Integer.parseInt(val);
		} catch (Throwable e) {
			THINK_GEAR_SOCKET_BROADCASTER_PORT = 12345;
		}
	}

	private NioSocketConnector connector;
	private String socketBroadcasterHost;

	private List<ThinkGearEventListener> listeners = Collections
			.synchronizedList(new ArrayList<ThinkGearEventListener>());

	private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock(true);
	private ReadLock readLock = rwLock.readLock();
	private WriteLock writeLock = rwLock.writeLock();

	/**
	 * Instantiate with the name of the host that the
	 * {@link MultiConnectionThinkGearSocket} is running on.
	 * 
	 * @param socketBroadcasterHost
	 */
	public ThinkGearSocketConnector(String socketBroadcasterHost) {
		this.socketBroadcasterHost = socketBroadcasterHost;
	}

	/**
	 * Adds a listener for {@link AbstractThinkGearEvent}s. Listeners do not
	 * receive events unless {@link #subscribe(EventType...)} or
	 * {@link #subscribeAll()} is also called.
	 * 
	 * @param listener
	 * @see ThinkGearSocketConnector
	 */
	public void addThinkGearEventListener(ThinkGearEventListener listener) {
		writeLock.lock();
		try {
			listeners.add(listener);
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Removes the specified listener.
	 * 
	 * @param listener
	 */
	public void removeThinkGearEventListener(ThinkGearEventListener listener) {
		writeLock.lock();
		try {
			listeners.remove(listener);
		} finally {
			writeLock.unlock();
		}
	}

	public void clearThinkGearEventListeners() {
		writeLock.lock();
		try {
			listeners.clear();
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * Returns true if connected to a {@link MultiConnectionThinkGearSocket}
	 * instance.
	 * 
	 * @return true if connected
	 */
	public boolean isConnected() {
		return connector != null && connector.isActive();
	}

	/**
	 * Connects to the {@link MultiConnectionThinkGearSocket} instance. The
	 * {@link ThinkGearEventListener}s will not be notified of events until
	 * {@link #subscribe(EventType...)} or {@link #subscribeAll()} has been
	 * called.
	 * 
	 * @throws ThinkGearSocketException
	 *           if unable to connect to a {@link MultiConnectionThinkGearSocket}
	 */
	public void connect() throws ThinkGearSocketException {
		if (isConnected()) {
			log.error("Already connected");
			return;
		}
		initConnector();

		ConnectFuture cf = connector.connect(new InetSocketAddress(socketBroadcasterHost,
				THINK_GEAR_SOCKET_BROADCASTER_PORT));
		cf.awaitUninterruptibly(2000);

		if (cf.isConnected()) {
			log.info("Connected to MultiConnectionThinkGearSocket on host {} and port {}", socketBroadcasterHost,
					THINK_GEAR_SOCKET_BROADCASTER_PORT);
		} else {
			connector.dispose(true);

			String msg = format("Could not connect to MultiConnectionThinkGearSocket on host {0} and port {1}",
					socketBroadcasterHost, THINK_GEAR_SOCKET_BROADCASTER_PORT);

			log.error(msg);

			throw new ThinkGearSocketException(msg);
		}
	}

	/**
	 * Disconnects from the {@link MultiConnectionThinkGearSocket} instance.
	 */
	public void disconnect() {
		if (!isConnected()) {
			log.debug("Already disconnected");
			return;
		}
		connector.dispose(true);
		connector = null;

		log.info("Disconnected from MultiConnectionThinkGearSocket on host {} and port {}", socketBroadcasterHost,
				THINK_GEAR_SOCKET_BROADCASTER_PORT);
	}

	/**
	 * Subscribes to the specified event types, all registered
	 * {@link ThinkGearEventListener}s will receive updates for these event types.
	 * Returns true if successful.
	 * 
	 * @param types
	 */
	public boolean subscribe(EventType... types) {
		ThinkGearSubscriptionRequest request = new ThinkGearSubscriptionRequest(types);

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
	public boolean subscribeAll() {
		return subscribe(EventType.values());
	}

	/**
	 * Sends the specified message to the Neurosky socket/headset. Returns true if
	 * successful.
	 * 
	 * @param message
	 */
	public boolean sendMessage(String message) {
		if (!isConnected()) {
			log.error("Cannot send message {}, not connected", message);
			return false;
		}

		NeuroskyMessage nm = new NeuroskyMessage(message);
		connector.broadcast(nm);

		log.info("Sent message {} to Neurosky", message);

		return true;
	}

	private void initConnector() {
		connector = new NioSocketConnector();

		DefaultIoFilterChainBuilder chain = connector.getFilterChain();

		MdcInjectionFilter mdcInjectionFilter = new MdcInjectionFilter();
		chain.addLast("mdc", mdcInjectionFilter);
		chain.addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));

		connector.setHandler(new IoHandlerAdapter() {
			public void messageReceived(IoSession session, Object message) throws Exception {
				if (message instanceof AbstractThinkGearEvent) processEvent((AbstractThinkGearEvent) message);
			}
		});
	}

	private void processEvent(AbstractThinkGearEvent message) {
		readLock.lock();
		try {
			for (ThinkGearEventListener l : listeners) {
				l.thinkGearEventPerformed(message);
			}
		} finally {
			readLock.unlock();
		}
	}

}
