package com.alexmochalov.slovotyk;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.alexmochalov.dic.Entry;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

public class DialogURL extends Dialog {
	private Context mContext;

	// что находится на сайте
	String[] groups = new String[] { "Сериал \"Друзья\"", "Оскар Уайльд" };

	// названия ссылок на каждом сайте
	String[] items1 = new String[] {"http://livesinabox.com/friends/season1/101pilot.htm",
    		"http://livesinabox.com/friends/season1/102towsg.htm",
    		"http://livesinabox.com/friends/season1/103thumb.htm",
    		"http://livesinabox.com/friends/season1/104towgs.htm",
    		"http://livesinabox.com/friends/season1/105egld.htm",
    		"http://livesinabox.com/friends/season1/106butt.htm",
    		"http://livesinabox.com/friends/season1/107towbo.htm"
    		};
	
	String[] items2 = new String[] { 
			"http://oscar-wilde.ru/english/portret-doriana-greya-na-angliyskom-yazyke.html", 
			"http://oscar-wilde.ru/english/the-canterville-ghost-na-angliyskom-yazyke-kentervilskoe-prividenie.html", 
			"http://oscar-wilde.ru/english/the-happy-prince-na-angliyskom-yazyke-schastlivyy-prints.html", 
			"http://oscar-wilde.ru/english/the-nightingale-and-the-rose-na-angliyskom-yazyke-solovey-i-roza.html", 
			"http://oscar-wilde.ru/english/the-selfish-giant-na-angliyskom-yazyke-velikan-egoist.html", 
			"http://oscar-wilde.ru/english/the-devoted-friend-na-angliyskom-yazyke-predannyy-drug.html", 
			"http://oscar-wilde.ru/english/the-remarkable-rocket-na-angliyskom-yazyke-zamechatelnaya-raketa.html", 
			"http://oscar-wilde.ru/english/the-star-child-na-angliyskom-yazyke-malchik-zvezda.html", 
			"http://oscar-wilde.ru/english/the-birthday-of-the-infanta-na-angliyskom-yazyke-den-rozhdeniya-infanty.html", 
			"http://oscar-wilde.ru/english/the-fisherman-and-his-soul-na-angliyskom-yazyke-rybak-i-ego-dusha.html",
			"http://oscar-wilde.ru/english/the-young-king-na-angliyskom-yazyke-molodoi-korol.html"
			};

	// коллекция для групп
	ArrayList<Map<String, String>> groupData;

	// коллекция для элементов одной группы
	ArrayList<Map<String, String>> childDataItem;

	// общая коллекция для коллекций элементов
	ArrayList<ArrayList<Map<String, String>>> childData;
	// в итоге получится childData = ArrayList<childDataItem>

	// список аттрибутов группы или элемента
	Map<String, String> m;

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
        groupData = new ArrayList<Map<String, String>>();
        for (String group : groups) {
          // заполняем список аттрибутов для каждой группы
          m = new HashMap<String, String>();
            m.put("groupName", group); // имя компании
            groupData.add(m);  
        }
        
        // список аттрибутов групп для чтения
        String groupFrom[] = new String[] {"groupName"};
        // список ID view-элементов, в которые будет помещены аттрибуты групп
        int groupTo[] = new int[] {android.R.id.text1};
        

        // создаем коллекцию для коллекций элементов 
        childData = new ArrayList<ArrayList<Map<String, String>>>(); 
        
        // создаем коллекцию элементов для первой группы
        childDataItem = new ArrayList<Map<String, String>>();
        // заполняем список аттрибутов для каждого элемента
        for (String item : items1) {
          m = new HashMap<String, String>();
            m.put("itemName", item); // название 
            childDataItem.add(m);  
        }
        // добавляем в коллекцию коллекций
        childData.add(childDataItem);

        // создаем коллекцию элементов для второй группы        
        childDataItem = new ArrayList<Map<String, String>>();
        for (String item : items2) {
          m = new HashMap<String, String>();
            m.put("itemName", item);
            childDataItem.add(m);  
        }
        childData.add(childDataItem);

        // список аттрибутов элементов для чтения
        String childFrom[] = new String[] {"itemName"};
        // список ID view-элементов, в которые будет помещены аттрибуты элементов
        int childTo[] = new int[] {android.R.id.text1};
        
        final SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
            mContext,
            groupData,
            android.R.layout.simple_expandable_list_item_1,
            groupFrom,
            groupTo,
            childData,
            android.R.layout.simple_list_item_1,
            childFrom,
            childTo);
            
        expandableListOfURLs = (ExpandableListView) findViewById(R.id.expandableListOfURLs);
        expandableListOfURLs.setAdapter(adapter);
        expandableListOfURLs.setOnChildClickListener(new OnChildClickListener(){
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				String s = ((Map<String,String>)(adapter.getChild(groupPosition, childPosition))).get("itemName");
				    
				URL.setText(s);
				return false;
			}});
        
        
    }		
		

}

