package com.pbx_stats.beans;

import com.pbx_stats.tools.BCrypt;

public class User {
	public User(int iduser, String name,String email, String password, boolean admin) {
		super();
		this.iduser = iduser;
		this.name = name;
		this.email = email;
		this.password = password;
		this.admin = admin;
	}
	private int iduser;
	private String name;
	private String email;
	private String password;
	
	public int getIduser() {
		return iduser;
	}
	public void setIduser(int iduser) {
		this.iduser = iduser;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = BCrypt.hashpw(password, BCrypt.gensalt());
	}
	public boolean checkPassword(String hash){
		return BCrypt.checkpw(this.password, hash);
	}
	public boolean isAdmin() {
		return admin;
	}
	public void setAdmin(boolean admin) {
		this.admin = admin;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	private boolean admin;
}
