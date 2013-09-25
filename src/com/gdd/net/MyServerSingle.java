package com.gdd.net;

import java.awt.BorderLayout;
import java.awt.TextArea;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JFrame;

import com.gdd.db.MyDatabaseConnection;
import com.gdd.mail.DayMail;
import com.gdd.model.Member;
import com.gdd.utils.SignBusiness;

public class MyServerSingle extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5965679769822091793L;
	// 服务器套接字
	private ServerSocket mServer = null;
	// 控件
	private static TextArea showServerLog = null;
	// 各种数据
	private String[] userinfo = new String[5];
	private String method = null;
	private Member member = null;
	private ResultSet rs = null;
	private MyDatabaseConnection mydatabaseconnection = null;
	private SignBusiness signbusiness = null;
	// 套接字的一些要用的输入输出
	private BufferedReader in = null;
	private PrintWriter out = null;

	public MyServerSingle() {
		// 控件的设置外观设置
		this.setLayout(new BorderLayout());
		this.setBounds(60, 60, 500, 500);
		this.setTitle("签到系统控制台");
		// 控件设置
		showServerLog = new TextArea(10, 10);
		this.add(showServerLog, BorderLayout.CENTER);
		// 显示窗口
		this.setVisible(true);
		// 设置定时邮件
		new DayMail().send();

		// 服务套接字开始
		try {
			mServer = new ServerSocket(9000);
		} catch (IOException e) {
			System.out.println("服务器套接字出错！");
			e.printStackTrace();
		}
		while (true) {
			// 套接字准备工作
			Socket socket;
			try {
				socket = mServer.accept();
				mydatabaseconnection = new MyDatabaseConnection();
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream());
				// 套接字接受数据显示在控制台
				String msg0 = in.readLine();
				StringBuffer sb0 = new StringBuffer();
				sb0.append(showServerLog.getText());
				if (showServerLog.getText() != null
						&& !showServerLog.getText().equals(""))
					sb0.append('\n');
				sb0.append(msg0);
				showServerLog.setText(sb0.toString());
				// 把套接字接受到的信息解析
				member = new Member();
				userinfo = msg0.split(";");
				if(userinfo.length!=4){
					out.println("LOGINFAIL");
					out.flush();
					socket.close();
					in.close();
					out.close();
					continue;
				}
				method = userinfo[0];
				member.setUsername(userinfo[1]);
				member.setPassword(userinfo[2]);

			} catch (IOException e) {
				System.out.println("套接字输入输出流出错了！");
				e.printStackTrace();
			}
			rs = mydatabaseconnection.executesql(member, 1);
			try {
				if (rs.next()) {
					if ("create".equals(method)) {
						member.setWorkcode(userinfo[3]);
						out.println("USEREXIST");
						out.flush();
					} else {
						signbusiness = new SignBusiness(member.getUsername());
						signbusiness.updatememberinfo();
						// 添 加 一 个 更 新 数 据 库 的 方 法
						out.println("LOGINSUCCESS");
						out.flush();
					}

				} else {
					if ("create".equals(method)) {
						member.setWorkcode(userinfo[3]);
						// 添加 一个 用户 检测 和 用户 插入 的 数据库 操作
						mydatabaseconnection.insertUser(member);
						out.println("USERCREATED");
						out.flush();
					} else {
						out.println("LOGINFAIL");
						out.flush();
					}

				}
			} catch (SQLException e) {
				System.out.println("数据库集合出错！");
				e.printStackTrace();
			} finally {
				try {
					in.close();
				} catch (Exception e) {
				}
				try {
					out.close();
				} catch (Exception e) {
				}
				try {
					rs.close();
				} catch (Exception e) {
				}
				try {
					mydatabaseconnection.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public static void main(String args[]) {
		new MyServerSingle();
	}

}
