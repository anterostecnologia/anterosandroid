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
import java.util.Collections;
import java.util.List;

import android.graphics.BitmapFactory.Options;
import br.com.anteros.android.ui.controls.image.mapper.Area;
import br.com.anteros.android.core.util.ImageUtils;

public class PageCurl {
	
	private String id;
	private String filePath;
	private List<Area> areas = new ArrayList<Area>();
	private List<Area> currentAreas = new ArrayList<Area>();

	public PageCurl(String id, String filePath, List<Area> areas) {
		this.id = id;
		this.filePath = filePath;
		this.areas.addAll(areas);
	}

	public PageCurl(String id, String filePath, Area... areas) {
		this.id = id;
		this.filePath = filePath;

		for (Area area : areas) {
			this.areas.add(area);
		}
	}

	public String getFilePath() {
		return filePath;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public List<Area> getAreas() {
		if (areas == null)
			areas = new ArrayList<Area>();
		return areas;
	}

	public void setAreas(List<Area> areas) {
		this.areas = areas;
	}
	
	public void selectAreaById(String id) {
		for (Area area : areas) {
			if (area.getId().equals(id)) {
				selectArea(area);
			}
		}
	}

	public void selectArea(Area area) {
		currentAreas.add(area);
	}
	
	public void unSelectArea(Area area) {
		currentAreas.remove(area);
	}

	public void selectAreas(List<Area> areas) {
		for (Area area : areas) {
			selectArea(area);
		}
	}
	
	public void unSelectAreas(List<Area> areas) {
		for (Area area : areas) {
			unSelectArea(area);
		}
	}

	public void clearSelection() {
		currentAreas.clear();
	}
	
	public boolean isSelectedArea(Area area) {
		return currentAreas.contains(area);
	}

	public List<Area> getCurrentAreas() {
		return Collections.unmodifiableList(currentAreas);
	}

	public void applyScale(int measuredWidth, int measuredHeight) {
		Options options = ImageUtils.getDefaultOptions(filePath);
		float scaleWidth = (float) measuredWidth / (float) options.outWidth;
		float scaleHeight = (float) measuredHeight / (float) options.outHeight;
		
		for (Area area : areas) {
			area.applyScale(scaleWidth, scaleHeight);
		}
	}
}
