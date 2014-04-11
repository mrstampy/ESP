package net.sourceforge.entrainer.dsp;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.github.mrstampy.esp.dsp.SignalInstance;
import com.github.mrstampy.esp.dsp.SignalProcessor;
import com.github.mrstampy.esp.dsp.SignalProcessorListener;

@SuppressWarnings("serial")
public class SignalProcessorTest extends SignalProcessor implements SignalProcessorListener {
  private Random rand = new Random(System.currentTimeMillis());

  private CountDownLatch latch = new CountDownLatch(1);

  private SignalInstance si;
  
  private SignalCaptureThread sct = new SignalCaptureThread();
  private boolean unitTest = false;

  public SignalProcessorTest() {
    super();
    addListener(this);
  }

  @Test
  public void testSignal() throws Exception {
    started = true;
    unitTest = true;
    long nanos = System.nanoTime();
    createSignal(nanos);
    processSignal(nanos);
    latch.await(100, TimeUnit.MILLISECONDS);
    validate(si);
  }
  
  public void startDemo() {
    sct.start();
    start();
  }

  private void createSignal(long nanos) {
    long interval = NANOS_PER_SECOND.divide(new BigDecimal(500)).longValue();
    long start = nanos - ONE_SECOND_NANOS;
    for (int i = 0; i < 500; i++) {
      addSignal(start, rand.nextDouble());
      start += interval;
    }
  }

  @Override
  public void signalProcessed(SignalInstance signalInstance) {
    if(unitTest) {
      this.si = signalInstance;
      latch.countDown();
    } else {
      validate(signalInstance);
    }
  }

  private void validate(SignalInstance si) {
    assertNotNull(si);
    assertEquals(si.getSignal().length, getSampleSize());
    
    long lastKey = -1;
    for (double d : si.getSignal()) {
      long key = getKeyFor(d);
      assertTrue(key > 0);
      assertTrue(lastKey < key);
      lastKey = key;
    }
    
    int size = rawSignal.size();
    assertTrue(size > getSampleSize());
    assertTrue(size < 520);
  }

  private long getKeyFor(double d) {
    for (Entry<Long, Double> entry : rawSignal.entrySet()) {
      if (entry.getValue() == d) return entry.getKey();
    }

    return -1;
  }

  public class SignalCaptureThread extends Thread {
    private volatile boolean run = true;

    public SignalCaptureThread() {
      super("Signal Capture Thread");
      setPriority(MAX_PRIORITY);
    }

    public void run() {
      while (run) {
        try {
          sleep(2); // ~500 Hz
        } catch (InterruptedException e) {

        }

        addSignal(rand.nextDouble());
      }
    }

    public void setRun(boolean run) {
      this.run = run;
    }
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    new SignalProcessorTest().startDemo();
  }

}
