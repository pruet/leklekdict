// Copyright (C) 2003,2004 Pruet Boonma <pruet@eng.cmu.ac.th>
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  US

/**
 *	Byte Array processor, all method please be static.
 *	@author	Pruet Boonma <pruet@eng.cmu.ac.th>
 *	@version 0.6, September 2004
 */
 
public class ByteArray
{

	static public byte[] convertFromSaraUm(byte[] bt)
	{
		// we have two choice
		// 1. make it two pass, but use less memory/time to alloc.
		// 2. make it one pass, but use more redundant memory/time to realloc

		if(bt == null || bt.length == 0) {
			return null;
		}

		int len = bt.length;
		int i, j;
		int saraUmCount = 0;
		for(i = 0; i != len; i++) {
			if(bt[i] == -45) saraUmCount++;
		}
		if(saraUmCount == 0) return bt;
		byte btNew[] = new byte[len + saraUmCount];
		j = 0;
		for(i = 0; i != len; i++) {
			if(bt[i] == -45) {
				btNew[j] = (byte)(-19);
				j++;
				btNew[j] = (byte)(-46);
			} else {
				btNew[j] = bt[i];
			}
			j++;
		}
		return btNew;

	}
	static public byte[] convertToSaraUm(byte[] bt)
	{
		if(bt == null || bt.length == 0) {
			return null;
		}
		int len = bt.length;
		int j = 0;
		for(int i = 0; i != len; i++) {
			bt[j] = bt[i];
			if(bt[j] == (byte)(-46) && bt[j - 1] == (byte)(-19)) {
				bt[j - 1] = (byte)(-45);
			} else {
				j++;
			}
		}
		if(j != len) {
			return substring(bt, 0, j);
		} else {
			return bt;
		}
	}
	/**
	 *	Get the index of <b>ch</b> in <b>bt</b> array.
	 */
	static public int indexOf(byte[] bt, char ch)
	{
		int len;
	    if(bt == null || (len = bt.length) == 0) return -1;
		int i;
		if(ch > 255) ch -= 3424;
		if(ch < 0) ch += 256;
		for(i = 0; i != len; i++) {
			if(bt[i] == ch) {
				break;
			}
		}
		if(i == len) {
			return -1;
		}
		return i;
	}
	
		/**
	 *	Get the index of <b>by</b> in <b>bt</b> array.
	 */
	static public int indexOf(byte[] bt, byte by)
	{
		int len;
		if(bt == null || (len = bt.length) == 0) return -1;
		int i;
		for(i = 0; i != len; i++) {
			if(bt[i] == by) {
				break;
			}
		}
		if(i == len) {
			return -1;
		}
		return i;
	}
	
	/**
	 *	Get a part of <b>bt</b>, from <b>start</b> to <b>stop</b>
	 */
	static public byte[] substring(byte[] bt, int start, int stop)
	{
		if(stop <= start || start < 0 || stop > bt.length) return null;
		byte[] out = new byte[stop - start];
		int j = 0;
		for(int i = start; i != stop; i++, j++) {
			out[j] = bt[i];
		}
		return out;
	}

	/**
	 * Get a part of <b>bt</b>, from start to sepearter <b>sp</b>
	 */
	static public byte[] substringEndWith(byte[] bt, char sp)
	{
		int l = bt.length;
		for(int i = 0; i != l; i++) {
			if(bt[i] == sp) {
				return substring(bt, 0, i);
			}
		}
		return null;	
	}

	/**
	 * Get a part of <b>bt</b>, from sepearter <b>sp</b> to end
	 */
	static public byte[] substringStartWith(byte[] bt, char sp)
	{
		int l = bt.length;
		for(int i = 0; i != l; i++) {
			if(bt[i] == sp) {
				return substring(bt, i + 1, l );
			}
		}
		return null;	
	}

	/**
	 *	Check if <b>bt</b> starts with <b>word</b>
	 */
	static public boolean startsWith(String bt, String word)
	{
		return startsWith(getBytes(bt), getBytes(word));
	}
	
	/**
	 *	Check if <b>bt</b> starts with <b>word</b>
	 */
	static public boolean startsWith(byte[] bt, String word)
	{
		return startsWith(bt, getBytes(word));
	}
	
	/**
	 *	Check if <b>bt</b> starts with <b>bta</b>
	 */
	static public boolean startsWith(byte[] bt, byte[] bta)
	{
		int len;
		if(bta == null || bt == null || bt.length < (len = bta.length)) return false;
		int i;
		for(i = 0; i != len; i++) {
			if(bt[i] != bta[i]) return false;
		}
		return true;
	}

	/**
	 * 	Connect to strings together
	 */
	static public byte[] concat(byte[] first, byte[] second)
	{
		byte[] out = new byte[first.length + second.length];
		int i;
		int l = first.length;
		for(i = 0; i != l; i++) {
			out[i] = first[i];
		}
		l = second.length;
		for(int j = 0; j != l;j++, i++) {
			out[i] = second[j];
		}
		return out;
	}

	/**
	 *	Check if <b>bt</b> has the same string sequence as <b>word</b>
	 */
	static public boolean equals(byte[] bt, String word)
	{
		return equals(bt, getBytes(word));
	}
	
	/**
	 *	Check if <b>bta</b> has the same string sequence as <b>btb</b>
	 */
	static public boolean equals(byte[] bta, byte[] btb)
	{
		if(bta == null || btb == null || bta.length != btb.length) return false;
		return startsWith(bta, btb);
	}

	static public boolean equals(byte[] bta, String word, int limit)
	{
		return equals(bta, getBytes(word), limit);
	}
	static public boolean equals(byte[] bta, byte[] btb, int limit)
	{
		if(bta == null || btb == null ||  btb.length != limit || bta.length < limit)			return false;
		for(int i = 0; i != limit; i++) {
			if(bta[i] != btb[i]) return false;
		}
		return true;

	}
	/**
	 *	get byte from String
	 */
	static public byte[] getBytes(String word)
	{
		if(word == null) return null;
		byte[] bt = word.getBytes();
		for(int i = 0; i != bt.length; i++) {
			//if(bt[i] > 255) bt[i] -= 3424;
			// ? wrong
			if(bt[i] < 0) bt[i] += 256;
		}
		return bt;
	}
	public static char[] convertToChars(byte[] bt)
	{
		int l;
		if(bt == null || (l = bt.length) == 0) return null;
		char[] ch = new char[l];
		for(int i = 0; i != l; i++) {
			//System.out.print("TC " + (int)bt[i]);
			ch[i] = (char)bt[i];
			//System.out.print(">" + (int)ch[i]);
			if(ch[i] > 256) {
				ch[i] -= 61856;
			}
			//System.out.println(">" + (int)ch[i]);
		}
		//System.out.println("---------------------");
		return ch;
	}

	public static String convertToString(byte[] bt)
	{
		if(bt == null || bt.length == 0) return null;
		return new String(convertToChars(bt));
	}

	public static byte[] convertFromChars(char[] ch)
	{
		int l;
		if(ch == null || (l = ch.length) == 0) return null;
		byte[] bt = new byte[ch.length];
		for(int i = 0; i != l; i++) {
			//System.out.print("FC " + (int)ch[i]);
			if(ch[i] > 255) {
				bt[i] = (byte)(ch[i] - 3424 - 256);
			} else {
				bt[i] = (byte) ch[i];
			}
			//System.out.println(">" + (int)bt[i]);
		}
		//System.out.println("---------------------");
		return bt;
	}

	public static byte[] convertFromString(String str)
	{
		int l;
		if(str == null || (l = str.length()) == 0) return null;
		char[] ch = new char[l];
		str.getChars(0, l, ch, 0);
		str = null;
		return convertFromChars(ch);
	}
}
