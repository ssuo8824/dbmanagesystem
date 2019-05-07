package com.dbmanagesys.manage.service;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dbmanagesys.manage.mapper.MenuMapper;
import com.dbmanagesys.manage.pojo.Menu;
import com.dbmanagesys.manage.service.redis.RedisService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MenuService {
	
	@Autowired
	private MenuMapper menuMapper; 
	
	@Autowired
	private RedisService redisService;
	
	private static final ObjectMapper mapper = new ObjectMapper();
	
	private static final	String redis_key ="DB_MANAGE_SYSTEM_MENU_";
	private static final	Integer redis_livetime =60*60*24*30*3;
	 private static final Logger logger = LoggerFactory.getLogger(MenuService.class);
  
	public List<Menu> queryHMenu(long l) {
		
	
		
		List<Menu> list=null;
		String getMenubyRedis =this.redisService.get(redis_key);
		
		try {
			list = this.menuMapper.queryListByIdandMapper(l);
			if(null==list)
			{
				return null;
			}
		
			return list;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	

		return null;
	}

}
