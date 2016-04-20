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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import br.com.anteros.core.utils.DateUtil;

public class DataGridRecord implements Comparable<DataGridRecord> {

	private boolean selected;
	private DataGridModel dataGridModel;

	public static final int EQUIVALENT = 0;
	public static final int FOLLOWS = 1;
	public static final int PRECEDES = -1;

	public DataGridRecord(DataGridModel dataGridModel) {
		this.dataGridModel = dataGridModel;
	}

	private List<DataGridField> fields = new ArrayList<DataGridField>();

	public List<DataGridField> getFields() {
		return Collections.unmodifiableList(fields);
	}

	public DataGridField appendField() {
		DataGridField field = new DataGridField();
		fields.add(field);
		return field;
	}

	public void removeField(DataGridField field) {
		int index = fields.indexOf(field);
		this.removeField(index);
	}

	public void removeField(int index) {
		if (index >= 0) {
			fields.remove(index);
		}
	}

	public DataGridField getField(int index) {
		if ((fields != null) && (index < fields.size()))
			return fields.get(index);
		return null;
	}

	public DataGridField getFieldByName(String fieldName) {
		if (fields != null) {
			int len = fields.size();
			for (int i = 0; i < len; i++) {
				if (fields.get(i).getColumn() != null) {
					if (fieldName.equals(fields.get(i).getColumn()
							.getColumnName()))
						return fields.get(i);
				}
			}
		}
		return null;
	}

	public int fieldCount() {
		if (fields != null)
			return fields.size();
		return -1;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public void invertSelected() {
		if (selected)
			selected = false;
		else
			selected = true;
	}

	public int getSortFieldIndex() {
		for (int i = 0; i < dataGridModel.columnCount(); i++) {
			if (dataGridModel.getColumn(i).isSort()) {
				return i;
			}
		}
		return -1;
	}

	public int compareTo(DataGridRecord another) {
		int index = getSortFieldIndex();
		DataGridField field1 = this.getField(index);
		DataGridField field2 = another.getField(index);
		if (DataGridColumn.TEXT == field1.getColumn().getDataType()) {
			if (String.valueOf(field1.getValue()).compareTo(
					String.valueOf(field2.getValue())) > 0) {
				return (!field1.getColumn().isSortDesc() ? FOLLOWS : PRECEDES);
			}
			if (String.valueOf(field1.getValue()).compareTo(
					String.valueOf(field2.getValue())) < 0) {
				return (!field1.getColumn().isSortDesc() ? PRECEDES : FOLLOWS);
			}
		} else if (DataGridColumn.INTEGER == field1.getColumn().getDataType()) {
			if (Integer.parseInt(field1.getValue().toString()) > Integer
					.parseInt(field2.getValue().toString())) {
				return (!field1.getColumn().isSortDesc() ? FOLLOWS : PRECEDES);
			}

			if (Integer.parseInt(field1.getValue().toString()) < Integer
					.parseInt(field2.getValue().toString())) {
				return (!field1.getColumn().isSortDesc() ? PRECEDES : FOLLOWS);
			}
		} else if (DataGridColumn.NUMBER == field1.getColumn().getDataType()) {
			if (Double.parseDouble(field1.getValue().toString()) > Double
					.parseDouble(field2.getValue().toString())) {
				return (!field1.getColumn().isSortDesc() ? FOLLOWS : PRECEDES);
			}

			if (Double.parseDouble(field1.getValue().toString()) < Double
					.parseDouble(field2.getValue().toString())) {
				return (!field1.getColumn().isSortDesc() ? PRECEDES : FOLLOWS);
			}
		} else if (((DataGridColumn.DATE == field1.getColumn().getDataType()) || (DataGridColumn.DATETIME == field1
				.getColumn().getDataType()))) {
			Date data1 = null;
			Date data2 = null;
			if (field1.getValue() instanceof String) {
				data1 = DateUtil.stringToDateTime(field1.getValue().toString());
				data2 = DateUtil.stringToDateTime(field2.getValue().toString());
			} else {
				data1 = (Date) field1.getValue();
				data2 = (Date) field2.getValue();
			}

			if (data1.getTime() < data2.getTime()) {
				return (!field1.getColumn().isSortDesc() ? PRECEDES : FOLLOWS);
			}
			if (data1.getTime() > data2.getTime()) {
				return (!field1.getColumn().isSortDesc() ? FOLLOWS : PRECEDES);
			}
		}

		return 0;
	}
}
