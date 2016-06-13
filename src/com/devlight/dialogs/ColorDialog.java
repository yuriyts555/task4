package com.devlight.dialogs;


import com.devlight.task1.R;
import com.devlight.task1.task.Task;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ColorDialog extends DialogFragment{
	
	int mColor;
	int mIndex;
	 float[] currentColorHsv = new float[3];
	 View viewHue;
	 ColorKotak viewSatVal;
	 ImageView viewCursor;
	 View viewOldColor;
	 View viewNewColor;
	 ImageView viewTarget;
	 ViewGroup viewContainer;
	 
	 
	 public interface IColorUpdate {	
			
	
			public void UpdateColor(int value,int index);

		};
		
		
	
	public static ColorDialog newInstance(int mColor,int mIndex) {
		ColorDialog fInstance = new ColorDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle(); 
        args.putInt("mColor", mColor);
        args.putInt("mIndex", mIndex);
        fInstance.setArguments(args);

        return fInstance;
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (getArguments() == null) return;
		mColor = getArguments().getInt("mColor" );
		mIndex = getArguments().getInt("mIndex" );

	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		

		getDialog().setTitle(getActivity().getString(R.string.select_color));
		
		Color.colorToHSV(mColor, currentColorHsv);
	
		
		final View mView = inflater.inflate(R.layout.color_dialog, container, false);
		viewHue = mView.findViewById(R.id.color_viewHue);
		viewSatVal = (ColorKotak) mView.findViewById(R.id.color_viewSatBri);
		viewCursor = (ImageView) mView.findViewById(R.id.color_cursor);
		viewOldColor = mView.findViewById(R.id.color_warnaLama);
		viewNewColor = mView.findViewById(R.id.color_warnaBaru);
		viewTarget = (ImageView) mView.findViewById(R.id.color_target);
		viewContainer = (ViewGroup) mView.findViewById(R.id.color_viewContainer);

		viewSatVal.setHue(getHue());
		viewOldColor.setBackgroundColor(mColor);
		viewNewColor.setBackgroundColor(mColor);
		
		viewHue.setOnTouchListener(new View.OnTouchListener() {
			@Override public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE
						|| event.getAction() == MotionEvent.ACTION_DOWN
						|| event.getAction() == MotionEvent.ACTION_UP) {

					float y = event.getY();
					if (y < 0.f) y = 0.f;
					if (y > viewHue.getMeasuredHeight()) y = viewHue.getMeasuredHeight() - 0.001f; // to avoid looping from end to start.
					float hue = 360.f - 360.f / viewHue.getMeasuredHeight() * y;
					if (hue == 360.f) hue = 0.f;
					setHue(hue);

					// update view
					viewSatVal.setHue(getHue());
					moveCursor();
					viewNewColor.setBackgroundColor(getColor());

					return true;
				}
				return false;
			}
		});
		
		viewSatVal.setOnTouchListener(new View.OnTouchListener() {
			@Override public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_MOVE
						|| event.getAction() == MotionEvent.ACTION_DOWN
						|| event.getAction() == MotionEvent.ACTION_UP) {

					float x = event.getX(); // touch event are in dp units.
					float y = event.getY();

					if (x < 0.f) x = 0.f;
					if (x > viewSatVal.getMeasuredWidth()) x = viewSatVal.getMeasuredWidth();
					if (y < 0.f) y = 0.f;
					if (y > viewSatVal.getMeasuredHeight()) y = viewSatVal.getMeasuredHeight();

					setSat(1.f / viewSatVal.getMeasuredWidth() * x);
					setVal(1.f - (1.f / viewSatVal.getMeasuredHeight() * y));

					// update view
					moveTarget();
					viewNewColor.setBackgroundColor(getColor());

					return true;
				}
				return false;
			}
		});
		
		// move cursor & target on first draw
		ViewTreeObserver vto = mView.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override public void onGlobalLayout() {
				moveCursor();
				moveTarget();
				mView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
		
		
		Button btnOk = (Button) mView.findViewById(R.id.btnOk);
		Button btnCancel = (Button) mView.findViewById(R.id.btnCancel);
		
		btnOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				((IColorUpdate)getActivity()).UpdateColor( getColor(),mIndex);
				ColorDialog.this.dismiss();
				
			}
		});
		
		btnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ColorDialog.this.dismiss();
				
			}
		});
		
		return mView;
	}
	
	
	protected void moveCursor() {
		float y = viewHue.getMeasuredHeight() - (getHue() * viewHue.getMeasuredHeight() / 360.f);
		if (y == viewHue.getMeasuredHeight()) y = 0.f;
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewCursor.getLayoutParams();
		layoutParams.leftMargin = (int) (viewHue.getLeft() - Math.floor(viewCursor.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft());
		;
		layoutParams.topMargin = (int) (viewHue.getTop() + y - Math.floor(viewCursor.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
		;
		viewCursor.setLayoutParams(layoutParams);
	}

	protected void moveTarget() {
		float x = getSat() * viewSatVal.getMeasuredWidth();
		float y = (1.f - getVal()) * viewSatVal.getMeasuredHeight();
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewTarget.getLayoutParams();
		layoutParams.leftMargin = (int) (viewSatVal.getLeft() + x - Math.floor(viewTarget.getMeasuredWidth() / 2) - viewContainer.getPaddingLeft());
		layoutParams.topMargin = (int) (viewSatVal.getTop() + y - Math.floor(viewTarget.getMeasuredHeight() / 2) - viewContainer.getPaddingTop());
		viewTarget.setLayoutParams(layoutParams);
	}

	
	
	private int getColor() {
		return Color.HSVToColor(currentColorHsv);
	}

	private float getHue() {
		return currentColorHsv[0];
	}

	private float getSat() {
		return currentColorHsv[1];
	}

	private float getVal() {
		return currentColorHsv[2];
	}

	private void setHue(float hue) {
		currentColorHsv[0] = hue;
	}

	private void setSat(float sat) {
		currentColorHsv[1] = sat;
	}

	private void setVal(float val) {
		currentColorHsv[2] = val;
	}
	
	
	public ColorDialog()
	{
	
	}

}
