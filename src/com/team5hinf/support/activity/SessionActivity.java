package com.team5hinf.support.activity;

import java.util.HashMap;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.team5hinf.support.DemoGlobals;
import com.team5hinf.support.R;
import com.team5hinf.support.clients.MirrorClient;
import com.team5hinf.support.data.CalendarEventList;
import com.team5hinf.support.tasks.MirrorAsyncTask;
import com.team5hinf.support.utils.MediaCapturer;

import edu.neu.android.wocketslib.support.DataStorage;

public class SessionActivity extends Activity {

	private TextView mTextViewTitle;
	private TextView mTextViewDescription;
	private TextView mTextViewStartTime;
	private TextView mTextViewEndTime;		
	
	private Button mBtnBack;
	private Button mBtnSend;
	private Button mBtnDelete;
	private Button mBtnAttachPhoto;
	private Button mBtnAttachVideo;
	
	private EditText mEditText;
	
	private ProgressDialog mProgressDialog;
	
	private MirrorClient mMirrorClient;
	
	private HashMap<String, String> mEvent;
	
	private MediaCapturer mMediaCapturer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_session);	
		
		int id = getIntent().getIntExtra("EVENT_ID", -1);
		mEvent = CalendarEventList.getInstance().get(id);		
		mMirrorClient = new MirrorClient(this);
		
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);		
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setCancelable(false);
		
		mMediaCapturer = new MediaCapturer(this);
		
		setupViews();
	}
	
	private void setupViews() {
		
		mTextViewTitle       = (TextView) findViewById(R.id.tv_title);
		mTextViewDescription = (TextView) findViewById(R.id.tv_description);
		mTextViewStartTime   = (TextView) findViewById(R.id.tv_start_time);
		mTextViewEndTime     = (TextView) findViewById(R.id.tv_end_time);
		
		mTextViewTitle.setText(mEvent.get(MainActivity.KEY_TITLE));
		mTextViewDescription.setText("Description: " + mEvent.get(MainActivity.KEY_DESCRIPTION));
		mTextViewStartTime.setText("From: " + mEvent.get(MainActivity.KEY_START_TIME));
		mTextViewEndTime.setText(" To  : " + mEvent.get(MainActivity.KEY_END_TIME));
		
		mEditText = (EditText) findViewById(R.id.edit_message);			 
		
		mBtnBack        = (Button) findViewById(R.id.btn_back);
		mBtnSend        = (Button) findViewById(R.id.btn_send);
		mBtnDelete      = (Button) findViewById(R.id.btn_delete);
		mBtnAttachPhoto = (Button) findViewById(R.id.btn_attach_photo);
		mBtnAttachVideo = (Button) findViewById(R.id.btn_attach_video);
				
		mBtnBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				DataStorage.SetValue(getApplicationContext(), DemoGlobals.KEY_ATTACHED_IMAGE_URL, null);
				DataStorage.SetValue(getApplicationContext(), DemoGlobals.KEY_ATTACHED_VIDEO_URL, null);
				finish();
			}
			
		});
		
		mBtnSend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String message = mEditText.getText().toString();
				MirrorAsyncTask.run(mMirrorClient, MirrorAsyncTask.TASK_SEND_TO_GLASS, message);
				
				mProgressDialog.setTitle("Sending Message to Glass...");
				mProgressDialog.setMessage("It may take a few seconds to send your message.\nJust a moment...");
				mProgressDialog.show();
				
				mBtnSend.setEnabled(false);
			}
			
		});
		
		mBtnDelete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MirrorAsyncTask.run(mMirrorClient, MirrorAsyncTask.TASK_DELETE_FROM_GLASS, null);
				mBtnDelete.setEnabled(false);
				
				mProgressDialog.setTitle("Delete your glass timelines...");
				mProgressDialog.setMessage("It may take a few seconds to delte your timelines.\nJust a moment...");
				mProgressDialog.show();
			}
			
		});
		
		mBtnAttachPhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMediaCapturer.captureImage();
			}
			
		});
		
		mBtnAttachVideo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mMediaCapturer.recordVideo();
			}
			
		});
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
    	if (mMirrorClient.checkGooglePlayServicesAvailable()) {
    		mMirrorClient.haveGooglePlayServices();
	    }
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	public void onMessageSent() {
		mBtnSend.setEnabled(true);
		mProgressDialog.dismiss();		
	}
	
	public void onMessageDeleted() {
		mBtnDelete.setEnabled(true);	
		mProgressDialog.dismiss();
	}
	
	public void onError() {
		mBtnSend.setEnabled(true);
		mProgressDialog.dismiss();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    switch (requestCode) {
    	case DemoGlobals.REQUEST_GOOGLE_PLAY_SERVICES:
	        if (resultCode == Activity.RESULT_OK) {
	        	mMirrorClient.haveGooglePlayServices();
	        } else {
	        	mMirrorClient.checkGooglePlayServicesAvailable();
	        }
	        break;
    	case DemoGlobals.REQUEST_AUTHORIZATION:
	        if (resultCode != Activity.RESULT_OK) {	        	
	        	mMirrorClient.chooseAccount();
	        }
	        break;
    	case DemoGlobals.REQUEST_ACCOUNT_PICKER:
	        if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
	        	String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
	        	if (accountName != null) {
	        		mMirrorClient.updateAccount(accountName);
	        	}	        	
	        }
	        break;
    	case MediaCapturer.REQUEST_CAMERA_CAPTURE_IMAGE:
			if (resultCode == RESULT_OK) {
				Uri uri = mMediaCapturer.getImageUri();
    			DataStorage.SetValue(getApplicationContext(), DemoGlobals.KEY_ATTACHED_IMAGE_URL, uri.toString());
    			mBtnAttachPhoto.setText("Attach Photo *");
			} else if (resultCode == RESULT_CANCELED) {
				// user cancelled Image capture
				Toast.makeText(
					getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT
				).show();
			} else {
				// failed to capture image
				Toast.makeText(
					getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT
				).show();
			}
			break;
    	case MediaCapturer.REQUEST_CAMERA_CAPTURE_VIDEO:
    		if (resultCode == RESULT_OK) {
    			Uri uri = mMediaCapturer.getVideoUri();   			
    			DataStorage.SetValue(getApplicationContext(), DemoGlobals.KEY_ATTACHED_VIDEO_URL, uri.toString());
    			mBtnAttachVideo.setText("Attach Video *");
			} else if (resultCode == RESULT_CANCELED) {
				// user cancelled recording
				Toast.makeText(
					getApplicationContext(), "User cancelled video recording", Toast.LENGTH_SHORT
				).show();
			} else {
				// failed to record video
				Toast.makeText(
					getApplicationContext(),"Sorry! Failed to record video", Toast.LENGTH_SHORT
				).show();
			}
    		break;
    	default:
    		break;
	    }
	}

}
