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

package br.com.anteros.android.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import br.com.anteros.core.utils.Base64;

public class ImageUtils {

	private static final int JPEG_EOI_1 = 0xFF;
	private static final int JPEG_EOI_2 = 0xD9;

	public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);
		return resizedBitmap;
	}

	public static String convertImgToBase64(Bitmap img) throws IOException {
		byte[] b = convertImgToArray(img);
		return Base64.encodeBytes(b);
	}

	public static byte[] convertImgToArray(Bitmap img) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		img.compress(Bitmap.CompressFormat.PNG, 100, baos);
		byte[] b = baos.toByteArray();
		return b;

	}

	public static Bitmap convertBase64ToImage(String imgStr, int width, int height) throws IOException {
		byte[] b;
		b = Base64.decode(imgStr);
		return convertArrayToImage(b, width, height);
	}

	public static Bitmap convertArrayToImage(byte[] imageData, int width, int height) throws IOException {
		return BitmapFactory.decodeStream(new ByteArrayInputStream(imageData));
	}

	public static boolean saveToFile(Bitmap bitmap, String mCurrentPhotoPath) {
		File file = new File(mCurrentPhotoPath);
		if (file.exists())
			file.delete();

		FileOutputStream out = null;

		try {
			out = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
			out.flush();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (Exception ex) {
			}
		}
		return false;
	}

	public static Bitmap loadScaledImage(String mCurrentPhotoPath, int targetW, int targetH) {

		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */

		BitmapFactory.Options bmOptions = getDefaultOptions(mCurrentPhotoPath);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW / targetW, photoH / targetH);
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

		return bitmap;
	}

	public static Bitmap resizeAndStorageImage(String mCurrentPhotoPath, int targetW, int targetH) {
		Bitmap bitmap = loadScaledImage(mCurrentPhotoPath, targetW, targetH);

		saveToFile(bitmap, mCurrentPhotoPath);

		return bitmap;
	}

	public static String getPathFromFileGallery(Uri mUri, Activity activity) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cursor = activity.managedQuery(mUri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public static Bitmap loadJPGImage(File arquivo) throws Exception {
		FileInputStream is = new FileInputStream(arquivo);
		final ByteArrayOutputStream bos = new ByteArrayOutputStream();
		final int size = 1024;
		int len = -1;
		final byte[] buf = new byte[size];

		while ((len = is.read(buf, 0, size)) != -1)
		{
			bos.write(buf, 0, len);
		}
		bos.write(JPEG_EOI_1);
		bos.write(JPEG_EOI_2);

		bos.flush();

		final byte[] bytes = bos.toByteArray();

		bos.close();
		is.close();

		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}

	public static int getScale(int originalWidth, int originalHeight, final int requiredWidth,
			final int requiredHeight) {
		// a scale of 1 means the original dimensions
		// of the image are maintained
		int scale = 1;
		// calculate scale only if the height or width of
		// the image exceeds the required value.
		if ((originalWidth > requiredWidth) || (originalHeight > requiredHeight)) {
			// calculate scale with respect to
			// the smaller dimension
			if (originalWidth < originalHeight)
				scale = Math.round((float) originalWidth / requiredWidth);
			else
				scale = Math.round((float) originalHeight / requiredHeight);
		}

		return scale;
	}

	public static BitmapFactory.Options getOptions(String filePath, int requiredWidth, int requiredHeight) {

		BitmapFactory.Options options = getDefaultOptions(filePath);

		// obtain the inSampleSize for loading a
		// scaled down version of the image.
		// options.outWidth and options.outHeight
		// are the measured dimensions of the
		// original image
		options.inSampleSize = getScale(options.outWidth, options.outHeight, requiredWidth, requiredHeight);

		// set inJustDecodeBounds to false again
		// so that we can now actually allocate the
		// bitmap some memory
		options.inJustDecodeBounds = false;

		return options;

	}

	public static BitmapFactory.Options getDefaultOptions(String filePath) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		// setting inJustDecodeBounds to true
		// ensures that we are able to measure
		// the dimensions of the image,without
		// actually allocating it memory
		options.inJustDecodeBounds = true;

		// decode the file for measurement
		BitmapFactory.decodeFile(filePath, options);
		return options;
	}

	public static Bitmap loadBitmap(String filePath, int requiredWidth, int requiredHeight) {
		BitmapFactory.Options options = getOptions(filePath, requiredWidth, requiredHeight);
		return BitmapFactory.decodeFile(filePath, options);
	}
}
