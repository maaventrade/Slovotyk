package com.alexmochalov.bmk;

import java.util.ArrayList;

import com.alexmochalov.slovotyk.MainActivity;
import com.alexmochalov.slovotyk.R;
import com.alexmochalov.slovotyk.Utils;
import com.alexmochalov.slovotyk.R.id;
import com.alexmochalov.slovotyk.R.layout;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.alex.mochalov.TextToDict.R;
//import com.alex.mochalov.TextToDic.*;

public class DialogBookmarks extends Dialog{
	private Context context;
	private Dialog dialog;
	private ArrayList<Bookmark> bookmarks;
	private ArrayAdapterBookmarks adapter;
	
	public OnEventListener listener;
	
	public interface OnEventListener{
		public void onClick(int firstLine);
	}
	
	public DialogBookmarks(MainActivity context,
						   ArrayList<Bookmark> bookmarks) {
		super(context);
		
		this.context = context;
		this.bookmarks = bookmarks;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setTitle("Bookmarks");

		setContentView(R.layout.bookmarks);
		
		ListView listView = (ListView)findViewById(R.id.bookmarks);
		
	    adapter = new ArrayAdapterBookmarks(context,
				  R.layout.bookmark, bookmarks);
	    listView.setAdapter(adapter);
	    
		dialog = this;
				
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
		

	}

	
}

