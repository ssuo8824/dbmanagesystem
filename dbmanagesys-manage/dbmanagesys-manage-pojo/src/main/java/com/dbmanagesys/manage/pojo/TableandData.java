package com.dbmanagesys.manage.pojo;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_TableandData")
public class TableandData extends BasePojo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	private Integer dbid;
	private Integer tableid;

	private String username;
	private String linkname;

	private String dbname;
	private String tablename;
	private String dbziduan;

	private String dbtype;
	
	private String ziduandata;
	private String dbzizeng;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getDbid() {
		return dbid;
	}
	public void setDbid(Integer dbid) {
		this.dbid = dbid;
	}
	public Integer getTableid() {
		return tableid;
	}
	public void setTableid(Integer tableid) {
		this.tableid = tableid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getLinkname() {
		return linkname;
	}
	public void setLinkname(String linkname) {
		this.linkname = linkname;
	}
	public String getDbname() {
		return dbname;
	}
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	public String getTablename() {
		return tablename;
	}
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}
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
	public String getZiduandata() {
		return ziduandata;
	}
	public void setZiduandata(String ziduandata) {
		this.ziduandata = ziduandata;
	}
	public String getDbzizeng() {
		return dbzizeng;
	}
	public void setDbzizeng(String dbzizeng) {
		this.dbzizeng = dbzizeng;
	}

	
	
	

	
	
}
