package com.devlight.task1;

import java.util.ArrayList;
import java.util.Random;

import com.devlight.dialogs.FragmentDialogDateTime;
import com.devlight.dialogs.FragmentOkCancelDialog;
import com.devlight.task1.task.Task;
import com.devlight.task1.utils.PrefsHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;

public class MainActivity extends AppCompatActivity   implements IUpdater,FragmentOkCancelDialog.DialogYesNoListener{
	


   Toolbar toolBar;

    boolean isExitSnackBarShowing= false; //is snackbar showing now (Press back again to exit)

   
    public final static int DIALOG_CLEAR =0; //ID Dialog "Are you really want to clear all items?"  
    
	public final static String DATA ="DATA";  
	public final static String POSITION ="POSITION";
	
	final static int RESULT_CREATE_ACTIVITY =1;  //ID for Create/Edit activity
	final static int RESULT_PREFS_ACTIVITY =2;  //ID for Preferences activity
	
	ItemListAdaptor itemListAdaptor;  //List adaptor
	ListView mListView;
	


	PrefsHelper mPrefsHelper;  //save list in sharedpreferences helper
	
	Random mRandom = new Random();
	
	@Override
	public void onBackPressed() {
	  tryExit();
	
	}
	
	void tryExit()
	{

		if (isExitSnackBarShowing) //Second back pressed
			finish();
		else { //First tie pressed, show snakbar
			
			mPrefsHelper.saveTasks(itemListAdaptor.getTasks());

			android.support.design.widget.CoordinatorLayout mainLayout = (android.support.design.widget.CoordinatorLayout) findViewById(
					R.id.mainLayout);
			Snackbar snackbar = Snackbar.make(mainLayout, R.string.confirmexit, Snackbar.LENGTH_SHORT);
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

	
	
    //Save items list
    @Override
	protected void onSaveInstanceState(Bundle outState) {
    	if (itemListAdaptor.getTasks()!=null)
    		outState.putParcelableArrayList(DATA, itemListAdaptor.getTasks()); //save list 
		super.onSaveInstanceState(outState);
	}



   

	@Override
    protected void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        
        toolBar = (Toolbar) findViewById(R.id.toolbar); //Use toolbar
        setSupportActionBar(toolBar);  
        getSupportActionBar().setTitle(R.string.cur_tasks);
        

        
        mListView = (ListView) findViewById(R.id.listView);

        mPrefsHelper = new PrefsHelper(this);
        
         ArrayList<Task> mTasks;
        
        //Restore tasks list     
        if (savedInstanceState != null) {
        	mTasks = savedInstanceState.getParcelableArrayList(DATA); //was screen rotation, restore tasks from savedInstance
        	
            // set list adaptor
    		itemListAdaptor = new ItemListAdaptor(this,mTasks);
    		mListView.setAdapter(itemListAdaptor);
    		
    		
        } else
        {
        	mTasks =new ArrayList<Task>(); //App run, restore from sharedPreference
        	
            // set list adaptor
    		itemListAdaptor = new ItemListAdaptor(this,mTasks);
    		mListView.setAdapter(itemListAdaptor);
    		
    		
        	loadFromPrefs();
        }
       
                

        ViewCompat.setNestedScrollingEnabled(mListView,true);
        
        
        



		
		//Long click on item in ListView
		mListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				
				Intent mIntent = new Intent(MainActivity.this, CreateTaskActivity.class);	
				mIntent.putExtra(DATA, itemListAdaptor.getItem(position));
				mIntent.putExtra(POSITION, position);
				startActivityForResult(mIntent, RESULT_CREATE_ACTIVITY);
				return true;
			}
		});
		
		
		// click on item in ListView
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {               
				
				Task mTask = itemListAdaptor.getItem(position);
				
				if (mTask.getStartTime()!=-1L && mTask.getEndTime()!=-1L) return; //All time filled, to correct it - edit item
				
				int curTimeType = FragmentDialogDateTime.SET_START_TIME;
				if (mTask.getStartTime()!=-1L) curTimeType = FragmentDialogDateTime.SET_END_TIME;
				
				//mDialogSetDateTime.showForTask(MainActivity.this, mTask, curTimeType, position);
				showDateDialog(mTask, curTimeType, position);
			}
		});
		
		
		
		//Add new task button
		FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fabAdd);
		fabAdd.setBackgroundColor(getResources().getColor(R.color.orange));
		fabAdd.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AddNewTask();
				
			}
		});
		
		


    }
	
	
	//Change item start/end date/time
	void showDateDialog( Task mTask,int timeType,int mPosition) {

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
    
    
    void AddNewTask()
    {
		Intent mIntent = new Intent(MainActivity.this, CreateTaskActivity.class);
		mIntent.putExtra(POSITION, -1);
		startActivityForResult(mIntent, RESULT_CREATE_ACTIVITY);
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //After edit/create activity
    	if (requestCode == RESULT_CREATE_ACTIVITY) {
        	
        	//Button save was pressed, we got result
            if(resultCode == Activity.RESULT_OK){
            	
            	Task task = data.getParcelableExtra(DATA);           
            	int mPosition = data.getIntExtra(POSITION, -1);
                
                if (mPosition == -1 ) itemListAdaptor.AddTask(task);  
                else itemListAdaptor.getTasks().set(mPosition, task);
                
                
                mPrefsHelper.saveTasks(itemListAdaptor.getTasks());
                itemListAdaptor.notifyDataSetChanged();
                
            }
            

            //Button exit was pressed, no result
            if (resultCode == Activity.RESULT_CANCELED) {
           
            }
        }
        
        
        //After preferences activity
        if (requestCode == RESULT_PREFS_ACTIVITY && resultCode == Activity.RESULT_OK) {
        	itemListAdaptor.updateColors();
        	mListView.invalidateViews();
        }
    }//onActivityResult
    
    
    





    //Update ListView
	@Override
	public void Update(Task task, int position) {
		
		//Update listView
		mPrefsHelper.saveTasks(itemListAdaptor.getTasks());
		itemListAdaptor.notifyDataSetChanged();
		
	}
	
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
       
        //Check default sort mode in menu
        int mDefaultSortMode = itemListAdaptor.getDefautlSortMode();
        
        MenuItem mItem;
        
        switch (mDefaultSortMode) {
        
		case ItemListAdaptor.SORTMODE_A_Z:
			mItem = menu.findItem(R.id.action_a_z) ;
			mItem.setChecked(true);
			break;
			
		case ItemListAdaptor.SORTMODE_Z_A:
			mItem = menu.findItem(R.id.action_z_a) ;
			mItem.setChecked(true);
			break;
			
		case ItemListAdaptor.SORTMODE_DATE_DEC:
			mItem = menu.findItem(R.id.action_date_dec) ;
			mItem.setChecked(true);
			break;
			
		case ItemListAdaptor.SORTMODE_DATE_INC:
			mItem = menu.findItem(R.id.action_date_inc) ;
			mItem.setChecked(true);
			break;

	
		}
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
      
        
        if (id == android.R.id.home) {
      
        	tryExit();
           return true;
        }


        if (id == R.id.action_fill) { //Fill list
        	
        	addTasksToListView();        	
           return true;
        }
        
        if (id == R.id.action_add) { //Add new task
        	
        	AddNewTask();    	
           return true;
        }
        
        if (id == R.id.action_clear) {
        	
        	//new CutomFragmentDialog(DIALOG_CLEAR,getString(R.string.confirm_clear)).show(getSupportFragmentManager(), "");
        	FragmentOkCancelDialog mDialog = FragmentOkCancelDialog.newInstance(DIALOG_CLEAR,getString(R.string.confirm_clear));
        	mDialog.show(getSupportFragmentManager(), "");
            return true;
         }
        
        if (id == R.id.action_a_z) { //Sort A-z
        	
        	item.setChecked(true);
        
        	itemListAdaptor.changeSortMode(ItemListAdaptor.SORTMODE_A_Z);
            return true;
         }
        
        if (id == R.id.action_z_a) { //Sort Z-a
        	
        	item.setChecked(true);
        	itemListAdaptor.changeSortMode(ItemListAdaptor.SORTMODE_Z_A);
            return true;
         }
        
        if (id == R.id.action_date_dec) { //Sort date decrement
        	
        	item.setChecked(true);
        	itemListAdaptor.changeSortMode(ItemListAdaptor.SORTMODE_DATE_DEC);
            return true;
         }
        
        if (id == R.id.action_date_inc) { //Sort date increment
        	
        	item.setChecked(true);
        	itemListAdaptor.changeSortMode(ItemListAdaptor.SORTMODE_DATE_INC);
            return true;
         }
        
        if (id == R.id.action_prefs) { //Open preferences
        	
			Intent mIntent = new Intent(MainActivity.this, PrefsActivity.class);
			startActivityForResult(mIntent, RESULT_PREFS_ACTIVITY);
            return true;
         }
        
        
        if (id == R.id.action_exit) {  //Exit
        	
        	finish();
            return true;
         }



        return super.onOptionsItemSelected(item);
    }
    
    
    
    void loadFromPrefs()  //Load list from prefs
    {
        this.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				mPrefsHelper.loadTasks(itemListAdaptor.getTasks());
				itemListAdaptor.notifyDataSetChanged();
				
			}
		});
    }

    
    
    Task getRandomTask() //get task with random integer header
    {
    	String mHeader = ""+mRandom.nextInt(10000);
    	String mComment = "Body"+mRandom.nextInt(10000);
    	
    	return new Task(mHeader,mComment);
    }
    		
    void addTasksToListView() //Fill list, with item count three time much more, than items visible
    {
    
    	
    	if (itemListAdaptor.getTasks().size()==0)//if we have no task, add first
    	{
    			itemListAdaptor.AddTask(getRandomTask());
    			mListView.invalidate();
    	}
    	

    	final int UNBOUNDED = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
    	View childView = itemListAdaptor.getView(0, null, mListView);
    	childView.measure(UNBOUNDED, UNBOUNDED);
    	
    	int mElementHeight = childView.getMeasuredHeight()+mListView.getDividerHeight();
    	
    	if (mElementHeight==0) return;
    	
    	
    	android.support.design.widget.CoordinatorLayout mainLayout = (android.support.design.widget.CoordinatorLayout) findViewById(
				R.id.mainLayout);
    	
    	
    	int listViewHeight = mainLayout.getHeight() - toolBar.getHeight();
    	
    	int mElementsNeedCount = (listViewHeight/mElementHeight)*3;   	
    	
    	
    	while(itemListAdaptor.getTasks().size()<mElementsNeedCount) itemListAdaptor.AddTask(getRandomTask());
    	
    	mPrefsHelper.saveTasks(itemListAdaptor.getTasks());

    }






	@Override
	public void onDialogYes(int mDialogCode) {
		

		if (mDialogCode == DIALOG_CLEAR) //Was yes pressed? on dialog clear all
			{
			   itemListAdaptor.ClearTasks();
			   mPrefsHelper.saveTasks(itemListAdaptor.getTasks());
			}
		
	}




	@Override
	public void onDialogNo(int mDialogCode) {
		
		
	}

}
