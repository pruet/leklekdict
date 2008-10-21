// $Id: Setting.java,v 1.2 2008/10/21 21:20:22 pruet Exp $
// Copyright (C) 2003,2004 Pruet Boonma <pruetboonma@gmail.com>
// Copyright (C) 2008 ANS Wireless Co., Ltd.
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

import java.util.*;
import javax.microedition.rms.*;

/**
 *	Setting class for configuration system. It uses RMS as persistant storage.
 *	@author	Pruet Boonma <pruetboonma@gmail.com>
 *	@version 0.4, September 2004
 */
public class Setting
{
	Hashtable hashStorage;
	Hashtable rmsIdHash;
	RecordStore rmsStorage;
	String name;
	final char sep = '=';
	final char end = '|';
	
	/**
	 *	Default constructor
	 *	@param n The setting name, and also the RMS name
	 */
	Setting(String n)
	{
		init(n, new Hashtable());
	}

	/**
	 *	Constructor with string initialization.
	 *	@param n The setting name, and also the RMS name
	 *	@param init the initial string, format is name=value|name=value|name=value
	 */
	Setting(String n, String init) // format is name=value|name=value|name=value
	{
		init(n, Utils.string2hash(init, sep, end));
	}
	
	/**
	 *	Constructor with Hashtable initialization.
	 *	@param n The setting name, and also the RMS name
	 *	@param i the initial hashtable
	 */
	Setting(String n, Hashtable i)
	{
		init(n, i);
	}
	
	/**
	 *	Copy constructor.
	 *	@param n The setting name, and also the RMS name
	 *	@param s The initial Setting object
	 */
	Setting(String n, Setting s)
	{
		init(n, s.getHashtable());
	}
	
	/**
	 *	Main intialize method
	 *	@param n The setting name, and also the RMS name
	 *	@param init the initial hashtable
	 */
	protected void init(String n, Hashtable init)
	{
		name = n;
		if(name == null || name.equals("") || init == null) {
			return;
		}
		name = "Setting" + name;
		Hashtable tempHash = init;
		rmsIdHash = new Hashtable();
		hashStorage = new Hashtable();
		try {
			rmsStorage = RecordStore.openRecordStore(name, true);
			int i = tempHash.size();
			int j = rmsStorage.getNumRecords();
			// craete a hashtable of the RMS records.
			if(j > 0) {
				RecordEnumeration re = rmsStorage.enumerateRecords(null, null, false);
				int id;
				int idx;
				String st;
				String key;
				while(re.hasNextElement()) {
					id = re.nextRecordId();
					st = new String(rmsStorage.getRecord(id));
					idx = st.indexOf(sep);
					key = st.substring(0, idx);
					hashStorage.put(key, st.substring(idx + 1, st.length()));
					rmsIdHash.put(key, new Integer(id));
				}
			}
			if(i > 0) {
				String str;
				int id;
				String key;
				String value;
				for(Enumeration keys = tempHash.keys(); keys.hasMoreElements();) {
					key = (String)keys.nextElement();
					if(j == 0 || !hashStorage.containsKey(key)) { // we don't have this key!!
						value = (String) tempHash.get(key);	
						hashStorage.put(key, value);
						str = key + sep + value;

						id = rmsStorage.addRecord(str.getBytes(), 0, str.length());
						rmsIdHash.put(key, new Integer(id));
					}
				}

			}
			rmsStorage.closeRecordStore();
			System.gc();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 *	Get a setup value by key, return null if not found
	 *	@param k the key of the retrieving value.
	 */
	public String get(String k)
	{
		String str = (String) hashStorage.get(k);
		return str;
		//return (String) hashStorage.get(k);
		//if(str == null) return "";
		//return str;
	}
	
	/**
	 *	Set a setup key-value pair
	 *	@param k the key part of the pair
	 *	@param v the value part of the pair
	 */
	public void set(String k, String v)
	{
		if(name == null || name.equals("") || k == null || k.equals("") || v == null) {
			return;
		}
		try {
			rmsStorage = RecordStore.openRecordStore(name, true);
			if(hashStorage.containsKey(k)) {
				String str = k + sep + v;
				rmsStorage.setRecord(((Integer)rmsIdHash.get(k)).intValue(), str.getBytes(), 0, str.getBytes().length);
				hashStorage.put(k, v);
			} else {
				String str = k + sep + v;
				int id = rmsStorage.addRecord(str.getBytes(), 0, str.length());
				rmsIdHash.put(k, new Integer(id));
				hashStorage.put(k, v);
			}
			rmsStorage.closeRecordStore();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 *	Check if we have that key
	 *	@param k the key name
	 */
	public boolean contains(String k)
	{
		return hashStorage.containsKey(k);
	}
	
	/**
	 *	Get the storage hashtable
	 */
	public Hashtable getHashtable()
	{
		return hashStorage;
	}
	
	/**
	 *	Get name of this setup
	 */
	public String getName()
	{
		return name;
	}
};
