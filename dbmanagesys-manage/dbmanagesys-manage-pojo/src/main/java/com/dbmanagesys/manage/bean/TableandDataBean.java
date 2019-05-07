package com.dbmanagesys.manage.bean;

import java.io.Serializable;
import java.util.List;

import com.dbmanagesys.manage.pojo.DBandTableandUserinfo;
import com.dbmanagesys.manage.pojo.TableandData;

public class TableandDataBean implements Serializable{
	 private List<TableandData> infiledList;

		public List<TableandData> getInfiledList() {
			return infiledList;
		}

		public void setInfiledList(List<TableandData> infiledList) {
			this.infiledList = infiledList;
		}
}
