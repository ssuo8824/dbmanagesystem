package com.dbmanagesys.manage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dbmanagesys.manage.mapper.DBandTableandUserinfoMapper;
import com.dbmanagesys.manage.mapper.TableandDataMapper;
import com.dbmanagesys.manage.pojo.CreateDBandUser;
import com.dbmanagesys.manage.pojo.DBandTableandUserinfo;
import com.dbmanagesys.manage.pojo.TableandData;
@Service
public class TableandDataService  extends BaseService<TableandData> {

	@Autowired
	private TableandDataMapper tableandDataMapper ;
	
}
