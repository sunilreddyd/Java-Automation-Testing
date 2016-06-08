package com.cai.qa.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TestData {

	private static final Log log = LogFactory.getLog(TestData.class);
	
	private String _databaseName;
	private Connection connection;
	private Statement statement;
	
	public TestData(String _databaseName){
		this._databaseName = _databaseName;
		setConnection(null);
		setStatement(null);
	}
	protected boolean connectToDB(){
		try{
			Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
			String database = "jdbc:odbc:Driver={Microsoft Access Driver (*.mdb, *.accdb)};DBQ="+this._databaseName;
			setConnection(DriverManager.getConnection(database));
		}catch (Exception e){
			log.debug("connectToDB - Exception:"+e);
			return false;
		}		
		return true;
	}
	
	/*
	P_SNo
	P_TestScenario
	P_IterationNumber
	P_RunStatus
	P_Comments
	*/
	public List<Map<String, String>> getTestData(String strTableName, String strCondition, String strOrderBy){
		
		StringBuffer strQuery = new StringBuffer("Select * from "+strTableName);
		
		if(strCondition!=null && !strCondition.isEmpty())	strQuery.append(" where "+strCondition);
		if(strOrderBy!=null && !strOrderBy.isEmpty())		strQuery.append(" order by "+strOrderBy);
		log.debug(strQuery.toString());
		if(!connectToDB()){disconnectFromDB();return null;}
		ResultSet resultSet = this.executeQueryStatement(strQuery.toString());
		if(resultSet==null){disconnectFromDB();	return null;}
		
		try {
			ResultSetMetaData resultSetMetaData=resultSet.getMetaData();
			int intColumnCount = resultSetMetaData.getColumnCount();
			
			ArrayList<Map<String, String>> testData = new ArrayList<Map<String, String>>();
			while(resultSet.next()){
				HashMap<String, String> rowData = new HashMap<String, String>();
				for(int colNum = 1; colNum<=intColumnCount;colNum++)
					rowData.put(resultSetMetaData.getColumnName(colNum), resultSet.getString(colNum));
				testData.add(rowData);
			}
			disconnectFromDB();return testData;
		} catch (SQLException e) {disconnectFromDB();return null;}
	}
	
	public List<String> getColumnNames(String strTableName){
		
		StringBuffer strQuery = new StringBuffer("Select TOP 1 * from "+strTableName);
		if(!connectToDB()){disconnectFromDB();return null;}
		ResultSet resultSet = this.executeQueryStatement(strQuery.toString());
		if(resultSet==null){disconnectFromDB();	return null;}
		try {
			ResultSetMetaData resultSetMetaData=resultSet.getMetaData();
			int intColumnCount = resultSetMetaData.getColumnCount();
			List<String> columnNames = new ArrayList<String>();
			for(int colNum = 1; colNum<=intColumnCount;colNum++)
				columnNames.add(resultSetMetaData.getColumnName(colNum));
			disconnectFromDB();
			return columnNames;
		}catch (SQLException e) {disconnectFromDB();return null;}
	}	
	public int updateTestData(String strTableName, String strSetValue, String strCondition){
		StringBuffer strQuery = new StringBuffer("Update "+strTableName+" Set "+strSetValue);
		
		if(strCondition!=null && !strCondition.isEmpty())	strQuery.append(" where "+strCondition);
		if(!connectToDB()){disconnectFromDB();return 0;}
		int intRowsUpdated = this.executeUpdateStatement(strQuery.toString());
		disconnectFromDB();
		if(intRowsUpdated>0){return intRowsUpdated;}
		return 0;
	}
	
	public int insertRow(String strTableName, String strColumns, String strValues){
		StringBuffer strQuery = new StringBuffer("Insert Into "+strTableName);
		if(strColumns!=null && !strColumns.isEmpty())	strQuery.append(" ("+strColumns+")");
		if(strValues!=null && !strValues.isEmpty())	strQuery.append(" Values ("+strValues+")");
		if(!connectToDB()){disconnectFromDB();return 0;}
		int intRowsUpdated = this.executeUpdateStatement(strQuery.toString());
		disconnectFromDB();
		if(intRowsUpdated>0){return intRowsUpdated;}
		return 0;
	}
	
	public int insertRowData(String strTableName, List<String> columnNames, Map<String, String> rowData){
		StringBuffer strColumns=new StringBuffer("");
		StringBuffer strValues=new StringBuffer("");
		Iterator<String> colNames = columnNames.iterator();
		while(colNames.hasNext()){
			StringBuffer strColName = new StringBuffer(colNames.next());
			StringBuffer strColValue = new StringBuffer("");
			String strMapValue = rowData.get(strColName.toString());
			if(strMapValue!=null){strColValue = new StringBuffer(strMapValue.replace("'", "''"));}
			strColumns.append("["+strColName+"]");strValues.append("'"+strColValue+"'");
			if(colNames.hasNext()){strColumns.append(",");strValues.append(",");}
		}
		return insertRow(strTableName, strColumns.toString(), strValues.toString());
	}
	

	protected ResultSet executeQueryStatement(String strSQL){
		try{
			setStatement(getConnection().createStatement());
			getStatement().executeQuery(strSQL);
			return getStatement().getResultSet();
		}
		catch (Exception e){return null;}
	}
	protected int executeUpdateStatement(String strSQL){
		log.debug(strSQL);
		try{
			setStatement(getConnection().createStatement());
			int updateCount = getStatement().executeUpdate(strSQL);
			getConnection().commit();
			log.debug(strSQL+" - records Updated:"+updateCount);
			return updateCount;
		}
		catch (Exception e){e.printStackTrace(); return 0;}
	}
	
	/*
	public void addWebTableDataToDB(List<Map<String, String>> listData, List<String> headers, String strTable){
		
		if(listData==null){return;}
		//System.out.println(listData.size());
		//DatapoolClass testData = new DatapoolClass(strDataPool);
		Iterator<Map<String, String>> listDataItr = listData.iterator();
		while(listDataItr.hasNext()){
			if(insertRowData(strTable, headers, listDataItr.next())==0){
				IKAGeneralInfo.setTestRunStatus(false);
			}
		}
	}
	*/
	
	protected Statement getStatement() {return statement;}
	protected void setStatement(Statement statement) {this.statement = statement;}
	protected Connection getConnection() {return connection;}
	protected void setConnection(Connection connection) {this.connection = connection;}

	protected void disconnectFromDB(){
		try {getStatement().close();} 	catch (SQLException e) {}	setStatement(null);
		try {getConnection().close();} 	catch (SQLException e) {}	setConnection(null);
	}
	
	
}
