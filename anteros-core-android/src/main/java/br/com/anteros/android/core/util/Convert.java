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

package br.com.anteros.android.core.util;

import java.util.Vector;

/**
 * 
 * @author Edson Martins e Thiago Turim
 */
@SuppressWarnings("rawtypes")
public class Convert {

	public static String valueOfDef(Object value, String defaultValue) {
		String result = String.valueOf(value);
		if ("".equals(result)) 
			result = defaultValue;
		return result;
	}

	public static String valueOfDef(int value, String defaultValue) {
		if (value == 0) 
			return defaultValue;

		String result = String.valueOf(value);
		if ("".equals(result)) 
			result = defaultValue;
		return result;
	}

	public static int parseInt(String value, int defaultValue) {
		int result = defaultValue;
		try {
			if (!"".equals(value)) 
				result = Integer.parseInt(value);
		} catch (Exception e) {
			result = defaultValue;
		}

		return result;
	}

	public static long parseLong(String value, long defaultValue) {
		long result = defaultValue;
		try {
			if (!"".equals(value)) 
				result = Long.parseLong(value);
		} catch (Exception e) {
			result = defaultValue;
		}

		return result;
	}

	public static double parseDouble(String value, int defaultValue) {
		double result = defaultValue;
		try {
			if (!"".equals(value)) 
				result = Double.parseDouble(value);
		} catch (Exception e) {
			result = defaultValue;
		}

		return result;
	}

	public static String removeCharacters(String value, String ch) {
		String result = "";
		boolean exists = false;

		for (int i = 0; i < value.length(); i++) {
			exists = false;
			for (int z = 0; z < ch.length(); z++) {
				if (value.charAt(i) == ch.charAt(z)) 
					exists = true;
			}
			if (!exists) 
				result += value.charAt(i);
		}

		return result;
	}

	
	public static String[] convertVectorInArray(Vector list) {
		String[] newList = new String[list.size()];
		for (int i = 0; i < list.size(); i++) 
			newList[i] = (String) list.elementAt(i);
		return newList;
	}

	
	public static Vector convertArrayInVector(String[] list) {
		Vector newList = new Vector();
		for (int i = 0; i < list.length; i++) {
			newList.addElement(list[i]);
		}
		return newList;
	}

	public static String formatValueZerosLeft(String value, int size) {
		StringBuffer sb = new StringBuffer(size);
		sb.append(replicate('0', size - value.length()));
		sb.append(value);
		return sb.toString();
	}

	public static String replicate(char value, int size) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < size; i++) {
			sb.append(value);
		}
		return sb.toString();
	}

	public static String getNumericChars(String text) {
		char[] chars = text.toCharArray();

		StringBuffer result = new StringBuffer();

		int start = 0;
		if (chars[0] == '-') {
			start = 1;
			result.append("-");
		}
		for (int i = start; i < chars.length; i++) {
			char c = chars[i];
			if (Character.isDigit(c)) {
				result.append(c);
			}
		}
		return result.toString();

	}

	private static int[] powersOfTen = { 1, 10, 100, 1000, 10000 };

	protected static double round(double x, int precision) {
		if (x == 0) {
			return x;
		}
		double y = Math.abs(x);
		int sign = x == y ? 1 : -1;
		int shift = 0;
		while (y < powersOfTen[precision - 1]) {
			y *= 10;
			shift++;
		}
		return sign * Math.floor(y + 0.5) / powersOfTen[shift];
	}

	public static void clearAndNUllVector(Vector list) {
		for (int i = 0; i < list.size(); i++) {
			Object obj = list.elementAt(i);
			obj = null;
		}
		list.removeAllElements();
		list = null;
	}

	public static Byte[] toByteArray(byte[] value) {
		Byte[] bytes = new Byte[((byte[]) value).length];
		for (int i = 0; i < ((byte[]) value).length; i++)
			bytes[i] = new Byte(((byte[]) value)[i]);

		return bytes;
	}

	public static byte[] toPrimitiveByteArray(Byte[] value) {
		byte[] bytes = new byte[value.length];
		for (int i = 0; i < value.length; i++)
			bytes[i] = value[i].byteValue();
		return bytes;
	}

	public static char[] toPrimitiveCharacterArray(byte[] value) {
		return new String(value).toCharArray();
	}

	public static Character[] toCharacterArray(byte[] value) {
		char[] c = new String(value).toCharArray();
		Character[] chars = new Character[value.length];
		for (int i = 0; i < value.length; i++)
			chars[i] = new Character(c[i]);
		return chars;
	}
}
