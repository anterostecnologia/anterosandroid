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

package br.com.anteros.android.ui.controls.grid;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class ScrollBar extends Drawable {

	private boolean horizontal;
	private Rect boundsBar;
	private transient float knobPoint, knobSize;
	private int screenSize;
	private int fullScreenSize;
	private int position;
	private Paint paint = new Paint();

	public ScrollBar(boolean horizontal, int screenSize, int fullScreenSize,
			int position) {
		this.horizontal = horizontal;
		this.screenSize = screenSize;
		this.fullScreenSize = fullScreenSize;
		this.position = position;
		paint.setAntiAlias(true);
		paint.setColor(Color.DKGRAY);
	}

	@Override
	public void draw(Canvas canvas) {
		computeScrollBar();
		paint.setColor(Color.DKGRAY);
		canvas.drawRect(this.getBounds(), paint);
		paint.setColor(Color.LTGRAY);
		if (horizontal) {
			canvas.drawRoundRect(new RectF(knobPoint+2, boundsBar.top, knobPoint
					+ knobSize+2, boundsBar.bottom), 2, 2, paint);
			paint.setColor(Color.GRAY);
			canvas.drawRoundRect(new RectF(knobPoint+3, boundsBar.top+1, knobPoint
					+ knobSize+1, boundsBar.bottom-1), 2, 2, paint);
		} else {
			canvas.drawRoundRect(new RectF(boundsBar.left, boundsBar.top+knobPoint,
					boundsBar.right, boundsBar.top+knobPoint + knobSize), 2, 2, paint);
			paint.setColor(Color.GRAY);
			canvas.drawRoundRect(new RectF(boundsBar.left+1, boundsBar.top+knobPoint+1,
					boundsBar.right-1, boundsBar.top+knobPoint + knobSize-1), 2, 2, paint);
		}
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	@Override
	public void setAlpha(int alpha) {

	}

	@Override
	public void setColorFilter(ColorFilter cf) {

	}

	protected void computeScrollBar() {
		float t = (horizontal ? boundsBar.right - boundsBar.left
				: boundsBar.bottom - boundsBar.top);
		float v = screenSize;
		float p = position;
		float c = fullScreenSize;
		knobSize = Math.min(Math.max(15, t * v / c), t);
		if (c > v)
			knobPoint = p * (t - knobSize) / (c - v);
		else {
			knobPoint = 0;
			knobSize = 0;
		}
	}

	public int getScreenSize() {
		return screenSize;
	}

	public void setScreenSize(int screenSize) {
		this.screenSize = screenSize;
	}

	public int getFullScreenSize() {
		return fullScreenSize;
	}

	public void setFullScreenSize(int fullScreenSize) {
		this.fullScreenSize = fullScreenSize;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	@Override
	public void setBounds(Rect bounds) {
		super.setBounds(bounds);
		boundsBar = new Rect(bounds.left + 2, bounds.top + 2, bounds.right - 2,
				bounds.bottom - 2);
	}

	@Override
	public void setBounds(int left, int top, int right, int bottom) {
		super.setBounds(left, top, right, bottom);
		boundsBar = new Rect(left + 2, top + 2, right - 2, bottom - 2);
	}

}