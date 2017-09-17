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

package br.com.anteros.android.ui.controls;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.anteros.core.utils.StringUtils;


public abstract class CustomAlert extends Dialog {

    protected TextView tvmensagem;
    protected ImageView imgAlert;

    public CustomAlert(Context context, String title, String message) {
        super(context);
        setCancelable(false);


        if (StringUtils.isBlank(title))
            requestWindowFeature(Window.FEATURE_NO_TITLE);
        else
            setTitle(title);

        setContentView(R.layout.alert_layout);

        tvmensagem = (TextView) findViewById(R.id.alert_layout_tvMensagem);
        tvmensagem.setText(message);

        imgAlert = (ImageView) findViewById(R.id.alert_layout_imageAlert);
    }

}