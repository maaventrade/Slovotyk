package com.alexmochalov.dic;

public class IndexEntry {
	String text;
	int pos;
	int length;

	IndexEntry(String text, int pos){
		super();
		this.text = text;
		this.pos = pos;
		this.length = 0;
	}

	IndexEntry(String text, int pos, int length){
		super();
		this.text = text;
		this.pos = pos;
		this.length = length;
	}

	IndexEntry(String text){
		super();
		char c = 0x9;
		int n1 = text.indexOf(c);
		int n2 = text.indexOf(c, n1+1);

		try{
			this.text = text.substring(0, n1);
			String posStr = text.substring(n1+1, n2);
			this.pos = Integer.parseInt(posStr, 16);
			String lenStr = text.substring(n2+1);
			this.length = Integer.parseInt(lenStr, 16);
		} catch(NumberFormatException e){
			int xxx = 1;
		} catch(NullPointerException e){
			int xxx = 1;
		} catch(StringIndexOutOfBoundsException e){
			int xxx = 1;
		}


	}

	public String getName()
	{
		// TODO: Implement this method
		return text;
	}
}

