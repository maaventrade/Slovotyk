package com.alexmochalov.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.alexmochalov.dic.Entry;
import com.alexmochalov.main.Utils;
import com.alexmochalov.slovotyk.R;
import com.alexmochalov.slovotyk.R.raw;
import com.alexmochalov.slovotyk.R.string;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import android.app.*;

/**
 * 
 * @author Alexey Mochalov
 * This utility load files  
 *
 */
public class AsynkLoader { 
	// For displaying the progress of the loading
	private ProgressDialog progressDialog;
	// 
	private MyTaskLoading myTaskLoading;

	private Context mContext;
	private Activity mActivity;
	
	// This method is called when procedure finished.
	public EventCallback eventCallback;
	
	// Copy of the string array. Files will be loaded to this array.
	private ArrayList<String> strings;

	// Name of the loading file. 
	private String name;
	private String nameDest;
	
	private boolean loadFromURL = false;
	
	private boolean mFromRresource;
	
	public interface EventCallback { 
		void loadingFinishedCallBack(); 
	}
	
	public AsynkLoader(Context context, Activity activity){
		super();
        mContext = context;
		mActivity = activity;
	}
	
	public void start(Context context, String name, ArrayList<String> strings, boolean fromRresource){
        mContext = context;
        this.name = name;
        this.strings = strings;
        mFromRresource = fromRresource;

        Utils.setTextLoading(true);
		myTaskLoading = new MyTaskLoading();    
		myTaskLoading.execute();
	}
	
	public void startURL(Context context, String name,String nameDest, ArrayList<String> strings){
		progressDialog = new ProgressDialog(context);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setTitle("Loading file");
		progressDialog.setMessage(name);
		progressDialog.setCancelable(false);
		progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
		    @Override
		    public void onClick(DialogInterface dialog, int which) {
		    	myTaskLoading.cancel(true);
		    }
		});
		
		progressDialog.show();

		loadFromURL = true;
		this.nameDest = nameDest;
		start(context, name, strings, false);
	}


	class MyTaskLoading extends AsyncTask<String, String, Void>
	{
		private void showToast(final String message){
			mActivity.runOnUiThread
			(new Runnable() {
                        @Override
                        public void run() {
							final AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(mContext);
							dlgAlert.setMessage(message);
							dlgAlert.setTitle("Error");
							dlgAlert.setPositiveButton("Ok",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
								});
							dlgAlert.create().show();
							
                           // Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                        }
                    });
		}
		
		
		@Override
		protected Void doInBackground(String[] p1)
		{
			Log.d("a", "Name  " + name);
			if (loadFromURL) loadURL(name, nameDest, strings);
			else if (name.endsWith(".txt")) loadTXT(name, strings);
			else if (name.endsWith(".fb2.zip")) loadZIP(name, strings);
			else if (name.endsWith(".fb2") || name.endsWith(".xml")) loadXML(name, strings);
			else if (name.endsWith(".htm") || name.endsWith(".html")) loadHTML(name, strings);
			
			for (int i = strings.size()-1; i >= 0; i--)
				if (strings.get(i).trim().length() == 0)
					strings.remove(i);
			
			return null;
		}

		@Override    
		protected void onCancelled(Void result) {      
			super.onCancelled(result);
			
			if (progressDialog != null){
				progressDialog.hide();
				progressDialog.dismiss();
			}

			Utils.setTextLoading(false);
			Log.d("", "LOADING CANCELLED --- " + strings.size());

		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (progressDialog != null) {
				progressDialog.hide();
				progressDialog.dismiss();
			}

			Utils.setTextLoading(false);

			//Log.d("", "LOADING FINISHED --- " + strings.size());

			if (mFromRresource)
				Utils.fileName = Utils.getAppDirectory() + name;
			else Utils.fileName = name;
			
			Log.d("a", "Utils.fileName  " + Utils.fileName);
			
			if (eventCallback != null)
				eventCallback.loadingFinishedCallBack();
		}

		@Override
		protected void onProgressUpdate(String... values) {
			super.onProgressUpdate(values);
			progressDialog.setMessage(values[0]);
			// progressDialog.setProgress();
		}

		public String removeTag(String data) {
			StringBuilder regex = new StringBuilder(
					"<script[^>]*>(.*?)</script>");
			int flags = Pattern.MULTILINE | Pattern.DOTALL
					| Pattern.CASE_INSENSITIVE;
			Pattern pattern = Pattern.compile(regex.toString(), flags);
			Matcher matcher = pattern.matcher(data);
			return matcher.replaceAll("");
		}

		public void loadURL(String nameSrc, String nameDest,
				ArrayList<String> strings) {
			
			// String nameDest = nameSrc;
			int i = nameDest.lastIndexOf("/");
			if (i >= 0)
				nameDest = nameDest.substring(i + 1);
			i = nameDest.lastIndexOf(".");
			if (i > 0)
				nameDest = nameDest.substring(0, i) + ".xml";
			else
				nameDest = nameDest + ".xml";

			File file = new File(Utils.getAppFolder() + nameDest);
			try {
				if (isCancelled())
					return;

				URL url = new URL(nameSrc);

				this.publishProgress("Open connection...");
				URLConnection urlc = url.openConnection();
				this.publishProgress("Create input stream...");

				InputStreamReader inputStream = new InputStreamReader(
						urlc.getInputStream());

				FileWriter fileWriter = new FileWriter(file);
				BufferedWriter bufferFileWriter = new BufferedWriter(fileWriter);

				// if file the available for reading
				if (inputStream != null) {
					// prepare the file for reading
					BufferedReader buffreader = new BufferedReader(inputStream);

					String line;
					boolean finish = false;
					// read every line of the file into the line-variable, on
					// line at the time
					i = 1;
					this.publishProgress("Read line " + i);
					line = buffreader.readLine();
					while (line != null) {
						if (!line.contains("<img ")
								&& !line.contains("a class=")) {
							line = removeTag(line);
							// line = Html.fromHtml(line).toString();
							line = line.replaceAll("</?meta ?[^>]*>", "");
							line = line.replaceAll("</?font ?[^>]*>", "");
							line = line.replaceAll("</?font ?[^>]*>", "");
							line = line.replaceAll("<link ?[^>]*>", "");
							line = line.replaceAll("<script/script>", "");
							line = line.replaceAll("<!.*>", "");
							line = line.replaceAll("<head>", "");
							line = line.replaceAll("</head>", "");
							line = line.replaceAll("<html>", "");
							line = line.replaceAll("</html>", "");
							line = line.replaceAll("</?title ?[^>]*>", "");
							line = line.replaceAll("</?a ?[^>]*>", "");

							line = line.replaceAll("&amp;", " ");
							line = line.replaceAll("&nbsp;", " ");
							line = line.replaceAll("=white>", "=\"white\">");
							

							line = line.replaceAll("[^\00-\255]", "");

							if (line.contains("(function ("))
								finish = true;
							if (!finish) {
								if (line.trim().length() > 0) {
									// Log.d("", "->"+line);
									if (line.endsWith("</p>"))
										fileWriter.write(line + "\n");
									else
										fileWriter.write(line + " ");
								}

							}
						}
						i++;
						this.publishProgress("Read line " + i);
						// progressDialog.setProgress(i);

						line = buffreader.readLine();
					}

					inputStream.close();
					bufferFileWriter.flush();
					bufferFileWriter.close();
					this.publishProgress("File loaded.");
				}
			} catch (Exception ex) {
				Utils.info = "Error loading " + " " + ex.toString();
				return;
			} finally {
				Log.d("", "FINISH");
			}
			Log.d("", "END");
			loadXML(Utils.getAppFolder() + nameDest, strings);
			name = nameDest;
		}

		public void loadXML(String name, ArrayList<String> strings) {
			strings.clear();
			/*
			 * try { progressDialog.setMax(Utils.countLines(name)); } catch
			 * (IOException e) { progressDialog.setMax(0); }
			 * progressDialog.setProgress(0);
			 */
			try {
				if (isCancelled()) return;
				
				Log.d("a","start "+name);
				BufferedReader reader;
				BufferedReader rd = new BufferedReader(new InputStreamReader(new FileInputStream(name)));
				
				String line = rd.readLine();
				
				rd.close();
				
				if (line.toLowerCase().contains("windows-1251"))
					reader = new BufferedReader(new InputStreamReader(new FileInputStream(name), "windows-1251")); //Cp1252
				else if (line.toLowerCase().contains("utf-8"))
					reader = new BufferedReader(new InputStreamReader(new FileInputStream(name), "UTF-8")); 
				else if (line.toLowerCase().contains("utf-16"))
					reader = new BufferedReader(new InputStreamReader(new FileInputStream(name), "utf-16"));
				else
					reader = new BufferedReader(new InputStreamReader(new FileInputStream(name)));

				XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); 
				factory.setNamespaceAware(true);         
				XmlPullParser parser = factory.newPullParser();
				
				parser.setInput(reader);
				
				boolean title = false;
				boolean section = false;
				boolean body = false;
				boolean a = false;
				boolean aref = false;
				boolean aUp = false;
				boolean aDn = false;
				
				boolean aSel = false;
				boolean noSel = false;
				
				boolean aBm = false;

				boolean bold = false;
				boolean noBold = false;

				boolean italic = false;
				boolean noItalic = false;
				
				boolean aUpStop = false;
				boolean aDnStop = false;
				
				boolean font = false;
				
				int j = 0;
				int eventType = parser.getEventType();         
				while (eventType != XmlPullParser.END_DOCUMENT) {          
					if(eventType == XmlPullParser.START_DOCUMENT) {} 
					else if(eventType == XmlPullParser.START_TAG) {
						if (parser.getName().toLowerCase().equals("sl"));
Log.d("", "START "+parser.getName());
						if(parser.getName() == null) {
							aUp = true;
							aUp = false;
						} else {
							if (parser.getName().toLowerCase().equals("section")) section = true;
							if (parser.getName().toLowerCase().equals("title")) title = true;
							if (parser.getName().toLowerCase().equals("up")){
								aUp = true;
							}
							if (parser.getName().toLowerCase().equals("dn")){
								aDn = true;
							}
							if (parser.getName().toLowerCase().equals("font")){
								font = true;
							}
							if (parser.getName().toLowerCase().equals("sl")){
								aSel = true;
							}
							if (parser.getName().toLowerCase().equals("bm")){
								aBm = true;
							}
							if (parser.getName().toLowerCase().equals("b")|| parser.getName().toLowerCase().equals("strong")) 
								bold = true;
							if (parser.getName().toLowerCase().equals("i")|| parser.getName().toLowerCase().equals("emphasis")) 
								italic = true;
							if (parser.getName().toLowerCase().equals("up_stop")) aUpStop = true;
							if (parser.getName().toLowerCase().equals("dn_stop")) aDnStop = true;
							if (parser.getName().toLowerCase().equals("body")){
								body = true;
								if (parser.getAttributeValue(null, "name") != null)
									if (parser.getAttributeValue(null, "name").equals("notes"))
										body = false;
							}
							if (parser.getName().toLowerCase().equals("a")) a = true;
							} 
						}					
					else if(eventType == XmlPullParser.END_TAG) {
						if (parser.getName().toLowerCase().equals("sl"));
Log.d("", "END "+parser.getName());
						if(parser.getName() == null) {
						} else {
							if (parser.getName().toLowerCase().equals("section")) section = false;
							if (parser.getName().toLowerCase().equals("title")) title = false;
							if (parser.getName().toLowerCase().equals("body")) body = false;
							if (parser.getName().toLowerCase().equals("b") || parser.getName().toLowerCase().equals("strong")) 
								noBold = true;
							if (parser.getName().toLowerCase().equals("i")|| parser.getName().toLowerCase().equals("emphasis")) 
								noItalic = true;
							if (parser.getName().toLowerCase().equals("sl")) 
								noSel = true;
							if (parser.getName().toLowerCase().equals("fv") ){
							};
							if (parser.getName().toLowerCase().equals("a")){
								a = false;
								aref = true;
							}
							if (parser.getName().toLowerCase().equals("font")){
								//font = false;
							}
						}
					}	
					else if(eventType == XmlPullParser.TEXT) {
Log.d("", "body "+body+" title "+title+" a "+a);
						if (body && !title && !a){
							String S = parser.getText();
Log.d("", "TEXT "+S);
							//if (S.trim().length() != 0){
							if (S == null); 
							else if (! S.equals("\n")){
								S = S.replaceAll("\n", "");
								
								Boolean SelOrBm = false;
								if (aSel){
									S = Utils.CHAR_SEL_START+S;
									aSel = false;
									SelOrBm = true;
								}	
								if (aBm){
									S = Utils.CHAR_BM+S;
									aBm = false;
									SelOrBm = true;
								}
								
								if (bold){
									S = Utils.CHAR_BOLD+S;
									bold = false;
									SelOrBm = true;
								}	
								
								if (noBold){
									S = Utils.CHAR_BOLD_END+S;
									noBold = false;
									SelOrBm = true;
								}	

								
								if (italic){
									S = Utils.CHAR_ITALIC+S;
									italic = false;
									SelOrBm = true;
								}	
								
								if (noItalic){
									S = Utils.CHAR_ITALIC_END+S;
									noItalic = false;
									SelOrBm = true;
								}	
								
								
								if (noSel){
									S = Utils.CHAR_SEL_END+S;
									noSel = false;
									SelOrBm = true;
								}	
								
								if (font){
									font = false;
									SelOrBm = true;
								}	
								
								if (SelOrBm){
										strings.set(strings.size()-1, strings.get(strings.size()-1)+S);
																														
								}
								else if (aref){
									if (strings.size() > 0) strings.set(strings.size()-1, strings.get(strings.size()-1)+S);
									else strings.add(S);
									aref = false;
								}	
								else{
								     //S = S.replaceAll("<emphasis>","");
								     //S = S.replaceAll("</emphasis>","");
								 									//if (strings.size() == 1 && strings.get(0).equals("")) strings.set(0, S);
									//if (S.trim().length() > 0)
									//	if(S.contains("<emphasis>") || S.contains("</emphasis>")){
											strings.add(S);
									//	}
									}	
								
							} 
							else strings.add("");
							}
						} 
						
						try {eventType = parser.next();}
						catch (XmlPullParserException  e) {
Log.d("",""+e);
							int i = 1;
						}

						j++;
						//progressDialog.setProgress(j);
						//publishProgress
						//eventType = parser.next();         
				}         
				Utils.info = "File <"+name+"> loaded. "+strings.size()+" strings.";
			} catch (Throwable t) {
				showToast(mContext.getResources().getString(R.string.error_load_xml)+". "+t.toString());
				Utils.info = mContext.getResources().getString(R.string.error_load_xml)+". "+t.toString();
			}
		}

		private void loadZIP(String name, ArrayList<String> strings){		
			try {

				int BUFFER_SIZE = 1024;
				byte[] buffer = new byte[BUFFER_SIZE];
				int size;
				
				String nameUnzipped = Utils.getAppDirectory()+"tempUnzipped.xml";
				
				FileInputStream fin = new FileInputStream(name);
				ZipInputStream zin = new ZipInputStream(fin);
				//BufferedReader reader = new BufferedReader(new InputStreamReader(zin));			
				ZipEntry ze = null;
				while ((ze = zin.getNextEntry()) != null) {
					if (isCancelled()) return;
					if (ze.isDirectory()) {
					} else {
						FileOutputStream fout = new FileOutputStream(nameUnzipped);
						BufferedOutputStream bout = new BufferedOutputStream(fout, BUFFER_SIZE);
						
						while ( (size = zin.read(buffer, 0, BUFFER_SIZE)) != -1 ) {
	                        fout.write(buffer, 0, size);
	                    }
						
						zin.closeEntry();
						bout.flush();
						bout.close(); 
					}
				}
				zin.close();
				loadXML(nameUnzipped, strings);
			} catch (Exception t) {
				Utils.info = mContext.getResources().getString(R.string.error_load_zip)+" "+ t.toString();
			}
		}	
		
		
		private InputStream openFileInputStream(String name){
			if (mFromRresource)
				return mContext.getResources().openRawResource(R.raw.salinger);
			else
				try {
					return new FileInputStream(name);
				} catch (FileNotFoundException e) {
					return null;
				}
		}
		
		private void loadTXT(String name, ArrayList<String> strings){
			File file = new File(name);
			if(!file.exists() && !mFromRresource){    
				showToast(mContext.getResources().getString(R.string.error_file_not_found)+name);
				return;                  
			}
			strings.clear();
			BufferedReader reader;
		
			
			try {
				int c[] = {0,0,0};
				
				InputStream fis = openFileInputStream(name);
				
				if (fis.available() >= 2){
					c[0] = fis.read();
					c[1] = fis.read();
				}
				if (fis.available() >= 3)
					c[2] = fis.read();
				
				if (c[0] == 255 && c[1] == 254 )
					reader = new BufferedReader(new InputStreamReader(openFileInputStream(name), "UTF-16"));
				else if (c[0] == 239 && c[1] == 187 && c[2] == 191 )
					reader = new BufferedReader(new InputStreamReader(openFileInputStream(name), "UTF-8"));
				else
					reader = new BufferedReader(new InputStreamReader(openFileInputStream(name), "windows-1251"));
				
				reader = new BufferedReader(new InputStreamReader(openFileInputStream(name), "UTF-8"));
					
				String line = reader.readLine();
				while (line != null){
					if (isCancelled()) return;
					strings.add(line);
					//if (strings.size()<9)
						//Log.d("z",strings.get(strings.size()-1));
						
					line = reader.readLine();
				}
				
				reader.close();
				Utils.info = "File <"+name+"> loaded. "+strings.size()+" strings.";
			} catch (IOException t) {
				Utils.info = "Error loading "+" "+ t.toString();
			}
		}	

		public void loadHTML(String name, ArrayList<String> strings){
			
			/*
			try {
				progressDialog.setMax(Utils.countLines(name));
			} catch (IOException e) {
				progressDialog.setMax(0);
			}
			*/
			//String UTF8 = "utf8";
			
			File file = new File(Utils.getAppFolder() + "temp.xml");
			try {
				InputStreamReader inputStream = new InputStreamReader(new FileInputStream(name));
				//InputStream inputStream = new FileInputStream(name, );

				FileWriter fileWriter = new FileWriter(file);
				BufferedWriter bufferFileWriter  = new BufferedWriter(fileWriter);
				
				// if file the available for reading
				if (inputStream != null) {
				  // prepare the file for reading
				  BufferedReader buffreader = new BufferedReader(inputStream);

				  String line;

				  // read every line of the file into the line-variable, on line at the time
				  int i = 0;
				  line = buffreader.readLine();
				  while (line != null){
						if (isCancelled()) return;
					  	 line = line.replaceAll("</?meta ?[^>]*>","");
					     line = line.replaceAll("</?font ?[^>]*>","");
					     line = line.replaceAll("</?font ?[^>]*>","");
					     line = line.replaceAll("</?br ?[^>]*>","");
					     line = line.replaceAll("&amp;"," ");
					     line = line.replaceAll("&nbsp;"," ");
					  
					     if (line.trim().length() > 0){
					    	if (line.endsWith("</p>"))
					    		fileWriter.write(line+"\n");
					    	else
					    		fileWriter.write(line+" ");
					     }
						  line = buffreader.readLine();
						  i++;
						 // progressDialog.setProgress(i);
				  }
				  
				  inputStream.close();
				  bufferFileWriter.flush();
				  bufferFileWriter.close();
				}
			} catch (Exception ex) {
				Utils.info = "Error loading "+" "+ ex.toString();
				return;
			} finally {
				
			}
			loadXML(Utils.getAppFolder() + "temp.xml", strings);
		}	

		
		public void copy(String src, String dst) throws IOException {
			Log.d("", src);
			Log.d("", dst);
			
		    InputStream in = new FileInputStream(src);
			Log.d("", "START 0");
		    OutputStream out = new FileOutputStream(dst);

			Log.d("", "START");
		    
		    // Transfer bytes from in to out
		    byte[] buf = new byte[1024];
		    int len;
		    while ((len = in.read(buf)) > 0) {
				if (isCancelled()) return;
		        out.write(buf, 0, len);
		    }
		    in.close();
		    out.close();
		}	
		
	}
	

	/**
	 * Loads users dictionary 
	 * @param name - nam� of the dictionary file
	 * @param entryes - list to put loaded Entries
	 */
	public void loadDictionary(String name, ArrayList<Entry> entryes) {
		try {
			BufferedReader reader;
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(name)));
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); 
			factory.setNamespaceAware(true);         
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(reader);
			
			String n = "";
			Entry entry = null;
		    while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
		        switch (xpp.getEventType()) {
		        //
		        case XmlPullParser.START_DOCUMENT:
			        break;
			    case XmlPullParser.START_TAG:
			    	n = xpp.getName();
			    	if (n.equals("entry"))
			    		entry = new Entry();
			        break;
			    case XmlPullParser.END_TAG:
			    	if (xpp.getName().equals("entry"))
			    		entryes.add(entry);
			    	n = "";
			        break;
			    case XmlPullParser.TEXT:
			    	if (entry != null)
			    		entry.setFromXpp(n, xpp);
			    	break;
			    default:
			          break;
			  }
		      xpp.next();
		    }
		    Utils.info = "File <"+name+"> loaded. "+entryes.size()+" entries.";
		} catch (Throwable t) {
			Utils.info = mContext.getResources().getString(R.string.error_load_xml)+". "+t.toString();
		}	
		
	}

}



