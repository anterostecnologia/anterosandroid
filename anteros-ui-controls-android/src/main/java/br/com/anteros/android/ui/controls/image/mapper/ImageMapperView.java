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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class ImageMapperView extends View {
	private List<Area> areas = new ArrayList<Area>();
	private List<Area> currentAreas = new ArrayList<Area>();
	private List<Area> markedAreas = new ArrayList<Area>();
	private ImageMapperListener listener;
	private boolean autoPreviewAreas;
	private boolean allowMultiselect = false;
	private boolean bLongTouch = false;
	private boolean bDrawText = false;

	private static final int LONG_PRESS_TIME = 400;

	private final Handler mLongTouchHandler = new Handler();

	private Runnable mLongTouchThread = new Runnable() {
		public void run() {
			bLongTouch = true;
		}
	};

	private float oldX;
	private float oldY;

	public ImageMapperView(Context context) {
		super(context);
	}

	public ImageMapperView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public List<Area> getAreas() {
		return areas;
	}

	public void setAreas(List<Area> areas) {
		this.areas = areas;
	}

	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private Bitmap bmp;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (bmp != null && !bmp.isRecycled()) {
			canvas.drawBitmap(bmp, 0, 0, paint);
			for (Area a : areas) {
				if (currentAreas.contains(a)) {
					a.draw(canvas, bDrawText);
				} else if (markedAreas.contains(a)) {
					a.draw(canvas, 100, 255, 205, 210, bDrawText);
				} else if (autoPreviewAreas) {
					a.draw(canvas, 100, 175, 246, 175, bDrawText);
				}
			}
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		oldX = event.getX();
		oldY = event.getY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLongTouchHandler.postDelayed(mLongTouchThread, LONG_PRESS_TIME);

			break;
		case MotionEvent.ACTION_MOVE:
			// Verifica se o movimento é menor que 2, caso o toque
			// esteja tremendo
			if ((Math.abs(oldX - event.getX()) > 2) || (Math.abs(oldY - event.getY()) > 2)) {
				mLongTouchHandler.removeCallbacks(mLongTouchThread);
			}
			break;
		case MotionEvent.ACTION_UP:
			mLongTouchHandler.removeCallbacks(mLongTouchThread);

			if (bmp != null) {
				boolean found = false;
				for (int i = 0; i < areas.size(); i++) {
					Area a = areas.get(i);

					if (!allowMultiselect) {
						if (a.isInArea(event.getX(), event.getY())) {
							found = true;
							currentAreas.clear();
							currentAreas.add(a);
							invalidate();
							if (listener != null) {
								listener.onTouchArea(a);
							}
							if (bLongTouch) {
								bLongTouch = false;
								if (listener != null)
									listener.onLongTouchArea(a);
							}
							break;
						}
					} else {
						if (a.isInArea(event.getX(), event.getY())) {
							found = true;

							if (isSelectedArea(a)) {
								if (!bLongTouch) {
									currentAreas.remove(a);
								}
							} else {
								currentAreas.add(a);
							}

							invalidate();
							if (listener != null) {
								listener.onTouchArea(a);
							}
							if (bLongTouch) {
								bLongTouch = false;
								if (listener != null)
									listener.onLongTouchArea(currentAreas.toArray(new Area[] {}));

							}
							break;
						}
					}
				}

				if (!found) {
					currentAreas.clear();
					invalidate();
				}
			}
			break;
		}

		return true;
	}

	public void addArea(Area area) {
		areas.add(area);
	}

	public void selectAreaById(String id) {
		for (Area area : areas) {
			if (area.getId().equals(id)) {
				currentAreas.add(area);
			}
		}
		invalidate();
	}

	public void selectArea(Area area) {
		currentAreas.clear();
		currentAreas.add(area);
		invalidate();
	}

	public void selectAreas(List<Area> areas) {
		currentAreas.clear();
		currentAreas.addAll(areas);
		invalidate();
	}

	public void clearSelection() {
		currentAreas.clear();
		invalidate();
	}
	
	public void markArea(Area area) {
		markedAreas.clear();
		markedAreas.add(area);
		invalidate();
	}

	public void markAreas(List<Area> areas) {
		markedAreas.clear();
		markedAreas.addAll(areas);
		invalidate();
	}

	public void clearMarking() {
		markedAreas.clear();
		invalidate();
	}

	public void setImageMapperEventListener(ImageMapperListener listener) {
		this.listener = listener;
	}

	public void setBitmap(Bitmap bmp) {
		this.bmp = bmp;
		resizeView();
		invalidate();
	}

	private void resizeView() {
		if (this.bmp != null) {
			LayoutParams params = getLayoutParams();
			params.width = bmp.getWidth();
			params.height = bmp.getHeight();
			setLayoutParams(params);
		}
	}

	public void setAutoPreviewAreas(boolean autoPreviewAreas) {
		this.autoPreviewAreas = autoPreviewAreas;
	}

	public boolean isAllowMultiselect() {
		return allowMultiselect;
	}

	public void setAllowMultiselect(boolean allowMultiselect) {
		this.allowMultiselect = allowMultiselect;
	}

	private boolean isSelectedArea(Area area) {
		return currentAreas.contains(area);
	}

	public boolean isDrawText() {
		return bDrawText;
	}

	public void setDrawText(boolean bDrawText) {
		this.bDrawText = bDrawText;
	}
}
