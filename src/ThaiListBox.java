/*
 * ThaiListBox.java
 *
 * Copyright (C) 2004,2005 Pruet Boonma <pruet@eng.cmu.ac.th>
 * Copyright (C) 2002 Vuthichai Ampornaramveth <vuthi@vuthi.com>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  US
 */

/**
 *  TODO:
 *  1. Add select by tap for UIQ (required callback)
 *  2. Add select by fire for S60 (required callback)
 */
import javax.microedition.lcdui.*;

public class ThaiListBox extends Canvas
{
// String buffered to be display
  protected byte [][] thaiString;
  protected int thaiLen = 0;
  protected int screenwidth = 0;
  protected int screenheight = 0;
  protected int maxwidth = 0;
  protected int maxheight = 0;
  protected int direction = 0;

  protected int font_offset [] = null;
  protected int font_offset_y [] = null;
  protected byte font_bbx[] = null;
  protected byte font_width[] = null;
  protected int font_height = 0;
  protected int font_top2bbx = 0;
  protected int max_entry = 0;
  protected int screen_entry = 0;
  protected int hi_entry;
  protected int lo_entry;
  private static Image myFont = null;       // Used Fon
  private int new_posi = 1;
  private int old_posi = 0;
  private boolean refresh = false;
  private LekLekDict midlet = null;
  

// Constructor
  public ThaiListBox(LekLekDict mid)
  {
	midlet = mid;
    try {
		ThaiFont tf = new ThaiFont4();
		font_offset = tf.get_font_offset();
		font_offset_y = tf.get_font_offset_y();
		font_bbx = tf.get_font_bbx();
		font_width = tf.get_font_width();
		font_height = tf.get_font_height();
		font_top2bbx = tf.get_font_top2bbx();
		String ffname = tf.get_font_filename();
		myFont=Image.createImage(ffname);
    }
    catch(Exception e) {
    }
  }



// Main method for displaying thai test, the idea is, we put the thai string to the buffer (thaiString), then refresh the screen, Thai rendering will be invoked from pain() method
  public void displayText(byte text[][])
  {
    thaiString = text;
    thaiLen = thaiString.length;
	new_posi = 0;
	old_posi = 0;
	screenwidth = this.getWidth();
	screenheight = this.getHeight();
	maxwidth = screenwidth - 5;
	maxheight = screenheight;
	screen_entry = screenheight/font_height;
	hi_entry = screen_entry - 1;
	lo_entry = 0;
    repaint();
  }

  public void refresh()
  {
  	refresh = true;
	repaint();
  }

// Font Rendering Mechanism
  void drawThai(Graphics g, int x, int y)
  {     
    int c,d,e,f,table_offset, bbx_offset, draw_x, draw_y;
    //int[] buffer;
    int eachlinelen;

	int i;
    int l = lo_entry;
    max_entry = thaiString.length - 1;
	if(max_entry < hi_entry) hi_entry = max_entry;
    while(l <= hi_entry) {
    	eachlinelen = thaiString[l].length;	
    	i = 0;
		x = 2;
    	while(i < eachlinelen && x < maxwidth) {
	        e = thaiString[l][i]; 
	        if(e < 0) e+= 256;     
	        if(e >= 32 || e == 10) {
				e-= 32;
				if(e > 0) {    
					table_offset=e; // find table offset from ThaiFontX class
					bbx_offset=table_offset << 2; // from ThaiFontX class as well                       
					draw_x=x+font_bbx[bbx_offset+2];  // find the drawing origin in x
					draw_y=y+font_top2bbx-font_bbx[bbx_offset+3]-font_bbx[bbx_offset+1];  // find the drawing origin in y
					g.setClip(draw_x,draw_y,font_bbx[bbx_offset],font_bbx[bbx_offset+1]); // define the drawing area in the screen (to make sure that it will not overlap with other area
					g.drawImage(myFont,draw_x-font_offset[table_offset],draw_y-font_offset_y[table_offset],Graphics.LEFT|Graphics.TOP); // copy a particular character from .PNG file and put it to screen.
					x += font_width[e];
				} else {
					x+=font_width[0];
				}
	        }
	        i++;
		}
        y+=font_height;
        if(y>screenheight) break;
	    l++;
    }
  }

  /**
   *	Draw selected area
   */
  protected void drawRec(Graphics g)
  {	
  	//if(new_posi != 0) {
		int i = font_height * (old_posi);
		g.setColor(255, 255, 255);
		g.setClip(0, i, maxwidth + 1, font_height + 1);
		g.drawRect(0, i, maxwidth - 1, font_height);
		i = font_height * (new_posi);
		g.setColor(255, 0, 0);
		g.setClip(0, i, maxwidth + 1, font_height + 1);
		g.drawRect(0, i, maxwidth - 1, font_height);
  	//}
  }

  /**
   * 	Get selected entry
   */
  public int getSelectedEntry()
  {
	  return new_posi + lo_entry;
  }

  /**
   * 	Get selected entry
   */
  public byte[] getSelectedEntryText()
  {
	  return thaiString[new_posi + lo_entry];
  }
  
  /** Required paint implementation */
  // paint() method, clear the screen then call drawThai() method for rendering Thai text
  protected void paint(Graphics g)
  {
    if(old_posi == 0 || refresh) {
		refresh = false;
		g.setColor((255<<16)+(255<<8)+255);
		g.fillRect(0,0,screenwidth, screenheight + 1);
	   	drawThai(g,0,0);
	   	g.setClip(maxwidth, 0, 4, screenheight);
	   	g.setColor(255, 0, 0);
		if(hi_entry > 0) {
            if(hi_entry == lo_entry) {
                g.drawRect(maxwidth + 1, (lo_entry - 1)*screenheight/max_entry, 2, (screenheight/max_entry));
            } else {
                g.drawRect(maxwidth + 1, lo_entry*screenheight/max_entry, 2, ((hi_entry - lo_entry)*screenheight/max_entry));
            }
		} else {
			g.drawRect(maxwidth + 1, 0 , 2, screenheight);
		}
	}
    drawRec(g);
  }

// Keypress handle, you know what it is.
  protected void keyPressed(int keyCode)
  {
    if (getGameAction(keyCode) == UP) {
        //direction = 1;
        if(new_posi + lo_entry > 0) {
			if(new_posi == 0) {
				old_posi = 0;
				hi_entry = lo_entry - 1;
				lo_entry = lo_entry - screen_entry;
				if(lo_entry < 0) lo_entry = 0;
				new_posi = screen_entry - 1;
			} else {
				old_posi = new_posi;
				new_posi--;
			}
		}
    } else if (getGameAction(keyCode) == DOWN) {
        //direction = -1;
        if(new_posi + lo_entry < max_entry) {
			if(new_posi + lo_entry == hi_entry) {
				old_posi = 0;
				lo_entry = hi_entry + 1;
				hi_entry = hi_entry + screen_entry;
				if(hi_entry > max_entry) hi_entry = max_entry;
				new_posi = 0;
			} else {
				old_posi = new_posi;
				new_posi++;
			}
		}
    } else if(getGameAction(keyCode) == RIGHT) {
		midlet.ThaiListBoxCommandActionCallBack(RIGHT);
	} else if(getGameAction(keyCode) == LEFT) {
		midlet.ThaiListBoxCommandActionCallBack(LEFT);
	}
  	repaint();
  }

  protected void keyReleased(int keyCode)
  {

  }
}
