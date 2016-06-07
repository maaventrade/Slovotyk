package com.alexmochalov.dic;

import android.*;
import android.text.*;
import android.util.*;
import android.widget.*;

import java.io.*;
import java.util.ArrayList;

import org.xmlpull.v1.*;

import android.R;

public class Entry
{

	private String text = "";
	private String translation = "";
	private String sample = "";
	private String phonetic = "";
	private int count = 0;
	
	private ArrayList<EntryLatin> entriesLatin;

	private boolean archived = false;

	public Entry() {
		super();
		entriesLatin  = new ArrayList<EntryLatin>();
	}	

	public Entry(String text, String translation, String sample) {
		super();
		this.text = text;
		this.entriesLatin  = new ArrayList<EntryLatin>();
		
		setTranslationAndPhonetic(translation, text);
		
		this.sample = sample;
		
	}

	public void save(Writer writer)
	{
		try
		{
			writer.write("<entry>" + "\n");
		writer.write("<text>" + text + "</text>\n");
		writer.write("<translation>"+ TextUtils.htmlEncode(translation) +"</translation>\n");
		writer.write("<count>"+count+"</count>\n");
		writer.write("<sample>"+TextUtils.htmlEncode(sample)+"</sample>\n");
		writer.write("<archived>"+archived+"</archived>\n");
		writer.write("<phonetic>"+phonetic+"</phonetic>\n");
		writer.write("</entry>"+"\n");
		} catch (IOException e) {
			//Toast.makeText(context, context.getResources().getString(R.string.error_save_file) , Toast.LENGTH_LONG).show();
		}
	}

	public String getPhonetic()
	{
		return phonetic;
	}

	public void setFromXpp(String n, XmlPullParser xpp)
	{
		if (n.equals("text"))
			text = xpp.getText();
		else if (n.equals("translation"))
			translation = xpp.getText();
		else if (n.equals("sample"))
			sample = xpp.getText();
		else if (n.equals("phonetic"))
			phonetic = xpp.getText();
		else if (n.equals("archived"))
			archived = xpp.getText().equals("true");
		
	}

	public ArrayList<EntryLatin> getEntryLatin()
	{
		return entriesLatin;
	}

	public String getSample()
	{
		return sample;
	}

	public String getTranslation()
	{
		return translation;
	}

	public String getText()
	{
		return text;
	}

	public void setTranslationAndPhonetic(String translation, String text) {
		this.phonetic = "";
		
		
		int i = translation.indexOf("<tr>");
		boolean phoneticFound = false;
		if (i >= 0){
			int j = translation.indexOf("</tr>");
			if (j > i){
				this.phonetic = translation.substring(i+4, j);
				translation = translation.replace(translation.substring(i, j+5), "");
				phoneticFound = true;
			}
		}
		if (!phoneticFound){
			i = translation.indexOf("[");
			if (i >= 0){
				int j = translation.indexOf("]");
				if (j > i){
					this.phonetic = translation.substring(i+1, j);
					translation = translation.replace(translation.substring(i, j+2), "");
				}
			}
		}
		
		if (translation.startsWith(text))
			translation = translation.substring(text.length());
		
		translation = translation.trim();
		translation = translation.replace(""+(char)0xa+(char)0xa, ""+(char)0xa);
		translation = translation.replace(""+(char)0xa, "<br>");
		translation = translation.replace("<ar>", "");
		translation = translation.replace("</ar>", "");
		translation = translation.replace("_Ex:", "\t_Ex:");

		int state = 0;
		int startLatinText = 0;
		
		for (i = translation.length()-1; i >= 0; i--){
			char c = translation.charAt(i);
			switch (state){
				case 0:
					if (isLatinLetter(c)){
						state = 2;
						startLatinText = i+1;
					}
					break;
				case 2:
					if (! isLatinLetterOrNotLetter(c) && startLatinText >= 0){
						state = 0;
						if (startLatinText > i+3 && translation.substring(i+3, startLatinText).indexOf(text) >= 0){
							entriesLatin.add(new EntryLatin(translation.substring(i+3, startLatinText).trim(),
									translation.substring(startLatinText).trim()));
							translation = translation.substring(0, i+3)+"<br><b>"+
									  translation.substring(i+3, startLatinText)+"</b>"+translation.substring(startLatinText);
						}
						startLatinText = -1;
					}
					break;
			}
		}
		
		state = 0;
		String level = "";
		
		for (i = translation.length()-1; i >= 0; i--){
			char c = translation.charAt(i);
			switch (state){
				case 0:
					if (c == '>'){
						state = 1;
						level = "<br>\t";
					}	
					else if (c == '.'){
						state = 2;
						level = "<br><br>";
					}	
					break;
				case 1:
					if (c == ' '){
						translation = translation.substring(0, i)+level+translation.substring(i); 
						state = 0;
					} else if (!Character.isDigit(c) && !isRusLowerCase(c))
						state = 0;
					break;
				case 2:
					if (c == ' '){
						translation = translation.substring(0, i)+level+translation.substring(i); 
						state = 0;
					} else if (!Character.isDigit(c))
						state = 0;
					break;
			}
		}
		
		this.translation = translation.trim();
		//Log.d("*",translation);
	}

	public boolean isRusLowerCase(char c){
		return (c >= 1072 && c <= 1103);
	}

	public boolean isLatinLetter(char c){
		c = Character.toUpperCase(c);
		return (c >= 'A' && c <= 'Z');
	}

	public boolean isLatinLetterOrNotLetter(char c){
		c = Character.toUpperCase(c);
		return (c >= 'A' && c <= 'Z' || (!Character.isLetter(c)));
	}
	
	public void refresh() {
		setTranslationAndPhonetic(Dictionary.find(text), text);
	}
}
