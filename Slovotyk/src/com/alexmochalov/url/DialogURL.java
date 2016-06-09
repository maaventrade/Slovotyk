package com.alexmochalov.url;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.alexmochalov.dic.Entry;
import com.alexmochalov.slovotyk.R;
import com.alexmochalov.slovotyk.Utils;
import com.alexmochalov.slovotyk.R.id;
import com.alexmochalov.slovotyk.R.layout;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Button;

public class DialogURL extends Dialog {
	Context mContext;
	public OnEventListener listener;
	public interface OnEventListener{
		public void onURLSelected(String url, String name);
	}
	
	// что находится на сайте
	String[] groups = new String[] { "Оскар Уайльд", "Сериал \"Друзья\"" };

	// названия ссылок на каждом сайте
	ListItem[] items2 = new ListItem[] { 
			new ListItem("The One Where Monica Gets a New Roommate","http://friends.tktv.net/Episodes1/summaries/1.html"),
			new ListItem("The One with the Sonogram at the End","http://friends.tktv.net/Episodes1/summaries/2.html"),
			new ListItem("The One With the Thumb","http://friends.tktv.net/Episodes1/summaries/3.html"),
			new ListItem("The One With George Stephanopoulos","http://friends.tktv.net/Episodes1/4.html"),
			new ListItem("The One with the East German Laundry Detergent","http://friends.tktv.net/Episodes1/5.html"),
			new ListItem("The One with the Butt","http://friends.tktv.net/Episodes1/6.html"),
			new ListItem("The One with the Blackout","http://friends.tktv.net/Episodes1/7.html"),
			new ListItem("The One Where Nana Dies Twice","http://friends.tktv.net/Episodes1/8.html")};
	
	ListItem[] items1 = new ListItem[] { 
			new ListItem("The Picture of Dorian Gray", "http://oscar-wilde.ru/english/portret-doriana-greya-na-angliyskom-yazyke.html"),
			new ListItem("The Canterville Ghost", "http://oscar-wilde.ru/english/the-canterville-ghost-na-angliyskom-yazyke-kentervilskoe-prividenie.html"),
			new ListItem("The Happy Prince", "http://oscar-wilde.ru/english/the-happy-prince-na-angliyskom-yazyke-schastlivyy-prints.html"),
			new ListItem("The Nightingale and the Rose", "http://oscar-wilde.ru/english/the-nightingale-and-the-rose-na-angliyskom-yazyke-solovey-i-roza.html"),
			new ListItem("The Selfish Giant", "http://oscar-wilde.ru/english/the-selfish-giant-na-angliyskom-yazyke-velikan-egoist.html"),
			new ListItem("The Devoted Friend", "http://oscar-wilde.ru/english/the-devoted-friend-na-angliyskom-yazyke-predannyy-drug.html"),
			new ListItem("The Remarkable Rocket", "http://oscar-wilde.ru/english/the-remarkable-rocket-na-angliyskom-yazyke-zamechatelnaya-raketa.html"),
			new ListItem("The Star-child", "http://oscar-wilde.ru/english/the-star-child-na-angliyskom-yazyke-malchik-zvezda.html"),
			new ListItem("The Birthday of the Infanta", "http://oscar-wilde.ru/english/the-birthday-of-the-infanta-na-angliyskom-yazyke-den-rozhdeniya-infanty.html"),
			new ListItem("The Fisherman and his Soul", "http://oscar-wilde.ru/english/the-fisherman-and-his-soul-na-angliyskom-yazyke-rybak-i-ego-dusha.html"),
			new ListItem("The Young King", "http://oscar-wilde.ru/english/the-young-king-na-angliyskom-yazyke-molodoi-korol.html")
					};
	
	// коллекция для групп
	ArrayList<Map<String, ListItem>> groupData;

	// коллекция для элементов одной группы
	ArrayList<Map<String, ListItem>> childDataItem;

	// общая коллекция для коллекций элементов
	ArrayList<ArrayList<Map<String, ListItem>>> childData;
	// в итоге получится childData = ArrayList<childDataItem>

	Map<String, ListItem> m;

	ExpandableListView expandableListOfURLs;

	public DialogURL(Context context) {
		super(context, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
		mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.select_url);
		
    	final EditText URL = (EditText)findViewById(R.id.editTextURL);
		
		// заполняем коллекцию групп из массива с названиями групп
        groupData = new ArrayList<Map<String, ListItem>>();
        for (String group : groups) {
          // заполняем список аттрибутов для каждой группы
          m = new HashMap<String, ListItem>();
            m.put("groupName", new ListItem(group)); // имя компании
            groupData.add(m);  
        }
        
        // список аттрибутов групп для чтения
        String groupFrom[] = new String[] {"groupName"};
        // список ID view-элементов, в которые будет помещены аттрибуты групп
        int groupTo[] = new int[] {android.R.id.text1};
        

        // создаем коллекцию для коллекций элементов 
        childData = new ArrayList<ArrayList<Map<String, ListItem>>>(); 
        
        // создаем коллекцию элементов для первой группы
        childDataItem = new ArrayList<Map<String, ListItem>>();
        // заполняем список аттрибутов для каждого элемента
        for (ListItem item : items1) {
          m = new HashMap<String, ListItem>();
            m.put("itemName", item); // название 
            childDataItem.add(m);  
        }
        // добавляем в коллекцию коллекций
        childData.add(childDataItem);

        // создаем коллекцию элементов для второй группы        
        childDataItem = new ArrayList<Map<String, ListItem>>();
        for (ListItem item : items2) {
          m = new HashMap<String, ListItem>();
            m.put("itemName", item);
            childDataItem.add(m);  
        }
        childData.add(childDataItem);

        // список аттрибутов элементов для чтения
        String childFrom[] = new String[] {"itemName"};
        // список ID view-элементов, в которые будет помещены аттрибуты элементов
        int childTo[] = new int[] {android.R.id.text1};
        
        final AdapterURL adapter = new AdapterURL(mContext);

		Button b = (Button)findViewById(R.id.buttonLoadURL);
		b.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
				//if (listener != null)
				//	listener.onURLSelected(url);
	        	dismiss();
			}
			
		});

		b = (Button)findViewById(R.id.buttonCancelURL);
		b.setOnClickListener(new Button.OnClickListener(){
			@Override
			public void onClick(View v) {
	        	dismiss();
			}
			
		});
        
        expandableListOfURLs = (ExpandableListView) findViewById(R.id.expandableListOfURLs);
        expandableListOfURLs.setAdapter(adapter);
        expandableListOfURLs.setOnChildClickListener(new OnChildClickListener(){
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				    
				if (listener != null)
					listener.onURLSelected(adapter.getChild(groupPosition, childPosition).mPath, 
							adapter.getChild(groupPosition, childPosition).mName);
	        	dismiss();
				
				//URL.setText(s);
				return false;
			}});
        
    }		

	
	class ListItem{
		String mName;
		String mPath;
		
		ListItem(String name){
			super();
			mName = name;
			mPath = name;
		}
		
		ListItem(String name, String path){
			super();
			mName = name;
			mPath = path;
		}
	}

	
	public class AdapterURL  extends BaseExpandableListAdapter{
	    private Context mContext;

		public AdapterURL(Context context) {
			super();
			mContext = context;
		}

		@Override
		public ListItem getChild(int groupPosition, int childPosition) {
			return childData.get(groupPosition).get(childPosition).get("itemName");
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {

			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(android.R.layout.simple_list_item_1, null);
			}

			TextView item = (TextView) convertView
					.findViewById(android.R.id.text1);
			item
					.setText(childData.get(groupPosition).get(childPosition).get("itemName").mName);

			return convertView;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			return childData.get(groupPosition).size();
		}

		@Override
		public Object getGroup(int groupPosition) {
			return null;
		}

		@Override
		public int getGroupCount() {
			return groupData.size();
		}

		@Override
		public long getGroupId(int groupPosition) {
			return 0;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {

			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(android.R.layout.simple_expandable_list_item_1, null);
			}

			TextView group = (TextView) convertView
					.findViewById(android.R.id.text1);
			group.setText(groupData.get(groupPosition).get("groupName").mName);

			return convertView;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}

	}

}
