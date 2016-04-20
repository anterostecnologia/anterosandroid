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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import br.com.anteros.android.ui.controls.R;


abstract class BalloonItemizedOverlay<Item extends OverlayItem> extends ItemizedOverlay<Item> {

	private MapView mapView;
	private BalloonOverlayView<Item> balloonView;
	private View clickRegion;
	private int viewOffset;
	final MapController mc;
	private Item currentFocussedItem;
	private int currentFocussedIndex;
	private int resourceCustomLayout;
	private View customLayout;

	public BalloonItemizedOverlay(Drawable defaultMarker, MapView mapView, int resourceCustomLayout) {
		super(boundCenter(defaultMarker));
		this.mapView = mapView;
		viewOffset = 0;
		mc = mapView.getController();
		this.resourceCustomLayout = resourceCustomLayout;
	}

	public void setBalloonBottomOffset(int pixels) {
		viewOffset = pixels;
	}

	public int getBalloonBottomOffset() {
		return viewOffset;
	}

	protected boolean onBalloonTap(int index, Item item) {
		return false;
	}

	@Override
	protected final boolean onTap(int index) {

		currentFocussedIndex = index;
		currentFocussedItem = createItem(index);

		boolean isRecycled;
		if (balloonView == null) {
			balloonView = createBalloonOverlayView();
			clickRegion = (View) balloonView.findViewById(R.id.balloon_inner_layout);
			clickRegion.setOnTouchListener(createBalloonTouchListener());

			LayoutInflater inflater = (LayoutInflater) mapView.getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE);
			customLayout = inflater.inflate(resourceCustomLayout, (ViewGroup) clickRegion);

			isRecycled = false;
		} else {
			isRecycled = true;
		}

		balloonView.setVisibility(View.GONE);

		List<Overlay> mapOverlays = mapView.getOverlays();
		if (mapOverlays.size() > 1) {
			hideOtherBalloons(mapOverlays);
		}

		balloonView.visible();

		bindCustomView(customLayout, currentFocussedIndex);

		GeoPoint point = currentFocussedItem.getPoint();
		MapView.LayoutParams params = new MapView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				point, MapView.LayoutParams.BOTTOM_CENTER);
		params.mode = MapView.LayoutParams.MODE_MAP;

		balloonView.setVisibility(View.VISIBLE);

		if (isRecycled) {
			balloonView.setLayoutParams(params);
		} else {
			mapView.addView(balloonView, params);
		}

		mc.animateTo(point);

		return true;
	}

	public abstract void bindCustomView(View customLayout, int index);

	protected BalloonOverlayView<Item> createBalloonOverlayView() {
		return new BalloonOverlayView<Item>(getMapView().getContext(), getBalloonBottomOffset());
	}

	protected MapView getMapView() {
		return mapView;
	}

	protected void hideBalloon() {
		if (balloonView != null) {
			balloonView.setVisibility(View.GONE);
		}
	}

	private void hideOtherBalloons(List<Overlay> overlays) {

		for (Overlay overlay : overlays) {
			if (overlay instanceof BalloonItemizedOverlay<?> && overlay != this) {
				((BalloonItemizedOverlay<?>) overlay).hideBalloon();
			}
		}

	}

	private OnTouchListener createBalloonTouchListener() {
		return new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {

				View l = ((View) v.getParent()).findViewById(R.id.balloon_main_layout);
				Drawable d = l.getBackground();

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					int[] states = { android.R.attr.state_pressed };
					if (d.setState(states)) {
						d.invalidateSelf();
					}
					return true;
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					int newStates[] = {};
					if (d.setState(newStates)) {
						d.invalidateSelf();
					}
					onBalloonTap(currentFocussedIndex, currentFocussedItem);
					return true;
				} else {
					return false;
				}

			}
		};
	}

}
