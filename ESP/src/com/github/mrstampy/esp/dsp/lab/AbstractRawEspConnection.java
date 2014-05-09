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

import com.github.mrstampy.esp.multiconnectionsocket.ConnectionEventListener;
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

/**
 * Abstract implementation.
 * 
 * @author burton
 *
 * @param <SOCKET>
 */
public abstract class AbstractRawEspConnection<SOCKET extends MultiConnectionSocket> implements RawEspConnection {

	private EspWindowFunction function = EspWindowFunction.HAMMING;

	@Override
	public void addConnectionEventListener(ConnectionEventListener listener) {
		getSocket().addConnectionEventListener(listener);
	}

	@Override
	public void removeConnectionEventListener(ConnectionEventListener listener) {
		getSocket().removeConnectionEventListener(listener);
	}

	@Override
	public void clearConnectionEventListeners() {
		getSocket().clearConnectionEventListeners();
	}

	@Override
	public void start() throws MultiConnectionSocketException {
		getSocket().start();
	}

	@Override
	public void stop() {
		getSocket().stop();
	}

	@Override
	public boolean isConnected() {
		return getSocket().isConnected();
	}

	@Override
	public void bindBroadcaster() throws IOException {
		getSocket().bindBroadcaster();
	}

	@Override
	public void unbindBroadcaster() {
		getSocket().unbindBroadcaster();
	}

	@Override
	public boolean isBound() {
		return getSocket().isBound();
	}

	@Override
	public boolean canBroadcast() {
		return getSocket().canBroadcast();
	}

	public EspWindowFunction getWindowFunction() {
		return function;
	}

	public void setWindowFunction(EspWindowFunction function) {
		this.function = function;
		getUtilities().setWindow(createWindow());
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

	protected abstract SOCKET getSocket();

}
