package com.gdd.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.gdd.db.MyDatabaseConnection;
import com.gdd.mail.DayMail;
import com.gdd.model.Member;
import com.gdd.utils.SignBusiness;

public class MyServerSingle{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5965679769822091793L;
	// 服务器套接字
	private ServerSocket mServer = null;
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
	private String outPrintStr = null;
	// 区分方法
	final static int CHECKUSER = 1;
	final static int CHANGEMM = 3;

	public MyServerSingle() {
		
		
		// 设置定时邮件
		new DayMail().send();

		// 服务套接字开始
		try {
			System.out.println("服务器开始运行！");
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
						socket.getInputStream(), "UTF-8")); // 规定文字编码格式为UTF-8，android默认的汉字编码
				out = new PrintWriter(socket.getOutputStream());
				// 套接字接受数据显示在控制台
				String msg0 = in.readLine();
				Date date = new Date();
				System.out.println(date +"："+ msg0); // 更新控制台界面
				// 把套接字接受到的信息解析
				member = new Member();
				userinfo = msg0.split(";");
				
				// 意外情况：传输字符串长度不符合要求抛出
				if (userinfo.length < 4) {
					outPrintStr = "LENGTHERROR";
					out.println(outPrintStr);
					out.flush();
					System.out.println(outPrintStr);
					socket.close();
					in.close();
					out.close();
					continue;
				}
				
				method = userinfo[0];
				member.setUsername(userinfo[1]);
				member.setPassword(userinfo[2]);
				member.setWorkcode(userinfo[3]);

			} catch (IOException e) {
				System.out.println("套接字输入输出流出错了！");
				e.printStackTrace();
			}
			// 检查该用户在User表中是否存在
			rs = mydatabaseconnection.executesql(member, CHECKUSER); 
			try {
				if (rs.next()) {
					/** 存在该用户记录 */
					switch (method) {
					case "create": // 创建用户
						outPrintStr = "USEREXIST";
						break;
					case "login": // 用户登录
						outPrintStr = "LOGINSUCCESS";
						break;
					case "check": // 用户签到
						signbusiness = new SignBusiness(member.getUsername());
						signbusiness.updatememberinfo();
						outPrintStr ="CHECKSUCCESS";
						break;
					case "changemm":// 更改密码
						member.setExtra(userinfo[4]); //
						mydatabaseconnection.executesql(member, CHANGEMM);
						outPrintStr ="CHANGEMMSUCCESS";
						break;
					}

				} else {
					/** 不存在该用户记录 */
					switch (method) {
					case "create":
						mydatabaseconnection.insertUser(member);
						outPrintStr ="USERCREATED";
						break;
					case "login":
						outPrintStr ="LOGINFAIL";
						break;
					case "check":
						outPrintStr ="CHECKFAIL";
						break;
					case "changemm":
						outPrintStr ="CHANGEMMFAIL";
						break;
					}
				}
				//输出数据
				out.println(outPrintStr);
				out.flush();
				System.out.println("Result:"+outPrintStr);
			} catch (SQLException e) {
				System.out.println("数据库集合出错！");
				e.printStackTrace();
			} finally {
				try {
					mydatabaseconnection.close();
					rs.close();
					in.close();
					out.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}
	

	public static void main(String args[]) {
		new MyServerSingle();
	}

}
