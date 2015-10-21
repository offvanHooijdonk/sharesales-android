package com.share.sales.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Date;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;

public class FSUtil {
	private static final int COMPRESS_QUALITY = 100;
	public static String ROOT_FOLDER = "Yousave";
	public static String CAMERA_FOLDER = "Camera";
	public static String UPLOAD_FOLDER = "Upload";
	public static CompressFormat IMAGE_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;

	public static String getRootPath() throws IOException {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		if (path == null || "".equals(path)) {
			throw new FileNotFoundException("External storage path is not found!");
		}
		
		path = path + File.separator + ROOT_FOLDER;
		
		File root = new File(path);
		if (!root.exists()) {
			root.mkdir();
		}
		
		return path;
	}
	
	public static String getCameraFolder() throws IOException {
		String path = getRootPath();
		path = path + File.separator + CAMERA_FOLDER;
		
		File root = new File(path);
		if (!root.exists()) {
			root.mkdir();
		}
		
		return path;
	}
	
	public static String getUploadFolder() throws IOException {
		String path = getRootPath();
		path = path + File.separator + UPLOAD_FOLDER;
		
		File root = new File(path);
		if (!root.exists()) {
			root.mkdir();
		}
		
		return path;
	}
	
	public static String saveCameraShot(Bitmap bitmap) throws IOException {
		String path = getCameraFolder();
		String filePath = saveBitMap(path, bitmap);
		return filePath;
	}
	
	private static String saveBitMap(String dir, Bitmap bitMap) throws IOException {
		FileOutputStream out = null;
		
		File file = generateTmpFileName(dir);
		
		try {
			out = new FileOutputStream(file);
			bitMap.compress(IMAGE_COMPRESS_FORMAT, COMPRESS_QUALITY, out);
		} finally {
			if (out != null) {
				out.close();
			}
		}
		
		return file.getAbsolutePath();
	}
	
	public static String storeFileToUploadFolder(String filePath, boolean removeSource) throws FileNotFoundException, IOException {
		File srcFile = new File(filePath);
		if (!srcFile.exists()) {
			throw new FileNotFoundException(String.format("File %s not found", filePath));
		}
		
		File destFile = generateTmpFileName(getUploadFolder());
		
		FileInputStream inStream = new FileInputStream(srcFile);
	    FileOutputStream outStream = new FileOutputStream(destFile);
	    try {
		    FileChannel inChannel = inStream.getChannel();
		    FileChannel outChannel = outStream.getChannel();
		    inChannel.transferTo(0, inChannel.size(), outChannel);
	    } finally {
		    inStream.close();
		    outStream.close();
	    }
	    
	    if (removeSource) {
	    	srcFile.delete();
	    }
	    
	    return destFile.getAbsolutePath();
	}
	
	private static File generateTmpFileName(String dir) {
		String filePath = dir + File.separator + String.valueOf((new Date()).getTime()) + "." + IMAGE_COMPRESS_FORMAT.name();

		File file = new File(filePath);
		while (file.exists()) {
			filePath = filePath.substring(0, filePath.lastIndexOf(".")) + "_" + String.valueOf(Math.round(Math.random()*10000)) + 
					"." + IMAGE_COMPRESS_FORMAT.name();
			file = new File(filePath);
		}
		
		return file;
	}
}
