package com.dbmanagesys.manage.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dbmanagesys.manage.common.utils.CookieUtils;
import com.dbmanagesys.manage.pojo.User;
import com.dbmanagesys.manage.service.UserService;



/**
 * 用户登录 信息
 * @author TJW
 *
 */

@RequestMapping("user")
@Controller
public class UserCotroller {

	
	@Autowired
	private UserService userService;
	
	
	private static final  String  COOKIE_TIME="DBMANAGESYS_TOKNE";

	


	

	 /**
     * 登录
     * @param user
     * @param request
     * @param response
     * @return
     */
	@RequestMapping(value = "doLogin", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> doLogin(@RequestParam("user")String username,
			@RequestParam("pass")String password,HttpServletRequest request,HttpServletResponse response)
	{
		Map<String, Object>  map =new HashMap<>();
		try {
			String token= 	this.userService.doLogin(username,password);
			 if(null==token)
			 {
				 map.put("status", 400);
			 }else{
				 map.put("status", 200);
				 map.put("token", token);
				//cookie
				 CookieUtils.setCookie(request, response, COOKIE_TIME, token);
			 }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			 map.put("status", 500);
		}
		return map;
		
	}
	
	
	/**
	 * 首页调用的接口 通过token
	 * @param token
	 * @return
	 *//*
	@RequestMapping(value="{token}",method=RequestMethod.GET)
	public ResponseEntity<User> queryUserByToken(@PathVariable("token") String token)
	{
		
	    try {
			User user=	this.userService.queryUserByToken(token);
			if(null==user)
			{   //404
				return	ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			//资源返回 200
			return ResponseEntity.ok(user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		
	}*/
 }





