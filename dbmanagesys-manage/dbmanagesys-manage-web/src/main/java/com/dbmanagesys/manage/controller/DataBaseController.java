package com.dbmanagesys.manage.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbmanagesys.manage.bean.Data;
import com.dbmanagesys.manage.bean.Table;
import com.dbmanagesys.manage.bean.TableBean;
import com.dbmanagesys.manage.bean.TableInfoBean;
import com.dbmanagesys.manage.bean.TableandDataBean;
import com.dbmanagesys.manage.common.bean.DBmenuResult;
import com.dbmanagesys.manage.pojo.CreateDBandUser;
import com.dbmanagesys.manage.pojo.DBMenuandUser;
import com.dbmanagesys.manage.pojo.DBMenuandUserList;
import com.dbmanagesys.manage.pojo.DBandTableandUserinfo;
import com.dbmanagesys.manage.pojo.TableandData;
import com.dbmanagesys.manage.pojo.User;
import com.dbmanagesys.manage.service.CreateDBandUserService;
import com.dbmanagesys.manage.service.DataBaseService;
import com.dbmanagesys.manage.service.TableandDataService;
import com.dbmanagesys.manage.service.UserService;
import com.dbmanagesys.manage.thtreadlocal.UserThreadLocal;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 数据库 操作
 * 
 * @author TJW
 *
 */
@RequestMapping("db")
@Controller
public class DataBaseController {

	@Autowired
	private TableandDataService tableandDataService;
	
	
	@Autowired
	private DataBaseService dataBaseService;

	@Autowired
	private CreateDBandUserService createDBandUserService;

	@Autowired
	private UserService userService;

	private static final Logger logger = LoggerFactory.getLogger(DataBaseController.class);
	private static final ObjectMapper mapper = new ObjectMapper();

	/**
	 * 测试数据库连接 返回所有数据库
	 * 
	 * @param databasename
	 * @param ipadress
	 * @param databaseport
	 * @param databaseusername
	 * @param databasepassword
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "checkLink", method = RequestMethod.POST)
	public Map<String, Object> checkLink(/*
											 * @RequestHeader(
											 * "Authentication-Token") String
											 * Token,
											 */
			HttpServletRequest request,
			@RequestParam("databaseChoice") String databasetype, @RequestParam("ipadress") String ipadress,
			@RequestParam("databaseport") String databaseport,
			@RequestParam("databaseusername") String databaseusername,
			@RequestParam("databasepassword") String databasepassword) {
		Map<String, Object> resulet = new HashMap<>();
		List<Map<String, Object>> list = this.createDBandUserService.checkLink(databasetype, ipadress, databaseport,
				databaseusername, databasepassword);

		try {
			/*
			 * String key = "DBSYS_TOKEN_" + Token; User userinfo =
			 * this.userService.queryUserByToken(key); // 判断token 用户信息 如果没有从新登录
			 * if (null == userinfo) { // 没用通过校验 重新登录 resulet.put("status",
			 * "401");
			 * 
			 * }
			 */
			
			if (null != list) {
				resulet.put("status", "200");
				resulet.put("data", list);
			} else {
				// 没用通过校验
				resulet.put("status", "400");
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			resulet.put("status", "500");

		}
		return resulet;

	}

	/**
	 * 连接成功并且将数据库名添加到数据库里，方便左侧显示
	 * 
	 * @param databasetype
	 * @param databasename
	 * @param ipadress
	 * @param databaseport
	 * @param databaseusername
	 * @param databasepassword
	 * @return
	 */

	@RequestMapping(value = "toLink", method = RequestMethod.POST)
	public ResponseEntity<Void> toLink(HttpServletRequest request,
			@RequestParam("databaseChoice") String databasetype, // 数据库类型
			@RequestParam("databasename") String databasename, // 数据库的名称mysql
			@RequestParam("ipadress") String ipadress, // ip
			@RequestParam("databaseport") String databaseport, // 端口号
			@RequestParam("databaseusername") String databaseusername, // 用户名
			@RequestParam("databasepassword") String databasepassword)// 密码
	{
		// 获取用户信息
		try {
			User user = UserThreadLocal.get();
			if (null == user) {
				// 失败返回 400
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}
			
		
			
			Boolean boolean1 = this.createDBandUserService.toLink(user, databasetype, ipadress, databaseport,
					databaseusername, databasepassword, databasename);
			// 如果成功 返回201
			if (boolean1) {
				
				
				logger.debug("创建连接成功boolean1---"+boolean1);
		
				return ResponseEntity.status(HttpStatus.CREATED).build();
			} else {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		// 失败返回 500
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

	}

	/**
	 * 返回连接的数据集
	 * 
	 * @return
	 */
	/*@RequestMapping(value = "getLinkInfo", method = RequestMethod.GET)
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

	}*/

	/**
	 * 显示左侧树形结构
	 * 
	 * @return
	 */
	@RequestMapping(value = "showDBMenu", method = RequestMethod.GET)
	public ResponseEntity<DBmenuResult> showDBMenu() {
		try {
			User user = UserThreadLocal.get();
			Boolean isLinkbool=UserThreadLocal.getisLink();
			logger.debug("showDBMenu-isLinkbool--"+isLinkbool);
			if (null == user ) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}
			if(isLinkbool){
				DBmenuResult dBmenuResult = this.dataBaseService.showDBMenu(user);
				if (null == dBmenuResult) {
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
				}
				return ResponseEntity.ok(dBmenuResult);
			}else{
				// 404
				logger.debug("showDBMenu-isLinkbool不等于true--"+isLinkbool);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}

			
		} catch (Exception e) {

			e.printStackTrace();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

	}

	/**
	 * 添加数据库
	 * 
	 * @param parentid
	 * @param addDbId
	 * @param databasename
	 * @param zifuji
	 * @param paixuguize
	 * @return
	 */
	@RequestMapping(value = "addDB", method = RequestMethod.POST)
	public ResponseEntity<Void> addDB(@RequestParam("databasetype") String dataBaseType,
			@RequestParam("parentid") Integer parentid,
			@RequestParam("addDbId") Integer addDbId, // 数据库类型
			@RequestParam("databasename") String databasename, // 数据库的名称mysql
			@RequestParam("zifuji") String zifuji, // ip
			@RequestParam("paixuguize") String paixuguize // 端口号
	) {
		try {
			Boolean isLinkbool= UserThreadLocal.getisLink();
			logger.debug("addDB-isLinkbool--"+isLinkbool);
			User user = UserThreadLocal.get();
		
			logger.debug("addDB数据类型------"+dataBaseType);
			if (null == user  ) {
				// 失败返回 401Unauthorized
				logger.debug("addDB用户token或数据类型为空");
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
			if(isLinkbool==false )
			{
				// 404
				logger.debug("addDB的isLinkbool是false----"+isLinkbool);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			if(null == dataBaseType)
			{
				// 404
				logger.debug("addDB的dataBaseType是null----"+dataBaseType);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			switch (dataBaseType) {
			case "MySQL":
				logger.debug("addDB数据类型的------"+dataBaseType);
				Boolean isdatabasename = this.dataBaseService.queryInfoByname(databasename);
				// 判断是否已经有数据库的名
				if (false == isdatabasename) {
					
					Boolean boolean1 = this.dataBaseService.addDB(user, parentid, addDbId, databasename, zifuji,
							paixuguize);
					// 如果成功 返回201
					if (boolean1) {
						return ResponseEntity.status(HttpStatus.CREATED).build();
					} else {
						// 404
						logger.debug("addDB的boolean1----"+boolean1);
						return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
					}
				} else {
					// 400
					logger.debug("addDB的isdatabasename已有数据库名----"+isdatabasename);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
				}
				

			default:
				break;
			}
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 失败返回 500
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

	}

	/**
	 * 创建表和字段
	 * 
	 * @return
	 */
	@RequestMapping(value = "createTable", method = RequestMethod.POST)
	public ResponseEntity<Void> createTable(@RequestParam("databasetype") String dataBaseType,
			@RequestParam("biao_parentId") Integer biao_parentId,
			@RequestParam("biaoId") Long biaonameId, @RequestParam("biaoName") String biaoName, // 数据库类型
			@RequestParam("dataList") String dataList) // 数据库的名称mysql)
	{
		Boolean isLinkbool=UserThreadLocal.getisLink();
		logger.debug("createTable-isLinkbool--"+isLinkbool);
		User user = UserThreadLocal.get();
	
		logger.debug("createTable数据类型------" + dataBaseType);
		if (null == user ) {
			// 失败返回 401Unauthorized
			logger.debug("createTable数据类型或user为空------" + user);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	
		if(null == dataBaseType)
		{
			// 404
			logger.debug("createTable的dataBaseType是null----"+dataBaseType);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		logger.debug("biao_parentId---" + biao_parentId);
		logger.debug("biaoId---" + biaonameId);
		logger.debug("biaoName---" + biaoName);
		logger.debug("dataList---" + dataList);
		switch (dataBaseType) {
		case "MySQL":
			logger.debug("createTable数据类型------" + dataBaseType);
			if (null != biaoName && null != dataList) {
				// 判断是否已经有数据库的表的名
		
				Boolean iSdatabaseTablename = this.dataBaseService.IsHaveTable(biaoName,biao_parentId);

				if (false == iSdatabaseTablename) {

					Boolean boolean1 = this.dataBaseService.createTable(user, biao_parentId, biaonameId, biaoName,
							dataList);
					logger.debug("返回创建表的一系列信息boolean1---" + boolean1);
					// 如果成功 返回201
					if (boolean1) {
						logger.debug("返回创建表的成功控制器的---" + boolean1);
						return ResponseEntity.status(HttpStatus.CREATED).build();
					} else {
						// 404
						logger.debug("返回创建表的一系列信息boolean1失败---" + boolean1);
						return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
					}
				} else {
					logger.debug("isdatabaseTablename错误---" + iSdatabaseTablename);
					// 400
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
				}

			} else {
				// 400
				logger.debug("没有表名或者数据解析问题---" + biaoName + "----------------" + dataList);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
			}

		default:
			break;
		}
		// 失败返回 500
		logger.debug("createTable数据类型为空");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();

	}
	/**
	 * 显示表信息desianTable
	 * @param dataBaseType
	 * @param biao_parentId
	 * @param biaoId
	 * @param biaoName
	 * @return
	 */
	@RequestMapping(value = "showTableInfo", method = RequestMethod.GET)
	public ResponseEntity<TableInfoBean> showTableInfo(@RequestParam("databasetype") String dataBaseType,
			@RequestParam("biao_parentId") Integer biao_parentId,
			@RequestParam("biaoId") Integer biaoId,
			@RequestParam("biaoName") String biaoName)
	{
		Boolean isLinkbool=UserThreadLocal.getisLink();
		logger.debug("showTableInfo-isLinkbool--"+isLinkbool);
		User user = UserThreadLocal.get();
		
		logger.debug("showTableInfo数据类型------" + dataBaseType);
		if (null == user ) {
			// 失败返回 401Unauthorized
			logger.debug("showTableInfo数据类型或user为空------" + user);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
		if(null == dataBaseType)
		{
			// 404
			logger.debug("showTableInfo的dataBaseType是null----"+dataBaseType);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		logger.debug("showTableInfo-----biao_parentId---" + biao_parentId);
		logger.debug("showTableInfo-----biaoId---" + biaoId);
		logger.debug("showTableInfo-----biaoName---" + biaoName);
		
		switch (dataBaseType) {
		case "MySQL":
			logger.debug("showTableInfo-----数据类型------" + dataBaseType);
			if (null != biaoName) {
				// 判断是否已经有数据库的表的名
		        DBMenuandUser biaonameInfo=    this.dataBaseService.queryInfoByid(biao_parentId);
		    	logger.debug("showTableInfo-----biaonameInfo.getParentId().longValue()---" + biaonameInfo.getParentId());
		    	
		    
				Boolean iSdatabaseTablename = this.dataBaseService.IsHaveTable(biaoName,biaonameInfo.getParentId());

				if (true == iSdatabaseTablename) {
					TableInfoBean infoBean=            this.dataBaseService.showTableInfo(user, biao_parentId, biaoId, biaoName);
					if(null==infoBean)
					{
						// 400
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
					}
					return ResponseEntity.ok(infoBean);
				} else {
					logger.debug("showTableInfo-----isdatabaseTablename错误---" + iSdatabaseTablename);
					// 400
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
				}

			} else {
				// 400
				logger.debug("showTableInfo-----没有表名或者---" + biaoName + "----------------" + biao_parentId+"----------------"+biaoId);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}

		default:
			break;
		}
		
		// 失败返回 500
		logger.debug("showTableInfo-----数据类型为空");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		
	}
	
	
	
	
	/**
	 * 返回显示字段
	 * @param dataBaseType
	 * @param biao_parentId
	 * @param biaoId
	 * @param biaoName
	 * @return
	 */
	@RequestMapping(value = "handTable", method = RequestMethod.POST)
	public ResponseEntity<Data> handTable(@RequestParam("databasetype") String dataBaseType,
			@RequestParam("biao_parentId") Integer biao_parentId,
			@RequestParam("biaoId") Integer biaoId,
			@RequestParam("biaoName") String biaoName,
			@RequestParam("dataList") String dataList)
	{
		Boolean isLinkbool=UserThreadLocal.getisLink();
		logger.debug("showTableInfo-isLinkbool--"+isLinkbool);
		User user = UserThreadLocal.get();
		
		logger.debug("handTable数据类型------" + dataBaseType);
		if (null == user ) {
			// 失败返回 401Unauthorized
			logger.debug("handTable数据类型或user为空------" + user);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
		if(null == dataBaseType)
		{
			// 404
			logger.debug("handTable的dataBaseType是null----"+dataBaseType);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	
		switch (dataBaseType) {
		case "MySQL":
			logger.debug("handTable-----数据类型------" + dataBaseType);
			if (null != biaoName) {
				Data datas=	this.dataBaseService.addTabledatas(user,biao_parentId,biaoId,biaoName,dataList);
				if(null!=datas)
				  {
					return ResponseEntity.status(HttpStatus.CREATED).body(null);
					}else{
						logger.debug("handTable的datas是null----");
						return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
					}
				

			} else {

				logger.debug("handTable-----没有表名或者---" + biaoName + "----------------" + biao_parentId
						+ "----------------" + biaoId);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}

		default:
			break;
		}

		// 失败返回 500
		logger.debug("handTable-----数据类型为空");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

	}
	/**
	 * 显示字段的数据信息
	 * @param dataBaseType
	 * @param biao_parentId
	 * @param biaoId
	 * @param biaoName
	 * @return
	 */
	@RequestMapping(value = "showTableZDdata", method = RequestMethod.GET)
	public ResponseEntity<List<Map<String, Object>> >showTableZDdata (@RequestParam("databasetype") String dataBaseType,
			@RequestParam("biao_parentId") Integer biao_parentId,
			@RequestParam("biaoId") Integer biaoId,
			@RequestParam("biaoName") String biaoName)
	{
		Boolean isLinkbool=UserThreadLocal.getisLink();
		logger.debug("showTableInfo-isLinkbool--"+isLinkbool);
		User user = UserThreadLocal.get();
		
		logger.debug("showTableZDdata----数据类型------" + dataBaseType);
		if (null == user ) {
			// 失败返回 401Unauthorized
			logger.debug("showTableZDdata----数据类型或user为空------" + user);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
		if(null == dataBaseType)
		{
			// 404
			logger.debug("showTableZDdata----的dataBaseType是null----"+dataBaseType);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	
		switch (dataBaseType) {
		case "MySQL":
			logger.debug("showTableZDdata-----数据类型------" + dataBaseType);
			if (null != biaoName) {
				// 判断是否已经有数据库的表的名
				List<Map<String, Object>> maps=    this.dataBaseService.showTableZDdata(user, biao_parentId, biaoId, biaoName);
				if(maps==null)
				{
					// 400
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
				}
				return ResponseEntity.ok(maps);
			} else {
				// 400
				logger.debug("showTableZDdata-----没有表名或者---" + biaoName + "----------------" + biao_parentId
						+ "----------------" + biaoId);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}

		default:
			break;
		}

		// 失败返回 500
		logger.debug("showTableZDdata-----数据类型为空");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		
	}


	/**
	 * 显示表和字段数据的信息openTable
	 * 
	 * @param dataBaseType
	 * @param biao_parentId
	 * @param biaoId
	 * @param biaoName
	 * @return
	 */
	@RequestMapping(value = "showTableandDatainfo", method = RequestMethod.GET)
	public ResponseEntity<TableandDataBean> showTableandDatainfo(@RequestParam("databasetype") String dataBaseType,
			@RequestParam("biao_parentId") Integer biao_parentId, @RequestParam("biaoId") Integer biaoId,
			@RequestParam("biaoName") String biaoName) {
		Boolean isLinkbool = UserThreadLocal.getisLink();
		logger.debug("showTableandDatainfo-isLinkbool--" + isLinkbool);
		User user = UserThreadLocal.get();

		logger.debug("showTableandDatainfo-------------数据类型------" + dataBaseType);
		if (null == user) {
			// 失败返回 401Unauthorized
			logger.debug("showTableandDatainfo-----数据类型或user为空------" + user);
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		}
		if (null == dataBaseType) {
			// 404
			logger.debug("showTableandDatainfo-----的dataBaseType是null----" + dataBaseType);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		logger.debug("showTableandDatainfo-----biao_parentId---" + biao_parentId);
		logger.debug("showTableandDatainfo-----biaoId---" + biaoId);
		logger.debug("showTableandDatainfo-----biaoName---" + biaoName);

		switch (dataBaseType) {
		case "MySQL":
			logger.debug("showTableandDatainfo-----数据类型------" + dataBaseType);
			if (null != biaoName) {
				// 判断是否已经有数据库的表的名
				DBMenuandUser biaonameInfo = this.dataBaseService.queryInfoByid(biao_parentId);
				logger.debug("showTableandDatainfo-----biaonameInfo.getParentId().longValue()---"
						+ biaonameInfo.getParentId());

				Boolean iSdatabaseTablename = this.dataBaseService.IsHaveTable(biaoName, biaonameInfo.getParentId());

				if (true == iSdatabaseTablename) {
					TableandDataBean tableandDataBean = this.dataBaseService.showTableandDatainfo(user, biao_parentId,
							biaoId, biaoName);
					if (null == tableandDataBean) {
						// 400
						return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
					}
					return ResponseEntity.ok(tableandDataBean);
				} else {
					logger.debug("showTableandDatainfo-----isdatabaseTablename错误---" + iSdatabaseTablename);
					// 400
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
				}

			} else {
				// 400
				logger.debug("showTableandDatainfo-----没有表名或者---" + biaoName + "----------------" + biao_parentId
						+ "----------------" + biaoId);
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}

		default:
			break;
		}

		// 失败返回 500
		logger.debug("showTableandDatainfo-----数据类型为空");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

	}

}
