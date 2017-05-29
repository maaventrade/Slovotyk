package com.alexmochalov.main;

import android.annotation.*;
import android.os.*;
import android.speech.tts.*;
import java.util.*;
import android.widget.*;
import android.content.*;

public class TtsUtils
{
	private static TextToSpeech tts;
	private Locale locale;
	private static boolean  langSupported;
	private static Context mContext;

	public static void speak(String text)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			ttsGreater21(text);
		} else {
			ttsUnder20(text);
		}

	}
	
	public static void newTts(MainActivity p){
		tts = new TextToSpeech(p, p);
	}
	
	public static void destroy()
	{
		if (tts != null) tts.shutdown();
	}

	public static void init(Context context)
	{
		mContext = context;
		langSupported = false;
		loadLangueges();
		Locale locale[] = Locale.getAvailableLocales();
		for (int i=0; i< locale.length; i++){
			//Log.d("", ""+locale[i].getISO3Language());
			//Toast.makeText(this, locale[i].getLanguage().toUpperCase()+"  "+language, Toast.LENGTH_LONG).show();
			if (locale[i].getISO3Language().equals(Utils.getaLanguage())){
				if (tts.isLanguageAvailable(locale[i]) == tts.LANG_NOT_SUPPORTED){
					Toast.makeText(context,"язык не поддерживается"+" ("+Utils.getaLanguage()+") ", Toast.LENGTH_LONG).show();
					langSupported = false;
				} else {
					langSupported = true;
					tts.setLanguage(locale[i]);
				}	
				return; 
			}}
		Toast.makeText(context,"язык не найден"+" ("+Utils.getaLanguage()+") ", Toast.LENGTH_LONG).show();
		
	}
	
	@SuppressWarnings("deprecation")
	private static void ttsUnder20(String text) {
		HashMap<String, String> map = new HashMap<>();
		map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
		tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	private static void ttsGreater21(String text) {
		String utteranceId = mContext.hashCode() + "";
		tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
	}
	
	private static void loadLangueges(){
    	Locale locale[] = Locale.getAvailableLocales();
        //Spinner spinnerLanguages = ((Spinner)findViewById(R.id.spinnerLanguages));
    	/*
		 ArrayList<String> languages = new ArrayList<String>();
		 for (int i=0; i< locale.length; i++)
		 if (tts.isLanguageAvailable(locale[i]) != tts.LANG_NOT_SUPPORTED)
		 if (! languages.contains(locale[i].getISO3Language()))
		 languages.add(locale[i].getISO3Language()); 
		 adapter = new ArrayAdapter(this,
		 android.R.layout.simple_spinner_item, languages);
		 spinner.setAdapter(adapter);
		 */
    }
}
