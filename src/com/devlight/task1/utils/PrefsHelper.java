package com.devlight.task1.utils;

import java.util.ArrayList;

import com.devlight.task1.ItemListAdaptor;
import com.devlight.task1.task.Task;
import com.google.gson.Gson;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PrefsHelper {
	
	final static String TASKS_COUNT_KEY = "TASKSCOUNT"; 
	final static String TASK_KEY = "TASK"; 
	public final static String PREFS_NAME = "PREFS";
	
	public final static String KEY_COLOR0 = "COLOR0"; 
	public final static String KEY_COLOR1 = "COLOR1"; 
	public final static String KEY_COLOR2 = "COLOR2";
	public final static String DEFAULT_SORT_MODE = "DEFAULT_SORT_MODE"; 
	
	SharedPreferences mPrefs;
	Editor mEditor;
	
	public PrefsHelper(Context context)
	{
		mPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);	
		mEditor = mPrefs.edit();
	}
	
	
	public void save(String name,String value)
	{
		mEditor.putString(name, value);
		mEditor.commit();
	}
	
	
	public int getDefaultSortMode()
	{
		return mPrefs.getInt("DEFAULT_SORT_MODE", ItemListAdaptor.SORTMODE_A_Z);
	}
	
	public void save(String name,ArrayList<String> value)
	{
		for (int i=0;i<value.size();i++) 
		{
			mEditor.putString(name+Integer.toString(i), value.get(i));
		}
		mEditor.commit();
	}
	
	public void saveTasks(ArrayList<Task> tasks)
	{
		
		mEditor.putInt(TASKS_COUNT_KEY, tasks.size());
		
		for (int i=0;i<tasks.size();i++) 
		{
			String mStr = tasks.get(i).getJSONString();
			mEditor.putString(TASK_KEY+Integer.toString(i), mStr);
		}
		mEditor.commit();
	}
	
	
	public void loadTasks(ArrayList<Task> tasks)
	{
		Gson gson = new Gson();
		
		int count=mPrefs.getInt(TASKS_COUNT_KEY, 0);
		
		for (int i=0;i<count;i++) 
		{
			String mStr =mPrefs.getString(TASK_KEY+Integer.toString(i), "");
			//Task mTask = new Task(mStr);
			Task mTask = gson.fromJson(mStr, Task.class);
			tasks.add(mTask);

		}
		mEditor.commit();
	}


	public void saveDefaultSortMode(int mDefaultSortMode) {
		
		
		mEditor.putInt(DEFAULT_SORT_MODE, mDefaultSortMode);
		mEditor.commit();
		
	}

}
