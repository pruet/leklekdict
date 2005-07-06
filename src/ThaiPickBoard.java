// Copyright (C) 2004 Pruet Boonma <pruet@eng.cmu.ac.th>
// Copyright (C) 2002 Vuthichai Ampornaramveth <vuthi@vuthi.com>
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
 * TODO:
 * 1. Add select-by-cursor for S60
 * 2. Add horizontal-abcd key layout
 */

import javax.microedition.lcdui.*;
import java.util.*;

/**
 *	KeyRepeatTimer is a support class for ThaiPickBoard
 */
class KeyRepeatTimer extends TimerTask
{
	/**
	 *	ThaiPickBoard object for call-back
	 */
	ThaiPickBoard tp = null;
	
	/**
	 *	Default constructor which create a null call-back
	 */
	KeyRepeatTimer()
	{
		tp = null;
	}
	
	/**
	 *	Constructor which is received call-back object
	 */
	KeyRepeatTimer(ThaiPickBoard main)
	{
		tp = main;
	}
	
	/**
	 *	When the timer finished, it calls the call-back object method
	 */
	public void run()
	{
		if(tp != null) {
			tp.timerTimeout();
		}
	}
}

/**
 *	ThaiPickBoard , Thai input platform for Nokia Series60 and UIQ. Support 
 *	both keypad and touchscreen. This class implements a simple WTT sequence
 *	check for Thai and also some display aid correction.
 *	@author	Pruet Boonma <pruet@eng.cmu.ac.th>
 *	@version 0.1, February 2004
 */
public class ThaiPickBoard extends Canvas
{
  public static final int MAP_TH_LO = 0;
  public static final int MAP_TH_UP = 1;
  public static final int MAP_EN_LO = 2;
  public static final int MAP_EN_UP = 3;
  public static final int DEF_TIMEOUT = 1000;
  public static final int DEF_BUFFER_LIMIT = 160;
  
  public static final int KEY_WIDTH = 16;
  public static final int KEY_HEIGHT = 16;
  public static final int KEY_ROW = 4;
  public static final int KEY_COL = 11;
  
  public static final int KEY_QWERTY = 0;
  public static final int KEY_ABCVER = 1;
  public static final int KEY_ABCHOR = 2;

  public static final int KEY_THAI = 0;
  public static final int KEY_ENG = 1;

  public static final int KEY_LO = 0;
  public static final int KEY_UP = 1;
  
  public static final int DIRECTION_NONE = 0;
  public static final int DIRECTION_LEFT = 1;
  public static final int DIRECTION_RIGHT = 2;
  
  private int map_th_lo[];
  private int map_th_up[];
  private int map_en_lo[];
  private int map_en_up[];
  
  private static int map_th_lo_q[] = {
    163,230,191,188,45,228,203,187,
    192,211,161,225,182,190,180,205,
    216,208,224,212,214,209,233,215,
    164,213,232,183,181,195,210,193,
    168,185,202,227,162,194,199,189,
    170,186,167,197
  };
  private static int map_th_up_q[] = {
  	241,240,196, 40,242, 34,166, 41,
  	243,174,175,169,244,177,226,206,
  	217,184,172,218,223,237,231,236,
  	245,234,235, 63,246,179,201,178,
  	247,207,200,204,248,173,171,198,
  	249,176,165, 44
  };
  private static int map_en_lo_q[] = {
  	 49,113, 97,122, 50,119,115,120,
  	 51,101,100, 99, 52,114,102,118,
  	 53,116,103, 98, 54,121,104,110,
  	 55,117,106,109, 56,105,107, 44,
  	 57,111,108, 46, 48,112, 59, 47,
  	 45, 91, 39, 93
  };
  private static int map_en_up_q[] = {
  	 33, 81, 65, 90, 64, 87, 83, 88,
  	 35, 69, 68, 67, 36, 82, 70, 86,
  	 37, 84, 71, 66, 94, 89, 72, 78,
  	 38, 85, 74, 77, 42, 73, 75, 60,
  	 40, 79, 76, 62, 41, 80, 58, 63,
  	 95,123, 34,125
  };
  private static int map_th_lo_a[] = {
  	161,162,164,166,167,168,169,170,
  	171,172,173,174,175,176,177,178,
  	179,180,181,182,183,184,185,186,
  	187,188,189,190,191,192,193,194,
  	195,196,197,198,199,200,201,202,
  	203,204,205,206  	
  };
  private static int map_th_up_a[] = {
  	207,208,209,210,211,212,213,214,
  	215,216,217,218, 43, 61,126,223,
  	224,225,226,227,228,229,230,231,
  	232,233,234,235,236,237,238,239,
  	240,241,242,243,244,245,246,247,
  	248,249,163,165
  };
  private static int map_en_lo_a[] = {
  	 97, 98, 99,100,101,102,103,104,
  	105,106,107,108,109,110,111,112,
  	113,114,115,116,117,118,119,120,
  	121,122, 49, 50, 51, 52, 53, 54,
  	 55, 56, 57, 48, 44, 46, 59, 47,
  	 45, 91, 39, 93
  };
  private static int map_en_up_a[] = {
  	 65, 66, 67, 68, 69, 70, 71, 72,
  	 73, 74, 75, 76, 77, 78, 79, 80,
  	 81, 82, 83, 84, 85, 86, 87, 88,
  	 89, 90, 33, 64, 35, 36, 37, 94,
  	 38, 42, 40, 41, 60, 62, 58, 63,
  	 95,123, 34,125
  };
  private static int map_th_lo_h[] = {
  	161,174,185,196,162,175,186,197,
	164,176,187,198,166,177,188,199,
	167,178,189,200,168,179,190,201,
	169,180,191,202,170,181,192,203,
	171,182,193,204,172,183,194,205,
	173,184,195,206
  };
  private static int map_th_up_h[] = {
  	207,218,230,241,208, 43,231,242,
	209, 61,232,243,210,216,233,244,
	211,223,234,245,212,224,235,246,
	213,225,236,247,214,226,237,248,
	215,227,238,249,216,228,239,163,
	217,229,240,165
  };
  private static int map_en_lo_h[] = {
	 97,108,119, 56, 98,109,120, 57,
	 99,110,121, 48,100,111,122, 44,
	101,112, 49, 46,102,113, 50, 59,
	103,114, 51, 47,104,115, 52, 45,
	104,116, 53, 91,106,117, 54, 39,
	107,118, 55, 93
  };
  private static int map_en_up_h[] = {
  	 65, 76, 87, 42, 66, 77, 88, 40,
	 67, 78, 89, 41, 68, 79, 90, 60,
	 69, 80, 33, 62, 70, 81, 64, 58,
	 71, 82, 35, 63, 72, 83, 36, 95,
	 73, 84, 37,123, 74, 85, 94, 34,
	 75, 86, 38, 125
  };
  // from ThaiEdit by P' Hui
  private static int levtable[]={
  //0,2,0,0,2,2,2,2,1,1,1,2,0,0,0,0,  29
  	0,2,0,2,2, 2,2,2,1,1,
  	1,2,0,0,0, 0,0,0,0,0,
  	0,0,0,3,3, 3,3,3,3,2,
  	3,0,0,0,0, 0,0,0,0,0,
  	0,0,0,0,0, 0,0,0 
  };

  
  private Vector thaiString = new Vector();
  private int caret = 0;
  private int screenwidth = 0;
  private int screenheight = 0;
  private int maxwidth = 0;
  private int maxheight = 0;
  private int direction = 0;
  private int sy = 0;

  private int font_offset [] = null;
  private int font_offset_y [] = null;
  private byte font_bbx[] = null;
  private byte font_width[] = null;
  private int font_height = 0;
  private int font_top2bbx = 0;
  private String font_id = null;
  private int new_posi = 0;
  private int shown_map = MAP_TH_LO;
  private static Image myFont = null;
  private Command backCmd;
  private Command  submitCmd;
  private int[] map;
  private int bufferLimit = DEF_BUFFER_LIMIT;
  
  private boolean dragged = false;
  private int pointDir = DIRECTION_NONE;
  private int or_x = 0;
  private int min_max_x = 0;

  private KeyRepeatTimer keyTimer = null;
  private Timer timer = null;
  private int timeout = DEF_TIMEOUT;
  private int showKeyHint = 0;


  /**
   *	Default constructor, initialize all necessary variables.
   */
  public ThaiPickBoard()
  {
    try {
      loadFont();
      screenwidth = this.getWidth();
      screenheight = this.getHeight() - 64;
	  maxwidth = screenwidth;
	  maxheight = screenheight;
	  keyTimer = new KeyRepeatTimer(this);
	  setKeyboardLayout(KEY_QWERTY);
	  timer = new Timer();
	  for(int i = 0; i != 40; i++) {
	  	thaiString.addElement(new String("a"));
	  }
    } catch(Exception e) {   }
  }
  
  /**
   *	Load appropriate keyboard layout map, according to selected keymap from user.
   */
  public void setKeyboardLayout(int lo)
  {
	switch(lo) {
		case KEY_QWERTY:
			map_th_lo = map_th_lo_q;
			map_th_up = map_th_up_q;
			map_en_lo = map_en_lo_q;
			map_en_up = map_en_up_q;
			break;
		case KEY_ABCHOR:
			map_th_lo = map_th_lo_h;
			map_th_up = map_th_up_h;
			map_en_lo = map_en_lo_h;
			map_en_up = map_en_up_h;
			break;
		default:
			map_th_lo = map_th_lo_a;
			map_th_up = map_th_up_a;
			map_en_lo = map_en_lo_a;
			map_en_up = map_en_up_a;
	}
  }
  
  /**
   *	Get keyboard layout
   */
  public int getKeyboardLayout()
  {
  	if(map_en_lo == map_en_lo_q) {
  		return KEY_QWERTY;
  	} else if(map_en_lo == map_en_lo_a) {
		return KEY_ABCVER;
	}
  	return KEY_ABCHOR;
  }
  
  /**
   *	Set the Show Key Hint status
   */
  public void setShowKeyHint(int i)
  {
  	showKeyHint = i;
  }
  
  /**
   *	Get Show Key Hint status
   */
  public int getShowKeyHint()
  {
  	return showKeyHint;
  }
  
  /**
   *	Reset Thai string buffer and map, this method should be clear each time
   *	a ThaiPickBoard canvas is displayed.
   */
  protected void reset()
  {
  	caret = 0;
  	thaiString = new Vector();
  	//shown_map = MAP_TH_LO;
  	repaint();
  }

  protected void reset(byte[] bt)
  {
  	caret = 0;
	int l;
	if(bt == null || (l = bt.length) == 0) return;
  	thaiString = new Vector();
	for(int i = 0; i != l; i++) {
		thaiString.addElement(new Byte(bt[i]));
	}
  	//shown_map = MAP_TH_LO;
  	repaint();
  }

  
  /**
   *	Set key timeout, for Keypad, this number should be greater than 500.
   *	On the other hand, for Touchscreen, this number should be less than 100.
   */
  public void setKeyRepeatTimeout(int t)
  {
  	timeout = t;
  }
  
  /**
   *	Get key repeat timeout
   */
  public int getKeyRepeatTimeout()
  {
  	return timeout;
  }
  
  /**
   *	Get buffer size limit
   */
  public int getBufferLimit()
  {
  	return bufferLimit;
  }
  
  /**
   *	Set buffer size limit
   */
  public void setBufferLimit(int b)
  {
  	bufferLimit = b;
  }
  
  /**
   *	Get buffer content, in byte array
   */
  public byte[] getBytes()
  {
	return getBytes(thaiString);  	
  }
  
  /**
   *	Get vector content, internal used only
   */
  protected byte[] getBytes(Vector vt)
  {
  	byte[] bt = new byte[vt.size()];
  	int i = 0;
  	byte c;
  	for (Enumeration e = vt.elements(); e.hasMoreElements() ; i++) {
  		bt[i] = ((Byte)e.nextElement()).byteValue();
    }
 	return bt;
  }
  
  /**
   *	Draw caret, standard color is red (255,0,0)
   */
  private void drawCaret(Graphics g, int x, int y)
  {	
	g.setColor(255, 0, 0);
    g.setClip(x, y - 5, 10, 20); 
    g.drawLine(x, y - 5, x, y + 7); 
  }
  
  /**
   *	Draw Thai String from buffer.
   */
  void drawThai(Graphics g, byte instr[], int x, int y)
  {
    int c,d,e,f,table_offset, bbx_offset = 524, draw_x = 0, draw_y = 0;
    int lastx=0;
    int len = instr.length;
	if(direction == -1) {
		if((screenheight - sy) < maxheight) {
			sy = sy - ((int) (screenheight / font_height)) * font_height;
		}
		direction = 0;
	} else if(direction == 1) {
		if(sy < 0) {
			sy = sy + ((int) (screenheight / font_height)) * font_height;
			if(sy > 0) sy = 0;
		}
		direction = 0;
	}
	g.translate(0, sy);

    for(int i = 0; i < len; i++) {
      e = instr[i];
      if(e < 0) e += 256;
      if(e == 10) {
        x = 0;
        y += font_height;
        if(y >= screenheight) {
        	g.translate(0, -font_height); 
        	y -= font_height;
		}
      }
      if(e >= 32) {
        e -= 32;
        table_offset = e;
        bbx_offset = table_offset << 2;   
        if(x + font_width[table_offset] >= screenwidth) {
          x = 0;
          y += font_height;
          if(y >= screenheight) {
          	g.translate(0, -font_height); 
        	y -= font_height;
          }
        }
        draw_x = x + font_bbx[bbx_offset + 2];
        draw_y = y + font_top2bbx - font_bbx[bbx_offset + 3] - font_bbx[bbx_offset + 1];
        g.setClip(draw_x, draw_y, font_bbx[bbx_offset], font_bbx[bbx_offset+1]);
        g.drawImage(myFont, draw_x - font_offset[table_offset], draw_y - font_offset_y[table_offset], Graphics.LEFT|Graphics.TOP);
        lastx = x;
        x += font_width[table_offset];
        if(i == caret) {
        	drawCaret(g , draw_x , y + 8);
        }
      }
    }
    if(len == 0) {
    	drawCaret(g, 0, 8);
    } else if(caret >= len) {    	
    	drawCaret(g, x, y + 8);
    }
  }
  
  /**
   *	Draw key map
   */
  protected void drawKey(Graphics g, int x, int y)
  {
  	int i, j;
  	int dx, dy;
  	int e;
  	int draw_x, draw_y;
  	int bbx_offset;
  	int table_width = 16 * KEY_COL;
	int table_height = 16 * KEY_ROW;
  	switch(shown_map) {
  		case MAP_EN_UP:
  			map = map_en_up;
  			break;
  		case MAP_EN_LO:
  			map = map_en_lo;
  			break;
  		case MAP_TH_UP:
  			map = map_th_up;
  			break;
  		case MAP_TH_LO:
  		default:
  			map = map_th_lo;
  			break;
  	}
  	j = map.length;
  	g.setColor(255, 255, 255);
  	g.setClip(x, y, screenwidth, 300);
  	g.fillRect(x, y, screenwidth, 300);	
  	g.setClip(x, y, table_width + 1, table_height + 1);
  	g.setColor(0, 0, 0);
  	for(i = 0; i != KEY_ROW; i++) {
  		g.drawLine(x, y + i * KEY_HEIGHT, x + table_width, y + i * KEY_HEIGHT);
  	}
  	for(i = 0; i != KEY_COL + 1; i++) {
  		g.drawLine(x + i * KEY_WIDTH, y, x + i * KEY_WIDTH, y + table_height);
  	}
  	for(i = 0; i != j; i++) {
  		dx = x + (KEY_WIDTH * (i / KEY_ROW));
  		dy = y + (KEY_HEIGHT * (i % KEY_ROW));
  		e = map[i] - 32;
  		bbx_offset=e << 2; 
  		draw_x = dx + 4 ;		
  		draw_y = dy + font_top2bbx - 1 - font_bbx[bbx_offset + 1];
  		// the line below is ugly, how can we get rid of it ?
  		draw_y = (e > 176 && levtable[e - 176] > 0)?draw_y:draw_y - font_bbx[bbx_offset + 3];
  	    g.setClip(draw_x,draw_y,font_bbx[bbx_offset],font_bbx[bbx_offset+1]);
        g.drawImage(myFont,draw_x-font_offset[e],draw_y-font_offset_y[e],Graphics.LEFT|Graphics.TOP);
    }
  }
  
  /**
   *	Draw selected area
   */
  protected void drawRec(Graphics g, int x, int y)
  {
	if(new_posi != 0) {
		int i = y + KEY_HEIGHT * ((new_posi - 1) % KEY_ROW);
		int j = x + KEY_WIDTH * ((new_posi - 1) / KEY_ROW);
		g.setColor(255, 0, 0);
		g.setClip(j, i, KEY_WIDTH + 1, KEY_HEIGHT + 1);
		g.drawRect(j, i, KEY_WIDTH, KEY_HEIGHT);
  	}
  }
  
  /**
   *	Draw keyhint
   */
  protected void drawNumber(Graphics g, int x, int y)
  {
  	int map[] = {49,  50, 51, 52, 53, 54, 55, 56, 57, 48, 35};
  	int i, j;
  	int dx, dy;
  	int e;
  	int draw_x, draw_y;
  	int bbx_offset;
  	j = map.length;
  	y -= KEY_HEIGHT;
  	g.setColor(255, 255, 255);
  	g.setClip(x, y, screenwidth, KEY_HEIGHT);
  	g.fillRect(x, y, screenwidth, KEY_HEIGHT);	
  	for(i = 0; i != j; i++) {
  		dx = x + (KEY_WIDTH * (i % KEY_COL));
  		dy = y + (KEY_HEIGHT * (i / KEY_COL));
  		e = map[i] - 32;
  		bbx_offset=e << 2; 
  		draw_x = dx + 4 ;		
  		draw_y = dy + font_top2bbx - 1 - font_bbx[bbx_offset + 1];
  		draw_y = draw_y - font_bbx[bbx_offset + 3];
  	    g.setClip(draw_x,draw_y,font_bbx[bbx_offset],font_bbx[bbx_offset+1]);
        g.drawImage(myFont,draw_x-font_offset[e],draw_y-font_offset_y[e],Graphics.LEFT|Graphics.TOP);
    }
  }
  
  /**
   *	Main paint method
   */
  protected void paint(Graphics g)
  {
    g.setColor((255<<16)+(255<<8)+255);
    g.fillRect(0,0,screenwidth, screenheight);
    try {
    	drawThai(g, getBytes(thaiString), 0,0);
    } catch(Exception ex) { }
    int w = (screenwidth - KEY_WIDTH * KEY_COL)/2;
    w = (w > 0)?w:0;
	drawKey(g, w, screenheight);
	drawRec(g, w, screenheight);
	if(showKeyHint == 0) drawNumber(g, w, screenheight);
  }
  
  /**
   *	Process a pick board key request, both from keypad and touchscreen
   *	It mainly works by sets the timer
   */
  private void processPickBoardKey(int start, int stop)
  {
  	if(new_posi >= start && new_posi < stop) {
  		new_posi = start + (new_posi + ((start - 1) / KEY_ROW)) % (KEY_ROW + 1);
  	} else {
  		new_posi = start;
  	}
  	timer.cancel();
  	keyTimer.cancel();
  	timer = new Timer();
  	keyTimer = new KeyRepeatTimer(this);
  	timer.schedule(keyTimer, timeout);
  }
  
  /**
   *	Process the switch language request
   */
  protected void switchLanguage()
  {  		
	if(shown_map == MAP_TH_LO || shown_map == MAP_TH_UP) {
		shown_map = MAP_EN_LO;
	} else {
		shown_map = MAP_TH_LO;
	}
  }

  protected void switchLanguage(int k)
  {
  	if(k == KEY_THAI) {
		shown_map = MAP_TH_LO;
	} else {
		shown_map = MAP_EN_LO;
	}
  }

  protected int getActiveLanguage()
  {
	if(shown_map == MAP_TH_LO || shown_map == MAP_TH_UP) {
		return KEY_THAI;
	} else {
		return KEY_ENG;
	}
  }
  
  /**
   *	Process the case swithcing request
   */
  protected void switchCase()
  {
  	switch(shown_map)
	{
		case MAP_TH_LO:
			shown_map = MAP_TH_UP;
			break;
		case MAP_EN_LO:
			shown_map = MAP_EN_UP;
			break;
		case MAP_EN_UP:
			shown_map = MAP_EN_LO;
			break;
		case MAP_TH_UP:
		default:
			shown_map = MAP_TH_LO;
	};
  }

  protected void switchCase(int k)
  {
  	if(k == KEY_LO) {
		if(shown_map == MAP_TH_UP) {
			shown_map = MAP_TH_LO;
		} else if(shown_map == MAP_EN_UP) {
			shown_map = MAP_EN_LO;
		}
	} else {
		if(shown_map == MAP_TH_LO) {
			shown_map = MAP_TH_UP;
		} else if(shown_map == MAP_EN_LO) {
			shown_map = MAP_EN_UP;
		}
	}
  }
  
  /**
   *	Touchscreen pressed request
   */
  protected void pointerPressed(int x, int y)
  {
  	dragged = false;
  	pointDir = DIRECTION_NONE;
  	
  	or_x = x;
  	min_max_x = x;
  }
  
  /**
   *	Touchscreen dragged request
   */
  protected void pointerDragged(int x, int y)
  {
  	dragged = true;
  	if(pointDir == DIRECTION_NONE) {
  		pointDir = (x > or_x)?DIRECTION_RIGHT:DIRECTION_LEFT;
  	} else if(pointDir == DIRECTION_RIGHT) {
  		if(x > min_max_x) min_max_x = x;
  	} else {
  		if(x < min_max_x) min_max_x = x;
  	}
  }
  
  /**
   *	Touchscreen untouched request
   */
  protected void pointerReleased(int x, int y)
  {
  	if(dragged && y < screenheight) {
  		if(pointDir == DIRECTION_RIGHT) {
  			if(min_max_x == x) {
  				moveCaretForward();
  			} else {
  				spaceBar();
  			}
  		} else {
  			if(min_max_x == x) {
  				moveCaretBackward();
  			} else {
  				backSpace();
  			}
  		}
  	} else {
	  	if(y < screenheight) {
	  		if(x > screenwidth/2) {
				switchLanguage();
	   		} else {
	   			switchCase();
	   		}
	  	} else {
	  		int w, h;
	  		w = (screenwidth - KEY_WIDTH * KEY_COL)/2;
	  		if(x < w) {
				thaiString.insertElementAt(new Byte((byte) (-224)), caret);
				caret++;
				new_posi = 0;
	  		} else  if (x > w + (KEY_WIDTH * KEY_COL)) {
				if(caret > 0) {
					caret--;
					thaiString.removeElementAt(caret);
				}
	  		} else {
		  		w = ((w > 0)?x-w:x)/KEY_WIDTH;
		  		h = (y - screenheight)/KEY_HEIGHT;
		  		w = h + (w * 4) + 1;
		  		processPickBoardKey(w , w + 3);
	  		}
	  	}
  	}
  	repaint();
  }
  
  /**
   *	Process space bar request
   */
  protected void spaceBar()
  {
	thaiString.insertElementAt(new Byte((byte) (-224)), caret);
	caret++;
	new_posi = 0;
  }
  
  /**
   *	Process back space request
   */
  protected void backSpace()
  {  		
  	if(caret > 0) {
  		caret--;
  		thaiString.removeElementAt(caret);
  	}  	
  }
  
  /**
   *	Process keypad pressed
   */
  protected void keyPressed(int keyCode)
  {
  	if(getKeyName(keyCode).toLowerCase().startsWith("clear")) {
		backSpace();
  	} else if(getKeyName(keyCode).toLowerCase().startsWith("abc") || getKeyName(keyCode).toLowerCase().startsWith("send") || getKeyName(keyCode).toLowerCase().startsWith("call")) { // it will work on S60 only!!
  		switchLanguage();
  	}
  	switch(keyCode)
  	{
 		case KEY_NUM1:
			processPickBoardKey(1, 4);
  			break;
  		case KEY_NUM2:
  			processPickBoardKey(5, 8);
  			break;
  		case KEY_NUM3:
  			processPickBoardKey(9, 12);
  			break;
  		case KEY_NUM4:
  			processPickBoardKey(13, 16);
  			break;
  		case KEY_NUM5:
  			processPickBoardKey(17, 20);
  			break;
  		case KEY_NUM6:
  			processPickBoardKey(21, 24);
  			break;	
  		case KEY_NUM7:
  			processPickBoardKey(25, 28);
  			break;	
  		case KEY_NUM8:
  			processPickBoardKey(29, 32);
  			break;
   		case KEY_NUM9:
  			processPickBoardKey(33, 36);
  			break;
  		case KEY_NUM0:
  			processPickBoardKey(37, 40);
  			break;	
  		case KEY_POUND:
  			processPickBoardKey(41, 44);
  			break;
  		case KEY_STAR:
  			switchCase();
  			break;
  		default :
  			new_posi = 0;
	  		switch (getGameAction(keyCode)) {
		  		case LEFT:
			  		moveCaretBackward();
		  			break;
		  		case RIGHT:
		  			moveCaretForward();
					break;
				case FIRE:
					spaceBar();
					break;
		  	};
  	};
  	repaint();
  }
  
  /**
   *	Process move forwared
   */
  protected void moveCaretForward()
  {
  	if(caret < thaiString.size()) {
   		int bt = 0;
   		do {
   			if(caret < thaiString.size() - 1) {
  				bt = (int)((Byte) thaiString.elementAt(caret + 1)).byteValue();
 				if(bt < 0) bt += 256;
 				if(bt >= 32) bt -= 32;
 			}
 			caret++;
 		} while(font_width[bt] <= 0 && caret < thaiString.size()); 				
  	}
  }
  
  /**
   *	Process move backward/
   */
  protected void moveCaretBackward()
  {
   	if(caret > 0) {
   		int bt;
   		do {
  			bt = (int)((Byte) thaiString.elementAt(caret - 1)).byteValue();
 			if(bt < 0) bt += 256;
 			if(bt >= 32) bt -= 32;
 			caret--; 			
 		} while(font_width[bt] <= 0 && caret > 0);
  	}
  }
  
  /**
   *	Timer timeout callback
   */
  public void timerTimeout()
  {  	
  	try {
  		if(thaiString.size() >= bufferLimit) return;  		
	  	int tch= 0;
	  	int tcg = 0;
	  	int tcf = 0;
	  	int itcg = 0;
	  	int itcf = 0;
	  	boolean isSaraAm = false;
	  	switch(shown_map) 
	  	{
	  		case MAP_TH_LO:
	  			tch = map_th_lo[new_posi - 1];
	  			break;
	  		case MAP_TH_UP:
	  			tch = map_th_up[new_posi - 1];
	  			break;
	  		case MAP_EN_LO:
	  			tch = map_en_lo[new_posi - 1];
	  			break;
	  		case MAP_EN_UP:
	  			tch = map_en_up[new_posi - 1];
	  			break;
	  	};
	  	if(tch == 211) {
	  		isSaraAm = true;
	  		tch = 237;
	  	}
		tcg = (caret <= 0)?0:(int) (((Byte)thaiString.elementAt(caret - 1)).byteValue() + 256);		
		tcg = (tcg >= 256)?0:tcg;
		itcg = (tcg >= 208)?levtable[tcg - 208]:0;
		switch((tch > 208)?levtable[tch - 208]:0) { 		
	  		case 1:
	  		case 2:	
	  			if(caret <= 0) break;
	  			if(itcg == 1 || itcg == 2) { 
	  				thaiString.removeElementAt(caret - 1);
	  				thaiString.insertElementAt(new Byte((byte)(tch - 256)), caret - 1);  								
	  			} else if(itcg == 3) {
	  				tcf = (caret <= 1)?0:(int) (((Byte)thaiString.elementAt(caret - 2)).byteValue() + 256);
					itcf = (tcf >= 208)?levtable[tcf - 208]:0;
	  				if(itcf == 1 || itcf == 2) {
	  					thaiString.removeElementAt(caret - 2);
	  					thaiString.insertElementAt(new Byte((byte)(tch - 256)),caret - 2); 
	  				} else {
	  					thaiString.insertElementAt(new Byte((byte)(tch - 256)), caret - 1);  				
	  					caret++;
	  				}
	  			} else {
	  				thaiString.insertElementAt(new Byte((byte)(tch - 256)), caret);  	
	  				caret++;
	  			}
	  			if(isSaraAm) {
	  				thaiString.insertElementAt(new Byte((byte)-46), caret);  	
	  				caret++;
	  			}
	  			break;
	  		case 3:
	  			if(caret <= 0) break;
	  			if(itcg == 3) {
	  				thaiString.removeElementAt(caret - 1);
	  				thaiString.insertElementAt(new Byte((byte)(tch - 256)), caret - 1);  								
	  			} else {
	  				thaiString.insertElementAt(new Byte((byte)(tch - 256)), caret);  	
	  				caret++;
	  			}
	  			break;
	  		case 0:
	  		default:
	  			thaiString.insertElementAt(new Byte((byte)(tch - 256)), caret);    	
			   	caret++;
			   	if(isSaraAm) {
	  				thaiString.insertElementAt(new Byte((byte)-46), caret);  	
	  				caret++;
	  			}
	  	}
		new_posi = 0;
	  	repaint();
  	} catch(Exception ex) {
  		//ex.printStackTrace();
  	}
  }
  
  /**
   *	Load font from PNG file
   */
  protected void loadFont()
  {
    ThaiFont tf = new ThaiFont4();
    font_offset = tf.get_font_offset();
    font_offset_y = tf.get_font_offset_y();
    font_bbx = tf.get_font_bbx();
    font_width = tf.get_font_width();
    font_height = tf.get_font_height();
    font_top2bbx = tf.get_font_top2bbx();
    font_id = tf.get_font_id();
    String ffname = tf.get_font_filename();
    try {
     // For MIDP-compliant devices
      myFont=Image.createImage(ffname);
      // For Siemens Phone
      // myFont=com.siemens.mp.ui.Image.createImageWithoutScaling(ffname);
    }
    catch(Exception e) {
    }
  }
}
