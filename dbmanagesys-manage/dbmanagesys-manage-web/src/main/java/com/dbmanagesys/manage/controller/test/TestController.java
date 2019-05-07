package com.dbmanagesys.manage.controller.test;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dbmanagesys.manage.pojo.User;
import com.dbmanagesys.manage.service.TestService;

@Controller
@RequestMapping("user")
public class TestController {

	
	@Autowired
	private TestService testService;
	
	
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<User>> queryUserinfo() {

		try {
			List<User> userinfo = this.testService.queryUserinfo();
			if(null == userinfo || userinfo.isEmpty())
			{
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			return ResponseEntity.ok(userinfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	}

}
