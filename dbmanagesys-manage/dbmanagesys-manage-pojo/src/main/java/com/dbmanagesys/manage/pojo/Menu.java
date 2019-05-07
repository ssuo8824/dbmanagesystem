package com.dbmanagesys.manage.pojo;

import java.util.List;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "tb_Menu")
public class Menu {
	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    private Long parentId;
	    
	    private String icon;
	    
	    private  String index;
	    
	    private String title;
	    
	    private  Boolean isParent;
	    
	    
	    //
	    private List<Menu> subs;

		public String getIndex() {
			return index;
		}

		public void setIndex(String index) {
			this.index = index;
		}

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Long getParentId() {
			return parentId;
		}

		public void setParentId(Long parentId) {
			this.parentId = parentId;
		}

		public String getIcon() {
			return icon;
		}

		public void setIcon(String icon) {
			this.icon = icon;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public Boolean getIsParent() {
			return isParent;
		}

		public void setIsParent(Boolean isParent) {
			this.isParent = isParent;
		}

		public List<Menu> getSubs() {
			return subs;
		}

		public void setSubs(List<Menu> subs) {
			this.subs = subs;
		}
	    
	    
}
