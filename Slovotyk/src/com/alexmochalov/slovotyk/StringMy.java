package com.alexmochalov.slovotyk;

public class StringMy {
	public StringMy(String string) {
		this.string = string;
	}
	String string;
	int substrCount;
	
	public StringMy addString(String string) {
		this.string = this.string+string;
		return this;
	}

}
