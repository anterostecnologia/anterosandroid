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

package br.com.anteros.android.ui.controls.pageCurl.canvas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import br.com.anteros.android.ui.controls.R;
import br.com.anteros.android.ui.controls.image.mapper.Area;
import br.com.anteros.android.ui.controls.pageCurl.PageCurlException;
import br.com.anteros.android.ui.controls.pageCurl.canvas.PageCurlBitmapProvider.BitmapProviderListener;
import br.com.anteros.android.ui.controls.ErrorAlert;

/**
 * 
 * @author Moritz 'Moss' Wundke (b.thax.dcg@gmail.com)
 * @author Eduardo Albertini
 * @author Edson Martins
 *
 */
public class PageCurlView extends View implements BitmapProviderListener {

	private static final int LONG_PRESS_TIME = 500;

	private Paint mTextPaint;
	private TextPaint mTextPaintShadow;

	/** Velocidade do efeito de dobra */
	private int mCurlSpeed;

	/** Tempo de atualização fixo usado para criar uma animação suave de dobra */
	private int mUpdateRate;

	/** O deslocamento de movimentos dos eixos X e Y inicial */
	private int mInitialEdgeOffset;

	/** O modo de dobra usado */
	private int mCurlMode;

	/** Modo Simples. A dobra ocorrerá em apenas um eixo */
	public static final int CURLMODE_SIMPLE = 0;

	/** Modo dinâmico. A dobra ocorrerá tanto no eixo X quanto no eixo Y */
	public static final int CURLMODE_DYNAMIC = 1;

	/** Habilita/desabilita o modo debug */
	private boolean bEnableDebugMode = false;

	/** Handler usado na hora do auto flip */
	private FlipAnimationHandler mAnimationHandler;

	/**
	 * Raio máximo que a página pode ser invertida, por padrão é a largura da
	 * view
	 */
	private float mFlipRadius;

	/** Ponto usado para mover */
	private Vector2D mMovement;

	/** Ponto que houve o toque */
	private Vector2D mFinger;

	/** Ponto do movimento do ultimo frame */
	private Vector2D mOldMovement;

	/** Borda da dobra */
	private Paint mCurlEdgePaint;

	/**
	 * Pontos usados para definir os pontos de recorte atuais na chamado do
	 * draw()
	 */
	private Vector2D mA, mB, mC, mD, mE, mF, mOldF, mOrigin;

	/** Se <code>false</code> nenhuma chamada para draw() é feita */
	private boolean bViewDrawn;

	/** Define a direção do flip considerado atualmente */
	private boolean bFlipRight;

	/** Se <code>true</code> está auto-flipping atualmente */
	private boolean bFlipping;

	/** Se <code>true</code> o usuário mudou a página */
	private boolean bUserMoves;

	/** Usado para conrtolar o bloqueio do toque */
	private boolean bBlockTouchInput = false;

	/** Habilita a entrada de toques após o evento de draw() */
	private boolean bEnableInputAfterDraw = false;

	/** Imagem que está sendo exibita atualmente */
	private Bitmap mForeground;

	/** Próxima imagem a ser exibida */
	private Bitmap mBackground;

	/** Lista de páginas do catálogo */
	private List<PageCurl> mPages = new ArrayList<PageCurl>();

	/** Indice da página atual */
	private int mIndex = 0;

	/** Listener para os eventos */
	private PageCurlListener mPageListener;

	/** Permite visualizar as toas as areas */
	private boolean bAutoPreviewAreas;

	/** Permite selecionar varias áreas */
	private boolean bAllowMultiselect = true;

	/** Tamanho da faixa lateral para mudança das páginas */
	private int mPageTouchSlop = 50;

	/** Verifica se o toque foi em uma área */
	private boolean bTouchInArea = false;

	/** Verifica se houve um long touch */
	private boolean bLongTouch = false;

	/** Handler para verificar se houve long touch */
	private final Handler mLongTouchHandler = new Handler();

	/** Thread para disparar evento de long touch */
	private Runnable mLongTouchThread = new Runnable() {
		public void run() {
			bLongTouch = true;
			if (bTouchInArea && !getCurrentPage().getCurrentAreas().isEmpty()) {
				if (mPageListener != null)
					mPageListener.onLongTouchArea(getCurrentPage().getCurrentAreas(), getCurrentPage());
			}
		}
	};

	private PageCurlBitmapProvider mBitmapProvider;
	private int finalWidth, finalHeight;

	public static final String AGRUPAMENTO = "AGRUPAMENTO";
	public static final String ID_CATALOGO_MAPFIGURA = "ID_CATALOGO_MAPFIGURA";

	/** Classe interna para representar ponto 2D */
	private class Vector2D {
		public float x, y;

		public Vector2D(float x, float y) {
			this.x = x;
			this.y = y;
		}

		@Override
		public String toString() {
			return "(" + this.x + "," + this.y + ")";
		}

		@SuppressWarnings(value = { "unused" })
		public float length() {
			return (float) Math.sqrt(x * x + y * y);
		}

		@SuppressWarnings(value = { "unused" })
		public float lengthSquared() {
			return (x * x) + (y * y);
		}

		public boolean equals(Object o) {
			if (o instanceof Vector2D) {
				Vector2D p = (Vector2D) o;
				return p.x == x && p.y == y;
			}
			return false;
		}

		@SuppressWarnings(value = { "unused" })
		public Vector2D reverse() {
			return new Vector2D(-x, -y);
		}

		public Vector2D sum(Vector2D b) {
			return new Vector2D(x + b.x, y + b.y);
		}

		public Vector2D sub(Vector2D b) {
			return new Vector2D(x - b.x, y - b.y);
		}

		@SuppressWarnings(value = { "unused" })
		public float dot(Vector2D vec) {
			return (x * vec.x) + (y * vec.y);
		}

		@SuppressWarnings(value = { "unused" })
		public float cross(Vector2D a, Vector2D b) {
			return a.cross(b);
		}

		public float cross(Vector2D vec) {
			return x * vec.y - y * vec.x;
		}

		public float distanceSquared(Vector2D other) {
			float dx = other.x - x;
			float dy = other.y - y;

			return (dx * dx) + (dy * dy);
		}

		public float distance(Vector2D other) {
			return (float) Math.sqrt(distanceSquared(other));
		}

		public float dotProduct(Vector2D other) {
			return other.x * x + other.y * y;
		}

		public Vector2D normalize() {
			float magnitude = (float) Math.sqrt(dotProduct(this));
			return new Vector2D(x / magnitude, y / magnitude);
		}

		public Vector2D mult(float scalar) {
			return new Vector2D(x * scalar, y * scalar);
		}
	}

	/**
	 * Inner class used to make a fixed timed animation of the curl effect.
	 */
	class FlipAnimationHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			PageCurlView.this.flipAnimationStep();
		}

		public void sleep(long millis) {
			this.removeMessages(0);
			sendMessageDelayed(obtainMessage(0), millis);
		}
	}

	/**
	 * Base
	 * 
	 * @param context
	 */
	public PageCurlView(Context context) {
		super(context);
		init(context);
		resetClipEdge();
	}

	/**
	 * Construct the object from an XML file. Valid Attributes:
	 * 
	 * @see View#View(Context,
	 *      AttributeSet)
	 */
	public PageCurlView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);

		// Get the data from the XML AttributeSet
		{
			TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PageCurlView);

			// Get data
			bEnableDebugMode = a.getBoolean(R.styleable.PageCurlView_enableDebugMode, bEnableDebugMode);
			mCurlSpeed = a.getInt(R.styleable.PageCurlView_curlSpeed, mCurlSpeed);
			mUpdateRate = a.getInt(R.styleable.PageCurlView_updateRate, mUpdateRate);
			mInitialEdgeOffset = a.getInt(R.styleable.PageCurlView_initialEdgeOffset, mInitialEdgeOffset);
			mCurlMode = a.getInt(R.styleable.PageCurlView_curlMode, mCurlMode);
			bAllowMultiselect = a.getBoolean(R.styleable.PageCurlView_allowMultiselect, true);

			// recycle object (so it can be used by others)
			a.recycle();
		}

		resetClipEdge();
	}

	/**
	 * Initialize the view
	 */
	private final void init(Context context) {
		// Foreground text paint
		mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mTextPaint.setAntiAlias(true);
		mTextPaint.setTextSize(16);
		mTextPaint.setColor(0xFF000000);

		// The shadow
		mTextPaintShadow = new TextPaint();
		mTextPaintShadow.setAntiAlias(true);
		mTextPaintShadow.setTextSize(16);
		mTextPaintShadow.setColor(0x00000000);

		// Base padding
		setPadding(3, 3, 3, 3);

		// The focus flags are needed
		setFocusable(true);
		setFocusableInTouchMode(true);

		mMovement = new Vector2D(0, 0);
		mFinger = new Vector2D(0, 0);
		mOldMovement = new Vector2D(0, 0);

		// Create our curl animation handler
		mAnimationHandler = new FlipAnimationHandler();

		// Create our edge paint
		mCurlEdgePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mCurlEdgePaint.setColor(Color.WHITE);
		mCurlEdgePaint.setAntiAlias(true);
		mCurlEdgePaint.setStyle(Style.FILL);
		mCurlEdgePaint.setShadowLayer(10, -5, 5, 0x99000000);

		// Set the default props, those come from an XML :D
		mCurlSpeed = 200;
		mUpdateRate = 10;
		mInitialEdgeOffset = 20;
		mCurlMode = 1;

		mBitmapProvider = new PageCurlBitmapProvider(this);
	}

	/**
	 * Reset points to it's initial clip edge state
	 */
	public void resetClipEdge() {
		// Set our base movement
		mMovement.x = mInitialEdgeOffset;
		mMovement.y = mInitialEdgeOffset;
		mOldMovement.x = 0;
		mOldMovement.y = 0;

		// Now set the points
		mA = new Vector2D(mInitialEdgeOffset, 0);
		mB = new Vector2D(this.getWidth(), this.getHeight());
		mC = new Vector2D(this.getWidth(), 0);
		mD = new Vector2D(0, 0);
		mE = new Vector2D(0, 0);
		mF = new Vector2D(0, 0);
		mOldF = new Vector2D(0, 0);

		// The movement origin point
		mOrigin = new Vector2D(this.getWidth(), 0);
	}

	/**
	 * See if the current curl mode is dynamic
	 * 
	 * @return TRUE if the mode is CURLMODE_DYNAMIC, FALSE otherwise
	 */
	public boolean isCurlModeDynamic() {
		return mCurlMode == CURLMODE_DYNAMIC;
	}

	/**
	 * Set the curl speed.
	 * 
	 * @param curlSpeed
	 *            - New speed in px/frame
	 * @throws IllegalArgumentException
	 *             if curlspeed < 1
	 */
	public void setCurlSpeed(int curlSpeed) {
		if (curlSpeed < 1)
			throw new IllegalArgumentException("curlSpeed deve ser maior que zero!");
		mCurlSpeed = curlSpeed;
	}

	/**
	 * Get the current curl speed
	 * 
	 * @return int - Curl speed in px/frame
	 */
	public int getCurlSpeed() {
		return mCurlSpeed;
	}

	/**
	 * Set the update rate for the curl animation
	 * 
	 * @param updateRate
	 *            - Fixed animation update rate in fps
	 * @throws IllegalArgumentException
	 *             if updateRate < 1
	 */
	public void setUpdateRate(int updateRate) {
		if (updateRate < 1)
			throw new IllegalArgumentException("updateRate deve ser maior que zero!");
		mUpdateRate = updateRate;
	}

	/**
	 * Get the current animation update rate
	 * 
	 * @return int - Fixed animation update rate in fps
	 */
	public int getUpdateRate() {
		return mUpdateRate;
	}

	/**
	 * Set the initial pixel offset for the curl edge o
	 * 
	 * @param initialEdgeOffset
	 *            - px offset for curl edge
	 * @throws IllegalArgumentException
	 *             if initialEdgeOffset < 0
	 */
	public void setInitialEdgeOffset(int initialEdgeOffset) {
		if (initialEdgeOffset < 0)
			throw new IllegalArgumentException("initialEdgeOffset não pode ser negativo!");
		mInitialEdgeOffset = initialEdgeOffset;
	}

	/**
	 * Get the initial pixel offset for the curl edge
	 * 
	 * @return int - px
	 */
	public int getInitialEdgeOffset() {
		return mInitialEdgeOffset;
	}

	/**
	 * Set the curl mode.
	 * <p>
	 * Can be one of the following values:
	 * </p>
	 * <table>
	 * <colgroup align="left" /> <colgroup align="left" />
	 * <tr>
	 * <th>Value</th>
	 * <th>Description</th>
	 * </tr>
	 * <tr>
	 * <td>
	 * <code>{@link #CURLMODE_SIMPLE com.dcg.pagecurl:CURLMODE_SIMPLE}</code></td>
	 * <td>Curl target will move only in one axis.</td>
	 * </tr>
	 * <tr>
	 * <td>
	 * <code>{@link #CURLMODE_DYNAMIC com.dcg.pagecurl:CURLMODE_DYNAMIC}</code></td>
	 * <td>Curl target will move on both X and Y axis.</td>
	 * </tr>
	 * </table>
	 * 
	 * @see #CURLMODE_SIMPLE
	 * @see #CURLMODE_DYNAMIC
	 * @param curlMode
	 * @throws IllegalArgumentException
	 *             if curlMode is invalid
	 */
	public void setCurlMode(int curlMode) {
		if (curlMode != CURLMODE_SIMPLE &&
				curlMode != CURLMODE_DYNAMIC)
			throw new IllegalArgumentException("curlMode inválido!");
		mCurlMode = curlMode;
	}

	/**
	 * Return an integer that represents the current curl mode.
	 * <p>
	 * Can be one of the following values:
	 * </p>
	 * <table>
	 * <colgroup align="left" /> <colgroup align="left" />
	 * <tr>
	 * <th>Value</th>
	 * <th>Description</th>
	 * </tr>
	 * <tr>
	 * <td>
	 * <code>{@link #CURLMODE_SIMPLE com.dcg.pagecurl:CURLMODE_SIMPLE}</code></td>
	 * <td>Curl target will move only in one axis.</td>
	 * </tr>
	 * <tr>
	 * <td>
	 * <code>{@link #CURLMODE_DYNAMIC com.dcg.pagecurl:CURLMODE_DYNAMIC}</code></td>
	 * <td>Curl target will move on both X and Y axis.</td>
	 * </tr>
	 * </table>
	 * 
	 * @see #CURLMODE_SIMPLE
	 * @see #CURLMODE_DYNAMIC
	 * @return int - current curl mode
	 */
	public int getCurlMode() {
		return mCurlMode;
	}

	/**
	 * Enable debug mode. This will draw a lot of data in the view so you can
	 * track what is happening
	 * 
	 * @param bFlag
	 *            - boolean flag
	 */
	public void setEnableDebugMode(boolean bFlag) {
		bEnableDebugMode = bFlag;
	}

	/**
	 * Check if we are currently in debug mode.
	 * 
	 * @return boolean - If TRUE debug mode is on, FALSE otherwise.
	 */
	public boolean isDebugModeEnabled() {
		return bEnableDebugMode;
	}

	/**
	 * @see View#measure(int, int)
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		final int w = getMeasuredWidth();
		final int h = getMeasuredHeight();
		if (finalWidth != w || finalHeight != h) {
			finalWidth = w;
			finalHeight = h;
			mBitmapProvider.clearCache();
			goToPage(mIndex);
		}
	}

	/**
	 * Determines the width of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The width of the view, honoring constraints from measureSpec
	 */
	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text
			result = specSize;
		}

		return result;
	}

	/**
	 * Determines the height of this view
	 * 
	 * @param measureSpec
	 *            A measureSpec packed into an int
	 * @return The height of the view, honoring constraints from measureSpec
	 */
	private int measureHeight(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else {
			// Measure the text (beware: ascent is a negative number)
			result = specSize;
		}
		return result;
	}

	// ---------------------------------------------------------------
	// Curling. This handles touch events, the actual curling
	// implementations and so on.
	// ---------------------------------------------------------------

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (!bBlockTouchInput && hasPages()) {

			// Get our finger position
			mFinger.x = event.getX();
			mFinger.y = event.getY();
			int width = getWidth();

			bTouchInArea = false;
			PageCurl pageCurl = getCurrentPage();

			for (Area a : pageCurl.getAreas()) {
				if (a.isInArea(event.getX(), event.getY())) {
					bTouchInArea = true;
					break;
				}
			}
			if (!bTouchInArea && !inMovePageArea(mFinger.x, mFinger.y)) {
				pageCurl.clearSelection();
				bLongTouch = false;
				invalidate();
			}

			if (!bTouchInArea || bUserMoves) {
				// Depending on the action do what we need to
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (!inMovePageArea(mFinger.x, mFinger.y) || bUserMoves)
						return false;

					bUserMoves = true;

					mOldMovement.x = mFinger.x;
					mOldMovement.y = mFinger.y;

					// If we moved over the half of the display flip to next
					if (mOldMovement.x > (width >> 1)) {
						mMovement.x = mInitialEdgeOffset;
						mMovement.y = mInitialEdgeOffset;

						// Set the right movement flag
						bFlipRight = true;

					} else {
						// Set the left movement flag
						bFlipRight = false;

						// go to next previous page
						previousView();

						// Set new movement
						mMovement.x = isCurlModeDynamic() ? width << 1 : width;
						mMovement.y = mInitialEdgeOffset;
					}

					break;
				case MotionEvent.ACTION_UP:
					if (bUserMoves) {
						bFlipping = true;
						flipAnimationStep();
					}
					bUserMoves = false;

					break;
				case MotionEvent.ACTION_MOVE:
					if (!bUserMoves)
						return false;

					if (Math.abs(mOldMovement.x - mFinger.x) > 6) {
						bUserMoves = true;

						// Get movement
						mMovement.x -= mFinger.x - mOldMovement.x;
						mMovement.y -= mFinger.y - mOldMovement.y;
						mMovement = capMovement(mMovement, true);

						// Make sure the y value get's locked at a nice level
						if (mMovement.y <= 1)
							mMovement.y = 1;

						// Get movement direction

						if (mFinger.x < mOldMovement.x) {
							bFlipRight = true;
						} else {
							bFlipRight = false;
						}

						// Save old movement values
						mOldMovement.x = mFinger.x;
						mOldMovement.y = mFinger.y;

						// Force a new draw call
						doPageCurl();
						this.invalidate();
					}
				}
			} else {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					mLongTouchHandler.postDelayed(mLongTouchThread, LONG_PRESS_TIME);
					break;
				case MotionEvent.ACTION_MOVE:
					// Verifica se o movimento é menor que 2, caso o toque
					// esteja tremendo
					if ((Math.abs(mFinger.x - event.getX()) > 2) || (Math.abs(mFinger.y - event.getY()) > 2)) {
						mLongTouchHandler.removeCallbacks(mLongTouchThread);
					}
					break;
				case MotionEvent.ACTION_UP:
					mLongTouchHandler.removeCallbacks(mLongTouchThread);
					for (Area a : pageCurl.getAreas()) {
						if (a.isInArea(event.getX(), event.getY())) {

							if (!bAllowMultiselect)
								pageCurl.clearSelection();

							if (pageCurl.isSelectedArea(a)) {

								if (bLongTouch) {
									bLongTouch = false;
								} else {
									pageCurl.unSelectArea(a);
									pageCurl.unSelectAreas(getAreasAgrupadas(pageCurl, a));
								}
							} else if (!pageCurl.isSelectedArea(a)) {
								pageCurl.selectArea(a);
								pageCurl.selectAreas(getAreasAgrupadas(pageCurl, a));
								bLongTouch = false;
							}

							if (mPageListener != null) {
								mPageListener.onTouchArea(a, pageCurl);
							}

							invalidate();
							break;
						}
					}
					break;
				}
			}
		}
		return true;
	}

	private List<Area> getAreasAgrupadas(PageCurl page, Area area) {
		List<Area> result = new ArrayList<Area>();
		String grupo = area.getValue(AGRUPAMENTO);
		if (grupo != null) {
			for (Area a : page.getAreas()) {
				if (!area.equals(a) && grupo.equals(a.getValue(AGRUPAMENTO))) {
					result.add(a);
				}
			}
		}

		return result;
	}

	private boolean inMovePageArea(float x, float y) {
		return (x <= mPageTouchSlop) || (x >= (getMeasuredWidth() - mPageTouchSlop));
	}

	private boolean hasPages() {
		return mPages != null && !mPages.isEmpty();
	}

	/**
	 * Make sure we never move too much, and make sure that if we move too much
	 * to add a displacement so that the movement will be still in our radius.
	 * 
	 * @param radius
	 *            - radius form the flip origin
	 * @param bMaintainMoveDir
	 *            - Cap movement but do not change the current movement
	 *            direction
	 * @return Corrected point
	 */
	private Vector2D capMovement(Vector2D point, boolean bMaintainMoveDir) {
		// Make sure we never ever move too much
		if (point.distance(mOrigin) > mFlipRadius) {
			if (bMaintainMoveDir) {
				// Maintain the direction
				point = mOrigin.sum(point.sub(mOrigin).normalize().mult(mFlipRadius));
			} else {
				// Change direction
				if (point.x > (mOrigin.x + mFlipRadius))
					point.x = (mOrigin.x + mFlipRadius);
				else if (point.x < (mOrigin.x - mFlipRadius))
					point.x = (mOrigin.x - mFlipRadius);
				point.y = (float) (Math.sin(Math.acos(Math.abs(point.x - mOrigin.x) / mFlipRadius)) * mFlipRadius);
			}
		}
		return point;
	}

	/**
	 * Execute a step of the flip animation
	 */
	public void flipAnimationStep() {
		if (!bFlipping)
			return;

		int width = getWidth();

		// No input when flipping
		bBlockTouchInput = true;

		// Handle speed
		float curlSpeed = mCurlSpeed;
		if (!bFlipRight)
			curlSpeed *= -1;

		// Move us
		mMovement.x += curlSpeed;
		mMovement = capMovement(mMovement, false);

		// Create values
		doPageCurl();

		// Check for endings :D
		if (mA.x < 1 || mA.x > width - 1) {
			bFlipping = false;
			if (bFlipRight) {
				// SwapViews();
				nextView();
			}
			resetClipEdge();

			// Create values
			doPageCurl();

			// Enable touch input after the next draw event
			bEnableInputAfterDraw = true;
		} else {
			mAnimationHandler.sleep(mUpdateRate);
		}

		// Force a new draw call
		invalidate();
	}

	/**
	 * Do the page curl depending on the methods we are using
	 */
	private void doPageCurl() {
		if (bFlipping) {
			if (isCurlModeDynamic())
				doDynamicCurl();
			else
				doSimpleCurl();

		} else {
			if (isCurlModeDynamic())
				doDynamicCurl();
			else
				doSimpleCurl();
		}
	}

	/**
	 * Do a simple page curl effect
	 */
	private void doSimpleCurl() {
		int width = getWidth();
		int height = getHeight();

		// Calculate point A
		mA.x = width - mMovement.x;
		mA.y = height;

		// Calculate point D
		mD.x = 0;
		mD.y = 0;
		if (mA.x > width / 2) {
			mD.x = width;
			mD.y = height - (width - mA.x) * height / mA.x;
		} else {
			mD.x = 2 * mA.x;
			mD.y = 0;
		}

		// Now calculate E and F taking into account that the line
		// AD is perpendicular to FB and EC. B and C are fixed points.
		double angle = Math.atan((height - mD.y) / (mD.x + mMovement.x - width));
		double _cos = Math.cos(2 * angle);
		double _sin = Math.sin(2 * angle);

		// And get F
		mF.x = (float) (width - mMovement.x + _cos * mMovement.x);
		mF.y = (float) (height - _sin * mMovement.x);

		// If the x position of A is above half of the page we are still not
		// folding the upper-right edge and so E and D are equal.
		if (mA.x > width / 2) {
			mE.x = mD.x;
			mE.y = mD.y;
		}
		else {
			// So get E
			mE.x = (float) (mD.x + _cos * (width - mD.x));
			mE.y = (float) -(_sin * (width - mD.x));
		}
	}

	/**
	 * Calculate the dynamic effect, that one that follows the users finger
	 */
	private void doDynamicCurl() {
		int width = getWidth();
		int height = getHeight();

		// F will follow the finger, we add a small displacement
		// So that we can see the edge
		mF.x = width - mMovement.x + 0.1f;
		mF.y = height - mMovement.y + 0.1f;

		// Set min points
		if (mA.x == 0) {
			mF.x = Math.min(mF.x, mOldF.x);
			mF.y = Math.max(mF.y, mOldF.y);
		}

		// Get diffs
		float deltaX = width - mF.x;
		float deltaY = height - mF.y;

		float BH = (float) (Math.sqrt(deltaX * deltaX + deltaY * deltaY) / 2);
		double tangAlpha = deltaY / deltaX;
		double alpha = Math.atan(deltaY / deltaX);
		double _cos = Math.cos(alpha);
		double _sin = Math.sin(alpha);

		mA.x = (float) (width - (BH / _cos));
		mA.y = height;

		mD.y = (float) (height - (BH / _sin));
		mD.x = width;

		mA.x = Math.max(0, mA.x);
		if (mA.x == 0) {
			mOldF.x = mF.x;
			mOldF.y = mF.y;
		}

		// Get W
		mE.x = mD.x;
		mE.y = mD.y;

		// Correct
		if (mD.y < 0) {
			mD.x = width + (float) (tangAlpha * mD.y);
			mE.y = 0;
			mE.x = width + (float) (Math.tan(2 * alpha) * mD.y);
		}
	}

	/**
	 * Swap to next view
	 */
	private void nextView() {
		int foreIndex = mIndex + 1;
		if (foreIndex >= mPages.size()) {
			foreIndex = 0;
		}
		int backIndex = foreIndex + 1;
		if (backIndex >= mPages.size()) {
			backIndex = 0;
		}
		mIndex = foreIndex;
		setViews(foreIndex, backIndex);
	}

	/**
	 * Swap to previous view
	 */
	private void previousView() {
		int backIndex = mIndex;
		int foreIndex = backIndex - 1;
		if (foreIndex < 0) {
			foreIndex = mPages.size() - 1;
		}
		mIndex = foreIndex;
		setViews(foreIndex, backIndex);
	}

	/**
	 * Set current fore and background
	 * 
	 * @param foreground
	 *            - Foreground view index
	 * @param background
	 *            - Background view index
	 */
	private void setViews(int foreground, int background) {
		try {
			mBitmapProvider.setSelectedPage(mPages.get(foreground));

			mForeground = mBitmapProvider.getBitmap(mPages.get(foreground));
			mBackground = mBitmapProvider.getBitmap(mPages.get(background));

			if (background != foreground) {
				mPages.get(background).getAreas().clear();
			}

			mPages.get(foreground).clearSelection();
			this.OnLoadBitmapFinish(mPages.get(mIndex));
			if (mPageListener != null)
				mPageListener.onPageChange(mPages.get(mIndex), mIndex);

		} catch (PageCurlException ex) {
			new ErrorAlert(getContext(), "Erro", ex.getMessage()).show();
		}
	}

	// ---------------------------------------------------------------
	// Drawing methods
	// ---------------------------------------------------------------

	@Override
	protected void onDraw(Canvas canvas) {

		if (hasPages() && finalWidth > 0 && finalHeight > 0) {
			// Translate the whole canvas
			// canvas.translate(mCurrentLeft, mCurrentTop);

			// We need to initialize all size data when we first draw the view
			if (!bViewDrawn) {
				bViewDrawn = true;
				onFirstDrawEvent(canvas);
			}

			canvas.drawColor(Color.WHITE);

			Rect rect = new Rect();
			rect.left = 0;
			rect.top = 0;
			rect.bottom = getHeight();
			rect.right = getWidth();

			// First Page render
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

			// Draw our elements
			if (hasPages()) {
				drawForeground(canvas, rect, paint);
				drawBackground(canvas, rect, paint);
				drawCurlEdge(canvas);

				if (!bFlipping)
					drawAreas(canvas);
			}

			// Draw any debug info once we are done
			if (bEnableDebugMode)
				drawDebug(canvas);

			// Check if we can re-enable input
			if (bEnableInputAfterDraw)
			{
				bBlockTouchInput = false;
				bEnableInputAfterDraw = false;
			}
		}
	}

	private void drawAreas(Canvas canvas) {
		for (Area a : getCurrentPage().getAreas()) {
			if (getCurrentPage().getCurrentAreas().contains(a)) {
				a.draw(canvas, false);
			} else if (bAutoPreviewAreas) {
				a.draw(canvas, 100, 175, 246, 175, false);
			}
		}
	}

	/**
	 * Called on the first draw event of the view
	 * 
	 * @param canvas
	 */
	protected void onFirstDrawEvent(Canvas canvas) {

		mFlipRadius = getWidth();

		resetClipEdge();
		doPageCurl();
	}

	/**
	 * Draw the foreground
	 * 
	 * @param canvas
	 * @param rect
	 * @param paint
	 */
	private void drawForeground(Canvas canvas, Rect rect, Paint paint) {
		if (mForeground == null) {
			return;
		}

		canvas.drawBitmap(mForeground, null, rect, paint);

		// Draw the page number (first page is 1 in real life :D
		// there is no page number 0 hehe)
		drawPageNum(canvas, mIndex);
	}

	/**
	 * Create a Path used as a mask to draw the background page
	 * 
	 * @return
	 */
	private Path createBackgroundPath() {
		Path path = new Path();
		path.moveTo(mA.x, mA.y);
		path.lineTo(mB.x, mB.y);
		path.lineTo(mC.x, mC.y);
		path.lineTo(mD.x, mD.y);
		path.lineTo(mA.x, mA.y);
		return path;
	}

	/**
	 * Draw the background image.
	 * 
	 * @param canvas
	 * @param rect
	 * @param paint
	 */
	private void drawBackground(Canvas canvas, Rect rect, Paint paint) {
		if (mBackground == null) {
			return;
		}

		Path mask = createBackgroundPath();

		// Save current canvas so we do not mess it up
		canvas.save();
		canvas.clipPath(mask);
		canvas.drawBitmap(mBackground, null, rect, paint);

		// Draw the page number (first page is 1 in real life :D
		// there is no page number 0 hehe)
		drawPageNum(canvas, mIndex);

		canvas.restore();
	}

	/**
	 * Creates a path used to draw the curl edge in.
	 * 
	 * @return
	 */
	private Path createCurlEdgePath() {
		Path path = new Path();
		path.moveTo(mA.x, mA.y);
		path.lineTo(mD.x, mD.y);
		path.lineTo(mE.x, mE.y);
		path.lineTo(mF.x, mF.y);
		path.lineTo(mA.x, mA.y);
		return path;
	}

	/**
	 * Draw the curl page edge
	 * 
	 * @param canvas
	 */
	private void drawCurlEdge(Canvas canvas) {
		Path path = createCurlEdgePath();
		canvas.drawPath(path, mCurlEdgePaint);
	}

	/**
	 * Draw page num (let this be a bit more custom)
	 * 
	 * @param canvas
	 * @param pageNum
	 */
	private void drawPageNum(Canvas canvas, int pageNum) {
		mTextPaint.setColor(Color.WHITE);
		String pageNumText = "- " + pageNum + " -";
		drawCentered(canvas, pageNumText, canvas.getHeight() - mTextPaint.getTextSize() - 5, mTextPaint,
				mTextPaintShadow);
	}

	// ---------------------------------------------------------------
	// Debug draw methods
	// ---------------------------------------------------------------

	/**
	 * Draw a text with a nice shadow
	 */
	public static void drawTextShadowed(Canvas canvas, String text, float x, float y, Paint textPain, Paint shadowPaint) {
		canvas.drawText(text, x - 1, y, shadowPaint);
		canvas.drawText(text, x, y + 1, shadowPaint);
		canvas.drawText(text, x + 1, y, shadowPaint);
		canvas.drawText(text, x, y - 1, shadowPaint);
		canvas.drawText(text, x, y, textPain);
	}

	/**
	 * Draw a text with a nice shadow centered in the X axis
	 * 
	 * @param canvas
	 * @param text
	 * @param y
	 * @param textPain
	 * @param shadowPaint
	 */
	public static void drawCentered(Canvas canvas, String text, float y, Paint textPain, Paint shadowPaint) {
		float posx = (canvas.getWidth() - textPain.measureText(text)) / 2;
		drawTextShadowed(canvas, text, posx, y, textPain, shadowPaint);
	}

	/**
	 * Draw debug info
	 * 
	 * @param canvas
	 */
	private void drawDebug(Canvas canvas) {
		float posX = 10;
		float posY = 20;

		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStrokeWidth(5);
		paint.setStyle(Style.STROKE);

		paint.setColor(Color.BLACK);
		canvas.drawCircle(mOrigin.x, mOrigin.y, getWidth(), paint);

		paint.setStrokeWidth(3);
		paint.setColor(Color.RED);
		canvas.drawCircle(mOrigin.x, mOrigin.y, getWidth(), paint);

		paint.setStrokeWidth(5);
		paint.setColor(Color.BLACK);
		canvas.drawLine(mOrigin.x, mOrigin.y, mMovement.x, mMovement.y, paint);

		paint.setStrokeWidth(3);
		paint.setColor(Color.RED);
		canvas.drawLine(mOrigin.x, mOrigin.y, mMovement.x, mMovement.y, paint);

		posY = debugDrawPoint(canvas, "A", mA, Color.RED, posX, posY);
		posY = debugDrawPoint(canvas, "B", mB, Color.GREEN, posX, posY);
		posY = debugDrawPoint(canvas, "C", mC, Color.BLUE, posX, posY);
		posY = debugDrawPoint(canvas, "D", mD, Color.CYAN, posX, posY);
		posY = debugDrawPoint(canvas, "E", mE, Color.YELLOW, posX, posY);
		posY = debugDrawPoint(canvas, "F", mF, Color.LTGRAY, posX, posY);
		posY = debugDrawPoint(canvas, "Mov", mMovement, Color.DKGRAY, posX, posY);
		posY = debugDrawPoint(canvas, "Origin", mOrigin, Color.MAGENTA, posX, posY);
		posY = debugDrawPoint(canvas, "Finger", mFinger, Color.GREEN, posX, posY);

		// Draw some curl stuff (Just some test)
		/*
		 * canvas.save(); Vector2D center = new
		 * Vector2D(getWidth()/2,getHeight()/2);
		 * //canvas.rotate(315,center.x,center.y);
		 * 
		 * // Test each lines //float radius = mA.distance(mD)/2.f; //float
		 * radius = mA.distance(mE)/2.f; float radius = mA.distance(mF)/2.f;
		 * //float radius = 10; float reduction = 4.f; RectF oval = new RectF();
		 * oval.top = center.y-radius/reduction; oval.bottom =
		 * center.y+radius/reduction; oval.left = center.x-radius; oval.right =
		 * center.x+radius; canvas.drawArc(oval, 0, 360, false, paint);
		 * canvas.restore(); /*
		 */
	}

	private float debugDrawPoint(Canvas canvas, String name, Vector2D point, int color, float posX, float posY) {
		return debugDrawPoint(canvas, name + " " + point.toString(), point.x, point.y, color, posX, posY);
	}

	private float debugDrawPoint(Canvas canvas, String name, float X, float Y, int color, float posX, float posY) {
		mTextPaint.setColor(color);
		drawTextShadowed(canvas, name, posX, posY, mTextPaint, mTextPaintShadow);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setStrokeWidth(5);
		paint.setColor(color);
		canvas.drawPoint(X, Y, paint);
		return posY + 15;
	}

	public void setPageCurlListener(PageCurlListener listener) {
		this.mPageListener = listener;
	}

	public void setAutoPreviewAreas(boolean autoPreviewAreas) {
		this.bAutoPreviewAreas = autoPreviewAreas;
	}

	protected void addPage(PageCurl page) {
		if (page == null)
			return;

		mPages.add(page);

		if (mPageListener != null)
			mPageListener.afterAddPage(page);
	}

	public void addPages(List<PageCurl> pages) {
		if (pages == null || pages.size() == 0)
			return;

		for (PageCurl page : pages) {
			page.applyScale(getMeasuredWidth(), getMeasuredHeight());
			addPage(page);
		}
		mBitmapProvider.initialize(getFirstPage());
		setViews(0, (mPages.size() > 1 ? 1 : 0));
		invalidate();

	}

	public void addPages(PageCurl... pages) {
		for (PageCurl page : pages) {
			addPage(page);
		}
		mBitmapProvider.initialize(getFirstPage());
		setViews(0, (mPages.size() > 1 ? 1 : 0));
		invalidate();
	}

	public void clearPages() {
		mPages.clear();
	}

	public void removePage(PageCurl page) {
		mPages.remove(page);
	}

	public List<PageCurl> getPages() {
		return Collections.unmodifiableList(mPages);
	}

	public PageCurl getCurrentPage() {
		return mPages.get(mIndex);
	}

	public int getPageCount() {
		return mPages.size();
	}

	public void goToPage(int position) {
		if (position < 0 || position > mPages.size() - 1)
			return;

		mIndex = position;

		int back = position + 1;
		if (back >= mPages.size())
			back = 0;

		if (mPageListener != null) {
			mPageListener.onPageChange(getCurrentPage(), position);
		}

		setViews(position, back);
		flipAnimationStep();
	}

	public void goToNextPage() {
		goToPage(mIndex + 1);
	}

	public void goToPreviousPage() {
		goToPage(mIndex - 1);
	}

	public void goToFirstPage() {
		goToPage(0);
	}

	public void goToLastPage() {
		goToPage(mPages.size() - 1);
	}

	public PageCurl getNextPage(PageCurl page) {
		int currentIndex = mPages.indexOf(page);

		if (currentIndex + 1 > mPages.size() - 1) {
			return null;
		}
		return mPages.get(currentIndex + 1);
	}

	public PageCurl getPreviousPage(PageCurl page) {
		int currentIndex = mPages.indexOf(page);

		if (currentIndex - 1 < 0) {
			return null;
		}
		return mPages.get(currentIndex - 1);
	}

	public PageCurl getFirstPage() {
		if (mPages.size() == 0) {
			return null;
		}
		return mPages.get(0);
	}

	public PageCurl getLastPage() {
		if (mPages.size() == 0) {
			return null;
		}
		return mPages.get(mPages.size() - 1);
	}

	@Override
	public void OnLoadBitmapFinish(PageCurl page) {
		if (mPageListener != null) {
			mPageListener.afterLoadBitmap(page);
			page.applyScale(getMeasuredWidth(), getMeasuredHeight());
			invalidate();
		}
	}

	public int getFinalWidth() {
		return finalWidth;
	}

	public int getFinalHeight() {
		return finalHeight;
	}

}
