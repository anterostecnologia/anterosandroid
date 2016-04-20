
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

/**
 * Entry class for the BarChart. (especially stacked bars)
 * 
 * @author Philipp Jahoda
 */
public class BarEntry extends Entry {

    /** the values the stacked barchart holds */
    private float[] mVals;

    /**
     * Constructor for stacked bar entries.
     * 
     * @param vals
     * @param xIndex
     */
    public BarEntry(float[] vals, int xIndex) {
        super(calcSum(vals), xIndex);

        this.mVals = vals;
    }

    /**
     * Constructor for normal bars (not stacked).
     * 
     * @param val
     * @param xIndex
     */
    public BarEntry(float val, int xIndex) {
        super(val, xIndex);
    }

    /**
     * Constructor for stacked bar entries.
     * 
     * @param vals
     * @param xIndex
     * @param label Additional description label.
     */
    public BarEntry(float[] vals, int xIndex, String label) {
        super(calcSum(vals), xIndex, label);

        this.mVals = vals;
    }

    /**
     * Constructor for normal bars (not stacked).
     * 
     * @param val
     * @param xIndex
     * @param data Spot for additional data this Entry represents.
     */
    public BarEntry(float val, int xIndex, Object data) {
        super(val, xIndex, data);
    }

    /**
     * Returns an exact copy of the BarEntry.
     */
    public BarEntry copy() {

        BarEntry copied = new BarEntry(getVal(), getXIndex(), getData());
        copied.mVals = mVals;
        return copied;
    }

    /**
     * Returns the stacked values this BarEntry represents, or null, if only a
     * single value is represented (then, use getVal()).
     * 
     * @return
     */
    public float[] getVals() {
        return mVals;
    }

    /**
     * Set the array of values this BarEntry should represent.
     * 
     * @param vals
     */
    public void setVals(float[] vals) {
        mVals = vals;
    }

    /**
     * Returns the closest value inside the values array (for stacked barchart)
     * to the value given as a parameter. The closest value must be higher
     * (above) the provided value.
     * 
     * @param val
     * @return
     */
    public int getClosestIndexAbove(float val) {

        if (mVals == null)
            return 0;

        float dist = 0f;
        int closestIndex = 0;

        for (int i = 0; i < mVals.length; i++) {

            float newDist = Math.abs((getVal() - mVals[i]) - val);

            if (newDist < dist && mVals[i] > val) {
                dist = newDist;
                closestIndex = i;
            }
        }

        return closestIndex;
    }

    /**
     * Calculates the sum across all values.
     * 
     * @param vals
     * @return
     */
    private static float calcSum(float[] vals) {

        float sum = 0f;

        for (float f : vals)
            sum += f;

        return sum;
    }
}
