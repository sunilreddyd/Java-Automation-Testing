package com.cai.qa.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

import com.google.common.io.Resources;

public class Utility {

	private static final Log log = LogFactory.getLog(Utility.class);
	private static Properties properties = new Properties();
	private static boolean loadCAIFramework = true;
	
	/**
	 * Load Required Objects and files for CAI framework
	 */
	public static void loadCAIFramework(List<String> resources){
		if(loadCAIFramework){
			loadClassPathFiles(resources);
			List<String> fileNames = new ArrayList<String>();
			fileNames.add("CAIFramework.properties");
			loadMultiplePropertyFilesFromClassPath(fileNames);
			List<String> listOfFiles = new ArrayList<String>();
			listOfFiles.add(getPropertyValue(Consts.APPLICATION_ENVIRONMENT_FILENAME));
			loadMultiplePropertyFilesFromClassPath(listOfFiles);
			setLogLevel();
			loadCAIFramework = false;
		}
	}
	
	public static void setLogLevel(){
		String logLevel = getPropertyValue(Consts.LOG_LEVEL);
		if(logLevel.equalsIgnoreCase("DEBUG")){
			Logger.getRootLogger().setLevel(Level.DEBUG);
		}else if(logLevel.equalsIgnoreCase("ALL")){
			Logger.getRootLogger().setLevel(Level.ALL);
		}else if(logLevel.equalsIgnoreCase("ERROR")){
			Logger.getRootLogger().setLevel(Level.ERROR);
		}else if(logLevel.equalsIgnoreCase("FATAL")){
			Logger.getRootLogger().setLevel(Level.FATAL);
		}else{
			Logger.getRootLogger().setLevel(Level.INFO);
		}
		  //Logger.getRootLogger().setLevel(level);
	}
	
	/**
	 * Get full system name for the resource available in Class Path
	 * @param resourceName
	 * @return filePath as String
	 */
	public static String getClassPathResourcePath(String resourceName){
		try{
			String result = java.net.URLDecoder.decode(Resources.getResource(resourceName).toString(), "UTF-8").replaceFirst("file:/", "");
			log.debug("getClasspathResourcePath - Result is:"+result);
			return result;
		}catch (Exception e){
			log.debug("getClasspathResourcePath - Error is:"+e);
			return null;
		}
	}
	
	/**
	 * Encode the password into Base64
	 * @param strPassword
	 * @return
	 */
	public static String passwordEncode(String strPassword){
		byte[] bytes = strPassword.trim().getBytes();
		return new String(Base64.encodeBase64(bytes));
	}
	
	/**
	 * Decode the Base64 password into text
	 * @param strPassword
	 * @return
	 */
	public static String passwordDecode(String strPassword){
		byte[] bytes = strPassword.trim().getBytes();
		return new String(Base64.decodeBase64(bytes));
	}
	
	/**
	 * Use this method to Load Multiple Property Files from Class Path
	 * @param listOfFiles
	 */
	public static void loadMultiplePropertyFilesFromClassPath(List<String> listOfFiles){
		
		for(String fileNmae: listOfFiles){
			String filePath = getClassPathResourcePath(fileNmae);
			if(filePath!=null){
				FileInputStream inputStream = null;
				Properties prop = new Properties();
				try {
					inputStream = new FileInputStream(filePath);
					prop.load(inputStream);
					inputStream.close();
				} catch (Exception e) {
					log.debug("loadMultiplePropertyFilesFromClassPath - Error is:"+e);
				}
				properties.putAll(prop);
			}
		}
	}
	
	/**
	 * Get Property Value
	 * @param key
	 * @return
	 */
	public static String getPropertyValue(String key){
		return properties.getProperty(key);
	}
	
	/**
	 * Get Property Value As List Object
	 * @param key
	 * @return
	 */
	public static List<String> getPropertyValueAsList(String key){
		if(getPropertyValue(key)==null || getPropertyValue(key).isEmpty())
			return null;
		List<String> propList = Arrays.asList(getPropertyValue(key).split("\\s*,\\s*"));
		return propList;
	}
	
	public static void loadClassPathFiles(List<String> listOfFilePaths){
		
		for(String s: listOfFilePaths){
			try{
				File f = new File(s);
			    URL u = f.toURL();
			    URLClassLoader urlClassLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
			    Class urlClass = URLClassLoader.class;
			    Method method = urlClass.getDeclaredMethod("addURL", new Class[]{URL.class});
			    method.setAccessible(true);
			    method.invoke(urlClassLoader, new Object[]{u});
			}catch(Exception e){
				log.debug("loadClassPathFiles - Error is:"+e);
			}
		}
	}
	
	
	@SuppressWarnings("deprecation")
	public static String addOrSubstractDaysFromDate(Object date, int days){
		if(date==null){return null;}
		try{
			SimpleDateFormat sourceDateFormat = new SimpleDateFormat("MM/dd/yyyy");
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date((String)date));
			cal.add(Calendar.DATE, days);
			
			return sourceDateFormat.format(new Date(cal.getTimeInMillis()));
		}catch(Exception e){}
		
		return null;
	}
	

	public static String getFormattedDateAsString(Object date, String sourceFormat, String destFormat){
		Date tempDate = new Date();
		if(date==null){
			return null;
		}
		
		if(!date.getClass().equals(Date.class)){
			SimpleDateFormat sourceDateFormat = new SimpleDateFormat(sourceFormat);
			try {
				tempDate = sourceDateFormat.parse((String) date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}else{
			tempDate = (Date)date;
		}
		
		SimpleDateFormat destDateFormat = new SimpleDateFormat(destFormat);
		return (String)destDateFormat.format(tempDate);
	}
	

	public static String checkDateLGE(String strDate1, String strDate2){
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		java.util.Date date1 =null;
		java.util.Date date2 =null;
		try {
			date1 = sdf.parse(strDate1);
			date2 = sdf.parse(strDate2);
			System.out.println(date1);
			System.out.println(date2);
			
			if(date1.compareTo(date2)>0){return "Greater";}
			else if(date1.compareTo(date2)<0){return "Lesser";}
			else if(date1.compareTo(date2)==0){return "Equal";}
		}catch (ParseException e) {return null;}
		return null; 
	}
	

	public static Map<String, List<String>> putThreeValuesInMap(String string1, String string2, String string3){
		List<String> tempList =  new ArrayList<String>();
		Map<String, List<String>> mapWithThreeValues = new HashMap<String, List<String>>();
		tempList.add(string2); tempList.add(string3);
		mapWithThreeValues.put(string1, tempList);
		return mapWithThreeValues;
	}
	
	public static String getMapValueByKey(Map<String, String> map, String key){
		if(map.get(key)!=null){
			return map.get(key);
		}else{
			return "";
		}
	}


	public static List<Map<String, String>> getListFromMapData(List<String> headers, Map<String, List<String>> tableRawData, String rowKeyCol, String rowKeyVal){
		if(headers==null || tableRawData==null || rowKeyCol==null){return null;}
		List<Map<String, String>> listRows = new ArrayList<Map<String, String>>();
		Iterator<String> tableRawDataKeys = tableRawData.keySet().iterator();
		
		while(tableRawDataKeys.hasNext()){
			Map<String, String> rowData = new HashMap<String, String>();
			String keyName = tableRawDataKeys.next();
			if(keyName.equalsIgnoreCase("th")){continue;}
			Iterator<String> row = tableRawData.get(keyName).iterator();
			Iterator<String> rowHead = headers.iterator();
			rowData.put(rowKeyCol, rowKeyVal);
			rowHead.next();
			while(row.hasNext() && rowHead.hasNext()){
				rowData.put(rowHead.next(), row.next());
			}
			listRows.add(rowData);
		}
		return listRows;
	}
	

	public static String getSubString(String strFullString, int startIndex, int noOfChars){
		try{
			if(noOfChars==0){
				return strFullString.substring(startIndex);
			}else{
				int endIndex = startIndex+noOfChars;
				return strFullString.substring(startIndex, endIndex);
			}
		}catch(Exception e){return null;}
	}
	

	public static String getStringToken(String strFullString, String token, int stringIndex){
		try{
			if(token=="|"){
				token = "\\|";	
			}
			
			//StringTokenizer stringTokenizer = new StringTokenizer(strFullString, token);
			
			int count = 0;
			for(String strText: strFullString.split(token)){
				if(count==stringIndex){return strText;}
				count++;
			}
			return null;
		}catch(Exception e){return null;}
	}
	

	public static String getStringForSQLWhereCondition(String strColumn, Object strValue, boolean singleQuotes, boolean trimDBCol){
		
		
		if(strValue==null || strValue.toString().trim().isEmpty()){
			if(!singleQuotes){
				if(trimDBCol){
					return"(Trim("+strColumn+") = 0)";
				}else{
					return"("+strColumn+" =  0)";
				}
			}
			if(trimDBCol){
				return"(Trim("+strColumn+") Is Null)";
			}else{
				return"("+strColumn+" Is Null)";
			}
		}
		
		if(singleQuotes && trimDBCol)
			return"(Trim("+strColumn+") = '"+strValue.toString().trim()+"')";
		else if(singleQuotes)
			return"("+strColumn+" = '"+strValue.toString().trim()+"')";
		else if(trimDBCol)
			return"(Trim("+strColumn+") = "+strValue.toString().trim()+")";
		else
			return"("+strColumn+" = "+strValue.toString().trim()+")";
		
			
	}
	

	public static String getStringForSQLWhereConditionForDate(String strColumn, Object strValue, String toDateFormat, boolean trimDBCol){
		
		
		if(strValue==null || strValue.toString().trim().isEmpty()){
			if(trimDBCol){
				return"(Trim("+strColumn+") Is Null)";
			}else{
				return"("+strColumn+" Is Null)";
			}
		}
		
		if(trimDBCol)
			return"(Trim("+strColumn+") = To_Date('"+strValue.toString().trim()+"', '"+toDateFormat+"'))";
		else
			return"("+strColumn+" = To_Date('"+strValue.toString().trim()+"', '"+toDateFormat+"'))";
			//return"("+strColumn+" = "+strValue.toString().trim()+")";
		
			
	}
	public static String getStringForSQLWhereConditionForTwoTableColumns(String strColumn1, String strColumn2, boolean addNullCheck, String strOther){
		
		if(addNullCheck){
			return "((Trim("+strColumn1.trim()+") = Trim("+strColumn2.trim()+")) Or (Trim("+strColumn1.trim()+") Is Null And Trim("+strColumn2.trim()+") Is Null))";
		}
		return "(Trim("+strColumn1.trim()+") = Trim("+strColumn2.trim()+"))";
		
	}
	
	
}
