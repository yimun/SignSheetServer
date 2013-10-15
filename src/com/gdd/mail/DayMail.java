package com.gdd.mail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Timer;

import com.gdd.db.MyDatabaseConnection;
import com.gdd.utils.ComParam;

public class DayMail {

	private static final long PERIOD_DAY = 24 * 60 * 60 * 1000;
	private MyDatabaseConnection mydatabaseconnection = null;

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
	/*
	 * public static void main(String[] args) {
	 * 
	 * new DayMail().send(); }
	 */

	public void send() {

		// 第一次发送
		Timer timerFirst = new Timer();
		MornTask taskFirst = new MornTask();
		timerFirst.schedule(taskFirst, taskFirst.getSendTime(), PERIOD_DAY);
		
		
		// 第二次发送
		Timer timerSecond = new Timer();
		NightTask task = new NightTask();
		timerSecond.schedule(task, task.getSendTime(), PERIOD_DAY);
		
	}

	
	// 晚上的邮件发送任务
	class NightTask extends SendTask {
		@Override
		String getMailContent() throws SQLException {
			// TODO Auto-generated method stub
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
			// System.out.println(set);

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
				sb.append(String.format("<td rowspan=\"%d\" >%d<br /></td>",
						time, row++));
				sb.append(String.format("<td rowspan=\"%d\" >%s<br /></td>",
						time, username));

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
								"<td rowspan=\"%d\" >%d小时%d分钟<br /></td>",
								time, // 时长总计
								timecal / 60, timecal % 60));
					sb.append("</tr>");
					rslim.next();
				}

			}
			sb.append("</tbody></table>");

			return sb.toString();

		}

		@Override
		Date getSendTime() {
			// TODO Auto-generated method stub
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY,
					Integer.parseInt(ComParam.getParam("Hour_Second")));
			calendar.set(Calendar.MINUTE,
					Integer.parseInt(ComParam.getParam("Minute_Second")));
			calendar.set(Calendar.SECOND, 0);
			Date date = calendar.getTime();

			// 如果第一次执行定时任务的时间 小于 当前的时间
			// 此时要在 第一次执行定时任务的时间 加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。
			if (date.before(new Date())) {
				date = addDay(date, 1);
			}
			return date;
		}
	}

	// 早上的签到定时任务
	class MornTask extends SendTask {

		String getMailContent() throws SQLException {
			// TODO Auto-generated method stub
			ResultSet rs;
			ResultSet rslim;
			Collection<String> comed = new HashSet<String>();
			Collection<String> notcome = new HashSet<String>();
			mydatabaseconnection = new MyDatabaseConnection();
			Date date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			String sql = "select * from signresult where currentday='"
					+ (cal.get(Calendar.MONTH) + 1) + "/"
					+ cal.get(Calendar.DAY_OF_MONTH) + "'";
			rs = mydatabaseconnection.getstate().executeQuery(sql);
			while (rs.next()) {
				comed.add(rs.getString("username"));
			}
			StringBuilder sb = new StringBuilder();
			sb.append("<table style=\"width:50%;background-color:#CCCCCC;\" border=\"1\" "
					+ "bordercolor=\"#000000\" cellpadding=\"2\" cellspacing=\"0\"><tbody>");
			sb.append("<tr><td><b>已签到同学</b><br /><td></tr>");
			for (String username : comed) {
				sb.append("<tr><td>" + username + "<br /><td></tr>");
			}
			sb.append("</tbody></table>");

			rs.beforeFirst();
			System.out.println(comed);
			sql = "select * from User ";
			rslim = mydatabaseconnection.getstate().executeQuery(sql);
			while (rslim.next()) {
				if (comed.add(rslim.getString("username"))) {
					notcome.add(rslim.getString("username"));
				}
			}
			System.out.println(notcome);
			sb.append("<hr>");
			sb.append("<table style=\"width:50%;background-color:#CCCCCC;\" border=\"1\" "
					+ "bordercolor=\"#000000\" cellpadding=\"2\" cellspacing=\"0\"><tbody>");
			sb.append("<tr><td><b>未签到同学</b><br /><td></tr>");
			for (String username : notcome) {
				sb.append("<tr><td>" + username + "<br /><td></tr>");
			}
			sb.append("</tbody></table>");

			return sb.toString();

		}

		@Override
		Date getSendTime() {
			// TODO Auto-generated method stub
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY,
					Integer.parseInt(ComParam.getParam("Hour_First")));
			calendar.set(Calendar.MINUTE,
					Integer.parseInt(ComParam.getParam("Minute_First")));
			calendar.set(Calendar.SECOND, 0);
			Date date = calendar.getTime();

			// 如果第一次执行定时任务的时间 小于 当前的时间
			// 此时要在 第一次执行定时任务的时间 加一天，以便此任务在下个时间点执行。如果不加一天，任务会立即执行。
			if (date.before(new Date())) {
				date = addDay(date, 1);
			}
			return date;
		}
	}

	

	/*
	 * public static void main(String[] args) { try {
	 * 
	 * String str = new DayMail().getMailContent(); System.out.println(str);
	 * 
	 * } catch (SQLException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * }
	 */

}
