package br.com.anteros.android.hardware.camera;

import android.graphics.Bitmap;

public interface CameraCallback {
	void onTakePicture(Bitmap bitmap);

	void onCancelTakePicture();

	void onExit();
}
