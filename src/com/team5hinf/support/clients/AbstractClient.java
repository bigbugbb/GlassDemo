package com.team5hinf.support.clients;

import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

import android.app.Activity;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.services.json.AbstractGoogleJsonClient;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.team5hinf.support.DemoGlobals;

import edu.neu.android.wocketslib.support.DataStorage;

public abstract class AbstractClient {
	
	protected final Level         mLoggingLevel  = Level.OFF;		
	protected final JsonFactory   mJsonFactory   = GsonFactory.getDefaultInstance();
	protected final HttpTransport mHttpTransport = AndroidHttp.newCompatibleTransport();		
	protected GoogleAccountCredential mCredential;				
		
	protected Activity mActivity;	
	protected AbstractGoogleJsonClient mClient;

	protected AbstractClient(Activity activity) {
		mActivity = activity;
		
		// enable logging
	    Logger.getLogger("com.google.api.client").setLevel(mLoggingLevel);
	    // Google Accounts
	    mCredential = GoogleAccountCredential.usingOAuth2(mActivity, Collections.singleton(getScope()));
	    String accountName = DataStorage.GetValueString(
	    	mActivity.getApplicationContext(), DemoGlobals.KEY_ACCOUNT_NAME, DemoGlobals.DEFAULT_ACCOUNT_NAME
	    );
	    mCredential.setSelectedAccountName(accountName);
	    // Tasks client
	    mClient = createService();
	}
	
	public AbstractGoogleJsonClient getService() {
		return mClient;
	}	
	
	public Activity getActivity() {
		return mActivity;
	}
	
	public void showGooglePlayServicesAvailabilityErrorDialog(final int connectionStatusCode) {
		mActivity.runOnUiThread(new Runnable() {
	    	public void run() {
	    		GooglePlayServicesUtil.getErrorDialog(
	    			connectionStatusCode, mActivity, DemoGlobals.REQUEST_GOOGLE_PLAY_SERVICES
	    		).show();
	    	}
	    });
	}

	/** Check that Google Play services APK is installed and up to date. */
	public boolean checkGooglePlayServicesAvailable() {
	    final int connectionStatusCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mActivity);
	    if (GooglePlayServicesUtil.isUserRecoverableError(connectionStatusCode)) {
	    	showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
	    	return false;
	    }
	    return true;
	}

	public void haveGooglePlayServices() {
	    // check if there is already an account selected
		String accountName = mCredential.getSelectedAccountName();
	    if (accountName == null) {
	    	// ask user to choose account
	    	chooseAccount();
	    } 
	}

	public void chooseAccount() {
	    mActivity.startActivityForResult(
	    	mCredential.newChooseAccountIntent(), DemoGlobals.REQUEST_ACCOUNT_PICKER
	    );
	}
	
	public void updateAccount(String accountName) {
		mCredential.setSelectedAccountName(accountName);	        		
		DataStorage.SetValue(mActivity.getApplicationContext(), DemoGlobals.KEY_ACCOUNT_NAME, accountName);
	}
	
	public String getSelectedAccountName() {
		return mCredential.getSelectedAccountName();
	}
	
	public abstract String getScope();
	public abstract AbstractGoogleJsonClient createService();
}	
