package br.com.anteros.android.synchronism.view;

import android.widget.SeekBar;

public class SeekBarThread implements Runnable {

	private static final long DELAY = 40;
	private SeekBar seekbar;
	private boolean executing = true;
	private boolean stop = false;
	private int currentValue = 0;
	private boolean increment = true;

	public SeekBarThread(SeekBar seekbar) {
		this.seekbar = seekbar;
	}

	public void run() {
		while (executing) {
			if (!stop) {
				if (increment)
					increment();
				else
					decrement();

			}
		}

	}

	public void dismiss() {
		executing = false;
	}

	private void decrement() {
		while (!stop && seekbar.getProgress() > 0) {
			seekbar.setProgress(currentValue--);
			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (currentValue < 0)
			increment = true;

	}

	private void increment() {
		while (!stop && seekbar.getProgress() <= seekbar.getMax() - 1) {
			seekbar.setProgress(currentValue++);
			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (seekbar.getProgress() == seekbar.getMax())
			increment = false;
	}

	public void stop() {
		stop = true;
	}

	public boolean isStopped() {
		return stop;
	}

	public void restart() {
		stop = false;
	}

}
