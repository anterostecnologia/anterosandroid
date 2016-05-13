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


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;

import br.com.anteros.android.persistence.R;
import br.com.anteros.android.persistence.sql.jdbc.SQLiteConnection;
import br.com.anteros.android.persistence.sql.jdbc.SQLiteResultSet;
import br.com.anteros.persistence.session.SQLSession;

/**
 * Created by edson on 12/05/16.
 */
public abstract class DatabaseMaintenanceFragment extends Fragment {

    private SQLiteConnection connection;
    private Cursor cursor;
    private CursorAdapter adapter;
    private ListView lvTables;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.databasemaintenance, null);

        lvTables = (ListView) view.findViewById(R.id.listview_database);
        try {
            connection = (SQLiteConnection) getSQLSession().getConnection();
        } catch (Exception e) {
        }
        cursor = connection
                .getDatabase()
                .rawQuery(
                        "SELECT name as _id FROM sqlite_master WHERE type='table' ORDER BY name;",
                        null);

        adapter = new CursorAdapter(getActivity(), cursor) {

            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                LayoutInflater inflater = LayoutInflater.from(context);
                View view = inflater.inflate(android.R.layout.simple_list_item_1,
                        parent, false);
                bindView(view, context, cursor);
                return view;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                TextView lbTabela = (TextView) view
                        .findViewById(android.R.id.text1);
                try {
                    lbTabela.setText(getObjectValue(cursor, 0) + "");
                } catch (SQLException e) {
                    e.printStackTrace();
                    lbTabela.setText("");
                }
            }

        };

        lvTables.setAdapter(adapter);
        lvTables.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> adapterView,
                                           View view, int position, long id) {
                String table = "";

                try {
                    table = getObjectValue((Cursor) adapter.getItem(position), 0) + "";
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                RecordsOfTableActivity.setData(table, getSQLSession());

                startActivity(new Intent(getActivity(),
                        RecordsOfTableActivity.class));

                return false;
            }
        });

        return view;
    }

    private Object getObjectValue(Cursor cursor, int columnIndex) throws SQLException {
        switch (SQLiteResultSet.getDataType((SQLiteCursor) cursor, columnIndex)) {
            case SQLiteResultSet.FIELD_TYPE_INTEGER:
                long val = cursor.getLong(columnIndex);
                if (val > Integer.MAX_VALUE || val < Integer.MIN_VALUE) {
                    return new Long(val);
                } else {
                    return new Integer((int) val);
                }
            case SQLiteResultSet.FIELD_TYPE_FLOAT:
                return new Double(cursor.getDouble(columnIndex));
            case SQLiteResultSet.FIELD_TYPE_BLOB:
                return cursor.getBlob(columnIndex);
            case SQLiteResultSet.FIELD_TYPE_NULL:
                return null;
            case SQLiteResultSet.FIELD_TYPE_STRING:
            default:
                return cursor.getString(columnIndex);
        }
    }

    public abstract SQLSession getSQLSession();


}