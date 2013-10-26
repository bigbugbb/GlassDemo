package com.team5hinf.support.tasks;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import android.util.Log;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import com.team5hinf.support.DemoGlobals;
import com.team5hinf.support.activity.MainActivity;
import com.team5hinf.support.clients.AbstractClient;
import com.team5hinf.support.data.CalendarEvent;

public class CalendarAsyncTask extends CommonAsyncTask {
	
	public static final int TASK_UPDATE_EVENTS = 0;
		
	protected Calendar		mService;	
	protected MainActivity  mActivity;
	
	protected int mTaskID;

	CalendarAsyncTask(int taskID, AbstractClient client) {
		super(client);
		mService  = (Calendar) client.getService();
		mActivity = (MainActivity) client.getActivity();

		mTaskID = taskID;
	}
	
	public static void run(int taskID, AbstractClient client) {
		new CalendarAsyncTask(taskID, client).execute();
	}

	@Override
	protected void doInBackground() throws IOException {	
		switch (mTaskID) {
		case TASK_UPDATE_EVENTS:
			doUpdateEventsTask();
			break;
		default:			
			break;
		}
		
		Log.d("", "");
	}
	
	private void doUpdateEventsTask() throws IOException {
//		CalendarList feed = mService.calendarList().list().setFields(CalendarInfo.FEED_FIELDS).execute();
//		mModel.reset(feed.getItems());
		
		String pageToken = null;
		do {
			Events events = mService.events().list(DemoGlobals.DEFAULT_ACCOUNT_NAME).
					setPageToken(pageToken).execute();
			
			List<Event> items = events.getItems();
			for (Event event : items) {		
				final CalendarEvent calendarEvent = new CalendarEvent(
					event.getId(),
					event.getSummary(), 
					event.getDescription(), 
					new Date(event.getStart().getDateTime().getValue()),
					new Date(event.getEnd().getDateTime().getValue())
				);		
				mActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						mActivity.updateListItem(calendarEvent);
					}
					
				});
			}
			pageToken = events.getNextPageToken();
		} while (pageToken != null);
	}
	
}
