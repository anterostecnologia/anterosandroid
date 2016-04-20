/*
 * Copyright 2016 Anteros Tecnologia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
