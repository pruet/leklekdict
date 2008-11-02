// $Id: LekLekDict.java,v 1.6 2008/11/02 06:28:19 pruet Exp $
// Copyright (C) 2003,2004,2005 Pruet Boonma 
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


/**
 *	@author	Pruet Boonma <pruetboonma@gmail.com>
 *	@version 0.3.2, Oct 2004
 */


// Standard MIDP library, please
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import java.io.*;
import java.util.*;

public class LekLekDict extends MIDlet implements CommandListener, ThaiListBoxCBInf, ThaiDisplayCBInf
{
  protected Display display;
  protected ThaiDisplay thaidisplay;
  protected Setting setting;
  protected ThaiPickBoard thaipickboard;
  protected ThaiListBox thailistbox;
  protected Displayable prevDisplay;

  protected Command exitCommand;

  protected Form findForm;
  protected TextField findTextField;
  protected Command submitCommand;
  protected Command submitPrefixCommand;
  protected Command aboutCommand;
  protected Command clearCommand;
  protected Command helpCommand;
  protected Command backCommand;
  protected Command setupCommand;
  protected Command switchIMCommand;
  protected Command switchViewCommand;
  protected ChoiceGroup cg;
  
  protected Form setupForm;
  protected TextField timeoutTextField;
  protected ChoiceGroup keyTypeChoiceGroup;
  protected ChoiceGroup showKeyHintChoiceGroup;

  protected TextBox thaitextbox;
  
  protected String tempString;
  private byte[] tempByteArray;
  
  public static final String TH_PREFIX = "te";
  public static final String EN_PREFIX = "et";

  public static final int IM_TEXTFIELD = 0;
  public static final int IM_PICKBOARD = 1;

  public static final int VIEW_TDIS = 0;

  public static final int VIEW_TBOX = 1;
  public LekLekDict()
  {
  	display = Display.getDisplay(this);
	setting = new Setting("LekLekDict");
	createUI();
  }
  
  protected String directSearch(String word) throws Exception
  {
	StringBuffer out = new StringBuffer();
	char ch = word.charAt(0);
	char chx = ch;
	  int chi;
	  if(ch > 255) ch -= 3424; // what da heck -_-''' unicode in 2bytes then rip down to 1 byte?
	int i;
	byte[] bt;
	byte[] searchbt;
	boolean found = false;
	String prefix;

	if((int)ch > 128) {
		prefix = TH_PREFIX;
	} else {
		prefix = EN_PREFIX;
	}
	InputStream indexIs = getClass().getResourceAsStream(prefix + "i" + (int)ch + ".t");
	InputStream dataIs = getClass().getResourceAsStream(prefix + "d" + (int)ch + ".t");
	if(indexIs == null || dataIs == null) return null;
	searchbt = ByteArray.convertFromSaraUm(ByteArray.convertFromString(word));
	while(true) {
		bt = getLine(indexIs, true); // it's O(n^2) here, anyway we need to reserve our memory so we have to load line by line.
		if(bt == null) break;
		chi = bt[0];
		if(chi < 0) chi += 256;
		if(chi != ch) break;
		i = ByteArray.indexOf(bt, '|');
		if(i == -1) break;
		
		if(ByteArray.equals(bt, searchbt, i)) {
			if(!found) {
				dataIs.skip(Integer.parseInt(new String(ByteArray.substring(bt, i + 1, bt.length))));
			}
			out.append(new String(getLine(dataIs, true)));
			out.append("\n");
			found = true;
		} else if(found) {
			break;
		}
	}
	indexIs.close();
	dataIs.close();
	if(out.length() <= 0) return null;
	out.insert(0, "[" + new String(searchbt) + "]\n");
	word = null;
	bt = null;
	dataIs = null;
	indexIs = null;
	return out.toString();
  }
	
  protected byte[][] prefixSearch(String word) throws Exception
  {
  	Vector out = new Vector();
	char ch = word.charAt(0);
	  int chi;
	  if(ch > 255) ch -= 3424; // what da heck -_-''' unicode in 2bytes then rip down to 2 bit?
	int i;
	byte[] bt;
	byte[] searchbt;
	boolean found = false;
	int maxSize = 0;
	String temp;
	String prefix;

	if((int)ch > 128) {
		prefix = TH_PREFIX;
	} else {
		prefix = EN_PREFIX;
	}
	InputStream indexIs = getClass().getResourceAsStream(prefix + "i" + (int)ch + ".t");
	if(indexIs == null) return null;
	searchbt = ByteArray.convertFromSaraUm(ByteArray.convertFromString(word));
	while(true) {
		bt = getLine(indexIs, true); // it's O(n^2) here, anyway we need to reserve our memory so we have to load line by line.
		if(bt == null) break;
		if(maxSize < bt.length) {
			maxSize = bt.length;
		}
		chi = bt[0];
		if(chi < 0) chi += 256;
		if(chi != ch) break;
		i = ByteArray.indexOf(bt, '|');
		if(i == -1) break;
		if(ByteArray.startsWith(bt, searchbt)) {
			temp = new String(ByteArray.substring(bt, 0, i));
			//temp = ByteArray.substring(bt, 0, i);
			if(out.isEmpty() || !out.lastElement().equals(temp)) {
				//if(!out.contains(temp)) {
				out.addElement(temp);
			}
			found = true;
		} else if(found) {
			break;
		}
	}
	indexIs.close();
	word = null;
	bt = null;
	indexIs = null;

	int len = out.size();
	if(len <= 0) return null;
	byte[][] outByte;
	outByte = new byte[len][maxSize];
	String str;
	for(i = 0; i != len; i++) {
		outByte[i] = ((String)out.elementAt(i)).getBytes();
		//outByte[i] = (byte [])out.elementAt(i);
	}	
	return outByte;
  }

  protected byte[] getLine(InputStream is, boolean oneLine) throws Exception
  {
	  int actual;
	  int size = 128;
	  int count = 0;
	  byte[] buffer = new byte[size];
	  byte[] buf1;
	  int i;
	  while(true) {
		actual = is.read();
		if((oneLine && actual == '\n') || actual == -1)
		  	break;
		if(count >= size) {
			size = count * 2;
			buf1 = new byte[size];
			for(i = 0; i != count; i++) {
				buf1[i] = buffer[i];
			}
			buffer = null;
			buffer = buf1;
		}
		buffer[count] = (byte)actual;
		count++;
	  }
	  if(count > 0) {
	  	buf1 = new byte[count];
	  	for(i = 0; i != count; i++ ) {
	  		buf1[i] = buffer[i];
	  	}
		buffer = null;
	  	return buf1;
	  }
	  return null;
  } 
  

  protected void createUI()
  {
  	findForm = new Form("LekLekDict");
  	setupForm = new Form("Setup");
	thaidisplay = new ThaiDisplay(this);
	thaipickboard = new ThaiPickBoard();
	thailistbox = new ThaiListBox(this);
	thaitextbox = new TextBox("LekLekDict", "", 1024, TextField.ANY);
	prevDisplay = null;
	
  	// All Command Button
    exitCommand = new Command("Exit", Command.EXIT, 10);
	backCommand = new Command("Back", Command.BACK, 1);
	submitCommand = new Command("Lookup..", Command.SCREEN, 1);
	submitPrefixCommand = new Command("Prefix Lookup..", Command.SCREEN, 2);
	clearCommand = new Command("Clear", Command.SCREEN, 3);
	switchIMCommand = new Command("Switch Input", Command.SCREEN, 4);
	switchViewCommand = new Command("Switch View", Command.SCREEN, 5);
	aboutCommand = new Command("About", Command.SCREEN, 6);
	setupCommand = new Command("Setup", Command.SCREEN, 7);
	helpCommand = new Command("Help", Command.HELP, 8);


  	// Eng->Thai From
  	findTextField = new TextField("Input search word(Th/Eng)", "", 30, TextField.ANY);
	findForm.append(findTextField);	

	findForm.addCommand(submitCommand);
	findForm.addCommand(submitPrefixCommand);
	findForm.addCommand(clearCommand);
	findForm.addCommand(setupCommand);
	findForm.addCommand(aboutCommand);
	findForm.addCommand(helpCommand);
	findForm.addCommand(exitCommand);
	findForm.addCommand(switchIMCommand);
    findForm.setCommandListener(this);
	
	// Setup Form
	String[] key_types = {"QWERTY", "ABC-VERT", "ABC-HORI"};
	keyTypeChoiceGroup = new ChoiceGroup("Keyboard Type", ChoiceGroup.EXCLUSIVE, key_types, null );
	String keylayout = setting.get("keytype");
	int keyi = 0;
	if(keylayout != null) {
		keyi = Integer.parseInt(keylayout);
	} else {
		setting.set("keytype", "" + keyi);
		setting.get("keytype");
	}
	keyTypeChoiceGroup.setSelectedIndex(keyi, true);
	
	String[] show_hint = {"YES", "NO"};
	showKeyHintChoiceGroup = new ChoiceGroup("Show Key Hint", ChoiceGroup.EXCLUSIVE, show_hint, null);
	String showhint = setting.get("showhint");
	int showk = 0;
	if(showhint != null) {
		showk = Integer.parseInt(showhint);
	} else {
		setting.set("showhint", "" + showk);
		setting.get("showhint");
	}
	showKeyHintChoiceGroup.setSelectedIndex(showk, true);

	String timeout = setting.get("keytimeout");
	if(timeout == null) {
		timeout = "" + thaipickboard.getKeyRepeatTimeout();
		setting.set("keytimeout", timeout );
	}
	timeoutTextField = new TextField("Key Timeout", timeout, 10, TextField.NUMERIC);
	
	setupForm.addCommand(backCommand);
	setupForm.setCommandListener(this);
	setupForm.append(keyTypeChoiceGroup);
	setupForm.append(timeoutTextField);
	setupForm.append(showKeyHintChoiceGroup);
	
	thaidisplay.addCommand(backCommand);
	thaidisplay.addCommand(switchViewCommand);
	thaidisplay.setCommandListener(this);

	thaitextbox.addCommand(backCommand);
	thaitextbox.addCommand(switchViewCommand);
	thaitextbox.setCommandListener(this);
	
	thaipickboard.addCommand(submitCommand);
	thaipickboard.addCommand(submitPrefixCommand);
	thaipickboard.addCommand(clearCommand);
	thaipickboard.addCommand(setupCommand);
	thaipickboard.addCommand(aboutCommand);
	thaipickboard.addCommand(helpCommand);
	thaipickboard.addCommand(switchIMCommand);
	thaipickboard.addCommand(exitCommand);
	thaipickboard.setCommandListener(this);

	String il = setting.get("input_lang");
	if(il == null) {
		setting.set("input_lang", "" + ThaiPickBoard.KEY_ENG);
	}

	thailistbox.addCommand(submitCommand);
	thailistbox.addCommand(backCommand);
	thailistbox.addCommand(exitCommand);
	thailistbox.setCommandListener(this);

	thaipickboard.setKeyboardLayout(Integer.parseInt(setting.get("keytype")));
	thaipickboard.setShowKeyHint(Integer.parseInt(setting.get("showhint")));
	thaipickboard.setKeyRepeatTimeout(Integer.parseInt(setting.get("keytimeout")));
	thaipickboard.switchLanguage(Integer.parseInt(setting.get("input_lang")));
	thaipickboard.reset();

	String vm = setting.get("view_method");
	if(vm == null) {
		setting.set("view_method", "" + VIEW_TDIS);
	}
	String im = setting.get("input_method");
	if(im == null) {
		setting.set("input_method", "" + IM_TEXTFIELD);	
	}
	if(Integer.parseInt(setting.get("input_method")) == IM_TEXTFIELD) {
		display.setCurrent(findForm); 
	} else {
		display.setCurrent(thaipickboard);
	}
  }
 
  protected void displayThaiText(byte[] tb)
  {
	int l = tb.length;
	tempByteArray = new byte[l];
	for(int i = 0; i != l; i++) {
		tempByteArray[i] = tb[i];
	}
	switchThaiDisplay();
  }
  private void switchThaiDisplay()
  {
	if(Integer.parseInt(setting.get("view_method")) == VIEW_TDIS) {
		thaidisplay.displayText(ByteArray.convertFromSaraUm(tempByteArray));
		display.setCurrent(thaidisplay); 
	} else {
		if(tempByteArray.length > thaitextbox.getMaxSize()) thaitextbox.setMaxSize(tempByteArray.length + 10);
		thaitextbox.setString(new String(ByteArray.convertToChars(ByteArray.convertToSaraUm(tempByteArray))).replace('|', (char)0));
		display.setCurrent(thaitextbox);
	}
 	
  }
  
  public void commandAction(Command command, Displayable displayable)
  {
	try {
		if (command.getCommandType() == Command.EXIT) {
			setting.set("input_lang", "" + thaipickboard.getActiveLanguage());
			closeApp(false);
		} else if(command == switchIMCommand) {
			if(Integer.parseInt(setting.get("input_method")) == IM_TEXTFIELD) {
				setting.set("input_method", "" + IM_PICKBOARD);
				thaipickboard.switchLanguage(Integer.parseInt(setting.get("input_lang")));
				thaipickboard.reset(ByteArray.convertFromSaraUm(ByteArray.convertFromString(findTextField.getString())));
				display.setCurrent(thaipickboard);
			} else {
				setting.set("input_method", "" + IM_TEXTFIELD);
				findTextField.setString(ByteArray.convertToString(ByteArray.convertToSaraUm(thaipickboard.getBytes())));
				display.setCurrent(findForm);
			}
			//setting.get("input_method");
		} else if(command == aboutCommand) {
			InputStream is = getClass().getResourceAsStream("/about.txt");
  			String about = new String(getLine(is, false));
			StringBuffer sb = new StringBuffer();
			String version;
			String copyright;
			if((version = getAppProperty("LekLekDict-Version")) == null) {
				version = new String("0.3.4");
			}
			if((copyright = getAppProperty("Copyright")) == null) {
				copyright = new String("(C) 2008 ANS Wireless Co., Ltd.\n(C) 2003,2004,2005 Pruet Boonma\n");
			}
			sb.append("* Lek-Lek Dict " + version + "\nCopyright " + copyright + " All Right Reserved. GPL Applied\n");
			sb.append("* ThaiFontDisplay Copyright (C) 2002 Vuthichai Ampornaramveth\n");
			prevDisplay = null;
			sb.append(about);
			displayThaiText(sb.toString().getBytes());
		} else if(command == helpCommand) {
			InputStream is = getClass().getResourceAsStream("/help.txt");
  			String str = new String(getLine(is, false));
			prevDisplay = null;
			displayThaiText(str.getBytes());
			str = null;
		 } else if(command == clearCommand) {
			findTextField.setString(new String(""));
			thaipickboard.reset();
		 }
		if(displayable == findForm) {
			if(command == submitCommand || command.getCommandType() == Command.OK) {
				String search = findTextField.getString().toLowerCase().trim();
				if(search.length() > 0) {
					findTextField.setString(new String("Searching..."));
					String str = directSearch(search);
					if(str == null) {
						Alert al = new Alert("Error", "Cannot find the meaning of the search word", null, AlertType.WARNING);
						al.setTimeout(Alert.FOREVER);
						display.setCurrent(al);
					} else {
						prevDisplay = findForm;
						displayThaiText(str.getBytes());
						str = null;
					}
					findTextField.setString(search);
					search = null;
				}
			 } else if(command == submitPrefixCommand) {
			 	String search = findTextField.getString().toLowerCase().trim();
				if(search != null && search.length() > 0) {
					byte result[][] = prefixSearch(search);
					if(result == null || result.length <= 0) {
						Alert al = new Alert("Error", "Cannot find any words start with the search word .", null, AlertType.WARNING);
						al.setTimeout(Alert.FOREVER);
						display.setCurrent(al);
					} else {
						thailistbox.displayText(result);
						prevDisplay = findForm;
						display.setCurrent(thailistbox);
					}
				}
			 } else if(command == setupCommand) {
			 	display.setCurrent(setupForm);
			 }
		} else if(displayable == thaidisplay) {
			if (command == backCommand) {
				BackFromThaiDisplay();
			} else if(command == switchViewCommand) {
				setting.set("view_method", "" + VIEW_TBOX);
				//displayThaiText(thaidisplay.getBytes());
				switchThaiDisplay();
			}
		} else if(displayable == thaitextbox) {
			if (command == backCommand) {
				if(prevDisplay != null) {
					if(prevDisplay == thailistbox) thailistbox.refresh();
					display.setCurrent(prevDisplay);
					prevDisplay = null;
				} else {
					if(Integer.parseInt(setting.get("input_method")) == IM_TEXTFIELD) {
						display.setCurrent(findForm);
					} else {
						thaipickboard.switchLanguage(Integer.parseInt(setting.get("input_lang")));
						display.setCurrent(thaipickboard);
					}
				}
			} else if(command == switchViewCommand) {
				setting.set("view_method", "" + VIEW_TDIS);
				switchThaiDisplay();
			}
		} else if(displayable == thaipickboard) {	
  			if(command == submitCommand) {
  				byte[] search = thaipickboard.getBytes();
				if(search != null && search.length > 0) {
					setting.set("input_lang", "" + thaipickboard.getActiveLanguage());
	  				String str = directSearch(ByteArray.convertToString(ByteArray.convertToSaraUm(thaipickboard.getBytes())));
					if(str == null) {
						Alert al = new Alert("Error", "Cannot find the meaning of the search word.", null, AlertType.WARNING);
						al.setTimeout(Alert.FOREVER);
						display.setCurrent(al);
					} else {
						prevDisplay = thaipickboard;
						displayThaiText(str.getBytes());
						str = null;
					}
				}
			} else if(command == submitPrefixCommand) {
  				byte[] search = thaipickboard.getBytes();
				if(search != null && search.length > 0) {
					byte result[][] = prefixSearch(ByteArray.convertToString(ByteArray.convertToSaraUm(search)));
					setting.set("input_lang", "" + thaipickboard.getActiveLanguage());
					if(result == null || result.length <= 0) {
						Alert al = new Alert("Error", "Cannot find any words start with the search word .", null, AlertType.WARNING);
						al.setTimeout(Alert.FOREVER);
						display.setCurrent(al);
					} else {
						thailistbox.displayText(result);
						display.setCurrent(thailistbox);
					}
				}
  			} else if(command == setupCommand) {
			 	display.setCurrent(setupForm);
			}	
		} else if(displayable == setupForm) {
			if(command == backCommand) {
				setting.set("keytype", Integer.toString(keyTypeChoiceGroup.getSelectedIndex()));
				setting.set("showhint", Integer.toString(showKeyHintChoiceGroup.getSelectedIndex()));
				setting.set("keytimeout", timeoutTextField.getString());
				thaipickboard.setKeyboardLayout(Integer.parseInt(setting.get("keytype")));
				thaipickboard.setShowKeyHint(Integer.parseInt(setting.get("showhint")));
				thaipickboard.setKeyRepeatTimeout(Integer.parseInt(setting.get("keytimeout")));
				thaipickboard.switchLanguage(Integer.parseInt(setting.get("input_lang")));
				thaipickboard.reset();
				if(Integer.parseInt(setting.get("input_method")) == IM_TEXTFIELD) {
					display.setCurrent(findForm);
				} else {
					display.setCurrent(thaipickboard);
				}
			}
		} else if(displayable == thailistbox) {
			if(command == submitCommand) {
				searchFromListBox();
			} else if(command == backCommand) {
				BackFromThaiListbox();
			}
		}
	} catch(Exception e) {
		e.printStackTrace();
		Alert al = new Alert("Exception", e.getMessage(), null, AlertType.ERROR);
		al.setTimeout(Alert.FOREVER);
		display.setCurrent(al);
	}
  } 


  public void closeApp(boolean con)
  {
	  destroyApp(con);
	  notifyDestroyed();
  }
  
  public void startApp()
  {
  	// should we implement threading ? for pause/start buz
  }

  public void pauseApp()
  {
  }
  
  public void destroyApp(boolean con)
  {
  }
  
  public void ThaiListBoxCommandActionCallBack(int cmd)
  {
	if(cmd == Canvas.RIGHT) {
		try {
			searchFromListBox();
		} catch (Exception ex) {
		}
	} else {
		BackFromThaiListbox();
	}
  }
  
  public void ThaiDisplayCommandActionCallBack()
  {
	BackFromThaiDisplay();
  }
  
  public void BackFromThaiDisplay()
  {
  	if(prevDisplay != null) {
		if(prevDisplay == thailistbox) thailistbox.refresh();
		display.setCurrent(prevDisplay);
		prevDisplay = null;
	} else {
		if(Integer.parseInt(setting.get("input_method")) == IM_TEXTFIELD) {
			display.setCurrent(findForm);
		} else {
			thaipickboard.switchLanguage(Integer.parseInt(setting.get("input_lang")));
			display.setCurrent(thaipickboard);
		}
	}
  }
  public void BackFromThaiListbox()
  {
	if(Integer.parseInt(setting.get("input_method")) == IM_TEXTFIELD) {
		display.setCurrent(findForm);
	} else {
		thaipickboard.switchLanguage(Integer.parseInt(setting.get("input_lang")));
		display.setCurrent(thaipickboard);
	}
  }
  protected void searchFromListBox() throws Exception
  {
	  byte[] search_orig = thailistbox.getSelectedEntryText();
	  byte[] search = ByteArray.substring(search_orig, 0, search_orig.length);
	  if(search != null && search.length > 0) {
		  String str = directSearch(ByteArray.convertToString(ByteArray.convertToSaraUm(search)));
		  if(str == null) {
			Alert al = new Alert("Error", "Cannot find the meaning of the search word.", null, AlertType.WARNING);
			al.setTimeout(Alert.FOREVER);
			display.setCurrent(al);
		} else {
			prevDisplay = thailistbox;
			displayThaiText(str.getBytes());
			str = null;
		}
	}
  }
}
