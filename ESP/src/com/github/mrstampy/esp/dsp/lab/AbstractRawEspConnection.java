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

import java.io.IOException;
import java.util.List;

import com.github.mrstampy.esp.multiconnectionsocket.ConnectionEventListener;
import com.github.mrstampy.esp.multiconnectionsocket.EspChannel;
import com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocket;
import com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocketException;

import ddf.minim.analysis.BartlettHannWindow;
import ddf.minim.analysis.BartlettWindow;
import ddf.minim.analysis.BlackmanWindow;
import ddf.minim.analysis.CosineWindow;
import ddf.minim.analysis.GaussWindow;
import ddf.minim.analysis.HammingWindow;
import ddf.minim.analysis.HannWindow;
import ddf.minim.analysis.LanczosWindow;
import ddf.minim.analysis.RectangularWindow;
import ddf.minim.analysis.TriangularWindow;
import ddf.minim.analysis.WindowFunction;

// TODO: Auto-generated Javadoc
/**
 * Abstract implementation.
 *
 * @author burton
 * @param <SOCKET>
 *          the generic type
 */
public abstract class AbstractRawEspConnection<SOCKET extends MultiConnectionSocket> implements RawEspConnection {

	private EspWindowFunction function = EspWindowFunction.RECTANGULAR;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocket#
	 * addConnectionEventListener
	 * (com.github.mrstampy.esp.multiconnectionsocket.ConnectionEventListener)
	 */
	@Override
	public void addConnectionEventListener(ConnectionEventListener listener) {
		getSocket().addConnectionEventListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocket#
	 * removeConnectionEventListener
	 * (com.github.mrstampy.esp.multiconnectionsocket.ConnectionEventListener)
	 */
	@Override
	public void removeConnectionEventListener(ConnectionEventListener listener) {
		getSocket().removeConnectionEventListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocket#
	 * clearConnectionEventListeners()
	 */
	@Override
	public void clearConnectionEventListeners() {
		getSocket().clearConnectionEventListeners();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocket#start()
	 */
	@Override
	public void start() throws MultiConnectionSocketException {
		getSocket().start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocket#stop()
	 */
	@Override
	public void stop() {
		getSocket().stop();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocket#isConnected
	 * ()
	 */
	@Override
	public boolean isConnected() {
		return getSocket().isConnected();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocket#
	 * bindBroadcaster()
	 */
	@Override
	public void bindBroadcaster() throws IOException {
		getSocket().bindBroadcaster();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocket#
	 * unbindBroadcaster()
	 */
	@Override
	public void unbindBroadcaster() {
		getSocket().unbindBroadcaster();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocket#isBound
	 * ()
	 */
	@Override
	public boolean isBound() {
		return getSocket().isBound();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocket#
	 * canBroadcast()
	 */
	@Override
	public boolean canBroadcast() {
		return getSocket().canBroadcast();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.esp.dsp.lab.RawEspConnection#getWindowFunction()
	 */
	public EspWindowFunction getWindowFunction() {
		return function;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.esp.dsp.lab.RawEspConnection#setWindowFunction(com.
	 * github.mrstampy.esp.dsp.lab.EspWindowFunction)
	 */
	public void setWindowFunction(EspWindowFunction function) {
		this.function = function;
		getUtilities().setWindow(createWindow());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocket#
	 * getNumChannels()
	 */
	public int getNumChannels() {
		return getSocket().getNumChannels();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocket#getChannels
	 * ()
	 */
	@Override
	public List<EspChannel> getChannels() {
		return getSocket().getChannels();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.github.mrstampy.esp.multiconnectionsocket.MultiConnectionSocket#getChannel
	 * (int)
	 */
	@Override
	public EspChannel getChannel(int channelNumber) {
		return getSocket().getChannel(channelNumber);
	}

	/**
	 * Returns a {@link DefaultLab} instance. Can be overridden in subclasses for
	 * custom {@link Lab} implementations.
	 */
	public Lab getDefaultLab() {
		return new DefaultLab();
	}

	private WindowFunction createWindow() {
		switch (getWindowFunction()) {
		case BARTLETT:
			return new BartlettWindow();
		case BARTLETT_HANN:
			return new BartlettHannWindow();
		case BLACKMAN:
			return new BlackmanWindow();
		case COSINE:
			return new CosineWindow();
		case GAUSS:
			return new GaussWindow();
		case HAMMING:
			return new HammingWindow();
		case HANN:
			return new HannWindow();
		case LANCZOS:
			return new LanczosWindow();
		case TRIANGULAR:
			return new TriangularWindow();
		default:
			return new RectangularWindow();
		}
	}

	/**
	 * Gets the socket.
	 *
	 * @return the socket
	 */
	public abstract SOCKET getSocket();

}
