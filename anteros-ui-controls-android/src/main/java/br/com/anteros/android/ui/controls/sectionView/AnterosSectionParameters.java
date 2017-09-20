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

/**
 * Class used as constructor parameters of {@link AnterosSection}.
 */
public class AnterosSectionParameters {
    @LayoutRes
    public final Integer headerResourceId;
    @LayoutRes
    public final Integer footerResourceId;
    @LayoutRes
    public final int itemResourceId;
    @LayoutRes
    public final Integer loadingResourceId;
    @LayoutRes
    public final Integer failedResourceId;
    @LayoutRes
    public final Integer emptyResourceId;

    /**
     * Builder of {@link AnterosSectionParameters}
     */
    public static class Builder {
        private final int itemResourceId;

        @LayoutRes
        private Integer headerResourceId;
        @LayoutRes
        private Integer footerResourceId;
        @LayoutRes
        private Integer loadingResourceId;
        @LayoutRes
        private Integer failedResourceId;
        @LayoutRes
        private Integer emptyResourceId;

        /**
         * Constructor with mandatory parameters of {@link AnterosSection}
         * @param itemResourceId layout resource for Section's items
         */
        public Builder(@LayoutRes int itemResourceId) {
            this.itemResourceId = itemResourceId;
        }

        /**
         * Set layout resource for Section's header
         * @param headerResourceId layout resource for Section's header
         * @return this builder
         */
        public Builder headerResourceId(@LayoutRes int headerResourceId) {
            this.headerResourceId = headerResourceId;

            return this;
        }

        /**
         * Set layout resource for Section's footer
         * @param footerResourceId layout resource for Section's footer
         * @return this builder
         */
        public Builder footerResourceId(@LayoutRes int footerResourceId) {
            this.footerResourceId = footerResourceId;

            return this;
        }

        /**
         * Set layout resource for Section's loading state
         * @param loadingResourceId layout resource for Section's loading state
         * @return this builder
         */
        public Builder loadingResourceId(@LayoutRes int loadingResourceId) {
            this.loadingResourceId = loadingResourceId;

            return this;
        }

        /**
         * Set layout resource for Section's failed state
         * @param failedResourceId layout resource for Section's failed state
         * @return this builder
         */
        public Builder failedResourceId(@LayoutRes int failedResourceId) {
            this.failedResourceId = failedResourceId;

            return this;
        }

        /**
         * Set layout resource for Section's empty state
         * @param emptyResourceId layout resource for Section's empty state
         * @return this builder
         */
        public Builder emptyResourceId(@LayoutRes int emptyResourceId) {
            this.emptyResourceId = emptyResourceId;

            return this;
        }
        
        /**
         * Build an instance of AnterosSectionParameters
         * @return an instance of AnterosSectionParameters
         */
        public AnterosSectionParameters build() {
            return new AnterosSectionParameters(this);
        }
    }

    private AnterosSectionParameters(Builder builder) {
        this.headerResourceId = builder.headerResourceId;
        this.footerResourceId = builder.footerResourceId;
        this.itemResourceId = builder.itemResourceId;
        this.loadingResourceId = builder.loadingResourceId;
        this.failedResourceId = builder.failedResourceId;
        this.emptyResourceId = builder.emptyResourceId;
    }
}
