package br.com.anteros.android.ui.controls.sectionView;

/**
 * The MIT License (MIT)

 Copyright (c) 2016 Gustavo Pagani

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */


import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Abstract {@link AnterosSection} with no states.
 */
public abstract class AnterosStatelessSection extends AnterosSection {

    /**
     * Create a stateless Section object without header and footer
     *
     * @deprecated Replaced by {@link #AnterosStatelessSection(AnterosSectionParameters)}
     *
     * @param itemResourceId layout resource for its items
     */
    @Deprecated
    public AnterosStatelessSection(@LayoutRes int itemResourceId) {
        this(new AnterosSectionParameters.Builder(itemResourceId)
                .build());
    }

    /**
     * Create a stateless Section object, with a custom header but without footer
     *
     * @deprecated Replaced by {@link #AnterosStatelessSection(AnterosSectionParameters)}
     *
     * @param headerResourceId layout resource for its header
     * @param itemResourceId layout resource for its items
     */
    @Deprecated
    public AnterosStatelessSection(@LayoutRes int headerResourceId, @LayoutRes int itemResourceId) {
        this(new AnterosSectionParameters.Builder(itemResourceId)
                .headerResourceId(headerResourceId)
                .build());
    }

    /**
     * Create a stateless Section object, with a custom header and a custom footer
     *
     * @deprecated Replaced by {@link #AnterosStatelessSection(AnterosSectionParameters)}
     *
     * @param headerResourceId layout resource for its header
     * @param footerResourceId layout resource for its footer
     * @param itemResourceId layout resource for its items
     */
    @Deprecated
    public AnterosStatelessSection(@LayoutRes int headerResourceId, @LayoutRes int footerResourceId,
                                   @LayoutRes int itemResourceId) {
        this(new AnterosSectionParameters.Builder(itemResourceId)
                .headerResourceId(headerResourceId)
                .footerResourceId(footerResourceId)
                .build());
    }

    /**
     * Create a stateless Section object based on {@link AnterosSectionParameters}
     * @param sectionParameters section parameters
     */
    public AnterosStatelessSection(AnterosSectionParameters sectionParameters) {
        super(sectionParameters);

        if (sectionParameters.loadingResourceId != null) {
            throw new IllegalArgumentException("Stateless section shouldn't have a loading state resource");
        }

        if (sectionParameters.failedResourceId != null) {
            throw new IllegalArgumentException("Stateless section shouldn't have a failed state resource");
        }

        if (sectionParameters.emptyResourceId != null) {
            throw new IllegalArgumentException("Stateless section shouldn't have an empty state resource");
        }
    }

    @Override
    public final void onBindLoadingViewHolder(RecyclerView.ViewHolder holder) {
        super.onBindLoadingViewHolder(holder);
    }

    @Override
    public final RecyclerView.ViewHolder getLoadingViewHolder(View view) {
        return super.getLoadingViewHolder(view);
    }

    @Override
    public final void onBindFailedViewHolder(RecyclerView.ViewHolder holder) {
        super.onBindFailedViewHolder(holder);
    }

    @Override
    public final RecyclerView.ViewHolder getFailedViewHolder(View view) {
        return super.getFailedViewHolder(view);
    }

    @Override
    public final void onBindEmptyViewHolder(RecyclerView.ViewHolder holder) {
        super.onBindEmptyViewHolder(holder);
    }

    @Override
    public final RecyclerView.ViewHolder getEmptyViewHolder(View view) {
        return super.getEmptyViewHolder(view);
    }
}
