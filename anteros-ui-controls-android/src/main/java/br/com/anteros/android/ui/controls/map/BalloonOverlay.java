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

package br.com.anteros.android.ui.controls.map;

import java.util.List;
import android.graphics.drawable.Drawable;
import android.view.View;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public abstract class BalloonOverlay<T> extends BalloonItemizedOverlay<OverlayItem> {

	private List<T> itens;

	public BalloonOverlay(Drawable defaultMarker, MapView mapView, List<T> itens, int resourceLayout) {
		super(defaultMarker, mapView, resourceLayout);
		this.itens = itens;
	}

	@Override
	protected OverlayItem createItem(int index) {
		T item = itens.get(index);
		return new OverlayItem(getGeoPoint(item), "", "");
	}

	@Override
	public void bindCustomView(View view, int index) {
		bindView(view, index, itens.get(index));

	}

	@Override
	public int size() {
		return itens.size();
	}

	public abstract void bindView(View view, int index, T item);

	public abstract GeoPoint getGeoPoint(T item);

}
