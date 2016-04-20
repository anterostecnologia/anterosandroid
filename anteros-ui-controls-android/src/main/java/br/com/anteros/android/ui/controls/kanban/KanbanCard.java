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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import br.com.anteros.android.ui.controls.R;

/**
 * 
 * @author Eduardo Albertini
 *
 */
public class KanbanCard extends View {

	private long ID;
	private String caption;
	private String description;
	private String columnName;
	private Bitmap picture;
	private int width;
	private int height;
	private Paint paint;
	private int colorHeader1;
	private int colorHeader2;
	private int colorCard1;
	private int colorCard2;
	private int drawable;
	private int captionSize;
	private int roundBorder;
	private int colorCaption;
	private int colorDescription;

	public KanbanCard(Context context) {
		super(context);

		this.paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
		this.caption = "";
		this.description = "";
		this.colorHeader1 = Color.WHITE;
		this.colorHeader2 = colorHeader1;
		this.colorCard1 = Color.WHITE;
		this.colorCard2 = colorCard1;
		this.captionSize = 16;
		this.roundBorder = 6;
		this.colorCaption = Color.BLACK;
		this.colorDescription = Color.BLACK;
		this.width = 50;
		this.height = 50;

	}

	public KanbanCard(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.KanbanCard, 0, 0);

		try {
			if (attributes != null) {
				this.caption = attributes.getString(R.styleable.KanbanCard_cardCaption) == null ? "" : attributes
						.getString(R.styleable.KanbanCard_cardCaption);
				this.description = attributes.getString(R.styleable.KanbanCard_cardDescription) == null ? ""
						: attributes.getString(R.styleable.KanbanCard_cardDescription);
				this.drawable = attributes.getResourceId(R.styleable.KanbanCard_cardPicture, 0);
				this.colorHeader1 = attributes.getColor(R.styleable.KanbanCard_cardColorHeader1, Color.WHITE);
				this.colorHeader2 = attributes.getColor(R.styleable.KanbanCard_cardColorHeader2, colorHeader1);
				this.colorCard1 = attributes.getColor(R.styleable.KanbanCard_cardColorCard1, Color.WHITE);
				this.colorCard2 = attributes.getColor(R.styleable.KanbanCard_cardColorCard2, colorCard1);
				this.captionSize = attributes.getInteger(R.styleable.KanbanCard_cardCaptionSize, 16);
				this.roundBorder = attributes.getInteger(R.styleable.KanbanCard_cardRoundBorder, 6);
				this.colorCaption = attributes.getColor(R.styleable.KanbanCard_cardColorCaption, Color.BLACK);
				this.colorDescription = attributes.getColor(R.styleable.KanbanCard_cardColorDescription, Color.BLACK);
				this.width = attributes.getInteger(R.styleable.KanbanCard_cardWidth, 50);
				this.height = attributes.getInteger(R.styleable.KanbanCard_cardHeight, 50);
			}
		} finally {
			if (attributes != null)
				attributes.recycle();
		}

		this.paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		// Desenha as bordas e pinta o cartão
		canvas.drawRoundRect(new RectF(0, 0, width, height), roundBorder, roundBorder, paint);
		paint.setTextAlign(Align.LEFT);
		paint.setShader(new LinearGradient(0, 0, 0, height, colorCard1, colorCard2, TileMode.CLAMP));
		paint.setStyle(Style.FILL);
		canvas.drawRoundRect(new RectF(0, 0, width, height), roundBorder, roundBorder, paint);
		paint.setShader(null);
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.STROKE);
		canvas.drawRoundRect(new RectF(0, 0, width, height), roundBorder, roundBorder, paint);

		// Desenha as bordas e pinta o cabeçalho do cartão
		paint.setShader(new LinearGradient(0, 0, 0, captionSize + (captionSize / 4), colorHeader1, colorHeader2,
				TileMode.CLAMP));
		paint.setStyle(Style.FILL);
		canvas.drawRoundRect(new RectF(0, 0, width, captionSize + (captionSize / 4)), roundBorder, roundBorder, paint);
		paint.setShader(null);
		paint.setColor(Color.BLACK);
		paint.setStyle(Style.STROKE);
		canvas.drawRoundRect(new RectF(0, 0, width, captionSize + (captionSize / 4)), roundBorder, roundBorder, paint);

		// Desenha o titulo do cartão
		paint.setTextSize(captionSize);
		paint.setColor(colorCaption);
		paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
		canvas.drawText(caption, (captionSize / 4), captionSize, paint);

		paint.setTextSize(captionSize - 4);
		paint.setColor(colorDescription);
		paint.setTypeface(null);
		paint.setTextAlign(Align.LEFT);

		if (drawable != 0)
			picture = scaleDown(BitmapFactory.decodeResource(getResources(), drawable), (float) (height * .6), false);
		else if (picture != null)
			picture = scaleDown(picture, (float) (height * .60), false);

		// Se não houver imagem, preenche o espaço todo do conteúdo com texto
		if (picture == null) {
			TextRect textRect = new TextRect(paint);
			textRect.prepare(description, (int) (width * .9), (int) (height * .7));
			textRect.draw(canvas, 5, (captionSize + 6));
		}
		// Senão posiciona a imagem a esquerda do cartão e preenche o restante
		// com texto
		else {
			canvas.drawBitmap(picture, 5, (captionSize + 7), paint);
			TextRect textRect = new TextRect(paint);
			textRect.prepare(description, (int) (width - (picture.getWidth() + 10)), (int) (height * .7));
			textRect.draw(canvas, (picture.getWidth() + 10), (captionSize + 6));
		}
	}

	/**
	 * Retorna um Bitmap redimensionado de acordo com o tamanho máximo
	 * especificado pelo usuário
	 * 
	 * @param realImage
	 *            Bitmap para ser redimensionado
	 * @param maxImageSize
	 *            tamanho máximo que o Bitmap irá possuir
	 * @param filter
	 *            true se o código fonte deve ser filtrado
	 * 
	 * @return imagem redimensionada
	 */
	private Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
		float ratio = Math.min((float) maxImageSize / realImage.getWidth(),
				(float) maxImageSize / realImage.getHeight());
		int width = Math.round((float) ratio * realImage.getWidth());
		int height = Math.round((float) ratio * realImage.getHeight());

		Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width, height, filter);
		return newBitmap;
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
		invalidate();
	}

	public String getCaption() {
		return caption;
	}

	public void setCaption(String caption) {
		this.caption = caption;
		invalidate();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
		invalidate();
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
		invalidate();
	}

	public Bitmap getPicture() {
		return picture;
	}

	public void setPicture(Bitmap picture) {
		this.picture = picture;
		invalidate();
	}

	public int getColorHeader1() {
		return colorHeader1;
	}

	public void setColorHeader1(int colorHeader1) {
		this.colorHeader1 = colorHeader1;
		invalidate();
	}

	public int getColorHeader2() {
		return colorHeader2;
	}

	public void setColorHeader2(int colorHeader2) {
		this.colorHeader2 = colorHeader2;
		invalidate();
	}

	public int getColorCard1() {
		return colorCard1;
	}

	public void setColorCard1(int colorCard1) {
		this.colorCard1 = colorCard1;
		invalidate();
	}

	public int getColorCard2() {
		return colorCard2;
	}

	public void setColorCard2(int colorCard2) {
		this.colorCard2 = colorCard2;
		invalidate();
	}

	public int getCaptionSize() {
		return captionSize;
	}

	public void setCaptionSize(int captionSize) {
		this.captionSize = captionSize;
		invalidate();
	}

	public int getRoundBorder() {
		return roundBorder;
	}

	public void setRoundBorder(int roundBorder) {
		this.roundBorder = roundBorder;
		invalidate();
	}

	public int getColorCaption() {
		return colorCaption;
	}

	public void setColorCaption(int colorCaption) {
		this.colorCaption = colorCaption;
		invalidate();
	}

	public int getColorDescription() {
		return colorDescription;
	}

	public void setColorDescription(int colorDescription) {
		this.colorDescription = colorDescription;
		invalidate();
	}

	public void setWidth(int width) {
		this.width = width;
		invalidate();
	}

	public void setHeight(int height) {
		this.height = height;
		invalidate();
	}

}
