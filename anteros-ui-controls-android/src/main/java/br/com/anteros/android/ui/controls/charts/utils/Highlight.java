
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
 * Contains information needed to determine the highlighted value.
 * 
 * @author Philipp Jahoda
 */
public class Highlight {

    /** the x-index of the highlighted value */
    private int mXIndex;
    
    /** the index of the dataset the highlighted value is in */
    private int mDataSetIndex;

    /**
     * constructor
     * 
     * @param x the index of the highlighted value on the x-axis
     * @param val the value at the position the user touched
     * @param dataSet the index of the DataSet the highlighted value belongs to
     */
    public Highlight(int x, int dataSet) {
        this.mXIndex = x;
        this.mDataSetIndex = dataSet;
    }

    /**
     * returns the index of the DataSet the highlighted value is in
     * 
     * @return
     */
    public int getDataSetIndex() {
        return mDataSetIndex;
    }

    /**
     * returns the index of the highlighted value on the x-axis
     * 
     * @return
     */
    public int getXIndex() {
        return mXIndex;
    }

    /**
     * returns true if this highlight object is equal to the other (compares
     * xIndex and dataSetIndex)
     * 
     * @param h
     * @return
     */
    public boolean equalTo(Highlight h) {

        if (h == null)
            return false;
        else {
            if (this.mDataSetIndex == h.mDataSetIndex && this.mXIndex == h.mXIndex)
                return true;
            else
                return false;
        }
    }
}
