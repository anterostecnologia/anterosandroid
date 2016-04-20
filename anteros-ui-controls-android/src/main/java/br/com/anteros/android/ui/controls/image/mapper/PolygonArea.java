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

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

public class PolygonArea extends Area {
	List<IPoint> points = new ArrayList<IPoint>();
	List<IPoint> originalPoints = new ArrayList<IPoint>();

	// centroid point for this poly
	float _x;
	float _y;

	// bounding box
	int top = -1;
	int bottom = -1;
	int left = -1;
	int right = -1;

	public PolygonArea(String id, String name, List<IPoint> points) {
		super(id, name);
		this.points = points;

		for (IPoint point : points) {
			originalPoints.add(point.clone());
		}

		computeCentroid();
	}

	/**
	 * area() and computeCentroid() are adapted from the implementation of
	 * polygon.java published from a princeton case study The study is here:
	 * http://introcs.cs.princeton.edu/java/35purple/ The polygon.java source is
	 * here: http://introcs.cs.princeton.edu/java/35purple/Polygon.java.html
	 */

	// return area of polygon
	public double area() {
		double sum = 0.0;
		for (int i = 0; i < points.size() - 1; i++) {
			sum = sum + (points.get(i).getX() * points.get(i + 1).getY())
					- (points.get(i).getY() * points.get(i + 1).getX());
		}
		sum = 0.5 * sum;
		return Math.abs(sum);
	}

	// compute the centroid of the polygon
	public void computeCentroid() {
		double cx = 0.0, cy = 0.0;
		for (int i = 0; i < points.size() - 1; i++) {
			cx = cx
					+ (points.get(i).getX() + points.get(i + 1).getX())
					* (points.get(i).getY() * points.get(i + 1).getX() - points.get(i).getX()
							* points.get(i + 1).getY());
			cy = cy
					+ (points.get(i).getY() + points.get(i + 1).getX())
					* (points.get(i).getY() * points.get(i + 1).getX() - points.get(i).getX()
							* points.get(i + 1).getY());
		}
		cx /= (6 * area());
		cy /= (6 * area());
		_x = Math.abs((int) cx);
		_y = Math.abs((int) cy);
	}

	@Override
	public float getOriginX() {
		return _x;
	}

	@Override
	public float getOriginY() {
		return _y;
	}

	/**
	 * This is a java port of the W. Randolph Franklin algorithm explained here
	 * http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly .html
	 */
	@Override
	public boolean isInArea(float testx, float testy) {
		int i, j;
		boolean c = false;
		for (i = 0, j = points.size() - 1; i < points.size(); j = i++) {
			if (((points.get(i).getY() > testy) != (points.get(j).getY() > testy))
					&& (testx < (points.get(j).getX() - points.get(i).getX()) * (testy - points.get(i).getY())
							/ (points.get(j).getY() - points.get(i).getY()) + points.get(i).getX()))
				c = !c;
		}
		return c;
	}

	@Override
	public void draw(Canvas canvas, int alfa, int r, int g, int b, boolean drawText) {
		Point[] points = getPoints();
		if (points.length < 2) {
			return;
		}

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

		// path
		Path polyPath = new Path();
		polyPath.moveTo(points[0].x, points[0].y);
		int i, len;
		len = points.length;
		for (i = 0; i < len; i++) {
			polyPath.lineTo(points[i].x, points[i].y);
		}
		polyPath.lineTo(points[0].x, points[0].y);

		if (drawText) {
			if (getName() != null) {
				paint.setTextSize(16);
				paint.setColor(Color.BLACK);
				paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

				if (points.length >= 4) {
					Rect bounds = new Rect((int) points[0].x, (int) points[0].y, (int) points[2].x, (int) points[2].y);
					TextPaint textPaint = new TextPaint(paint);
					StaticLayout sl = new StaticLayout(getName(), textPaint, (int) bounds.width(),
							Layout.Alignment.ALIGN_CENTER, 1, 1, true);
					canvas.save();

					float textYCoordinate = bounds.exactCenterY() - (sl.getHeight() / 2);
					float textXCoordinate = bounds.exactCenterX() - (sl.getWidth() / 2);

					canvas.translate(textXCoordinate, textYCoordinate);
					sl.draw(canvas);
					canvas.restore();

				} else {
					canvas.drawText(getName(), (points[0].x + ((points[1].x - points[0].x) / 2)), (points[0].y
							+ ((points[(len - 1)].y - points[0].y) / 2) + (paint.getTextSize() / 2)), paint);
				}
			}
		}

		// paint
		paint.setColor(Color.argb(alfa, r, g, b));
		paint.setStyle(Style.FILL);

		// draw
		canvas.drawPath(polyPath, paint);
	}

	private Point[] getPoints() {
		Point[] result = new Point[points.size()];
		for (int i = 0; i < points.size(); i++) {
			result[i] = new Point(points.get(i).getX(), points.get(i).getY());
		}
		return result;
	}

	@Override
	public void applyScale(float scaleWidth, float scaleHeight) {
		points.clear();
		for (IPoint iPoint : originalPoints) {
			IPoint newPoint = iPoint.clone();
			newPoint.setX(newPoint.getX() * scaleWidth);
			newPoint.setY(newPoint.getY() * scaleHeight);
			points.add(newPoint);
		}

	}

}