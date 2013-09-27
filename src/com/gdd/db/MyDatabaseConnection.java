package com.gdd.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.gdd.model.Member;
import com.gdd.model.Signtime;

public class MyDatabaseConnection {
	// ���ַ���
	final static int CHECKUSER = 1;
	final static int UPDATESHEET = 2;
	final static int CHANGEMM = 3;
	// ���ݿ�����ַ���
	public String sql = null;
	// ���ݿ������ַ���
	public String dbDriver = null;
	public String url = null;
	public String username = null;
	public String password = null;

	// ���ݿ��������
	private Connection mConnection;
	private Statement mStatement;
	private ResultSet mResultSet;

	public MyDatabaseConnection() {

		dbDriver = "com.mysql.jdbc.Driver";
		url = "jdbc:mysql://192.168.9.114/checkin" +
				"?useUnicode=true&characterEncoding=utf8"; // ��ֹ���ݿ⺺������
		username = "root";
		password = "linwei";

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

		// ����flag�Ĵ�С���з�������֤�û����Ǹ���ǩ����
		switch (flag) {
		case CHECKUSER:
			Member mymember = (Member) mParameter;
			StringBuffer sbCheckuser = new StringBuffer();
			sbCheckuser.append("select * from User where ");
			sbCheckuser.append("username='" + mymember.getUsername() + "' ");
			sbCheckuser.append("and password='" + mymember.getPassword() + "' ");
			sbCheckuser.append("and workcode='" + mymember.getWorkcode() + "'");
			sql = sbCheckuser.toString();
			break;
		case UPDATESHEET:
			StringBuffer sb = new StringBuffer();
			Signtime signtime = (Signtime) mParameter;
			int getid = checkDistance(signtime);

			if (getid != -1) { // ������ڲ�����һ���뿪��ʱ����ô�ǩ��ʱ��������10����

				sb.append("update signresult set timesum=timesum+2,");
				sb.append("leave_time='" + signtime.getLeave_time() + "' ");
				sb.append("where id='" + getid + "'");

			} else {
				sb.append("insert into signresult values(NULL,'");
				sb.append(signtime.getUsername() + "','");
				sb.append(signtime.getCome_time() + "','"
						+ signtime.getCome_time());
				sb.append("','0','" + signtime.getCurrentDay() + "')");
			}
			sql = sb.toString();
			break;
		case CHANGEMM:
			Member member = (Member) mParameter;
			StringBuffer sbChangemm = new StringBuffer();
			sbChangemm.append("update User set password='" + member.getExtra()
					+ "' ");
			sbChangemm.append("where username='" + member.getUsername() + "'");
			sql = sbChangemm.toString();
			break;
		}

		try {

			switch (flag) {
			case CHECKUSER:
				this.mResultSet = this.getstate().executeQuery(sql);
				break;
			case UPDATESHEET:
			case CHANGEMM:
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

	// ����һ���û�
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

	// �ر����ݿ����صĹ��ߣ���֪���Բ���Ŷ��
	public void close() {

		try {
			if (mResultSet != null)
				mResultSet.close();
		} catch (SQLException e2) {
			e2.printStackTrace();
		}

		try {
			if (mStatement != null)
				mStatement.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		try {
			if (mConnection != null)
				mConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���ǩ���Ƿ������һ���ۼ�
	 * 
	 * @param signtime
	 * @return �����������ؼ�¼��id�����򷵻�-1
	 */
	public int checkDistance(Signtime signtime) {

		String timeFore;
		sql = "select * from signresult where username='"
				+ signtime.getUsername() + "'" + "and currentday='"
				+ signtime.getCurrentDay() + "'";
		try {
			mResultSet = this.getstate().executeQuery(sql);
			while (mResultSet.next()) {
				timeFore = mResultSet.getString(4);
				// System.out.println("timefore="+timeFore);
				if (getMinOfDay(signtime.getCome_time())
						- getMinOfDay(timeFore) < 10) {
					return mResultSet.getInt(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public int getMinOfDay(String str) {
		int hour, minute;
		String strarr[] = str.split(":");
		hour = Integer.parseInt(strarr[0]);
		minute = Integer.parseInt(strarr[1]);
		return hour * 60 + minute;
	}

}
