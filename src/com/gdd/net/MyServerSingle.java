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
	// �������׽���
	private ServerSocket mServer = null;
	// �ؼ�
	private static TextArea showServerLog = null;
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

	public MyServerSingle() {
		// �ؼ��������������
		this.setLayout(new BorderLayout());
		this.setBounds(60, 60, 500, 500);
		this.setTitle("ǩ��ϵͳ����̨");
		// �ؼ�����
		showServerLog = new TextArea(10, 10);
		this.add(showServerLog, BorderLayout.CENTER);
		// ��ʾ����
		this.setVisible(true);
		// ���ö�ʱ�ʼ�
		new DayMail().send();

		// �����׽��ֿ�ʼ
		try {
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
						socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream());
				// �׽��ֽ���������ʾ�ڿ���̨
				String msg0 = in.readLine();
				StringBuffer sb0 = new StringBuffer();
				sb0.append(showServerLog.getText());
				if (showServerLog.getText() != null
						&& !showServerLog.getText().equals(""))
					sb0.append('\n');
				sb0.append(msg0);
				showServerLog.setText(sb0.toString());
				// ���׽��ֽ��ܵ�����Ϣ����
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
				System.out.println("�׽�����������������ˣ�");
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
						// �� �� һ �� �� �� �� �� �� �� �� ��
						out.println("LOGINSUCCESS");
						out.flush();
					}

				} else {
					if ("create".equals(method)) {
						member.setWorkcode(userinfo[3]);
						// ��� һ�� �û� ��� �� �û� ���� �� ���ݿ� ����
						mydatabaseconnection.insertUser(member);
						out.println("USERCREATED");
						out.flush();
					} else {
						out.println("LOGINFAIL");
						out.flush();
					}

				}
			} catch (SQLException e) {
				System.out.println("���ݿ⼯�ϳ���");
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
