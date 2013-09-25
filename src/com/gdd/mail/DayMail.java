package com.gdd.mail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.gdd.db.MyDatabaseConnection;

public class DayMail {

	private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;
	private MyDatabaseConnection mydatabaseconnection = null;
	
	/*public static void main(String[] args){
		
		new DayMail().send();
	}*/

	public void send() {

		TimerTask task = new TimerTask() {
			/**
			 * 此计时器任务要执行的操作。
			 */
			public void run() {
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
				mailInfo.setToAddress("470679427@qq.com");
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
				if (sms.sendTextMail(mailInfo)) {
					System.out.println("每日签到邮件发送成功");
				} else {
					System.out.println("每日签到邮件发送失败");
				}
			}
		};

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 21);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date date = calendar.getTime();

		// 如果第一次执行定时任务的时间 小于 当前的时间
		// 此时要在 第一次执行定时任务的时间 加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。
		if (date.before(new Date())) {
			date = addDay(date, 1);
		}

		Timer timer = new Timer();
		timer.schedule(task, date, PERIOD_DAY);

	}

	/**
	 * 增加或减少天数
	 * 
	 * @param date
	 * @param num
	 * @return
	 */
	public static Date addDay(Date date, int num) {

		Calendar startDT = Calendar.getInstance();
		startDT.setTime(date);
		startDT.add(Calendar.DAY_OF_MONTH, num);
		return startDT.getTime();

	}

	public String getMailContent() throws SQLException {

		ResultSet rs;
		mydatabaseconnection = new MyDatabaseConnection();
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String sql = "select * from signresult where currentday='"
				+ (cal.get(Calendar.MONTH) + 1) + "/"
				+ cal.get(Calendar.DAY_OF_MONTH) + "'";

		rs = mydatabaseconnection.getstate().executeQuery(sql);

		StringBuilder sb = new StringBuilder();
		sb.append("       用户| 到来时间| 离开时间| 统计时长| \n");
		sb.append("---------------------------------");
		while (rs.next()) {
			for(int i = 2;i<6;i++){
				sb.append(String.format("%10s", rs.getString(i)));
				sb.append("|");
			}
			sb.append("\n");
		}
		return sb.toString();

	}
}
