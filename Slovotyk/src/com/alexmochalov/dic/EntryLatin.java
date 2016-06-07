package com.alexmochalov.dic;

import java.util.*;

public class EntryLatin
{
	private String text;
	private String translation;
	
	public EntryLatin(String text, String translation){
		super();
		this.text = text;
		this.translation = translation;
	}

	public String getText() {
		return text;
	}

	public String getTranslation() {
		return translation;
	}

}
