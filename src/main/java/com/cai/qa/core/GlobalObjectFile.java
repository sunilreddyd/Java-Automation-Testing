package com.cai.qa.core;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;

public class GlobalObjectFile {
	
	private static final Log log = LogFactory.getLog(GlobalObjectFile.class);
	
	private static String appGroupName;
	private static String pageScreenName;
	
	private static String getAppGroupName() {return appGroupName;}
	private static void setAppGroupName(String appGroupName) {GlobalObjectFile.appGroupName = appGroupName;}
	private static String getPageScreenName() {return pageScreenName;}
	private static void setPageScreenName(String pageScreenName) {GlobalObjectFile.pageScreenName = pageScreenName;}

	public static void setApplicationAndScreen(String appGroupName, String pageScreenName){
		if(appGroupName!=null && !appGroupName.isEmpty()){
			setAppGroupName(appGroupName);
		}
		if(pageScreenName!=null && !pageScreenName.isEmpty()){
			setPageScreenName(pageScreenName);
		}
	}
	
	private static JSONObject getGlobalObjectsFileDataAsJSONObject(){
		JSONParser jsonParser = new JSONParser();
		try {
			String fileName = Utility.getPropertyValue(Consts.GLOBALOBJECTS_FILENAME);
			JSONObject fileDataObject = (JSONObject)jsonParser.parse(new FileReader(Utility.getClassPathResourcePath(fileName)));
			return fileDataObject;
		} catch (IOException | ParseException e) {
			log.debug("getGlobalObjectsFileDataAsJSONObject - IOException | ParseException :"+e);
		}
		return null;
	}
	
	private static JSONObject getApplicaitonObjectsDataAsJSONObject(){
		
		JSONObject fileObject = getGlobalObjectsFileDataAsJSONObject();
		if(fileObject==null){return null;}
		try{
			JSONObject appObject = (JSONObject)fileObject.get(getAppGroupName());
			return appObject;
		}catch(Exception e){
			log.debug("getApplicaitonObjectsDataAsJSONObject - Exception :"+e);
		}
		return null;
	}
	
	private static JSONObject getPageOrScreenObjectsDataAsJSONObject(){
		
		JSONObject appObject = getApplicaitonObjectsDataAsJSONObject();
		if(appObject==null){return null;}
		try{
			JSONObject pageObject = (JSONObject)appObject.get(getPageScreenName());
			return pageObject;
		}catch(Exception e){
			log.debug("getPageOrScreenObjectsDataAsJSONObject - Exception :"+e);
		}
		return null;
	}
	
	public static By getWebObject(String strFieldId){
		
		if(strFieldId==null || strFieldId.isEmpty()){return null;}
		
		By objectProperties;
		JSONObject pageObject =getPageOrScreenObjectsDataAsJSONObject();
		if(pageObject==null){return null;}
		try{
			JSONObject webObject = (JSONObject)pageObject.get(strFieldId);
			String objectDescritpion = (String)webObject.get("description");
			String type = (String)webObject.get("type");
			switch(type.toLowerCase()){
				case "id":				objectProperties = By.id(objectDescritpion);break;
				case "name":			objectProperties = By.name(objectDescritpion);break;
				case "xpath":			objectProperties = By.xpath(objectDescritpion);break;
				case "cssselector":		objectProperties = By.cssSelector(objectDescritpion);break;
				case "className":		objectProperties = By.className(objectDescritpion);break;
				case "linktext":		objectProperties = By.linkText(objectDescritpion);break;
				case "partiallinktext":	objectProperties = By.partialLinkText(objectDescritpion);break;
				case "tagName":			objectProperties = By.tagName(objectDescritpion);break;
				default: 				objectProperties = null;
			}
			log.debug("getWebObject - Final Object:"+objectProperties.toString());
			return objectProperties;
		}catch(Exception e){
			log.debug("getWebObject - Exception :"+e);
		}
		return null;
	}
	
	public static Map<String,String> getTEObject(String strFieldId){
		
		if(strFieldId==null || strFieldId.isEmpty()){return null;}
		
		JSONObject screenObject =getPageOrScreenObjectsDataAsJSONObject();
		if(screenObject==null){return null;}

		Map<String,String> teObjectProperties = new HashMap<String, String>();
		
		try {
			JSONObject webObject = (JSONObject)screenObject.get(strFieldId);
			teObjectProperties.put("row", (String)webObject.get("row"));
			teObjectProperties.put("column", (String)webObject.get("column"));
			teObjectProperties.put("text", (String)webObject.get("text"));
			return teObjectProperties;
		} catch (Exception e) {log.debug("getTEObject - Exception :"+e);}
		
		return null;
	}

}
