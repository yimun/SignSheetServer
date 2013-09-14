package com.gdd.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.gdd.model.Member;
import com.gdd.model.Signtime;

public class MyDatabaseConnection {
	// 区分方法
	final static int CHECKUSER = 1;
	final static int UPDATESHEET = 2;
	// 数据库语句字符串
	public String sql = null;
	// 数据库连接字符串
	public String dbDriver = null;
	public String url = null;
	public String username = null;
	public String password = null;

	// 数据库操作工具
	private Connection mConnection;
	private Statement mStatement;
	private ResultSet mResultSet;

	public MyDatabaseConnection() {

		dbDriver = "com.mysql.jdbc.Driver";
		url = "jdbc:mysql://localhost/signsheet";
		username = "root";
		password = "gdd759";

		mConnection = null;
		mStatement = null;
		mResultSet = null;

	}

	public Connection sqlcon() {
		try {
			Class.forName(this.dbDriver).newInstance();
			this.mConnection = DriverManager.getConnection(url, username,
					password);
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			return null;
		}
		return mConnection;
	}

	public Statement getstate() {
		try {
			this.mStatement = this.sqlcon().createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return mStatement;
	}

	public ResultSet executesql(Object mParameter, int flag) {

		// 根据flag的大小进行分流，验证用户还是更新签到表
		switch (flag) {
		case CHECKUSER:
			Member mymember = (Member) mParameter;
			StringBuffer sbCheckuser = new StringBuffer();
			sbCheckuser.append("select * from User where ");
			sbCheckuser.append("username='" + mymember.getUsername() + "' ");
			sbCheckuser.append("and password='" + mymember.getPassword() + "'");
			sql = sbCheckuser.toString();
			break;
		case UPDATESHEET:
			boolean Isexist = false;
			StringBuffer sb = new StringBuffer();
			Signtime signtime = (Signtime) mParameter;
			
			Isexist = checkexist(signtime.getUsername(),signtime.getCurrentDay());
			if (Isexist) {
				sb.append("update signresult set timesum=timesum+5,");
				sb.append("leave_time='" + signtime.getLeave_time() + "' ");
				sb.append("where username='" + signtime.getUsername() + "'");
			} else {
				sb.append("insert into signresult values(NULL,'");
				sb.append(signtime.getUsername() + "','");
				sb.append(signtime.getCome_time() + "','0:00");
				sb.append("','0','" + signtime.getCurrentDay() + "')");
			}
			sql = sb.toString();
			break;
		}

		try {

			switch (flag) {
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

	// 检查签到表中用户的存在与否
	private boolean checkexist(String username2, String currentday) {
		boolean isexist = false;
		sql = "select * from signresult where username='" + username2 + 
				"'" +" and currentday='" + currentday + "'";
		try {
			mResultSet = this.getstate().executeQuery(sql);
			if (mResultSet.next()) {
				isexist = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isexist;
	}

	// 新增一个用户
	public void insertUser(Member member) {
		StringBuffer insertUser_sb = new StringBuffer();
		insertUser_sb.append("insert into User values(NULL,'");
		insertUser_sb.append(member.getUsername() + "','");
		insertUser_sb.append(member.getPassword() + "','");
		insertUser_sb.append(member.getWorkcode() + "')");
		sql = insertUser_sb.toString();
		try {
			this.getstate().executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 关闭数据库的相关的工具，不知道对不对哦。
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
