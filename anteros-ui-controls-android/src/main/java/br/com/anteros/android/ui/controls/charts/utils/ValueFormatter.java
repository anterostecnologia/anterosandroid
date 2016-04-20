
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

package br.com.anteros.android.ui.controls.charts.utils;

/**
 * Interface that allows custom formatting of all values and value-labels
 * displayed inside the chart. Simply create your own formatting class and let
 * it implement ValueFormatter. Then override the getFormattedLabel(...) method
 * and return whatever you want.
 * 
 * @author Philipp Jahoda
 */
public interface ValueFormatter {

    /**
     * Called when a value (from labels, or inside the chart) is formatted
     * before being drawn. For performance reasons, avoid excessive calculations
     * and memory allocations inside this method.
     * 
     * @param value the value to be formatted
     * @return the formatted label ready for being drawn
     */
    public String getFormattedValue(float value);
}
