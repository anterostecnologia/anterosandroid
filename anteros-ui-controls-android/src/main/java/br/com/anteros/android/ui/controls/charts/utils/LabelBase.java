
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

import android.graphics.Color;
import android.graphics.Typeface;

/**
 * Baseclass of all labels.
 * 
 * @author Philipp Jahoda
 */
public abstract class LabelBase {

    /** the typeface to use for the labels */
    private Typeface mTypeface;

    /** the size of the label text */
    private float mTextSize = 10f;
    
    /** the text color to use */
    private int mTextColor = Color.BLACK;

    /** default constructor */
    public LabelBase() {
        mTextSize = Utils.convertDpToPixel(10f);
    }

    /**
     * sets the size of the label text in pixels min = 6f, max = 16f, default
     * 10f
     * 
     * @param size
     */
    public void setTextSize(float size) {

        if (size > 16f)
            size = 16f;
        if (size < 6f)
            size = 6f;

        mTextSize = Utils.convertDpToPixel(size);
    }

    /**
     * returns the text size that is currently set for the labels
     * 
     * @return
     */
    public float getTextSize() {
        return mTextSize;
    }

    /**
     * sets the typeface that should be used for the labels
     * 
     * @param t
     */
    public void setTypeface(Typeface t) {
        mTypeface = t;
    }

    /**
     * returns the typeface that is used for the labels
     * 
     * @return
     */
    public Typeface getTypeface() {
        return mTypeface;
    }

    /**
     * Sets the text color to use for the labels. Make sure to use
     * getResources().getColor(...) when using a color from the resources.
     * 
     * @param color
     */
    public void setTextColor(int color) {
        mTextColor = color;
    }

    /**
     * Returns the text color that is set for the labels.
     * 
     * @return
     */
    public int getTextColor() {
        return mTextColor;
    }
}
