package com.alexmochalov.main;

import android.annotation.*;
import android.app.*;
import android.content.*;
import android.content.SharedPreferences.*;
import android.content.res.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.preference.*;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.support.v4.view.*;
import android.text.*;
import android.util.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import android.widget.RadioGroup.*;

import com.alexmochalov.bmk.*;
import com.alexmochalov.dic.*;
import com.alexmochalov.files.*;
import com.alexmochalov.infoPager.DialogInformation;
import com.alexmochalov.infoPager.SamplePagerAdapter;
import com.alexmochalov.lexicon.DialogLexicon;
import com.alexmochalov.lexicon.Lexicon;
import com.alexmochalov.slovotyk.R;
import com.alexmochalov.url.*;
import com.alexmochalov.url.DialogURL.*;
import com.alexmochalov.viewText.SeekBarVertical;
import com.alexmochalov.viewText.ViewTextSelectable;

import java.io.*;
import java.util.*;

import android.view.View.OnClickListener;

import com.alexmochalov.dic.Dictionary;
/**
 * 
 * @author @Alexey Mochalov
 * Приложение Slovotyk предназначено для изучения иностранных языков
 * Вы открываете в приложении текст на иностранном языке и читаете его, 
 * помечая незнакомые слова (проводя горизонтально по экрану пальцем).
 * Программа переводит эти слова. Их список вместе с переводом можно просмотреть на экране Списка слов.
 * Для упрощения работы с текстом текущий отрывок можно выделить закладками.
 * Приложение имеет встроенный словарь В.К. Мюллера. 
 * Также можно загрузить словари в формате xdxf. 
 * 
 */
public class MainActivity extends Activity  implements OnInitListener
{    
	// Сохраняемые параметры приложения
	SharedPreferences prefs;
	static final String PREFS_FILE_NAME = "PREFS_FILE_NAME";
	static final String PREFS_FIRST_LINE = "PREFS_FIRST_LINE";
	static final String PREFS_DICT_NAME = "PREFS_DICT_NAME";
	static final String PREFS_INDX_NAME = "PREFS_INDX_NAME";
	static final String INTERNAL_DICT = "INT_DICT";
	static final String INIT_PATH = "INIT_PATH";
	static final String INSTANT_TRANSLATION = "INSTANT_TRANSLATION";

	ViewTextSelectable viewTextSelectable;
	EntryEditor entryEditor = new EntryEditor(); 
	
	ArrayList<String> strings = new ArrayList<String>();
	
	String initPath = Utils.EXTERNAL_STORAGE_DIRECTORY;
	static final String FILE_EXT[] = {".txt",".xml",".htm",".html",".fb2"};
	
	 Menu optionsMenu;
	
	String dictionary_name;
	//String index_file_name;

	private int MY_DATA_CHECK_CODE = 90;
	
	
	private MenuItem item_instant;
	 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);

        // Настроить вид ActionBar
        Utils.setActionBar(this);
		
		
        /*
    	int upId = Resources.getSystem().getIdentifier("action_bar_title", "id", "android");
    	if (upId > 0) {
    	    TextView title = (TextView) findViewById(upId);
    	    title.setSingleLine(false);
    	    title.setMaxLines(2);
    	    title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
    	}
    	*/
    	
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		// Настройка объекта Lexicon
        /*
		Lexicon.mCallback = new Lexicon.EventCallback(){

			
			@Override
			public void itemSelected(Entry entry) {
				// При выборе элемента списка переходим на страницу словаря 
				selectPage(2);
				prevPageIndex = 1;
				entryEditor.start(entry);
				}};
		*/
				
		Lexicon.setParams(this);
				
		selectPage(0);
		
		Utils.setViewInformation();
		
		prefs =  PreferenceManager.getDefaultSharedPreferences(this);

		
		
		Utils.setInternalDictionary(prefs.getString(INTERNAL_DICT, "eng_ru.xdxf"));

		dictionary_name = prefs.getString(PREFS_DICT_NAME, "eng_ru.xdxf");
		if (dictionary_name.length() == 0)
			dictionary_name = "eng_ru.xdxf";
		
		
		initPath = prefs.getString(INIT_PATH, Utils.EXTERNAL_STORAGE_DIRECTORY);
		//Log.d("z","dictionary_name "+dictionary_name);
		Utils.instant_translation = prefs.getBoolean(INSTANT_TRANSLATION, false);
		
		// Пока только один словарь
		//dictionary_name = "eng_ru.xdxf"; 
		//index_file_name = Utils.APP_FOLDER+"/eng_ru.xdxf";
		
		Intent intent = getIntent();
	    String action = intent.getAction();
	    String type = intent.getType();
		
	    if (Intent.ACTION_SEND.equals(action) && type != null) {
	    	String sharedText;
	        if ("text/html".equals(type)) 
	        	sharedText = intent.getStringExtra(Intent.EXTRA_HTML_TEXT);
	        else
	        	sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
	        	
	        Log.d("", sharedText);
	        
			Utils.fileName = Utils.getAppFolder()+"new file.txt";
				loadString(Utils.fileName, sharedText);
				
	    } else {
			Utils.fileName = prefs.getString(PREFS_FILE_NAME, "");
			
			
			if (Utils.fileName.length() > 0){
				// Start loading the text 
				loadFile(Utils.fileName, false);
				//if (Utils.info.length() > 0)
				//	Toast.makeText(this, Utils.info, Toast.LENGTH_LONG).show();
				
				Utils.saveViewParams(prefs.getInt(PREFS_FIRST_LINE, 0), 999);
			} else{
				showInformation();	
			}	
	    }
		
	    Lexicon.load();
	    
		Dictionary.eventCallback = new Dictionary.EventCallback(){
			@Override
			public void loadingFinishedCallBack(boolean result)
			{
				
				if (result){
					dictionary_name = Utils.getDictionaryFileName();
					
					entryEditor.reset();
					
					Utils.setActionbarTitle(Utils.getaLanguage(),
												 Utils.extractFileName(), false);
				} else {
					// If Index file not found show the message
					queryReindex();
				}
			}

			@Override
			public void indexingFinishedCallBack(String dictionary_name) {
				Dictionary.load(dictionary_name);
			}
		};
		
		Log.d("a","dictionary_name "+dictionary_name);
		
		Dictionary.setParams(this);
		Dictionary.load(dictionary_name);
		

		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
		
    }
    
    protected void queryReindex() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this); 

        alert.setTitle(getResources().getString(R.string.index_not_found)); 
        alert.setMessage(R.string.query_index_dictionary); 

        AlertDialog dlg = alert.create();

        alert.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() { 
        public void onClick(DialogInterface dialog, int whichButton) {
			Dictionary.createIndexAsinc(dictionary_name);
        } 
        }); 

        alert.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() { 
          public void onClick(DialogInterface dialog, int whichButton) { 
          } 
        }); 

        alert.show(); 
	}

@Override
	public void onInit(int status) {
		if (status == TextToSpeech.SUCCESS) {
			
			TtsUtils.init(getApplicationContext());
			

		}
		else if (status == TextToSpeech.ERROR) {
			Toast.makeText(this, "Error occurred while initializing Text-To-Speech engine", Toast.LENGTH_LONG).show();
		}
	}
	

	@Override 
	protected void onDestroy() {
		TtsUtils.destroy();
		
		//saveParameters();
		super.onDestroy(); 
	}
	
	
    /**
     * 
     * @param page - номер страницы, которую надо выбрать 
     */
	 void selectPage(int page){

			getActionBar().show();
			setContentView(R.layout.activity_main);
	    	int upId = Resources.getSystem().getIdentifier("up", "id", "android");
	    	if (upId > 0) {
	    	    ImageView up = (ImageView) findViewById(upId);
	    	   // up.setImageResource(R.drawable.logo);
	    	}    	
			Utils.setActionbarTitle(Utils.getaLanguage(),
				Utils.extractFileName(), false);
			//Utils.setActionbarTitle("");
			
			viewTextSelectable = (ViewTextSelectable)findViewById(R.id.viewTextSelectable);
			
	//		 android:screenOrientation="nosensor" 
	//		 android:configChanges="keyboardHidden|orientation" 
			SeekBarVertical seekBarVertical = (SeekBarVertical)findViewById(R.id.SeekBarVertical);
			//TextView textView = (TextView)findViewById(R.id.textView);
			
			int size = getResources().getDimensionPixelSize(R.dimen.text_size_medium);
			
			viewTextSelectable.setParams(this, strings, seekBarVertical, size, this);
			
			Utils.restoreViewParams(viewTextSelectable);
			
			if (optionsMenu != null){
				optionsMenu.findItem(R.id.action_add_del_bookmark).setVisible(true);
				optionsMenu.findItem(R.id.action_bookmark).setVisible(true);
				optionsMenu.findItem(R.id.action_clear).setVisible(true);
				optionsMenu.findItem(R.id.action_save).setVisible(true);
				
				optionsMenu.findItem(R.id.action_refresh).setVisible(false);
				
				optionsMenu.findItem(R.id.action_select_dictionary).setVisible(false);
				optionsMenu.findItem(R.id.internal_dictionary).setVisible(false);
				
			}
		 
		 /*
		if (pageIndex == 0)
			Utils.saveViewParams(viewTextSelectable);
		else if (pageIndex == 1)
			Utils.saveViewParams();
			 
		pageIndex = page;
		if (page == 0){
			getActionBar().show();
			setContentView(R.layout.main);
	    	int upId = Resources.getSystem().getIdentifier("up", "id", "android");
	    	if (upId > 0) {
	    	    ImageView up = (ImageView) findViewById(upId);
	    	   // up.setImageResource(R.drawable.logo);
	    	}    	
			Utils.setActionbarTitle(Utils.getaLanguage(),
				Utils.extractFileName(), false);
			//Utils.setActionbarTitle("");
			
			viewTextSelectable = (ViewTextSelectable)findViewById(R.id.viewTextSelectable);
			
	//		 android:screenOrientation="nosensor" 
	//		 android:configChanges="keyboardHidden|orientation" 
			SeekBarVertical seekBarVertical = (SeekBarVertical)findViewById(R.id.SeekBarVertical);
			//TextView textView = (TextView)findViewById(R.id.textView);
			
			int size = getResources().getDimensionPixelSize(R.dimen.text_size_medium);
			
			viewTextSelectable.setParams(this, strings, seekBarVertical, size, this);
			
			Utils.restoreViewParams(viewTextSelectable);
			
			if (optionsMenu != null){
				optionsMenu.findItem(R.id.action_add_del_bookmark).setVisible(true);
				optionsMenu.findItem(R.id.action_bookmark).setVisible(true);
				optionsMenu.findItem(R.id.action_clear).setVisible(true);
				optionsMenu.findItem(R.id.action_save).setVisible(true);
				
				optionsMenu.findItem(R.id.action_refresh).setVisible(false);
				
				optionsMenu.findItem(R.id.action_select_dictionary).setVisible(false);
				optionsMenu.findItem(R.id.internal_dictionary).setVisible(false);
				
			}

		} else if (page == 1){
			setContentView(R.layout.dictionary);
			Lexicon.setGUI();
			
			optionsMenu.findItem(R.id.action_add_del_bookmark).setVisible(false);
			optionsMenu.findItem(R.id.action_bookmark).setVisible(false);
			optionsMenu.findItem(R.id.action_clear).setVisible(true);
	
			optionsMenu.findItem(R.id.action_refresh).setVisible(true);

			optionsMenu.findItem(R.id.action_select_dictionary).setVisible(false);
			optionsMenu.findItem(R.id.internal_dictionary).setVisible(false);
			
			Utils.setActionbarTitle(Utils.getaLanguage(),
				Utils.getDictionaryFileName(), true);
			Utils.restoreViewParams();
		} else if (page == 2){
			setContentView(R.layout.dictionary_entry);
			entryEditor.setGUI(this);
			entryEditor.start(null);

			optionsMenu.findItem(R.id.action_add_del_bookmark).setVisible(false);
			optionsMenu.findItem(R.id.action_bookmark).setVisible(false);
			optionsMenu.findItem(R.id.action_save).setVisible(false);
			optionsMenu.findItem(R.id.action_clear).setVisible(false);
			
			optionsMenu.findItem(R.id.action_refresh).setVisible(false);
			
			optionsMenu.findItem(R.id.action_select_dictionary).setVisible(true);
			optionsMenu.findItem(R.id.internal_dictionary).setVisible(true);
			
			Utils.setActionbarTitle(Utils.getaLanguage(),
				Utils.getDictionaryFileName(), true);
		}
		
		*/
    }

     void setMenuItemsVisibility(int min, int max) {
		for (int i = 0; i < optionsMenu.size()-1 ; i++)
		{	
			if (optionsMenu.getItem(i).getOrder() >= min && optionsMenu.getItem(i).getOrder() <= max)
				optionsMenu.getItem(i).setVisible(true);
			else
				optionsMenu.getItem(i).setVisible(false);
    	}
		
	}

	 void setGUIDic(View page){
       
    }

	 public void loadFile(String fileName, boolean fromRresource){
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

		viewTextSelectable.loadFile(displaymetrics.widthPixels, fileName, fromRresource);
		//Utils.setActionbarTitle(Utils.getFileName());
	}

	 void loadURL(String fileName, String name){
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

		viewTextSelectable.loadURL(displaymetrics.widthPixels, fileName, name);
		//Utils.setActionbarTitle(Utils.getFileName());
	}

	 void loadString(String fileName, String sharedText) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);

		viewTextSelectable.loadString(displaymetrics.widthPixels, sharedText);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		optionsMenu = menu;
		
		item_instant = menu.findItem(R.id.action_instant);
		item_instant.setCheckable(true);
		item_instant.setChecked(Utils.instant_translation);
		//setGUI(0);
    	//ab.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0099ff")));
		
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		
		File file = new File(Utils.getAppFolder());
		if(!file.exists()){                          
			file.mkdirs();                  
		}
	}

	@Override 
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int itemId = item.getItemId(); 

		switch (itemId) { 
			case android.R.id.home:
				break;
			case R.id.action_open:
				dialogSaveFile(false, false);
				break;
			case R.id.action_open_url:
				dialogSaveFile(false, true);
				break;
			case R.id.action_save:
				saveFile();
				break;
			case R.id.action_clear:
				LayoutInflater factory = LayoutInflater.from(this);            
		        final View clearView = factory.inflate(R.layout.clear, null);

		        AlertDialog.Builder alert = new AlertDialog.Builder(this); 

		        alert.setTitle(getResources().getString(R.string.action_clear)); 
		        //alert.setMessage("Enter your email and password"); 
		        // Set an EditText view to get user input  
		        alert.setView(clearView); 
		        AlertDialog dlg = alert.create();

		        final CheckBox checkBoxSelections = (CheckBox)clearView.findViewById(R.id.checkBoxSelections);
		        final CheckBox checkBoxBookmarks = (CheckBox)clearView.findViewById(R.id.checkBoxBookmarks);
		        final CheckBox checkBoxLexicon = (CheckBox)clearView.findViewById(R.id.checkBoxLexicon);

		        checkBoxSelections.setChecked(true);
		        checkBoxBookmarks.setChecked(true);
		        checkBoxLexicon.setChecked(true);
		        
		        alert.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() { 
		        public void onClick(DialogInterface dialog, int whichButton) { 
		        	if (checkBoxSelections.isChecked())
						viewTextSelectable.clearMarks(0);
		        	if (checkBoxBookmarks.isChecked())
						viewTextSelectable.clearMarks(1);
		        	if (checkBoxLexicon.isChecked())
						Lexicon.clearEntryes();
		        } 
		        }); 

		        alert.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() { 
		          public void onClick(DialogInterface dialog, int whichButton) { 
		          } 
		        }); 

		        alert.show(); 
				break;
			
			case R.id.action_select_dictionary:
				selectDictionary("");
				break;
			case R.id.action_view_dictionary:
				selectPage(2);
				break;
			//case R.id.action_index_dictionary:
			//	dictionary.createIndexAsinc(Utils.dictionary_name);
			//	break;
			case R.id.action_bookmark:
				dialogBookmarks();
				break;
			case R.id.action_add_del_bookmark:
				viewTextSelectable.addDelBookmark();
				break;
			case R.id.action_refresh: 
				Lexicon.refresh();
				Utils.setModified(true);
				break;
			case R.id.internal_dictionary_en_ru: 
				String fileName = "eng_ru.xdxf";
				String indexFileName = Utils.getAppFolder()+fileName;
				Utils.setInternalDictionary("eng_ru.xdxf");
				// Try to find the Index file
				File file = new File(indexFileName);
				if(!file.exists())
					// If Index file not found create the Index file 
					Dictionary.createIndexAsinc(fileName);
				else
					Dictionary.load(fileName);
				break;
			case R.id.internal_dictionary_sp_en: 
				fileName = "span_eng.xdxf";
				indexFileName = Utils.getAppFolder()+fileName;
				Utils.setInternalDictionary("span_eng.xdxf");
				// Try to find the Index file
				file = new File(indexFileName);
				if(!file.exists())
					// If Index file not found create the Index file 
					Dictionary.createIndexAsinc(fileName);
				else
					Dictionary.load(fileName);
				break;
			case R.id.internal_dictionary_it_ru: 
				dictionary_name = "it_ru.xdxf";
				String index_file_name = Utils.getAppFolder()+dictionary_name;/// replace to .index !!!
				
				Utils.setInternalDictionary("it_ru.xdxf");
				// Try to find the Index file
				file = new File(index_file_name);
				if(!file.exists()){
				// If Index file not found create the Index file 
					//Log.d("s","Start indexing=== "+index_file_name);
					Dictionary.createIndexAsinc(dictionary_name);
					}
				else {
					
					Dictionary.load(dictionary_name);
				}
				break;
			case R.id.action_lexicon:
				DialogLexicon dialogLexicon = new DialogLexicon(this);
				dialogLexicon.show();
				break;
			case R.id.information:
				showInformation();
				break;
			case R.id.action_instant:
				Utils.instant_translation = !Utils.instant_translation;
				item_instant.setChecked(Utils.instant_translation);
				break;
			case R.id.action_information:
				AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
			    dlgAlert.setTitle(getResources().getString(R.string.info));              
				
			    dlgAlert.setMessage(Utils.getFilePath()+"\n"+
					    		Utils.getDictionaryFileNameStr()+"\n"+
		    					Utils.getIndexFileName()+"\n"+
		    					Dictionary.getCount()+" entries."+"\n"+
		    					Dictionary.getDictionaryInfo()
			    		);
			    
			    dlgAlert.setPositiveButton("OK", null);
			    dlgAlert.setCancelable(true);
			    dlgAlert.create().show();
			    break;
		} 
  		return true;
	}

	 private void showInformation() {
		DialogInformation dialogInformation = new DialogInformation(this);
		dialogInformation.listener = new DialogInformation.OnEventListener() {
			@Override
			public void onButtonLoadFile(String fileName, boolean fromRresource) {

				loadFile(fileName, fromRresource);
				
			}
		};
		dialogInformation.show();
	}

	void dialogBookmarks() {
		if (longOperation()) return; 
			
		if (viewTextSelectable.getBookmarks().size() == 0){
			Toast.makeText(this, this.getString(R.string.warning_no_bookmarks), Toast.LENGTH_LONG).show();
			return;
		}
		
		DialogBookmarks dlg = new DialogBookmarks(this, viewTextSelectable.getBookmarks()); 
		dlg.listener = new DialogBookmarks.OnEventListener(){

			@Override
			public void onClick(int firstLine)
			{
				if (firstLine >= 0)
					viewTextSelectable.setFirstLine(firstLine, 1000);
				else
					viewTextSelectable.clearMarks(1);
			}
		};
		dlg.show();
	}
	
	 boolean longOperation() {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * Opens dialog for selection a dictionary file 
	 */
	 void selectDictionary(String message) {
  		Intent intent = new Intent(MainActivity.this, SelectFile.class);
		String file_ext[] = {".xdxf"};
		intent.putExtra("initPath",initPath);
  		intent.putExtra("fileExt", file_ext); 
  		intent.putExtra("addInfo", Utils.getDictionaryFileName()); 
  		intent.putExtra("message", message); 
  		startActivityForResult(intent, 1);
	}
	
	 void dialogClearEntries()
	{
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
	    alertDialog.setTitle(getResources().getString(R.string.action_clear_entries));
	    alertDialog.setMessage(getResources().getString(R.string.clear_entries_query));

	    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					Lexicon.clearEntryes();

				} }); 

	    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					
				}}); 
	    alertDialog.show();
	}

	 void saveFile(){
		String name = Utils.fileName;
		if (name.endsWith(".zip"))
			name = name.substring(0,(name.lastIndexOf(".")));
		
		if (name.length() == 0)
			return;
			
		name = name.substring(0,(name.lastIndexOf(".")))+".xml";
		//if (name.lastIndexOf("/") >= 0)
			//name = name.substring(name.lastIndexOf("/")+1);
		
		if (viewTextSelectable.saveFile(name)){
			 Utils.fileName = name;
			 Utils.setActionbarSubTitle(name);		
			 
			 Editor editor = prefs.edit();
			 editor.putString(PREFS_FILE_NAME, Utils.fileName);
			 editor.commit();
			 
		}
		
		Lexicon.saveFile();
		Utils.setModified(false);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if (requestCode == MY_DATA_CHECK_CODE) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				TtsUtils.newTts(this);
				
			} else {
				Intent installIntent = new Intent();
				installIntent
					.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installIntent);
			}
			return;
		} else
			super.onActivityResult(requestCode, resultCode, data);
		
		if (resultCode==RESULT_OK){
			if (requestCode == 0){
				Utils.fileName = data.getStringExtra("returnedData");
				//if (Utils.fileName.lastIndexOf("/") >= 0)
					//initPath = Utils.fileName.substring(0, Utils.fileName.lastIndexOf("/"));
				
				loadFile(Utils.fileName, false);
			} else if (requestCode == 1){
				// Get a name of the Dictionary file
				String fileName = data.getStringExtra("returnedData");
				String indexFileName = fileName.replace(".xdxf", ".index");
				Utils.setInternalDictionary("");
				// Try to find the Index file
				File file = new File(indexFileName);
				if(!file.exists())
					// If Index file not found create the Index file 
					Dictionary.createIndexAsinc(fileName);
				else
					Dictionary.load(fileName);
			}
		}  
		
	}

	@Override    
	public void onPause() {        
		super.onPause();
		
		viewTextSelectable.progressDialogHide();
		Editor editor = prefs.edit();
		
		//editor.putString(PREFS_FILE_NAME, Utils.fileName);
		editor.putInt(PREFS_FIRST_LINE, viewTextSelectable.getFirstLine());

		editor.putString(PREFS_DICT_NAME, Utils.getDictionaryFileName());
		editor.putString(PREFS_INDX_NAME, Utils.getIndexFileName());
		editor.putString(INIT_PATH, initPath);
		editor.putString(INTERNAL_DICT, Utils.getInternalDictionary());
		
		editor.putBoolean(INSTANT_TRANSLATION, Utils.instant_translation);
		editor.apply();

    }

	 void selectFile(){
  		// Opens intent of file selection
  		Intent intent = new Intent(MainActivity.this, SelectFile.class);
  		intent.putExtra("initPath",initPath);
  		intent.putExtra("fileExt",FILE_EXT); 
  		startActivityForResult(intent, 0);
	}

	 void selectURL(){
		 DialogURL dialogURL = new DialogURL(this);
		 dialogURL.listener = new OnEventListener(){
			@Override
			public void onURLSelected(String url, String name) {
				loadURL(url, name);
			}};
		 dialogURL.show();
	}

	 void dialogSaveFile(final boolean onExit, final boolean fromURL){
		if (!Utils.getModified())
		{
			if (onExit)
				MainActivity.this.finish();
	    	else if (fromURL) 
	    		selectURL();
	    	else 
	    		selectFile();
	    	return;
		}

		AlertDialog alertDialog = new AlertDialog.Builder(this).create();
	    alertDialog.setTitle(getResources().getString(R.string.dialog_save));
	    alertDialog.setMessage(getResources().getString(R.string.dialog_save_message));

	    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					saveFile();
					Toast.makeText(MainActivity.this,"Saved", Toast.LENGTH_LONG).show();
					Utils.setModified( false);
					if (onExit)
						MainActivity.this.finish();
			    	else if (fromURL) 
			    		selectURL();
					else 	
						selectFile();

				} }); 

	    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					if (onExit)
						MainActivity.this.finish();
			    	else if (fromURL) 
			    		selectURL();
					else 
						selectFile();

				}}); 
	    alertDialog.show();
	}

	@Override
	public void onBackPressed() {
		dialogSaveFile(true, false);
	}	 

	
}
