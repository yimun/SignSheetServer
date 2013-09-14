package com.gdd.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MyDatabaseConnection {

	final static int CHECKUSER = 1;
	final static int UPDATESHEET = 2;
	
	public String sql = null;
	
	public String dbDriver = null;
	public String url = null;
	public String username = null;
	public String password = null;
	
	
	private Connection mConnection;
	private Statement mStatement;
	private ResultSet mResultSet;
	
	public MyDatabaseConnection(){

		dbDriver = "com.mysql.jdbc.Driver";
		url = "jdbc:mysql://localhost/signsheet";
		username = "root";
		password = "gdd759";
		
		mConnection = null;
		mStatement = null;
		mResultSet = null;
		
	}
	
	public Connection sqlcon(){		
		try {
			Class.forName(this.dbDriver).newInstance();
			this.mConnection = DriverManager.getConnection(url, username, password);
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return null;
		}	
		return mConnection;				
	}
	
	public Statement getstate(){		
		try {
			this.mStatement = this.sqlcon().createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}		
		return mStatement;			
	}
	
	public ResultSet executesql(Object mParameter ,int flag){		
		switch(flag){
		case CHECKUSER:
			
			StringBuffer sbCheckuser = new StringBuffer();
			sbCheckuser.append("select * from User where ");
			
			sql = sbCheckuser.toString();
			
			break;
		case UPDATESHEET:
			StringBuffer sb = new StringBuffer();
		
			sql = sb.toString();			
			break;
		}
		
		try {
			
			switch(flag){
			case CHECKUSER:
				this.mResultSet = this.getstate().executeQuery(sql);
				break;
			case UPDATESHEET:
				this.getstate().executeUpdate(sql);
				this.mResultSet = null;
				break;
			}

		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}		
		return this.mResultSet;		
	}
	
	public void close() {
		
		try {
			mResultSet.close();
		} catch (SQLException e2) {
			e2.printStackTrace();
		}
		
		try {
			mStatement.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		
		try {
			mConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
