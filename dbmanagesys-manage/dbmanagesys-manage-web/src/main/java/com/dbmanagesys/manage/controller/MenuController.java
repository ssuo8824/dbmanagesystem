package com.dbmanagesys.manage.controller;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.dbmanagesys.manage.pojo.CreateDBandUser;
import com.dbmanagesys.manage.pojo.Menu;
import com.dbmanagesys.manage.service.CreateDBandUserService;
import com.dbmanagesys.manage.service.MenuService;



/**
 * 显示导航
 * @author TJW
 *
 */
@Controller
public class MenuController {
   
	@Autowired
	private MenuService menuService;
	
	@Autowired
	private CreateDBandUserService createDBandUserService;
	/**
	 * 获取横向菜单
	 * @return
	 */
	@RequestMapping(value="menu",method = RequestMethod.GET)
	public  ResponseEntity<List<Menu>> queryHMenu()
	{
		try {
			List<Menu>	 menus= this.menuService.queryHMenu(0L);
			if(null == menus || menus.isEmpty())
			{
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
	
			 	return ResponseEntity.ok(menus);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		
	}
	
	
	/**
	 * 返回连接的数据集
	 * 
	 * @return
	 */
	@RequestMapping(value = "getLinkInfo", method = RequestMethod.GET)
	public ResponseEntity<List<CreateDBandUser>> getLinkInfo() {
		try {
			CreateDBandUser record = new CreateDBandUser();
			List<CreateDBandUser> createDBandUsers;

			createDBandUsers = this.createDBandUserService.queryListByWhere(record);
			if (createDBandUsers == null || createDBandUsers.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			return ResponseEntity.ok(createDBandUsers);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

	}
}
