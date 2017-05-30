package com.alexmochalov.lexicon;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.widget.ListView;
import android.widget.TextView;

import com.alexmochalov.dic.Entry;
import com.alexmochalov.main.MainActivity;
import com.alexmochalov.slovotyk.R;

public class DialogEntry extends Dialog{
	private Context mContext;
	private Dialog mDialog;
	private Entry mEntry;
	
	public DialogEntry(Activity context, Entry entry) {
		super(context); // android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen
		mContext = context;
		mEntry = entry;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dialog_entry);
		
		mDialog = this;
			
		TextView translation = (TextView)findViewById(R.id.translationEntry);
		translation.setText(Html.fromHtml(mEntry.getTranslation().toString()));
		translation.setMovementMethod(new ScrollingMovementMethod());
		
		/*
		listView.setOnItemClickListener(new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (listener != null)
				listener.onClick(bookmarks.get(position).line);
			
			dialog.dismiss();
		}});
		
		Button button = (Button)findViewById(R.id.ButtonClearBookmarks);
		button.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				Utils.setModified(true);
				if (listener != null)
					listener.onClick(-1);
				
				dialog.dismiss();
			}
			
		});
		*/

	}

	
}

