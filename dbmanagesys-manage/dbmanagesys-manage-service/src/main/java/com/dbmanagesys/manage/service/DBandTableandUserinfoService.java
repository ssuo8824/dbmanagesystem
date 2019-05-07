package com.dbmanagesys.manage.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dbmanagesys.manage.mapper.DBandTableandUserinfoMapper;
import com.dbmanagesys.manage.pojo.CreateDBandUser;
import com.dbmanagesys.manage.pojo.DBandTableandUserinfo;
@Service
public class DBandTableandUserinfoService  extends BaseService<DBandTableandUserinfo> {

	@Autowired
	private DBandTableandUserinfoMapper bandTableandUserinfoMapper ;
	
}
