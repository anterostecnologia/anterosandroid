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

package br.com.anteros.android.ui.controls.kanban;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.ViewGroup;

import br.com.anteros.android.ui.controls.R;

/**
 * 
 * @author Eduardo Albertini
 *
 */
public class KanbanColumn extends ViewGroup {

	private Paint paint;
	private int colorHeader1;
	private int colorHeader2;
	private String caption;
	private int captionColor;
	private int columnWidth;
	private int columnHeight;
	private float columnWidthPercent;

	public KanbanColumn(Context context, AttributeSet attrs) {
		super(context, attrs);

		this.paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
		setWillNotDraw(false);
		TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.KanbanColumn, 0, 0);

		try {
			if (attributes != null) {
				this.colorHeader1 = attributes.getColor(R.styleable.KanbanColumn_columnColorHeader1, Color.TRANSPARENT);
				this.colorHeader2 = attributes.getColor(R.styleable.KanbanColumn_columnColorHeader2, colorHeader1);
				this.caption = attributes.getString(R.styleable.KanbanColumn_columnCaption) == null ? "" : attributes
						.getString(R.styleable.KanbanColumn_columnCaption);
				this.captionColor = attributes.getColor(R.styleable.KanbanColumn_columnCaptionColor, Color.BLACK);
				this.columnWidthPercent = attributes.getFloat(R.styleable.KanbanColumn_columnWidthPercent, 0);
			}
		} finally {
			if (attributes != null)
				attributes.recycle();
		}
	}

	public KanbanColumn(Context context) {
		super(context);
		this.paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
		setWillNotDraw(false);

		this.caption = "";
		this.captionColor = Color.BLACK;
		this.colorHeader2 = colorHeader1;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		this.columnWidth = getWidth();
		this.columnHeight = getHeight();

		// Pinta a coluna de acordo com os tamanhos especificados
		paint.setStyle(Style.FILL);
		paint.setColor(Color.TRANSPARENT);
		canvas.drawRect(0, 0, columnWidth, columnHeight, paint);
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.STROKE);
		canvas.drawRect(0, 0, columnWidth, columnHeight, paint);
	}

	/**
	 * Método responsável por pintar o cabeçalho das colunas
	 * 
	 * @param canvas
	 * @param left
	 *            a coordenada inicial de X
	 * @param top
	 *            a coordenada inicial de Y
	 * @param right
	 *            a coordenada final de X
	 * @param bottom
	 *            a coordenada final de Y
	 * 
	 */
	public void drawHeader(Canvas canvas, int left, int top, int rigth, int bottom) {
		Kanban kanban = new Kanban(getContext());
		if (getParent().getParent() instanceof Kanban)
			kanban = (Kanban) getParent().getParent();

		// Desenha as bordas e pinta o background do cabeçalho
		paint.setStyle(Style.FILL);
		paint.setShader(new LinearGradient(0, 0, 0, bottom, colorHeader1, colorHeader2, TileMode.CLAMP));
		canvas.drawRect(left, top, rigth, bottom, paint);
		paint.setShader(null);
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.STROKE);
		canvas.drawRect(left, top, rigth, bottom, paint);

		// Centraliza o texto na coluna e desenha o texto
		paint.setColor(captionColor);

		paint.setTextSize(adjustTextSize(caption, kanban.getCaptionSize(), rigth - left));
		paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
		paint.setTextAlign(Align.CENTER);

		canvas.drawText(caption, (int) (left + (rigth - left) / 2), (kanban.getCaptionSize() + top), paint);
	}

	/**
	 * Método responsável por ajustar a largura do texto dentro de um
	 * determinado espaço
	 * 
	 * @param caption
	 *            texto para ser redimenionado
	 * @param textSize
	 *            tamanho atual/padrão do texto
	 * @param maxWidth
	 *            tamanho máximo do texto
	 * 
	 */
	private int adjustTextSize(String caption, int textSize, int maxWidth) {
		paint.setTextSize(textSize);
		Rect r = new Rect();
		paint.getTextBounds(caption, 0, caption.length(), r);
		int newSize = textSize;

		while (r.width() > maxWidth) {
			newSize -= 1;
			paint.setTextSize(newSize);
			paint.getTextBounds(caption, 0, caption.length(), r);
		}

		return newSize;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		Kanban kanban = (Kanban) ((KanbanContent) getParent()).getParent();

		int itemAnteriorBottom = 0;
		int itemAnteriorRigth = 0;
		int itemAnteriorTop = kanban.getCardSpacing();
		int itemTop = kanban.getCardSpacing();
		int itemBottom = 0;
		int itemLeft = 0;
		int itemRigth = 0;

		for (int i = 0; i < this.getChildCount(); i++) {
			if (getChildAt(i) instanceof KanbanCard) {
				KanbanCard kanbanCard = (KanbanCard) getChildAt(i);

				// Obtém o filho anterior da coluna
				try {
					KanbanCard itemAnterior = (KanbanCard) getChildAt(i - 1);
					itemAnteriorBottom = itemAnterior.getBottom();
					itemAnteriorRigth = itemAnterior.getRight();
					itemAnteriorTop = itemAnterior.getTop();
				} catch (Exception ex) {
				}

				// Se o tamanho do cartão for menor que o tamanho da coluna,
				// define as posições para colocar no layout. Os cartões serão
				// dispostos horizontalmente até atingirem a borda da coluna.
				// Quando chegar na borda, pula para a próxima linha
				// if (kanban.getCardWidth() < getWidth()) {
				if (itemAnteriorRigth + kanban.getCardSpacing() + kanban.getCardWidth() < getWidth()) {
					itemLeft = itemAnteriorRigth + kanban.getCardSpacing();
					itemTop = itemAnteriorTop;
				} else {
					itemTop = itemAnteriorBottom + kanban.getCardSpacing();
					itemLeft = kanban.getCardSpacing();
				}
				itemBottom = itemTop + kanban.getCardHeight();
				itemRigth = itemLeft + kanban.getCardWidth();

				// Define a posição no layout
				kanbanCard.layout(itemLeft, itemTop, itemRigth, itemBottom);
				// } else {
				// throw new IllegalArgumentException("A largura do cartão [" +
				// kanban.getCardWidth()
				// + "] excede a largura da coluna [" + columnWidth + "]");
				// }
			}
		}
	}

	public int getColorHeader1() {
		return colorHeader1;
	}

	// Se o usuário não definir a segunda cor do cabeçalho, ela será igual a
	// primeira cor
	public void setColorHeader1(int colorHeader1) {
		this.colorHeader1 = colorHeader1;
		if (colorHeader2 == 0)
			this.colorHeader2 = colorHeader1;
		invalidate();
	}

	public int getColorHeader2() {
		return colorHeader2;
	}

	public void setColorHeader2(int colorHeader2) {
		this.colorHeader2 = colorHeader2;
		invalidate();
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
		invalidate();
	}

	public int getColumnWidth() {
		return columnWidth;
	}

	public void setColumnWidth(int columnWidth) {
		this.columnWidth = columnWidth;
		invalidate();
	}

	public float getColumnWidthPercent() {
		return columnWidthPercent;
	}

	public void setColumnWidthPercent(float columnWidthPercent) {
		this.columnWidthPercent = columnWidthPercent;
		invalidate();
	}

	public int getCaptionColor() {
		return captionColor;
	}

	public void setCaptionColor(int captionColor) {
		this.captionColor = captionColor;
		invalidate();
	}
}
