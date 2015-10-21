package com.share.sales.helper;

import java.io.File;
import java.io.FileNotFoundException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class ImagesHelper {

	public static Bitmap createScaledBitmapByHight(String filePath, int height) throws FileNotFoundException {
		if (filePath == null) {
			throw new FileNotFoundException("File path was not provided!");
		}
		File file = new File(filePath);
		if (!file.exists()) {
			throw new FileNotFoundException(String.format("File %s does not exist!", filePath));
		}
		Bitmap bitmap = BitmapFactory.decodeFile(filePath);
		int widthScaled = Math.round((float) bitmap.getWidth() * (height/(float)bitmap.getHeight()));
		return Bitmap.createScaledBitmap(bitmap, widthScaled, height, false);
	}
}
