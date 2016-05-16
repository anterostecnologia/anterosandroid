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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.Image;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import br.com.anteros.android.persistence.R;

public class RecordsOfTableAdapter extends CursorAdapter {
    public RecordsOfTableAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewGroup root = (ViewGroup) view.findViewById(R.id.recordstable_item_root);
        root.removeAllViews();

        for (int i = 0; i < cursor.getColumnCount() - 1; i++) {
            LinearLayout container = new LinearLayout(context);
            container.setLayoutParams(new LinearLayout.LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            container.setOrientation(LinearLayout.HORIZONTAL);

            TextView lbColuna = new TextView(context);
            lbColuna.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            lbColuna.setPadding(2, 2, 2, 2);
            lbColuna.setText(cursor.getColumnName(i));
            lbColuna.append(": ");
            lbColuna.setTypeface(null, Typeface.BOLD);


            String value = null;
            Bitmap bitmap=null;
            try {
                value = cursor.getString(i);
            } catch (Exception ex) {
                if ((ex.getMessage() + "").contains("BLOB")) {
                    byte[] blob = cursor.getBlob(i);
                    ByteArrayInputStream bais = new ByteArrayInputStream(blob);
                    try {
                        bitmap = BitmapFactory.decodeStream(bais);
                    } catch (Exception e){
                        value = new String(blob);
                    }
                } else {
                    ex.printStackTrace();
                }
            }
            if (bitmap!=null){
                ImageView imgView = new ImageView(context);
                imgView.setImageBitmap(bitmap);
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, context.getResources().getDisplayMetrics());
                int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, context.getResources().getDisplayMetrics());
                imgView.setLayoutParams(new LayoutParams(width, height));
                imgView.setPadding(2, 2, 2, 2);
                imgView.setId(i);
                container.addView(lbColuna);
                container.addView(imgView);
            } else {
                TextView lbValor = new TextView(context);
                lbValor.setText(value);
                lbValor
                        .setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                lbValor.setPadding(2, 2, 2, 2);
                lbValor.setId(i);
                container.addView(lbColuna);
                container.addView(lbValor);
            }

            root.addView(container);
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recordstableitem, parent, false);
        bindView(view, context, cursor);
        return view;
    }

}
