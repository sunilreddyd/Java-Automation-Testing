package com.cai.qa.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class OracleDBClass {
	private static OracleDBClass oracleDBClass = null;
	private Connection connection;
	private Statement statement;


	public Connection getConnection() {return connection;}
	public void setConnection(Connection connection) {this.connection = connection;}
	public Statement getStatement() {return statement;}
	public void setStatement(Statement statement) {this.statement = statement;}
	public void disconnectFromDB(){
		try {getStatement().close();} 	catch (Exception e) {}	setStatement(null);
		try {getConnection().close();} 	catch (Exception e) {}	setConnection(null);
	}
	
	protected OracleDBClass(){
		setConnection(null);
		setStatement(null);
	}
	public static OracleDBClass getInstance(){
		if(oracleDBClass==null){oracleDBClass = new OracleDBClass();}
		return oracleDBClass;
	}
	

	public boolean connectToDB(){
		try{
			
				disconnectFromDB();
				Class.forName("oracle.jdbc.driver.OracleDriver");
				setConnection(DriverManager.getConnection(Utility.getPropertyValue(Consts.DB_ORACLE_DBNAME),
										Utility.getPropertyValue(Consts.DB_ORACLE_USERNAME), 
										Utility.passwordDecode(Utility.getPropertyValue(Consts.DB_ORACLE_PASSWORD))));
			
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}		
		return true;
	}

	public ResultSet executeQueryStatement(String strSQL){
		try{
			setStatement(getConnection().createStatement());
			getStatement().executeQuery(strSQL);
			return getStatement().getResultSet();
		}
		catch (Exception e){
			return null;
		}
	}
}
