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
