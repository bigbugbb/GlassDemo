package com.team5hinf.support.clients;

import android.app.Activity;

import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.services.mirror.Mirror;
import com.team5hinf.support.DemoGlobals;

public class MirrorClient extends AbstractClient {	
	
	static final String GLASS_TIMELINE_SCOPE = "https://www.googleapis.com/auth/glass.timeline";
	static final String GLASS_LOCATION_SCOPE = "https://www.googleapis.com/auth/glass.location";
	static final String SCOPE = String.format("%s %s", GLASS_TIMELINE_SCOPE, GLASS_LOCATION_SCOPE);	
	
	public MirrorClient(Activity activity) {
		super(activity);
	}

	@Override
	public final String getScope() {		
		return SCOPE;
	}

	@Override
	public AbstractGoogleJsonClient createService() {
		return new Mirror.Builder(mHttpTransport, mJsonFactory, mCredential)
			.setApplicationName(DemoGlobals.APP_NAME).build();		
	}
}
