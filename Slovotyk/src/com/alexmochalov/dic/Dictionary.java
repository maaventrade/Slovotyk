package com.alexmochalov.dic;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.regex.Pattern;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import android.text.*;

import com.alexmochalov.dic.Dictionary.*;
import com.alexmochalov.slovotyk.R;
import com.alexmochalov.slovotyk.Utils;
import com.alexmochalov.slovotyk.R.string;

public final class Dictionary{

	private static Context mContext;
	private static ArrayList<IndexEntry> indexEntries = new ArrayList<IndexEntry>();
	
	static MyTaskLoading myTaskLoading;
	static MyTaskIndexing myTaskIndexing;
	
	static ProgressDialog progressDialog;
	static Boolean parsing = false;
	
	public static EventCallback eventCallback;
	
	private static String mDictionaryName;
	private static String mIndexFileName;
	
	static String info;

	public interface EventCallback { 
		void loadingFinishedCallBack(); 
		void indexingFinishedCallBack(String dictionary_name, String index_file_name); 
	}
	
	public static void setParams(Context context) {
		mContext = context;
	}
	
	public static void load(String dictionary_name, String index_file_name) {
		if (Utils.isInternalDictionary())
			index_file_name= Utils.APP_FOLDER+"/"+dictionary_name.replace(".xdxf", ".index");
		else	
			index_file_name= dictionary_name.replace(".xdxf", ".index");
		
		mIndexFileName = index_file_name;
		mDictionaryName = dictionary_name;

		//Log.d("s","Start Loading "+dictionary_name);
		//Log.d("s",index_file_name);
		
		File file = new File(index_file_name);
		
		if(!file.exists()){
			// If Index file not found show the message  
			Toast.makeText(mContext, mContext.getResources().getString(R.string.index_not_found), Toast.LENGTH_LONG)
					.show();
			if (eventCallback != null)
				eventCallback.loadingFinishedCallBack();
			return;
		}
		loadAsinc();
	}
		
	private static void loadAsinc() {
		progressDialog = new ProgressDialog(mContext);
		progressDialog.setTitle("Loading dictionary...");
		progressDialog.setMessage(mIndexFileName);
		progressDialog.show();
		
		myTaskLoading = new MyTaskLoading();    
		myTaskLoading.execute(indexEntries);
	}

	static class MyTaskLoading extends AsyncTask<ArrayList<IndexEntry>, Integer, Void> {    
		@Override 
		protected void onPreExecute() {      
			super.onPreExecute();      
		    //progressBar.setVisibility(View.VISIBLE);
			}    
		@Override    
		protected Void doInBackground(ArrayList<IndexEntry>... values) {
			loadAsinc(values[0]);
			return null; 
			
		}
		
		@Override    
		protected void onCancelled(Void result) {      
			super.onCancelled(result);
			progressDialog.hide();
			progressDialog.dismiss();
			
			Utils.setInformation(info);
			
			//seekBarVertical.setMax(getStringsSize());
			Toast.makeText(mContext,
					"Cancelled", Toast.LENGTH_LONG) // mContext.getResources().getString(R.string.loading_cancelled)
					.show();
			eventCallback.loadingFinishedCallBack(); 	
		}
		
		@Override    
		protected void onPostExecute(Void result) {      
			super.onPostExecute(result);
			progressDialog.hide();
			progressDialog.dismiss();
			
			Utils.setIndexFileName(mIndexFileName);
			Utils.setDictionaryFileName(mDictionaryName);
			
			Utils.setInformation(info);
			
			if (eventCallback != null)
				eventCallback.loadingFinishedCallBack();
			//invalidate();    
			//seekBarVertical.setMax(getStringsSize());
		}
		
		protected void loadAsinc(ArrayList<IndexEntry> ens) {
			parsing = true;
			ens.clear();
			
			BufferedReader reader;

			ens.clear();
		//Log.d("s","LOAD "+mIndexFileName);
			try {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(mIndexFileName)));

				String line = reader.readLine();
				while (line != null){
					IndexEntry indexEntry = new IndexEntry(line);
					if (indexEntry.getText().length() > 0)
						ens.add(indexEntry);
					line = reader.readLine();
				    if (isCancelled()){
						reader.close();
				    	break;
				    }
				}
				info = "File <"+mDictionaryName+"> loaded. "+ens.size()+" entries.";
				reader.close();
			} catch (IOException t) {
				info = "Error loading <"+mDictionaryName+"> loaded. "+t.toString();
				//Log.d("s", info);
			}
			parsing = false;
			//Log.d("s", "OK");
		}
		
	}	
	
	public static IndexEntry find(String string) {
		ArrayList<IndexEntry> results = new ArrayList<IndexEntry>();
		string = string.toLowerCase();

		String[] strings = Utils.getStringForms(string);
//
int n = 0;
		for (IndexEntry indexEntry: indexEntries){
			if (indexEntry.text == null)
				continue;
				
			if (string.charAt(0) > indexEntry.text.charAt(0))
				continue;
			if (string.charAt(0) < indexEntry.text.charAt(0)){
				//Log.d("z", string.charAt(0) +" - "+ indexEntry.text.charAt(0));
				break;
			}
			
			String text = indexEntry.text.replace("-", "");
	n++;
			if (string.equals(text)){
				
				return indexEntry;
			}
				
			if (strings.length == 1){
				
				if (strings[0].equals(text)){
				
					results.add(indexEntry);
				}
			}
				
			else if (strings[1].equals(text))
				return indexEntry;
			else if (string.startsWith(text) && string.length() - text.length() > 2)
				results.add(indexEntry);
			else if (strings[0].startsWith(text))
				results.add(indexEntry);
			else if (strings[2].equals(text))
				results.add(indexEntry);
		}
	
		Log.d("z", "n "+n);
		if (results.size() > 0)
			return results.get(results.size()-1);
		else return null;
	}
	
	/**
	 * Reads bytes from the Dictionary fileget
	 * @param indexEntry - gives the position and length of translation in the file   
	 * @return the translation as String
	 */
	public static String readTranslation(IndexEntry indexEntry)
	{
		BufferedInputStream bis;
		try {
			if (Utils.isInternalDictionary())
				bis = new BufferedInputStream(mContext.getResources().openRawResource(Utils.getInternalDictionaryID()));
			else
				bis = new BufferedInputStream(new FileInputStream(Utils.getDictionaryFileName()));

			//bis.skip(1000);
			//byte[] buffer = new byte[500];
			
			bis.skip(indexEntry.pos);
			byte[] buffer = new byte[indexEntry.length];

			int bytesRead = bis.read(buffer);

			String s = new String(buffer, 0, indexEntry.length-1);
//			String s = new String(buffer, 0, 500);

			bis.close();

			return s;
		} catch (IOException t) {
			Toast.makeText(mContext,
						   "Error:" + t.toString(), Toast.LENGTH_LONG)
				.show();
			Utils.setInformation(""+t);
			return "";
		}
		
	}

	static final int BUFFER_SYZE = 2048;
	public static boolean createIndexAsinc(String dictionary_name) {
		
		String index_file_name;
		if (Utils.isInternalDictionary())
			index_file_name= Utils.APP_FOLDER+"/"+dictionary_name.replace(".xdxf", ".index");
		else	
			index_file_name= dictionary_name.replace(".xdxf", ".index");

		mIndexFileName = index_file_name;
		mDictionaryName = dictionary_name;
		
		progressDialog = new ProgressDialog(mContext);
		progressDialog.setTitle("Indexing dictionary...");
		progressDialog.setMessage(index_file_name);
		progressDialog.show();
		
		myTaskIndexing = new MyTaskIndexing();    
		myTaskIndexing.execute(index_file_name);
		return true;
	}	
		
	static class MyTaskIndexing extends AsyncTask<String, Integer, Void> {

		@Override
		protected Void doInBackground(String... params) {
			createIndex();
			return null;
		}
		
		@Override    
		protected void onCancelled(Void result) {      
			super.onCancelled(result);
			progressDialog.hide();
			progressDialog.dismiss();
			Utils.setInformation(info);
			Toast.makeText(mContext,
					"Cancelled", Toast.LENGTH_LONG) // mContext.getResources().getString(R.string.loading_cancelled)
					.show();
		}
		
		@Override    
		protected void onPostExecute(Void result) {      
			super.onPostExecute(result);
			progressDialog.hide();
			progressDialog.dismiss();
			Utils.setInformation(info);
			Log.d("","eventCallback --->>>>> "+eventCallback);
			if (eventCallback != null){
				eventCallback.indexingFinishedCallBack(mDictionaryName, mIndexFileName);
				}
		}
	}	
	
	public static boolean createIndex() {
		ArrayList<IndexEntry> indexEntries = new ArrayList<IndexEntry>();
		
		BufferedInputStream bis;
		BufferedOutputStream bos;
		
		
		String chunk;
		String chunk0 = "";
		
		byte[] buffer = new byte[BUFFER_SYZE];
		int bytesRead = 0;

		try {
			if (Utils.isInternalDictionary())
				bis = new BufferedInputStream(mContext.getResources().openRawResource(Utils.getInternalDictionaryID()));
			else
				bis = new BufferedInputStream(new FileInputStream(mDictionaryName));
			
			bos = new BufferedOutputStream(new FileOutputStream(mIndexFileName));
			
			int state = 0;
			int start = 0;
			int end = 0;
			int pos = 0;
			
			int prevTextEnd = 0;
			
			while ((bytesRead = bis.read(buffer)) != -1) {
				start = 0;
				end = 0;
				
				for (int i = 0; i < BUFFER_SYZE; i++){
					switch (state) {
					case 0:
						if (buffer[i] == '<')
						{	
							prevTextEnd = pos-1;
							state = 1;
						}	
						break;
					case 1:
						if (buffer[i] == 'k')
							state = 2;
						else 
							state = 0;
						break;
					case 2:
						if (buffer[i] == '>'){
							state = 3;
							start = i+1;
						}	
						break;
					case 3:
						if (buffer[i] != '<'){
						} else {
							end = i;
							state = 4;
						}
						break;
					case 4:
						if (buffer[i] == 'k')
							state = 5;
						break;
					case 5:
						if (buffer[i] == '>'){
						    chunk = new String(buffer, start, end-start);
						    if ((chunk0+chunk).trim().length() > 0
						    &&  !chunk.startsWith("  ")){
							    indexEntries.add(new IndexEntry((chunk0+chunk).trim(), pos+1, prevTextEnd));
							    chunk0 = "";
						    }
							state = 0;
						}
						else
							state = 0;
						break;
					default:	
					    Log.d("", "state ??? "+state);
					}
					pos++;
				}
				if (state == 3){
				    chunk0 = new String(buffer, start, BUFFER_SYZE-start);
				    if (chunk0.startsWith("  ")){
				    	chunk0 = "";
				    	state = 0;
				    }
				}    
				else
					chunk0 = "";	
			}		

			
			int ind = 0;
			for (IndexEntry IndexEntry: indexEntries){
				if (ind < indexEntries.size()-1)
					IndexEntry.length = indexEntries.get(ind+1).length - IndexEntry.pos + 2;
				else	
					IndexEntry.length = pos - IndexEntry.pos;
				ind++;
			}
			
			EntryComparator ec = new EntryComparator();
			java.util.Collections.sort(indexEntries, ec);			
			
			for (IndexEntry IndexEntry: indexEntries){
				bos.write(IndexEntry.text.getBytes());
				bos.write((char)(0x9));
				bos.write(String.format("%x", IndexEntry.pos).getBytes());
				bos.write((char)(0x9));
				bos.write(String.format("%x", IndexEntry.length).getBytes());
				bos.write((char)(0xa));
			}
			
			bos.flush();
			bos.close();
			
			//Utils.dictionary_name = name;
			//Utils.dictionary_index = index;
			
			info = "Create index <"+mIndexFileName+">. "+indexEntries.size()+" entries.";
			return true;
		} catch (IOException t) {
			info = "Error indexing "+t;
			return false;
		}
	}
	
	static final class EntryComparator implements Comparator<IndexEntry> {
		  public int compare(IndexEntry e1, IndexEntry e2) {
		    return e1.text.compareToIgnoreCase(e2.text);
		  }
	}
	

	public void remove(int line, int pos) {
		// TODO Auto-generated method stub
		
	}

	public CharSequence getFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	public static int getCount() {
		return indexEntries.size();
	}

	public String[] getDictionaryAsStrings() {
		String[] strings = new String[indexEntries.size()];
		
		int n = 0;
		for (IndexEntry i: indexEntries)
			strings[n++] = i.text;
		
		return strings;
	}
		
	public static ArrayList<IndexEntry> getIndexEntries(){
		return indexEntries;
	}

	public static String getDictionaryInfo() {
			try {
				BufferedReader reader;
				if (Utils.isInternalDictionary())
					reader = new BufferedReader(new InputStreamReader(mContext.getResources().openRawResource(Utils.getInternalDictionaryID())));
				else
					reader = new BufferedReader(new InputStreamReader(new FileInputStream(Utils.getDictionaryFileName())));
				
				String line = reader.readLine();
				int i = 0;
				while (i < 20 && line != null){
					int j = line.indexOf("<full_name>");
					if (j >= 0){
						reader.close();
						int k = line.indexOf("</full_name>");
						if (k >=0)
							return line.substring(j+11,k);
						else
							return line.substring(j+11);
					}
					line = reader.readLine();
				}
				reader.close();
				return "";
			} catch (IOException t) {
				Utils.setInformation(""+t);
				return "";
			}
	}
	 
	private boolean isSeparator(char charAt) {
		Pattern p = Pattern.compile(";,.!?");
		return ";,.!?".contains(""+charAt);		
	}

}

