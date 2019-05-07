package com.dbmanagesys.manage.pojo;

import java.io.Serializable;
import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_DBMenuandUser")
public class DBMenuandUserList extends BasePojo implements Serializable  {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String username;

	private Integer parent_Id;

	private String icon;

	private String name;

	private String level;

	// 子菜单列表
	private List<DBMenuandUserList> children;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getParent_Id() {
		return parent_Id;
	}

	public void setParent_Id(Integer parent_Id) {
		this.parent_Id = parent_Id;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public List<DBMenuandUserList> getChildren() {
		return children;
	}

	public void setChildren(List<DBMenuandUserList> children) {
		this.children = children;
	}

	
	

}
