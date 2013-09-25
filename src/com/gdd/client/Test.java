package com.gdd.client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		Connection mConnection;
		
		String dbDriver = "com.mysql.jdbc.Driver";
		String url = "jdbc:mysql://localhost/checkin";
		String username = "root";
		String password = "dianxin";
		
		try {
			Class.forName(dbDriver).newInstance();
			mConnection = DriverManager.getConnection(url, username,
					password);
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

	}

}
