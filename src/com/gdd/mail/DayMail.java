package com.gdd.mail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import com.gdd.db.MyDatabaseConnection;
import com.gdd.utils.ComParam;

public class DayMail {

	private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;
	private MyDatabaseConnection mydatabaseconnection = null;

	/*
	 * public static void main(String[] args) {
	 * 
	 * new DayMail().send(); }
	 */

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
		};

		Timer timer = new Timer();
		timer.schedule(task, getSendTime(), PERIOD_DAY);

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

	/**
	 * 获得邮件发送内容
	 * 数据库的当天数据读取
	 * @return
	 * @throws SQLException
	 */
	public String getMailContent() throws SQLException {

		ResultSet rs;
		ResultSet rslim;
		Collection<String> set = new HashSet<String>();
		
		mydatabaseconnection = new MyDatabaseConnection();
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String sql = "select * from signresult where currentday='"
				+ (cal.get(Calendar.MONTH) + 1) + "/"
				+ cal.get(Calendar.DAY_OF_MONTH) + "'";
		rs = mydatabaseconnection.getstate().executeQuery(sql);
		while (rs.next()) {
			set.add(rs.getString("username"));
		}
		System.out.println(set);

		StringBuilder sb = new StringBuilder();
		sb.append("<table style=\"width:100%;background-color:#CCCCCC;\" border=\"1\" "
				+ "bordercolor=\"#000000\" cellpadding=\"2\" cellspacing=\"0\"><tbody>");
		sb.append("<td>记录<br /><td>学生<br /></td><td>签到时间<br /></td><td>离开时间<br /></td><td>所在时长(min)<br /></td><td>合计时长<br /></td>");
		int row = 1;
		for (String username : set) {

			rslim = mydatabaseconnection.getstate().executeQuery(
					sql + " and username='" + username + "'");
			int time = 0;
			int timecal = 0;
			while (rslim.next()) {
				timecal += Integer.parseInt(rslim.getString("timesum"));
				time++;
			}

			sb.append("<tr>");
			sb.append(String.format("<td rowspan=\"%d\" >%d<br /></td>", time,
					row++));
			sb.append(String.format("<td rowspan=\"%d\" >%s<br /></td>", time,
					username));

			rslim.first();
			for (int j = 0; j < time; j++) {

				if (j != 0)
					sb.append("<tr>");
				for (int i = 3; i < 6; i++) {
					sb.append(String.format("<td>%s<br /></td>",
							rslim.getString(i)));
				}
				if (j == 0)
					sb.append(String.format(
							"<td rowspan=\"%d\" >%d小时%d分钟<br /></td>", time, // 时长总计
							timecal / 60, timecal % 60));
				sb.append("</tr>");
				rslim.next();
			}

		}
		sb.append("</tbody></table>");

		return sb.toString();

	}

	private Date getSendTime() {

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY,
				Integer.parseInt(ComParam.getParam("Hour")));
		calendar.set(Calendar.MINUTE,
				Integer.parseInt(ComParam.getParam("Minute")));
		calendar.set(Calendar.SECOND, 0);
		Date date = calendar.getTime();

		// 如果第一次执行定时任务的时间 小于 当前的时间
		// 此时要在 第一次执行定时任务的时间 加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。
		if (date.before(new Date())) {
			date = addDay(date, 1);
		}
		return date;

	}

	public static void main(String[] args) {
		try {

			String str = new DayMail().getMailContent();
			System.out.println(str);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
