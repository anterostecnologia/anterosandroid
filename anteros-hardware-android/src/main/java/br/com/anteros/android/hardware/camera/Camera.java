package br.com.anteros.android.hardware.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;

/**
 * Activity para utilização da Camera <b>Para utilização dessa Activity, ela
 * deverá ser informada no AndroidManifest.xml</b>
 *<p>
 * {@code <activity android:name="br.com.anteros.android.hardware.camera.Camera" />}
 * <p>
 * 
 */
public class Camera extends Activity {
	private static final int TAKE_PICTURE = 7893;
	private static CameraCallback callback;

	/**
	 * Inicia Camera
	 */
	public static void open(Activity activity, CameraCallback cameraCallback) {
		activity.startActivity(new Intent(activity, Camera.class));
		callback = cameraCallback;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(intent, TAKE_PICTURE);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == TAKE_PICTURE) {
			if (resultCode == RESULT_OK) {
				callback.onTakePicture((Bitmap) data.getExtras().get("data"));
			} else if (resultCode == RESULT_CANCELED) {
				callback.onCancelTakePicture();
			} else {
				callback.onExit();
			}
			finish();
		}
	}
}
