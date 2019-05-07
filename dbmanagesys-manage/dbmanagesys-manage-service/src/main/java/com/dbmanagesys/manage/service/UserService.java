package com.dbmanagesys.manage.service;

import java.io.IOException;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dbmanagesys.manage.mapper.UserMapper;
import com.dbmanagesys.manage.pojo.User;
import com.dbmanagesys.manage.service.redis.RedisService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class UserService {

	@Autowired
	private UserMapper userMapper;

	private static final Integer REDIS_TIME = 60 * 30 * 30;
	private static final ObjectMapper mapper = new ObjectMapper();
	@Autowired
	private RedisService redisService;

	
	/**
	 * 判断登录和生成token
	 * @param username
	 * @param password
	 * @return  token
	 * @throws Exception
	 */
	public String doLogin(String username, String password) throws Exception {
		User user = new User();
		// 根据username查询 加快查询书速度然后在比对密码
		user.setUsername(username);
		User user2 = this.userMapper.selectOne(user);
		// 比对密码
		if (!StringUtils.equals(DigestUtils.md5Hex(password), user2.getPassword())) {
			return null;
		}
		// 登录成功
		// 生成token
		String token = DigestUtils.md5Hex(username + System.currentTimeMillis());
		// 缓存
		this.redisService.set("DBSYS_TOKEN_" + token, mapper.writeValueAsString(user2), 60 * 30 * 30);
		return token;
	}

	/**
	 * 接口 通过token方法 获取用户数据
	 * 
	 * @param token
	 * @return
	 */
	public User queryUserByToken(String token) {
		// 从redis中饭获取 get 如果调用这个接口 就是用户在访问 所以要设置redis生存时间
		String key = "DBSYS_TOKEN_" + token;
		String jsonData = this.redisService.get(key);
		if (StringUtils.isEmpty(jsonData)) {
			return null;
		}
		try {
			// 非常重要 重新设置生存时间
			this.redisService.expire(key, 60 * 30);
			return mapper.readValue(jsonData, User.class);
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

}
