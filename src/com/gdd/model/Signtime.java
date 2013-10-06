package com.gdd.model;

public class Signtime {

	private String id = null;
	private String username = null;
	private String come_time = null;
	private String leave_time = null;
	private String time_sum = null;
	private String currentday = null;

	public void setId(String id) {
		this.id = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setCome_tiem(String come_time) {
		this.come_time = come_time;
	}

	public void setLeave_time(String leave_time) {
		this.leave_time = leave_time;
	}

	public void setTime_sum(String time_sum) {
		this.time_sum = time_sum;
	}

	public void setCurrentday(String currentday) {
		this.currentday = currentday;
	}
	


	public String getId() {
		return this.id;
	}

	public String getUsername() {
		return this.username;
	}

	public String getCome_time() {
		return this.come_time;
	}

	public String getLeave_time() {
		return this.leave_time;
	}

	public String getTime_sum() {
		return this.time_sum;
	}

	public String getCurrentDay() {
		return currentday;
	}
	
	
}
