package com.alexmochalov.io;

import java.util.ArrayList;

import com.alexmochalov.main.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;

/**
 * 
 * @author Alexey Mochalov
 * This utility divides the strings from the source list by screen width 
 *
 */
public class AsynkDivider
{
	//private ProgressDialog progressDialog;
	// Progress bar slows down the procedure
	//
	private MyTaskDividing myTaskDividing;
	// Width of the screen
	private int width;
	// Paint object is used for getting characters size. 
	private Paint paint;
	// Copy of the source and destination string arrays
	private ArrayList<String> source;
	private ArrayList<String> dest;
	// This method is called when procedure finished.
	public EventCallback eventCallback;
	
	private ProgressDialog progressDialog;
	private Context context;
	
	public interface EventCallback { 
		void dividingFinishedCallBack(); 
	}
	
	/**
	 * Store parameters and start the dividindg task
	 * @param source - source string array
	 * @param dest - destination string array
	 * @param width - width of the screen
	 * @param paint - Point object of the screen
	 */
	public void start(ArrayList<String> source, ArrayList<String> dest, int width, Paint paint, Context context){
		dest.clear();
		this.source = source;
	 	this.dest = dest;
		this.width = width;
		this.paint = paint;
		this.context = context;

		progressDialog = new ProgressDialog(context);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setTitle("Dividing strings");
		progressDialog.setMessage(Utils.extractFileName());
		progressDialog.setCancelable(false);
		progressDialog.setMax(source.size());
		
		progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	myTaskDividing.cancel(true);
		    }
		});
		
		progressDialog.show();
		
		// Set the TextLoading flag
		Utils.setTextLoading(true);
		
		myTaskDividing = new MyTaskDividing();    
		myTaskDividing.execute();
	}
	
	class MyTaskDividing extends AsyncTask<String, Integer, Void>
	{

		@Override
		protected Void doInBackground(String[] p1)
		{
			divide();
			return null;
		}

		@Override    
		protected void onCancelled(Void result) {      
			super.onCancelled(result);

			if (progressDialog != null){
				progressDialog.hide();
				progressDialog.dismiss();
			}
			
			// Reset the TextLoading flag
			Utils.setTextLoading(false);
			
			if (eventCallback != null)
				eventCallback.dividingFinishedCallBack(); 	
		}
		
		@Override    
		protected void onPostExecute(Void result) {      
			super.onPostExecute(result);
			
			if (progressDialog != null){
				progressDialog.hide();
				progressDialog.dismiss();
			}
			
			// Reset the TextLoading flag
			Utils.setTextLoading(false);
			
			// Notify the mainActivity what dividing finished 
			if (eventCallback != null)
				eventCallback.dividingFinishedCallBack(); 	
		}
		
		/**
		 * Dividing of the string
		 */
		private void divide()
		{
			float x = Utils.PARAG;
			// Repeat for every source string
			int n = 0;
			for (String string: source){
				if (isCancelled()) return;
				
				String S = string;
				if (S.length() == 0) continue;

				int start = 0;			

				x = Utils.PARAG;
				int index = 0;
				// Find first blank
				int blank = Utils.findBlank(paint, width, index, x, S);
				char tab = Utils.CHAR_TAB;
				// Repeat for every symbol of the string
				while (index < S.length()){
					char c = S.charAt(index);
					if (c < 20){
						// If symbol is not visible, don't disply it
						if (c == 3)
							// Symbol Start bold text
							paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
						else if (c == 4)
							// Symbol End bold text
							paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
					} else {
						// Add width of the current symbol to width of the screen 
						x = x + paint.measureText(""+c);
						if (x >= width - Utils.XGAP- Utils.XGAP
							|| index == blank)
						{
							// Add to the string
							if (tab != 0)
								dest.add(tab+S.substring(start, index));
							else
								dest.add(S.substring(start, index));
							tab = 0;
							x = Utils.XGAP;
							// Find next blank
							blank = Utils.findBlank(paint, width, index+1, x, S);
							start = index+1;
						}
					}
					index++;
				}
				if (start != index)
					// Add the rest of the string
					if (tab != 0)
						dest.add(tab + S.substring(start, index));
					else
						dest.add(S.substring(start, index));
				//Utils.setTextLoadingStr("Text is dividing...   "+(n++)+"/"+source.size());
				progressDialog.setProgress(n++);
			}
		}
	}
}
