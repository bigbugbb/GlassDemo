package com.team5hinf.support.clients;

import android.app.Activity;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.team5hinf.support.DemoGlobals;

public class CalendarClient extends AbstractClient {

	public CalendarClient(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getScope() {
		// TODO Auto-generated method stub
		return CalendarScopes.CALENDAR;
	}

	@Override
	public AbstractGoogleJsonClient createService() {
		return new Calendar.Builder(mHttpTransport, mJsonFactory, mCredential)
			.setApplicationName(DemoGlobals.APP_NAME).build();		
	}
}
