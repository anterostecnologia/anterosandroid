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

package br.com.anteros.android.ui.controls.grid;

import android.graphics.Canvas;
import android.graphics.Paint;
import br.com.anteros.android.core.util.DrawProperties;

public interface DataGridListener {

	public void onTitleClick(DataGridColumn column);

	public boolean onDrawField(DataGridField field, DrawProperties fieldProperties, Canvas canvas, Paint paint);
}
