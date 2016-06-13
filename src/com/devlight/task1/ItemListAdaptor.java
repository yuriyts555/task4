package com.devlight.task1;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import com.devlight.task1.task.Task;
import com.devlight.task1.utils.PrefsHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ItemListAdaptor extends BaseAdapter{
	
	
	public static final int SORTMODE_A_Z = 0;
	public static final int SORTMODE_Z_A = 1;
	public static final int SORTMODE_DATE_DEC = 2;
	public static final int SORTMODE_DATE_INC = 3;
	
	Context mContext;
	LayoutInflater mLayoutInflater;	
	ArrayList<Task> mTasks; //Tasks list
	
	int mColors[] = new int[3];
	
	int mDefaultSortMode = SORTMODE_A_Z;
	
	PrefsHelper mPrefsHelper;
	
	

	//Comparator AZ
	Comparator<Task> mCompAZ = new Comparator<Task>() {

		@Override
		public int compare(Task lhs, Task rhs) {

			return lhs.getHeader().compareTo(rhs.getHeader());
		}
	};
	
	//Comparator ZA
	Comparator<Task> mCompZA = new Comparator<Task>() {

		@Override
		public int compare(Task lhs, Task rhs) {

			return rhs.getHeader().compareTo(lhs.getHeader());
		}
	};
	
	
	//Comparator Date inc
	Comparator<Task> mCompDateInc = new Comparator<Task>() {

		@Override
		public int compare(Task lhs, Task rhs) {

			return (int) (lhs.getStartTime() - rhs.getStartTime());
		}
	};
	
	//Comparator Date dec
	Comparator<Task> mCompDateDec = new Comparator<Task>() {

		@Override
		public int compare(Task lhs, Task rhs) {

			return (int) (rhs.getStartTime() - lhs.getStartTime());
		}
	};
	
	
	public ItemListAdaptor(Context context,ArrayList<Task> mTasks)
	{
		this.mTasks = mTasks;
		mContext = context;
		mLayoutInflater = LayoutInflater.from(mContext);
		
		mPrefsHelper = new PrefsHelper(mContext);
		
		mDefaultSortMode = mPrefsHelper.getDefaultSortMode();
		
        updateColors();
	}
	

	public void changeSortMode(int mode)
	{
		mDefaultSortMode = mode;
		mPrefsHelper.saveDefaultSortMode(mDefaultSortMode);	
		
		sortItems();
	}
	
	
	public void sortItems()
	{
		

		
		switch (mDefaultSortMode) {
		case SORTMODE_A_Z:	
			Collections.sort(mTasks, mCompAZ);
			break;
		case SORTMODE_Z_A:	
			Collections.sort(mTasks, mCompZA);
			break;
		case SORTMODE_DATE_DEC:		
			Collections.sort(mTasks, mCompDateDec);
			break;
		case SORTMODE_DATE_INC:
			Collections.sort(mTasks, mCompDateInc);
			break;


		}		
		
		
		notifyDataSetChanged();
	}
	
	public void updateColors() {
		
		SharedPreferences mPrefs = mContext.getSharedPreferences(PrefsHelper.PREFS_NAME, Context.MODE_PRIVATE);
		mColors[0] = mPrefs.getInt(PrefsHelper.KEY_COLOR0, mContext.getResources().getColor(R.color.green_bkg));
		mColors[1] = mPrefs.getInt(PrefsHelper.KEY_COLOR1, mContext.getResources().getColor(R.color.yellow_bkg));
		mColors[2] = mPrefs.getInt(PrefsHelper.KEY_COLOR2, mContext.getResources().getColor(R.color.red_bkg));
		
		notifyDataSetChanged();
		
		
	}
	
	public ArrayList<Task> getTasks() //return tasks list
	{
		return mTasks;
	}
	
	public void  AddTask(Task task) //Add task
	{
		mTasks.add(task);
		sortItems();
	}
	
	public void  ClearTasks() //Add task
	{
		mTasks.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {

		return mTasks.size();
	}

	@Override
	public Task getItem(int position) {
		
		if (position>=mTasks.size()) return null;
		return mTasks.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ViewHolderItem viewHolder;

		if (convertView == null) 
			{
			   convertView=mLayoutInflater.inflate(R.layout.task_item,null);
			   viewHolder = new ViewHolderItem();
			   
			   viewHolder.tvHeader = (TextView) convertView.findViewById(R.id.tvHeader); 
			   viewHolder.tvComment = (TextView) convertView.findViewById(R.id.tvComment); 
			   
			   convertView.setTag(viewHolder);
			} else 
			{
				viewHolder = (ViewHolderItem) convertView.getTag();
			}
		
		Task mTask = getItem(position);
		

		
		if (mTask !=null)
		{
			
			viewHolder.tvHeader.setText(mTask.getHeader());
			viewHolder.tvComment.setText(mTask.getStrTimePresentation() );

			//set background colors if task started, finished or didn't started
			if(mTask.getStartTime() ==-1 && mTask.getEndTime()==-1) 
				convertView.setBackgroundColor(mColors[0]);
			
			if(mTask.getStartTime() !=-1 && mTask.getEndTime()==-1) 
				convertView.setBackgroundColor(mColors[1]);
			
			if(mTask.getStartTime() !=-1 && mTask.getEndTime()!=-1) 
				convertView.setBackgroundColor(mColors[2]);
		}
			
		
		return convertView;
	}
	
	
	static class ViewHolderItem {
		TextView tvHeader;
		TextView tvComment;
	}


	public int getDefautlSortMode() {
	
		return mDefaultSortMode;
	}




}
