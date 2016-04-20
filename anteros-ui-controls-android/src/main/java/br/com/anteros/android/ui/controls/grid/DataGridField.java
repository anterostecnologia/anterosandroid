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

import android.graphics.Rect;

public class DataGridField {

	private Object value;
	private Rect rect;
	private Rect dragRect;
	private boolean selected = false;
	private int rowNumber;
	private int colNumber;
	private DataGridColumn column;

	public Object getValue() {
		return value;
	}

	public DataGridField setValue(Object value) {
		this.value = value;
		return this;
	}

	public Rect getRect() {
		return rect;
	}

	public DataGridField setRect(Rect rect) {
		this.rect = rect;
		return this;
	}

	public Rect getDragRect() {
		return dragRect;
	}

	public DataGridField setDragRect(Rect dragRect) {
		this.dragRect = dragRect;
		return this;
	}

	public boolean isSelected() {
		return selected;
	}

	public DataGridField setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}

	public int getRowNumber() {
		return rowNumber;
	}

	public DataGridField setRowNumber(int rowNumber) {
		this.rowNumber = rowNumber;
		return this;
	}

	public int getColNumber() {
		return colNumber;
	}

	public DataGridField setColNumber(int colNumber) {
		this.colNumber = colNumber;
		return this;
	}

	public DataGridColumn getColumn() {
		return column;
	}

	public DataGridField setColumn(DataGridColumn column) {
		this.column = column;
		return this;
	}

	public int getWidth() {
		if (column != null)
			return column.getWidth();
		return 0;
	}

	public boolean isVisible() {
		if (column != null)
			return column.isVisible();
		return false;
	}

	public String getFieldName() {
		if (column == null)
			return "";
		return column.getColumnName();
	}

	@Override
	public String toString() {
		if (rect != null)
			return "Field Name=" + column.getColumnName() + ", Field Value="
					+ getValue() + ", Width=" + getWidth() + ", Row Number="
					+ rowNumber + ", Col Number=" + colNumber + ", Left="
					+ rect.left + ", Top=" + rect.top + ", Right=" + rect.right
					+ ", Bottom=" + rect.bottom;
		else
			return "Field Name=" + column.getColumnName() + ", Field Value="
					+ getValue() + ", Width=" + getWidth() + ", Row Number="
					+ rowNumber + ", Col Number=" + colNumber;
	}
}
