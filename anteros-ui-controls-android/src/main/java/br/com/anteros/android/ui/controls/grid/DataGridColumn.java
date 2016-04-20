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
import br.com.anteros.android.core.util.CanvasUtils;

public class DataGridColumn {

	public static int TEXT = 0;
	public static int INTEGER = 1;
	public static int NUMBER = 2;
	public static int DATE = 3;
	public static int DATETIME = 4;
	public static int TIME = 5;
	public static int SELECTED = 6; 
	public static int IMAGE = 7;

	private String headerText;
	private int width;
	private String columnName;
	private boolean visible;
	private Rect rect;
	private int dataType = TEXT;
	private String format;
	private boolean sort=false;
	private boolean sortDesc=false;
	private int alignVertical = CanvasUtils.VCENTER;
	private int alignHorizontal = CanvasUtils.LEFT;

	public DataGridColumn(String columnName, String headerText, int width,
			boolean visible) {
		this.headerText = headerText;
		this.columnName = columnName;
		this.width = width;
		this.visible = visible;
	}
	
	public DataGridColumn(String columnName, String headerText, int width,
			boolean visible, boolean sort, boolean sortDesc) {
		this.headerText = headerText;
		this.columnName = columnName;
		this.width = width;
		this.visible = visible;
		this.sort = sort;
		this.sortDesc = sortDesc;
	}
	
	public DataGridColumn(String columnName, String headerText, int width,
			boolean visible, int dataType) {
		this(columnName, headerText, width, visible);
		this.dataType = dataType;
	}
	
	public DataGridColumn(String columnName, String headerText, int width,
			boolean visible, int dataType, String format) {
		this(columnName, headerText, width, visible);
		this.dataType = dataType;
		this.format = format;
	}

	public DataGridColumn(String columnName, String headerText, int width,
			boolean visible, int dataType, String format, int alignVertical, int alignHorizontal) {
		this(columnName, headerText, width, visible);
		this.dataType = dataType;
		this.format = format;
		this.alignVertical = alignVertical;
		this.alignHorizontal = alignHorizontal;
	}

	public String getHeaderText() {
		return headerText;
	}

	public void setHeaderText(String headerText) {
		this.headerText = headerText;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((columnName == null) ? 0 : columnName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataGridColumn other = (DataGridColumn) obj;
		if (columnName == null) {
			if (other.columnName != null)
				return false;
		} else if (!columnName.equals(other.columnName))
			return false;
		return true;
	}

	public Rect getRect() {
		return rect;
	}

	public void setRect(Rect rect) {
		this.rect = rect;
	}

	public int getAlignVertical() {
		return alignVertical;
	}

	public void setAlignVertical(int alignVertical) {
		this.alignVertical = alignVertical;
	}

	public int getAlignHorizontal() {
		return alignHorizontal;
	}

	public void setAlignHorizontal(int alignHorizontal) {
		this.alignHorizontal = alignHorizontal;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}

	public boolean isSort() {
		return sort;
	}

	public void setSort(boolean sort) {
		this.sort = sort;
	}

	public boolean isSortDesc() {
		return sortDesc;
	}

	public void setSortDesc(boolean sortDesc) {
		this.sortDesc = sortDesc;
	}
}
