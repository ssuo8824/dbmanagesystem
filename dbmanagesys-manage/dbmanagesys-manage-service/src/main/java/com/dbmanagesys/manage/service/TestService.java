package com.dbmanagesys.manage.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dbmanagesys.manage.mapper.TestUserMapper;
import com.dbmanagesys.manage.pojo.User;

@Service
public class TestService {

	@Autowired
	private TestUserMapper testUserMapper;

	public List<User> queryUserinfo() {

		List<User> users = testUserMapper.select(null);
		return users;
	}
	
	
}
