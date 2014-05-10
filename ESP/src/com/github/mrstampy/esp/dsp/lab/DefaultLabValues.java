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
package com.github.mrstampy.esp.dsp.lab;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class DefaultLabValues.
 */
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

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#getFftType()
	 */
	public FFTType getFftType() {
		return fftType;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#setFftType(com.github.mrstampy.esp.dsp.lab.FFTType)
	 */
	public void setFftType(FFTType fftType) {
		this.fftType = fftType;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#getNumBands()
	 */
	public int getNumBands() {
		return numBands;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#setNumBands(int)
	 */
	public void setNumBands(int numBands) {
		this.numBands = numBands;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#getHighPassFrequency()
	 */
	public double getHighPassFrequency() {
		return highPassFrequency;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#setHighPassFrequency(double)
	 */
	public void setHighPassFrequency(double highPassFrequency) {
		this.highPassFrequency = highPassFrequency;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#getLowPassFrequency()
	 */
	public double getLowPassFrequency() {
		return lowPassFrequency;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#setLowPassFrequency(double)
	 */
	public void setLowPassFrequency(double lowPassFrequency) {
		this.lowPassFrequency = lowPassFrequency;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#getPassFilter()
	 */
	public PassFilter getPassFilter() {
		return passFilter;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#setPassFilter(com.github.mrstampy.esp.dsp.lab.PassFilter)
	 */
	public void setPassFilter(PassFilter passFilter) {
		this.passFilter = passFilter;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#isAbsoluteValues()
	 */
	public boolean isAbsoluteValues() {
		return absoluteValues;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#setAbsoluteValues(boolean)
	 */
	public void setAbsoluteValues(boolean absoluteValues) {
		this.absoluteValues = absoluteValues;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#isNormalizeFft()
	 */
	public boolean isNormalizeFft() {
		return normalizeFft;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#setNormalizeFft(boolean)
	 */
	public void setNormalizeFft(boolean normalizeFft) {
		this.normalizeFft = normalizeFft;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#isNormalizeSignal()
	 */
	public boolean isNormalizeSignal() {
		return normalizeSignal;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#setNormalizeSignal(boolean)
	 */
	public void setNormalizeSignal(boolean normalizeSignal) {
		this.normalizeSignal = normalizeSignal;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#getHighNormalizeFftFrequency()
	 */
	public double getHighNormalizeFftFrequency() {
		return highNormalizeFftFrequency;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#setHighNormalizeFftFrequency(double)
	 */
	public void setHighNormalizeFftFrequency(double highNormalizeFftFrequency) {
		this.highNormalizeFftFrequency = highNormalizeFftFrequency;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#getLowNormalizeFftFrequency()
	 */
	public double getLowNormalizeFftFrequency() {
		return lowNormalizeFftFrequency;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#setLowNormalizeFftFrequency(double)
	 */
	public void setLowNormalizeFftFrequency(double lowNormalizeFftFrequency) {
		this.lowNormalizeFftFrequency = lowNormalizeFftFrequency;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#getBaseline()
	 */
	public double getBaseline() {
		return baseline;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#setBaseline(double)
	 */
	public void setBaseline(double baseline) {
		this.baseline = baseline;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#getLowPassFilterFactor()
	 */
	public double getLowPassFilterFactor() {
		return lowPassFilterFactor;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#setLowPassFilterFactor(double)
	 */
	public void setLowPassFilterFactor(double passFilterFactor) {
		this.lowPassFilterFactor = passFilterFactor;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#getHighPassFilterFactor()
	 */
	public double getHighPassFilterFactor() {
		return highPassFilterFactor;
	}

	/* (non-Javadoc)
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#setHighPassFilterFactor(double)
	 */
	public void setHighPassFilterFactor(double highPassFilterFactor) {
		this.highPassFilterFactor = highPassFilterFactor;
	}

}
