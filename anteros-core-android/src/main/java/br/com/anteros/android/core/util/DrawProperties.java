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

package br.com.anteros.android.core.util;

import android.graphics.Typeface;

public class DrawProperties {

	public int color;
	public int fontColor;
	public int fontSize;
	public Typeface fontType;

	public DrawProperties() {

	}

	public DrawProperties(int color, int fontColor, int fontSize,
			Typeface fontType) {
		this.color = color;
		this.fontColor = fontColor;
		this.fontSize = fontSize;
		this.fontType = fontType;
	}
}
