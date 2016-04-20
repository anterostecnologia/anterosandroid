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

import java.text.DecimalFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import br.com.anteros.android.core.util.CanvasUtils;
import br.com.anteros.android.core.util.DrawProperties;
import br.com.anteros.android.ui.controls.R;
import br.com.anteros.core.log.Logger;
import br.com.anteros.core.log.LoggerProvider;
import br.com.anteros.core.utils.DateUtil;

public class DataGrid extends View implements OnGestureListener,
		OnDoubleTapListener {

	private static Logger LOG = LoggerProvider.getInstance().getLogger(DataGrid.class.getName());

	private ScrollBar scrollBarVertical, scrollBarHorizontal;

	float scrollX, scrollY;

	Paint paint = new Paint();

	BitmapDrawable downArrow, upArrow, leftArrow, rightArrow, checkBoxYes,
			sortAscend, sortDescend, checkBoxNo;
	int widthArrow, heightArrow, widthCheckBox, heightCheckBox, widthSort,
			heightSort;

	int defaultScrollBarSize = 8;

	static final int minSizeBar = 15;

	boolean drawGrid = true;

	private Handler myHandler = new Handler();

	private DataGridListener listener;

	private int fieldColor = Color.WHITE;
	private int fieldFontColor = Color.BLACK;
	private int fieldFontSize = 17;
	private Typeface fieldFontType = Typeface.DEFAULT;

	private int fixedFieldColor = Color.rgb(180, 180, 180);
	private int fixedFieldFontColor = Color.BLACK;
	private int fixedFieldFontSize = 17;
	private Typeface fixedFieldFontType = Typeface.DEFAULT;

	private int selectedFieldColor = Color.BLUE;
	private int selectedFieldFontColor = Color.WHITE;
	private int selectedFieldFontSize = 17;
	private Typeface selectedFieldFontType = Typeface.DEFAULT;

	private int sortFieldColor = Color.rgb(197, 216, 253);
	private int sortFieldFontColor = Color.rgb(3, 40, 115);
	private int sortFieldFontSize = 17;
	private Typeface sortFieldFontType = Typeface.DEFAULT;

	/*
	 * Largura da Grid
	 */
	int withDataGrid = 0;
	/*
	 * Altura da Grid
	 */
	int heightDataGrid = 0;

	/**************************************************************************************************************
	 * CabeÃ§alho da Grid
	 *************************************************************************************************************/
	/*
	 * Largura do CabeÃ§alho Fixo
	 */
	int widthFixedHeader;

	/*
	 * Altura do CabeÃ§alho Fixo
	 */
	int heightFixedHeader;

	/*
	 * Ponto X Esquerda do CabeÃ§alho Fixo
	 */
	int leftFixedHeaderX;

	/*
	 * Ponto Y Esquerda do CabeÃ§alho Fixo
	 */
	int leftFixedHeaderY;

	/*
	 * Largura do CabeÃ§alho = Largura da Grid - Largura do CabeÃ§alho Fixo
	 */
	int widthHeader;

	/*
	 * Altura do CabeÃ§alho = Altura padrÃ£o de uma cÃ©lula da grid
	 */
	int heightHeader;

	/*
	 * Ponto X Esquerda do CabeÃ§alho
	 */
	int leftHeaderX;

	/*
	 * Ponto Y Esquerda do CabeÃ§alho
	 */
	int leftHeaderY;

	/*************************************************************************************************************
	 * Ã�rea de Dados da Grid
	 ************************************************************************************************************/
	/*
	 * Largura da Ã�rea Fixa = Largura do CabeÃ§alho Fixo
	 */
	int widthFixedArea;

	/*
	 * Altura da Ã�rea Fixa = Altura da Grid - Altura padrÃ£o de uma cÃ©lula da
	 * grid
	 */
	int heightFixedArea;

	/*
	 * Ponto X Esquerda da Ã�rea Fixa
	 */
	int leftFixedAreaX;

	/*
	 * Ponto Y Esquerda da Ã�rea Fixa
	 */
	int leftFixedAreaY;

	/*
	 * Largura da Ã�rea Principal
	 */
	int widthMainArea;

	/*
	 * Altura da Ã�rea Principal
	 */
	int heightMainArea;

	/*
	 * Ponto X Esquerda da Ã�rea Principal
	 */
	int leftMainAreaX;

	/*
	 * Ponto Y Esquerda da Ã�rea Principal
	 */
	int leftMainAreaY;

	/*
	 * Ponto X da PosiÃ§Ã£o Atual da Ã�rea Principal
	 */
	int deltaMainAreaX;

	/*
	 * Ponto Y da PosiÃ§Ã£o Atual da Ã�rea Principal
	 */
	int deltaMainAreaY;

	/*
	 * Altura da Ã�rea de Dados Principal
	 */
	int heightMainAreaData;

	/*
	 * Largura da Ã�rea de Dados Principal
	 */
	int widthMainAreaData;

	/*************************************************************************************************************
	 * Barra de Scroll HORIZONTAL
	 ************************************************************************************************************/
	/*
	 * Largura da Barra de Scroll Horizontal
	 */
	int widthHorizontalScrollBar;
	/*
	 * Altura da Barra de Scroll Horizontal
	 */
	int heightHorizontalScrollBar;

	/*
	 * Ponto X Esquerda da Barra de Scroll Horizontal
	 */
	int leftHorizontalScrollBarX;
	/*
	 * Ponto Y Esquerda da Barra de Scroll Horizontal
	 */
	int leftHorizontalScrollBarY;

	/*
	 * PosiÃ§Ã£o Esquerda do Inicio da Barra de Scroll Horizontal
	 */
	int leftStartHorizontalBar;

	/*
	 * Largura da Ã�rea de MovimentaÃ§Ã£o da Barra Scroll Horizontal
	 */
	int widthHorizontalBarWorkArea;

	/*
	 * Largura da Barra de Scroll Horizontal
	 */
	int widthHorizontalBar;

	/*
	 * PosiÃ§Ã£o Atual a Esquerda da Barra de Scroll Horizontal
	 */
	int currentLeftHorizontalBar;

	/*
	 * Flag que indica se existe uma Barra de Scroll Horizontal
	 */
	boolean noHorizontalBar, sNoHorizontalBar = false;

	/*************************************************************************************************************
	 * Barra de Scroll VERTICAL
	 ************************************************************************************************************/
	/*
	 * Largura da Barra de Scroll Vertical
	 */
	int widthVerticalScrollBar;
	/*
	 * Altura da Barra de Scroll Horizontal
	 */
	int heightVerticalScrollBar;

	/*
	 * PosiÃ§Ã£o no Topo do Inicio da Barra de Scroll Vertical
	 */
	int topStartVerticalBar;
	/*
	 * Altura da Ã�rea de MovimentaÃ§Ã£o da Barra Scroll Vertical
	 */
	int heightVerticalBarWorkArea;
	/*
	 * Altura da Barra de Scroll Vertical
	 */
	int heightVerticalBar;

	/*
	 * PosiÃ§Ã£o Atual no Topo da Barra de Scroll Vertical
	 */
	int currentTopVerticalBar;

	/*
	 * Ponto X Esquerda da Barra de Scroll Vertical
	 */
	int leftVerticalScrollBarX;
	/*
	 * Ponto Y Esquerda da Barra de Scroll Vertical
	 */
	int leftVerticalScrollBarY;

	/*
	 * Flag que indica se a Barra de Scroll Vertical serÃ¡ pequena
	 */
	boolean smallVerticalBar = false;

	/*
	 * Flag que indica se existe uma Barra de Scroll Vertical
	 */
	boolean noVerticalBar, sNoVerticalBar = false;

	/*
	 * Flag que indica se as Barras de Scroll foram modificadas
	 */
	boolean scrollBarsChanged;

	/*
	 * Largura padrÃ£o para uma cÃ©lula da Grid
	 */
	int defaultCellWith = 0;
	/*
	 * Altura padrÃ£o para uma cÃ©lula da Grid
	 */
	int defaultFieldHeight;

	/*
	 * Modelo de dados
	 */
	private DataGridModel dataModel;

	/*
	 * NÃºmero de colunas fixas da esquerda para a direita
	 */
	private int fixedColumns = 0;

	/*
	 * Flag que indica se Ã© primeira vez que a tela Ã© desenhada
	 */
	boolean firstPaint = true;

	/*
	 * Imagem para desenhar o CabeÃ§alho Fixo da Grid
	 */
	Bitmap imageFixHeader;
	/*
	 * Imagem para desenhar o CabeÃ§alho da Grid
	 */
	Bitmap imageHeader;
	/*
	 * Imagem para desenhar a Ã�rea Fixa da Grid
	 */
	Bitmap imageFixArea;
	/*
	 * Imagem para desenhar a Ã�rea Principal da Grid
	 */
	Bitmap imageMainArea;
	/*
	 * Imagem para desenhar a Barra de Scroll Horizontal
	 */
	Bitmap imageHorizontalScrollBar;
	/*
	 * Imagem para desenhar a Ã rea de background da Grid
	 */
	Bitmap imageAllArea;

	/*
	 * Canvas para desenhar o CabeÃ§alho Fixo da Grid
	 */
	Canvas graphicsFixHeader;
	/*
	 * Canvas para desenhar o CabeÃ§alho da Grid
	 */
	Canvas graphicsHeader;
	/*
	 * Canvas para desenhar a Ã¡rea Fixa da Grid
	 */
	Canvas graphicsFixArea;
	/*
	 * Canvas para desenhar a Ã¡rea Principal da Grid
	 */
	Canvas graphicsMainArea;
	/*
	 * Canvas para desenhar a Barra Scroll Horizontal
	 */
	Canvas graphicsHorizontalScrollBar;
	/*
	 * Canvas para desenhar a Ã¡rea de background da grid
	 */
	Canvas graphicsAllArea;

	/*
	 * Cores da Grid, Barra de Scroll
	 */
	static final int clSilver1 = Color.rgb(99, 99, 99);
	static final int clWhite = Color.rgb(255, 255, 255);
	static final int clGray1 = Color.rgb(206, 206, 206);
	static final int clBlack = Color.rgb(0, 0, 0);
	static final int clGray2 = Color.rgb(214, 214, 206);
	static final int cLightBlue = Color.rgb(206, 206, 255);
	static final int clSilver2 = Color.rgb(132, 132, 132);
	static final int clHeader = Color.rgb(151, 151, 151);

	private int allAreaColor = Color.rgb(180, 180, 180);

	boolean baDown = false;
	boolean taDown = false;
	boolean laDown = false;
	boolean raDown = false;

	private DataGridField selectedField, oldSelectedField = null;

	int rcHAbsX, rcAbsX, absX, absY, dX, dY;
	int lastColumnNumber = -1;
	int firstColumnNumber = -1;
	int firstRecordNumber = -1;
	int lastRecordNumber = -1;

	int fixLastColumnNumber = -1;
	int fixFirstColumnNumber = -1;
	int fixFirstRecordNumber = -1;
	int fixLastRecordNumber = -1;

	Point absPoint;

	GestureDetector detector;

	private boolean drawSelectedFieldOnly;

	public DataGridModel getDataModel() {
		return dataModel;
	}

	public void setDataModel(DataGridModel dataModel) {
		this.dataModel = dataModel;
	}

	public int getFixedColumns() {
		return fixedColumns;
	}

	public void setFixedColumns(int fixedColumns) {
		this.fixedColumns = fixedColumns;
	}

	public DataGrid(Context context, DataGridModel dataModel) {
		super(context);
		detector = new GestureDetector(this);
		setFocusable(true);
		setFocusableInTouchMode(true);
		paint.setAntiAlias(true);
		withDataGrid = this.getWidth();
		heightDataGrid = this.getHeight();
		this.dataModel = dataModel;
		paint.setTextSize(fieldFontSize);
		paint.setTypeface(Typeface.DEFAULT);

		Bitmap bmp = BitmapFactory.decodeResource(getResources(),
				R.drawable.grid_arrow);
		widthArrow = bmp.getWidth();
		heightArrow = bmp.getHeight();

		upArrow = new BitmapDrawable(bmp);

		Bitmap bmpCheckBoxYes = BitmapFactory.decodeResource(getResources(),
				R.drawable.checkbox_yes);
		widthCheckBox = bmp.getWidth();
		heightCheckBox = bmp.getHeight();
		checkBoxYes = new BitmapDrawable(bmpCheckBoxYes);

		Bitmap bmpCheckBoxNo = BitmapFactory.decodeResource(getResources(),
				R.drawable.checkbox_no);
		checkBoxNo = new BitmapDrawable(bmpCheckBoxNo);

		Bitmap bmpSortAscend = BitmapFactory.decodeResource(getResources(),
				R.drawable.sort_ascend);
		widthSort = bmp.getWidth();
		heightSort = bmp.getHeight();
		sortAscend = new BitmapDrawable(bmpSortAscend);

		Bitmap bmpSortDescend = BitmapFactory.decodeResource(getResources(),
				R.drawable.sort_descend);
		sortDescend = new BitmapDrawable(bmpSortDescend);

		Matrix mtx = new Matrix();
		mtx.postRotate(180);
		downArrow = new BitmapDrawable(Bitmap.createBitmap(bmp, 0, 0,
				widthArrow, heightArrow, mtx, true));

		mtx = new Matrix();
		mtx.postRotate(90);
		leftArrow = new BitmapDrawable(Bitmap.createBitmap(bmp, 0, 0,
				widthArrow, heightArrow, mtx, true));

		mtx = new Matrix();
		mtx.postRotate(-90);
		rightArrow = new BitmapDrawable(Bitmap.createBitmap(bmp, 0, 0,
				widthArrow, heightArrow, mtx, true));

	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		initialize();
		paint.setColor(allAreaColor);
		canvas.drawPaint(paint);
		firstPaint = false;

		scrollBarsChanged = ((sNoHorizontalBar != noHorizontalBar));

		if (scrollBarsChanged) {
			createImages();
			scrollBarsChanged = false;
			sNoHorizontalBar = noHorizontalBar;
			sNoVerticalBar = noVerticalBar;
		}

		if ((!noVerticalBar) && (!noHorizontalBar)) {
			paint.setColor(Color.GREEN);
			canvas.drawRect(leftVerticalScrollBarX, 0, defaultScrollBarSize,
					heightHeader, paint);
			canvas.drawRect(leftVerticalScrollBarX, leftHorizontalScrollBarY,
					defaultScrollBarSize, defaultScrollBarSize, paint);
		}

		if (!noVerticalBar) {
			paint.setColor(Color.LTGRAY);
			canvas.drawRect(leftVerticalScrollBarX, 0, defaultScrollBarSize,
					heightHeader, paint);
		}

		if (existsColumns()) {
			drawFixHeader(canvas);
			drawHeader(canvas);
		}

		if (existsData()) {
			drawFixArea(canvas);
			drawMainArea(canvas);
		} else {
			CanvasUtils.drawText(canvas,
					new Rect(0, 0, this.getRight(), this.getBottom()),
					"Modelo de Dados NÃƒO definido.", Color.RED, 16,
					CanvasUtils.VCENTER, CanvasUtils.HCENTER);
		}

		if (!noVerticalBar) {
			scrollBarVertical.setPosition(currentTopVerticalBar);
			scrollBarVertical.draw(canvas);
		}

		if (!noHorizontalBar) {
			scrollBarHorizontal.setPosition(-deltaMainAreaX);
			scrollBarHorizontal.draw(canvas);
		}
	}

	private boolean existsData() {
		return ((dataModel != null) && (dataModel.recordCount() > 0));
	}

	private boolean existsColumns() {
		return ((dataModel != null) && (dataModel.columnCount() > 0));
	}

	private void initialize() {
		if (firstPaint) {
			defaultFieldHeight = 25;
			defaultScrollBarSize = 8;
			widthFixedHeader = this.getWidthFixedArea();
			heightFixedHeader = defaultFieldHeight;
			heightHeader = defaultFieldHeight;
			widthFixedArea = widthFixedHeader;
			heightHorizontalScrollBar = defaultScrollBarSize;
			widthVerticalScrollBar = defaultScrollBarSize;
			withDataGrid = this.getWidth();
			heightDataGrid = this.getHeight();
			widthMainArea = withDataGrid - widthFixedHeader;
			heightMainArea = heightDataGrid - heightHeader;

			calcAllValues();

			deltaMainAreaX = 0;
			deltaMainAreaY = 0;
			/*
			 * Ponto corrente Ã  esquerda da Barra Scroll Horizontal
			 */
			currentLeftHorizontalBar = 0;
			/*
			 * Ponto corrente no topo da Barra Scroll Vertical
			 */
			currentTopVerticalBar = 0;

			createImages();

			if ((dataModel != null) && (dataModel.recordCount() > 0)) {
				selectedField = dataModel.getRecord(0).getField(fixedColumns);
			}
		}
	}

	private void createImages() {
		imageAllArea = Bitmap.createBitmap(withDataGrid, heightDataGrid,
				Bitmap.Config.ARGB_8888);
		graphicsAllArea = new Canvas(imageAllArea);

		imageHeader = Bitmap.createBitmap(widthHeader, heightHeader,
				Bitmap.Config.ARGB_8888);
		graphicsHeader = new Canvas(imageHeader);

		if (fixedColumns > 0) {
			imageFixHeader = Bitmap.createBitmap(widthFixedHeader,
					heightFixedHeader, Bitmap.Config.ARGB_8888);
			graphicsFixHeader = new Canvas(imageFixHeader);

			imageFixArea = Bitmap.createBitmap(widthFixedArea, heightFixedArea,
					Bitmap.Config.ARGB_8888);
			graphicsFixArea = new Canvas(imageFixArea);
		}

		imageMainArea = Bitmap.createBitmap(widthMainArea, heightMainArea,
				Bitmap.Config.ARGB_8888);
		graphicsMainArea = new Canvas(imageMainArea);

		imageHorizontalScrollBar = Bitmap.createBitmap(
				widthHorizontalScrollBar, heightHorizontalScrollBar,
				Bitmap.Config.ARGB_8888);
		graphicsHorizontalScrollBar = new Canvas(imageHorizontalScrollBar);

	}

	/**
	 * Calcula todos os valores da grid
	 */
	private void calcAllValues() {
		heightMainAreaData = calcHeightMainAreaData();
		widthMainAreaData = calcWidthMainAreaData();
		checkScrollBars();

		leftFixedHeaderX = 0;
		leftFixedHeaderY = 0;
		leftHeaderX = widthFixedHeader;
		leftHeaderY = 0;
		leftFixedAreaX = 0;
		leftFixedAreaY = heightFixedHeader;
		leftMainAreaX = widthFixedArea;
		leftMainAreaY = heightHeader;
		leftHorizontalScrollBarX = 0;
		leftHorizontalScrollBarY = heightHeader + heightMainArea;
		leftVerticalScrollBarX = widthFixedHeader + widthHeader;
		leftVerticalScrollBarY = heightHeader;

		leftStartHorizontalBar = leftHorizontalScrollBarX;
		widthHorizontalBarWorkArea = widthHorizontalScrollBar;
		widthHorizontalBar = calcWidthHorizontalBar();

		topStartVerticalBar = 0;

		heightVerticalBarWorkArea = heightVerticalScrollBar;
		heightVerticalBar = calcHeightVerticalBar();

		if (scrollBarVertical == null) {
			scrollBarVertical = new ScrollBar(false, heightMainArea,
					calcHeightMainAreaData(), 0);
			scrollBarVertical.setBounds(leftVerticalScrollBarX, 0,
					leftVerticalScrollBarX + widthVerticalScrollBar,
					this.getHeight() - defaultScrollBarSize);
		}
		if (scrollBarHorizontal == null) {
			scrollBarHorizontal = new ScrollBar(true, widthFixedArea
					+ widthMainArea, calcWidthAllAreaData(), 0);
			scrollBarHorizontal.setBounds(0, this.getHeight()
					- defaultScrollBarSize, this.getWidth()
					- defaultScrollBarSize, this.getHeight());
		}
	}

	public int getWidthFixedArea() {
		if ((fixedColumns == 0) || (!existsColumns()))
			return 0;
		int w = 0;
		for (int i = 0; i < fixedColumns; i++) {
			if (dataModel.getColumn(i).isVisible()) {
				w += dataModel.getColumn(i).getWidth();
			}
		}
		if (w > (this.getWidth() * 40 / 100))
			return (int) (this.getWidth() * 40 / 100);

		return w;
	}

	/**
	 * Desenha o cabeÃ§alho Fixo
	 * 
	 * @param canvas
	 */
	private void drawFixHeader(Canvas canvas) {
		if (fixedColumns > 0) {
			int j;
			Rect headerRect;
			paint.setColor(clHeader);
			graphicsHeader.drawRect(0, 0, widthHeader, heightHeader, paint);
			calcFixHeaderRects();
			DataGridColumn column = null;
			for (j = 0; j < fixedColumns; j++) {
				column = dataModel.getColumn(j);
				if (column.isVisible()) {
					headerRect = column.getRect();
					CanvasUtils.fillRect(graphicsFixHeader, headerRect, true,
							clHeader, Color.BLACK, 0.5f);
					paint.setColor(Color.BLACK);
					if (DataGridColumn.SELECTED == column.getDataType()) {
						int x = (column.getWidth() / 2) - widthCheckBox / 2;
						int y = (defaultFieldHeight / 2) - heightCheckBox / 2;
						checkBoxYes.setBounds(headerRect.left + x,
								headerRect.top + x, headerRect.right - y,
								headerRect.bottom - y);
						checkBoxYes.draw(graphicsFixHeader);
					} else {
						CanvasUtils.drawText(graphicsFixHeader, headerRect,
								String.valueOf(column.getHeaderText()),
								Color.BLACK, 16, CanvasUtils.VCENTER,
								CanvasUtils.HCENTER);
					}
				}
			}

			canvas.drawBitmap(imageFixHeader, leftFixedHeaderX,
					leftFixedHeaderY, paint);
		}
	}

	/**
	 * Calcula o retÃ¢ngulo de cada cabeÃ§alho de coluna fixa
	 */
	private void calcFixHeaderRects() {
		int j;
		int actX, actY;
		actX = 0;
		DataGridColumn column = null;
		for (j = 0; j < fixedColumns; j++) {
			if (column != null) {
				actX += column.getWidth();
			}
			column = null;

			if (dataModel.getColumns().get(j).isVisible()) {
				column = dataModel.getColumn(j);
				actY = leftFixedHeaderY;
				Rect r = new Rect(actX, actY, actX + column.getWidth(), actY
						+ defaultFieldHeight);
				column.setRect(r);
			}
		}
	}

	/**
	 * Desenha o cabeÃ§alho
	 * 
	 * @param canvas
	 */
	private void drawHeader(Canvas canvas) {
		int j;
		Rect headerRect;
		paint.setColor(clHeader);
		graphicsHeader.drawRect(0, 0, widthHeader, heightHeader, paint);
		calcHeaderRects();
		DataGridColumn column = null;
		for (j = fixedColumns; j < dataModel.columnCount(); j++) {
			column = dataModel.getColumn(j);
			if (column.isVisible()) {
				headerRect = column.getRect();
				CanvasUtils.fillRect(graphicsHeader, headerRect, true,
						clHeader, Color.BLACK, 0.5f);
				paint.setColor(Color.BLACK);
				if (DataGridColumn.SELECTED == column.getDataType()) {
					int x = (column.getWidth() / 2) - widthCheckBox / 2;
					int y = (defaultFieldHeight / 2) - heightCheckBox / 2;
					checkBoxYes.setBounds(headerRect.left + x, headerRect.top
							+ x, headerRect.right - y, headerRect.bottom - y);
					checkBoxYes.draw(graphicsHeader);
				} else {
					if (column.isSort()) {
						int y = (defaultFieldHeight / 2) - heightCheckBox / 2;
						if (column.isSortDesc()) {
							sortDescend.setBounds(headerRect.left,
									headerRect.top + y, headerRect.left + 16,
									headerRect.top + y + 16);
							sortDescend.draw(graphicsHeader);
						} else {
							sortAscend.setBounds(headerRect.left,
									headerRect.top + y, headerRect.left + 16,
									headerRect.top + y + 16);
							sortAscend.draw(graphicsHeader);
						}

						CanvasUtils.drawText(graphicsHeader, headerRect,
								String.valueOf(column.getHeaderText()),
								Color.BLACK, 16, CanvasUtils.VCENTER,
								CanvasUtils.HCENTER);
					} else {
						CanvasUtils.drawText(graphicsHeader, headerRect,
								String.valueOf(column.getHeaderText()),
								Color.BLACK, 16, CanvasUtils.VCENTER,
								CanvasUtils.HCENTER);
					}
				}
			}
		}
		canvas.drawBitmap(imageHeader, leftHeaderX, leftHeaderY, paint);
	}

	/**
	 * Calcula o retÃ¢ngulo de cada cabeÃ§alho de coluna
	 */
	private void calcHeaderRects() {
		int j;
		int actX, actY;
		actX = 0;
		actX += deltaMainAreaX;
		int columnCount = dataModel.columnCount();
		DataGridColumn column = null;
		for (j = fixedColumns; j < columnCount; j++) {
			if (column != null) {
				actX += column.getWidth();
			}
			column = null;
			if (dataModel.getColumns().get(j).isVisible()) {
				column = dataModel.getColumn(j);
				actY = leftFixedHeaderY;
				Rect r = new Rect(actX, actY, actX + column.getWidth(), actY
						+ defaultFieldHeight);
				column.setRect(r);
			}
		}
	}

	/**
	 * Calcula o retÃ¢ngulo de cada cÃ©lula fixa da coluna zero
	 */
	private void calcFixCellsRects() {
		int i;
		int j;
		int actX, actY;
		DataGridRecord record = null;
		DataGridField field = null;

		int numRecords = dataModel.recordCount();
		for (i = 0; i < numRecords; i++) {
			record = dataModel.getRecord(i);

			actX = 0;
			actY = deltaMainAreaY + (i * defaultFieldHeight);

			field = null;
			for (j = 0; j < fixedColumns; j++) {
				if (field != null)
					actX += field.getWidth();

				field = null;

				if (record.getField(j).getColumn().isVisible()) {
					field = record.getField(j);

					Rect r = createNewRectangle(actX, actY, field.getWidth(),
							defaultFieldHeight);
					field.setRect(r);
					if ((r.left <= 0) && (r.right > 0)) {
						fixFirstColumnNumber = j;
					}
					if ((r.left <= widthFixedArea)
							&& (r.right > widthFixedArea)) {
						fixLastColumnNumber = j + 1;
					}

					if ((r.top <= 0) && (r.bottom > 0)) {
						fixFirstRecordNumber = i;
					}
					if ((r.top <= heightFixedArea)
							&& (r.bottom > heightFixedArea)) {
						fixLastRecordNumber = i + 2;
					}

					if ((lastColumnNumber == -1) && (j + 1 >= fixedColumns))
						fixLastColumnNumber = j + 1;

					/*
					 * OtimizaÃ§Ã£o: para quando ultrapassou a direita da grid
					 */
					if (r.right > (leftFixedAreaX + widthFixedArea))
						break;
				}
			}
			/*
			 * OtimizaÃ§Ã£o: para quando ultrapassou o parte de baixo da grid
			 */
			if (actY > (leftFixedAreaY + heightFixedArea))
				break;
		}
		if (fixLastRecordNumber > dataModel.recordCount()) {
			fixLastRecordNumber = dataModel.recordCount();
		}
		if (fixFirstRecordNumber == -1) {
			fixFirstRecordNumber = 0;
		}
		if (fixLastRecordNumber == -1) {
			fixLastRecordNumber = dataModel.recordCount();
		}
		if (fixLastRecordNumber > dataModel.recordCount()) {
			fixLastRecordNumber = dataModel.recordCount();
		}

		if (fixFirstColumnNumber == -1) {
			fixFirstColumnNumber = 0;
		}
		if (fixLastColumnNumber == -1) {
			fixLastColumnNumber = fixedColumns;
		}
		if (fixLastColumnNumber > fixedColumns) {
			fixLastColumnNumber = fixedColumns;
		}
	}

	/**
	 * Desenha a Ã¡rea de dados fixa
	 * 
	 * @param canvas
	 */
	private void drawFixArea(Canvas canvas) {
		if (fixedColumns > 0) {
			int i, j;
			DataGridRecord record;
			DataGridField field;

			paint.setColor(Color.LTGRAY);
			graphicsFixArea.drawRect(0, 0, widthFixedArea, heightFixedArea,
					paint);
			/*
			 * Calcula o retÃ¢ngulo de todas as cÃ©lulas da coluna fixa
			 */
			fixFirstColumnNumber = -1;
			fixLastColumnNumber = -1;
			fixFirstRecordNumber = -1;
			fixLastRecordNumber = -1;
			calcFixCellsRects();
			/*
			 * Desenha todas as cÃ©lulas da coluna fixa
			 */
			for (i = fixFirstRecordNumber; i < fixLastRecordNumber; i++) {
				record = dataModel.getRecords().get(i);

				for (j = fixFirstColumnNumber; j < fixLastColumnNumber; j++) {
					field = record.getField(j);
					drawField(field);
				}
			}

			canvas.drawBitmap(imageFixArea, leftFixedAreaX, leftFixedAreaY,
					paint);
		}
	}

	/**
	 * Desenha a Ã¡rea de dados principal
	 * 
	 * @param canvas
	 */
	private void drawMainArea(Canvas canvas) {
		int i, j;
		DataGridRecord record;
		/*
		 * Calcula os retÃ¢ngulos dos campos da Ã¡rea principal
		 */
		firstColumnNumber = -1;
		lastColumnNumber = -1;
		firstRecordNumber = -1;
		lastRecordNumber = -1;
		calcCellsRects();
		DataGridField field;
		/*
		 * Desenha somente o campo selecionado
		 */
		if (drawSelectedFieldOnly) {
			if (oldSelectedField != null) {
				if ((oldSelectedField.getRowNumber() >= firstRecordNumber)
						&& (oldSelectedField.getRowNumber() < lastRecordNumber))
					if ((oldSelectedField.getColNumber() >= firstColumnNumber)
							&& (oldSelectedField.getColNumber() < lastColumnNumber))
						drawField(oldSelectedField);
			}

			drawField(selectedField);
		} else {
			/*
			 * Desenha os registros/campos visÃ­veis na Ã¡rea principal
			 */
			for (i = firstRecordNumber; i < lastRecordNumber; i++) {
				record = dataModel.getRecord(i);

				for (j = firstColumnNumber; j < lastColumnNumber; j++) {
					field = record.getField(j);
					drawField(field);
				}
			}
		}
		drawSelectedFieldOnly = false;

		canvas.drawBitmap(imageMainArea, leftMainAreaX, leftMainAreaY, paint);
	}

	private void drawField(DataGridField field) {
		if ((field != null) && (field.isVisible())) {
			DecimalFormat df;
			boolean doDraw = true;
			Rect r = field.getRect();
			doDraw = true;
			DrawProperties properties = new DrawProperties();
			Canvas tmpCanvas;
			/*
			 * Se for uma coluna fixa
			 */
			if (field.getColNumber() < fixedColumns) {
				tmpCanvas = graphicsFixArea;
				properties.color = fixedFieldColor;
				properties.fontColor = fixedFieldFontColor;
				properties.fontSize = fixedFieldFontSize;
				properties.fontType = fixedFieldFontType;

				if ((selectedField != null)
						&& (field.getRowNumber() == selectedField
								.getRowNumber())) {
					properties.color = selectedFieldColor;
					properties.fontColor = selectedFieldFontColor;
					properties.fontSize = selectedFieldFontSize;
					properties.fontType = selectedFieldFontType;
				}
			} else {
				tmpCanvas = graphicsMainArea;
				if (field.getColumn().isSort()) {
					properties.color = sortFieldColor;
					properties.fontColor = sortFieldFontColor;
					properties.fontSize = sortFieldFontSize;
					properties.fontType = sortFieldFontType;
				} else {
					properties.color = fieldColor;
					properties.fontColor = fieldFontColor;
					properties.fontSize = fieldFontSize;
					properties.fontType = fieldFontType;
				}
				if (field == selectedField) {
					properties.color = selectedFieldColor;
					properties.fontColor = selectedFieldFontColor;
					properties.fontSize = selectedFieldFontSize;
					properties.fontType = selectedFieldFontType;
				}
			}
			CanvasUtils.fillRect(tmpCanvas, r.left, r.top, r.right, r.bottom,
					drawGrid, properties.color, Color.DKGRAY, 0.5f);

			/*
			 * Se for a coluna de seleÃ§Ã£o de registros desenha o checkbox
			 */
			if (DataGridColumn.SELECTED == field.getColumn().getDataType()) {
				int x = (field.getWidth() / 2) - widthCheckBox / 2;
				int y = (defaultFieldHeight / 2) - heightCheckBox / 2;
				if (dataModel.getRecord(field.getRowNumber()).isSelected()) {
					checkBoxYes.setBounds(r.left + x, r.top + x, r.right - y,
							r.bottom - y);
					checkBoxYes.draw(tmpCanvas);
				} else {
					checkBoxNo.setBounds(r.left + x, r.top + x, r.right - y,
							r.bottom - y);
					checkBoxNo.draw(tmpCanvas);
				}
			} else {
				if (listener != null)
					doDraw = listener.onDrawField(field, properties, tmpCanvas,
							paint);

				if (doDraw) {
					CanvasUtils.fillRect(tmpCanvas, r.left, r.top, r.right,
							r.bottom, drawGrid, properties.color, Color.DKGRAY,
							0.5f);
					String value = null;
					if (DataGridColumn.DATE == field.getColumn().getDataType()) {
						if (field.getValue() instanceof Date)
							value = DateUtil.toStringDateDMA((Date) field
									.getValue());
						else
							value = DateUtil.toStringDateDMA(DateUtil
									.stringToDate((String) field.getValue(),
											DateUtil.DATE));
					} else if (DataGridColumn.TIME == field.getColumn()
							.getDataType()) {
						if (field.getValue() instanceof Date)
							value = DateUtil.toStringTimeHMS((Date) field
									.getValue());
						else
							value = DateUtil
									.toStringTimeHMS(DateUtil
											.stringToDateTime((String) field
													.getValue()));
					} else if (DataGridColumn.DATETIME == field.getColumn()
							.getDataType()) {
						if (field.getValue() instanceof Date)
							value = DateUtil.toStringDateDMA((Date) field
									.getValue())
									+ " "
									+ DateUtil.toStringTimeHMS((Date) field
											.getValue());
						else {
							Date date = DateUtil
									.stringToDateTime((String) field.getValue());
							value = DateUtil.toStringDateDMA(date) + " "
									+ DateUtil.toStringTimeHMS(date);
						}
					} else if (DataGridColumn.INTEGER == field.getColumn()
							.getDataType()) {
						value = String.valueOf(field.getValue());
					} else if (DataGridColumn.NUMBER == field.getColumn()
							.getDataType()) {
						if (field.getColumn().getFormat() != null) {
							df = new DecimalFormat(field.getColumn()
									.getFormat());
							value = df.format(field.getValue());
						} else
							value = String.valueOf(field.getValue());
					} else
						value = String.valueOf(field.getValue());

					CanvasUtils.drawText(tmpCanvas, r.left + 2, r.top + 2,
							r.right - 2, r.bottom - 2, value, properties, field
									.getColumn().getAlignVertical(), field
									.getColumn().getAlignHorizontal());
				}
			}
		}
	}

	private void calcCellsRects() {
		int i;
		int j;
		int actX, actY;
		DataGridRecord record = null;
		DataGridField field = null;

		int numRecords = dataModel.recordCount();
		int numFields = dataModel.columnCount();

		for (i = 0; i < numRecords; i++) {
			record = dataModel.getRecord(i);

			actX = 0;
			actX += deltaMainAreaX;
			actY = deltaMainAreaY + (i * defaultFieldHeight);

			field = null;
			for (j = fixedColumns; j < numFields; j++) {
				if (field != null)
					actX += field.getWidth();
				field = null;

				if (record.getField(j).getColumn().isVisible()) {
					field = record.getField(j);

					Rect r = createNewRectangle(actX, actY, field.getWidth(),
							defaultFieldHeight);
					field.setRect(r);
					if ((r.left <= 0) && (r.right > 0)) {
						firstColumnNumber = j;
					}
					if ((r.left <= widthMainArea)
							&& ((r.right > widthMainArea) || ((r.right <= widthMainArea) && (j == numFields - 1)))) {
						lastColumnNumber = j + 1;
					}

					if ((r.top <= 0) && (r.bottom > 0)) {
						firstRecordNumber = i;
					}
					if ((r.top <= heightMainArea)
							&& (r.bottom > heightMainArea)) {
						lastRecordNumber = i + 2;
					}

					/*
					 * OtimizaÃ§Ã£o: para quando ultrapassou a direita da grid
					 */
					if (r.right > (leftMainAreaX + widthMainArea))
						break;
				}
			}
			/*
			 * OtimizaÃ§Ã£o: para quando ultrapassou o parte de baixo da grid
			 */
			if (actY > (leftMainAreaY + heightMainArea))
				break;
		}
		if (lastRecordNumber > dataModel.recordCount()) {
			lastRecordNumber = dataModel.recordCount();
		}
		if (firstRecordNumber == -1) {
			firstRecordNumber = 0;
		}
		if (lastRecordNumber == -1) {
			lastRecordNumber = dataModel.recordCount();
		}
		if (lastRecordNumber > dataModel.recordCount()) {
			lastRecordNumber = dataModel.recordCount();
		}

		if (firstColumnNumber == -1) {
			firstColumnNumber = 0;
		}
		if (lastColumnNumber == -1) {
			lastColumnNumber = record.fieldCount();
		}
		if (lastColumnNumber > record.fieldCount()) {
			lastColumnNumber = record.fieldCount();
		}
	}

	/**
	 * Calcula largura total da Barra de Scroll Horizontal
	 * 
	 * @return Largura total
	 */
	private int calcWidthHorizontalBar() {
		return (widthHorizontalBarWorkArea - (widthMainAreaData - widthMainArea));
	}

	/**
	 * Calcula altura total da Barra de Scroll Vertical
	 * 
	 * @return Altura total
	 */
	private int calcHeightVerticalBar() {
		return (heightVerticalBarWorkArea - (heightMainAreaData - heightMainArea));
	}

	/**
	 * Verifica se as barras de scroll irÃ£o ser desenhadas ou nÃ£o
	 */
	private void checkScrollBars() {
		noVerticalBar = (heightMainArea >= calcHeightMainAreaData());
		if (noVerticalBar) {
			widthMainArea = withDataGrid - widthFixedHeader;
			widthHeader = withDataGrid - widthFixedHeader;
			widthHorizontalScrollBar = withDataGrid;
		} else {
			widthMainArea = withDataGrid - widthFixedHeader
					- defaultScrollBarSize;
			widthHeader = withDataGrid - widthFixedHeader
					- defaultScrollBarSize;
			widthHorizontalScrollBar = withDataGrid - defaultScrollBarSize;
		}

		noHorizontalBar = (widthMainAreaData < widthMainArea);

		if (noHorizontalBar) {
			heightMainArea = heightDataGrid - heightHeader;
			heightFixedArea = heightDataGrid - defaultFieldHeight;
			heightVerticalScrollBar = heightDataGrid - heightHeader;
		} else {
			heightMainArea = heightDataGrid - heightHeader
					- defaultScrollBarSize;
			heightFixedArea = heightDataGrid - defaultFieldHeight
					- defaultScrollBarSize;
			heightVerticalScrollBar = heightDataGrid - heightHeader
					- defaultScrollBarSize;
		}
	}

	/**
	 * Calcula a altura total da Ã¡rea de dados da grid
	 * 
	 * @return altura total
	 */
	private int calcHeightMainAreaData() {
		/*
		 * altura da cÃ©lula * nÃºmero linhas do modelo de dados
		 */
		if ((dataModel == null) || (dataModel.recordCount() == 0))
			return 0;

		return defaultFieldHeight * (dataModel.recordCount() - 1);
	}

	/**
	 * Calcula a largura total da Ã¡rea de dados da grid
	 * 
	 * @return largura total
	 */
	private int calcWidthMainAreaData() {
		if ((dataModel == null) || (dataModel.recordCount() == 0))
			return 0;

		int i;
		int retValue = 0;
		for (i = fixedColumns; i < dataModel.columnCount(); i++) {
			if (dataModel.getColumn(i).isVisible()) {
				retValue += dataModel.getColumn(i).getWidth();
			}
		}
		return retValue;

	}

	private int calcWidthAllAreaData() {
		if ((dataModel == null) || (dataModel.recordCount() == 0))
			return 0;

		int i;
		int retValue = 0;
		for (i = 0; i < dataModel.columnCount(); i++) {
			if (dataModel.getColumn(i).isVisible()) {
				retValue += dataModel.getColumn(i).getWidth();
			}
		}
		return retValue;

	}

	private Rect createNewRectangle(int x, int y, int width, int height) {
		return new Rect(x, y, x + width, height + y);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
				scrollLeftNext(true);
			if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT)
				scrollRightNext(true);
			if (keyCode == KeyEvent.KEYCODE_DPAD_UP)
				scrollUpNext(true);
			if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN)
				scrollDownNext(true);
		}
		return super.onKeyDown(keyCode, event);
	}

	public int leftmostVisibleCol() {
		int i;
		DataGridColumn column;
		for (i = 1; i < dataModel.columnCount(); i++) {
			column = dataModel.getColumn(i);
			if ((column.getRect().left + column.getWidth()) > widthFixedArea) {
				if (i > 1) {
					return i - 1;
				} else {
					return 1;
				}
			}
		}
		return -1;
	}

	@Override
	public boolean onKeyShortcut(int keyCode, KeyEvent event) {
		return super.onKeyShortcut(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.detector.onTouchEvent(event);

		if ((event.getAction() == MotionEvent.ACTION_UP)
				|| (event.getAction() == MotionEvent.ACTION_CANCEL)) {
			baDown = false;
			taDown = false;
			laDown = false;
			raDown = false;

			invalidate();
		}

		super.onTouchEvent(event);
		return true;
	}

	private void doSelection(Point p) {

		if (isInMainArea(p)) {
			oldSelectedField = selectedField;
			selectedField = getFieldOnPoint((int) p.x, (int) p.y);
			if (selectedField != null) {
				if (isNeedAlignField(selectedField)) {
					alignShowCurrentCell(selectedField);
				} else {
					drawSelectedFieldOnly = true;
				}
				invalidate();
			}
		}
	}

	private DataGridField getFieldOnPoint(int x, int y) {
		int i, j;
		int x1 = x - leftMainAreaX;
		int y1 = y - leftMainAreaY;
		DataGridRecord record;
		Rect cr = null;
		for (i = firstRecordNumber; i < lastRecordNumber; i++) {
			record = dataModel.getRecord(i);
			for (j = firstColumnNumber; j < lastColumnNumber; j++) {
				cr = record.getField(j).getRect();
				if (cr != null) {
					if (((x1 > cr.left) && (x1 < cr.right))
							&& ((y1 > cr.top) && (y1 < cr.bottom))) {
						if (dataModel.getColumn(j).isVisible())
							return record.getField(j);
					}
				}
			}
		}

		return null;
	}

	private DataGridField getFieldOnPointFixed(int x, int y) {
		int i, j;
		int x1 = x - leftFixedAreaX;
		int y1 = y - leftFixedAreaY;
		DataGridRecord record;
		Rect cr = null;
		for (i = fixFirstRecordNumber; i < fixLastRecordNumber; i++) {
			record = dataModel.getRecord(i);
			for (j = fixFirstColumnNumber; j < fixLastColumnNumber; j++) {
				cr = record.getField(j).getRect();
				if (cr != null) {
					if (((x1 > cr.left) && (x1 < cr.right))
							&& ((y1 > cr.top) && (y1 < cr.bottom))) {
						if (dataModel.getColumn(j).isVisible())
							return record.getField(j);
					}
				}
			}
		}
		return null;
	}

	private DataGridColumn getColumnOnPointFixHeader(int x, int y) {
		int i;
		int x1 = x - leftFixedHeaderX;
		int y1 = y - leftFixedHeaderY;
		Rect cr = null;
		for (i = 0; i < fixedColumns; i++) {
			cr = dataModel.getColumn(i).getRect();
			if (cr != null) {
				if (((x1 > cr.left) && (x1 < cr.right))
						&& ((y1 > cr.top) && (y1 < cr.bottom))) {
					if (dataModel.getColumn(i).isVisible())
						return dataModel.getColumn(i);
				}
			}
		}
		return null;
	}

	private DataGridColumn getColumnOnPointHeader(int x, int y) {
		int i;
		int x1 = x - leftHeaderX;
		int y1 = y - leftHeaderY;
		Rect cr = null;
		for (i = fixedColumns; i < dataModel.columnCount(); i++) {
			cr = dataModel.getColumn(i).getRect();
			if (cr != null) {
				if (((x1 > cr.left) && (x1 < cr.right))
						&& ((y1 > cr.top) && (y1 < cr.bottom))) {
					if (dataModel.getColumn(i).isVisible())
						return dataModel.getColumn(i);
				}
			}
		}
		return null;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		LOG.info("trackBall x=" + event.getX() + ", y=" + event.getY());
		this.detector.onTouchEvent(event);
		return true;
	}

	private boolean scrollLeftNext(boolean doDraw) {
		if (selectedField != null) {
			if (selectedField.getColNumber() > 1) {
				oldSelectedField = selectedField;
				selectedField = dataModel.getRecord(
						selectedField.getRowNumber()).getField(
						selectedField.getColNumber() - 1);
				while (!dataModel.getColumn(selectedField.getColNumber())
						.isVisible()) {
					if (selectedField.getColNumber() > 1) {
						selectedField = dataModel.getRecord(
								selectedField.getRowNumber()).getField(
								selectedField.getColNumber() - 1);
					} else {
						return false;
					}
				}
				if (doDraw) {
					alignShowCurrentCell(selectedField);
					invalidate();
				}
				return true;
			}
		}
		return false;
	}

	private boolean scrollRightNext(boolean doDraw) {
		if (selectedField != null) {
			if (selectedField.getColNumber() < (dataModel.columnCount() - 1)) {
				oldSelectedField = selectedField;
				selectedField = dataModel.getRecord(
						selectedField.getRowNumber()).getField(
						selectedField.getColNumber() + 1);
				while (!dataModel.getColumn(selectedField.getColNumber())
						.isVisible()) {
					if (selectedField.getColNumber() < (dataModel.columnCount() - 1)) {
						selectedField = dataModel.getRecord(
								selectedField.getRowNumber()).getField(
								selectedField.getColNumber() + 1);
					} else {
						return false;
					}
				}
				if (doDraw) {
					alignShowCurrentCell(selectedField);
					invalidate();

				}
				return true;
			}
		}
		return false;
	}

	private boolean scrollDownNext(boolean doDraw) {
		if (selectedField != null) {
			if (selectedField.getRowNumber() < dataModel.recordCount() - 1) {
				oldSelectedField = selectedField;
				selectedField = dataModel.getRecord(
						selectedField.getRowNumber() + 1).getField(
						selectedField.getColNumber());
				if (doDraw) {
					alignShowCurrentCell(selectedField);
					invalidate();
				}
				return true;
			}
		}
		return false;
	}

	private boolean scrollUpNext(boolean doDraw) {
		if (selectedField != null) {
			if (selectedField.getRowNumber() > 0) {
				oldSelectedField = selectedField;
				selectedField = dataModel.getRecord(
						selectedField.getRowNumber() - 1).getField(
						selectedField.getColNumber());
				if (doDraw) {
					alignShowCurrentCell(selectedField);
					invalidate();
				}
				return true;
			}
		}
		return false;
	}

	private void alignShowCurrentCell(DataGridField field) {
		if (field != null) {
			if ((field.getRect().left + field.getWidth()) > widthMainArea) {
				doScrollRight((field.getRect().left + field.getWidth())
						- widthMainArea);
			}
			if (field.getRect().left < 0) {
				doScrollLeft(-field.getRect().left);
			}
			if (field.getRect().top < 0) {
				doScrollUp(-field.getRect().top);
			}
			if ((field.getRect().top + defaultFieldHeight) > heightMainArea) {
				doScrollDown((field.getRect().top + defaultFieldHeight)
						- heightMainArea);
			}
		}
	}

	private boolean isNeedAlignField(DataGridField field) {
		if (field != null) {
			if ((field.getRect().left + field.getWidth()) > widthMainArea) {
				return true;
			}
			if (field.getRect().left < 0) {
				return true;
			}
			if (field.getRect().top < 0) {
				return true;
			}
			if ((field.getRect().top + defaultFieldHeight) > heightMainArea) {
				return true;
			}
		}
		return false;
	}

	private void doScrollLeft(int delta) {
		deltaMainAreaX += delta;
		calcCurrentLeftHorizontalBar();

		if (currentLeftHorizontalBar <= leftStartHorizontalBar) {
			currentLeftHorizontalBar = leftStartHorizontalBar;
			deltaMainAreaX = 0;
		}
		if (deltaMainAreaX > 0) {
			deltaMainAreaX = 0;
		}
		invalidate();
	}

	private void doScrollRight(int delta) {
		deltaMainAreaX -= delta;
		calcCurrentLeftHorizontalBar();
		if ((currentLeftHorizontalBar + widthHorizontalBar) >= widthHorizontalBarWorkArea) {
			currentLeftHorizontalBar = widthHorizontalBarWorkArea
					- widthHorizontalBar;
		}
		if (deltaMainAreaX < -(widthMainAreaData - widthMainArea)) {
			deltaMainAreaX = -(widthMainAreaData - widthMainArea);
		}
		invalidate();
	}

	private void doScrollUp(int delta) {
		deltaMainAreaY += delta;
		calcCurTopVerticalBar();
		if (currentTopVerticalBar < /* = */topStartVerticalBar) {
			currentTopVerticalBar = topStartVerticalBar;
		}
		if (deltaMainAreaY > 0) {
			deltaMainAreaY = 0;
		}
		invalidate();
	}

	private void doScrollDown(int delta) {
		deltaMainAreaY -= delta;
		calcCurTopVerticalBar();
		if ((currentTopVerticalBar + heightVerticalBar) >= heightVerticalBarWorkArea) {
			currentTopVerticalBar = (heightVerticalBarWorkArea)
					- heightVerticalBar + 1;
		}
		if (deltaMainAreaY < -(heightMainAreaData - heightMainArea)
				- heightHeader) {
			deltaMainAreaY = -(heightMainAreaData - heightMainArea)
					- heightHeader;
		}
		invalidate();
	}

	private void calcCurTopVerticalBar() {
		if (!smallVerticalBar) {
			currentTopVerticalBar = -deltaMainAreaY;
		} else {
			currentTopVerticalBar = ((-deltaMainAreaY) * heightVerticalBarWorkArea)
					/ (heightMainAreaData - heightMainArea);
		}
		if (currentTopVerticalBar < defaultScrollBarSize) {
			currentTopVerticalBar = 0;
		}
	}

	private void calcCurrentLeftHorizontalBar() {
		currentLeftHorizontalBar = (-deltaMainAreaX * widthHorizontalBarWorkArea)
				/ (widthMainAreaData - widthMainArea);
		if (currentLeftHorizontalBar < 0) {
			currentLeftHorizontalBar = 0;
		}
	}

	private boolean isInMainArea(Point p) {
		if (p == null)
			return false;
		return ((p.x > leftMainAreaX) && (p.x < leftMainAreaX + widthMainArea))
				&& ((p.y > leftMainAreaY) && (p.y < leftMainAreaY
						+ heightMainArea));
	}

	private boolean isInFixHeader(Point p) {
		if (p == null)
			return false;
		Rect r = new Rect(leftFixedHeaderX, leftFixedHeaderY, leftFixedHeaderX
				+ widthFixedHeader, leftFixedHeaderY + heightFixedHeader);
		return r.contains((int) p.x, (int) p.y);
	}

	private boolean isInHeader(Point p) {
		if (p == null)
			return false;
		Rect r = new Rect(leftHeaderX, leftHeaderY, leftHeaderX + widthHeader,
				leftHeaderY + heightHeader);
		return r.contains((int) p.x, (int) p.y);
	}

	private boolean isInFixArea(Point p) {
		if (p == null)
			return false;
		return ((p.x > leftFixedAreaX) && (p.x < leftFixedAreaX
				+ widthFixedArea))
				&& ((p.y > leftFixedAreaY) && (p.y < leftFixedAreaY
						+ heightFixedArea));
	}

	public boolean onDown(MotionEvent e) {
		LOG.debug("---onDown---- Evento-> x=" + e.getX() + ", y=" + e.getY());
		scrollX = 0;
		scrollY = 0;
		return true;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		LOG.debug(
				"Grid ---onFling--- Evento-> x=" + e1.getX() + ", y=" + e1.getY()
						+ "  Evento 2-> x=" + e2.getX() + ", y=" + e2.getY()
						+ " velocityX=" + velocityX + ", velocityY="
						+ velocityY + " distanceY="
						+ Math.abs((int) (e2.getY() - e1.getY())));

		if (Math.abs(velocityY) > Math.abs(velocityX)) {
			if (velocityY - scrollY < 0)
				doScrollDown((int) Math.abs(velocityY - scrollY) / 2);
			else
				doScrollUp((int) Math.abs(velocityY - scrollY) / 2);
		} else {
			if (velocityX - scrollX < 0)
				doScrollRight((int) Math.abs(velocityX - scrollX) / 5);
			else
				doScrollLeft((int) Math.abs(velocityX - scrollX) / 5);
		}

		return true;
	}

	public void onLongPress(MotionEvent e) {
		// Log.d("grid","---onLongPress--- Evento-> x=" + e.getX() + ", y=" +
		// e.getY());
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// Log.d("grid","---onScroll--- Evento-> x=" + e1.getX() + ", y=" +
		// e1.getY()+ "  Evento 2-> x=" + e2.getX() + ", y=" + e2.getY());

		if (Math.abs(distanceY) > Math.abs(distanceX)) {
			if (distanceY < 0)
				doScrollUp((int) Math.abs(distanceY));
			else
				doScrollDown((int) Math.abs(distanceY));
			scrollX += (int) Math.abs(distanceY);
		} else {
			if (distanceX < 0)
				doScrollLeft((int) Math.abs(distanceX));
			else
				doScrollRight((int) Math.abs(distanceX));
			scrollY += (int) Math.abs(distanceX);
		}
		return true;
	}

	public void onShowPress(MotionEvent e) {
		// Log.d("grid","---onShowPress--- Evento-> x=" + e.getX() + ", y=" +
		// e.getY());
	}

	public boolean onSingleTapUp(MotionEvent e) {
		// Log.d("grid","---onSingleTapUp--- Evento-> x=" + e.getX() + ", y="+
		// e.getY());
		return false;
	}

	public boolean onDoubleTap(MotionEvent e) {
		LOG.debug("---onDoubleTap--- Evento-> x=" + e.getX() + ", y=" + e.getY());
		return false;
	}

	public boolean onDoubleTapEvent(MotionEvent e) {
		LOG.debug("---onDoubleTapEvent--- Evento-> x=" + e.getX() + ", y="
				+ e.getY());
		return false;
	}

	public boolean onSingleTapConfirmed(MotionEvent event) {
		LOG.debug("---onSingleTapConfirmed--- Evento-> x=" + event.getX()
				+ ", y=" + event.getY());

		absPoint = new Point(event.getX(), event.getY());
		if (isInMainArea(absPoint)) {
			DataGridField f = getFieldOnPoint((int) absPoint.x,
					(int) absPoint.y);
			if (f != null) {
				if (DataGridColumn.SELECTED == f.getColumn().getDataType()) {
					dataModel.getRecord(f.getRowNumber()).invertSelected();
				}
			}
			doSelection(absPoint);
			return true;
		}

		if (isInFixArea(absPoint)) {
			DataGridField f = getFieldOnPointFixed((int) absPoint.x,
					(int) absPoint.y);
			if (f != null) {
				if (DataGridColumn.SELECTED == f.getColumn().getDataType()) {
					dataModel.getRecord(f.getRowNumber()).invertSelected();
					drawSelectedFieldOnly = true;
					invalidate();
					return true;
				}
			}
		}

		if (isInHeader(absPoint)) {
			DataGridColumn c = getColumnOnPointHeader((int) absPoint.x,
					(int) absPoint.y);
			if (c != null) {
				if (DataGridColumn.SELECTED == c.getDataType()) {
					dataModel.invertSelectAllRecords();
					oldSelectedField = selectedField;
					invalidate();
					return true;
				}

				boolean desc = false;
				if (c.equals(dataModel.getColumnSort()))
					desc = !c.isSortDesc();

				final DataGrid g = this;
				Handler handler = new Handler() {
					@Override
					public void handleMessage(Message msg) {
						g.invalidate();
					}
				};

				dataModel.sortColumn(this.getContext(), handler, c, desc);
				if (listener != null) {
					listener.onTitleClick(c);
					return true;
				}
			}
		}

		if (isInFixHeader(absPoint)) {
			DataGridColumn c = getColumnOnPointFixHeader((int) absPoint.x,
					(int) absPoint.y);
			if (c != null) {
				if (DataGridColumn.SELECTED == c.getDataType()) {
					dataModel.invertSelectAllRecords();
					invalidate();
					return true;
				}
			}
		}

		return true;
	}

	public Integer[] selectedIndexes() {
		if (dataModel != null)
			return dataModel.selectedIndexes();
		return null;
	}

	public int selectedCount() {
		if (dataModel != null)
			return dataModel.selectedCount();
		return 0;
	}

	public void selectAll() {
		if (dataModel != null)
			dataModel.selectAll();
	}

	public void unSelectAll() {
		if (dataModel != null)
			dataModel.unSelectAll();
	}

	public DataGridRecord[] selectedRecords() {
		if (dataModel != null)
			return dataModel.selectedRecords();
		return null;
	}

	public DataGridListener getListener() {
		return listener;
	}

	public void setListener(DataGridListener listener) {
		this.listener = listener;
	}

	public int getFieldColor() {
		return fieldColor;
	}

	public void setFieldColor(int fieldColor) {
		this.fieldColor = fieldColor;
	}

	public int getFixedFieldColor() {
		return fixedFieldColor;
	}

	public void setFixedFieldColor(int fixedFieldColor) {
		this.fixedFieldColor = fixedFieldColor;
	}

	public int getFixedFieldFontColor() {
		return fixedFieldFontColor;
	}

	public void setFixedFieldFontColor(int fixedFieldFontColor) {
		this.fixedFieldFontColor = fixedFieldFontColor;
	}

	public int getFixedFieldFontSize() {
		return fixedFieldFontSize;
	}

	public void setFixedFieldFontSize(int fixedFieldFontSize) {
		this.fixedFieldFontSize = fixedFieldFontSize;
	}

	public Typeface getFixedFieldFontType() {
		return fixedFieldFontType;
	}

	public void setFixedFieldFontType(Typeface fixedFieldFontType) {
		this.fixedFieldFontType = fixedFieldFontType;
	}

	public int getSelectedFieldColor() {
		return selectedFieldColor;
	}

	public void setSelectedFieldColor(int selectedFieldColor) {
		this.selectedFieldColor = selectedFieldColor;
	}

	public int getSelectedFieldFontColor() {
		return selectedFieldFontColor;
	}

	public void setSelectedFieldFontColor(int selectedFieldFontColor) {
		this.selectedFieldFontColor = selectedFieldFontColor;
	}

	public int getSelectedFieldFontSize() {
		return selectedFieldFontSize;
	}

	public void setSelectedFieldFontSize(int selectedFieldFontSize) {
		this.selectedFieldFontSize = selectedFieldFontSize;
	}

	public Typeface getSelectedFieldFontType() {
		return selectedFieldFontType;
	}

	public void setSelectedFieldFontType(Typeface selectedFieldFontType) {
		this.selectedFieldFontType = selectedFieldFontType;
	}

	public int getFieldFontColor() {
		return fieldFontColor;
	}

	public void setFieldFontColor(int fieldFontColor) {
		this.fieldFontColor = fieldFontColor;
	}

	public int getFieldFontSize() {
		return fieldFontSize;
	}

	public void setFieldFontSize(int fieldFontSize) {
		this.fieldFontSize = fieldFontSize;
	}

	public Typeface getFieldFontType() {
		return fieldFontType;
	}

	public void setFieldFontType(Typeface fieldFontType) {
		this.fieldFontType = fieldFontType;
	}

	public int getSortFieldColor() {
		return sortFieldColor;
	}

	public void setSortFieldColor(int sortFieldColor) {
		this.sortFieldColor = sortFieldColor;
	}

	public int getSortFieldFontColor() {
		return sortFieldFontColor;
	}

	public void setSortFieldFontColor(int sortFieldFontColor) {
		this.sortFieldFontColor = sortFieldFontColor;
	}

	public int getSortFieldFontSize() {
		return sortFieldFontSize;
	}

	public void setSortFieldFontSize(int sortFieldFontSize) {
		this.sortFieldFontSize = sortFieldFontSize;
	}

	public Typeface getSortFieldFontType() {
		return sortFieldFontType;
	}

	public void setSortFieldFontType(Typeface sortFieldFontType) {
		this.sortFieldFontType = sortFieldFontType;
	}

	public DataGridField getSelectedField() {
		return selectedField;
	}

}

/**
 * RetÃ¢ngulo AWT
 * 
 * public Rectangle(int x, int y, int width, int height) Constructs a new
 * Rectangle whose upper-left corner is specified as (x,y) and whose width and
 * height are specified by the arguments of the same name. Parameters: x - the
 * specified X coordinate y - the specified Y coordinate width - the width of
 * the Rectangle height - the height of the Rectangle
 * 
 * 
 * RetÃ¢ngulo Android public Rect (int left, int top, int right, int bottom)
 * Create a new rectangle with the specified coordinates. Note: no range
 * checking is performed, so the caller must ensure that left <= right and top
 * <= bottom. Parameters left The X coordinate of the left side of the rectagle
 * top The Y coordinate of the top of the rectangle right The X coordinate of
 * the right side of the rectagle bottom The Y coordinate of the bottom of the
 * rectangle
 * 
 */
