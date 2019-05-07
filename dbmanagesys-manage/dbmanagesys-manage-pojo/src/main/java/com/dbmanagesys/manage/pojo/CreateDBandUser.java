package com.dbmanagesys.manage.pojo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_CreateDBandUser")
public class CreateDBandUser extends BasePojo{
	
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userdbid;
	
	
	
	private String databasename;
	
	
	private String username;

    
	private String databasetype;
	
	private String defaultdatabase;
	
	private String databaseusername;
	
	
	private String databaseport;
	
	
	private String ipadress;


	public Long getUserdbid() {
		return userdbid;
	}


	public void setUserdbid(Long userdbid) {
		this.userdbid = userdbid;
	}


	public String getDatabasename() {
		return databasename;
	}


	public void setDatabasename(String databasename) {
		this.databasename = databasename;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getDatabasetype() {
		return databasetype;
	}


	public void setDatabasetype(String databasetype) {
		this.databasetype = databasetype;
	}


	public String getDefaultdatabase() {
		return defaultdatabase;
	}


	public void setDefaultdatabase(String defaultdatabase) {
		this.defaultdatabase = defaultdatabase;
	}


	public String getDatabaseusername() {
		return databaseusername;
	}


	public void setDatabaseusername(String databaseusername) {
		this.databaseusername = databaseusername;
	}


	public String getDatabaseport() {
		return databaseport;
	}


	public void setDatabaseport(String databaseport) {
		this.databaseport = databaseport;
	}


	public String getIpadress() {
		return ipadress;
	}


	public void setIpadress(String ipadress) {
		this.ipadress = ipadress;
	}


	

    
	
	
	
	
	
}
