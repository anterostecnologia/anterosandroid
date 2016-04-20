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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class WarningAlert extends CustomAlert implements View.OnClickListener {

	protected ImageView imageView;
	public String okText = "OK";
	private WarningListener listener;
	private Button btnOK;

	public WarningAlert(Context context, String title, String message) {
		this(context, title, message, null);
	}

	public WarningAlert(Context context, String title, String message, WarningListener listener) {
		super(context, title, message);

		this.listener = listener;

		imageView = (ImageView) findViewById(R.layout_alert.ic_alert);
		imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.alert_dialog_warning));
		imageView.setVisibility(View.VISIBLE);

		btnOK = (Button) findViewById(R.layout_alert.btnSim);
		btnOK.setText(okText);
		btnOK.setOnClickListener(this);
		btnOK.setVisibility(View.VISIBLE);
	}

	public void onClick(View view) {
		dismiss();
		if (listener != null)
			listener.onOkClick();
	}

	public interface WarningListener {
		void onOkClick();
	}
}