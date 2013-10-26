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

import android.os.AsyncTask;

import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.team5hinf.support.DemoGlobals;
import com.team5hinf.support.activity.MainActivity;
import com.team5hinf.support.clients.AbstractClient;
import com.team5hinf.support.utils.Utils;

/**
 * Asynchronous task that also takes care of common needs, such as displaying progress,
 * authorization, exception handling, and notifying UI when operation succeeded.
 * 
 * @author bigbug
 */
public abstract class CommonAsyncTask extends AsyncTask<Void, Void, Boolean> {

	protected AbstractClient mClient;

	CommonAsyncTask(AbstractClient client) {
		mClient = client;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected final Boolean doInBackground(Void... ignored) {
		try {
			doInBackground();
			return true;
		} catch (final GooglePlayServicesAvailabilityIOException availabilityException) {
			mClient.showGooglePlayServicesAvailabilityErrorDialog(
				availabilityException.getConnectionStatusCode()
			);
		} catch (UserRecoverableAuthIOException userRecoverableException) {
			if (userRecoverableException.getIntent() != null) {			
				mClient.getActivity().startActivityForResult(
					userRecoverableException.getIntent(), DemoGlobals.REQUEST_AUTHORIZATION
				);
			}
		} catch (IOException e) {
			Utils.logAndShow(mClient.getActivity(), MainActivity.TAG, e);
			onIOError();
		}
		return false;
	}
	
	protected void onIOError() {}

	abstract protected void doInBackground() throws IOException;
}
