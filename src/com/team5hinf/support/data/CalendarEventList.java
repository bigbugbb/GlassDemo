package com.team5hinf.support.data;

import java.util.ArrayList;
import java.util.HashMap;

public class CalendarEventList extends ArrayList<HashMap<String, String>> {

	private static final long serialVersionUID = 1L;
	
	private static CalendarEventList sInstance;
	
	public static CalendarEventList getInstance() {
		if (sInstance == null) {
			sInstance = new CalendarEventList();
		}
		return sInstance;
	}
	
	private CalendarEventList() {}
	
	@Override 
	public boolean add(HashMap<String, String> event) {
		String eid = event.get("KEY_ID");
		for (HashMap<String, String> e : this) {
			if (e.get("KEY_ID").equals(eid)) {
				return false;
			}
		}
		return super.add(event);		
	}
}
