/*
 * Copyright (c) 2012 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.team5hinf.support.tasks;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.mirror.Mirror;
import com.google.api.services.mirror.Mirror.Timeline;
import com.google.api.services.mirror.model.MenuItem;
import com.google.api.services.mirror.model.NotificationConfig;
import com.google.api.services.mirror.model.TimelineItem;
import com.google.api.services.mirror.model.TimelineListResponse;
import com.google.common.io.ByteStreams;
import com.team5hinf.support.DemoAppManager;
import com.team5hinf.support.DemoGlobals;
import com.team5hinf.support.R;
import com.team5hinf.support.activity.SessionActivity;
import com.team5hinf.support.clients.AbstractClient;

import edu.neu.android.wocketslib.support.DataStorage;

/**
 * Asynchronously load the tasks.
 * 
 * @author bigbug
 */
public class MirrorAsyncTask extends CommonAsyncTask {
	
	public static final int TASK_SEND_TO_GLASS     = 0;
	public static final int TASK_DELETE_FROM_GLASS = 1;
	
	protected Mirror mService;
	protected SessionActivity mActivity;
	
	protected int    mTaskID; 
	protected Object mParam;	

	MirrorAsyncTask(int taskID, AbstractClient client, Object param) {
		super(client);
		mService  = (Mirror) client.getService();
		mActivity = (SessionActivity) client.getActivity();
		
		mTaskID = taskID;
		mParam  = param;
	}
	
	public static void run(AbstractClient client, int taskID, Object param) {
		new MirrorAsyncTask(taskID, client, param).execute();
	}

	@Override
	protected void doInBackground() throws IOException {
		switch (mTaskID) {
		case TASK_SEND_TO_GLASS:
			Context context = DemoAppManager.getAppContext();
			sendTimeline(
				(String) mParam, 
				DataStorage.GetValueString(context, DemoGlobals.KEY_ATTACHED_IMAGE_URL, null),
				DataStorage.GetValueString(context, DemoGlobals.KEY_ATTACHED_VIDEO_URL, null)
			);
			mActivity.runOnUiThread(new Runnable() {

				@Override
				public void run() {					
					Toast.makeText(
						DemoAppManager.getAppContext(), R.string.send_successfully, Toast.LENGTH_SHORT
					).show();					
					mActivity.onMessageSent();
				}
				
			});
			break;
		case TASK_DELETE_FROM_GLASS:
			List<TimelineItem> result = new ArrayList<TimelineItem>();
		    try {
		    	Timeline.List request = mService.timeline().list();
		    	do {
		    		TimelineListResponse timelineItems = request.execute();
		    		if (timelineItems.getItems() != null && timelineItems.getItems().size() > 0) {
		    			result.addAll(timelineItems.getItems());
		    			request.setPageToken(timelineItems.getNextPageToken());
		    		} else {
		    			break;
		    		}
		    	} while (request.getPageToken() != null && request.getPageToken().length() > 0);
		    	
		    	for (TimelineItem item : result) {
		    		mService.timeline().delete(item.getId()).execute();
		    	}
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
		    
		    mActivity.runOnUiThread(new Runnable() {

				@Override
				public void run() {					
					Toast.makeText(
						DemoAppManager.getAppContext(), R.string.delete_successfully, Toast.LENGTH_SHORT
					).show();
					mActivity.onMessageDeleted();
				}
				
			});
			break;
		default:
			break;
		}	
	}
	
	private void sendTimeline(String message, String photoUrl, String videoUrl) throws IOException {					
	  	TimelineItem timelineItem = new TimelineItem();	  	
	  	timelineItem.setText(message);
	  	timelineItem.setSpeakableText(message);
	  	
	  	List<MenuItem> menuItemList = new ArrayList<MenuItem>();
  		menuItemList.add(new MenuItem().setAction("READ_ALOUD"));
  		timelineItem.setMenuItems(menuItemList);
	  	
	  	ByteArrayContent photoContent = null;
	  	if (photoUrl != null) {
	  		URL url = new URL(photoUrl);
	  		photoContent = new ByteArrayContent("image/jpeg", ByteStreams.toByteArray(url.openStream()));
	  	}
	  	
	  	if (videoUrl != null) {
//	  		List<MenuItem> menuItemList = new ArrayList<MenuItem>();
//	  		menuItemList.add(new MenuItem().setAction("PLAY_VIDEO").setPayload(videoUrl));
//	  		timelineItem.setMenuItems(menuItemList);
	  		
	  		URL url = new URL(videoUrl);
			mService.timeline().insert(
				timelineItem, new ByteArrayContent("video/mp4", ByteStreams.toByteArray(url.openStream()))
			).execute();
	  	}
	  	
	  	NotificationConfig notification = new NotificationConfig();
		notification.setLevel("DEFAULT");	
		timelineItem.setNotification(notification);
		
		if (photoContent != null) {
			mService.timeline().insert(timelineItem, photoContent).execute();
		} else {
			mService.timeline().insert(timelineItem).execute();
		}
	}
	
	protected void onIOError() {
		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mActivity.onError();
			}
			
		});
	}
//
//	private void sendTimelineWithVideo() throws IOException {
//		NotificationConfig notification = new NotificationConfig();
//		notification.setLevel("DEFAULT");
//		
//	  	TimelineItem timelineItem = new TimelineItem();	  	
//		List<MenuItem> menuItemList = new ArrayList<MenuItem>();	
//		menuItemList.add(
//			new MenuItem().setAction("PLAY_VIDEO").setPayload(
//				"http://www.kaltura.com/p/309/sp/0/playManifest/entryId/1_rcit0qgs/format/url/flavorParamId/301971/video.mp4"
//			)
//		);
//		timelineItem.setNotification(notification);
//		timelineItem.setMenuItems(menuItemList);
//		mService.timeline().insert(timelineItem).execute();
//	}
//  
//	private void sendTimelineWithAttachedPicture() throws IOException {
//		NotificationConfig notification = new NotificationConfig();
//		notification.setLevel("DEFAULT");
//		
//	  	TimelineItem timelineItem = new TimelineItem();	  			
//		timelineItem.setNotification(notification);
//		URL url = new URL("file:///sdcard/uscteens/appdata/rewards/background.png");
//		mService.timeline().insert(
//			timelineItem, new ByteArrayContent("image/png", ByteStreams.toByteArray(url.openStream()))
//		).execute();				
//	}
//  
//	private void sendTimelineWithAttachedVideo() throws IOException {
//		NotificationConfig notification = new NotificationConfig();
//		notification.setLevel("DEFAULT");
//		
//	  	TimelineItem timelineItem = new TimelineItem();	  			
//		timelineItem.setNotification(notification);
//		URL url = new URL("file:///sdcard/DCIM/Camera/test.mp4");
//		mService.timeline().insert(
//			timelineItem, new ByteArrayContent("video/mp4", ByteStreams.toByteArray(url.openStream()))
//		).execute();				
//	}  	
//  
//	private void sendTimelineWithDeliveryTime() throws IOException {
//		DateTime dt = new DateTime(System.currentTimeMillis() + 60000);
//		NotificationConfig notification = new NotificationConfig();
//		notification.setLevel("DEFAULT");	
//		
//		notification.setDeliveryTime(dt);
//	  	TimelineItem timelineItem = new TimelineItem();
//	  	timelineItem.setNotification(notification);
//	  	timelineItem.setText(new Date().toString() + "delivery");
//	  	mService.timeline().insert(timelineItem).execute();
//	}
//  
//	private void listTimelines() throws IOException {
//		//TimelineItem timelineItem = new TimelineItem();
//		Mirror.Timeline.List list = mService.timeline().list();
//		TimelineListResponse response = list.execute();
//		Log.i("", "");
//	}	
}
