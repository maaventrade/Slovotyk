package com.alexmochalov.bmk;

import java.util.ArrayList;
import java.util.List;

import com.alexmochalov.main.Mark;
import com.alexmochalov.slovotyk.R;
import com.alexmochalov.slovotyk.R.id;
import com.alexmochalov.slovotyk.R.layout;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * 
 * @author Alexey Mochalov
 * This Adapter shows the list of the bookmarks
 *
 */
public class ArrayAdapterBookmarks  extends ArrayAdapter<Mark>{
	private ArrayList<Bookmark> values;
	Context context;
	int resource;

	public ArrayAdapterBookmarks(Context context, int res,
								 ArrayList<Bookmark> values) {
		super(context, res);
		this.values = values;
		this.resource = res;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) { 
			LayoutInflater inflater = (LayoutInflater) context.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
			convertView = inflater.inflate(R.layout.bookmark, null);
		}

		Bookmark bookmark = values.get(position);
		// Bookmark text
		TextView text = (TextView)convertView.findViewById(R.id.TextViewBookmark);
		text.setText(bookmark.text);
		// The number of string where the bookmark is set
        TextView text2 = (TextView)convertView.findViewById(R.id.TextViewBookmarkLine);
		text2.setText(""+bookmark.line);

		return convertView;
	}

	public int getCount(){
		return values.size();
	}

	public long getItemId(int position){
		return position;
	}	
	
}
