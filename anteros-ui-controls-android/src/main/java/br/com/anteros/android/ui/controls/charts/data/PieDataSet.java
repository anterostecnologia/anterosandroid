
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

import br.com.anteros.android.ui.controls.charts.utils.Utils;

public class PieDataSet extends DataSet<Entry> {

    /** the space in degrees between the chart-slices, default 0f */
    private float mSliceSpace = 0f;

    /** indicates the selection distance of a pie slice */
    private float mShift = 18f;

    public PieDataSet(ArrayList<Entry> yVals, String label) {
        super(yVals, label);

//        mShift = Utils.convertDpToPixel(12f);
    }

    @Override
    public DataSet<Entry> copy() {

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < mYVals.size(); i++) {
            yVals.add(mYVals.get(i).copy());
        }

        PieDataSet copied = new PieDataSet(yVals, getLabel());
        copied.mColors = mColors;
        copied.mSliceSpace = mSliceSpace;
        copied.mShift = mShift;
        return copied;
    }

    /**
     * sets the space that is left out between the piechart-slices, default: 0°
     * --> no space, maximum 45, minimum 0 (no space)
     * 
     * @param degrees
     */
    public void setSliceSpace(float degrees) {

        if (degrees > 45)
            degrees = 45f;
        if (degrees < 0)
            degrees = 0f;

        mSliceSpace = degrees;
    }

    /**
     * returns the space that is set to be between the piechart-slices of this
     * DataSet, in degrees
     * 
     * @return
     */
    public float getSliceSpace() {
        return mSliceSpace;
    }

    /**
     * sets the distance the highlighted piechart-slice of this DataSet is
     * "shifted" away from the center of the chart, default 12f
     * 
     * @param shift
     */
    public void setSelectionShift(float shift) {
        mShift = Utils.convertDpToPixel(shift);
    }

    /**
     * returns the distance a highlighted piechart slice is "shifted" away from
     * the chart-center
     * 
     * @return
     */
    public float getSelectionShift() {
        return mShift;
    }
}
