package com.dbmanagesys.manage.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Data implements Serializable{

	
private 	List<Map<String, Object>> data;

public List<Map<String, Object>> getData() {
	return data;
}

public void setData(List<Map<String, Object>> data) {
	this.data = data;
}

}
