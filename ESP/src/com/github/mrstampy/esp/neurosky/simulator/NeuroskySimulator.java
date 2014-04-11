package com.github.mrstampy.esp.neurosky.simulator;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.InetSocketAddress;
import java.util.Random;

import javolution.text.TextBuilder;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.MdcInjectionFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mrstampy.esp.mutliconnectionsocket.MultiConnectionSocket;
import com.github.mrstampy.esp.mutliconnectionsocket.MultiConnectionSocketException;
import com.github.mrstampy.esp.neurosky.MultiConnectionThinkGearSocket;
import com.github.mrstampy.esp.neurosky.ThinkGearSocketException;
import com.github.mrstampy.esp.neurosky.event.BlinkStrengthThinkGearEvent;
import com.github.mrstampy.esp.neurosky.event.EventType;
import com.github.mrstampy.esp.neurosky.event.PoorSignalLevelThinkGearEvent;
import com.github.mrstampy.esp.neurosky.event.RawEEGThinkGearEvent;
import com.github.mrstampy.esp.neurosky.subscription.ThinkGearEventListenerAdapter;
import com.github.mrstampy.esp.neurosky.subscription.ThinkGearSocketConnector;

/**
 * Class which tests the {@link MultiConnectionThinkGearSocket}, the
 * {@link ThinkGearSocketConnector} and demonstrates the use of these classes.
 * 
 * @author burton
 */
public class NeuroskySimulator {
	private static final Logger log = LoggerFactory.getLogger(NeuroskySimulator.class);

	public static final String CYCLES = "cycles";

	private final boolean broadcasting;

	private MultiConnectionThinkGearSocket thinkGearSocket;

	// Fakes out a ThinkGear socket.
	private NioSocketAcceptor fake;

	private Random rand = new Random(System.currentTimeMillis());

	private ThinkGearSocketConnector connector1;
	private ThinkGearSocketConnector connector2;
	private ThinkGearSocketConnector connector3;

	private TextBuilder builder = new TextBuilder();

	private volatile long messageCount = 0;
	private volatile long tooLong = 0;
	private volatile long ltHundThou = 0;
	private volatile long ltQuarterMil = 0;
	private volatile long ltHalfMil = 0;
	private volatile long ltOneMill = 0;
	private volatile long ltOneFiveMil = 0;
	private volatile long ltTwoMil = 0;
	private volatile long ltTwoFiveMil = 0;
	private volatile long ltThreeMil = 0;
	private volatile long ltThreeFiveMil = 0;

	public NeuroskySimulator(boolean broadcasting) throws IOException, MultiConnectionSocketException,
			InterruptedException {
		this.broadcasting = broadcasting;
		// sets up a fake Netgear socket for the
		// multi connection socket to attach to,
		// and binds to the port.
		initFakeThinkGearSocket();

		// instantiates and starts the multi connection socket
		initMultiConnectionThinkGearSocket();

		// instantiates connectors and adds some event listeners
		if (broadcasting) {
			initConnector1();
			initConnector2();
			initConnector3();
		}

		// connects two of the connectors
		if (broadcasting) connectConnectors();

		// subscribes two of the connectors for Netgear events
		if (broadcasting) subscribeConnectors();
	}

	private void initConnector1() {
		connector1 = new ThinkGearSocketConnector(MultiConnectionThinkGearSocket.LOCAL_HOST);

		connector1.addThinkGearEventListener(new ThinkGearEventListenerAdapter() {

			protected void blinkStrengthEventPerformed(BlinkStrengthThinkGearEvent e) {
				messageCount++;
				print(EventType.blinkStrength.name(), e.getBlinkStrength(), 1, e.getNanoTime());
			}

		});
	}

	private void initConnector2() {
		connector2 = new ThinkGearSocketConnector(MultiConnectionThinkGearSocket.LOCAL_HOST);

		connector2.addThinkGearEventListener(new ThinkGearEventListenerAdapter() {

			protected void blinkStrengthEventPerformed(BlinkStrengthThinkGearEvent e) {
				print(EventType.blinkStrength.name(), e.getBlinkStrength(), 2, e.getNanoTime());
			}

			protected void rawEEGEventPerformed(RawEEGThinkGearEvent e) {
				messageCount++;
				print(EventType.rawEeg.name(), (int) e.getRawData(), 2, e.getNanoTime());
			}

		});
	}

	private void initConnector3() {
		connector3 = new ThinkGearSocketConnector(MultiConnectionThinkGearSocket.LOCAL_HOST);

		connector3.addThinkGearEventListener(new ThinkGearEventListenerAdapter() {

			protected void blinkStrengthEventPerformed(BlinkStrengthThinkGearEvent e) {
				print(EventType.blinkStrength.name(), e.getBlinkStrength(), 3, e.getNanoTime());
			}

			protected void poorSignalEventPerformed(PoorSignalLevelThinkGearEvent e) {
				messageCount++;
				print(EventType.poorSignalLevel.name(), e.getPoorSignalLevel(), 3, e.getNanoTime());
			}

		});
	}

	private synchronized void print(String key, int value, int connectorNum, long created) {
		long diff = System.nanoTime() - created;

		builder.clear();

		builder.append(connectorNum < 4 ? "Connector " : "Local listener ");
		builder.append(connectorNum);
		builder.append(" message ");
		builder.append(key);
		builder.append(" value ");
		builder.append(value);
		builder.append(" received in ");
		builder.append(diff);
		builder.append("ns");

		if (diff < 4000000) { // 4 million ns = 4 ms
			log.trace(builder.toString());
			incrementCount(diff);
		} else {
			tooLong++;

			BigDecimal percent = getPercentSlow();

			builder.append(", percent too slow: ");
			builder.append(percent);
			builder.append("%");

			log.error(builder.toString());
		}
	}

	private void incrementCount(long diff) {
		if (diff < 100000) {
			ltHundThou++;
		} else if (diff < 250000) {
			ltQuarterMil++;
		} else if (diff < 500000) {
			ltHalfMil++;
		} else if (diff < 1000000) {
			ltOneMill++;
		} else if (diff < 1500000) {
			ltOneFiveMil++;
		} else if (diff < 2000000) {
			ltTwoMil++;
		} else if (diff < 2500000) {
			ltTwoFiveMil++;
		} else if (diff < 3000000) {
			ltThreeMil++;
		} else if (diff < 3500000) {
			ltThreeFiveMil++;
		}
	}

	private BigDecimal getPercentSlow() {
		if (tooLong == 0 || messageCount == 0) return BigDecimal.ZERO;
		BigDecimal dec = new BigDecimal(tooLong).divide(new BigDecimal(messageCount), 10, RoundingMode.HALF_UP);
		BigDecimal percent = dec.multiply(new BigDecimal(100), getMathContext(dec.doubleValue()));

		return percent;
	}

	private MathContext getMathContext(double val) {
		if (val > 0.1) return new MathContext(4);

		if (val < 0.1 && val > 0.01) return new MathContext(3);

		return new MathContext(4);
	}

	public void printFinal() {
		builder.clear();

		BigDecimal percent = getPercentSlow();

		builder.append("Total messages: ");
		builder.append(messageCount);
		builder.append(", slow messages: ");
		builder.append(tooLong);
		builder.append(", percent too slow: ");
		builder.append(percent);
		builder.append("%");

		log.info(builder.toString());

		if (broadcasting) {
			connector1.disconnect();
			connector2.disconnect();
			connector3.disconnect();
		}

		thinkGearSocket.stop();
		if (broadcasting) fake.unbind();

		System.out.println();
		System.out.println(builder.toString());
		System.out.println();
		System.out.println("Nanosecond message receipt time");
		System.out.println();
		System.out.println("lt       10e5 ns : " + ltHundThou + " (local listener times)");
		System.out.println("lt 2.5 * 10e5 ns : " + ltQuarterMil);
		System.out.println("lt   5 * 10e5 ns : " + ltHalfMil);
		System.out.println("lt       10e6 ns : " + ltOneMill);
		System.out.println("lt 1.5 * 10e6 ns : " + ltOneFiveMil);
		System.out.println("lt   2 * 10e6 ns : " + ltTwoMil);
		System.out.println("lt 2.5 * 10e6 ns : " + ltTwoFiveMil);
		System.out.println("lt   3 * 10e6 ns : " + ltThreeMil);
		System.out.println("lt 3.5 * 10e6 ns : " + ltThreeFiveMil);

		System.exit(0);
	}

	public void connectConnectors() throws InterruptedException, ThinkGearSocketException {
		connector1.connect();
		connector2.connect();
		Thread.sleep(200);
	}

	public void subscribeConnectors() {
		connector1.subscribe(EventType.blinkStrength);
		connector2.subscribe(EventType.blinkStrength, EventType.rawEeg);
	}

	/**
	 * Called during event dispatching to test the connecting and subscribing of
	 * the third connector to a live system.
	 */
	public void connectConnector3() {
		Thread thread = new Thread() {
			public void run() {
				try {
					connector3.connect();
					Thread.sleep(200);
					connector3.subscribe(EventType.blinkStrength, EventType.poorSignalLevel);
				} catch (Throwable e) {
					e.printStackTrace();
					System.exit(-1);
				}
			}
		};

		thread.start();
	}

	public void sendBlinkStrength() throws JSONException {
		send(EventType.blinkStrength.name());
	}

	public void sendPoorSignal() throws JSONException {
		send(EventType.poorSignalLevel.name());
	}

	public void sendRawEEG() throws JSONException {
		send(EventType.rawEeg.name());
	}

	/**
	 * Generates a random integer 0 -> 100 and sends it as the value of the
	 * specified key thru the fake Netgear socket.
	 * 
	 * @param key
	 * @throws JSONException
	 */
	public void send(String key) throws JSONException {
		int strength = rand.nextInt(101);

		JSONObject json = new JSONObject();
		json.put(key, strength);

		fake.broadcast(json.toString());
	}

	private void initMultiConnectionThinkGearSocket() throws IOException, MultiConnectionSocketException {
		thinkGearSocket = new MultiConnectionThinkGearSocket();
		thinkGearSocket.addListener(new ThinkGearEventListenerAdapter() {

			protected void blinkStrengthEventPerformed(BlinkStrengthThinkGearEvent e) {
				if (!broadcasting) messageCount++;
				print(EventType.blinkStrength.name(), e.getBlinkStrength(), 4, e.getNanoTime());
			}

			protected void rawEEGEventPerformed(RawEEGThinkGearEvent e) {
				if (!broadcasting) messageCount++;
				print(EventType.rawEeg.name(), (int) e.getRawData(), 4, e.getNanoTime());
			}

		});
		thinkGearSocket.start();
	}

	private void initFakeThinkGearSocket() throws IOException, InterruptedException {
		fake = new NioSocketAcceptor();

		DefaultIoFilterChainBuilder chain = fake.getFilterChain();
		MdcInjectionFilter mdcInjectionFilter = new MdcInjectionFilter();
		chain.addLast("mdc", mdcInjectionFilter);
		chain.addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));

		fake.setHandler(new IoHandlerAdapter());

		fake.bind(new InetSocketAddress(MultiConnectionThinkGearSocket.NEUROSOCKET_PORT));
		Thread.sleep(200);
	}

	/**
	 * Defaults to 50000 messages (100 seconds). Set the system property 'cycles'
	 * to an integer to override ie: -Dcycles=20000
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int cycles = getCycles();
		boolean broadcasting = getBroadcastingFlag();

		System.out.println("*****************************************************");
		System.out.println();
		System.out.println("Starting NeuroskySimulator, running " + cycles + " cycles and" + (broadcasting ? "" : " not")
				+ " broadcasting events");
		System.out.println();
		System.out.println("To change the number of cycles start with property parameter '-Dcycles=###'");
		System.out.println("ie. java -Dcycles=20000 -jar EntrainerESP.jar");
		System.out.println();
		System.out.println("To turn off broadcasting start with property parameter '-Dbroadcast.messages=false'");
		System.out.println("ie. java -Dbroadcast.messages=false -jar EntrainerESP.jar");
		System.out.println();
		System.out.println("*****************************************************");
		System.out.println();
		System.out.println();

		startSimulatorThread(cycles, broadcasting);
	}

	private static boolean getBroadcastingFlag() {
		Boolean b = new Boolean(System.getProperty(MultiConnectionSocket.BROADCAST_MESSAGES, "true"));
		System.setProperty(MultiConnectionSocket.BROADCAST_MESSAGES, b.toString());

		return b;
	}

	private static void startSimulatorThread(final int cycles, final boolean broadcasting) {
		Thread thread = new Thread() {
			int count = 0;

			public void run() {
				int sec = 0;
				try {
					NeuroskySimulator sim = new NeuroskySimulator(broadcasting);
					Thread.sleep(100);
					while (sim.messageCount < cycles) {
						sim.sendRawEEG();
						Thread.sleep(2);
						count++;
						sec++;
						if (sec >= 500) {
							sec = 0;
							sim.sendBlinkStrength();
							sim.sendPoorSignal();
						}
						if (broadcasting && count == 50) sim.connectConnector3();
					}
					sim.printFinal();
				} catch (Throwable e) {
					log.error("Could not instantiate NeuroskySimulator", e);
					e.printStackTrace();
					System.exit(-1);
				}
			}
		};

		thread.setPriority(Thread.MAX_PRIORITY);

		thread.start();
	}

	private static int getCycles() {
		int cycles = 0;
		String val = System.getProperty(CYCLES, Integer.toString(50000));
		try {
			cycles = Integer.parseInt(val);
		} catch (NumberFormatException e) {
			log.error("{} is not an integer", val);
			System.out.println(val + " is not an integer");
			System.exit(-1);
		}
		return cycles;
	}

}
