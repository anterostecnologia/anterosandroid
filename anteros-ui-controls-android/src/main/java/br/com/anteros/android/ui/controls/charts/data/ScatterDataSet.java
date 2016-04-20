
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

import android.graphics.Path;
import br.com.anteros.android.ui.controls.charts.ScatterChart.ScatterShape;
import br.com.anteros.android.ui.controls.charts.utils.Utils;

import java.util.ArrayList;

public class ScatterDataSet extends BarLineScatterCandleRadarDataSet<Entry> {

    /** the size the scattershape will have, in screen pixels */
    private float mShapeSize = 12f;

    /**
     * the type of shape that is set to be drawn where the values are at,
     * default ScatterShape.SQUARE
     */
    private ScatterShape mScatterShape = ScatterShape.SQUARE;

    /**
     * Custom path object the user can provide that is drawn where the values
     * are at. This is used when ScatterShape.CUSTOM is set for a DataSet.
     */
    private Path mCustomScatterPath = null;

    public ScatterDataSet(ArrayList<Entry> yVals, String label) {
        super(yVals, label);

        // mShapeSize = Utils.convertDpToPixel(8f);
    }

    @Override
    public DataSet<Entry> copy() {

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < mYVals.size(); i++) {
            yVals.add(mYVals.get(i).copy());
        }

        ScatterDataSet copied = new ScatterDataSet(yVals, getLabel());
        copied.mColors = mColors;
        copied.mShapeSize = mShapeSize;
        copied.mScatterShape = mScatterShape;
        copied.mCustomScatterPath = mCustomScatterPath;
        copied.mHighLightColor = mHighLightColor;

        return copied;
    }

    /**
     * Sets the size in density pixels the drawn scattershape will have. This
     * only applies for non custom shapes.
     * 
     * @param size
     */
    public void setScatterShapeSize(float size) {
        mShapeSize = Utils.convertDpToPixel(size);
    }

    /**
     * returns the currently set scatter shape size
     * 
     * @return
     */
    public float getScatterShapeSize() {
        return mShapeSize;
    }

    /**
     * Sets the shape that is drawn on the position where the values are at. If
     * "CUSTOM" is chosen, you need to call setCustomScatterShape(...) and
     * provide a path object that is drawn as the custom scattershape.
     * 
     * @param shape
     */
    public void setScatterShape(ScatterShape shape) {
        mScatterShape = shape;
    }

    /**
     * returns all the different scattershapes the chart uses
     * 
     * @return
     */
    public ScatterShape getScatterShape() {
        return mScatterShape;
    }

    /**
     * Sets a path object as the shape to be drawn where the values are at. Do
     * not forget to call setScatterShape(...) and set the shape to
     * ScatterShape.CUSTOM.
     * 
     * @param shape
     */
    public void setCustomScatterShape(Path shape) {
        mCustomScatterPath = shape;
    }

    /**
     * returns the custom path / shape that is specified to be drawn where the
     * values are at
     * 
     * @return
     */
    public Path getCustomScatterShape() {
        return mCustomScatterPath;
    }
}