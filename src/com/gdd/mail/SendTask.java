package com.gdd.mail;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimerTask;

import com.gdd.utils.ComParam;

public abstract class SendTask extends TimerTask {

	/**
	 * 此计时器任务要执行的操作。
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Calendar cal = Calendar.getInstance();
		Date executeTime = new Date(this.scheduledExecutionTime());
		System.out.println("本次任务执行的时间是" + executeTime);
		// 这个类主要是设置邮件
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setMailServerHost("smtp.163.com");
		mailInfo.setMailServerPort("25");
		mailInfo.setValidate(true);
		mailInfo.setUserName("check811@163.com");
		mailInfo.setPassword("123abcd");
		mailInfo.setFromAddress("check811@163.com");
		mailInfo.setToAddress(ComParam.getParam("Mail_Address"));
		mailInfo.setSubject((cal.get(Calendar.MONTH) + 1) + "月"
				+ cal.get(Calendar.DAY_OF_MONTH) + "日签到状况");
		try {
			mailInfo.setContent(getMailContent());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 这个类主要来发送邮件
		SimpleMailSender sms = new SimpleMailSender();
		// 发送文体格式
		if (sms.sendHtmlMail(mailInfo)) {
			System.out.println("每日签到邮件发送成功");
		} else {
			System.out.println("每日签到邮件发送失败");
		}
	}

	abstract String getMailContent() throws SQLException;

	abstract Date getSendTime();
}