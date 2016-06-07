package com.alexmochalov.dic;

import java.util.ArrayList;

import com.alexmochalov.slovotyk.R;
import com.alexmochalov.slovotyk.R.id;
import com.alexmochalov.slovotyk.R.layout;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
//import com.alex.mochalov.TextToDic.*;
//import com.alex.mochalov.TextToDict.R;
import android.content.Context;
import android.graphics.Typeface;


public class ArrayAdapterDic extends ArrayAdapter<Entry>{
	private ArrayList<Entry> values;
	Context context;
	int resource;

	private Typeface face;

	public ArrayAdapterDic(Context context, int res, ArrayList<Entry> values){
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

