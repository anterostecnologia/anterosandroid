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

package br.com.anteros.android.ui.controls.image.mapper;

import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.Canvas;

public abstract class Area {
	public static final int TEXT_SIZE = 10;
	public static final float DENSITY_ULTRA_LOW = 100f;

	String _id;
	String _name;
	HashMap<String, String> _values;
	Bitmap _decoration = null;

	public Area(String id, String name) {
		_id = id;
		if (name != null) {
			_name = name;
		}
	}

	public String getId() {
		return _id;
	}

	public String getName() {
		return _name;
	}

	public void addValue(String key, String value) {
		if (_values == null) {
			_values = new HashMap<String, String>();
		}
		_values.put(key, value);
	}

	public String getValue(String key) {
		String value = null;
		if (_values != null) {
			value = _values.get(key);
		}
		return value;
	}

	public abstract boolean isInArea(float x, float y);

	public abstract float getOriginX();

	public abstract float getOriginY();

	public void draw(Canvas canvas, boolean drawText) {
		draw(canvas, 100, 255, 255, 0, drawText);
	}

	public abstract void draw(Canvas canvas, int alfa, int r, int g, int b, boolean drawText);

	public abstract void applyScale(float scaleWidth, float scaleHeight);
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_id == null) ? 0 : _id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Area other = (Area) obj;
		if (_id == null) {
			if (other._id != null)
				return false;
		} else if (!_id.equals(other._id))
			return false;
		return true;
	}

	public void setName(String name) {
		this._name = name;
	}
}
