package com.alexmochalov.dic;

import android.app.*;
import android.content.*;
import android.text.*;
import android.text.method.ScrollingMovementMethod;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;

import java.util.*;

import com.alexmochalov.slovotyk.R;
import com.alexmochalov.slovotyk.R.id;
import com.alexmochalov.slovotyk.R.layout;

public class EntryEditor {
	AutoCompleteTextView text = null;
	TextView phonetic;
	TextView translation;
	
	Activity activity;
	Entry entry = null;
	
	public void setGUI(Activity activity) {
		this.activity = activity;
        text = (AutoCompleteTextView)activity.findViewById(R.id.editTextEntry);
		phonetic = (TextView)activity.findViewById(R.id.phoneticEntry);
		translation = (TextView)activity.findViewById(R.id.translationEntry);
	}
	
	public void start(Entry entry_) {
		if (entry_ == null)
			entry_ = new Entry();
		this.entry = entry_;
			
		final ArrayAdapterDictionary adapter = new ArrayAdapterDictionary(activity, 
															R.layout.dic_string, 
															(ArrayList<IndexEntry>)Dictionary.getIndexEntries().clone());
        text.setAdapter(adapter);
		
		text.setOnItemClickListener(new OnItemClickListener(){
			  @Override
			  public void onItemClick(AdapterView<?> adapterView, View p2, int position, long p4)
			  {
				  IndexEntry indexEntry = (IndexEntry)adapterView.getItemAtPosition(position);
				  String str = Dictionary.readTranslation(indexEntry);
						  
				  entry.setTranslationAndPhonetic(str, indexEntry.text);
				  
				  translation.setText(Html.fromHtml(entry.getTranslation().toString()));
				  phonetic.setText(entry.getPhonetic());
				  
			  }});

		text.setText(entry.getText());
		phonetic.setText(entry.getPhonetic());
		translation.setText(Html.fromHtml(entry.getTranslation().toString()));
		translation.setMovementMethod(new ScrollingMovementMethod());
	}

}


