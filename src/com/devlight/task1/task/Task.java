package com.devlight.task1.task;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Task implements Parcelable{
	

    final static String KEY_HEADER = "mHeader";
    final static String KEY_COMMENT = "mComment";
    final static String KEY_STARTTIME = "mStartTime";
    final static String KEY_ENDTIME = "mEndTime";
	
	
	String mHeader;
	String mComment;
	
	long mStartTime = - 1;
	long mEndTime = -1;
	
	
	static final String DATE_TIME_PRESENTATION = "yyyy.MM.dd HH:mm";
	
	
	
	public Task(String mHeader,String mComment)
	{
		this.mHeader = mHeader;
		this.mComment = mComment;
	}
	
	
	public Task()
	{

	}
	
	public long getStartTime()
	{
		return mStartTime;
	}
	
	public long getEndTime()
	{
		return mEndTime;
	}
	
	
	public void setStartTime(long value)
	{
		mStartTime = value;
	}
	
	public void setEndTime(long value)
	{
		mEndTime = value;
	}
	
	
	public void setStrings(String mHeader,String mComment)
	{
		this.mHeader = mHeader;
		this.mComment = mComment;
	}
	
	
	public String getHeader()
	{
		return mHeader;
	}
	
	public String getComment()
	{
		return mComment;
	}
	
	
	public String getStrStartTime()  //get Start date and time as String
	{
		if (mStartTime ==-1) return "-";
		
		SimpleDateFormat mDateFormat = new SimpleDateFormat(DATE_TIME_PRESENTATION,Locale.getDefault());
		
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(mStartTime);		
		
		String currentDateAndTime = mDateFormat.format(mCalendar.getTime());		
		
		return currentDateAndTime;
	}
	
	public String getStrEndTime()//get End date and time as String
	{
		if (mEndTime ==-1) return "-";
		
		SimpleDateFormat mDateFormat = new SimpleDateFormat(DATE_TIME_PRESENTATION,Locale.getDefault());
		
		
		Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(mEndTime);		
		
		String currentDateAndTime = mDateFormat.format(mCalendar.getTime());		
		
		return currentDateAndTime;
	}
	
	public String getStrSpentTime() //get spend time as String
	{
		if (mStartTime == -1 || mEndTime == -1) return "-";
		
		long mDifMinutes = (mEndTime - mStartTime)/(1000L*60L);  //Get difference in minutes
		
			   
	   long mHours = mDifMinutes/60L;  //Get hours
	   long mMinutes = mDifMinutes- mHours*60L;  //Get minutes
	   
	   return getTwoDigitString(mHours)+":"+getTwoDigitString(mMinutes);
	}
	
	
	String getTwoDigitString(long value) //return string from long in minimum two digit representation
	{
		
		if (value==0) return "00";
		
		if (value<10L) return "0"+Long.toString(value);
		
		return Long.toString(value);
	}
	
	
	public String getStrTimePresentation()  //get string dd:MM:yyyy hh:mm - dd:MM:yyyy hh:mm   hh:mm
	{
	  if (mStartTime == -1 && mEndTime == -1) return "";
	  if ( mEndTime == -1) return getStrStartTime();
		
		return getStrStartTime()+" - "+getStrEndTime()+" "+getStrSpentTime();
	}


	@Override
	public int describeContents() {

		return 0;
	}


	@Override
	public void writeToParcel(Parcel dest, int flags) {

		dest.writeString(mHeader);
		dest.writeString(mComment);
		dest.writeLong(mStartTime);
		dest.writeLong(mEndTime);
		
	}
	
	private Task(Parcel in) {
		mHeader = in.readString();
		mComment = in.readString();
		mStartTime = in.readLong();
		mEndTime = in.readLong();
	  } 
	
	
	/*
	public JSONObject  getJSON()  //return JSON object
	{
		JSONObject mJSON = new JSONObject();
		try {
			mJSON.put(KEY_HEADER, mHeader);
			mJSON.put(KEY_COMMENT, mComment);
			mJSON.put(KEY_STARTTIME, mStartTime);
			mJSON.put(KEY_ENDTIME, mEndTime);
		} catch (JSONException e) {
		
			e.printStackTrace();
			return null;
		}		
		
		return mJSON;
	}
	*/
	
	
	public String getJSONString()
	{
		Gson gson = new Gson();
		return gson.toJson(this);
	
	}
	
	/*
	public Task(String jSONString) //crate Task from json string
	{
		
		
		
		JSONObject mJSON;
		
		try {
			mJSON = new JSONObject(jSONString);
			
			mHeader = mJSON.getString(KEY_HEADER);
			mComment = mJSON.getString(KEY_COMMENT);			
			mStartTime = mJSON.getLong(KEY_STARTTIME);
			mEndTime = mJSON.getLong(KEY_ENDTIME);
			
		} catch (Exception e) {
		    e.printStackTrace();
		};
		
		
		Gson gson = new Gson();
		Task tmp = gson.fromJson(jSONString, Task.class);
		
		mHeader  = tmp.mHeader;
		mComment = tmp.mComment;
		
		mStartTime = tmp.mStartTime;
		mEndTime = tmp.mEndTime;
	}*/
	
	
    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {


    	@Override
    	public Task createFromParcel(Parcel in) {
    		return new Task(in);
    	}


    	@Override
    	public Task[] newArray(int size) {
    		return new Task[size];
    	}
    	};

}
