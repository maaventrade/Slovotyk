package com.alexmochalov.dic;

import java.util.ArrayList;

import com.alexmochalov.slovotyk.R;
import com.alexmochalov.slovotyk.R.id;
import com.alexmochalov.slovotyk.R.layout;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

/**
 * 
 * @author Alexey Mochalov
 * This Adapter shows the drop-down list in the Dictionary 
 *
 */
public class ArrayAdapterDictionary  extends ArrayAdapter<IndexEntry>
{
	private ArrayList<IndexEntry> values;
	private ArrayList<IndexEntry> suggestions;
	private ArrayList<IndexEntry> valuesAll;
	
	Context context;
	int resource;

	public ArrayAdapterDictionary(Context context, int res, ArrayList<IndexEntry> values){
		super(context, res, values);
		this.values = values;
		this.valuesAll = (ArrayList<IndexEntry>) values.clone();
		this.suggestions = new ArrayList<IndexEntry>();
		
		this.resource = res;
		this.context = context;
		
		Log.d("", "values----> "+values.size());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) { 
			LayoutInflater inflater = (LayoutInflater) context.
				getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
			convertView = inflater.inflate(R.layout.dic_string, null);
		}
		IndexEntry entry = values.get(position);
				
		TextView text = (TextView)convertView.findViewById(R.id.dicstringTextView);
		text.setText(""+entry.text);

		return convertView;
	}

	public int getCount(){
		return values.size();
	}

	public long getItemId(int position){
		return position;
	}	

	private Filter mFilter = new Filter() 
	{
		//@Override 
		public String convertResultToString(Object resultValue) {
			return ((IndexEntry)resultValue).getName();
		}
		
		@Override 
		protected FilterResults performFiltering(CharSequence constraint) { 
			if(constraint != null) {
                suggestions.clear();
                for (IndexEntry customer : valuesAll) {
                    if(customer.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())){
                        suggestions.add(customer);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
		}
		
		@Override 
		protected void publishResults(CharSequence constraint, FilterResults results)
		{
			ArrayList<IndexEntry> filteredList = (ArrayList<IndexEntry>) results.values;
            if(results != null && results.count > 0) {
                clear();
                for (IndexEntry c : filteredList) {
                    add(c);
                }
                notifyDataSetChanged();
            }
		} 
	};
		
		@Override 
		public Filter getFilter() { 
			return mFilter; 
		}

}
 
