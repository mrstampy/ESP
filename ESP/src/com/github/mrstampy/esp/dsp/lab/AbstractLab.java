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

import java.util.ArrayList;
import java.util.List;

import rx.Scheduler;
import rx.Scheduler.Inner;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractLab encapsulates common Lab properties.
 */
public abstract class AbstractLab implements Lab {
	private static final long serialVersionUID = 7423309217437369736L;

	private RawEspConnection connection;
	private FFTType fftType = FFTType.no_fft;
	private int numBands;
	private List<SignalProcessedListener> listeners = new ArrayList<SignalProcessedListener>();
	private int channel = 1;

	/**
	 * Instantiates a new abstract lab.
	 */
	public AbstractLab() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#triggerProcessing()
	 */
	@Override
	public void triggerProcessing() {
		connectionCheck();

		Schedulers.newThread().schedule(new Action1<Scheduler.Inner>() {

			@Override
			public void call(Inner t1) {
				process(getConnection().getCurrentFor(getChannel()));
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#triggerProcessing(int)
	 */
	@Override
	public void triggerProcessing(final int numSamples) {
		connectionCheck();

		Schedulers.newThread().schedule(new Action1<Scheduler.Inner>() {

			@Override
			public void call(Inner t1) {
				process(getConnection().getCurrentFor(numSamples, getChannel()));
			}
		});
	}

	/**
	 * Notify processed listeners.
	 *
	 * @param processed
	 *          the processed
	 */
	protected void notifyProcessedListeners(double[] processed) {
		for (SignalProcessedListener l : listeners) {
			l.signalProcessed(processed);
		}
	}

	/**
	 * Connection check.
	 */
	void connectionCheck() {
		if (getConnection() == null) throw new RuntimeException("No RawEspConnection implementation in the lab");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#getConnection()
	 */
	@Override
	public RawEspConnection getConnection() {
		return connection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.esp.dsp.lab.Lab#setConnection(com.github.mrstampy.esp
	 * .dsp.lab.RawEspConnection)
	 */
	@Override
	public void setConnection(RawEspConnection connection) {
		this.connection = connection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#getFftType()
	 */
	@Override
	public FFTType getFftType() {
		return fftType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.esp.dsp.lab.LabValues#setFftType(com.github.mrstampy
	 * .esp.dsp.lab.FFTType)
	 */
	@Override
	public void setFftType(FFTType fftType) {
		this.fftType = fftType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#getNumBands()
	 */
	@Override
	public int getNumBands() {
		return numBands;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#setNumBands(int)
	 */
	@Override
	public void setNumBands(int numBands) {
		this.numBands = numBands;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#getLabValues()
	 */
	@Override
	public LabValues getLabValues() {
		DefaultLabValues values = new DefaultLabValues();

		values.setNumBands(getNumBands());
		values.setAbsoluteValues(isAbsoluteValues());
		values.setFftType(getFftType());
		values.setHighNormalizeFftFrequency(getHighNormalizeFftFrequency());
		values.setLowNormalizeFftFrequency(getLowNormalizeFftFrequency());
		values.setHighPassFrequency(getHighPassFrequency());
		values.setLowPassFrequency(getLowPassFrequency());
		values.setNormalizeFft(isNormalizeFft());
		values.setNormalizeSignal(isNormalizeSignal());
		values.setPassFilter(getPassFilter());
		values.setBaseline(getBaseline());
		values.setLowPassFilterFactor(getLowPassFilterFactor());
		values.setHighPassFilterFactor(getHighPassFilterFactor());
		values.setWindowFunction(getWindowFunction());
		values.setChannel(getChannel());
		values.setNumChannels(getNumChannels());

		return values;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.esp.dsp.lab.Lab#setLabValues(com.github.mrstampy.esp
	 * .dsp.lab.LabValues)
	 */
	@Override
	public void setLabValues(LabValues values) {
		setNumBands(values.getNumBands());
		setAbsoluteValues(values.isAbsoluteValues());
		setFftType(values.getFftType());
		setHighNormalizeFftFrequency(values.getHighNormalizeFftFrequency());
		setLowNormalizeFftFrequency(values.getLowNormalizeFftFrequency());
		setHighPassFrequency(values.getHighPassFrequency());
		setLowPassFrequency(values.getLowPassFrequency());
		setNormalizeFft(values.isNormalizeFft());
		setNormalizeSignal(values.isNormalizeSignal());
		setPassFilter(values.getPassFilter());
		setBaseline(values.getBaseline());
		setLowPassFilterFactor(values.getLowPassFilterFactor());
		setHighPassFilterFactor(values.getHighPassFilterFactor());
		setWindowFunction(values.getWindowFunction());
		setChannel(values.getChannel());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.esp.dsp.lab.Lab#addSignalProcessedListener(com.github
	 * .mrstampy.esp.dsp.lab.SignalProcessedListener)
	 */
	@Override
	public void addSignalProcessedListener(SignalProcessedListener l) {
		if (l != null && !listeners.contains(l)) listeners.add(l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.esp.dsp.lab.Lab#removeSignalProcessedListener(com.github
	 * .mrstampy.esp.dsp.lab.SignalProcessedListener)
	 */
	@Override
	public void removeSignalProcessedListener(SignalProcessedListener l) {
		if (l != null) listeners.remove(l);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.esp.dsp.lab.Lab#clearSignalProcessedListeners()
	 */
	@Override
	public void clearSignalProcessedListeners() {
		listeners.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#getChannel()
	 */
	public int getChannel() {
		return channel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#setChannel(int)
	 */
	public void setChannel(int channel) {
		this.channel = channel;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.esp.dsp.lab.LabValues#getNumChannels()
	 */
	@Override
	public int getNumChannels() {
		return getConnection().getNumChannels();
	}

}