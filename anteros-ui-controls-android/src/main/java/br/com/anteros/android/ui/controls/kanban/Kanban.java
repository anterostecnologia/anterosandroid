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

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import br.com.anteros.android.ui.controls.R;

/**
 * 
 * @author Eduardo Albertini
 *
 */
public class Kanban extends ViewGroup {

	private KanbanHeader header;
	private KanbanContent content;
	private int cardSpacing;
	private int captionSize;
	private int cardWidth;
	private int cardHeight;
	private int headerSize;

	private List<KanbanClickListener> listenersClick = new ArrayList<KanbanClickListener>();
	private List<KanbanLongClickListener> listenersLongClick = new ArrayList<KanbanLongClickListener>();

	public Kanban(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.Kanban, 0, 0);

		try {
			if (attributes != null) {
				this.cardSpacing = attributes.getInteger(R.styleable.Kanban_kanbanCardSpacing, 3);
				this.captionSize = attributes.getInteger(R.styleable.Kanban_kanbanCaptionSize, 20);
				this.cardWidth = attributes.getInteger(R.styleable.Kanban_kanbanCardWidth, 50);
				this.cardHeight = attributes.getInteger(R.styleable.Kanban_kanbanCardHeight, 50);
				this.headerSize = attributes.getInteger(R.styleable.Kanban_kanbanHeaderSize, 25);

			}
		} finally {
			if (attributes != null)
				attributes.recycle();
		}
	}

	public Kanban(Context context) {
		super(context);

		this.header = new KanbanHeader(context);
		this.content = new KanbanContent(context);
		this.cardSpacing = 3;
		this.captionSize = 20;
		this.cardWidth = 50;
		this.cardHeight = 50;
		this.headerSize = 25;

	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			if (view instanceof KanbanHeader) {
				header = (KanbanHeader) view;
			} else if (view instanceof KanbanContent) {
				content = (KanbanContent) view;
			}
		}
		final Kanban k = this;

		final GestureDetector gestureDetector = new GestureDetector(getContext(),
				new GestureDetector.SimpleOnGestureListener() {
					KanbanCard card;

					@Override
					public void onLongPress(MotionEvent e) {
						System.out.println("LONG PRESS");
						card = k.getCardAtXY(e.getRawX(), e.getRawY());
						if (card != null) {
							for (KanbanLongClickListener listener : listenersLongClick) {
								listener.onCardLongClick(card);
							}
						}
					}

					@Override
					public boolean onSingleTapUp(MotionEvent e) {
						System.out.println("PRESS");
						card = k.getCardAtXY(e.getRawX(), e.getRawY());
						if (card != null) {
							for (KanbanClickListener listener : listenersClick) {
								listener.onCardClick(card);
							}
						}
						return false;
					}

				});

		content.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int heigthMeasure = 0;
		float rowCount = 0;
		for (int i = 0; i < content.getChildCount(); i++) {
			View view = content.getChildAt(i);
			if (view instanceof KanbanColumn) {
				int width = 0;
				KanbanColumn column = (KanbanColumn) view;
				if (column.getChildCount() > 0) {

					// Se o usuário definiu o tamanho da coluna em pixel recebe
					// esse
					// valor, se não definiu em pixels, recebe o valor calculado
					// pelo valor definido por porcentagem
					if (column.getLayoutParams().width > 0)
						width = column.getLayoutParams().width;
					else
						width = (int) (getResources().getDisplayMetrics().widthPixels * column.getColumnWidthPercent() / 100);

					// Calcula a quantidade de cartões das colunas. Calculo
					// usado para definir a altura do KanbanContent
					if (cardWidth > 0) {
						float columnCount = 1;
						columnCount = width / cardWidth;
						rowCount = column.getChildCount() / columnCount;
						if ((width % cardWidth) != 0)
							rowCount++;
					}

					// Calcula a altura da coluna. Obs: rowCount + 1 para sempre
					// sobrar espaço abaixo da ultima para fazer scroll.
					int measure = (int) (rowCount * cardSpacing + (rowCount + 1) * cardHeight + cardSpacing * 2);

					// Se a altura da coluna atual for maior que a altura da
					// coluna anterior, a altura do KanbanContent é atualizado
					if (measure > heigthMeasure)
						heigthMeasure = measure;
				}
			}
		}
		// Se o tamanho da tela é menor que o tamanho do KanbanContent, atualiza
		// o tamanho da tela
		if (getResources().getDisplayMetrics().heightPixels < heigthMeasure) {
			setMeasuredDimension(getResources().getDisplayMetrics().widthPixels, heigthMeasure + header.getHeight());
		}
	}

	/**
	 * Retorna o KanbanCard presente nas coordenadas X e Y passadas.
	 * 
	 * @param x
	 *            o valor da coordenada do eixo X
	 * @param y
	 *            o valor da coordenada do eixo Y
	 * 
	 * @return o KanbanCard presente na posição XY se houver um KanbanCard nas
	 *         coordenadas ou null se não houver um KanbanCard nas coordenadas
	 *         clicada.
	 * */
	public KanbanCard getCardAtXY(float x, float y) {
		if (getChildCount() > 1 && getChildAt(1) instanceof KanbanContent) {
			KanbanContent content = (KanbanContent) getChildAt(1);
			for (int i = 0; i < content.getChildCount(); i++) {
				KanbanColumn column = (KanbanColumn) content.getChildAt(i);
				for (int j = 0; j < column.getChildCount(); j++) {
					KanbanCard card = (KanbanCard) column.getChildAt(j);

					if (isPointInsideView(x, y, card)) {
						return card;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Retorna se as coordenadas XY passadas estão instercepta a view.
	 * 
	 * @param x
	 *            o valor da coordenada do eixo X
	 * @param y
	 *            o valor da coordenada do eixo Y
	 * @param view
	 *            a view para verificar se está sendo interceptada pelas
	 *            coordenadas XY
	 * 
	 * @return retorna true se as coordenadas XY interceptam a view ou false se
	 *         não interceptam
	 * */
	private boolean isPointInsideView(float x, float y, View view) {
		int location[] = new int[2];
		view.getLocationOnScreen(location);
		int viewX = location[0];
		int viewY = location[1];

		if ((x > viewX && x < (viewX + view.getWidth())) && (y > viewY && y < (viewY + view.getHeight())))
			return true;
		else
			return false;

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		header.layout(0, 0, getWidth(), headerSize);
		content.layout(0, header.getBottom(), getWidth(), getHeight());
	}

	public void addClickListener(KanbanClickListener listener) {
		listenersClick.add(listener);
	}

	public void removeClickListener(KanbanClickListener listener) {
		listenersClick.remove(listener);
	}

	public void addLongClickListener(KanbanLongClickListener listener) {
		listenersLongClick.add(listener);
	}

	public void removeLongClickListener(KanbanLongClickListener listener) {
		listenersLongClick.remove(listener);
	}

	public int getCardSpacing() {
		return cardSpacing;
	}

	public void setCardSpacing(int cardSpacing) {
		this.cardSpacing = cardSpacing;
		invalidate();
	}

	public int getCaptionSize() {
		return captionSize;
	}

	public void setCaptionSize(int captionSize) {
		this.captionSize = captionSize;
		invalidate();
	}

	public int getCardWidth() {
		return cardWidth;
	}

	public void setCardWidth(int cardWidth) {
		this.cardWidth = cardWidth;
		invalidate();
	}

	public int getCardHeight() {
		return cardHeight;
	}

	public void setCardHeight(int cardHeight) {
		this.cardHeight = cardHeight;
		invalidate();
	}

	public int getHeaderSize() {
		return headerSize;
	}

	public void setHeaderSize(int headerSize) {
		this.headerSize = headerSize;
		invalidate();
	}

	// Listeners
	public interface KanbanClickListener {
		public void onCardClick(KanbanCard card);
	}

	public interface KanbanLongClickListener {
		public void onCardLongClick(KanbanCard card);
	}
}
