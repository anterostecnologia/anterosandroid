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

package br.com.anteros.android.ui.controls.currencyEditText;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Locale;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import br.com.anteros.android.ui.controls.R;


public class CurrencyEditText extends EditText implements TextWatcher {

	private String current = "";
	private int precision;
	private boolean printDollar;
	private DecimalFormat decimalFormat;
	private boolean initialized = false;
	private boolean editingBefore;
	private boolean editingOnChanged;
	private boolean editingAfter;
	private boolean onlyPositive;

	/**
	 * 
	 * @param context
	 * @param precision
	 * @param printDollar
	 * @param locale
	 *            - pt_BR, en_US
	 */
	public CurrencyEditText(Context context, int precision,
			boolean printDollar, String locale) {
		super(context);
		init();
		this.precision = precision;
		this.printDollar = printDollar;
		this.decimalFormat = createNumberFormat(locale);
		this.initialized = true;
	}

	public CurrencyEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();

		TypedArray attributes = context.obtainStyledAttributes(attrs,
				R.styleable.CurrencyEditText);
		printDollar = attributes.getBoolean(
				R.styleable.CurrencyEditText_printDollar, false);

		precision = attributes.getInteger(
				R.styleable.CurrencyEditText_precision, 2);

		decimalFormat = createNumberFormat(attributes
				.getString(R.styleable.CurrencyEditText_locale));

		onlyPositive = attributes.getBoolean(
				R.styleable.CurrencyEditText_onlyPositive, false);

		initialized = true;
	}

	private void init() {
		setRawInputType(Configuration.KEYBOARD_12KEY);
		addTextChangedListener(this);
		// Ignoring enter key presses
		// setOnEditorActionListener(new OnEditorActionListener() {
		// @Override
		// public boolean onEditorAction(TextView v, int actionId,
		// KeyEvent event) {
		// return true;
		// }
		// });
	}

	@Override
	public void afterTextChanged(Editable s) {
		if (!editingAfter && editingBefore && editingOnChanged) {
			editingAfter = true;
			// Log.v("afterTextChanged", s + "");
			editingBefore = false;
			editingOnChanged = false;
			editingAfter = false;
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

		if (!editingBefore) {
			editingBefore = true;
			// Log.v("beforeTextChanged", s + "");
		}
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (!editingOnChanged && editingBefore) {
			editingOnChanged = true;
			removeTextChangedListener(this);
			// Log.v("onTextChanged", "s " + s + " start " + start + " count "
			// + count + " before " + before);
			if (initialized) {
				String text = s.toString();
				if (text.length() > 0) {
					if (isTypedCorrectly(text, start, count, before)) {
						char lastChar = text.charAt(text.length() - 1);

						if (Character.isDigit(lastChar)) {
							addNewDigit(text, start, before, count);
						} else if (lastChar == '-' || lastChar == '+') {
							checkNegativeOrPositiveNumber(lastChar);
						} else {
							ignoreChange();
						}
					} else {
						setText(current);
						setSelection(current.length());
					}
				} else {
					current = text;
					setText(text);
				}
			}
			addTextChangedListener(this);
		}
	}

	private void ignoreChange() {
		setText(current);
		setSelection(current.length());
	}

	private void checkNegativeOrPositiveNumber(char lastChar) {
		double parsed = 0;
		try {
			parsed = decimalFormat.parse(current).doubleValue();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (onlyPositive == false && lastChar == '-' && parsed > 0) {
			parsed = parsed * -1;
		} else if (lastChar == '+' && parsed < 0) {
			parsed = parsed * -1;
		}
		String formated = decimalFormat.format(parsed);

		current = formated;

		setText(formated);

		setSelection(formated.length());
	}

	/**
	 * Adiciona novo digito digitado
	 * 
	 * @param text
	 * @param start
	 * @param before
	 * @param count
	 */
	private void addNewDigit(String text, int start, int before, int count) {
		text = checkLocale(text);
		double parsed = 0;
		try {
			// verifica se é o primeiro caractere digitado
			if (current.length() == 0 && text.length() == 1) {
				parsed = parseFirstCaracterTyped(text);
			} else {
				parsed = decimalFormat.parse(text).doubleValue();
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// Log.v("parsed", parsed + "");

		String formated;

		// verifica se é necessário andar casas com a virgula
		if (start != 0 && current.length() != text.length()) {
			if (count > before) {
				formated = decimalFormat.format(parsed * 10);
			} else {
				formated = decimalFormat.format(parsed / 10);
			}
		} else {
			formated = decimalFormat.format(parsed);
		}

		current = formated;

		setText(formated);

		setSelection(formated.length());
	}

	private double parseFirstCaracterTyped(String text) throws ParseException {
		String textZero = decimalFormat.format(0);
		
		text = textZero.substring(0, textZero.length() - 1) + text;
			
		return decimalFormat.parse(text).doubleValue();
	}

	/**
	 * Verifica se o usuário digitou o texto na posição correta
	 * 
	 * @param text
	 * @param start
	 * @return
	 */
	private boolean isTypedCorrectly(String text, int start, int count,
			int before) {
		int length = text.length();
		if (start == 0 && count > 1) {
			return true;
		}
		if (start == 0 && before == 0) {
			return true;
		}
		if (start == length) {
			return true;
		}
		if ((start + 1) == length) {
			return true;
		}
		return false;
	}

	private String checkLocale(String string) {
		char decimalSeparator = decimalFormat.getDecimalFormatSymbols()
				.getDecimalSeparator();
		// Log.v("checkLocale", string);
		// Log.v("checkLocale", "decimalSeparator " + decimalSeparator);
		// precorre a String de traz para frente e verifica se o locale está
		// certo
		for (int i = string.length() - 1; i >= 0; i--) {
			char c = string.charAt(i);
			// Log.v("char", c + "");
			if (!Character.isDigit(c)) {
				if (c != decimalSeparator) {
					string = string.replace(decimalSeparator + "", "");
					string = string.replace(c + "", decimalSeparator + "");
				}
				break;
			}
		}
		// Log.v("checkLocale", string);
		return string;
	}

	/**
	 * Cria o NumberFormat com base no Locale informado no XML de leiaute.
	 * 
	 * @param string
	 * @return
	 */
	private DecimalFormat createNumberFormat(String string) {
		Locale locale = Locale.getDefault();
		if (string != null && string.length() != 0) {
			String[] tokens = string.split("_");
			if (tokens.length == 2) {
				try {
					locale = new Locale(tokens[0].toLowerCase(),
							tokens[1].toUpperCase());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		DecimalFormat nf;
		if (printDollar) {
			nf = (DecimalFormat) DecimalFormat.getCurrencyInstance(locale);
		} else {
			nf = (DecimalFormat) DecimalFormat.getNumberInstance(locale);
		}
		nf.applyPattern(createMask());
		return nf;
	}

	private String createMask() {
		StringBuilder sb = new StringBuilder();
		if (printDollar) {
			sb.append("¤ ");
		}
		sb.append("#,##0");
		if (precision > 0) {
			sb.append(".");
		}
		for (int i = 0; i < precision; i++) {
			sb.append("0");
		}
		// Log.v("mask", sb.toString());
		return sb.toString();
	}

	public Editable getTextWithoutMask() {
		Editable eText = super.getText();
		String text = eText.toString();

		double parsed = 0;
		try {
			parsed = decimalFormat.parse(text).doubleValue();
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return Editable.Factory.getInstance().newEditable(
				Double.toString(parsed));
	}
}
