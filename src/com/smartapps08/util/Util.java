package com.smartapps08.util;

import java.io.File;
import java.io.IOException;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;

import com.smartapps08.model.FileListEntry;
import com.smartapps08.storage.R;

public final class Util {

	private static final String TAG = Util.class.getName();
	private static File COPIED_FILE = null;
	private static int pasteMode = 1;

	public static final int PASTE_MODE_COPY = 0;
	public static final int PASTE_MODE_MOVE = 1;

	private Util() {
	}

	public static synchronized void setPasteSrcFile(File f, int mode) {
		COPIED_FILE = f;
		pasteMode = mode % 2;
	}

	public static synchronized File getFileToPaste() {
		return COPIED_FILE;
	}

	public static synchronized int getPasteMode() {
		return pasteMode;
	}

	static boolean isMusic(File file) {

		Uri uri = Uri.fromFile(file);
		String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
				MimeTypeMap.getFileExtensionFromUrl(uri.toString()));

		if (type == null)
			return false;
		else
			return (type.toLowerCase().startsWith("audio/"));

	}

	static boolean isVideo(File file) {

		Uri uri = Uri.fromFile(file);
		String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
				MimeTypeMap.getFileExtensionFromUrl(uri.toString()));

		if (type == null)
			return false;
		else
			return (type.toLowerCase().startsWith("video/"));
	}

	public static boolean isPicture(File file) {

		Uri uri = Uri.fromFile(file);
		String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
				MimeTypeMap.getFileExtensionFromUrl(uri.toString()));

		if (type == null)
			return false;
		else
			return (type.toLowerCase().startsWith("image/"));
	}

	public static boolean isProtected(File path) {
		return (!path.canRead() && !path.canWrite());
	}

	public static boolean isUnzippable(File path) {
		return (path.isFile() && path.canRead() && path.getName().endsWith(
				".zip"));
	}

	public static boolean isRoot(File dir) {

		return dir.getAbsolutePath().equals("/");
	}

	public static boolean isSdCard(File file) {

		try {
			return (file.getCanonicalPath().equals(Environment
					.getExternalStorageDirectory().getCanonicalPath()));
		} catch (IOException e) {
			return false;
		}

	}

	public static Drawable getIcon(Context mContext, FileListEntry file) {

		if (file.isDir()) // dir
		{
			return mContext.getResources().getDrawable(R.drawable.filetype_dir);
		} else // file
		{
			String fileName = file.getName();
			if (fileName.endsWith(".apk")) {
				return mContext.getResources().getDrawable(
						R.drawable.filetype_apk);
			}
			if (fileName.endsWith(".zip")) {
				return mContext.getResources().getDrawable(
						R.drawable.filetype_zip);
			} 
			// else if (Util.isMusic(file)) {
			// return mContext.getResources().getDrawable(
			// R.drawable.filetype_music);
			// } else if (Util.isVideo(file)) {
			// return mContext.getResources().getDrawable(
			// R.drawable.filetype_video);
			// } else if (Util.isPicture(file)) {
			// return mContext.getResources().getDrawable(
			// R.drawable.filetype_image);
			// } 
			else {
				return mContext.getResources().getDrawable(
						R.drawable.filetype_generic);
			}
		}

	}

	// public static String prepareMeta(FileListEntry file,
	// StorageDropboxActivity context) {
	//
	// File f = file.getPath();
	// try {
	// if (isProtected(f)) {
	// return context.getString(R.string.system_path);
	// }
	// if (file.getPath().isFile()) {
	// return context.getString(R.string.size_is,
	// FileUtils.byteCountToDisplaySize(file.getSize()));
	// }
	//
	// } catch (Exception e) {
	// Log.e(Util.class.getName(), e.getMessage());
	// }
	// return "";
	// }

}
