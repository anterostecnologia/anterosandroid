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

package br.com.anteros.android.core.util;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class CanvasUtils {

	private static Paint paint = new Paint();
	private static DrawProperties dp = new DrawProperties();
	
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int TOP = 2;
	public static final int BOTTOM = 3;
	public static final int HCENTER = 4;
	public static final int VCENTER = 5;

	public static void fillRect(Canvas canvas, int left, int top, int right,
			int bottom, boolean drawBorder, int fillColor, int borderColor, float borderSize) {
		if (borderSize == 0)
			borderSize = 0.5f;
		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		canvas.save();
		canvas.clipRect(new Rect(left,top,right,bottom));
		if (drawBorder) {
			paint.setColor(borderColor);
			canvas.drawRect(left, top, right, bottom, paint);
			paint.setColor(fillColor);
			canvas.drawRect(left + borderSize, top + borderSize, right - (borderSize*2), bottom - (borderSize*2),
					paint);
		} else {
			paint.setColor(fillColor);
			canvas.drawRect(left, top, right, bottom, paint);
		}
		canvas.restore();

	}

	public static void fillRect(Canvas canvas, Rect rect, boolean drawBorder,
			int fillColor, int borderColor, float borderSize) {
		fillRect(canvas, rect.left, rect.top, rect.right, rect.bottom,
				drawBorder, fillColor, borderColor, borderSize);
	}

	public static void drawText(Canvas canvas, Rect rect, String text,
			DrawProperties properties, int alignVertical, int alignHorizontal) {

		paint.setColor(properties.fontColor);
		paint.setTextSize(properties.fontSize);
		paint.setTypeface(properties.fontType);
		
		float newLeft = rect.left;
		float newTop = rect.top;

		if (alignVertical== TOP) {
            newTop -= +paint.getFontMetrics().ascent;
		} else if (alignVertical== BOTTOM) {
			newTop = rect.bottom-paint.getFontMetrics().descent;
		} else if (alignVertical== VCENTER) {
			float heightText = paint.getFontMetrics().ascent+paint.getFontMetrics().descent;
			newTop += (rect.height()/2)-(heightText/2);
		}
		
		if (alignHorizontal== HCENTER) {
			newLeft += + rect.width()/2 - paint.measureText(text) / 2;
		} else if (alignHorizontal== RIGHT) {
			newLeft = rect.right - paint.measureText(text) - 2;
		}
		
		canvas.save();
		canvas.clipRect(rect);
		canvas.drawText(text, newLeft, newTop, paint);
		canvas.restore();
	}
	
	
	public static void drawText(Canvas canvas, int left, int top, int right, int bottom, String text,
			DrawProperties properties, int alignVertical, int alignHorizontal) {
		drawText(canvas, new Rect(left,top,right,bottom), text, properties, alignVertical, alignHorizontal);
	}
	
	public static void drawText(Canvas canvas, int left, int top, int right, int bottom, String text,
			int fontColor, int fontSize, int alignVertical, int alignHorizontal) {
		dp.fontColor = fontColor;
		dp.fontSize = fontSize;
		drawText(canvas, new Rect(left,top,right,bottom), text, dp, alignVertical, alignHorizontal);
	}
	
	public static void drawText(Canvas canvas, Rect rect, String text,
			int fontColor, int fontSize, int alignVertical, int alignHorizontal) {
		dp.fontColor = fontColor;
		dp.fontSize = fontSize;
		drawText(canvas, rect, text, dp, alignVertical, alignHorizontal);
	}
	
	
}
