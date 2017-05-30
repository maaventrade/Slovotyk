package com.alexmochalov.main;

import java.util.ArrayList;

import com.alexmochalov.dic.Entry;
import com.alexmochalov.slovotyk.R;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.content.Context;
import android.graphics.Typeface;
import android.widget.*;

/**
 * 
 * @author Alexey Mochalov
 * This Adapter shows the list of the the Lexicon entries
 *
 */
public class ArrayAdapterLexicon extends ArrayAdapter<Entry>{
	private ArrayList<Entry> values;
	Context context;
	int resource;
	// Font to disply phonetic symbols 
	private Typeface face;

	public ArrayAdapterLexicon(Context context, int res, ArrayList<Entry> values){
		super(context, res, values);
		this.values = values;
		this.resource = res;
		this.context = context;
		face = Typeface.createFromAsset(context.getAssets(), "fonts/Constructium.ttf");
	}

	private float x0;
	private float y0;
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) { 
			LayoutInflater inflater = (LayoutInflater) context.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
			convertView = inflater.inflate(R.layout.entry, null);
		}

		final Entry entry = values.get(position);

		TextView text = (TextView)convertView.findViewById(R.id.text);
		text.setText(entry.getText());

        TextView text2 = (TextView)convertView.findViewById(R.id.translation);
		text2.setText(Html.fromHtml(entry.getTranslation()));

		TextView text3 = (TextView)convertView.findViewById(R.id.phonetic);
		text3.setText(entry.getPhonetic());
		
		text3.setTypeface(face); 

		TextView text1 = (TextView)convertView.findViewById(R.id.sample);

		text1.setText(Html.fromHtml(entry.getSample()));

		
		ImageButton imageButton = (ImageButton)convertView.findViewById(R.id.entryImageButtonSpeak);
		imageButton.setOnClickListener(new ImageButton.OnClickListener(){
				@Override
				public void onClick(View p1)
				{
					TtsUtils.speak(entry.getText());
				}
			});
			
		final LinearLayout linearLayoutButtons = (LinearLayout)convertView.findViewById(R.id.linearLayoutButtons);
		LinearLayout layoutText = (LinearLayout)convertView.findViewById(R.id.layoutText);
		layoutText.setOnTouchListener(new View.OnTouchListener() {
	        @Override
	        public boolean onTouch(View arg0, MotionEvent event) {
	        	
	        	int action = event.getAction();
	    		int y = (int) event.getY();
	    		int x = (int) event.getX();
	    		
	            if (action == MotionEvent.ACTION_DOWN) {
					
					
	            } else if (action == MotionEvent.ACTION_MOVE) {
	            	if (x0 > x){
						
						final int left  =  linearLayoutButtons.getWidth();
						arg0.animate().translationX(-left);
						
					} else if (x0 < x){
						arg0.animate().translationX(0);
					}
	            	//LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
	            	//	     LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
	            	//layoutParams.setMargins(30, 0, 0, 0);	            	
	            	
	            //arg0.setLayoutParams(layoutParams);
	            	
	            	
	            } else if (action == MotionEvent.ACTION_UP) {
	            	
	            }
	            
            	y0 = y;
            	x0 = x;
            	
            	Log.d("b", ""+x+" "+y);
            	
				return true;
	        }
	    });
		
		return convertView;
	}
	
	private int getRelativeLeft(View myView) {
		if (myView.getParent() == myView.getRootView())
			return myView.getLeft();
		else
			return myView.getLeft() + getRelativeLeft((View) myView.getParent());
	}

	public int getCount(){
		return values.size();
	}

	public long getItemId(int position){
		return position;
	}	



}

