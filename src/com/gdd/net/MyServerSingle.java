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
import com.gdd.utils.ComParam;
import com.gdd.utils.SignBusiness;

public class MyServerSingle{

	/**
	 * ���������
	 */
	// �������׽���
	private ServerSocket mServer = null;
	// ��������
	private String[] userinfo = new String[5];
	private String method = null;
	private Member member = null;
	private ResultSet rs = null;
	private MyDatabaseConnection mydatabaseconnection = null;
	private SignBusiness signbusiness = null;
	// �׽��ֵ�һЩҪ�õ��������
	private BufferedReader in = null;
	private PrintWriter out = null;
	private String outPrintStr = null;
	// ���ַ���
	final static int CHECKUSER = 1;
	final static int CHANGEMM = 3;

	public MyServerSingle() {
		
		
		// ���ö�ʱ�ʼ�
		if(ComParam.getParam("Is_Accept_Mail").equals("Yes"))
			new DayMail().send();

		// �����׽��ֿ�ʼ
		try {
			System.out.println("��������ʼ���У�");
			mServer = new ServerSocket(9000);
		} catch (IOException e) {
			System.out.println("�������׽��ֳ���");
			e.printStackTrace();
		}
		while (true) {
			// �׽���׼������
			Socket socket;
			try {
				socket = mServer.accept();
				mydatabaseconnection = new MyDatabaseConnection();
				in = new BufferedReader(new InputStreamReader(
						socket.getInputStream(), "UTF-8")); // �涨���ֱ����ʽΪUTF-8��androidĬ�ϵĺ��ֱ���
				out = new PrintWriter(socket.getOutputStream());
				// �׽��ֽ���������ʾ�ڿ���̨
				String msg0 = in.readLine();
				Date date = new Date();
				System.out.println(date +"��"+ msg0); // ���¿���̨����
				// ���׽��ֽ��ܵ�����Ϣ����
				member = new Member();
				userinfo = msg0.split(";");
				
				// ��������������ַ������Ȳ�����Ҫ���׳�
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
				System.out.println("�׽�����������������ˣ�");
				e.printStackTrace();
			}
			// �����û���User�����Ƿ����
			rs = mydatabaseconnection.executesql(member, CHECKUSER); 
			try {
				if (rs.next()) {
					/** ���ڸ��û���¼ */
					switch (method) {
					case "create": // �����û�
						outPrintStr = "USEREXIST";
						break;
					case "login": // �û���¼
						outPrintStr = "LOGINSUCCESS";
						break;
					case "check": // �û�ǩ��
						signbusiness = new SignBusiness(member.getUsername());
						signbusiness.updatememberinfo();
						outPrintStr ="CHECKSUCCESS";
						break;
					case "changemm":// ��������
						member.setExtra(userinfo[4]); //
						mydatabaseconnection.executesql(member, CHANGEMM);
						outPrintStr ="CHANGEMMSUCCESS";
						break;
					}

				} else {
					/** �����ڸ��û���¼ */
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
				//�������
				out.println(outPrintStr);
				out.flush();
				System.out.println("Result:"+outPrintStr);
			} catch (SQLException e) {
				System.out.println("���ݿ⼯�ϳ���");
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
