package com.team5hinf.support.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;

import com.team5hinf.support.DemoGlobals;
import com.team5hinf.support.R;
import com.team5hinf.support.clients.CalendarClient;
import com.team5hinf.support.data.CalendarEvent;
import com.team5hinf.support.data.CalendarEventList;
import com.team5hinf.support.tasks.CalendarAsyncTask;

public class MainActivity extends Activity {

	public static final String TAG = "MainActivity";
	
	private CalendarClient mCalendarClient;
	
	private ListView       mEventListView;
	private SimpleAdapter  mListAdapter;
	private ArrayList<HashMap<String, String>> mListItems;
	
	public final static String KEY_ID           = "KEY_ID";
	public final static String KEY_TITLE        = "KEY_TITLE";
	public final static String KEY_DISPLAY_TIME = "KEY_DISPLAY_TIME";
	public final static String KEY_DESCRIPTION  = "KEY_DESCRIPTION";	
	public final static String KEY_START_TIME   = "KEY_START_TIME";
	public final static String KEY_END_TIME     = "KEY_END_TIME";	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		setupViews();
		
		mCalendarClient = new CalendarClient(this);	
	}
	
	protected void setupViews() {		
		mListItems = CalendarEventList.getInstance();
		mListAdapter = new SimpleAdapter(
			this, mListItems, R.layout.list_item_events, 
			new String[] { KEY_TITLE, KEY_START_TIME, KEY_END_TIME }, 
			new int[] { R.id.title, R.id.start_time, R.id.end_time }
		);
		mListAdapter.setViewBinder(new ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data, String textRepresentation) {
            	if (view instanceof TextView && data instanceof String) {
            		TextView textView = (TextView) view;
            		textView.setText((String) data);
            		return true;
            	}
                return false;
            }
        });
		mEventListView = (ListView) findViewById(R.id.listview_events);
		mEventListView.setAdapter(mListAdapter);        
		mEventListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent i = new Intent(MainActivity.this, SessionActivity.class);							
				i.putExtra("EVENT_ID", position);
				startActivity(i);
			}	
        });
	}
	
	@Override
	protected void onResume() {
	    super.onResume();
	    
    	if (mCalendarClient.checkGooglePlayServicesAvailable()) {
    		mCalendarClient.haveGooglePlayServices();
	    }
    	
    	if (mCalendarClient.getSelectedAccountName() != null) {
    		CalendarAsyncTask.run(CalendarAsyncTask.TASK_UPDATE_EVENTS, mCalendarClient);
    	}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_refresh:
			if (mCalendarClient.getSelectedAccountName() != null) {
	    		CalendarAsyncTask.run(CalendarAsyncTask.TASK_UPDATE_EVENTS, mCalendarClient);
	    	}
			break;
		case R.id.menu_accounts:
			mCalendarClient.chooseAccount();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	
	    switch (requestCode) {
    	case DemoGlobals.REQUEST_GOOGLE_PLAY_SERVICES:
	        if (resultCode == Activity.RESULT_OK) {
	        	mCalendarClient.haveGooglePlayServices();
	        } else {
	        	mCalendarClient.checkGooglePlayServicesAvailable();
	        }
	        break;
    	case DemoGlobals.REQUEST_AUTHORIZATION:
	        if (resultCode != Activity.RESULT_OK) {	        	
	        	mCalendarClient.chooseAccount();
	        }
	        break;
    	case DemoGlobals.REQUEST_ACCOUNT_PICKER:
	        if (resultCode == Activity.RESULT_OK && data != null && data.getExtras() != null) {
	        	String accountName = data.getExtras().getString(AccountManager.KEY_ACCOUNT_NAME);
	        	if (accountName != null) {
	        		mCalendarClient.updateAccount(accountName);
	        	}	        	
	        }
	        break;
	    }
	}
	
	public void updateListItem(CalendarEvent event) {
		HashMap<String, String> item = new HashMap<String, String>();
		item.put(KEY_ID,          event.getEventID());
		item.put(KEY_TITLE,       event.getTitle());
		item.put(KEY_DESCRIPTION, event.getDescription());
		item.put(KEY_START_TIME,  event.getStartTime().toString());
		item.put(KEY_END_TIME,    event.getEndTime().toString());
		mListItems.add(item);		
		mListAdapter.notifyDataSetChanged();
	}

}
