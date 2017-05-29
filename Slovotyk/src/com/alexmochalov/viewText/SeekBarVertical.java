package com.alexmochalov.viewText;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class SeekBarVertical extends ImageView {
	OnCustomEventListener listener;

	private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);;

	private int max;
	private int progress = 0;

	private int height;

	boolean pressed;

	public interface OnCustomEventListener{
		public void onChanged(int progress);
	}

	public void setCustomEventListener(OnCustomEventListener eventListener) {
		listener = eventListener;
	}


	public SeekBarVertical(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SeekBarVertical(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SeekBarVertical(Context context) {
		super(context);
	}

	public void setMax(int max){
		this.max = max;
	}

	public void setProgress(int progress){
		this.progress = Math.max(progress, 0);
		invalidate();
	}

	private void setPaint(){
		paint.setStrokeWidth(1);
		//paint.setColor(context.getResources().getColor(R.color.sky_blue));
		paint.setAlpha(100);
	}


	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int Y = (int) event.getY();
		int X = (int) event.getX();

        if (action == MotionEvent.ACTION_DOWN) {
    		paint.setAlpha(200);
        	progress = (int)Math.max(0, (float)((Y)*max)/(float)(height-H));

        	if(listener!=null) listener.onChanged(progress);

        	pressed = true;

        	invalidate();
        	return true;
        } else if (action == MotionEvent.ACTION_MOVE) {
        	progress = (int)Math.max(0, (float)((Y)*max)/(float)(height-H));
        	progress = Math.min(progress, max);


        	if(listener!=null) listener.onChanged(progress);

            invalidate();
        	return true;
        } else if (action == MotionEvent.ACTION_UP) {
        	pressed = false;
    		paint.setAlpha(100);
        	invalidate();
        	return true;
        }
		return false;
	}

	int W = 13;
	int H = 80;
	int H1 = 40;

	protected void onSizeChenged(int widthMeasureSpec, int heightMeasureSpec) {
    }


	@Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(W*2, getMeasuredWidth());
        //Log.d("", "getMeasuredHeight() "+getMeasuredHeight());
        //Log.d("", "getMeasuredWidth() "+getMeasuredWidth());
        height = getMeasuredHeight(); 
		setPaint();
    }


	@Override
	protected void onDraw(Canvas canvas) {
		if (isInEditMode()) {
			return;
		}
	    if (max == 0) return;
	    canvas.drawRect(new Rect(W, progress*height/max-H/2, W*2, H/2+progress*height/max), paint);

/*
	    canvas.drawLine(RADIUS,0,RADIUS, RADIUS+progress*height/max , paintBold);

	    canvas.drawCircle(RADIUS, RADIUS+progress*height/max , RADIUS, paint);

	    canvas.drawCircle(RADIUS, RADIUS+progress*height/max , RADIUS0, paintBold);

	    if (pressed){
		    paintBold.setStyle(Paint.Style.STROKE);
		    canvas.drawCircle(RADIUS, RADIUS+progress*height/max , RADIUS-3, paintBold);
		    paintBold.setStyle(Paint.Style.FILL_AND_STROKE);
	    }
	    */

	}


}

