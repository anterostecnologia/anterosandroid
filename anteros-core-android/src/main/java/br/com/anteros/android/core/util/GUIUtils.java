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

import android.app.Activity;
import android.util.DisplayMetrics;

import java.math.BigDecimal;

public class GUIUtils {

	public static boolean isTablet(Activity activity) {
		if (activity == null)
			return false;

		DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
		int widthPixels = displayMetrics.widthPixels;
		int heightPixels = displayMetrics.heightPixels;

		float widthDpi = displayMetrics.xdpi;
		float heightDpi = displayMetrics.ydpi;

		float widthInches = widthPixels / widthDpi;
		float heightInches = heightPixels / heightDpi;

		double diagonalInches = Math.sqrt((widthInches * widthInches) + (heightInches * heightInches));

		BigDecimal bd = new BigDecimal(Double.toString(diagonalInches));
		bd = bd.setScale(0, BigDecimal.ROUND_CEILING);

		return !(Math.round(diagonalInches) < 7 || (Math.round(diagonalInches) >= 7 && (displayMetrics.densityDpi >= DisplayMetrics.DENSITY_HIGH)));

	}
}
