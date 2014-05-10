package com.github.mrstampy.esp.dsp.lab;

import java.io.Serializable;

public class DefaultLabValues implements LabValues, Serializable {
	private static final long serialVersionUID = -6703910237225704114L;
	
	private FFTType fftType;
	private int numBands;
	private double highPassFrequency;
	private double lowPassFrequency;
	private PassFilter passFilter;
	private boolean absoluteValues;
	private boolean normalizeFft;
	private boolean normalizeSignal;
	private double highNormalizeFftFrequency;
	private double lowNormalizeFftFrequency;
	private double baseline;
	private double lowPassFilterFactor;
	private double highPassFilterFactor;

	public FFTType getFftType() {
		return fftType;
	}

	public void setFftType(FFTType fftType) {
		this.fftType = fftType;
	}

	public int getNumBands() {
		return numBands;
	}

	public void setNumBands(int numBands) {
		this.numBands = numBands;
	}

	public double getHighPassFrequency() {
		return highPassFrequency;
	}

	public void setHighPassFrequency(double highPassFrequency) {
		this.highPassFrequency = highPassFrequency;
	}

	public double getLowPassFrequency() {
		return lowPassFrequency;
	}

	public void setLowPassFrequency(double lowPassFrequency) {
		this.lowPassFrequency = lowPassFrequency;
	}

	public PassFilter getPassFilter() {
		return passFilter;
	}

	public void setPassFilter(PassFilter passFilter) {
		this.passFilter = passFilter;
	}

	public boolean isAbsoluteValues() {
		return absoluteValues;
	}

	public void setAbsoluteValues(boolean absoluteValues) {
		this.absoluteValues = absoluteValues;
	}

	public boolean isNormalizeFft() {
		return normalizeFft;
	}

	public void setNormalizeFft(boolean normalizeFft) {
		this.normalizeFft = normalizeFft;
	}

	public boolean isNormalizeSignal() {
		return normalizeSignal;
	}

	public void setNormalizeSignal(boolean normalizeSignal) {
		this.normalizeSignal = normalizeSignal;
	}

	public double getHighNormalizeFftFrequency() {
		return highNormalizeFftFrequency;
	}

	public void setHighNormalizeFftFrequency(double highNormalizeFftFrequency) {
		this.highNormalizeFftFrequency = highNormalizeFftFrequency;
	}

	public double getLowNormalizeFftFrequency() {
		return lowNormalizeFftFrequency;
	}

	public void setLowNormalizeFftFrequency(double lowNormalizeFftFrequency) {
		this.lowNormalizeFftFrequency = lowNormalizeFftFrequency;
	}

	public double getBaseline() {
		return baseline;
	}

	public void setBaseline(double baseline) {
		this.baseline = baseline;
	}

	public double getLowPassFilterFactor() {
		return lowPassFilterFactor;
	}

	public void setLowPassFilterFactor(double passFilterFactor) {
		this.lowPassFilterFactor = passFilterFactor;
	}

	public double getHighPassFilterFactor() {
		return highPassFilterFactor;
	}

	public void setHighPassFilterFactor(double highPassFilterFactor) {
		this.highPassFilterFactor = highPassFilterFactor;
	}

}
