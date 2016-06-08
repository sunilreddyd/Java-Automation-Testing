package com.cai.qa.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FileHandling {
	
	private static final Log log = LogFactory.getLog(FileHandling.class);

	public static boolean renameFile(String oldName, String newName){
		File oldFile = new File(oldName);
		File newFile = new File(newName);
		if(oldFile.exists()){if(oldFile.renameTo(newFile)){return true;}}
		return false;
	}

	public static List<String> getAllFileNamesInFolder(String folderPath){
		List<String> fileNames = new ArrayList<String>();
		File folder = new File(folderPath);
		File[] listOfFiles = folder.listFiles();
		for(int i=0;i<listOfFiles.length; i++){
			if(listOfFiles[i].isFile()){
				fileNames.add(listOfFiles[i].lastModified()+";"+listOfFiles[i].getName());
			}
		}
		log.debug("getAllFileNamesInFolder - Total files:"+fileNames.size());
		return fileNames;
	}
	
	public static List<String> getFileDataIntoList(String fileName){
		File file = new File(fileName);
		try{
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			List<String> returnList = new ArrayList<String>();
			String line;
			while((line=bufferedReader.readLine())!=null){
				returnList.add(line);
			}
			bufferedReader.close();
			fileReader.close();
			return returnList;
		}catch(IOException e){log.debug("getFileDataIntoList - IOException:"+e);return null;}
	}
	

	public static void writeFileDataFromList(String fileName, List<String> fileData){
		File file = new File(fileName);
		try {
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter writer = new BufferedWriter(fileWriter);
			for(String fileLine:fileData){
				writer.write(fileLine);
				writer.newLine();
			}
			writer.close();
			fileWriter.close();
		} catch (IOException e) {log.debug("writeFileDataFromList - IOException:"+e);}
	}
	
	public static void writeAppendFileDataFromList(String fileName, List<String> fileData){
		File file = new File(fileName);
		try {
			FileWriter fileWriter = new FileWriter(file, true);
			BufferedWriter writer = new BufferedWriter(fileWriter);
			for(String fileLine:fileData){
				writer.write(fileLine);
				writer.newLine();
			}
			writer.close();
			fileWriter.close();
		} catch (IOException e) {log.debug("writeAppendFileDataFromList - IOException:"+e);}
	}

	public static Map<Integer, String> getFileDataIntoMap(String fileName){
		File file = new File(fileName);
		try{
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			Map<Integer, String> returnList = new HashMap<Integer, String>();
			String line;
			int lineCount=0;

			while((line=bufferedReader.readLine())!=null){
				lineCount++;
				returnList.put(new Integer(lineCount), line);
			}
			bufferedReader.close();
			fileReader.close();
			return returnList;
		}catch(IOException e){log.debug("getFileDataIntoMap - IOException:"+e);return null;}
	}
	
	public static void writeFileDataFromMap(String fileName, Map<String, String> fileData){
		File file = new File(fileName);
		try {
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter writer = new BufferedWriter(fileWriter);
			for(Map.Entry<String, String> fileLine: fileData.entrySet()){
				writer.write(fileLine.getKey()+"\t"+fileLine.getValue());
				writer.newLine();
			}
			writer.close();
			fileWriter.close();
		} catch (IOException e) {log.debug("writeFileDataFromMap - IOException:"+e);}
	}
	
	public static void writeFileDataFromMapInteger(String fileName, Map<Integer, String> fileData){
		File file = new File(fileName);
		try {
			FileWriter fileWriter = new FileWriter(file);
			BufferedWriter writer = new BufferedWriter(fileWriter);
			for(Map.Entry<Integer, String> fileLine: fileData.entrySet()){
				writer.write(fileLine.getKey()+"\t"+fileLine.getValue());
				writer.newLine();
			}
			writer.close();
			fileWriter.close();
		} catch (IOException e) {log.debug("writeFileDataFromMapInteger - IOException:"+e);}
	}
	
	public static void writeAppendFileDataFromMap(String fileName, Map<String, String> fileData){
		File file = new File(fileName);
		try {
			FileWriter fileWriter = new FileWriter(file, true);
			BufferedWriter writer = new BufferedWriter(fileWriter);
			for(Map.Entry<String, String> fileLine: fileData.entrySet()){
				writer.write(fileLine.getKey()+"\t"+fileLine.getValue());
				writer.newLine();
			}
			writer.close();
			fileWriter.close();
		} catch (IOException e) {log.debug("writeAppendFileDataFromMap - IOException:"+e);}
	}
	
}
