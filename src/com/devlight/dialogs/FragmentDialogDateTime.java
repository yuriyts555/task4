package com.devlight.dialogs;

import java.util.Calendar;

import com.devlight.task1.IUpdater;
import com.devlight.task1.R;
import com.devlight.task1.R.id;
import com.devlight.task1.R.layout;
import com.devlight.task1.R.string;
import com.devlight.task1.task.Task;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

public class FragmentDialogDateTime extends DialogFragment{
	




	public static final int SET_START_TIME = 0; //input start data
	public static final int SET_END_TIME = 1; //input end data
	
	
	TextView tvHeader;
	DatePicker mDatePicker;
	TimePicker mTimePicker;
	
	Task mTask;  //editing task
	int curTimeType = SET_START_TIME; // start  or end  time
	Context mContext;
	//IUpdater mUpdater; //interface to update after dialog finish
	int mPosition = -1;  //task position
	

	
	public static FragmentDialogDateTime newInstance(Task mTask,int curTimeType,int mPosition) {
		FragmentDialogDateTime fInstance = new FragmentDialogDateTime();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putParcelable("mTask", mTask);
        args.putInt("curTimeType", curTimeType);
        args.putInt("mPosition", mPosition);
        fInstance.setArguments(args);

        return fInstance;
    }
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (getArguments() == null) return; 
		mTask = getArguments().getParcelable("mTask" );
		curTimeType = getArguments().getInt("curTimeType" );
		mPosition = getArguments().getInt("mPosition");
	}



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View mView = inflater.inflate(R.layout.dialog_date_time, container, false);
		
		tvHeader = (TextView) mView.findViewById(R.id.tvHeader);
		mDatePicker = (DatePicker) mView.findViewById(R.id.datePicker);
		mTimePicker = (TimePicker) mView.findViewById(R.id.timePicker);
		mTimePicker.setIs24HourView(true);
		
		long mTime = 0;
		
		if (curTimeType == SET_START_TIME) 
		{
			   tvHeader.setText(R.string.set_start_time);
			   mTime = mTask.getStartTime();
		}
		else
		{
			   tvHeader.setText(R.string.set_end_time);
			   mTime = mTask.getEndTime();
		}
		
		Calendar mCalendar = Calendar.getInstance();
		if (mTime == -1) mTime = mCalendar.getTimeInMillis();
		else mCalendar.setTimeInMillis(mTime);
		
		mDatePicker.updateDate(mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH), 
                mCalendar.get(Calendar.DAY_OF_MONTH));

		mTimePicker.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
		mTimePicker.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
		
		
		final Button btnOk = (Button) mView.findViewById(R.id.btnOk);
		
		btnOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				Calendar mCalendar = Calendar.getInstance();
				
				mCalendar.set(Calendar.YEAR, mDatePicker.getYear());
				mCalendar.set(Calendar.MONTH, mDatePicker.getMonth());
				mCalendar.set(Calendar.DAY_OF_MONTH, mDatePicker.getDayOfMonth());
				
				mCalendar.set(Calendar.HOUR_OF_DAY, mTimePicker.getCurrentHour());
				mCalendar.set(Calendar.MINUTE, mTimePicker.getCurrentMinute());
				
				if (curTimeType == SET_START_TIME) 
					{
					   if (mTask.getEndTime() !=-1L)
						   if (mTask.getEndTime()<=mCalendar.getTimeInMillis())
						   {
							   showErrorMsg(R.string.set_timestart_error);
							   return;
						   }
					   
					   
					   mTask.setStartTime(mCalendar.getTimeInMillis());
					   
					}
				else 
					{
					  
					   if (mTask.getStartTime() !=-1L)
						   if (mTask.getStartTime()>=mCalendar.getTimeInMillis())
						   {
							   showErrorMsg(R.string.set_timeend_error);
							   return;
						   }
					    mTask.setEndTime(mCalendar.getTimeInMillis());
					}
				
				
				((IUpdater)getActivity()).Update(mTask, mPosition); //Update views
				
				FragmentDialogDateTime.this.dismiss();
				
			}
		});
		
		
		final Button btnCancel = (Button) mView.findViewById(R.id.btnCancel);
		
		btnCancel.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				FragmentDialogDateTime.this.dismiss();
				
			}
		});

		
		return mView;
	}
	
	
	void showErrorMsg(int msg)
	{
		FragmentOkDialog mDialog = FragmentOkDialog.newInstance(getString(msg));
    	mDialog.show(getActivity().getSupportFragmentManager(), "");
	}

	
	
	public FragmentDialogDateTime()
	{
	
	}
	
	

}
