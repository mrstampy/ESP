package ddf.minim.analysis;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.github.mrstampy.esp.dsp.SignalInstance;

public class Tester {

  private static final double FREQUENCY = 7.5;
  private static int arraySize = 256;

  public Tester() {
    calc();
  }

  private void calc() {
    double[] d = new double[arraySize];
    for (int i = 0; i < arraySize; i++) {
      BigDecimal bd = new BigDecimal(i).divide(new BigDecimal(arraySize), 5, RoundingMode.HALF_UP);
      d[i] = nextVal(bd.doubleValue(), 0);
    }
    
    SignalInstance si = new SignalInstance(System.nanoTime(), 50, d);

    for (double freq = 0.125; freq <= 25; freq += 0.125) {
      System.out.println(freq + "Hz: " + si.getPowerAt(freq));
    }
    
    System.out.println(FREQUENCY + "Hz: " + si.getPowerAt(FREQUENCY));
    System.out.println((2 * FREQUENCY) + "Hz: " + si.getPowerAt(2 * FREQUENCY));
  }

  private double nextVal(double interval, double offset) {
    return Math.sin(2 * Math.PI * FREQUENCY * interval + offset) + Math.sin(4 * Math.PI * FREQUENCY * interval + offset);
  }

  /**
   * @param args
   */
  public static void main(String[] args) {
    new Tester();
  }

}
