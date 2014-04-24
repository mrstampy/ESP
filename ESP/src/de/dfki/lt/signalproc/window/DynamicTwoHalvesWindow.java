package de.dfki.lt.signalproc.window;

public class DynamicTwoHalvesWindow extends DynamicWindow {
	protected double prescale;

	public DynamicTwoHalvesWindow(int windowType) {
		super(windowType);
		prescale = 1.;
	}

	public DynamicTwoHalvesWindow(int windowType, double prescale) {
		super(windowType);
		this.prescale = prescale;
	}

	/**
	 * apply the left half of a window of the specified type to the data. The left
	 * half will be as long as the given len.
	 */
	public void applyInlineLeftHalf(double[] data, int off, int len) {
		Window w = Window.get(windowType, 2 * len, prescale);
		w.apply(data, off, data, off, 0, len);
	}

	/**
	 * apply the right half of a window of the specified type to the data. The
	 * right half will be as long as the given len.
	 */
	public void applyInlineRightHalf(double[] data, int off, int len) {
		Window w = Window.get(windowType, 2 * len, prescale);
		w.apply(data, off, data, off, len, len);
	}

}
