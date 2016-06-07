package com.alexmochalov.slovotyk;

import java.util.ArrayList;

import com.alexmochalov.dic.Entry;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.content.Context;
import android.graphics.Typeface;

/**
 * 
 * @author Alexey Mochalov
 * This Adapter shows the list of the the Lexicon entries
 *
 */
public class ArrayAdapterLexicon extends ArrayAdapter<Entry>{
	private ArrayList<Entry> values;
	Context context;
	int resource;
	// Font to disply phonetic symbols 
	private Typeface face;

	public ArrayAdapterLexicon(Context context, int res, ArrayList<Entry> values){
		super(context, res, values);
		this.values = values;
		this.resource = res;
		this.context = context;
		face = Typeface.createFromAsset(context.getAssets(), "fonts/Constructium.ttf");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) { 
			LayoutInflater inflater = (LayoutInflater) context.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
			convertView = inflater.inflate(R.layout.entry, null);
		}

		Entry entry = values.get(position);

		TextView text = (TextView)convertView.findViewById(R.id.text);
		text.setText(entry.getText());

        TextView text2 = (TextView)convertView.findViewById(R.id.translation);
		text2.setText(Html.fromHtml(entry.getTranslation()));

		TextView text3 = (TextView)convertView.findViewById(R.id.phonetic);
		text3.setText(entry.getPhonetic());
		
		text3.setTypeface(face); 

		TextView text1 = (TextView)convertView.findViewById(R.id.sample);

		text1.setText(Html.fromHtml(entry.getSample()));

		return convertView;
	}

	public int getCount(){
		return values.size();
	}

	public long getItemId(int position){
		return position;
	}	



}

