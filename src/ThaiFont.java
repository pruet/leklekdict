/*
 * ThaiFont.java
 *
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
interface ThaiFont {
  public int [] get_font_offset();
  public int [] get_font_offset_y();
  public byte [] get_font_bbx();
  public byte [] get_font_width();
  public int get_font_height();
  public int get_font_top2bbx();
  public String get_font_filename();
  public String get_font_name();
  public String get_font_id();
}
