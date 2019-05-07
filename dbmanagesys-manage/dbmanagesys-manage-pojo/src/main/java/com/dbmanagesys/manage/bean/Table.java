package com.dbmanagesys.manage.bean;

import java.io.Serializable;

public class Table  implements Serializable{
	 private String dbziduan;
	   
	  private String dbtype;
	  
	  private String dblong;
	  
	  private String dbisnull;
	  private String dbiskey;
	  
	  private String dbzizeng;

	public String getDbziduan() {
		return dbziduan;
	}

	public void setDbziduan(String dbziduan) {
		this.dbziduan = dbziduan;
	}

	public String getDbtype() {
		return dbtype;
	}

	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}

	public String getDblong() {
		return dblong;
	}

	public void setDblong(String dblong) {
		this.dblong = dblong;
	}

	public String getDbisnull() {
		return dbisnull;
	}

	public void setDbisnull(String dbisnull) {
		this.dbisnull = dbisnull;
	}

	public String getDbiskey() {
		return dbiskey;
	}

	public void setDbiskey(String dbiskey) {
		this.dbiskey = dbiskey;
	}

	public String getDbzizeng() {
		return dbzizeng;
	}

	public void setDbzizeng(String dbzizeng) {
		this.dbzizeng = dbzizeng;
	}

}
