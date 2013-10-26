package com.team5hinf.support.data;

import java.util.Date;

public class CalendarEvent {
	
	private String mEventID;
	private String mTitle;
	private String mDescription;
	private Date   mStartTime;
	private Date   mEndTime;
	
	public CalendarEvent(String eid, String title, String description, Date startTime, Date endTime) {
		mEventID     = eid;
		mTitle       = title;
		mDescription = description;
		mStartTime   = startTime;
		mEndTime     = endTime;
	}
	
	public String getEventID() {
		return mEventID;
	}
	
	public String getTitle() {
		return mTitle;
	}
	
	public String getDescription() {
		return mDescription;
	}
	
	public Date getStartTime() {
		return mStartTime;
	}
	
	public Date getEndTime() {
		return mEndTime;
	}
}
