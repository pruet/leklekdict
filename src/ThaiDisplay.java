// $Id: ThaiDisplay.java,v 1.3 2008/10/21 21:20:22 pruet Exp $
// Copyright (C) 2002 Vuthichai Ampornaramveth
// Copyright (C) 2003, 2004 Pruet Boonma <pruetboonma@gmail.com>
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

// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  US

import javax.microedition.lcdui.*;

public class ThaiDisplay extends Canvas
{
// String buffered to be display
  byte [] thaiString;
  int thaiLen = 0;
  int screenwidth = 0;
  int screenheight = 0;
  int maxwidth = 0;
  int maxheight = 0;
  int direction = 0;
  int sy = 0;

  int font_offset [] = null;
  int font_offset_y [] = null;
  byte font_bbx[] = null;
  byte font_width[] = null;
  int font_height = 0;
  int font_top2bbx = 0;
  int[] buffer;
  String font_id = null;
  private static Image myFont = null;       // Used Fon
  private LekLekDict midlet = null;

// Constructor
  public ThaiDisplay(LekLekDict mid)
  {
	midlet = mid;
    try {
      loadFont(1);
    }
    catch(Exception e) {
    }
  }


// Main method for displaying thai test, the idea is, we put the thai string to the buffer (thaiString), then refresh the screen, Thai rendering will be invoked from pain() method
  public void displayText(byte text[])
  {
  	sy = 0;
	screenwidth = this.getWidth();
	screenheight = this.getHeight();
	maxwidth = screenwidth - 5;
	maxheight = screenheight;
	int len = text.length;
	if(len <= 0) return;
  	if(text[len - 1] != '\n') {
  		byte[] temp = new byte[len + 1];
  		for(int i = 0; i != len; i++) {
  			temp[i] = text[i];
  		}
  		temp[text.length] = '\n';
  		thaiString = temp;
  	} else {
	  thaiString = text;
  	}
	thaiLen = thaiString.length;
    buffer = new int[thaiString.length];
	repaint();
  }

  public byte[] getBytes()
  {
  	return thaiString;
  }

// Font Rendering Mechanism
  void drawThai(Graphics g, int len, int x, int y)
  {  	
    int c,d,e,f,table_offset, bbx_offset, draw_x, draw_y;

	if(direction == -1) { // DOWN pressed, move the screen up
		if((screenheight - sy) < maxheight) {
			int shift;
			sy = sy - ((int) (screenheight / font_height)) * font_height;
		}
		direction = 0;
	} else if(direction == 1) { // UP pressed, move the screen down.
		if(sy < 0) {
			sy = sy + ((int) (screenheight / font_height)) * font_height;
			if(sy > 0) sy = 0;
		}
		direction = 0;
	}
	g.translate(0, sy);

    int i = 0;
    int j = 0;
    int k = 0;
    while(i < len) {
    	e = thaiString[i];
    	if(e < 0) e+= 256;   	
      	if(e >= 32 || e == 10) {
      		if(e == 124) {
      			k = i;
      		} else {
      			e-= 32;
      			if(e >= 0) {    
      				if(e == 0) k = i;  			
      				buffer[j] = e;
      				x += font_width[e];
      				j++;
      			} 
      		}
      		if(x >= maxwidth - 5 || e == -22) {
      			k = (e == -22 || k == 0)?i:k;
  				x = 0;
  				j += k - i;
  				for(int l = 0; l !=  j; l++) {    				  										
  					e = buffer[l];
  					if(e > 0) { 							        
				        table_offset=e; // find table offset from ThaiFontX class
				        bbx_offset=table_offset << 2; // from ThaiFontX class as well				        
				        draw_x=x+font_bbx[bbx_offset+2];  // find the drawing origin in x
				        draw_y=y+font_top2bbx-font_bbx[bbx_offset+3]-font_bbx[bbx_offset+1];  // find the drawing origin in y
				        g.setClip(draw_x,draw_y,font_bbx[bbx_offset],font_bbx[bbx_offset+1]); // define the drawing area in the screen (to make sure that it will not overlap with other area
				        g.drawImage(myFont,draw_x-font_offset[table_offset],draw_y-font_offset_y[table_offset],Graphics.LEFT|Graphics.TOP); // copy a particular character from .PNG file and put it to screen.
				        x+=font_width[table_offset]; // move the cursor			        
				    } else {
				    	x+=font_width[0];
				    }
  				}
  				i = k;
  				k = 0;
				x = 0;
				j = 0;
				y+=font_height;
				if(y>screenheight)
				maxheight = y;
      		}
      	}
      	i++;
    }
	//buffer = null;
  }


  /** Required paint implementation */
  // paint() method, clear the screen then call drawThai() method for rendering Thai text
  protected void paint(Graphics g)
  {
    g.setColor((255<<16)+(255<<8)+255);
    g.fillRect(0,0,screenwidth, screenheight);
	if(thaiString != null) drawThai(g, thaiLen, 0,0);
	g.translate(-g.getTranslateX(), -g.getTranslateY());
	g.setClip(0, 0, screenwidth + 2, screenheight + 2);
	g.setColor(255, 0, 0);
	g.drawRect(maxwidth + 1, (-sy*screenheight/maxheight), 2, (screenheight*screenheight/maxheight));
  }
  protected void pointerReleased(int x, int y)
  {
      if(y < screenheight/2) {
        direction = 1;
        repaint();
      } else {
        direction = -1;
        repaint();
      }
  }
// Keypress handle, you know what it is.
  protected void keyPressed(int keyCode)
  {
    if (keyCode==KEY_NUM1) {
		sy = 0;
		loadFont(1);
		screenwidth = this.getWidth();
		screenheight = this.getHeight();
		maxwidth = screenwidth - 5;
		maxheight = screenheight;
		repaint();
    } else if (keyCode==KEY_NUM2) {
		sy = 0;
		loadFont(0);
		screenwidth = this.getWidth();
		screenheight = this.getHeight();
		maxwidth = screenwidth - 5;
		maxheight = screenheight;
		repaint();
    } else if (getGameAction(keyCode) == UP) {
		direction = 1;
		repaint();
	} else if (getGameAction(keyCode) == DOWN) {
		direction = -1;
		repaint();
	} else if (getGameAction(keyCode) == LEFT) {
		midlet.ThaiDisplayCommandActionCallBack();
	}
  }

  protected void keyReleased(int keyCode)
  {

  }

// Font loading mechanism, font is in .png file format, with the respective ThaiFont sub-class.
  private void loadFont(int f)
  {
    ThaiFont tf = null;
    switch(f) {
      case 0: tf = new ThaiFont5(); break;
      case 1: tf = new ThaiFont4(); break;
    }

    font_offset = tf.get_font_offset();
    font_offset_y = tf.get_font_offset_y();
    font_bbx = tf.get_font_bbx();
    font_width = tf.get_font_width();
    font_height = tf.get_font_height();
    font_top2bbx = tf.get_font_top2bbx();
    font_id = tf.get_font_id();
    String ffname = tf.get_font_filename();
    try {
      myFont=Image.createImage(ffname);
    } catch(Exception e) {
    }
  }
}
