package com.team5hinf.support.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

public class MediaCapturer {
	
	public static final int REQUEST_CAMERA_CAPTURE_IMAGE = 100;
	public static final int REQUEST_CAMERA_CAPTURE_VIDEO = 200;
	
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;

	// directory name to store captured images and videos
	private static final String MEDIA_DIRECTORY_NAME = "SupportMe";

	private Uri mImageUri;
	private Uri mVideoUri;
	
	private Activity mActivity;
	private Context  mContext;

	public MediaCapturer(Activity activity) {
		mActivity = activity;
		mContext  = mActivity.getApplicationContext();
	}

	/*
	 * Capturing Camera Image will lauch camera app requrest image capture
	 */
	public void captureImage() {
		
		if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			Toast.makeText(mContext, "Sorry! Your device doesn't support camera", Toast.LENGTH_LONG).show();
			return;
		}

		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		mImageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

		intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
		intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

		// start the image capture Intent
		mActivity.startActivityForResult(intent, REQUEST_CAMERA_CAPTURE_IMAGE);
	}

	/*
	 * Recording video
	 */
	public void recordVideo() {
		
		if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			Toast.makeText(mContext, "Sorry! Your device doesn't support camera", Toast.LENGTH_LONG).show();
			return;
		}
		
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

		mVideoUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
	
		intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);             
        intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 60);
        intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 5 * 1024 * 1024);
        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, mVideoUri);

		// start the video capture Intent
		mActivity.startActivityForResult(intent, REQUEST_CAMERA_CAPTURE_VIDEO);
	}
	
	public Uri getImageUri() {
		return mImageUri;
	}
	
	public Uri getVideoUri() {
		return mVideoUri;
	}

	/**
	 * ------------ Helper Methods ---------------------- 
	 * */

	/*
	 * Creating file uri to store image/video
	 */
	private Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/*
	 * returning image / video
	 */
	private File getOutputMediaFile(int type) {

		// External sdcard location
		File mediaStorageDir = new File(
			Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), MEDIA_DIRECTORY_NAME
		);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(MEDIA_DIRECTORY_NAME, "Oops! Failed create " + MEDIA_DIRECTORY_NAME + " directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
		} else if (type == MEDIA_TYPE_VIDEO) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
		} else {
			return null;
		}

		return mediaFile;
	}
}