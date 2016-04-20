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


public class QuestionAlert extends CustomAlert implements View.OnClickListener {

	private ImageView imageView;
	private Button btnPositive;
	private Button btnNegative;
	public String postitiveText = "Sim";
	public String negativeText = "Não";
	private QuestionListener listener;

	public QuestionAlert(Context context, String title, String message, QuestionListener listener) {
		super(context, title, message);

		this.listener = listener;

		imageView = (ImageView) findViewById(R.layout_alert.ic_alert);
		imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.alert_dialog_question));
		imageView.setVisibility(View.VISIBLE);

		btnPositive = (Button) findViewById(R.layout_alert.btnSim);
		btnPositive.setText(postitiveText);
		btnPositive.setOnClickListener(this);
		btnPositive.setVisibility(View.VISIBLE);

		btnNegative = (Button) findViewById(R.layout_alert.btnNao);
		btnNegative.setText(negativeText);
		btnNegative.setOnClickListener(this);
		btnNegative.setVisibility(View.VISIBLE);
	}

	public void onClick(View view) {
		if (view == btnNegative) {
			listener.onNegativeClick();
			dismiss();
		} else if (view == btnPositive) {
			listener.onPositiveClick();
			dismiss();
		}
	}

	public interface QuestionListener {
		void onPositiveClick();

		void onNegativeClick();
	}

}
