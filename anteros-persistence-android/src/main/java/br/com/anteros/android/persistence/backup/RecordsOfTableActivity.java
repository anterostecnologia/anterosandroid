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

package br.com.anteros.android.persistence.backup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.sql.ResultSetMetaData;

import br.com.anteros.android.persistence.R;
import br.com.anteros.android.persistence.sql.jdbc.SQLiteResultSet;
import br.com.anteros.persistence.session.SQLSession;

public class RecordsOfTableActivity extends AppCompatActivity {
	private static String table;
	private static SQLSession session;
	private ListView lvRegistros;
	private RecordsOfTableAdapter recordsOfTableAdapter;
	private SQLiteResultSet resultSet;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recordstable);


		try {
			resultSet = (SQLiteResultSet) session.createQuery("select *, rowid as _id from " + table).executeQuery();

			lvRegistros = (ListView) findViewById(R.id.recordstable_lvrecords);
			lvRegistros.setHorizontalScrollBarEnabled(true);
			
			
			ResultSetMetaData metaData = resultSet.getMetaData();
			for (int i = 1; i <= metaData.getColumnCount(); i++) {
			    System.out.println("COLUMN " + i + ": " + metaData.getColumnName(i));
			}

			recordsOfTableAdapter = new RecordsOfTableAdapter(this, resultSet.getCursor());
			lvRegistros.setAdapter(recordsOfTableAdapter);

			recordsOfTableAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void setData(String table, SQLSession session) {
		RecordsOfTableActivity.table = table;
		RecordsOfTableActivity.session = session;
	}

	@Override
	protected void onDestroy() {
		try {
			resultSet.close();
			session = null;
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		super.onDestroy();
	}

}
