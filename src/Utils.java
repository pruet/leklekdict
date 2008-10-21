// $Id: Utils.java,v 1.2 2008/10/21 21:20:22 pruet Exp $
// Copyright (C) 2003 CMUD <neng@ba.cmu.ac.th>
// Copyright (C) 2004 Pruet Boonma <pruetboonma@gmail.com>
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


public class Utils {
    
    /**
     *  Convert formated string to a hash table, each entry of hash item is name and 
     *  value pair which is separated by <b>sep</b> character. Each pair in the
     *  <b>propStr</b> is separted by <b>end</b> character.
     *  @param propStr the property list, in pattern of name<b>sep</b>value<b>end</b>name<b>sep</b>value
     *  @param sep the separator between name and value
     *  @param end the separator between each pair
     */
    static public Hashtable string2hash(String propStr, char sep, char end)
    {
        if(propStr == null) return null;
        if(!propStr.endsWith("|")) {
            propStr = propStr + "|";
        }
        Hashtable ht = new Hashtable();
        byte[] bt = propStr.getBytes();
        int kStop = 0;
        int vStop = 0;
        int kStart = 0;
        int i;
        boolean flag = false;
        for(i = 0; i != bt.length; i++) {
            char ch = (char)bt[i];
            if(ch == sep) {
                kStop = i;  
            }
            if(ch == end) {
                vStop = i;
                flag = true;
            }
            if(flag && kStop > kStart) {
                ht.put(new String(bt, kStart, kStop - kStart), new String(bt, kStop + 1, vStop - kStop - 1));
                kStart = vStop + 1;
                flag = false;
            }
        }
        return ht;
    }
    /**
     *  Convert a normal string to URL-compliant String, a character lookup table required
     *  @param str the original string
     *  @param cStr the convertion pattern string, in form of orig1:rep1|orig1:rep1|orig1:rep1
     */
    static public String convert(String str, String cStr) throws Exception 
    {
        if(str == null || cStr == null) return null;

        String sKey = "";
        String sValue = "";
        
        try {   
            Hashtable charTable = string2hash(cStr, ':', '|');
            Enumeration enu = charTable.keys();
            while (enu.hasMoreElements()) {
                Object key = enu.nextElement();
                Object value = charTable.get(key);
                sKey = (String)key;
                sValue = (String)value;
                str = replace(str, sKey, sValue);
            }
        }
        catch (Exception e) {
            //e.printStackTrace();
        }
        return str;
    }

    /**
     *  String repleacement function
     *  @param str the original string
     *  @param pattern the target string
     *  @param replace  the replacement string
     */
    static public String replace(String str, String pattern, String replace) 
    {
        if(str == null || pattern == null || replace == null) return null;  
        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();

        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e + pattern.length();
        }

        result.append(str.substring(s));
        return result.toString();

    }
    
}
