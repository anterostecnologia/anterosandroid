/*
 * Copyright 2016 Anteros Tecnologia
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package br.com.anteros.android.ui.controls.maskEditText;

/**
 * @website https://github.com/toshikurauchi/MaskedEditText
 * @author TOSHI KURAUCHI
 *
 */
public class RawText {
	private String text;
	
	public RawText() {
		text = "";
	}
	
	public void subtractFromString(Range range) {
		String firstPart = "";
		String lastPart = "";
		
		if(range.getStart() > 0 && range.getStart() <= text.length()) {
			firstPart = text.substring(0, range.getStart());
		}
		if(range.getEnd() >= 0 && range.getEnd() < text.length()) {
			lastPart = text.substring(range.getEnd(), text.length());
		}
		text = firstPart.concat(lastPart);
	}

	/**
	 * 
	 * @param newString New String to be added
	 * @param start Position to insert newString
	 * @param maxLength Maximum raw text length
	 * @return Number of added characters
	 */
	public int addToString(String newString, int start, int maxLength) {
		String firstPart = "";
		String lastPart = "";
		
		if(newString == null || newString.equals("")) {
			return 0;
		}
		else if(start < 0) {
			throw new IllegalArgumentException("Start position must be non-negative");
		}
		else if(start > text.length()) {
			throw new IllegalArgumentException("Start position must be less than the actual text length");
		}
		
		int count = newString.length();
		
		if(start > 0) {
			firstPart = text.substring(0, start);
		}
		if(start >= 0 && start < text.length()) {
			lastPart = text.substring(start, text.length());
		}
		if(text.length() + newString.length() > maxLength) {
			count = maxLength - text.length();
			newString = newString.substring(0, count);
		}
		text = firstPart.concat(newString).concat(lastPart);		
		return count;
	}

	public String getText() {
		return text;
	}

	public int length() {
		return text.length();
	}

	public char charAt(int position) {
		return text.charAt(position);
	}
}
