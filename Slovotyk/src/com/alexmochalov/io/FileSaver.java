package com.alexmochalov.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

//import com.alex.mochalov.TextToDict.R;



import com.alexmochalov.dic.Entry;
import com.alexmochalov.slovotyk.R;
import com.alexmochalov.slovotyk.Utils;
import com.alexmochalov.slovotyk.R.string;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
//import com.alex.mochalov.TextToDic.*;

public class FileSaver {
	private Context context;

	public FileSaver(Context context){
        super();
        this.context = context;
	}

	public boolean saveText(String name, String APP_FOLDER, ArrayList<String> strings){
		try {

			File file = new File(name);
			
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

			//BufferedWriter writer = new BufferedWriter(new FileWriter(file));

			writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>"+"\n");
			writer.write("<body>"+"\n");
			writer.write("<section>"+"\n");

			String S = "<p>";
			for (String str: strings){
				if (str.charAt(0) == Utils.CHAR_TAB){
					if (strings.indexOf(str) != 0){
						//Log.d("", S);
						writer.write(S+"</p>"+"\n");
					}
					S = "<p>"+strToXML(str).trim();
				} else 
					S = S+" "+strToXML(str).trim();
			}
			if (!S.equals(""))
				writer.write(S+"</p>"+"\n");

			writer.write("</section>"+"\n");
			writer.write("</body>"+"\n");

			writer.close();
			Utils.setInformation("Saved file "+file.getPath());
			Toast.makeText(context,
					context.getResources().getString(R.string.file_saved)+" "+name, Toast.LENGTH_LONG)
					.show();
		} catch (IOException e) {
			//Utils.setInformation(context.getResources().getString(R.string.error_save_file)+" "+e);
			Toast.makeText(context, context.getResources().getString(R.string.error_save_file) +" "+e , Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	private String strToXML(String str) {
		String result = "";
		/*
		for (int index = 0; index < str.length(); index++){
			char c = str.charAt(index);
			if (c < 20){ // control code
				if (c == Utils.CHAR_BOLD)
					result = result+"<b>";
				else if (c == Utils.CHAR_BOLD_END)
					result = result+"</b>";
				else if (c == Utils.CHAR_SEL_START)
					result = result+"<sl>";
				else if (c == Utils.CHAR_SEL_END)
					result = result+"</sl>";
				else if (c == Utils.CHAR_BM)
					result = result+"<bm>";
				else if (c == Utils.CHAR_ITALIC)
					result = result+"<i>";
				else if (c == Utils.CHAR_ITALIC_END)
					result = result+"</i>";
			} 
			else 
				result = result+c;
		}
		*/
		result = str.replace(""+(char)(Utils.CHAR_BOLD), "<b>")
			.replace(""+(char)(Utils.CHAR_BOLD_END), "</b>")
			.replace(""+(char)(Utils.CHAR_SEL_START), "<sl>")
			.replace(""+(char)(Utils.CHAR_SEL_END), "</sl>")
			.replace(""+(char)(Utils.CHAR_BM), "<bm>")
			.replace(""+(char)(Utils.CHAR_ITALIC), "<i>")
			.replace(""+(char)(Utils.CHAR_ITALIC_END), "</i>")
			.replace("&", "&amp;");
		return result;
	}

	/**
	 * Saves the user dictionaries
	 * @param name
	 * @param APP_FOLDER
	 * @param entries
	 */
	public void save(String name, ArrayList<Entry> entries) {
		try {
			File file = new File(name);
			
			
			Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));

			writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>"+"\n");
			writer.write("<data>"+"\n");

			for (Entry entry: entries){
				entry.save(writer);
			}	

			writer.write("</data>"+"\n");

			writer.close();
		} catch (IOException e) {
			Toast.makeText(context, context.getResources().getString(R.string.error_save_file) +" "+name, Toast.LENGTH_LONG).show();
		}
	}	

}

