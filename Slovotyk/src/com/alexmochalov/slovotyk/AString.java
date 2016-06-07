package com.alexmochalov.slovotyk;

	public class AString {
		String string;
		Boolean start;

		int index;
		int posStart;
		int posEnd;

		float addToBlank; // extra pixels (used when blanks drawed)

		public AString(String string, Boolean start, int index, int posStart, int posEnd, float addToBlank){
			super();
			this.string = string;
			this.start = start;
			this.index = index;
			this.posStart = posStart;
			this.posEnd = posEnd;
			this.addToBlank = Math.max(0, addToBlank);
		}
		
}
