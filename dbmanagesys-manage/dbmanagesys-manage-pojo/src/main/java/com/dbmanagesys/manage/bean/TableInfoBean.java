package com.dbmanagesys.manage.bean;

import java.io.Serializable;
import java.util.List;

import com.dbmanagesys.manage.pojo.DBandTableandUserinfo;

public class TableInfoBean implements Serializable{

	  private List<DBandTableandUserinfo> infiledList;

		public List<DBandTableandUserinfo> getInfiledList() {
			return infiledList;
		}

		public void setInfiledList(List<DBandTableandUserinfo> infiledList) {
			this.infiledList = infiledList;
		}
}
