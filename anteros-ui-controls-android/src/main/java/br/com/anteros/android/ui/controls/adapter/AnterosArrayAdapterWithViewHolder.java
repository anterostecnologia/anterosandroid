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

package br.com.anteros.android.ui.controls.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by edson on 15/05/16.
 */
public abstract class AnterosArrayAdapterWithViewHolder<T> extends ArrayAdapter<T>{
    public AnterosArrayAdapterWithViewHolder(Context context, int resource) {
        super(context, resource);
    }

    public AnterosArrayAdapterWithViewHolder(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public AnterosArrayAdapterWithViewHolder(Context context, int resource, T[] objects) {
        super(context, resource, objects);
    }

    public AnterosArrayAdapterWithViewHolder(Context context, int resource, int textViewResourceId, T[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public AnterosArrayAdapterWithViewHolder(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
    }

    public AnterosArrayAdapterWithViewHolder(Context context, int resource, int textViewResourceId, List<T> objects) {
        super(context, resource, textViewResourceId, objects);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        List<T> beans = geBeans();
        View row = getRowView(position,convertView,parent);
        T currentBean = beans.get(position);
        AnterosViewHolder viewHolder = new AnterosViewHolder(currentBean);
        Set<View> viewsToHolderController = getViewsToHolderController(row);
        if (viewsToHolderController!=null) {
           for (View vc : viewsToHolderController){
               viewHolder.putView(vc.getId(),vc, beans.get(position));
           }
        }
        
        viewHolder.configure();

        loadValuesFromCurrentBean(viewHolder,currentBean);

        row.setTag(viewHolder);

        return row;
    }

    public abstract void loadValuesFromCurrentBean(AnterosViewHolder viewHolder, T currentBean);

    public abstract Set<View> getViewsToHolderController(View row);

    public abstract List<T> geBeans();

    public abstract View getRowView(int position, View convertView, ViewGroup parent);

    public abstract void onTextChanged(View view, String text, AnterosViewHolder viewHolder);

    public abstract void onAfterTextChanged(View view, String text, AnterosViewHolder viewHolder);

    public abstract void onClickView(View view, AnterosViewHolder viewHolder);


    public class AnterosViewHolder {

        private Map<Integer,View> views = new LinkedHashMap<>();

        private final T bean;

        public AnterosViewHolder(T bean){
            this.bean = bean;
        }

        public AnterosViewHolder putView(Integer key, View view, Object tag){
            view.setTag(tag);
            views.put(key,view);
            return this;
        }

        public void configure() {
            for (View view : views.values()) {
                if (view instanceof EditText) {
                    ((EditText) view).addTextChangedListener(new EditTextWatcher(this, view));
                }

                view.setOnClickListener(new ViewClick(this, view));
            }

        }

        public View getViewById(Integer id) {
            return views.get(id);
        }

        public T getBean() {
            return bean;
        }

        public boolean viewEqualsTo(View view, Integer id) {
            return (view == this.getViewById(id));
        }
    }


    private class EditTextWatcher implements TextWatcher {

        private final AnterosViewHolder viewHolder;
        private final View view;

        public EditTextWatcher(AnterosViewHolder viewHolder, View view) {
            this.viewHolder = viewHolder;
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            AnterosArrayAdapterWithViewHolder.this.onTextChanged(view, s.toString(), viewHolder);
        }

        @Override
        public void afterTextChanged(Editable s) {
           AnterosArrayAdapterWithViewHolder.this.onAfterTextChanged(view, s.toString(), viewHolder);
        }
    }

    private class ViewClick implements View.OnClickListener {

        private final AnterosViewHolder viewHolder;
        private final View view;

        public ViewClick(AnterosViewHolder viewHolder, View view) {
            this.viewHolder = viewHolder;
            this.view = view;
        }

        @Override
        public void onClick(View v) {
            AnterosArrayAdapterWithViewHolder.this.onClickView(view, viewHolder);
        }
    }




}
