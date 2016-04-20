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
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author Eduardo Albertini
 *
 */
public class KanbanContent extends ViewGroup {

	private float downYValue = 0;

	public KanbanContent(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public KanbanContent(Context context) {
		super(context);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		int itemWidth = 0;
		int itemAnteriorRight = 0;

		for (int i = 0; i < this.getChildCount(); i++) {
			KanbanColumn kanBanColumn = new KanbanColumn(getContext());
			if (getChildAt(i) instanceof KanbanColumn) {
				kanBanColumn = (KanbanColumn) getChildAt(i);

				// Obtém a coluna anterior
				try {
					KanbanColumn itemAnterior = (KanbanColumn) getChildAt(i - 1);
					itemAnteriorRight = itemAnterior.getRight();
				} catch (Exception ex) {
				}

				// Se o usuário definiu o tamanho da coluna em pixel recebe esse
				// valor, se não definiu em pixels, recebe o valor calculado
				// pelo valor definido por porcentagem
				if (kanBanColumn.getLayoutParams().width > 0)
					itemWidth = kanBanColumn.getLayoutParams().width;
				else
					itemWidth = (int) (getWidth() * kanBanColumn.getColumnWidthPercent() / 100);

				// Se for a ultima coluna e o valor total das colunas for menor
				// que o tamanho da tela, a ultima coluna recebe o tamanho que
				// falta para completar a tela
				if (i == getChildCount() - 1)
					if ((itemAnteriorRight + itemWidth) < getWidth())
						itemWidth = getWidth() - itemAnteriorRight;

				kanBanColumn.layout(itemAnteriorRight, 0, itemAnteriorRight + itemWidth, b - t);
			}

			// if ((itemAnteriorRight + itemWidth) > getWidth()) {
			// throw new
			// IllegalArgumentException("O tamanho total das colunas [" +
			// (itemAnteriorRight + itemWidth)
			// + "] excede o tamanho total do KanBan [" + getWidth() + "]");
			// }
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Kanban kanban = new Kanban(getContext());
		if (getParent() instanceof Kanban)
			kanban = (Kanban) getParent();

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			// Se a tela foi pressionada, guarda a coordenada Y
			downYValue = event.getY();
			break;

		case MotionEvent.ACTION_MOVE:
			int index = 0;
			float diff;
			int rolagem = 0;
			if (event.getY() < downYValue) {
				// DESCENDO

				int visibleHeigth = 0;
				if (getParent().getParent() != null)
					visibleHeigth = ((View) getParent().getParent()).getHeight();
				else
					visibleHeigth = getRootView().getHeight();
				
				// Se a quantidade que já foi rolada mais o tamanho do cartão
				// for menor que a o tamanho da tela que falta para mostrar,
				// calcula quanto fazer scroll
				if (getScrollY() + kanban.getCardHeight() < getHeight() - visibleHeigth) {
					diff = downYValue - event.getY();
					index = (int) (diff / (kanban.getCardHeight() + kanban.getCardSpacing()));
					if (index > 0)
						index = 1;
					rolagem = index * (kanban.getCardHeight() + kanban.getCardSpacing());
				}

			} else if (event.getY() > downYValue) {
				// SUBINDO

				// Se a quantidade que já foi rolada menos o tamanho do cartão
				// for maior que zero (chegou no topo), calcula quanto fazer
				// scroll.
				if (getScrollY() - kanban.getCardHeight() > 0) {
					diff = event.getY() - downYValue;
					index = (int) (diff / (kanban.getCardHeight() + kanban.getCardSpacing()));
					if (index > 0)
						index = 1;
					rolagem = -(index * (kanban.getCardHeight() + kanban.getCardSpacing()));
				}

			}
			// Faz scroll
			scrollBy(0, rolagem);

			if (index > 0)
				downYValue = event.getY();

			break;
		}

		return true;
	}
}
