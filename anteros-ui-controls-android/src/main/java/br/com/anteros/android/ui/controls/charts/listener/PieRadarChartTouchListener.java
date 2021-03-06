
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

package br.com.anteros.android.ui.controls.charts.listener;

import android.graphics.PointF;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import br.com.anteros.android.ui.controls.charts.PieRadarChartBase;
import br.com.anteros.android.ui.controls.charts.RadarChart;
import br.com.anteros.android.ui.controls.charts.interfaces.OnChartGestureListener;
import br.com.anteros.android.ui.controls.charts.utils.Highlight;
import br.com.anteros.android.ui.controls.charts.utils.SelInfo;
import br.com.anteros.android.ui.controls.charts.utils.Utils;

import java.util.ArrayList;

/**
 * Touchlistener for the PieChart.
 * 
 * @author Philipp Jahoda
 */
public class PieRadarChartTouchListener extends SimpleOnGestureListener implements OnTouchListener {

    private static final int NONE = 0;
    private static final int ROTATE = 1;

    private PointF mTouchStartPoint = new PointF();

    private PieRadarChartBase mChart;

    private int mTouchMode = NONE;

    private GestureDetector mGestureDetector;

    public  PieRadarChartTouchListener(PieRadarChartBase ctx) {
        this.mChart = ctx;

        mGestureDetector = new GestureDetector(ctx.getContext(), this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent e) {

        if (mGestureDetector.onTouchEvent(e))
            return true;

        // if rotation by touch is enabled
        if (mChart.isRotationEnabled()) {

            float x = e.getX();
            float y = e.getY();

            switch (e.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    mChart.setStartAngle(x, y);
                    mTouchStartPoint.x = x;
                    mTouchStartPoint.y = y;
                    break;
                case MotionEvent.ACTION_MOVE:

                    if (mTouchMode == NONE && distance(x, mTouchStartPoint.x, y, mTouchStartPoint.y) 
                            > Utils.convertDpToPixel(8f)) {
                        mTouchMode = ROTATE;
                        mChart.disableScroll();
                    } else if (mTouchMode == ROTATE) {
                        mChart.updateRotation(x, y);
                        mChart.invalidate();
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    mChart.enableScroll();
                    mTouchMode = NONE;
                    break;
            }
        }

        return true;
    }

    @Override
    public void onLongPress(MotionEvent me) {
        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartLongPressed(me);
        }
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return true;
    }

    /** reference to the last highlighted object */
    private Highlight mLastHighlight = null;

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartSingleTapped(e);
        }

        float distance = mChart.distanceToCenter(e.getX(), e.getY());

        // check if a slice was touched
        if (distance > mChart.getRadius()) {

            // if no slice was touched, highlight nothing
            mChart.highlightValues(null);
            mLastHighlight = null;

        } else {

            float angle = mChart.getAngleForPoint(e.getX(), e.getY());
            int index = mChart.getIndexForAngle(angle);

            // check if the index could be found
            if (index < 0) {

                mChart.highlightValues(null);
                mLastHighlight = null;

            } else {

                ArrayList<SelInfo> valsAtIndex = mChart.getYValsAtIndex(index);

                int dataSetIndex = 0;

                // get the dataset that is closest to the selection (PieChart
                // only
                // has one DataSet)
                if (mChart instanceof RadarChart) {

                    dataSetIndex = Utils.getClosestDataSetIndex(valsAtIndex, distance
                            / ((RadarChart) mChart).getFactor());
                }

                Highlight h = new Highlight(index, dataSetIndex);

                if (h.equalTo(mLastHighlight)) {

                    mChart.highlightTouch(null);
                    mLastHighlight = null;
                } else {

                    mChart.highlightTouch(h);
                    mLastHighlight = h;
                }
            }
        }

        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        OnChartGestureListener l = mChart.getOnChartGestureListener();

        if (l != null) {
            l.onChartDoubleTapped(e);
        }
        return super.onDoubleTap(e);
    }

    /**
     * returns the distance between two points
     * 
     * @param eventX
     * @param startX
     * @param eventY
     * @param startY
     * @return
     */
    private static float distance(float eventX, float startX, float eventY, float startY) {
        float dx = eventX - startX;
        float dy = eventY - startY;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
}
