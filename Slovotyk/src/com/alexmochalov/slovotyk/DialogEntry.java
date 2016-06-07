package com.alexmochalov.slovotyk;

import java.util.ArrayList;

import com.alexmochalov.dic.Entry;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

//import com.alex.mochalov.TextToDict.R;
//import com.alex.mochalov.TextToDic.*;

public class DialogEntry extends Dialog{
	private Context context;
	private Entry entry;

	public DialogEntry(Context context, Entry entry){
		super(context);
		this.context = context;
		this.entry = entry;
	}	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.entry);

		TextView text = (TextView)findViewById(R.id.text);
		text.setText(entry.getText());

        TextView text2 = (TextView)findViewById(R.id.translation);
		text2.setText(entry.getTranslation());
		text2.setMaxLines(999999);
		text2.setMovementMethod(new ScrollingMovementMethod());
		//text2.setTypeface(face); 

		TextView text1 = (TextView)findViewById(R.id.sample);
		text1.setText(Html.fromHtml(entry.getSample()));


	    /*
		 listView.setOnItemClickListener(new OnItemClickListener(){
		 @Override
		 // An item of the listViewSubs are selected 
		 public void onItemClick(AdapterView<?> parent, View view,
		 int position, long id) {
		 seekToPosition(position);
		 }});
		 */

	}

}

