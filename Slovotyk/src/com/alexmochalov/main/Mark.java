package com.alexmochalov.main;

import java.io.File;
import java.util.Comparator;
import java.util.regex.Pattern;

import com.alexmochalov.main.Mark.MTypes.MarkTypes;

import android.graphics.Rect;

public class Mark {
	int line;
	int pos;
	
	boolean stop;
	String text;
	int length;

	int lineSrc;
	int posSrc;

	Rect rect;

	MarkTypes type;

	public Mark(int line, int pos, int pos2, MarkTypes type){
		super();
		this.line = line;
		this.pos = pos;
		this.type = type;
		this.stop = false;
		this.length = pos2 - pos;

		rect = new Rect();
	}

	public Mark(int line, int pos, MarkTypes type){
		super();
		this.line = line;
		this.pos = pos;
		this.type = type;
		this.stop = false;

		rect = new Rect();
	}

	public Mark(String text, int line, int pos, MarkTypes type){
		super();
		this.line = line;
		this.pos = pos;
		this.type = type;
		this.stop = false;
		this.text = text;

		rect = new Rect();
	}

	public Mark(Mark mark){
		super();
		this.line = mark.line;
		this.pos = mark.pos;
		this.type = mark.type;
		this.stop = mark.stop;

		this.lineSrc = mark.lineSrc;
		this.posSrc = mark.posSrc;

		rect = new Rect();
		rect.set(mark.rect);
	}

	
	public void setLength(String string) {
		if (this.type == MarkTypes.BookMark)
			this.text = string;
		else {
			for (int i = pos; i < string.length(); i++)
				if (!isAlphabetic(string.charAt(i))){
					this.length = i - pos;
					this.text = string.substring(pos, pos+length-1);
					return;
				}
			this.length = string.length() - pos;
			this.text = string.substring(pos, pos+length-1);
		}
	}
	
	public Mark(int line, int pos, int lineSrc, int posSrc, MarkTypes type, boolean stop){
		super();
		this.line = line;
		this.pos = pos;
		this.stop = stop;

		this.lineSrc = lineSrc;
		this.posSrc = posSrc;

		this.type = type;
		rect = new Rect();
	}

	public static class MTypes{
		enum MarkTypes{Up, Dn, Sel, BookMark}
	}

	public void restoreSrc(){
		line = lineSrc;
		pos = posSrc;
	}

	public void setRect(int left, int top, int right, int bottom){
		rect.left = left;
		rect.top = top;
		rect.right = right;
		rect.bottom = bottom;
	}

	public void moveRect(int dx, int dy){
		rect.left = rect.left+dx;
		rect.top = rect.top+dy;
		rect.right = rect.right+dx;
		rect.bottom = rect.bottom+dy;
	}

	public void setType(MarkTypes type){
		this.type = type;
	}

	public void setline(int line){
		this.line = line;
	}

	public String getTipeStr(){
		if (stop){
			if (type == MarkTypes.Dn) return "<dn_stop/>";
			return "<up_stop/>";
		}else{
			if (type == MarkTypes.Dn) return "<dn/>";
			if (type == MarkTypes.Up) return "<up/>";
			if (type == MarkTypes.Sel) return "<sl/>";
			return "<bm/>";
		}
	}

	public void setPos(int pos){
		this.pos = pos;
	}

	public int getCenterY(){
		return(rect.top+((rect.bottom-rect.top)>>1));
	}

	public int getCenterX(){
		return(rect.left+((rect.right-rect.left)>>1));
	}
/*
	public void setLineAndPosSrc(AString S){
		lineSrc = S.index;
		posSrc = S.posStart + pos + 1;
	}
*/
	public static class MarkComparator implements Comparator<Mark> {   
		public int compare(Mark markA, Mark markB) {
			if (markA.line < markB.line)
				return -1;
			else if (markA.line > markB.line)
				return 1;
			else
			if (markA.pos <= markB.pos)
				return -1;
			else
				return 1;
		}
	}

	
	private boolean isAlphabetic(char charAt) {
		Pattern p = Pattern.compile("^[a-zA-Z]");
		return p.matcher(""+charAt).find();		
	}

}

