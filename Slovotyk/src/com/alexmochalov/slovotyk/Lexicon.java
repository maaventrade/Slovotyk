package com.alexmochalov.slovotyk;

import android.app.*;
import android.util.*;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.*;
import com.alexmochalov.dic.*;
import com.alexmochalov.io.*;
import java.util.*;

public final class Lexicon {
	private static Activity mContext;
	
	private static ArrayList<Entry> entryes = new ArrayList<Entry>();

	private static ListView listView;
	private static ArrayAdapterLexicon adapter;

	static EventCallback mCallback;

	interface EventCallback { 
		void itemSelected(Entry entry);
	}
	
	public static void setParams(Activity activity){
		mContext = activity;
	}	

	private static Entry find(ArrayList<Entry> entryes, String text, boolean delete){
		boolean found = false;
		for (Entry e: entryes)
			if (e.getText().equals(text)){
				if (delete)
					entryes.remove(e);
				return e;
			}	
		return null;
	}

	public static Entry addEntry(String text, String translation, String sample){
		Entry e = find(entryes, text, false); 
		if (e == null){
			e = new Entry(text, translation, sample); 
			entryes.add(e);
		}
		
		if (adapter != null)
			adapter.notifyDataSetChanged();
		return e;
	}

	public void removeEntry(String text) {
		find(entryes, text, true);
		adapter.notifyDataSetChanged();
	}

	public static void clearEntryes(){
		entryes.clear();
		if (adapter != null)
			adapter.notifyDataSetChanged();
	}

	public void notifyDataSetChanged() {
		adapter.notifyDataSetChanged();
	}

	public void sort(ArrayList<Entry> pentryes) {
        EntryComparator entrySort = new EntryComparator();
        Collections.sort(pentryes, entrySort);
	}

	class EntryComparator implements Comparator<Entry> {
		public int compare(Entry A, Entry B) {
			return A.getText().  compareToIgnoreCase(B.getText());
		} 
	}

	public static void setGUI() {
		listView = (ListView)mContext.findViewById(R.id.dictionary);
	  
		adapter = new ArrayAdapterLexicon(mContext,
									  R.layout.entry, entryes);
	   listView.setAdapter(adapter);
		Log.d("z","setAdapter "+mCallback);
	    listView.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
										int position, long id) {
					Log.d("z","mCallback "+mCallback);
					if (mCallback != null)
						mCallback.itemSelected(entryes.get(position));
				}});
				
		
	}


	public static void refresh() {
		for (Entry e: entryes)
			e.refresh();
			
		adapter.notifyDataSetChanged();
		Toast.makeText(mContext,"Ok", Toast.LENGTH_LONG).show();
	}
	
	public static void saveFile() {
		FileSaver fileSaver = new FileSaver(mContext);
		fileSaver.save(Utils.APP_FOLDER+"/dic.xml", entryes);
	}

	public static void load() {
		entryes.clear();
		AsynkLoader fileLoader = new AsynkLoader(mContext);
		fileLoader.loadDictionary(Utils.APP_FOLDER+"/dic.xml", entryes);
		//notifyDataSetChanged();
	}

	public static void deleteEntry(String text) {
		find(entryes, text, true);
	}	

	public static int getPosition(){
		return listView.getFirstVisiblePosition();
	}
	
	public static void setPosition(int position){
		listView.setSelection(position);
	}
	
	public static void addEntries(Entry entry, String text,
			String translation, String sentence, String[] nextTwoWords) {
		Log.d("z","nextTwoWords "+nextTwoWords.length);
		
		String[] strings = Utils.getStringForms(text);
		Log.d("z","strings "+strings.length);
		
		ArrayList<EntryLatin> e = entry.getEntryLatin();
		for (int j = 0; j < e.size(); j++){
			String textLatin = e.get(j).getText();

			String s0 = (text+" "+nextTwoWords[0]+" "+nextTwoWords[1]).trim();
			if (textLatin.contains(s0))
			{	
				addEntry(s0, e.get(j).getTranslation(), sentence);
				continue;
			}	
			
			for (int i = 0; i < strings.length; i++){
				String s = (strings[i]+" "+nextTwoWords[0]+" "+nextTwoWords[1]).trim();
				if (textLatin.contains(s))
					addEntry(s0, e.get(j).getTranslation(), sentence);
			}

			s0 = (text+" "+nextTwoWords[0]).trim();
			if (textLatin.contains(s0))
			{	
				addEntry(s0, e.get(j).getTranslation(), sentence);
				continue;
			}	
			for (int i = 0; i < strings.length; i++){
				String s = (strings[i]+" "+nextTwoWords[0]).trim();
				if (textLatin.contains(s))
					addEntry(s0, e.get(j).getTranslation(), sentence);
			}
		}
		
		
	}
	
}

