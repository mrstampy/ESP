package com.github.mrstampy.esp.dsp.lab;

import java.io.Serializable;

public interface LabValues extends Serializable {

	/**
	 * Gets the fft type.
	 *
	 * @return the fft type
	 */
	FFTType getFftType();

	/**
	 * Sets the fft type.
	 *
	 * @param fftType the new fft type
	 */
	void setFftType(FFTType fftType);

	/**
	 * Gets the num bands.
	 *
	 * @return the num bands
	 */
	int getNumBands();

	/**
	 * Sets the num bands.
	 *
	 * @param numBands the new num bands
	 */
	void setNumBands(int numBands);

	/**
	 * Sets the high pass frequency.
	 *
	 * @param freq the new high pass frequency
	 */
	void setHighPassFrequency(double freq);

	/**
	 * Gets the high pass frequency.
	 *
	 * @return the high pass frequency
	 */
	double getHighPassFrequency();

	/**
	 * Sets the low pass frequency.
	 *
	 * @param freq the new low pass frequency
	 */
	void setLowPassFrequency(double freq);

	/**
	 * Gets the low pass frequency.
	 *
	 * @return the low pass frequency
	 */
	double getLowPassFrequency();

	/**
	 * Gets the pass filter.
	 *
	 * @return the pass filter
	 */
	PassFilter getPassFilter();

	/**
	 * Sets the pass filter.
	 *
	 * @param filter the new pass filter
	 */
	void setPassFilter(PassFilter filter);

	/**
	 * Checks if is absolute values.
	 *
	 * @return true, if is absolute values
	 */
	boolean isAbsoluteValues();

	/**
	 * Sets the absolute values.
	 *
	 * @param absolute the new absolute values
	 */
	void setAbsoluteValues(boolean absolute);

	/**
	 * Checks if is normalize signal.
	 *
	 * @return true, if is normalize signal
	 */
	boolean isNormalizeSignal();

	/**
	 * Sets the normalize signal.
	 *
	 * @param normalize the new normalize signal
	 */
	void setNormalizeSignal(boolean normalize);

	/**
	 * Checks if is normalize fft.
	 *
	 * @return true, if is normalize fft
	 */
	boolean isNormalizeFft();

	/**
	 * Sets the normalize fft.
	 *
	 * @param normalize the new normalize fft
	 */
	void setNormalizeFft(boolean normalize);

	/**
	 * Sets the high normalize fft frequency.
	 *
	 * @param freq the new high normalize fft frequency
	 */
	void setHighNormalizeFftFrequency(double freq);

	/**
	 * Gets the high normalize fft frequency.
	 *
	 * @return the high normalize fft frequency
	 */
	double getHighNormalizeFftFrequency();

	/**
	 * Sets the low normalize fft frequency.
	 *
	 * @param freq the new low normalize fft frequency
	 */
	void setLowNormalizeFftFrequency(double freq);

	/**
	 * Gets the low normalize fft frequency.
	 *
	 * @return the low normalize fft frequency
	 */
	double getLowNormalizeFftFrequency();
	
	void setBaseline(double baseline);
	
	double getBaseline();

	void setLowPassFilterFactor(double factor);
	
	double getLowPassFilterFactor();
	
	void setHighPassFilterFactor(double factor);
	
	double getHighPassFilterFactor();
}
