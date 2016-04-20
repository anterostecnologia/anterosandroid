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
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * 
 * @author Eduardo Albertini
 *
 */
public class KanbanHeader extends View {

	private KanbanContent content;

	public KanbanHeader(Context context) {
		super(context);
	}

	public KanbanHeader(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		Kanban kanban = (Kanban) getParent();
		for (int i = 0; i < kanban.getChildCount(); i++) {
			View view = kanban.getChildAt(i);
			if (view instanceof KanbanContent) {
				content = (KanbanContent) view;
			}
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		int itemWidth = 0;
		int itemAnteriorRight = 0;
		for (int i = 0; i < content.getChildCount(); i++) {
			KanbanColumn column = new KanbanColumn(getContext());
			if (content.getChildAt(i) instanceof KanbanColumn) {
				column = (KanbanColumn) content.getChildAt(i);

				// Obtém a coluna anterior
				try {
					KanbanColumn itemAnterior = (KanbanColumn) content.getChildAt(i - 1);
					itemAnteriorRight = itemAnterior.getRight();
				} catch (Exception ex) {
				}

				// Se o usuário definiu o tamanho da coluna em pixel recebe esse
				// valor, se não definiu em pixels, recebe o valor calculado
				// pelo valor definido por porcentagem
				if (column.getLayoutParams().width > 0) {
					itemWidth = column.getLayoutParams().width;
				} else {
					itemWidth = (int) (content.getWidth() * column.getColumnWidthPercent() / 100);
				}
				if (i == content.getChildCount() - 1)
					if ((itemAnteriorRight + itemWidth) < content.getWidth())
						itemWidth = content.getWidth() - itemAnteriorRight;

				column.drawHeader(canvas, itemAnteriorRight, 0, (itemAnteriorRight + itemWidth),
						((Kanban) content.getParent()).getHeaderSize());
			}

		}
	}

}
