package com.alexmochalov.lexicon;

import java.util.ArrayList;

import com.alexmochalov.main.MainActivity;
import com.alexmochalov.main.Utils;
import com.alexmochalov.slovotyk.R;
import com.alexmochalov.slovotyk.R.id;
import com.alexmochalov.slovotyk.R.layout;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//import com.alex.mochalov.TextToDict.R;
//import com.alex.mochalov.TextToDic.*;

public class DialogLexicon extends Dialog{
	private Context mContext;
	private Dialog mDialog;
	//public OnEventListener listener;
	
	//public interface OnEventListener{
	//	public void onClick(int firstLine);
	//}
	
	public DialogLexicon(MainActivity context) {
		super(context); //android.R.style.Theme_Black_NoTitleBar_Fullscreen
		mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
        //        WindowManager.LayoutParams.MATCH_PARENT);		
		
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setTitle("Temp dictionary");
		
		setContentView(R.layout.dialog_lexicon);
		
		//ListView listView = (ListView)findViewById(R.id.bookmarks);
		
		Lexicon.setGUI((ListView)findViewById(R.id.dictionary));

		
		mDialog = this;
			
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

