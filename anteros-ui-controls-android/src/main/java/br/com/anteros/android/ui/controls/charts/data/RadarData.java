
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

/**
 * Data container for the RadarChart.
 * 
 * @author Philipp Jahoda
 */
public class RadarData extends BarLineScatterCandleRadarData<RadarDataSet> {

    public RadarData(ArrayList<String> xVals) {
        super(xVals);
    }
    
    public RadarData(String[] xVals) {
        super(xVals);
    }
    
    public RadarData(ArrayList<String> xVals, ArrayList<RadarDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public RadarData(String[] xVals, ArrayList<RadarDataSet> dataSets) {
        super(xVals, dataSets);
    }

    public RadarData(ArrayList<String> xVals, RadarDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }

    public RadarData(String[] xVals, RadarDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }
    
    private static ArrayList<RadarDataSet> toArrayList(RadarDataSet dataSet) {
        ArrayList<RadarDataSet> sets = new ArrayList<RadarDataSet>();
        sets.add(dataSet);
        return sets;
    }
}
