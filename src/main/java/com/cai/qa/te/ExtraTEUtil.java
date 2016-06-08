package com.cai.qa.te;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.cai.qa.core.Consts;
import com.cai.qa.core.GlobalObjectFile;
import com.cai.qa.core.Utility;
import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class ExtraTEUtil implements TEUtil {
	
	private static final Log log = LogFactory.getLog(ExtraTEUtil.class);
	
	private static ExtraTEUtil teObject = null;
	private Object systemObject = null;
	private Object sessionObject = null;
	private Object screenObject = null;
	
	public Object getScreenObject() {return screenObject;}
	public void setScreenObject(Object screenObject) {this.screenObject = screenObject;}
	public Object getSystemObject() {return systemObject;}
	public void setSystemObject(Object systemObject) {this.systemObject = systemObject;}
	public Object getSessionObject() {return sessionObject;}
	public void setSessionObject(Object sessionObject) {this.sessionObject = sessionObject;}
	
	private ExtraTEUtil(){}
	
	/**
	 * Get this Class Object
	 * @return this Class Object
	 */
	public static ExtraTEUtil getInstance(){
		if(teObject==null){teObject = new ExtraTEUtil();}
		return teObject;
	}
	
	/**
	 * Connect to Mainframe emulator and Session
	 * @return Connection status
	 */
	public boolean connectToServer(){
		try{
			ActiveXComponent shellObj = new ActiveXComponent("WScript.Shell");
			Object winShellObject = shellObj.getObject();
			
			
			String fileName = Utility.getClassPathResourcePath(Utility.getPropertyValue(Consts.EXTRA_TE_SESSION_NAME));
			Variant _3270Session = new Variant(fileName);
			
			Dispatch.call((Dispatch) winShellObject, "Run", _3270Session);
			try {Thread.sleep(5000);} catch (InterruptedException e){log.debug("connectToServer InterruptedException:"+e);}
			
			ActiveXComponent terminalSystem = new ActiveXComponent("EXTRA.System");
			setSystemObject(terminalSystem.getObject());
			log.debug("Terminal Server Version:" + terminalSystem.getProperty("Version"));
			try {Thread.sleep(5000);} catch (InterruptedException e){log.debug("connectToServer InterruptedException:"+e);}
			
			Object allSessions = Dispatch.get((Dispatch)getSystemObject(), "Sessions").toDispatch();
			@SuppressWarnings("deprecation")
			int sessionsCount = Dispatch.get((Dispatch)allSessions, "Count").toInt();
			if(sessionsCount==0){
				log.error("No Terminal Session found!");
				return false;
			}
			
			setSessionObject(Dispatch.get((Dispatch)getSystemObject(), "ActiveSession").toDispatch());
			log.debug("Terminal Server Active Session Name:" + Dispatch.get((Dispatch)getSessionObject(), "Name"));
			try {Thread.sleep(2000);} catch (InterruptedException e){log.debug("connectToServer InterruptedException:"+e);}
			
			setScreenObject((Object)Dispatch.get((Dispatch)getSessionObject(), "Screen").toDispatch());
		
		}catch(Exception e){
			log.debug("connectToServer Exception:"+e);
			return false;
		}
		return true;
	}
	
	/**
	 * Quit the session and Close Mainframe emulator
	 * @return Status
	 */
	public boolean disconnectFromServer(){
		try{
			Dispatch.call((Dispatch)getSessionObject(), "CloseEx", new Variant(1));
			try {Thread.sleep(2000);} catch (InterruptedException e){log.debug("disconnectFromServer InterruptedException:"+e);}
			Dispatch.call((Dispatch)getSystemObject(), "Quit");
			try {Thread.sleep(2000);} catch (InterruptedException e){log.debug("disconnectFromServer InterruptedException:"+e);}
		}catch(Exception e){
			log.debug("disconnectFromServer Exception:"+e);
			return false;
		}
		return true;
	}
	
	/**
	 * Write characters in Screen
	 * @param text
	 * @param rowNum
	 * @param colNum
	 */
	public void putString(String text, int rowNum, int colNum){
		try{
			if(text!=null&&!text.isEmpty()){
				Variant _text = new Variant(text);
				Variant _rowNum = new Variant(rowNum);
				Variant _colNum = new Variant(colNum);
				Dispatch.call((Dispatch)getScreenObject(), "PutString", _text, _rowNum, _colNum);
			}
		}catch(Exception e){log.debug("putString Exception:"+e);}
	}
	
	/**
	 * Read data from Screen
	 * @param rowNum
	 * @param colNum
	 * @param legth
	 * @return screen data
	 */
	public String getString(int rowNum, int colNum, int legth){
		try{
			Variant _rowNum = new Variant(rowNum);
			Variant _colNum = new Variant(colNum);
			Variant _legth = new Variant(legth);
			return Dispatch.call((Dispatch)getScreenObject(), "GetString", _rowNum, _colNum, _legth).toString();
		}catch(Exception e){log.debug("getString Exception:"+e); return null;}
	}
		
	/**
	 * Send keyboard keys to Screen
	 * @param keys
	 */
	public void sendKeys(String keys){
		try{
			if(keys!=null&&!keys.isEmpty()){
				Variant _keys = new Variant(keys);
				Dispatch.call((Dispatch)getScreenObject(), "SendKeys", _keys);
			}
		}catch(Exception e){log.debug("sendKeys Exception:"+e);}
	}
	
	/**
	 * Check if Text found in screen
	 * @param text
	 * @param rowNum
	 * @param colNum
	 * @param legth
	 * @return Status
	 */
	public boolean isTextFound(String text, int rowNum, int colNum, int legth){
		try{
			String screenText = getString(rowNum, colNum, legth);
			if((screenText!=null) && (screenText.indexOf(text)!=-1)){
				return true;
			}
		}catch(Exception e){log.debug("isTextFound Exception:"+e); return false;}
		return false;
	}
	
	/**
	 * Get TE Object from JSON file
	 * @param strFieldId
	 * @return TE Map Object
	 */
	public Map<String, String> getTEObject(String strFieldId){
		Map<String, String> teObject = GlobalObjectFile.getTEObject(strFieldId);
		if(teObject==null){
			log.debug("No Global Object found! "+strFieldId);
			return null;
		}
		log.debug("Global Object found! "+strFieldId);
		return teObject;
	}
	
	/**
	 * Write characters in Screen
	 * @param strFieldId
	 * @param text
	 */
	public void putString(String strFieldId, String text){
		Map<String, String> teObject = getTEObject(strFieldId);
		if(teObject!=null){
			try {
				putString(text, Integer.getInteger(teObject.get("row")), Integer.getInteger(teObject.get("column")));
			} catch (Exception e) {log.debug("putString Exception:"+e);}
		}
	}
	
	/**
	 * Get characters from Screen
	 * @param strFieldId
	 * @param legth
	 * @return String
	 */
	public String getString(String strFieldId, int legth){
		Map<String, String> teObject = getTEObject(strFieldId);
		if(teObject!=null){
			try {
				return getString(Integer.getInteger(teObject.get("row")), Integer.getInteger(teObject.get("column")), legth);
			} catch (Exception e) {log.debug("getString Exception:"+e);}
		}
		return null;
	}
	
	/**
	 * Check if Text found in screen
	 * @param strFieldId
	 * @param text
	 * @param legth
	 * @return true/false
	 */
	public boolean isTextFound(String strFieldId, String text, int legth){
		Map<String, String> teObject = getTEObject(strFieldId);
		if(teObject!=null){
			try {
				return isTextFound(text, Integer.getInteger(teObject.get("row")), Integer.getInteger(teObject.get("column")), legth);
			} catch (Exception e) {log.debug("isTextFound Exception:"+e);}
		}
		return false;
	}
	
	
	
}
