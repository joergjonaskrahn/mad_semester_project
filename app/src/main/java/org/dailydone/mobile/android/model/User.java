package org.dailydone.mobile.android.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

	private String pwd;
	
	private String email;
	
	public User() {
	}

	public User(String email,String pwd) {
		this.email = email;
		this.pwd = pwd;
	}
}