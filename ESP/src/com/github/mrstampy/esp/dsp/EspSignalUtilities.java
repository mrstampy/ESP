package com.github.mrstampy.esp.dsp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import javolution.lang.MathLib;
import ddf.minim.analysis.FFT;
import ddf.minim.analysis.WindowFunction;
import de.dfki.lt.signalproc.filter.BandPassFilter;
import de.dfki.lt.signalproc.filter.HighPassFilter;
import de.dfki.lt.signalproc.filter.LowPassFilter;

/**
 * A collection of utility methods which may or may not be useful when analysing
 * a raw signal.
 * 
 * @author burton
 * 
 */
public abstract class EspSignalUtilities {

	private FFT fft = new FFT(getFFTSize(), getSampleRate());

	protected EspSignalUtilities(WindowFunction window) {
		setWindow(window);
	}

	/**
	 * Sets the windowing function within the FFT instance.
	 * 
	 * @param window
	 */
	public void setWindow(WindowFunction window) {
		fft.window(window);
	}

	/**
	 * Returns a map of frequency / log power pairs from the given sample. This
	 * method is not thread safe.
	 * 
	 * @param sample
	 * @param frequencies
	 * @return
	 */
	public Map<Double, Double> getLogPowersFor(double[] sample, double... frequencies) {
		assert sample != null && sample.length > 0;
		assert frequencies != null && frequencies.length > 0;

		Map<Double, Double> map = new HashMap<Double, Double>();

		double[] logFftd = fftLogPowerSpectrum(sample);

		for (double frequency : frequencies) {
			assert frequency >= 1 && frequency < getUpperMeasurableFrequency();

			boolean isInt = ((int) frequency) - frequency == 0;

			double value = isInt ? logFftd[(int) frequency] : getLogPower(logFftd, frequency);

			map.put(frequency, value);
		}

		return map;
	}

	/**
	 * Returns the real half of the fft transform of the given time domain sample.
	 * This method is not thread safe.
	 * 
	 * @param sample
	 * @return
	 */
	public double[] fftRealSpectrum(double[] sample) {
		assert sample != null && sample.length > 0;

		fft.forward(sample);

		return fft.getSpectrumReal();
	}

	/**
	 * Returns the log of the power spectrum of the given time domain sample. This
	 * method is not thread safe.
	 * 
	 * @param sample
	 * @return
	 * @see #getLogPower(double[], double)
	 * @see #getLogPower(double[], int, int)
	 */
	public double[] fftLogPowerSpectrum(double[] sample) {
		assert sample != null && sample.length > 0;

		fft.forward(sample);

		return fft.getLogPowerSpectrum();
	}

	/**
	 * Attempts average the power over the specified range by providing more
	 * weight to values in between lower & upper frequencies ie:<br>
	 * <br
	 * 
	 * For range i to i + 1, weights 1, 1<br>
	 * For range i to i + 2, weights 1, 2, 1<br>
	 * For range i to i + 3, weights 1, 2, 2, 1<br>
	 * For range i to i + 4, weights 1, 2, 3, 2, 1<br>
	 * 
	 * @param logFftd
	 *          the frequency-domain shifted sample
	 * @param lowerFreqHz
	 *          > 0 Hz
	 * @param upperFreqHz
	 *          < {@link #getUpperMeasurableFrequency()} Hz
	 * @see #fftLogPowerSpectrum(double[])
	 * @see #createBandPassFilter(double, double)
	 * @return
	 */
	public double getLogPower(double[] logFftd, int lowerFreqHz, int upperFreqHz) {
		assert logFftd != null && logFftd.length > 0;
		assert lowerFreqHz > 0 && lowerFreqHz <= upperFreqHz && upperFreqHz < getUpperMeasurableFrequency();

		int boundary = upperFreqHz - lowerFreqHz;
		int remainder = boundary % 2;

		int total = 0;
		int cntr = 1;
		int sign = 1;
		double val = 0;
		for (int i = lowerFreqHz; i <= upperFreqHz; i++) {
			total += cntr;
			val += (logFftd[i] * cntr);
			int span = i - lowerFreqHz;
			int hispan = upperFreqHz - span;

			if (hispan - i - remainder == 0) {
				cntr += remainder;
				sign = -1;
			}

			cntr += sign;
		}

		return new BigDecimal(val).divide(new BigDecimal(total), 10, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * Linearly estimates the signal power at the specified frequency, between the
	 * surrounding two fft data points (non-integer value between 1 Hz and (
	 * {@link #getUpperMeasurableFrequency()} - 1) Hz exclusive).
	 * 
	 * @param logFftd
	 *          the frequency-domain shifted sample
	 * @param frequency
	 *          non-integer, > 1 && < ({@link #getUpperMeasurableFrequency()} - 1)
	 * @see #fftLogPowerSpectrum(double[])
	 * @return
	 */
	public double getLogPower(double[] logFftd, double frequency) {
		assert logFftd != null && logFftd.length > 0;
		assert frequency >= 1 && frequency <= getUpperMeasurableFrequency() - 1;
		
		boolean isInt = ((int)frequency) - frequency == 0;
		if(isInt) return logFftd[(int)frequency];

		int lower = (int) frequency;
		int upper = lower + 1;

		double powLower = logFftd[lower];
		double powHigher = logFftd[upper];

		double highFrac = ((double) upper) - frequency;
		double lowFrac = frequency - ((double) lower);

		BigDecimal frac = new BigDecimal(lowFrac).divide(new BigDecimal(highFrac), 10, RoundingMode.HALF_UP);

		BigDecimal low = new BigDecimal(powLower);
		BigDecimal high = new BigDecimal(powHigher).multiply(frac);

		return low.add(high).divide(BigDecimal.ONE.add(frac), 10, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * Returns an array of size upperCutoffHz + 1, containing the normalized
	 * values in the specified array from index lower thru upperCutoffHz
	 * inclusive. Indexes outside of the range will have a zero value.
	 * 
	 * @param fftd
	 *          the specified array of frequency domain values
	 * @param lowerCutoffHz
	 *          the minimum index to use for normalization, >= 1
	 * @param upperCutoffHz
	 *          the maximum index to use for normalization, <
	 *          {@link #getUpperMeasurableFrequency()}
	 * @return
	 */
	public double[] normalize(double[] fftd, int lowerCutoffHz, int upperCutoffHz) {
		assert fftd != null && fftd.length > 0;
		assert lowerCutoffHz >= 1 && lowerCutoffHz < upperCutoffHz && upperCutoffHz < getUpperMeasurableFrequency();
		assert fftd.length > upperCutoffHz;

		double[] normalized = new double[upperCutoffHz + 1];

		double min = getMin(fftd, lowerCutoffHz, upperCutoffHz);
		double max = getMax(fftd, lowerCutoffHz, upperCutoffHz);

		normalize(fftd, min, max, normalized, lowerCutoffHz, upperCutoffHz);

		return normalized;
	}

	/**
	 * Normalizes the raw signal to values between 0 and 1;
	 * 
	 * @param sample
	 * @return
	 */
	public double[] normalize(double[] sample) {
		return normalize(sample, 1);
	}

	/**
	 * Normalizes the raw signal to values between 0 and scale;
	 * 
	 * @param sample
	 * @param scale
	 *          the scale amount
	 * @return
	 */
	public double[] normalize(double[] sample, double scale) {
		assert sample != null && sample.length > 0;
		assert scale != 0;

		BigDecimal sc = new BigDecimal(scale);

		BigDecimal breadth = getRawSignalBreadth();

		double[] normalized = new double[sample.length];

		for (int i = 0; i < sample.length; i++) {
			normalized[i] = new BigDecimal(sample[i]).multiply(sc).divide(breadth, 10, RoundingMode.HALF_UP).doubleValue();
		}

		return normalized;
	}

	/**
	 * Returns the root mean square value of the given inputs over the given
	 * range.
	 * 
	 * @param lowerFreqHz
	 *          >= 1
	 * @param upperFreqHz
	 *          < {@link #getUpperMeasurableFrequency()}
	 * @param fftd
	 *          powers
	 * 
	 * @return the rms value
	 */
	public double rms(int lowerFreqHz, int upperFreqHz, double... fftd) {
		assert fftd != null && fftd.length > 0;
		assert lowerFreqHz >= 1 && lowerFreqHz < upperFreqHz && upperFreqHz < getUpperMeasurableFrequency();

		int divisor = fftd.length;

		double sum = 0;
		for (int i = lowerFreqHz; i <= upperFreqHz; i++) {
			sum += MathLib.pow(fftd[i], 2);
		}

		return new BigDecimal(MathLib.sqrt(sum)).divide(new BigDecimal(divisor), 10, RoundingMode.HALF_UP).doubleValue();
	}

	/**
	 * Returns the weighted moving average of the given inputs. The first element
	 * is considered to be the least weight (or oldest value).
	 * 
	 * @param powers
	 * @return
	 */
	public double wma(double... powers) {
		assert powers != null && powers.length > 0;

		int weight = 1;
		double total = 0;
		int divisor = 0;

		for (double d : powers) {
			divisor += weight;

			total += d * weight;

			weight++;
		}

		return new BigDecimal(total).divide(new BigDecimal(divisor), 10, RoundingMode.HALF_UP).doubleValue();
	}

	public BandPassFilter createBandPassFilter(double lowerCutoffHz, double upperCutoffHz) {
		assert lowerCutoffHz > 0 && lowerCutoffHz < upperCutoffHz && upperCutoffHz < getUpperMeasurableFrequency();

		double lowerFrac = lowerCutoffHz / getSampleRate();
		double upperFrac = upperCutoffHz / getSampleRate();

		BandPassFilter bpf = new BandPassFilter(lowerFrac, upperFrac);

		return bpf;
	}

	public HighPassFilter createHighPassFilter(double cutoffHz) {
		assert cutoffHz > 0 && cutoffHz < getUpperMeasurableFrequency();

		HighPassFilter hpf = new HighPassFilter(cutoffHz / getSampleRate());

		return hpf;
	}

	public LowPassFilter createLowPassFilter(double cutoffHz) {
		assert cutoffHz > 0 && cutoffHz < getUpperMeasurableFrequency();

		LowPassFilter lpf = new LowPassFilter(cutoffHz / getSampleRate());

		return lpf;
	}

	/**
	 * Convenience method to create an esp - specific low pass filter, filtering
	 * out signals above {@link #getUpperMeasurableFrequency()} - 0.1.
	 * 
	 * @return
	 */
	public LowPassFilter getEspLowPassFilter() {
		return createLowPassFilter(getUpperMeasurableFrequency() - 0.1);
	}

	protected double getUpperMeasurableFrequency() {
		return getSampleRate() / 2;
	}

	/**
	 * Return the size of the sample arrays. Must be a power of 2;
	 * 
	 * @return
	 */
	protected abstract int getFFTSize();

	/**
	 * Return the sample rate
	 * 
	 * @return
	 */
	protected abstract double getSampleRate();

	/**
	 * Return the range of the raw signal values (max - min)
	 * 
	 * @return
	 */
	protected abstract BigDecimal getRawSignalBreadth();

	private double getMax(double[] fftd, int lowerCutoffHz, int upperCutoffHz) {
		double max = Double.MIN_VALUE;

		for (int i = lowerCutoffHz; i <= upperCutoffHz; i++) {
			max = MathLib.max(max, fftd[i]);
		}

		return max;
	}

	private double getMin(double[] fftd, int lowerCutoff, int upperCutoffHz) {
		double min = Double.MAX_VALUE;

		for (int i = lowerCutoff; i <= upperCutoffHz; i++) {
			min = MathLib.min(min, fftd[i]);
		}

		return min;
	}

	private void normalize(double[] fftd, double min, double max, double[] normalized, int lowerCutoffHz,
			int upperCutoffHz) {
		BigDecimal divisor = new BigDecimal(max - min);

		for (int i = lowerCutoffHz; i <= upperCutoffHz; i++) {
			normalized[i] = normalize(fftd[i], divisor);
		}
	}

	private double normalize(double d, BigDecimal divisor) {
		return new BigDecimal(d).divide(divisor, 3, RoundingMode.HALF_UP).doubleValue();
	}
}
