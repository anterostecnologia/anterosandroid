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

package br.com.anteros.android.ui.controls.maskEditText;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import br.com.anteros.android.ui.controls.R;

/**
 * @author Douglas Junior
 * 
 */
public class MaskedEditText extends EditText implements TextWatcher {

	private String mask;
	private char charRepresentation;
	private boolean editingBefore;
	private boolean editingOnChanged;
	private boolean editingAfter;
	private OnBeforeChangeListner beforeChangeListner;

	public MaskedEditText(Context context) {
		super(context);
		init();
	}

	public MaskedEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();

		TypedArray attributes = context.obtainStyledAttributes(attrs,
				R.styleable.MaskedEditText);
		mask = attributes.getString(R.styleable.MaskedEditText_mask);
		String representation = attributes
				.getString(R.styleable.MaskedEditText_char_representation);

		if (representation == null) {
			charRepresentation = '#';
		} else {
			charRepresentation = representation.charAt(0);
		}
	}

	public MaskedEditText(Context context, String mask, char charRepresentation) {
		super(context);
		init();
		this.mask = mask;

		if (Character.isDefined(charRepresentation)) {
			this.charRepresentation = '#';
		} else {
			this.charRepresentation = charRepresentation;
		}
	}

	public MaskedEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public String getMask() {
		return this.mask;
	}

	public void setCharRepresentation(char charRepresentation) {
		this.charRepresentation = charRepresentation;
	}

	public char getCharRepresentation() {
		return this.charRepresentation;
	}

	private void init() {
		addTextChangedListener(this);

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		if (!editingBefore) {
			editingBefore = true;

		}
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (!editingOnChanged && editingBefore) {
			editingOnChanged = true;
			removeTextChangedListener(this);
		//	Log.v("onTextChanged", s + "");
			String text = removeMask(s + "");
			if (beforeChangeListner != null)
				beforeChangeListner.onBeforeChange(text);
			applyMask(text);
			addTextChangedListener(this);
		}
	}

	private void applyMask(String textMaskless) {
		char[] textWithMask = mask.toCharArray();
		int validPosition = 0;
		int selection = -1;
		for (int i = 0; i < textWithMask.length; i++) {
			if (textWithMask[i] == charRepresentation) {
				if (selection == -1)
					selection = i;
				if (validPosition < textMaskless.length()) {
					char c = textMaskless.charAt(validPosition);
					if (Character.isDigit(c)) {
						textWithMask[i] = c;
						selection = i + 1;
					} else {
						textWithMask[i] = ' ';
					}
				} else {
					textWithMask[i] = ' ';
				}
				validPosition++;
			}
		}
		String text = new String(textWithMask);
		setText(text);
		setSelection(selection);
	}

	@Override
	public void afterTextChanged(Editable s) {
		if (!editingAfter && editingBefore && editingOnChanged) {
			editingAfter = true;

			editingBefore = false;
			editingOnChanged = false;
			editingAfter = false;
		}
	}

	@Override
	protected void onSelectionChanged(int selStart, int selEnd) {
		super.onSelectionChanged(selStart, selEnd);
	}

	public Editable getTextWithoutMask() {
		String text = getText().toString();

		text = removeMask(text);

		return Editable.Factory.getInstance().newEditable(text);
	}

	private String removeMask(String text) {
		for (int i = 0; i < mask.length(); i++) {
			char c = mask.charAt(i);
			if (c != charRepresentation) {
				text = text.replace(c + "", "");
			}
		}
		return text;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public void setOnBeforeChangeListner(OnBeforeChangeListner listner) {
		beforeChangeListner = listner;
	}

	public interface OnBeforeChangeListner {
		public void onBeforeChange(String text);
	}

}