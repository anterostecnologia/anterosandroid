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

package br.com.anteros.android.ui.controls.image;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * Just like image view, but with scrolling and scale abilities.
 * 
 * TODO write better description with usage examples.
 * 
 * @author Konstantin Burov (aectann@gmail.com)
 *
 */
public class BigImage extends View implements OnGestureListener, OnTouchListener  {

	/**
	 * TODO
	 */
	protected static final String ATTR_BOUND_PAD = "boundPad";
	protected static final String ATTR_MAP = "map";
	protected static final String ATTR_SRC = "src";
	protected float scale;
	protected float viewWidth;
	protected float viewHeight;
	protected GestureDetector gestureDetector;
	protected float dx;
	protected float dy;
	protected Matrix matrix;
	protected RectF bounds;
	protected float initScale;
	private int imageWidth;
	private int imageHeight;
	protected boolean boundsInitialized;
	protected Handler guiHander;
	protected int bitmapResource;
	private double scaleFactor;
	private String file;
  private int[] coords;
	private static Map<String, SoftReference<Drawable>> drawableCache = new HashMap<String, SoftReference<Drawable>>();
	
	public BigImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		guiHander = new Handler();
		bitmapResource = attrs.getAttributeResourceValue(null, ATTR_SRC, 0);
		setFocusable(true);
		setFocusableInTouchMode(true);
		gestureDetector = new GestureDetector(context, this);
		this.setOnTouchListener(this);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (matrix != null) {
		  int[] coords = getCoords();
			matrix.reset();
			ajustDeltas();
			matrix.postScale(scale, scale);
			matrix.postTranslate(dx + coords[0], dy + coords[1]);
			canvas.save();
			canvas.setMatrix(matrix);
			Drawable image = getImage();
			image.draw(canvas);
			canvas.restore();
		} else {
			super.onDraw(canvas);
		}
		if(!boundsInitialized){
			initBounds();
		}
	}

  private int[] getCoords() {
    if (coords == null) {
      coords = new int[2];
      getLocationOnScreen(coords);
    }
    return coords;
  }

	private Drawable getImage() {
	  String drawableKey = getDrawableKey();
    Drawable result = drawableCache.containsKey(drawableKey) ? drawableCache.get(drawableKey).get() : null;
    if (result == null) {
      if (bitmapResource > 0) {
        result = getResources().getDrawable(bitmapResource);
      } else{
        try {
          InputStream stream = new BufferedInputStream(new FileInputStream(file), 4096);
          Options options = new Options();
          options.inInputShareable = true;
          options.inPurgeable = true;
          options.inPreferredConfig = Config.ARGB_8888;
          options.inDither = true;
          result = new BitmapDrawable(getResources(), BitmapFactory.decodeStream(stream, null, options));
          stream.close();
        } catch (IOException e) {
          throw new IllegalStateException(e);
        }
      }
  	  result.setBounds(0, 0, imageWidth, imageHeight);
  	  drawableCache.put(drawableKey, new SoftReference<Drawable>(result));
	  } 
    return result;
	}

	private String getDrawableKey() {
    return file == null ? String.valueOf(bitmapResource) : file;
  }

  protected synchronized void initBounds() {
			viewWidth = getMeasuredWidth();
			viewHeight = getMeasuredHeight();
			if (viewWidth > 0 && viewHeight > 0 && (bitmapResource > 0 || file != null)) {
				Options opt = loadBitmapOpts();
				imageWidth = opt.outWidth;
				imageHeight = opt.outHeight;
				initScale = Math.min(viewWidth/imageWidth, viewHeight/imageHeight);
				dx = 0;
				dy = 0;
				matrix = new Matrix();
				scale = initScale;
				scaleFactor = 1 / initScale;
				this.boundsInitialized = true;
				notify();
			} else {
				matrix = null;
				this.boundsInitialized = false;
			}
			invalidate();
		}
	
	public void setImageFile(String file){
		setImageFile(file, null);
	}
	
	public void setImageFile(String file, Drawable drawable){
    this.file = file;
    this.bitmapResource = 0;
    if (drawable != null) {
      drawableCache.put(getDrawableKey(), new SoftReference<Drawable>(drawable));
    }
    initBounds();
  }
	
	public void setImageResource(int drawable){
      this.file = null;
      this.bitmapResource = drawable;
      initBounds();
  }

	private Options loadBitmapOpts() {
		Options opts = new Options();
		opts.inJustDecodeBounds = true;
    InputStream stream;
    if(bitmapResource > 0){
    	stream = getResources().openRawResource(bitmapResource);
    } else{
    	try {
    		stream = new BufferedInputStream(new FileInputStream(file));
    	} catch (FileNotFoundException e) {
    		throw new IllegalStateException(e);
    	}
    }
    BitmapFactory.decodeStream(stream ,null, opts);
		return opts;
	}

	private void ajustDeltas() {
		if (dx > 0) {
			dx = 0;
		}
		if (dy > 0) {
			dy = 0;
		}
		float minDx = 0;
		if (imageWidth * scale < viewWidth) {
			minDx = (viewWidth - imageWidth * scale) / 2;
		} else {
			minDx = -imageWidth * scale + viewWidth;
		}
		if (scale == initScale && viewWidth > viewHeight) {
			dx = (viewWidth - imageWidth * scale) / 2;
		} else if (dx < minDx) {
			dx = minDx;
		}
		float minDy = 0;
		if (imageHeight * scale < viewHeight) {
			minDy = (viewHeight - imageHeight * scale) / 2;
		} else {
			minDy = -imageHeight * scale + viewHeight;
		}
		if (scale == initScale && viewHeight > viewWidth) {
			dy = (viewHeight - imageHeight * scale) / 2;
		} else if (dy < minDy) {
			dy = minDy;
		}
	}

	/**
	 * Restores the map state to the initial.
	 */
	public void reset() {
		if (!boundsInitialized) {
			return;
		}
		scale = initScale;
		dx = 0;
		dy = 0;
		invalidate();
	}

	/**
	 * Zooms map in, preserving currently centered point at the center of the
	 * view.
	 */
	public void scaleOut() {
		scale(1 / scaleFactor);
	}

	/**
	 * Zooms map out, preserving currently centered point at the center of the
	 * view.
	 */
	public void scaleIn() {
		scale(scaleFactor);
	}

	protected void scale(double scaleFactor) {
		float prevDx = dx + (imageWidth * scale - viewWidth) / 2;
		prevDx *= scaleFactor;
		float prevDy = dy + (imageHeight * scale - viewHeight) / 2;
		prevDy *= scaleFactor;

		scale *= scaleFactor;
		
		if(scale < initScale){
			scale = initScale;
		}
		
		dx = prevDx - (imageWidth * scale - viewWidth) / 2;
		dy = prevDy - (imageHeight * scale - viewHeight) / 2;
		invalidate();
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		dx -= distanceX;
		dy -= distanceY;
		invalidate();
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
	  
		if(scale < 0.7){
			dx += viewWidth/2 - e.getX();
			dy += viewHeight/2 - e.getY();
			scaleIn();
			return true;
		}
		return false;
	}

	double prevDelta = 0;
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		/*if (event.getPointerCount() == 1) {
			gestureDetector.onTouchEvent(event);
		} else {*/
			float x = event.getX();
			float y = event.getY();
			float prevX = event.getX();
			float prevY = event.getY();

			int action = event.getAction();
			int actionCode = action & 255;
			if (actionCode == MotionEvent.ACTION_DOWN
					|| actionCode == 5){
				prevDelta = 0;
				dx += viewWidth/2 - (x + prevX) / 2;
				dy += viewHeight/2 - (y + prevY) / 2;
			} else {
				double currentDelta = getDelta(x, y, prevX, prevY);
				if (prevDelta != 0) {
					double dd = currentDelta - prevDelta;
					float abs = (float) Math.abs(dd);
					if (abs > 2) {
						double scaleFactor = scale * (1 + (float) dd / 200);
						scaleFactor = scaleFactor / scale;
						scale(scaleFactor);
					}
				}
				prevDelta = currentDelta;
			}
			invalidate();
		//}
		return true;
	}

	protected double getDelta(float x, float y, float prevX, float prevY) {
		return Math.sqrt((x - prevX) * (x- prevX) + (y - prevY) * (y - prevY));
	}

}