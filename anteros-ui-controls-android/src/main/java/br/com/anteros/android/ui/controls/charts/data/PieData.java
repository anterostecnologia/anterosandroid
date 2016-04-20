
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
 * A PieData object can only represent one DataSet. Unlike all other charts, the
 * legend labels of the PieChart are created from the x-values array, and not
 * from the DataSet labels.
 * 
 * @author Philipp Jahoda
 */
public class PieData extends ChartData<PieDataSet> {
    
    public PieData(ArrayList<String> xVals) {
        super(xVals);
    }
    
    public PieData(String[] xVals) {
        super(xVals);
    }

    public PieData(ArrayList<String> xVals, PieDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }

    public PieData(String[] xVals, PieDataSet dataSet) {
        super(xVals, toArrayList(dataSet));
    }
    
    private static ArrayList<PieDataSet> toArrayList(PieDataSet dataSet) {
        ArrayList<PieDataSet> sets = new ArrayList<PieDataSet>();
        sets.add(dataSet);
        return sets;
    }

    /**
     * Returns the DataSet this PieData object represents.
     * 
     * @return
     */
    public PieDataSet getDataSet() {
        return (PieDataSet) mDataSets.get(0);
    }
}
