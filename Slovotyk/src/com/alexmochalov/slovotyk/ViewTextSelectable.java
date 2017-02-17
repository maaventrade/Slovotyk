package com.alexmochalov.slovotyk;
  
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Pattern;

import com.alexmochalov.bmk.Bookmark;
import com.alexmochalov.dic.Dictionary;
import com.alexmochalov.dic.Entry;
import com.alexmochalov.io.AsynkDivider;
import com.alexmochalov.io.AsynkLoader;
import com.alexmochalov.io.FileSaver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;

import com.alexmochalov.dic.*;

/**
 * 
 * @author d920maal
 *
 */
public final class ViewTextSelectable extends TextView {
	//OnEventListener listener;

	// Copies:  
	// Copy of the loaded text.
	private ArrayList<String> strings;
	// Main activity 
	private Context context;
	private ViewTextSelectable thisContext;
	
	// Visual elements:
	// Seek bar for text scrolling 
	private SeekBarVertical  seekBarVertical;
	// Shows in process of loading the text and dividing the strings
	private ProgressDialog progressDialog;
	// Colors and stiles
	private  Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
	private  Paint paintMarkBG = new Paint(Paint.ANTI_ALIAS_FLAG);
	// Width of the screen
	private int width;
	// Height of strings = paint.getTextSize() 
	private int heightOfString;
	// Default text height 
	//private float textSize = 36;
	// Picture to draw the bookmarks
	private Bitmap bitmapBookMark;
	private Rect rectBitmapBookMark;
	
	
	// Current text shifts: 
	// First visible line
	private int firstLine;  
	// Shift of the first visible line in pixels
	private int firstLinePixelShift; 
	// Maximal shift of the first visible line in pixels
	private int firstLinePixelShiftMax; 
	
	// Gaps are moved to Utils
	//private int XGAP = 5; // left and right 
	//private int PARAG = 25; // 
	// Width of the symbol blank was used when strings was aligned to the screen width  
	//private float widthOfBlank = 0;
	//private int heightOfTopBorderMax = 0; 
	//private int heightOfTopBorder = 0; 
	
	// Last touch event coordinates
	private int yDn;
	private int xDn;
	// Last touch or move event coordinate
	private int yDnMv;
	
	// Text sliding:
	// If text slides then true
	private boolean isSlided = false; 
	private Handler handler = new Handler(); 
	private int sladeY = 0; // 
	private long eventTime;

	// If text id loaded od divided then true
	//private boolean parsing = false;

	// Current selection:
	// Coordinates
	private int selectionLine = -1;
	private int selectionStart = -1;
	private int selectionEnd = -1;
	// and current selected text
	private String text = "";

	/**
	 * Removes symbols of the Selecting or Bookmarks
	 * from all strings. 
	 * @param par 0 - delete selections
	 * 			  1 - delete bookmarks
	 */
	public void clearMarks(int par)
	{
		for (String string: strings){
			String s = "";
			for (int i = 0; i < string.length(); i++)
				if (par == 0){
					if (string.charAt(i) != Utils.CHAR_SEL_START &&
						string.charAt(i) != Utils.CHAR_SEL_END)
						s = s + string.charAt(i);
				} else 
					if (string.charAt(i) != Utils.CHAR_BM)
						s = s + string.charAt(i);
			strings.set(strings.indexOf(string), s);
		}
		// Sets the flag that text is modified  
		Utils.modified = true;
		invalidate();
		Toast.makeText(context, "Ok", Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * Finds all strings with bookmarks.
	 * @return ArrayList of bookmarks (text and number of the string)
	 */
	public ArrayList<Bookmark> getBookmarks(){
		ArrayList<Bookmark> bookmarks = new ArrayList<Bookmark>();
		for (String string: strings)
			for (int i = 0; i < string.length(); i++)
				if (string.charAt(i) == Utils.CHAR_BM )
					bookmarks.add(new Bookmark(string,
						strings.indexOf(string)));
			
		return bookmarks;
	}
	
	/*
	public interface OnEventListener{
		public void onSlide(int firstLine);
	}
	
	public void setEventListener(
			OnEventListener onEventListener) {
		listener = onEventListener;
	}
	*/	
	
	/*
	@Override 
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
	   int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
	   int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
	   super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	*/
	
	/**
	 * Sets parameters of the Paint objects and depended parameters
	 */
	public void setPaints(int textSize) {

		paint.setTextSize( textSize );
		paint.setStrokeWidth(1);
		
		heightOfString = (int) paint.getTextSize()+((int)paint.getTextSize()>>1);

		firstLinePixelShiftMax = heightOfString+heightOfString/2;
		firstLinePixelShift = firstLinePixelShiftMax;
	}
	
	/**
	 * 
	 * @param context
	 * @param lexicon
	 * @param strings
	 * @param seekBarVertical
	 */
	void setParams(Context context, ArrayList<String> strings, SeekBarVertical seekBarVertical, int textSize) {
		this.context = context;
		this.strings = strings;
		this.seekBarVertical = seekBarVertical;

		seekBarVertical.listener = new SeekBarVertical.OnCustomEventListener(){
			@Override
			public void onChanged(int progress)
			{
				// Scrolls text when seekBar changes. 
				firstLine = progress;
				invalidate();
			}
		};
		// Sets parameters of the seekbar;
		seekBarVertical.setMax(strings.size());
		seekBarVertical.setProgress(firstLine);
		
		// Load the picture for the Bookmarks
		Resources res = getResources();
		bitmapBookMark = BitmapFactory.decodeResource(res, R.drawable.bookmark1);
		rectBitmapBookMark = new Rect(0,0,bitmapBookMark.getWidth(), bitmapBookMark.getHeight());
		
		setPaints(textSize);
	}
	
	public ViewTextSelectable(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		
	}

	public ViewTextSelectable(Context context, AttributeSet attrs) {
		super(context, attrs);
		
	}

	public ViewTextSelectable(Context context) {
		super(context);
		
	}

	public double getDistance(int x0, int y0, int x1, int y1) {
		return Math.sqrt((x0-x1)*(x0-x1)+(y0-y1)*(y0-y1));
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (strings.size() == 0) 
			return true;
		
		int action = event.getAction();
		int Y = (int) event.getY();
		int X = (int) event.getX();
Log.d("","X = "+event.getRawX());
		sladeY = 0;
		
        if (action == MotionEvent.ACTION_DOWN) {
        	clearTempSelection();
        	tempSelectiom(X, Y);
        	yDnMv = Y;
        	
        	yDn = Y;
        	xDn = X;
        	
			eventTime = event.getEventTime();
        	
			isSlided = true;
        	invalidate();
        	return true;
        } else if (action == MotionEvent.ACTION_MOVE) {
        	if (selectionLine >= 0){
        		if (Math.abs(Y-yDn) > Math.abs(X-xDn))
					seekBarVertical.setVisibility(View.VISIBLE);
        		else
					seekBarVertical.setVisibility(View.INVISIBLE);
        	}
        	
        	if (isSlided = true){
        		setFirstVisible(Y - yDnMv);
        		yDnMv = Y;
            	invalidate();
        	}
			seekBarVertical.setProgress(firstLine);
        	return true;
        } else if (action == MotionEvent.ACTION_UP) {
        	if (selectionLine >= 0){
        		if (Math.abs(Y-yDn) <= Math.abs(X-xDn)){
        			int line = selection();
					
        			IndexEntry indexEntry = Dictionary.find(text);
					String translation = "";
					String sentence = "";
					if (indexEntry != null){
						translation = Dictionary.readTranslation(indexEntry);
						sentence = getSentence(line);
						if (!text.equals(indexEntry.getText()))
							text = text + " (" + indexEntry.getText() + ")";
					} 
					
					if (Utils.instant_translation){
						DialogEntry dialodEntry = new DialogEntry(context,
																  new Entry(text, translation, sentence));
						dialodEntry.show();

						clearTempSelection();
						tempSelectiom(X, Y);
					} else {
						Entry entry = Lexicon.addEntry(text, 
													   translation, 
													   sentence);
						if (translation.length() > 0){
							String[] nextTwoWords = nextTwoWords(line);
							Lexicon.addEntries(entry, text, translation, sentence, nextTwoWords); 
						}

						Utils.modified = true;
						
					}
					
        		} else
                	clearTempSelection();
        			
        		//selectionLine = -1;
        	}
        	
        	isSlided = false;

        	//if (X == xTouch && Y == y1 && context.state == State.read){
	        //	callback.callbackCall("ShowSlideBar");
        	//}
        	
		    long eventTime1 = event.getEventTime(); 
            if (yDn > Y) sladeY = -Math.round((yDn - Y)/(eventTime1-eventTime)*50);
            else if (yDn < Y) sladeY = -Math.round((yDn - Y)/(eventTime1-eventTime)*50);
            else yDn = 0;
            
            if (sladeY !=0){
            	//Log.d("--", "sladeY "+sladeY);
            	handler.postDelayed(updateTimeTask, 10);
            }	
        	
        	invalidate();
        	return true;
        }
        
		return true;
	}
	
	private void setFirstVisible(int delta) {
		if (firstLine == 0 && firstLinePixelShift == firstLinePixelShiftMax){
			/*int htb = heightOfTopBorder;
			
			heightOfTopBorder = heightOfTopBorder+delta;
			heightOfTopBorder = Math.min(heightOfTopBorder, heightOfTopBorderMax);
			heightOfTopBorder = Math.max(heightOfTopBorder, 0);
				
			if (heightOfTopBorder > 0)
				delta = 0;
			*/
		}
				
    	firstLinePixelShift = firstLinePixelShift + delta;
    	if (firstLinePixelShift > firstLinePixelShiftMax) {
    		if (firstLine > 0){
    			firstLine--;
        		firstLinePixelShift = 0;
    		}
    		else firstLinePixelShift = firstLinePixelShiftMax;
    			
    	} else if (firstLinePixelShift < 0){
    		firstLinePixelShift = firstLinePixelShiftMax;
    		if (firstLine < strings.size()) firstLine++;
    	}
    	
		if (firstLine >= strings.size()-1) {
			firstLine = Math.max(strings.size()-1, 0);
    		firstLinePixelShift = firstLinePixelShiftMax;
		}
    	
    	//if(listener!=null) listener.onSlide(firstLine);
	}


	private void slade() {
		if (sladeY > 0) sladeY--;
		else if (sladeY < 0) sladeY++;
		setFirstVisible(sladeY);
		this.invalidate();
		seekBarVertical.setProgress(firstLine);
	}
	
    private Runnable updateTimeTask = new Runnable() { 
		   public void run() { 
			   slade();
			   handler.postDelayed(this, 10);
		       if (sladeY ==0)
			   	   handler.removeCallbacks(updateTimeTask); 
		   } 
		};        
	
	MyCallbackIv callback = null;
		
	interface MyCallbackIv {
		void callbackCall(String action); 
	} 

	private void tempSelectiom(int X, int Y){
		int line = (Y - firstLinePixelShift + heightOfString + heightOfString/4 + firstLinePixelShift/4)/(firstLinePixelShiftMax) + firstLine;

		line = Math.max(line, 0);
		line = Math.min(line, strings.size()-1);
		
		// Find a position of touching in the string
		String s = strings.get(line);
		
		float x = Utils.XGAP;
		if (s.charAt(0) == '\t')
			x = Utils.PARAG;
		x = Utils.XGAP;
		
		// Find the touched letter 
		int center = -1;
		for (int index = 0; index < s.length(); index++){
			char c = s.charAt(index);
			if (c < 20){ // control code
				if (c == Utils.CHAR_BOLD)
					paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
				else if (c == Utils.CHAR_BOLD_END)
					paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
				///////////////////////////////////////////////////////
				if (c == Utils.CHAR_ITALIC)
					paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
				else if (c == Utils.CHAR_ITALIC_END)
					paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
				//////////// TO DO BOLD + ITALIC
				
			} else {
				x = x + paint.measureText(""+c);
				if (x >= X && center == -1){
					center = index-1;
					break;
				}	
			}
		}
		
		if (center >= 0){
			if (isAlphabetic(s.charAt(center))){
				Utils.setInformation(">"+s.charAt(center)+"<");
				
				int alreadySelected = 0;
				int start = center;
				int end = center;
				boolean foundStart = false;
				// find position of the first character in the word
				for (int index = center-1; index >= 0; index--)
					if (!isAlphabetic(s.charAt(index))){
						foundStart = true;
						//int n1 = s.charAt(index);
						//int n2 = Utils.CHAR_SEL_START;
						if (s.charAt(index) == Utils.CHAR_SEL_START){
							alreadySelected++;
							start = index;
						} else
							start = index + 1;
						break;
					}
				if (! foundStart)
					start = 0;

				boolean foundEnd = false;
				for (int index = center; index < s.length(); index++)
					if (!isAlphabetic(s.charAt(index))){
						if (s.charAt(index) == Utils.CHAR_SEL_END){
							alreadySelected++;
							end = index-1;
						} else
							end = index;
						foundEnd = true;
						break;
					}
				if (!foundEnd)
					end = s.length();

				selectionLine = line;
				selectionStart = start;
				selectionEnd = end;
				
				if (alreadySelected == 2){
					clearTempSelection();
					return;
				}
				
				s = new StringBuilder(s).insert(end, Utils.CHAR_TMP_SEL_END).toString();
				s = new StringBuilder(s).insert(start, Utils.CHAR_TMP_SEL_START).toString();
				
				strings.set(line, s); 
				
				
				//Log.d("", "*"+s.substring(start,end)+"*");
			}
		}
		invalidate();
	}
	
	private void clearTempSelection(){
		if (selectionLine >= 0){
			String s = strings.get(selectionLine);
			
			Lexicon.deleteEntry(s.substring(selectionStart+1, selectionEnd+1)); 
			
			s = new StringBuilder(s).deleteCharAt(selectionEnd+1).toString();
			s = new StringBuilder(s).deleteCharAt(selectionStart).toString();
			strings.set(selectionLine, s);
			selectionLine = -1;
			invalidate();
		}
	}
	

	private int selection(){
		int result = 0;
		if (selectionLine >= 0){
			String s = strings.get(selectionLine);
			s = new StringBuilder(s).replace(selectionEnd+1, selectionEnd+2, ""+Utils.CHAR_SEL_END).toString();
			s = new StringBuilder(s).replace(selectionStart, selectionStart+1, ""+Utils.CHAR_SEL_START).toString();
			text = s.substring(selectionStart+1,selectionEnd+1);
			Utils.setInformation("*"+text+"*");
			strings.set(selectionLine, s);
			result = selectionLine;
			selectionLine = -1;
			invalidate();
		}
		return result;
	}
	
	private boolean isAlphabetic(char charAt) {
		return Character.isLetter(charAt);
		
		//Pattern p = Pattern.compile("^[a-zA-Z]");
		//return p.matcher(""+charAt).find();		
	}

	// Count of pixels  
	private float getTextWidth(Paint paint, String S, int pos, float addToBlank){
	//	paint.getTextBounds(S.string, 0, mark.pos+1, bounds);
		float width = 0;
		
		float[] widths = new float[pos]; 
		paint.getTextWidths(S, 0, pos, widths);
		
		for (int i=0; i<pos; i++){
			width = width + widths[i];
			if (S.charAt(i) == ' ')
				width = (int)(width + addToBlank);
		}
		
		return width;
	}
	

	public void divideStrings(ArrayList<String> strs) {
		//for (int i = 0; i < 15; i++)
		//	Utils.setInformation(""+strings.get(i));
	}
	
	private int selSymbolColor(int bgColor, char c, ArrayList<RectF> boormarkRects, float x, float y){
		if (c == Utils.CHAR_BOLD)
			paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
		else if (c == Utils.CHAR_BOLD_END)
			paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
		else if (c == Utils.CHAR_ITALIC)
			paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.ITALIC));
		else if (c == Utils.CHAR_ITALIC_END)
			paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
		else if (c == Utils.CHAR_TMP_SEL_START)
			bgColor = Color.YELLOW;
		else if (c == Utils.CHAR_TMP_SEL_END)
			bgColor = Color.WHITE;
		else if (c == Utils.CHAR_SEL_START)
			bgColor = Color.GREEN;
		else if (c == Utils.CHAR_SEL_END)
			bgColor = Color.WHITE;
		else if (c == Utils.CHAR_BM)
			boormarkRects.add(new RectF(x, y-60, x + 40, y - 20));
		return bgColor; 
	}
	
	
	@Override
	protected void onDraw (Canvas canvas){
		if (isInEditMode()) {
			return;
		}

		if (strings.size() == 0) return;

		////////////////////////////////////////////////
		paint.setColor(Color.WHITE);
		canvas.drawColor(paint.getColor());

		////////////////////////////////////////////////
		
		paint.setColor(Color.BLACK);
		int y = firstLinePixelShift;
		float x = Utils.PARAG;

		// Draw backgound for selected text
		//drawSelectedBackground(firstLine, canvas, y);
									
		for (int i = firstLine; i< strings.size(); i++){
			
			paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
			String s = strings.get(i);
			if (s.length() == 0)
				continue;
			
			int index = 0;
			if (s.charAt(0) == '\t'){
				x = Utils.PARAG;
				index++;
			}
			else
				x = Utils.XGAP;
			
			int bgColor = Color.WHITE;
			ArrayList<RectF> boormarkRects = new ArrayList<RectF>(); 
			int start = 0;
			int state = 0;
			while (index < s.length()){
				char c = s.charAt(index);
				if (state == 0){ // state is "symbols"
					if (c < 20){ // control code
						state = 1; // set state to "control code"
						if (start != index){ // if we have something to draw then do it
							if (bgColor != Color.WHITE){
								paintMarkBG.setColor(bgColor);
								canvas.drawRect(new RectF(x, y-heightOfString, x + paint.measureText(s.substring(start, index)), y + heightOfString/2), paintMarkBG);
							}
							canvas.drawText(s.substring(start, index), x, y, paint);
							x = x + paint.measureText(s.substring(start, index));
						}
						bgColor = selSymbolColor(bgColor, c, boormarkRects, x , y);
						start = index+1;
					} 
				} else { // state is "control code"
					if (c < 20){ // control code
						bgColor = selSymbolColor(bgColor, c, boormarkRects, x , y);
						start = index+1;
					} else {
						state = 0; // set state to "symbols"
					}
				}
				index++;
			}

			if (start != index){
				// the rest of the text to draw
				if (bgColor != Color.WHITE){
					paintMarkBG.setColor(bgColor);
					canvas.drawRect(new RectF(x, y-heightOfString, x + paint.measureText(s.substring(start, index)), y + heightOfString/2), paintMarkBG);
				}
				canvas.drawText(s.substring(start, index), x, y, paint);
			}
			
			for (RectF r: boormarkRects)
				canvas.drawBitmap(bitmapBookMark, rectBitmapBookMark, r, paint);
			
			y = y + firstLinePixelShiftMax;
			
			if (y > getHeight()+heightOfString)
				break;
			
		}
		
		paint.setColor(Color.GRAY);
		canvas.drawRect(0,y,getWidth(),getHeight(), paint);
		
		if (Utils.getTextLoading()){
			canvas.drawText(Utils.getTextLoadingStr(), 100, 100, paint);
		}
			
	}

	
	public void setFirstLine(int progress, int firstLinePixelShift) {
		firstLine = progress;
		if( firstLinePixelShift >= 999)
			this.firstLinePixelShift = firstLinePixelShiftMax;
		else this.firstLinePixelShift = firstLinePixelShift;
		
		if( firstLinePixelShift != 999)
			seekBarVertical.setProgress(progress);

		invalidate();
	}

	public void loadFile(int width, String fileName, boolean fromRresource) {
		this.width = width;
		final ArrayList<String> strs = new ArrayList<String>(); 

		AsynkLoader asynkLoader = new AsynkLoader(context);
		asynkLoader.eventCallback = new AsynkLoader.EventCallback(){
			@Override
			public void loadingFinishedCallBack() {
				
				divideFile(strs);
			}};
		asynkLoader.start(context, Utils.fileName, strs, fromRresource);
	}	

	public void loadURL(int width, String fileName, String name) {
		this.width = width;
		final ArrayList<String> strs = new ArrayList<String>(); 

		AsynkLoader asynkLoader = new AsynkLoader(context);
		asynkLoader.eventCallback = new AsynkLoader.EventCallback(){
			@Override
			public void loadingFinishedCallBack() {
				divideFile(strs);
			}};
		asynkLoader.startURL(context, fileName, name, strs);
	}	

	private void divideFile(ArrayList<String> strs) {
		thisContext = this;
		AsynkDivider asynkDivider = new AsynkDivider();
		asynkDivider.eventCallback = new AsynkDivider.EventCallback(){
			@Override
			public void dividingFinishedCallBack() {
				seekBarVertical.setMax(strings.size());
				Utils.restoreViewParams(thisContext);
				if (firstLine >= 0)
					seekBarVertical.setProgress(firstLine);
				invalidate();
			}};
			asynkDivider.start(strs, strings, width, paint, context);
	}
	
	public void progressDialogHide() {
		if (progressDialog != null){
			progressDialog.hide();
			progressDialog.dismiss();
		}
	}	
	
	public void saveFile(String name) {
	   	 //RESULTS_FOLDER
		 FileSaver fileSaver = new FileSaver(context);
		 fileSaver.saveText(name, Utils.APP_FOLDER, strings);
		 
		 name = Utils.APP_FOLDER+"/"+name;
		 Log.d("",name);;
		 Log.d("",Utils.fileName);
		 /*
		 if (! name.equals(Utils.fileName)){
			Utils.fileName = name;
			loadFile(this.width, name, false);
		 }
		 */
		 
	}	
	
	private String getSentence(int line)
	{
		String z = strings.get(line);

		// Find the left part of the string 
		boolean found = false;
		int i = z.indexOf(text);
		while (i >=0 )
			if (isSeparatorSent(z.charAt(i--))){
				found = true;
				break;
			}
		if (!found){
			for (int k = line-1; k >= 0; k--){
				String z0 = strings.get(k);
				found = false;
				i = z0.length()-1;
				while (i >=0 )
					if (isSeparatorSent(z0.charAt(i--))){
						found = true;
						break;
					}
				if (!found)
					z = z0.trim()+ " " + z;
				else {
					z = z0.substring(i+2).trim()+" "+ z;
					break;
				}
			}
		} else
			z = z.substring(i+2);
		
		// Find the right part of the string 
		found = false;
		i = z.indexOf(text)+text.length()-1;
		while (i < z.length() )
			if (isSeparatorSent(z.charAt(i++))){
				found = true;
				break;
			}
		if (!found){
			for (int k = line+1; k < strings.size(); k++){
				String z0 = strings.get(k);
				found = false;
				i = 0;
				while (i < z0.length() )
					if (isSeparatorSent(z0.charAt(i++))){
						found = true;
						break;
					}
				if (!found)
					z = z.trim()+ " " + z0;
				else {
					z = z.trim()+ " " + z0.substring(0,i);
					break;
				}
				
			}
		} else
			z = z.substring(0, i);
		z = z.replace(text,"<b>"+text+"</b>");
		
		return z;
	}
	/*
	private  getTranslation(String text)
	{
		return Dictionary.find(text);
	}
	*/

	public int getFirstLine() {
		return firstLine;
	}

	private boolean isSeparator(char charAt) {
		Pattern p = Pattern.compile(";,.!?");

		return ";,.!?".contains(""+charAt);		
	}

	private boolean isSeparatorSent(char charAt) {
		Pattern p = Pattern.compile(";,.!?");

		return ";.!?:()".contains(""+charAt);		
	}

	
	public String getFirstText() {
		if (firstLine >= 0 && firstLine < strings.size())
			return strings.get(firstLine); 
		else return "";
	}	

	private int findCharacter(String s){
		for (int i = 0; i<s.length()-1; i++)
			if (s.charAt(i) != Utils.CHAR_TAB)
			//if (s.charAt(i) > 20)
				return i;
		return -1;
	}
	
	
	public void addDelBookmark() {
		if (strings.size() == 0){
			Toast.makeText(context, context.getString(R.string.warning_load_text), Toast.LENGTH_LONG).show();
			return;
		}
		if (firstLine >= 0){
			String s = strings.get(firstLine);
			
			if (s.contains(""+Utils.CHAR_BM)) strings.set(firstLine, s.replace(""+Utils.CHAR_BM, ""));
			else {
				int pos = findCharacter(s);
				strings.set(firstLine, new StringBuilder(s).insert(pos, Utils.CHAR_BM).toString()); 
			}// !!!!!! error
		}
		invalidate();
	}
	
	public int getFirstLinePixelShift(){
		return firstLinePixelShift;
	}

	private String[] nextTwoWords(int line) {
		String[] result = {"", ""};

		int state = 0;
		for (int k = line; k < Math.min(line+2, strings.size()); k++ ){
			String string = strings.get(k);			
			int start = 0;
			for (int i = selectionEnd+1; i < string.length(); i++){
				char c = string.charAt(i);
				if (c > 20){
					switch (state) {
					case 0:
						if (isSeparatorSent(c) || c == ' ' ){
						}
						else if (Character.isLetter(c)){
							start = i;
							state = 1;
						}
						break;
					case 1:
						if (isSeparatorSent(c) || c == ' ' ){
							result[0] = string.substring(start, i).trim();
							state = 2;
						}
						break;
					case 2:
						if (isSeparatorSent(c) || c == ' ' ){
						}
						else if (Character.isLetter(c)){
							start = i;
							state = 3;
						}
						break;
					case 3:
						if (isSeparatorSent(c) || c == ' ' ){
							result[1] = string.substring(start, i).trim();
							return result;
						}
						break;
					}
				}
			}
		}
		
		return result;
	}

	public void loadString(int width, String sharedText) {
		this.width = width;
		
		String lines[] = sharedText.split("\\r?\\n");
		final ArrayList<String> strs = new ArrayList<String>(Arrays.asList(lines));
		
		divideFile(strs);
	}

	
}
