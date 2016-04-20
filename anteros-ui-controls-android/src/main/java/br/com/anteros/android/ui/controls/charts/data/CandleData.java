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

package br.com.anteros.android.ui.controls.charts.data;

import java.util.ArrayList;

public class CandleData extends BarLineScatterCandleData<CandleDataSet> {

    public CandleData(ArrayList<String> xVals) {
        super(xVals);
    }
    
    public CandleData(String[] xVals) {
        super(xVals);
    }
    
    public CandleData(ArrayList<String> xVals, ArrayList<CandleDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public CandleData(String[] xVals, ArrayList<CandleDataSet> dataSets) {
        super(xVals, dataSets);
    }
    
    public CandleData(ArrayList<String> xVals, CandleDataSet dataSet) {
        super(xVals, toArrayList(dataSet));        
    }
    
    public CandleData(String[] xVals, CandleDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }
    
    private static ArrayList<CandleDataSet> toArrayList(CandleDataSet dataSet) {
        ArrayList<CandleDataSet> sets = new ArrayList<CandleDataSet>();
        sets.add(dataSet);
        return sets;
    }
}
