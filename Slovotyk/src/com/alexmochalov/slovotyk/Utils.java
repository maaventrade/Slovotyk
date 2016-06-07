package com.alexmochalov.slovotyk;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.widget.TextView;

public final class Utils {
	private static TextView textViewInformation;
	private static ActionBar actionBar;
	private static Activity activity;

	private static boolean textLoading = false; 
	private static String textLoadingStr = ""; 
	
	// Gaps
	public static final int XGAP = 5; // left and right 
	public static final int PARAG = 25; // 

	public static final char CHAR_BM =  1;
	public static final char CHAR_TMP_SEL_START = 2;
	public static final char CHAR_TMP_SEL_END = 3;
	public static final char CHAR_SEL_START = 4;
	public static final char CHAR_SEL_END = 5;
	
	public static final char CHAR_BOLD = 6;
	public static final char CHAR_BOLD_END = 7;

	public static final char  CHAR_ITALIC = 18;
	public static final char  CHAR_ITALIC_END = 19;
	
	public static final char CHAR_TAB =  9;
	
	static String PROGRAMM_FOLDER = "xolosoft";
	public static String APP_FOLDER = Environment.getExternalStorageDirectory().getPath()+"/"+PROGRAMM_FOLDER+"/Slovotyk";
	String RESULTS_FOLDER = "results";
	static String EXTERNAL_STORAGE_DIRECTORY = Environment.getExternalStorageDirectory().getPath();
	public static String fileName = "";

	public static String dictionary_name = "";
	public static String dictionary_index = "";

	private static int internalDictionaryID = 0;
	private static String internalDictionary = "";
	
	public static boolean modified = false;

	private static int firstLine = -1;
	private static int firstLinePixelShift = -1;
	private static int firstPosition = -1;
	
	public static String index_file_name = "";

	public static void restoreViewParams(Object object)
	{
		if (object instanceof ViewTextSelectable){
			if (firstLine >= 0)
				((ViewTextSelectable)object).setFirstLine(firstLine, firstLinePixelShift);
		} else {
			((Lexicon)object).setPosition(firstPosition);
		}
		
	}

	public static void restoreViewParams()
	{
		Lexicon.setPosition(firstPosition);
	}
	
	public static void saveViewParams()
	{
		firstPosition = Lexicon.getPosition();
	}
	
	public static void saveViewParams(ViewTextSelectable object)
	{
			if (object != null){
				firstLine = ((ViewTextSelectable)object).getFirstLine();
				firstLinePixelShift = ((ViewTextSelectable)object).getFirstLinePixelShift();
			}
	}

	public static String getAppDirectory() {
		File file = new File(EXTERNAL_STORAGE_DIRECTORY, PROGRAMM_FOLDER);
		if(!file.exists()){                          
			file.mkdirs();                  
		}
		return file.getAbsolutePath() + "/";
	}

	public static CharSequence getFileName() {
		if (fileName.length() == 0) return "No text";
		int i = fileName.lastIndexOf("/");
		if (i > 0)
			return fileName.substring(i+1);
		else
			return fileName;
	}

	public static CharSequence getFilePath() {
		if (fileName.length() == 0)
			return "No file opened";
		else
			return fileName;
	}

	public static CharSequence getDictionaryPath() {
		String name = "";
		if (internalDictionaryID != 0)
			name = "Internal: ";
		if (dictionary_name.length() == 0)
			return "No dictionary";
		else
			return name+dictionary_name;
	}

	public static CharSequence getIndexPath() {
		if (index_file_name.length() == 0)
			return "No index opened";
		else
			return index_file_name;
	}

	public static CharSequence getDictionaryName() {
		if (dictionary_name.length() == 0)
			return "No dictionary";
		String name = "";
		if (internalDictionaryID != 0)
			name = "Internal: ";
		int i = dictionary_name.lastIndexOf("/");
		if (i > 0)
			return name+dictionary_name.substring(i+1);
		else
			return name+dictionary_name;
	}
	
	public static void setInformation(String info){
		/*
		String text = textViewInformation.getText().toString();
		if (text.length() > 0)
			text = text+'\n'+info;
		else
			text = info;
		textViewInformation.setText(text);
		textViewInformation.setVisibility(View.VISIBLE);
		*/
	}

	public static void clearInfo() {
		/*
		textViewInformation.setText("");
		textViewInformation.setVisibility(View.INVISIBLE);
		*/
	}
	
	public int findBlank0(char[] chars, int prevPos, int nextPos){
		if (prevPos + nextPos >= chars.length) return nextPos;

		char c = chars[prevPos + nextPos];
		if (c == ',' || c == '.' || c == '?' || c == '!' || c == ';'  || c == ' ' ) return nextPos;

		char c1 = chars[prevPos + nextPos-1];
		if (c1 == ',' || c1 == '.' || c1 == '?' || c1 == '!' || c1 == ';'  || c1 == ' ' ) return nextPos;

		for (int i = prevPos + nextPos-1; i > prevPos+1; i--)
			if (chars[i] == ',' || chars[i] == '.' || chars[i] == '?' || chars[i] == '!' || chars[i] == ';'  || chars[i] == ' ' ){
				return i-prevPos;
			}

		return nextPos;
	}
	

	public static int findBlank(Paint paint, int width, int start, float x, String S){
		int prevBlank = S.length();
		int index = start;
		while (index < S.length()){
			char c = S.charAt(index);

			if (c == ' '){
				prevBlank = index;
			}
			else if (c < 20){
				if (c == 3)
					paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
				else if (c == 4)
					paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
				index++;
				continue;
			} 
			x = x + paint.measureText(""+c);
			if (x >= width - XGAP- XGAP){
				return prevBlank;
			}

			index++;
		}	
		//Log.d("", "prevBlank "+prevBlank);
		return S.length();
	}

	@SuppressLint("NewApi")
	public static void setActionBar(Activity activity_) {
		actionBar = activity_.getActionBar();
		actionBar.setHomeButtonEnabled(true);
		actionBar.setDisplayHomeAsUpEnabled(true);
		//actionBar.setIcon(new ColorDrawable(activity_.getResources().getColor(android.R.color.transparent)));
		//actionBar.setDisplayShowTitleEnabled(false);
		
		activity = activity_;
	}

	public static void setActionbarTitle(CharSequence charSequence, boolean displayHome){
		actionBar.setDisplayHomeAsUpEnabled(displayHome);
		actionBar.setDisplayShowHomeEnabled(!displayHome);
		//actionBar.setDisplayShowTitleEnabled(false);
		/*
		if (charSequence.length() == 0)
			actionBar.setDisplayShowTitleEnabled(false);
		else {
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setTitle(charSequence);
		}
		*/
		//if (charSequence.length() != 0)
		//actionBar.setTitle(charSequence);
		
	}
	
	public static void setViewInformation() {
		/*
       	Utils.textViewInformation = (TextView)activity.findViewById(R.id.textViewInformation);
       	Utils.textViewInformation.setMovementMethod(new ScrollingMovementMethod());
       	Utils.textViewInformation.setOnLongClickListener(new OnLongClickListener(){
			@Override
			public boolean onLongClick(View v) {
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        switch (which){
				        case DialogInterface.BUTTON_POSITIVE:
				        	Utils.clearInfo();
				            break;

				        case DialogInterface.BUTTON_NEGATIVE:
				            //No button clicked
				            break;
				        }
				    }
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
				    .setNegativeButton("No", dialogClickListener).show();				return false;
			}});
		 */
	}

	public static void saveViewParams(int int1, int int2) {
		firstLine = int1;
		firstLinePixelShift = int2;
	}

	public static void setInternalDictionary(String name) {
		internalDictionary = name;
		if (name.equals("eng_ru.xdxf"))  
			internalDictionaryID = R.raw.eng_ru;
		else if (name.equals("span_eng.xdxf"))
			internalDictionaryID = R.raw.span_eng;
		else
			internalDictionaryID = 0;
	}

	public static boolean isInternalDictionary() {
		return internalDictionaryID != 0;
	}

	public static int getInternalDictionaryID() {
		return internalDictionaryID;
	}

	public static String getInternalDictionary() {
		return internalDictionary;
	}
	
	public static int countLines(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];
	        int count = 0;
	        int readChars = 0;
	        boolean empty = true;
	        while ((readChars = is.read(c)) != -1) {
	            empty = false;
	            for (int i = 0; i < readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	        }
	        return (count == 0 && !empty) ? 1 : count;
	    } finally {
	        is.close();
	    }
	}
	
	public static void setTextLoading(boolean param){
		textLoading = param;
	}
	
	public static void setTextLoadingStr(String param){
		textLoadingStr = param;
	}
	
	public static boolean getTextLoading(){
		return textLoading;
	}
	
	public static String getTextLoadingStr(){
		return textLoadingStr;
	}
	
	
	public static String[] getStringForms(String string){
		String[] strings = {string, string, string};
		if (string.endsWith("ied"))
			strings[1] = string.replace("ied","y");
		else
			if (strings[0].endsWith("d"))
				strings[0] = strings[0].substring(0, string.length()-1);
		else 
			if (string.endsWith("ing") && string.length() > 3)
				strings[2] = string.substring(0, string.length()-3)+"e";
		
		return strings;
	}

	
}

