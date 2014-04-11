/*
 * 
 * This provides a simple socket connector to the NeuroSky MindWave ThinkGear connector.
 * For more info visit http://crea.tion.to/processing/thinkgear-java-socket
 * 
 * No warranty or any stuffs like that.
 * 
 * Have fun!
 * Andreas Borg
 * borg@elevated.to
 * 
 * 
 * (c) 2010
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 * 
 * @author		Andreas Borg, borg@elevated.to
 * @modified	June, 2011
 * @version		1.0
 * 
 * 
 * This library is following the same design as the one developed by Jorge C. S. Cardoso for the MindSet device.
 * The MindWave device can communicate to a socket over JSON instead of the serial port. That makes it easier and tidier
 * to talk between the device and Java. For instructions on how to use the callback listeners please refer to
 * 
 * http://jorgecardoso.eu/processing/MindSetProcessing/
 * 
 * 
 * Data is passed back to the application via the following callback methods:
 * 
 * 
 * public void attentionEvent(int attentionLevel)
 * Returns the current attention level [0, 100].
 * Values in [1, 20] are considered strongly lowered.
 * Values in [20, 40] are considered reduced levels.
 * Values in [40, 60] are considered neutral.
 * Values in [60, 80] are considered slightly elevated.
 * Values in [80, 100] are considered elevated.
 * 
 * public void meditationEvent(int meditationLevel)
 * Returns the current meditation level [0, 100].
 * The interpretation of the values is the same as for the attentionLevel.
 * 
 * 
 * public void poorSignalEvent(int signalLevel)
 * Returns the signal level [0, 200]. The greater the value, the more noise is detected in the signal.
 * 200 is a special value  that means that the ThinkGear contacts are not touching the skin.
 * 
 * 
 * public void eegEvent(int delta, int theta, int low_alpha, int high_alpha, int low_beta, int high_beta, int low_gamma, int mid_gamma) </code><br>
 * Returns the EEG data. The values have no units.
 * 
 * 
 * 
 * public void rawEvent(int [])
 * Returns the the current 512 raw signal samples [-32768, 32767]. 
 * 
 * 
 */
package com.github.mrstampy.esp.neurosky;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

import javolution.util.FastList;

import org.apache.mina.core.service.IoHandler;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.esp.mutliconnectionsocket.AbstractMultiConnectionSocket;
import com.github.mrstampy.esp.mutliconnectionsocket.event.AbstractMultiConnectionEvent;
import com.github.mrstampy.esp.neurosky.event.AbstractThinkGearEvent;
import com.github.mrstampy.esp.neurosky.event.BlinkStrengthThinkGearEvent;
import com.github.mrstampy.esp.neurosky.event.EEGPowerThinkGearEvent;
import com.github.mrstampy.esp.neurosky.event.ESenseThinkGearEvent;
import com.github.mrstampy.esp.neurosky.event.EventType;
import com.github.mrstampy.esp.neurosky.event.PoorSignalLevelThinkGearEvent;
import com.github.mrstampy.esp.neurosky.event.RawEEGThinkGearEvent;
import com.github.mrstampy.esp.neurosky.simulator.NeuroskySimulator;
import com.github.mrstampy.esp.neurosky.subscription.ThinkGearEventListener;
import com.github.mrstampy.esp.neurosky.subscription.ThinkGearSocketConnector;

/**
 * This class connects to the socket described in the ThinkGear socket protocol
 * documentation. The events received are converted into strongly typed events
 * located in the net.sourceforge.entrainer.neurosky.event package and are
 * queued for transport. An Apache MINA socket acceptor is bound to the
 * broadcaster port defined in {@link ThinkGearSocketConnector} and the events
 * are read from the queue and sent to subscribers connected using the
 * {@link ThinkGearSocketConnector} class. <br>
 * <br>
 * Broadcasting is turned off by default. To enable set the System property
 * 'broadcast.messages' to true ie. java -Dbroadcast.messages=true ...
 * 
 * @author burton
 * @see NeuroskySimulator
 */
public class MultiConnectionThinkGearSocket extends AbstractMultiConnectionSocket {
	private static final Logger log = LoggerFactory.getLogger(MultiConnectionThinkGearSocket.class);

	public static final int NEUROSOCKET_PORT = 13854;
	public final static String VERSION = "2.1";

	/**
	 * System property 'acquire.raw.data' to control whether raw data is obtained
	 * from the device. Defaults to false.
	 */
	public static final String RAW_DATA_KEY = "acquire.raw.data";

	public static final String LOCAL_HOST = "127.0.0.1";

	// ESense
	public static final String MEDITATION = "meditation";
	public static final String ATTENTION = "attention";

	// EEG Power
	public static final String HIGH_GAMMA = "highGamma";
	public static final String LOW_GAMMA = "lowGamma";
	public static final String HIGH_BETA = "highBeta";
	public static final String LOW_BETA = "lowBeta";
	public static final String HIGH_ALPHA = "highAlpha";
	public static final String LOW_ALPHA = "lowAlpha";
	public static final String THETA = "theta";
	public static final String DELTA = "delta";

	private String host = LOCAL_HOST;

	private Socket neuroSocket;
	private PrintWriter out;
	private BufferedReader stdIn;

	private SubscriptionHandlerAdapter subscriptionHandlerAdapter;

	private List<ThinkGearEventListener> listeners = new FastList<ThinkGearEventListener>();
	private ReentrantReadWriteLock listenerLock = new ReentrantReadWriteLock(true);
	private ReadLock readLock = listenerLock.readLock();
	private WriteLock writeLock = listenerLock.writeLock();

	private Thread socketReadThread;

	private boolean rawData;

	private boolean canSendNeuroskyMessages;

	/**
	 * Connects to the ThinkGear socket on the local host. The system property
	 * 'broadcast.messages' is used to enable/disable broadcasting for
	 * {@link ThinkGearSocketConnector}s, and the system property
	 * 'send.neurosky.messages' is used to enable/disable remote
	 * {@link ThinkGearSocketConnector}s sending messages to the Neurosky device.
	 * 
	 * @throws IOException
	 */
	public MultiConnectionThinkGearSocket() throws IOException {
		this(LOCAL_HOST, Boolean.getBoolean(BROADCAST_MESSAGES), Boolean
				.getBoolean(SubscriptionHandlerAdapter.SEND_NEUROSKY_MESSAGES));
	}

	/**
	 * Connects to the ThinkGear socket on the specified host. The system property
	 * 'broadcast.messages' is used to enable/disable broadcasting for
	 * {@link ThinkGearSocketConnector}s, and the system property
	 * 'send.neurosky.messages' is used to enable/disable remote
	 * {@link ThinkGearSocketConnector}s sending messages to the Neurosky device.
	 * 
	 * @param host
	 * @throws IOException
	 */
	public MultiConnectionThinkGearSocket(String host) throws IOException {
		this(host, Boolean.getBoolean(BROADCAST_MESSAGES), Boolean
				.getBoolean(SubscriptionHandlerAdapter.SEND_NEUROSKY_MESSAGES));
	}

	/**
	 * Connects to the ThinkGear socket on the specified host, allowing
	 * programmatic control of broadcasting for {@link ThinkGearSocketConnector}s,
	 * and the system property 'send.neurosky.messages' is used to enable/disable
	 * remote {@link ThinkGearSocketConnector}s sending messages to the Neurosky
	 * device.
	 * 
	 * @param host
	 * @param broadcasting
	 * @throws IOException
	 */
	public MultiConnectionThinkGearSocket(String host, boolean broadcasting) throws IOException {
		this(host, broadcasting, Boolean.getBoolean(SubscriptionHandlerAdapter.SEND_NEUROSKY_MESSAGES));
	}

	/**
	 * Connects to the ThinkGear socket on the specified host, allowing
	 * programmatic control of broadcasting for {@link ThinkGearSocketConnector}s,
	 * and of enabling/disabling remote {@link ThinkGearSocketConnector}s sending
	 * messages to the Neurosky device.
	 * 
	 * @param host
	 * @param broadcasting
	 * @param canSendNeuroskyMessages
	 * @throws IOException
	 */
	public MultiConnectionThinkGearSocket(String host, boolean broadcasting, boolean canSendNeuroskyMessages)
			throws IOException {
		this(host, broadcasting, canSendNeuroskyMessages, Boolean.getBoolean(RAW_DATA_KEY));
	}

	/**
	 * Connects to the ThinkGear socket on the specified host, allowing
	 * programmatic control of broadcasting for {@link ThinkGearSocketConnector}s,
	 * of enabling/disabling remote {@link ThinkGearSocketConnector}s sending
	 * messages to the Neurosky device and of the raw data acquisition from the
	 * Neurosky device.
	 * 
	 * @param host
	 * @param broadcasting
	 * @param canSendNeuroskyMessages
	 * @throws IOException
	 */
	public MultiConnectionThinkGearSocket(String host, boolean broadcasting, boolean canSendNeuroskyMessages,
			boolean rawData) throws IOException {
		super(broadcasting);

		this.host = host;
		this.rawData = rawData;
		this.canSendNeuroskyMessages = canSendNeuroskyMessages;

		log.info("MultiConnectonThinkGearSocket is {} events", broadcasting ? "broadcasting" : "not broadcasting");
	}

	/**
	 * Adds the specified {@link ThinkGearEventListener} to the
	 * {@link MultiConnectionThinkGearSocket} directly. Use this method to receive
	 * events in preference to {@link ThinkGearSocketConnector} if running in the
	 * same JVM as the {@link MultiConnectionThinkGearSocket}.
	 * 
	 * @param listener
	 */
	public void addListener(ThinkGearEventListener listener) {
		writeLock.lock();
		try {
			listeners.add(listener);
		} finally {
			writeLock.unlock();
		}
	}

	public void removeListener(ThinkGearEventListener listener) {
		writeLock.lock();
		try {
			listeners.remove(listener);
		} finally {
			writeLock.unlock();
		}
	}

	public void clearListeners() {
		writeLock.lock();
		try {
			listeners.clear();
		} finally {
			writeLock.unlock();
		}
	}

	/**
	 * return the version of the library.
	 * 
	 * @return String
	 */
	public static String version() {
		return VERSION;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.neurosky.MultiConnectionSocket#start()
	 */
	@Override
	protected void startImpl() throws ThinkGearSocketException {
		closeSocket();

		try {
			createSocketConnection();
		} catch (ThinkGearSocketException e) {
			closeSocket();
			throw e;
		}

		sendFormats();

		startReadThread();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.neurosky.MultiConnectionSocket#stop()
	 */
	@Override
	protected void stopImpl() {
		try {
			closeSocket();
		} catch (Throwable e) {
			log.error("Unexpected exception on stop", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.entrainer.neurosky.MultiConnectionSocket#isConnected()
	 */
	@Override
	public boolean isConnected() {
		return neuroSocket != null && neuroSocket.isConnected();
	}

	/**
	 * Sends the specified message to the ThinkGear socket.
	 * 
	 * @param msg
	 */
	public void sendMessage(String msg) {
		if (!isConnected()) {
			log.error("Cannot send message {} to NeuroSky, not connected", msg);
			return;
		}

		log.info("Sending message to NeuroSky: {}", msg);
		out.println(msg);
	}

	/**
	 * Returns true if {@link ThinkGearSocketConnector}s can send messages to the
	 * Neurosky device.
	 * 
	 * @return
	 * @see SubscriptionHandlerAdapter
	 * @see ThinkGearSocketConnector
	 */
	public boolean canSendNeuroskyMessages() {
		return canBroadcast() && subscriptionHandlerAdapter.canSendNeuroskyMessages();
	}

	/**
	 * Returns true if the device is to acquire raw data.
	 */
	public boolean isRawData() {
		return rawData;
	}

	/**
	 * Enable/disable raw data acquisition after the next connect to the ThinkGear
	 * socket.
	 */
	public void setRawData(boolean rawData) {
		this.rawData = rawData;
	}

	private void createSocketConnection() throws ThinkGearSocketException {
		try {
			neuroSocket = new Socket(host, NEUROSOCKET_PORT);
			out = new PrintWriter(neuroSocket.getOutputStream(), true);
			stdIn = new BufferedReader(new InputStreamReader(neuroSocket.getInputStream()));
		} catch (Throwable e) {
			log.error("Could not connect to ThinkGearSocket", e);
			throw new ThinkGearSocketException(e);
		}
	}

	private void closeSocket() throws ThinkGearSocketException {
		if (neuroSocket == null) return;

		try {
			socketReadThread.interrupt();
			neuroSocket.close();
			out.close();
			stdIn.close();

			socketReadThread = null;
			neuroSocket = null;
			out = null;
			stdIn = null;
		} catch (Throwable e) {
			throw new ThinkGearSocketException(e);
		}
	}

	private void sendFormats() throws ThinkGearSocketException {
		JSONObject format = new JSONObject();
		try {
			format.put("enableRawOutput", rawData);
			format.put("format", "Json");
		} catch (JSONException e) {
			log.error("Could not create formats object", e);
			throw new ThinkGearSocketException(e);
		}

		sendMessage(format.toString());
	}

	private void startReadThread() {
		socketReadThread = new Thread("MultiConnectionThinkGearSocket read thread") {
			public void run() {
				String userInput;
				try {
					while (neuroSocket.isConnected() && (userInput = stdIn.readLine()) != null) {
						String[] packets = userInput.split("/\r/");
						for (int s = 0; s < packets.length; s++) {
							if (((String) packets[s]).indexOf("{") > -1) {
								publishMessage(packets[s]);
							}
						}
					}
				} catch (SocketException e) {
					log.debug("Unexpected socket exception", e);
				} catch (Throwable e) {
					log.error("Unexpected exception", e);
				}
			}
		};

		socketReadThread.start();
	}

	@Override
	protected void parseMessage(String message) {
		try {
			log.trace("Processing message {}", message);

			int first = message.indexOf("{");
			int last = message.lastIndexOf("}");

			if (first == -1 || last == -1) {
				log.info("Cannot parse message {}", message);
				return;
			}

			if (first > 0) message = message.substring(first);
			if (last < message.length()) message = message.substring(0, last + 1);

			parsePacket(new JSONObject(message));
		} catch (Throwable e) {
			log.error("Could not process message {}", e, message);
		}
	}

	@SuppressWarnings("rawtypes")
	private void parsePacket(JSONObject jsonObject) throws JSONException {
		Iterator itr = jsonObject.keys();
		while (itr.hasNext()) {

			Object e = itr.next();
			String key = e.toString();

			EventType type = EventType.valueOf(key);

			AbstractThinkGearEvent event = null;

			switch (type) {
			case blinkStrength:
				event = getBlinkStrengthThinkGearEvent(jsonObject);
				break;
			case eSense:
				JSONObject esense = jsonObject.getJSONObject(EventType.eSense.name());
				event = getESenseThinkGearEvent(esense);
				break;
			case eegPower:
				JSONObject eegPower = jsonObject.getJSONObject(EventType.eegPower.name());
				event = getEegPowerThinkGearEvent(eegPower);
				break;
			case poorSignalLevel:
				event = getPoorSignalLevelThinkGearEvent(jsonObject);
				break;
			case rawEeg:
				event = getRawEEGThinkGearEvent(jsonObject);
				break;
			default:
				break;
			}

			if (event == null) continue;

			notifyListeners(event);
			if (canBroadcast()) subscriptionHandlerAdapter.sendMultiConnectionEvent(event);
		}
	}

	public void notifyListeners(AbstractMultiConnectionEvent<EventType> event) {
		readLock.lock();
		try {
			if (listeners.isEmpty()) return;

			for (ThinkGearEventListener listener : listeners) {
				listener.thinkGearEventPerformed(event);
			}
		} finally {
			readLock.unlock();
		}
	}

	private RawEEGThinkGearEvent getRawEEGThinkGearEvent(JSONObject jsonObject) {
		RawEEGThinkGearEvent event = new RawEEGThinkGearEvent();

		event.setRawData(getDouble(jsonObject, EventType.rawEeg.name()));

		return event;
	}

	private PoorSignalLevelThinkGearEvent getPoorSignalLevelThinkGearEvent(JSONObject jsonObject) {
		PoorSignalLevelThinkGearEvent event = new PoorSignalLevelThinkGearEvent();

		event.setPoorSignalLevel(getInt(jsonObject, EventType.poorSignalLevel.name()));

		return event;
	}

	private EEGPowerThinkGearEvent getEegPowerThinkGearEvent(JSONObject jsonObject) {
		EEGPowerThinkGearEvent event = new EEGPowerThinkGearEvent();

		event.setDelta(getDouble(jsonObject, DELTA));
		event.setHighAlpha(getDouble(jsonObject, HIGH_ALPHA));
		event.setHighBeta(getDouble(jsonObject, HIGH_BETA));
		event.setHighGamma(getDouble(jsonObject, HIGH_GAMMA));
		event.setLowAlpha(getDouble(jsonObject, LOW_ALPHA));
		event.setLowBeta(getDouble(jsonObject, LOW_BETA));
		event.setLowGamma(getDouble(jsonObject, LOW_GAMMA));
		event.setTheta(getDouble(jsonObject, THETA));

		return event;
	}

	private BlinkStrengthThinkGearEvent getBlinkStrengthThinkGearEvent(JSONObject jsonObject) {
		BlinkStrengthThinkGearEvent event = new BlinkStrengthThinkGearEvent();

		event.setBlinkStrength(getInt(jsonObject, EventType.blinkStrength.name()));

		return event;
	}

	private ESenseThinkGearEvent getESenseThinkGearEvent(JSONObject jsonObject) {
		ESenseThinkGearEvent event = new ESenseThinkGearEvent();

		event.setAttention(getInt(jsonObject, ATTENTION));
		event.setMeditation(getInt(jsonObject, MEDITATION));

		return event;
	}

	private int getInt(JSONObject jsonObject, String key) {
		try {
			return jsonObject.getInt(key);
		} catch (JSONException e) {
			log.error("Could not extract {} from JSON object {}", e, key, jsonObject);
		}

		return -1;
	}

	private double getDouble(JSONObject jsonObject, String key) {
		try {
			return jsonObject.getDouble(key);
		} catch (JSONException e) {
			log.error("Could not extract {} from JSON object {}", e, key, jsonObject);
		}

		return -1;
	}

	@Override
	protected IoHandler getHandlerAdapter() {
		subscriptionHandlerAdapter = new SubscriptionHandlerAdapter(this, canSendNeuroskyMessages);
		return subscriptionHandlerAdapter;
	}
}
