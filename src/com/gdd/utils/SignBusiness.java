package com.gdd.utils;

import java.util.Calendar;
import java.util.Date;

import com.gdd.db.MyDatabaseConnection;
import com.gdd.model.Signtime;

public class SignBusiness {
	// ������ص�
	private Signtime signtime = null;

	private int curMonth = 0;
	private int curDay = 0;
	private int curHour = 0;
	private int curMinute = 0;
	final static int UPDATESHEET = 2;
	

	// ������ص�
	private MyDatabaseConnection busiConnection = null;

	public SignBusiness(String username) {
		// ��ʼ�����ݺ͹���
		signtime = new Signtime();
		busiConnection = new MyDatabaseConnection();
		// ��ȡʱ����ص�����
		Date date = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		curMonth = cal.get(Calendar.MONTH)+1;
		curDay = cal.get(Calendar.DAY_OF_MONTH);
		curHour = cal.get(Calendar.HOUR_OF_DAY);
		curMinute = cal.get(Calendar.MINUTE);
		// ����ʱ����ص�����
		signtime.setCome_tiem(curHour + ":" + curMinute);
		signtime.setLeave_time(curHour + ":" + curMinute);
		signtime.setCurrentday(curMonth + "/" + curDay);
		signtime.setUsername(username);

	}

	public void updatememberinfo() {
		busiConnection.executesql(signtime, UPDATESHEET);
	}
}
