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

package br.com.anteros.android.ui.controls.image.mapper;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import java.util.List;

public class RoundRectangleArea extends Area {

	float _left;
	float _top;
	float _right;
	float _bottom;
	float _originalLeft;
	float _originalTop;
	float _originalRight;
	float _originalBottom;

	public RoundRectangleArea(String id, String name, List<IPoint> points) {
		super(id, name);
		if (points.get(0).getX() > points.get(1).getX()) {
			_left = points.get(1).getX();
			_top = points.get(1).getY();
			_right = points.get(0).getX();
			_bottom = points.get(0).getY();
		} else {
			_left = points.get(0).getX();
			_top = points.get(0).getY();
			_right = points.get(1).getX();
			_bottom = points.get(1).getY();
		}

		_originalLeft = _left;
		_originalTop = _top;
		_originalRight = _right;
		_originalBottom = _bottom;
	}

	public boolean isInArea(float x, float y) {
		boolean ret = false;
		if ((x > _left) && (x < _right)) {
			if ((y > _top) && (y < _bottom)) {
				ret = true;
			}
		}
		return ret;
	}

	public float getOriginX() {
		return _left;
	}

	public float getOriginY() {
		return _top;
	}

	@Override
	public void draw(Canvas canvas, int alfa, int r, int g, int b, boolean drawText) {

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

		if (drawText) {
			if (getName() != null) {
				paint.setTextAlign(Paint.Align.CENTER);

				int density = canvas.getDensity();
				if (density > 0)
					paint.setTextSize(getTextSize() * (density / Area.DENSITY_ULTRA_LOW));
				else
					paint.setTextSize(getTextSize());

				paint.setColor(Color.BLACK);
				paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

				Rect bounds = new Rect((int) _left, (int) _top, (int) _right, (int) _bottom);
				TextPaint textPaint = new TextPaint(paint);
				StaticLayout sl = new StaticLayout(getName(), textPaint, (int) bounds.width(),
						Layout.Alignment.ALIGN_CENTER, 1, 1, true);
				canvas.save();

				float textYCoordinate = bounds.exactCenterY() - (sl.getHeight() / 2);
				float textXCoordinate = sl.getWidth() / 4;

				canvas.translate(textXCoordinate, textYCoordinate);
				sl.draw(canvas);
				canvas.restore();
			}
		}

		paint.setColor(Color.argb(alfa, r, g, b));
		paint.setStyle(Style.FILL);

		canvas.drawRoundRect(new RectF(_left, _top, _right, _bottom), 15, 15, paint);
	}

	@Override
	public void applyScale(float scaleWidth, float scaleHeight) {
		_left = _originalLeft * scaleWidth;
		_right = _originalRight * scaleWidth;
		_top = _originalTop * scaleHeight;
		_bottom = _originalBottom * scaleHeight;
	}

}
