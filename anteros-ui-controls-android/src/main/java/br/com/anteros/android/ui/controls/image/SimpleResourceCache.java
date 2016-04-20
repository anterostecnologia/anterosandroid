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

package br.com.anteros.android.ui.controls.image;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Path;
import br.com.anteros.android.ui.controls.image.MapParser.Area;


public class SimpleResourceCache implements ImageMapResourcesCache {

	HashMap<Integer, WeakReference<Bitmap>> bitmaps;
	private HashMap<Integer, Object> dataIds;
	private HashMap<Integer, Object> paths;
	private MapParser mapParser;
	private HashMap<Integer, HashMap<Integer, ArrayList<Integer>>> areaGroups;
	public SimpleResourceCache(MapParser mapParser) {
		bitmaps = new HashMap<Integer, WeakReference<Bitmap>>();
		dataIds = new HashMap<Integer, Object>();
		paths = new HashMap<Integer, Object>();
		areaGroups = new HashMap<Integer, HashMap<Integer, ArrayList<Integer>>>();
		this.mapParser = mapParser;
	}

	@Override
	public synchronized Path[] getAreaPaths(Context context, Integer xmlResourceId) {
		if (dataIds.containsKey(xmlResourceId)) {
			return (Path[]) paths.get(xmlResourceId);
		}
		try {
			init(context, xmlResourceId);
		} catch (Exception e) {
			throw new IllegalStateException("Failed to init image map areas", e);
		}
		notify();
		return (Path[]) paths.get(xmlResourceId);
	}

	public synchronized void init(Context context, int mapResource)
			throws XmlPullParserException, IOException {
		ArrayList<Area> areas = mapParser.parseAreas(context, mapResource);
		int size = areas.size();
		int[][] areaIds = new int[size][2];
		Path[] areaPaths = new Path[size];
		HashMap<Integer, ArrayList<Integer>> groupsByData = new HashMap<Integer, ArrayList<Integer>>();
		int i = 0;
		for (Area a : areas) {
			areaIds[i][0] = a.id;
			areaIds[i][1] = a.target;
			areaPaths[i] = a.path;
			if(!groupsByData.containsKey(a.id)){
				groupsByData.put(a.id, new ArrayList<Integer>());
			}
			groupsByData.get(a.id).add(a.target);
			i++;
		}
		dataIds.put(mapResource, areaIds);
		paths.put(mapResource, areaPaths);
		areaGroups.put(mapResource, groupsByData);
	}

	@Override
	public int getDataId(Context context, Integer xmlResourceId, Integer pathIndex) {
		if(!dataIds.containsKey(xmlResourceId)){
			try {
				init(context, xmlResourceId);
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ((int[][]) dataIds.get(xmlResourceId))[pathIndex][0];
	}

	@Override
	public int getAreaId(Context context, Integer xmlResourceId, Integer dataId, Integer target) {
		if(!dataIds.containsKey(xmlResourceId)){
			try {
				init(context, xmlResourceId);
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		int[][] ids = (int[][]) this.dataIds.get(xmlResourceId);
		for (int i = 0; i < ids.length; i++) {
			if (dataId == ids[i][0] && (target == -1 || target == ids[i][1])) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public int getAreaId(Context context, Integer mapResource, Integer id) {
		return getAreaId(context, mapResource, id, -1);
	}

	@Override
	public ArrayList<Integer> getAreaGroups(Context context,
			Integer xmlResourceId, Integer dataId) {
		if(!areaGroups.containsKey(xmlResourceId)){
			try {
				init(context, xmlResourceId);
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return areaGroups.get(xmlResourceId).get(dataId);
	}
}
