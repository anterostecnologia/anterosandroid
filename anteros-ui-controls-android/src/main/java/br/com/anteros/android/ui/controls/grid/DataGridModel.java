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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import br.com.anteros.core.utils.ReflectionUtils;
import br.com.anteros.persistence.metadata.annotation.Column;
import br.com.anteros.persistence.metadata.annotation.type.TemporalType;
import br.com.anteros.persistence.sql.dialect.type.DateFormatter;

public class DataGridModel {

	public static final int REFRESH = 0;

	private List<DataGridRecord> records = new ArrayList<DataGridRecord>();
	private List<DataGridColumn> columns = new ArrayList<DataGridColumn>();

	public List<DataGridRecord> getRecords() {
		return Collections.unmodifiableList(records);
	}

	public DataGridModel(DataGridColumn[] columns) {
		this.columns = Arrays.asList(columns);
	}

	public DataGridModel(DataGridColumn[] columns, Object[][] data) throws Exception {
		this(columns);
		if (data.length > 0)
			if (columns.length != data[0].length)
				throw new Exception("Dados informados não possuem o mesmo número de Colunas informadas.");

		DataGridRecord newRecord;
		for (int i = 0; i < data.length; i++) {
			Object[] values = data[i];
			newRecord = this.appendRecord();
			DataGridField field;
			for (int j = 0; j < columns.length; j++) {
				field = newRecord.appendField();
				field.setColNumber(j);
				field.setColumn(columns[j]);
				field.setRowNumber(i);
				field.setValue(values[j]);
			}
		}

		if (this.getColumnSort() != null) {
			this.sortColumn(null, null, this.getColumnSort(), this.getColumnSort().isSortDesc());
		}
	}

	public DataGridModel(DataGridColumn[] columns, DataGridRecord[] data) throws Exception {
		this(columns);
		if (data.length > 0)
			if (columns.length != data[0].fieldCount())
				throw new Exception("Dados informados não possuem o mesmo número de Colunas informadas.");
		records = Arrays.asList(data);
	}

	public DataGridModel(DataGridColumn[] columns, List data, Class clazz) throws Exception {
		this(columns);
		if (data.size() > 0) {
			if (data.get(0) instanceof DataGridRecord)
				records = (List<DataGridRecord>) data;
			else {
				records = convertListToRecords(data.toArray(), clazz);
			}
		}
	}

	public DataGridModel(DataGridColumn[] columns, Object[] data, Class clazz) throws Exception {
		this(columns);
		if (data.length > 0) {
			if (data[0] instanceof DataGridRecord) {
				for (Object obj : data) {
					records.add((DataGridRecord) obj);
				}
			} else {
				records = convertListToRecords(data, clazz);
			}
		}
	}

	private Object getValueByField(Object object, Field[] fields, String columnName) throws Exception {
		for (Field field : fields) {
			field.setAccessible(true);
			if (field.isAnnotationPresent(Column.class)) {
				if (columnName.toUpperCase().equals(field.getAnnotation(Column.class).name().toUpperCase())) {
					return field.get(object);
				}
			} else if (columnName.toUpperCase().equals(field.getName().toUpperCase())) {
				return field.get(object);
			}
		}
		return null;
	}

	private List<DataGridRecord> convertListToRecords(Object[] data, Class clazz) throws Exception {
		ArrayList<DataGridRecord> result = new ArrayList<DataGridRecord>();
		int i = 0;
		Field[] fields = ReflectionUtils.getFieldsObjectReflection(clazz);
		for (Object obj : data) {
			DataGridRecord newRecord = new DataGridRecord(this);
			result.add(newRecord);
			DataGridField field;
			DataGridColumn column;
			for (int j = 0; j < columns.size(); j++) {
				column = columns.get(j);
				if (column.getDataType() == DataGridColumn.SELECTED) {
					field = newRecord.appendField();
					field.setColNumber(j);
					field.setColumn(column);
					field.setRowNumber(i);
					field.setValue(new Boolean(false));
				} else {
					Object value = getValueByField(obj, fields, column.getColumnName());
					if (value != null) {
						field = newRecord.appendField();
						field.setColNumber(j);
						field.setColumn(columns.get(j));
						field.setRowNumber(i);
						if (column.getDataType() == DataGridColumn.DATE) {
							field.setValue(new DateFormatter(TemporalType.DATE).parse((String) value));
						} else if (column.getDataType() == DataGridColumn.DATETIME) {
							field.setValue(new DateFormatter(TemporalType.DATE_TIME).parse((String) value));
						} else if (column.getDataType() == DataGridColumn.IMAGE) {
							field.setValue((byte[]) value);
						} else if (column.getDataType() == DataGridColumn.INTEGER) {
							field.setValue((Integer) value);
						} else if (column.getDataType() == DataGridColumn.NUMBER) {
							field.setValue((Float) value);
						} else if (column.getDataType() == DataGridColumn.TEXT) {
							field.setValue((String) value);
						} else if (column.getDataType() == DataGridColumn.TIME) {
							field.setValue(new DateFormatter(TemporalType.DATE_TIME).parse((String) value));
						}

					} else
						throw new Exception("Coluna " + columns.get(j).getColumnName()
								+ " não encontrada no Cursor passado como fonte de dados para a Grid.");
				}
			}
			i++;
		}
		return result;
	}

	private List<DataGridRecord> convertCursorToRecords(Cursor data) throws Exception {
		ArrayList<DataGridRecord> result = new ArrayList<DataGridRecord>();
		if (data.moveToFirst()) {
			for (int i = 0; i < data.getCount(); i++) {
				DataGridRecord newRecord = new DataGridRecord(this);
				result.add(newRecord);
				DataGridField field;
				DataGridColumn column;
				for (int j = 0; j < columns.size(); j++) {
					column = columns.get(j);
					if (column.getDataType() == DataGridColumn.SELECTED) {
						field = newRecord.appendField();
						field.setColNumber(j);
						field.setColumn(column);
						field.setRowNumber(i);
						field.setValue(new Boolean(false));
					} else {
						int columnIndex = data.getColumnIndex(column.getColumnName());
						if (columnIndex >= 0) {
							field = newRecord.appendField();
							field.setColNumber(j);
							field.setColumn(columns.get(j));
							field.setRowNumber(i);
							if (column.getDataType() == DataGridColumn.DATE) {
								field.setValue(new DateFormatter(TemporalType.DATE).parse(data.getString(columnIndex)));
							} else if (column.getDataType() == DataGridColumn.DATETIME) {
								field.setValue(new DateFormatter(TemporalType.DATE_TIME).parse(data
										.getString(columnIndex)));
							} else if (column.getDataType() == DataGridColumn.IMAGE) {
								field.setValue(data.getBlob(columnIndex));
							} else if (column.getDataType() == DataGridColumn.INTEGER) {
								field.setValue(data.getInt(columnIndex));
							} else if (column.getDataType() == DataGridColumn.NUMBER) {
								field.setValue(data.getFloat(columnIndex));
							} else if (column.getDataType() == DataGridColumn.TEXT) {
								field.setValue(data.getString(columnIndex));
							} else if (column.getDataType() == DataGridColumn.TIME) {
								field.setValue(new DateFormatter(TemporalType.DATE_TIME).parse(data
										.getString(columnIndex)));
							}

						} else
							throw new Exception("Coluna " + columns.get(j).getColumnName()
									+ " não encontrada no Cursor passado como fonte de dados para a Grid.");
					}
					if (!data.moveToNext())
						break;
				}
			}

		}
		return result;
	}

	public DataGridModel(DataGridColumn[] columns, Cursor data) throws Exception {
		this(columns);
		records = convertCursorToRecords(data);
	}

	public DataGridRecord appendRecord() {
		DataGridRecord record = new DataGridRecord(this);
		records.add(record);
		return record;
	}

	public void addRecord(DataGridRecord record) {
		records.add(record);
	}

	public void removeRecord(DataGridRecord record) {
		int index = records.indexOf(record);
		this.removeRecord(index);
	}

	public void removeRecord(int index) {
		if (index >= 0) {
			records.remove(index);
		}
	}

	public List<DataGridColumn> getColumns() {
		return Collections.unmodifiableList(columns);
	}

	public int recordCount() {
		if (records != null)
			return records.size();
		return 0;
	}

	public int columnCount() {
		if (columns != null)
			return columns.size();
		return 0;
	}

	public DataGridColumn getColumn(int index) {
		if ((columns != null) && (index < columns.size()))
			return columns.get(index);
		return null;
	}

	public DataGridColumn getColumnByName(String columnName) {
		if (columns != null) {
			int len = columns.size();
			for (int i = 0; i < len; i++) {
				if (columnName.equals(columns.get(i).getColumnName()))
					return columns.get(i);
			}
		}
		return null;
	}

	public DataGridRecord getRecord(int index) {
		if ((records != null) && (index < records.size()))
			return records.get(index);
		return null;
	}

	public Integer[] selectedIndexes() {
		ArrayList<Integer> result = new ArrayList<Integer>();
		for (int i = 0; i < records.size(); i++) {
			if (records.get(i).isSelected()) {
				result.add(i);
			}
		}
		return result.toArray(new Integer[] {});
	}

	public int selectedCount() {
		int result = 0;
		for (int i = 0; i < records.size(); i++) {
			if (records.get(i).isSelected()) {
				result++;
			}
		}
		return result;
	}

	public void selectAll() {
		for (int i = 0; i < records.size(); i++) {
			records.get(i).setSelected(true);
		}
	}

	public void unSelectAll() {
		for (int i = 0; i < records.size(); i++) {
			records.get(i).setSelected(false);
		}
	}

	public DataGridRecord[] selectedRecords() {
		ArrayList<DataGridRecord> result = new ArrayList<DataGridRecord>();
		for (int i = 0; i < records.size(); i++) {
			if (records.get(i).isSelected()) {
				result.add(records.get(i));
			}
		}
		return result.toArray(new DataGridRecord[] {});
	}

	public void invertSelectAllRecords() {
		if (selectedCount() > 0)
			unSelectAll();
		else
			selectAll();
	}

	public DataGridColumn getColumnSort() {
		for (int i = 0; i < this.columnCount(); i++) {
			if (this.getColumn(i).isSort())
				return this.getColumn(i);
		}
		return null;
	}

	public void sortColumn(Context context, final Handler handler, DataGridColumn column, boolean desc) {
		for (int i = 0; i < this.columnCount(); i++) {
			this.getColumn(i).setSort(false);
			this.getColumn(i).setSortDesc(false);
		}
		column.setSort(true);
		column.setSortDesc(desc);

		if (context == null) {
			Collections.sort(records);
		} else {
			final ProgressDialog dialog = ProgressDialog.show(context, "Ordenando", "Aguarde...", true);
			new Thread() {
				public void run() {
					try {
						Collections.sort(records);
						for (int i = 0; i < records.size(); i++) {
							for (int j = 0; j < records.get(i).getFields().size(); j++) {
								records.get(i).getField(j).setRowNumber(i);
							}
						}
						dialog.setMessage("Conclu�do...");
					} catch (Exception e) {
						e.printStackTrace();
					}
					handler.sendEmptyMessage(REFRESH);
					dialog.dismiss();
				}

			}.start();
		}

	}

}
