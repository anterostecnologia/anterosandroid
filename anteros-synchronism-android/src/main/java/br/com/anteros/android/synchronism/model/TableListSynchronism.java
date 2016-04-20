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

package br.com.anteros.android.synchronism.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import br.com.anteros.android.synchronism.communication.protocol.MobileResponse;
import br.com.anteros.core.utils.DateUtil;
import android.content.Context;

@SuppressWarnings("serial")
public class TableListSynchronism extends ArrayList<TableSynchronism> {
	
	public static final String FIELD_TABLE_NAME = "NM_TABELA";
	public static final String FIELD_DATE_TIME = "DH_ULT_ALTERACAO";
	

	public TableListSynchronism() {
	}

	public void save(Context context) throws Exception {
		File fileSynchronism = new File(context.getFilesDir().getPath() + "/tablesSynchronism.dat");
		FileOutputStream fos = new FileOutputStream(fileSynchronism);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(this);
	}

	public void load(Context context) throws Exception {
		File fileSynchronism = new File(context.getFilesDir().getPath() + "/tablesSynchronism.dat");
		FileInputStream fis = new FileInputStream(fileSynchronism);

		ObjectInputStream ois = new ObjectInputStream(fis);
		TableListSynchronism list = (TableListSynchronism) ois.readObject();
		if (list != null) {
			this.clear();
			this.addAll(list);
		}
	}

	public TableSynchronism getTableSynchronismByName(String tableName) {
		for (TableSynchronism table : this) {
			if (table.tableName.equals(tableName))
				return table;
		}
		return null;
	}

	public void processResponse(Context context, MobileResponse mobileResponse) throws Exception {
		TableSynchronism tableSynchronism = null;
		String[] fields = mobileResponse.getFields();
		int tableIndex = -1;
		int dhSynchronismIndex = -1;
		for (int z = 0; z < fields.length; z++) {
			if (fields[z].equalsIgnoreCase(FIELD_TABLE_NAME))
				tableIndex = z;
			if (fields[z].equalsIgnoreCase(FIELD_DATE_TIME))
				dhSynchronismIndex = z;
		}
		for (int i = 0; i < mobileResponse.getData().size(); i++) {
			String[] data = (String[]) mobileResponse.getData().get(i);
			if ((tableIndex != -1) && (dhSynchronismIndex != -1)) {
				tableSynchronism = this.getTableSynchronismByName(data[tableIndex]);
				if (tableSynchronism != null) 
					tableSynchronism.setDhSynchronismServer(DateUtil.stringToDateTime(data[dhSynchronismIndex]));
			}
		}
		save(context);
	}
}
