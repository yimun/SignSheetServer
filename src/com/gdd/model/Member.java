package com.gdd.model;

public class Member {

	private String id = null;
	private String username = null;
	private String password = null;
	private String workcode = null;
	private String extra = null;

	public void setId(String id) {
		this.id = id;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setWorkcode(String workcode) {
		this.workcode = workcode;
	}
	public void setExtra(String extra){
		this.extra = extra;
	}

	public String getId() {
		return this.id;
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public String getWorkcode() {
		return this.workcode;
	}
	
	public String getExtra(){
		return this.extra;
	}
}
