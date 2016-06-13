package com.devlight.task1;


import com.devlight.dialogs.FragmentDialogDateTime;
import com.devlight.task1.R;
import com.devlight.task1.task.Task;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CreateTaskActivity extends AppCompatActivity implements IUpdater{
	

	Task mTask;
	int mPosition;
	
	EditText etHeader = null;
	EditText etComment = null;
	TextView tvStartTimeDate = null;
	TextView tvEndTimeDate = null;
	TextView tvSpentTime =  null;
	
	Toolbar toolBar;
	boolean isExitSnackBarShowing= false;  //is snackbar showing now (Press back again to exit)
	
	@Override
	public void onBackPressed() {
		tryExit();
		
	}
	
	
	void tryExit()
	{

		if (isExitSnackBarShowing) //Second back pressed
		{
	        Intent returnIntent = new Intent();
	        setResult(Activity.RESULT_CANCELED, returnIntent);
			finish();
		}
		else {//First tie pressed, show snakbar

			LinearLayout mainLayout = (LinearLayout) findViewById(R.id.layoutMain);
			Snackbar snackbar = Snackbar.make(mainLayout, R.string.exit_without_save, Snackbar.LENGTH_LONG);
			snackbar.show();
			isExitSnackBarShowing = true;

			snackbar.setCallback(new Snackbar.Callback() {

				@Override
				public void onDismissed(Snackbar snackbar, int event) {

					isExitSnackBarShowing = false;
				}

				@Override
				public void onShown(Snackbar snackbar) {
					
				}
			});

		}
	}

	
	
    @Override
	protected void onSaveInstanceState(Bundle outState) {
    	
    	mTask.setStrings(etHeader.getText().toString(),etComment.getText().toString());
    	
    	outState.putParcelable(MainActivity.DATA, mTask);
    	outState.putInt(MainActivity.POSITION, mPosition);
		super.onSaveInstanceState(outState);
	}
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        setContentView(R.layout.create_task_activity);
        
        toolBar = (Toolbar) findViewById(R.id.tbCreateEdit); //Use toolbar
        setSupportActionBar(toolBar);  
        getSupportActionBar().setTitle("");
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        
        
        //CutomFragmentDialog m = new CutomFragmentDialog(0,"ffff");
        
        
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
            	mTask= new Task("", "");
            } else {
            	mTask= extras.getParcelable(MainActivity.DATA);
            	mPosition= extras.getInt(MainActivity.POSITION);
            }
        } else {
        	mTask=  savedInstanceState.getParcelable(MainActivity.DATA);
        	mPosition=  savedInstanceState.getInt(MainActivity.POSITION);
        }
        
        if (mTask == null) mTask = new Task("", "");
        
       
        
          etHeader = (EditText) findViewById(R.id.etHeader);
          etComment = (EditText) findViewById(R.id.etComment);
        

        
       final  OnClickListener mOnClickStartTime = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			//Change start date
			//mFragmentDialogDateTime.getDialogDateTime().showForTask(CreateTaskActivity.this,mTask,DialogDateTime.SET_START_TIME,mPosition);
			showDateDialog(FragmentDialogDateTime.SET_START_TIME);
		}
	  };
	  
	  
      final  OnClickListener mOnClickEndTime = new OnClickListener() {
  		
		@Override
		public void onClick(View v) {
			
			//Change end date
			//mFragmentDialogDateTime.getDialogDateTime().showForTask(CreateTaskActivity.this,mTask,DialogDateTime.SET_END_TIME,mPosition);
			showDateDialog(FragmentDialogDateTime.SET_END_TIME);
			
		}
	  };
	  
	  
	  
	  
	  tvStartTimeDate = (TextView) findViewById(R.id.tvStartTimeDate);	  
	  tvEndTimeDate = (TextView) findViewById(R.id.tvEndTimeDate);	 
	  tvSpentTime = (TextView) findViewById(R.id.tvSpentTime);
	  
	  
	  final TextView tvEndTimeHeader = (TextView) findViewById(R.id.tvEndTimeHeader);
	  final TextView tvStartTimeHeader = (TextView) findViewById(R.id.tvStartTimeHeader);
	  //final TextView tvSpentTimeHeader = (TextView) findViewById(R.id.tvSpentTimeHeader);
	  
	  etComment.setText(mTask.getComment());
	  etHeader.setText(mTask.getHeader());
	  
	  tvStartTimeDate.setText(mTask.getStrStartTime());
	  tvEndTimeDate.setText(mTask.getStrEndTime());
	  tvSpentTime.setText(mTask.getStrSpentTime());
	  
	  
	  
	  tvStartTimeHeader.setOnClickListener(mOnClickStartTime);
	  tvStartTimeDate.setOnClickListener(mOnClickStartTime);	  
	  tvEndTimeHeader.setOnClickListener(mOnClickEndTime);
	  tvEndTimeDate.setOnClickListener(mOnClickEndTime);
        

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_create_edit, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        
        if (id == android.R.id.home)
        {
        	tryExit();
        	return true;
        }
        
        
        if (id == R.id.action_cancel)
        {
        	//finish this activity
        	Intent returnIntent = new Intent();
	        setResult(Activity.RESULT_CANCELED, returnIntent);
	        finish();
        	return true;
        }
        
        if (id == R.id.action_ready)
        {
			//return task to previous activity
	        Intent returnIntent = new Intent();

	        mTask.setStrings(etHeader.getText().toString(),etComment.getText().toString());
	        returnIntent.putExtra(MainActivity.DATA, mTask);
	        returnIntent.putExtra(MainActivity.POSITION, mPosition);
	        setResult(Activity.RESULT_OK,returnIntent);
	        finish();
        	return true;
        }
        
        
        return super.onOptionsItemSelected(item);
    }


	@Override
	public void Update(Task task, int position) {
		
	       //Update views
		  tvStartTimeDate.setText(mTask.getStrStartTime());
		  tvEndTimeDate.setText(mTask.getStrEndTime());
		  tvSpentTime.setText(mTask.getStrSpentTime());
		
	}
    
    
	void showDateDialog(int timeType) { //Change start/end time/date

	    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
	    Fragment prev = getSupportFragmentManager().findFragmentByTag("dialogDateTime");
	    if (prev != null) {
	        ft.remove(prev);
	    }
	    ft.addToBackStack(null);

	    // Create and show the dialog.
	    FragmentDialogDateTime newFragment = FragmentDialogDateTime.newInstance(mTask,timeType,mPosition);
	    newFragment.show(ft, "dialogDateTime");
	}
    
    

}
