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

package br.com.anteros.android.hardware.storage;

import java.io.File;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.os.Environment;

class BitmapSDFile implements SDFile {

	private Bitmap bitmap;
	private String name;

	public BitmapSDFile(Bitmap bitmap, String name) {
		this.bitmap = bitmap;
		this.name = name;
	}

	public void save() throws Exception {
		File file = new File(Environment.getExternalStorageDirectory(),
				this.name);
		FileOutputStream fos = new FileOutputStream(file);
		bitmap.compress(Bitmap.CompressFormat.PNG, 90, fos);

		fos.flush();
		fos.close();
	}

}
