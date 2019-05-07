package com.dbmanagesys.manage.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MysqlDBService {

	@Value("${jdbc.mysql.driverClassName}")
	public String driverClassName;
	
	
	@Value("${jdbc.mysql.url}")
	public String url;

	
	@Value("${jdbc.mysql.username}")
	public String username;
	
	@Value("${jdbc.mysql.password}")
	public String password;
	
	 
}
