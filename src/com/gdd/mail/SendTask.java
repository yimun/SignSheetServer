package com.gdd.mail;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

import com.gdd.utils.ComParam;

public abstract class SendTask extends TimerTask {

	/**
	 * �˼�ʱ������Ҫִ�еĲ�����
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		Date executeTime = new Date(this.scheduledExecutionTime());
		System.out.println("��������ִ�е�ʱ����" + executeTime);
		// �������Ҫ�������ʼ�
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost("smtp.163.com");
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(true);
		mailInfo.setUserName("check811@163.com");
		mailInfo.setPassword("123abcd");
		mailInfo.setFromAddress("check811@163.com");
		mailInfo.setToAddress(ComParam.getParam("Mail_Address"));
		mailInfo.setSubject((cal.get(Calendar.MONTH) + 1) + "��"
				+ cal.get(Calendar.DAY_OF_MONTH) + "��ǩ��״��");
		try {
			mailInfo.setContent(getMailContent());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// �������Ҫ�������ʼ�
		SimpleMailSender sms = new SimpleMailSender();
		// ���������ʽ
		if (sms.sendHtmlMail(mailInfo)) {
			System.out.println("ÿ��ǩ���ʼ����ͳɹ�");
		} else {
			System.out.println("ÿ��ǩ���ʼ�����ʧ��");
		}
	}

	abstract String getMailContent() throws SQLException;

	abstract Date getSendTime();
}