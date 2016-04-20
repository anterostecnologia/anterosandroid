package br.com.anteros.android.hardware.storage;

import java.io.File;

import android.graphics.Bitmap;
import android.os.Environment;

public class StorageManager {
	private static StorageManager storageManager;

	private StorageManager() {

	}

	public static StorageManager getManager() {
		if (storageManager == null)
			storageManager = new StorageManager();
		return storageManager;
	}

	private void save(SDFile file) throws Exception {
		file.save();
	}

	public void saveBitmap(Bitmap bitmap, String name) throws Exception {
		save(new BitmapSDFile(bitmap, name));
	}

	public File[] getAllFiles() {
		return Environment.getExternalStorageDirectory().listFiles();
	}

}
