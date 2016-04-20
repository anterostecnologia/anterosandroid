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

package br.com.anteros.android.ui.controls.pageCurl.canvas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import br.com.anteros.android.core.util.ImageUtils;
import br.com.anteros.android.ui.controls.pageCurl.PageCurlException;

public class PageCurlBitmapProvider {

	private PageCurlView view;
	private Map<PageCurl, Bitmap> cacheBitmaps;

	public PageCurlBitmapProvider(PageCurlView view) {
		this.view = view;
		cacheBitmaps = new HashMap<PageCurl, Bitmap>();
	}

	public void initialize(PageCurl page) {

	}

	public Bitmap getBitmap(PageCurl page) {
		return cacheBitmaps.get(page);
	}

	private void loadBitmapPutInCache(PageCurl page) {
		if (!cacheBitmaps.containsKey(page)) {
			Bitmap bitmap = ImageUtils.loadBitmap(page.getFilePath(), view.getMeasuredWidth(), view.getMeasuredHeight());
			
			if (bitmap == null)
				throw new PageCurlException("O bitmap não pode ser nulo! " + page.getFilePath());
			
			cacheBitmaps.put(page, bitmap);
		}
	}

	public void setSelectedPage(PageCurl page) {
		PageCurl nextPage = view.getNextPage(page);
		PageCurl previousPage = view.getPreviousPage(page);

		if (nextPage == null)
			nextPage = view.getFirstPage();
		if (previousPage == null)
			previousPage = view.getLastPage();
		
		List<PageCurl> listToRemove = new ArrayList<PageCurl>();
		Iterator<PageCurl> it = cacheBitmaps.keySet().iterator();
		while (it.hasNext()) {
			PageCurl pg = it.next();
			if (!pg.equals(previousPage) && !pg.equals(page) && !pg.equals(nextPage)) {
				listToRemove.add(pg);
			}
		}
		
		for (PageCurl pageCurl : listToRemove) {
			Bitmap bitmap = cacheBitmaps.remove(pageCurl);
			if (bitmap != null)
				bitmap.recycle();
		}

		loadBitmapPutInCache(nextPage);
		loadBitmapPutInCache(page);
		loadBitmapPutInCache(previousPage);
		
	}

	public interface BitmapProviderListener {
		void OnLoadBitmapFinish(PageCurl page);
	}
	
	public void clearCache() {
		cacheBitmaps.clear();
	}
}
