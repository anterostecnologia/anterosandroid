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

import android.content.Context;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;


public class PasswordAlert extends CustomAlert {

    private EditText edPassword;
    private OnPasswordListener passwordListener;


    public PasswordAlert(Context context, String title, String message) {
        super(context, title, message);

        imgAlert.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_alert_dialog_password));
        imgAlert.setVisibility(View.VISIBLE);

        edPassword = new EditText(context);
        edPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        LinearLayout contentLayout = (LinearLayout) findViewById(R.id.alert_layout_layoutContent);
        contentLayout.addView(edPassword);

        Button btnOk = (Button) findViewById(R.id.layout_alert_btnOk);
        btnOk.setVisibility(View.VISIBLE);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordListener != null)
                    passwordListener.OnPasswordOk(edPassword.getText() + "");
            }
        });

        Button btnCancel = (Button) findViewById(R.id.layout_alert_btnCancel);
        btnCancel.setVisibility(View.VISIBLE);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passwordListener != null)
                    passwordListener.OnPasswordCancel();
                dismiss();

            }
        });

        setCancelable(false);
    }

    public OnPasswordListener getOnPasswordListener() {
        return passwordListener;
    }

    public void setOnPasswordListener(OnPasswordListener passwordListener) {
        this.passwordListener = passwordListener;
    }

    public interface OnPasswordListener {
        void OnPasswordOk(String pass);

        void OnPasswordCancel();
    }
}
