package com.dbmanagesys.manage.service;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbmanagesys.manage.DB.MySQLDButil;
import com.dbmanagesys.manage.bean.Data;
import com.dbmanagesys.manage.bean.Table;
import com.dbmanagesys.manage.bean.TableBean;
import com.dbmanagesys.manage.bean.TableInfoBean;
import com.dbmanagesys.manage.bean.TableandDataBean;
import com.dbmanagesys.manage.common.bean.DBmenuResult;
import com.dbmanagesys.manage.common.service.MysqlDBService;
import com.dbmanagesys.manage.mapper.CreateDBandUserMapper;
import com.dbmanagesys.manage.mapper.DBMenuandUserListMapper;
import com.dbmanagesys.manage.mapper.DBMenuandUserMapper;
import com.dbmanagesys.manage.mapper.DBandTableandUserinfoMapper;
import com.dbmanagesys.manage.pojo.CreateDBandUser;
import com.dbmanagesys.manage.pojo.DBMenuandUser;
import com.dbmanagesys.manage.pojo.DBMenuandUserList;
import com.dbmanagesys.manage.pojo.DBandTableandUserinfo;
import com.dbmanagesys.manage.pojo.TableandData;
import com.dbmanagesys.manage.pojo.User;
import com.dbmanagesys.manage.service.redis.RedisService;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class DataBaseService extends BaseService<DBMenuandUser> {

	@Autowired
	private MysqlDBService mysqlDBService;

	@Autowired
	private CreateDBandUserService createDBandUserService;
	
	@Autowired
	private TableandDataService tableandDataService;

	@Autowired
	private DBMenuandUserMapper dbMenuandUserMapper;

	@Autowired
	private DBMenuandUserListMapper dbMenuandUserListMapper;
	
	@Autowired
	private RedisService redisService;
	
	@Autowired
	private DBandTableandUserinfoService bandTableandUserinfoService;
	
	private static final String DB_NAME = "db_managesys_test";
	private static final Logger logger = LoggerFactory.getLogger(DataBaseService.class);

	private static final String DB_MENU_1 = "el-icon-lx-yunfuwuqi";
	private static final String DB_MENU_shujuku = "el-icon-lx-yunshujuku";

	private static final String DB_MENU_shujukubiao = "el-icon-lx-shujukubiao";
	private static final String DB_MENU_ziduanguanli = "el-icon-lx-ziduanguanli";
	private static final ObjectMapper mapper = new ObjectMapper();

	/**
	 * 显示左侧树形结构
	 * 
	 * @param user
	 * @param dbmenuid
	 * @return
	 */
	public DBmenuResult showDBMenu(User user) {
		String username = user.getUsername();
		// 查询得所以菜单
		List<DBMenuandUserList> alldbMenuandUsers = this.dbMenuandUserListMapper.queryListByIdandUsername(username);
		logger.debug("alldbMenuandUsers.isEmpty()" + alldbMenuandUsers.isEmpty());
		// 跟节点
		List<DBMenuandUserList> rootdbMenu = new ArrayList<>();

		for (DBMenuandUserList dbMenuandUser : alldbMenuandUsers) {
			logger.debug("(dbMenuandUser.getParentId()" + dbMenuandUser.getParent_Id());
			if (dbMenuandUser.getParent_Id().equals(0)) {
				logger.debug("(dbMenuandUser.getParentId()" + dbMenuandUser.getParent_Id());
				logger.debug("进入成功!!");
				rootdbMenu.add(dbMenuandUser);
			}
		}

		// 为根菜单设置子菜单，getClild是递归调用的
		for (DBMenuandUserList nav : rootdbMenu) {
			// 获取根节点下的所有子节点 使用getChild方法
			List<DBMenuandUserList> childList = getChild(nav.getId(), alldbMenuandUsers);
			// 给根节点设置子节点
			nav.setChildren(childList);
		}

		DBmenuResult dBmenuResult = new DBmenuResult();
		dBmenuResult.setMaxexpandId(205);
		dBmenuResult.setTreelist(rootdbMenu);

		return dBmenuResult;
	}

	/**
	 * 遍历子节点
	 * 
	 * @param id
	 * @param allMenu
	 * @return
	 */
	private List<DBMenuandUserList> getChild(Integer id, List<DBMenuandUserList> allMenu) {
		// 子菜单
		List<DBMenuandUserList> childList = new ArrayList<DBMenuandUserList>();
		for (DBMenuandUserList nav : allMenu) {
			// 遍历所有节点，将所有菜单的父id与传过来的根节点的id比较
			// 相等说明：为该根节点的子节点。
			if (nav.getParent_Id().equals(id)) {
				childList.add(nav);
			}
		}
		// 递归设置子节点
		for (DBMenuandUserList nav : childList) {
			nav.setChildren(getChild(nav.getId(), allMenu));
		}
		// 排序

		// 如果节点下没有子节点，返回一个空List（递归退出）
		if (childList.size() == 0) {
			return new ArrayList<DBMenuandUserList>();
		}
		return childList;
	}

	/**
	 * 根据数据库名 返回信息 true 是有信息则不要
	 * 
	 * @param databasename
	 * @return
	 */

	public Boolean queryInfoByname(String databasename) {
		DBMenuandUser menuandUser;

		try {
			DBMenuandUser record = new DBMenuandUser();
			record.setName(databasename);
			menuandUser = this.queryOne(record);
			if (null != menuandUser) {
				logger.debug("queryInfoByname不等于空" + menuandUser);
				return true;
			}
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug("queryInfoByname等于空");
		return false;

	}
	
/**
 * 通过主键查询
 * @param id
 * @return
 */
	public DBMenuandUser queryInfoByid(Integer id) {
		DBMenuandUser menuandUser;
		try {
			menuandUser = this.dbMenuandUserMapper.selectByPrimaryKey(id);
			if (null != menuandUser) {
				logger.debug("queryInfoByid---menuandUser不等于空" + menuandUser);
				return menuandUser;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
		logger.debug("queryInfoByid---menuandUser等于空");
		return null;

	}

	/**
	 * 根据parentid查询父类在目录里的信息
	 * 
	 * @param parentid
	 * @return
	 */

	public DBMenuandUser queryInfoByparentid(Integer parentid) {
		DBMenuandUser menuandUser;
		try {
			menuandUser = this.dbMenuandUserMapper.selectByPrimaryKey(parentid);
			if (null != menuandUser) {
				logger.debug("queryInfoByparentid---menuandUser不等于空" + menuandUser);
				return menuandUser;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block

			e.printStackTrace();
		}
		logger.debug("queryInfoByparentid---menuandUser等于空");
		return null;

	}

	/**
	 * 根据parentid 递归查询最顶级父类在目录里的信息
	 * 
	 * @param parentid
	 * @return
	 */

	public DBMenuandUser queryparentid0Byparentid(Integer parentid) {
		DBMenuandUser menuandUser = null;
	

			logger.debug("queryparentid0Byparentid------" + menuandUser);
			menuandUser = this.dbMenuandUserMapper.selectByPrimaryKey(parentid);
			
				if(menuandUser.getParentId().intValue() == 0) {
				
					logger.debug("queryparentid0Byparentid------menuandUser==0-------" + menuandUser.getName()+"----"+menuandUser.getParentId());
					return menuandUser;
				} 
				return	this.queryparentid0Byparentid(menuandUser.getParentId());
				
				
			}
			
            
	



	/**
	 * 创建数据库并且添加到左侧导航菜单
	 * 
	 * @param user
	 * @param addDbId
	 * @param databasename
	 * @param zifuji
	 * @param paixuguize
	 * @return
	 */
	public Boolean addDB(User user, Integer parentid, Integer addDbId, String databasename, String zifuji,
			String paixuguize) {

		// 1通过parentid获取父类信息，写个方法
		DBMenuandUser parentInfo = this.queryInfoByparentid(parentid);
		if (parentInfo == null) {
			logger.debug("parentInfob等于空");
		}

		// 2通过父类id和admin查询数据库连接信息 写个方法
		CreateDBandUser createDBandUser = this.createDBandUserService
				.queryInfoBydatabasenameandusername(parentInfo.getUsername(), parentInfo.getName());
		// 3连接数据库
		if (createDBandUser == null) {
			logger.debug("createDBandUser等于空");
		}
		String url = "jdbc:mysql://" + createDBandUser.getIpadress() + ":" + createDBandUser.getDatabaseport() + "/"
				+ DB_NAME + "?useSSL=false&serverTimezone=UTC&characterEncoding=utf-8&allowPublicKeyRetrieval=true";

		String newurl = "jdbc:mysql://" + createDBandUser.getIpadress() + ":" + createDBandUser.getDatabaseport() + "/"
				+ databasename
				+ "?useSSL=false&serverTimezone=UTC&characterEncoding=utf-8&allowPublicKeyRetrieval=true";
		// 4创建数据库

		// create database `test2` default character set utf8 collate
		// utf8_general_ci
		String creatSql = "create database `" + databasename + "` default character set " + zifuji + " collate "
				+ paixuguize;

		MySQLDButil mdb = MySQLDButil.getInstance();
		// mdb.getConnection(url, databaseusername, databasepassword);
		try {
			Connection con = mdb.getConnection(url, this.mysqlDBService.username, this.mysqlDBService.password);

			Integer i = mdb.getSQL(creatSql);
			Connection createcon = mdb.getConnection(newurl, this.mysqlDBService.username,
					this.mysqlDBService.password);
			if (null != createcon) {
				logger.debug("创建新数据库成功");
				logger.debug("已经连接到新创建的数据库：" + databasename);
				// 5创建成功后保存信息在到导航中
				DBMenuandUser t = new DBMenuandUser();
				t.setUsername(user.getUsername());// admin
				t.setParentId(addDbId);// 2
				t.setIcon(DB_MENU_shujuku);
				t.setName(databasename);
				t.setLevel("3");
				t.setId(null);
				Integer i1 = this.save(t);

				// 添加数据表的名字
				DBMenuandUser t2 = new DBMenuandUser();
				t2.setName("表");
				t2.setParentId(t.getId());
				t2.setIcon(DB_MENU_shujukubiao);
				t2.setUsername(t.getUsername());
				t2.setLevel("4");
				t2.setId(null);

				Integer i2 = this.save(t2);
				logger.debug("插入新数据库信息" + i1 + "++++" + i2);
				// 返回为true
				return i1.intValue() == 1 && i2.intValue() == 1;
			} else {
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

/**
 * 创建表和字段
 * @param biao_parentId
 * @param biaoId
 * @param biaoName
 * @param dataList
 * @return
 */
	/**
	 * @param user
	 * @param biao_parentId
	 * @param biaonameId
	 * @param biaoName
	 * @param dataList
	 * @return
	 */
	@SuppressWarnings("unused")
	public Boolean createTable(User user,Integer biao_parentId, Long biaonameId, String biaoName, String dataList) {
		
		 String ctreateTablesql="";
		 TableBean bean = null ;
		 int flag=0;
		 int flag2=0;
		 Integer i1 = null;
		 Integer i2 = null;
		//解析datalist
		try {
		      bean = mapper.readValue(dataList, TableBean.class);

			logger.debug("createTable---bean---" + bean.getInfiledList());
		
			String PRIMARYkey = "";
			StringBuffer sb = new StringBuffer("");
			sb.append("CREATE TABLE" + " " + biaoName + "(");
			List<Table> a = bean.getInfiledList();
			int count = 0;
			for (int i = 0; i < a.size(); i++) {
				int length = a.size();

				sb.append(a.get(i).getDbziduan() + " " + a.get(i).getDbtype() + "(" + a.get(i).getDblong() + ")" + " "
						+ a.get(i).getDbisnull() + " " + a.get(i).getDbzizeng());

				// 防止最后一个,
				// if(i<length-1){
				sb.append(",");
				// }
			}
			for (int i = 0; i < a.size(); i++) {
				int length = a.size();

				if ("true".equals(a.get(i).getDbiskey())) {
					count++;

					if (count > 1) {

						PRIMARYkey = PRIMARYkey + ",";
					}
					PRIMARYkey = PRIMARYkey + "`" + a.get(i).getDbziduan() + "`";

				}

			}
			//判断是否有主键 是否空
			if(StringUtils.isNotBlank(PRIMARYkey))
			{
				sb.append("PRIMARY key(" + PRIMARYkey + "))");
			}
		
			sb.replace(sb.length() - 1, sb.length(), "");
			sb.append(")ENGINE=InnoDB DEFAULT CHARSET=utf8;");
			logger.debug("createTable-----ctresql--------" + sb.toString());
			ctreateTablesql = sb.toString();

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
		// 1通过parentid获取数据库名

		DBMenuandUser databasenameInfo = this.queryInfoByparentid(biao_parentId.intValue());
		if (databasenameInfo == null) {
			logger.debug("createTable-------databasenameInfo等于空");
		}
		String databasename = databasenameInfo.getName();// 数据库名MysqlTEST

		// 2获取到父类的数据库连接名
		DBMenuandUser parentInfocreateTable = this.queryparentid0Byparentid(biao_parentId.intValue());
		logger.debug("createTable----parentInfocreateTable------" + parentInfocreateTable.getParentId() + "---"
				+ parentInfocreateTable.getUsername() + "-----" + parentInfocreateTable.getName());
		if (parentInfocreateTable == null) {
			logger.debug("createTable----parentInfobcreateTable等于空");
		}

		// 3通过父类id和admin查询数据库连接信息

		CreateDBandUser createDBandUsercreateTable = this.createDBandUserService.queryInfoBydatabasenameandusername(
				parentInfocreateTable.getUsername(), parentInfocreateTable.getName());
		// 3连接数据库
		if (createDBandUsercreateTable == null) {
			logger.debug("createTable----createDBandUsercreateTable等于空");
		}
		// 3连接数据库
		String newurl = "jdbc:mysql://" + createDBandUsercreateTable.getIpadress() + ":"
				+ createDBandUsercreateTable.getDatabaseport() + "/" + databasename
				+ "?useSSL=false&serverTimezone=UTC&characterEncoding=utf-8&allowPublicKeyRetrieval=true";
		logger.debug("newurl-----" + newurl);

		// 创建数据表的sql
		String creatTableSql = ctreateTablesql;
		logger.debug("creatTableSql-----" + creatTableSql);
		MySQLDButil mdb = MySQLDButil.getInstance();
		// mdb.getConnection(url, databaseusername, databasepassword);
		try {
			Connection createcon = mdb.getConnection(newurl, this.mysqlDBService.username,
					this.mysqlDBService.password);// 连接新数据库
			if (null != createcon) { // 执行sql语句
				Integer i = mdb.createTable(creatTableSql);
				String seletable = "select * from" + "  " + biaoName;
				ResultSet resultSet = mdb.query(seletable);
				logger.debug("createTable----创建表成功---------" + resultSet);
				// 如果成功
				if (resultSet != null) {
					// 1线保存到菜单目录里 表的信息 id自增，parentid= biaonameId
					// icon=el-icon-lx-shujukubiao name=biaoName level=5
					logger.debug("createTable----创建表已经成功----" + i);
					logger.debug("createTable----已经连接到新创建的数据库的表：-----" + biaoName);
					DBMenuandUser t = new DBMenuandUser();
					t.setId(null);
					t.setUsername(parentInfocreateTable.getUsername());
					t.setParentId(biaonameId.intValue());
					t.setIcon(DB_MENU_shujukubiao);
					t.setName(biaoName);
					t.setLevel("5");
					Integer createTableint = this.save(t);
					Integer ziduanPartID = t.getId();
					if (createTableint.intValue() == 1) {
						int createTabledatecount = 0;
						logger.debug("createTable-----创建表的菜单信息成功");
						logger.debug("createTable-----继续添加表字段的菜单信息");
						// 2保存目录 表内容信息 id =自增 parentid=等表名添加完了返回的id
						// icon=el-icon-lx-ziduanguanli
						// name=tables.getDbtype()：tables.getDbziduan() level=6
						DBMenuandUser t2 = new DBMenuandUser();
						DBandTableandUserinfo dBandTableandUserinfo = new DBandTableandUserinfo();
						// 添加到表和字段里
						TableandData tableandData = new TableandData();
						if (null != bean.getInfiledList()) {
							logger.debug("createTable-----表字段的菜单信息不等于空");
							for (Table tables : bean.getInfiledList()) {
								t2.setId(null);
								t2.setUsername(t.getUsername());
								t2.setParentId(t.getId());
								t2.setIcon(DB_MENU_ziduanguanli);
								String talename = tables.getDbtype() + ":" + tables.getDbziduan();
								t2.setName(talename);
								t2.setLevel("6");
								Integer createTabledateint = this.save(t2);
								dBandTableandUserinfo.setZiduanid(t2.getId());
								createTabledatecount++;
								logger.debug("createTable-----添加到表数据到目录成功createTabledateint----" + createTabledateint);
								logger.debug(
										"createTable-----添加到表数据到目录成功条数createTabledatecount----" + createTabledatecount);
							}
							if (createTabledatecount != 0) {
								logger.debug("createTable-----表数据到目录成功");
								logger.debug("createTable-----将数据库信息和个人信息添加到DBandTableandUserinfo");

								// 3创建成功后 保存表信息 id自增的 username=admin
								// linkname=Mysql88 dbname=MysqlTEST
								// tablename=tb_abc dbziduan=id dbtype=int
								// dblong=25 dbisnull=not noll
								// dbiskey=true dbzizeng=AUTO_INCREMENT
								// fielddata=1

								// 保存到数据库信息表里

								if (null != bean.getInfiledList()) {

									logger.debug("createTable-----可以保存表信息 用户信息 数据库");

									for (Table tables : bean.getInfiledList()) {
										dBandTableandUserinfo.setId(null);
										dBandTableandUserinfo.setUid(user.getId().intValue());
										dBandTableandUserinfo.setDbid(biao_parentId.intValue());
										dBandTableandUserinfo.setTableid(t.getId());

										dBandTableandUserinfo.setUsername(parentInfocreateTable.getUsername());
										dBandTableandUserinfo.setLinkname(parentInfocreateTable.getName());
										dBandTableandUserinfo.setDbname(databasename);
										dBandTableandUserinfo.setTablename(biaoName);
										dBandTableandUserinfo.setDbziduan(tables.getDbziduan());
										dBandTableandUserinfo.setDbtype(tables.getDbtype());
										dBandTableandUserinfo.setDblong(tables.getDblong());
										dBandTableandUserinfo.setDbisnull(tables.getDbisnull());
										dBandTableandUserinfo.setDbiskey(tables.getDbiskey());
										dBandTableandUserinfo.setDbzizeng(tables.getDbzizeng());
										dBandTableandUserinfo.setFielddata(null);
										i1 = this.bandTableandUserinfoService.save(dBandTableandUserinfo);
										flag++;
										logger.debug("createTable-----保存表信息 用户信息 数据库成功flag----" + flag);
										logger.debug("createTable-----保存表信息 用户信息 数据库成功i1----" + i1);
									}
									if (flag != 0 && i1 != 0) {

										logger.debug("createTable----可以保存表信息 用户信息 数据库");
										logger.debug("createTable----下面将数据保存到表和字段数据的数据表里");
										for (Table tables : bean.getInfiledList()) {
											tableandData.setId(null);
											tableandData.setDbid(biao_parentId.intValue());
											tableandData.setTableid(t.getId());
											tableandData.setUsername(parentInfocreateTable.getUsername());
											tableandData.setLinkname(parentInfocreateTable.getName());
											tableandData.setDbname(databasename);
											tableandData.setTablename(biaoName);
											tableandData.setDbziduan(tables.getDbziduan());
											tableandData.setDbtype(tables.getDbtype());
											tableandData.setDbzizeng(tables.getDbzizeng());
											tableandData.setZiduandata(null);
											i2 = this.tableandDataService.save(tableandData);
											flag2++;
											logger.debug("createTable-----保存表和字段数据的表添加成功--flag2----" + flag2);
											logger.debug("createTable-----保存表和字段数据信息  数据库成功----i2----" + i2);

										}
										if (flag2 != 0 && i2 != 0) {
											logger.debug("createTable-----保存表和字段数据信息  数据库成功----");
											return true;
										} else {
											logger.debug("createTable-----保存表和字段数据信息  数据库失败----");
											return false;

										}
									} else {
										logger.debug("createTable----保存表信息  用户信息   没有成功");
										return false;

									}

								} else {
									logger.debug("createTable----保存表信息没有成功--bean.getInfiledList()-为空");
									return false;

								}

							} else {
								logger.debug("createTable-----表数据到目录失败");
								return false;

							}
						} else {
							logger.debug("createTable-----bean.getInfiledList()-为空");
							return false;

						}

					} else {
						logger.debug("createTable----创建表的菜单信息失败");
						return false;
					}

				} else {
					logger.debug("createTable------创建表信息没有成功----" + i);
					return false;
				}
			}
		} catch (Exception e) {

			e.printStackTrace();
			logger.debug("createTable----连接数据库失败--");
		}

		return null;
	}

	/**
	 * 判断是否存在表
	 * 
	 * @param biaoName
	 * @param biaonameId
	 * @return
	 */
	@SuppressWarnings("unused")
	public Boolean IsHaveTable(String biaoName, Integer biao_parentId) {
		Integer biaopid = biao_parentId;

		DBMenuandUser databasenameInfo = this.queryInfoByparentid(biao_parentId.intValue());
		if (databasenameInfo == null) {
			logger.debug("IsHaveTable-------databasenameInfo-*-----等于空");
		}
		String databasename = databasenameInfo.getName();// 数据库名MysqlTEST

		// 2获取到父类的数据库名
		DBMenuandUser parentInfocreateTable = this.queryparentid0Byparentid(biao_parentId.intValue());
		logger.debug("IsHaveTable----parentInfocreateTable------" + parentInfocreateTable.getParentId() + "---"
				+ parentInfocreateTable.getUsername() + "-----" + parentInfocreateTable.getName());
		if (parentInfocreateTable == null) {
			logger.debug("IsHaveTable----parentInfobcreateTable等于空");
		}

		// 3通过父类id和admin查询数据库连接信息

		CreateDBandUser createDBandUsercreateTable = this.createDBandUserService.queryInfoBydatabasenameandusername(
				parentInfocreateTable.getUsername(), parentInfocreateTable.getName());
		// 3连接数据库
		if (createDBandUsercreateTable == null) {
			logger.debug("IsHaveTable----createDBandUsercreateTable等于空");
		}
		// 3连接数据库
		String newurl = "jdbc:mysql://" + createDBandUsercreateTable.getIpadress() + ":"
				+ createDBandUsercreateTable.getDatabaseport() + "/" + databasename
				+ "?useSSL=false&serverTimezone=UTC&characterEncoding=utf-8&allowPublicKeyRetrieval=true";
		logger.debug("IsHaveTable-----" + newurl);

		MySQLDButil mdb = MySQLDButil.getInstance();

		Connection createcon = mdb.getConnection(newurl, this.mysqlDBService.username, this.mysqlDBService.password);// 连接新数据库

		if (null != createcon) { // 执行sql语句
			try {

				String seletable = "select t.table_name from information_schema.TABLES t where t.TABLE_SCHEMA ='"
						+ databasename + "' and t.TABLE_NAME ='" + biaoName + "'";
				logger.debug("IsHaveTable----seletable----" + seletable);
				ResultSet resultSet;
				try {
					String isTable = "";
					resultSet = mdb.query(seletable);
					while (resultSet.next()) {
						isTable = resultSet.getString("TABLE_NAME");
						logger.debug("IsHaveTable----是否存在---------" + isTable);
						if (StringUtils.isEmpty(isTable)) {
							logger.debug("-IsHaveTable-----不存在表---------" + isTable);
							// 等于空返回
							return false;
						} else {
							logger.debug("-IsHaveTable-----存在表---------" + isTable);
							// 等于空返回
							return true;
						}
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		logger.debug("存在表有问题---------");
		return false;
	}

	/**
	 * 对应表字段信息
	 * 
	 * @param user
	 * @param biao_parentId
	 * @param biaonameId
	 * @param biaoName
	 * @return
	 */
	@SuppressWarnings("unused")
	public TableInfoBean showTableInfo(User user, Integer biao_parentId, Integer biaoId, String biaoName) {
		// biao_parentId 表id 40 biaonameId 79 tb_141
		// 1通过表biao_parentId 先查询此id的parentid 39 然后查询此39的Id的信息

		DBMenuandUser biaoParentInfo = this.queryInfoByid(biao_parentId);//
		biaoParentInfo.getParentId();

		logger.debug("showTableInfo----biaoParentInfo.getParentId().longValue()---" + biaoParentInfo.getParentId());
		DBMenuandUser databaseInfo = this.queryInfoByid(biaoParentInfo.getParentId());

		// 2获取数据库的连接名
		logger.debug("showTableInfo----databaseInfo.getParentId()---" + databaseInfo.getParentId());
		DBMenuandUser parentInfocreateTable = this.queryparentid0Byparentid(databaseInfo.getParentId());

		logger.debug("showTableInfo----parentInfocreateTable------" + parentInfocreateTable.getParentId() + "---"
				+ parentInfocreateTable.getUsername() + "-----" + parentInfocreateTable.getName());
		if (parentInfocreateTable == null) {
			logger.debug("showTableInfo----parentInfobcreateTable等于空");
		}
		// 3通过父类id和admin查询数据库连接信息

		CreateDBandUser createDBandUsercreateTable = this.createDBandUserService.queryInfoBydatabasenameandusername(
				parentInfocreateTable.getUsername(), parentInfocreateTable.getName());
		// 4通过dbid=databaseId tableid=biaoId user
		// =createDBandUsercreateTable.getUsername()
		// linkname=createDBandUsercreateTable.getDatabaseusername()
		logger.debug("showTableInfo-------databaseInfo.getId()--" + databaseInfo.getId());
		logger.debug("showTableInfo------biaoId.intValue()--" + biaoId.intValue());
		logger.debug("showTableInfo-------createDBandUsercreateTable.getUsername()--"
				+ createDBandUsercreateTable.getUsername());

		logger.debug("showTableInfo-------createDBandUsercreateTable.getDatabasename()--"
				+ createDBandUsercreateTable.getDatabasename());
		logger.debug("showTableInfo------biaoName--" + biaoName);
		DBandTableandUserinfo record = new DBandTableandUserinfo();
		record.setDbid(databaseInfo.getId());
		record.setTableid(biaoId.intValue());
		record.setUsername(createDBandUsercreateTable.getUsername());
		record.setLinkname(createDBandUsercreateTable.getDatabasename());
		record.setTablename(biaoName);
		List<DBandTableandUserinfo> tableandUserinfos = null;
		try {
			tableandUserinfos = this.bandTableandUserinfoService.queryListByWhere(record);
		} catch (Exception e) {
			logger.debug("showTableInfo-------查询有问题--" + tableandUserinfos);

			e.printStackTrace();
		}

		TableInfoBean tableInfoBean = new TableInfoBean();
		tableInfoBean.setInfiledList(tableandUserinfos);
		return tableInfoBean;

	}

	/**
	 * 显示表和字段数据的信息openTable
	 * 
	 * @param user
	 * @param biao_parentId
	 * @param biaoId
	 * @param biaoName
	 * @return
	 */
	@SuppressWarnings("unused")
	public TableandDataBean showTableandDatainfo(User user, Integer biao_parentId, Integer biaoId, String biaoName) {
		// biao_parentId 表id 40 biaonameId 79 tb_141
		// 1通过表biao_parentId 先查询此id的parentid 39 然后查询此39的Id的信息

		DBMenuandUser biaoParentInfo = this.queryInfoByid(biao_parentId);//
		biaoParentInfo.getParentId();

		logger.debug(
				"showTableandDatainfo----biaoParentInfo.getParentId().longValue()---" + biaoParentInfo.getParentId());
		DBMenuandUser databaseInfo = this.queryInfoByid(biaoParentInfo.getParentId());

		// 2获取数据库的连接名
		logger.debug("showTableandDatainfo----databaseInfo.getParentId()---" + databaseInfo.getParentId());
		DBMenuandUser parentInfocreateTable = this.queryparentid0Byparentid(databaseInfo.getParentId());

		logger.debug("showTableandDatainfo----parentInfocreateTable------" + parentInfocreateTable.getParentId() + "---"
				+ parentInfocreateTable.getUsername() + "-----" + parentInfocreateTable.getName());
		if (parentInfocreateTable == null) {
			logger.debug("showTableandDatainfo----parentInfobcreateTable等于空");
		}
		// 3通过父类id和admin查询数据库连接信息

		CreateDBandUser createDBandUsercreateTable = this.createDBandUserService.queryInfoBydatabasenameandusername(
				parentInfocreateTable.getUsername(), parentInfocreateTable.getName());
		// 4通过dbid=databaseId tableid=biaoId user
		// =createDBandUsercreateTable.getUsername()
		// linkname=createDBandUsercreateTable.getDatabaseusername()
		logger.debug("showTableandDatainfo-------databaseInfo.getId()--" + databaseInfo.getId());
		logger.debug("showTableandDatainfo------biaoId.intValue()--" + biaoId.intValue());
		logger.debug("showTableandDatainfo-------createDBandUsercreateTable.getUsername()--"
				+ createDBandUsercreateTable.getUsername());

		logger.debug("showTableandDatainfo-------createDBandUsercreateTable.getDatabasename()--"
				+ createDBandUsercreateTable.getDatabasename());
		logger.debug("showTableandDatainfo------biaoName--" + biaoName);
		TableandData record = new TableandData();
		record.setDbid(databaseInfo.getId());
		record.setTableid(biaoId.intValue());
		record.setUsername(createDBandUsercreateTable.getUsername());
		record.setLinkname(createDBandUsercreateTable.getDatabasename());
		record.setTablename(biaoName);
		List<TableandData> tableandDatas = null;
		try {
			tableandDatas = this.tableandDataService.queryListByWhere(record);
		} catch (Exception e) {
			logger.debug("showTableandDatainfo-------查询有问题--" + tableandDatas);

			e.printStackTrace();
		}

		TableandDataBean tableandDataBean = new TableandDataBean();
		tableandDataBean.setInfiledList(tableandDatas);
		return tableandDataBean;
	}

	/**
	 * 
	 * @param user
	 * @param biao_parentId
	 * @param biaoId
	 * @param biaoName
	 * @param dataList
	 * @return
	 */
	@SuppressWarnings("unused")
	public Data addTabledatas(User user, Integer biao_parentId, Integer biaoId, String biaoName, String dataList) {
		logger.debug("addTabledatas-----biao_parentId---" + biao_parentId);
		logger.debug("addTabledatas-----biaoId---" + biaoId);
		logger.debug("addTabledatas-----biaoName---" + biaoName);
		logger.debug("addTabledatas-----dataList---" + dataList);
		Data datas = new Data();
		Integer dbid = null;
		Integer tableid = null;
		String username = "";
		String linkname = "";
		String dbname = "";
		String tablename = "";
		// INSERT into t;b_418 VALUES(null,'tmac','2'),(null,'tmac','3')
		StringBuffer sb = new StringBuffer("");

		sb.append("INSERT into" + " " + biaoName + " " + "VALUES");

		TableandData record = new TableandData();
		record.setTableid(biaoId);
		List<TableandData> tableandDatas = this.tableandDataService.queryListByWhere(record);

		JSONObject jsonObject = JSON.parseObject(dataList);
		JSONArray jsonArray = jsonObject.getJSONArray("data");
		List<Object> listss = JSONObject.parseArray(jsonArray.toJSONString(), Object.class);

		Iterator<Object> it = jsonArray.iterator();

		int count = 0;
		while (it.hasNext()) {
			// 遍历数组
			sb.append("(");
			JSONObject arrayObj = (JSONObject) it.next();// JSONArray中是很多个JSONObject对象
			for (TableandData data : tableandDatas) {
				dbid = data.getDbid();
				tableid = data.getTableid();
				username = data.getUsername();
				dbname = data.getDbname();
				tablename = data.getTablename();
				linkname = data.getLinkname();
				logger.debug("addTabledatas------1213---->>>" + data.getDbziduan() + "--------->"
						+ arrayObj.get(data.getDbziduan()));

				if (data.getDbtype().equals("varchar")) {
					sb.append("'" + arrayObj.get(data.getDbziduan()) + "',");
				} else {
					sb.append(arrayObj.get(data.getDbziduan()) + ",");
				}

			}
			sb.replace(sb.length() - 1, sb.length(), "");
			sb.append(")");
			if (it.hasNext()) {
				sb.append(",");
			}
			logger.debug("------------------------------------");
	

		}
		String insertSql = sb.toString();
		logger.debug("addTabledatas-----datasql--------" + sb.toString());

		// biao_parentId 表id 40 biaonameId 79 tb_141
		// 1通过表biao_parentId 先查询此id的parentid 39 然后查询此39的Id的信息

		DBMenuandUser biaoParentInfo = this.queryInfoByid(biao_parentId);//
		biaoParentInfo.getParentId();

		logger.debug("addTabledatas----biaoParentInfo.getParentId().longValue()---" + biaoParentInfo.getParentId());
		DBMenuandUser databaseInfo = this.queryInfoByid(biaoParentInfo.getParentId());// db_413Test1

		// 2获取数据库的连接名
		logger.debug("addTabledatas----databaseInfo.getParentId()---" + databaseInfo.getParentId());
		DBMenuandUser parentInfocreateTable = this.queryparentid0Byparentid(databaseInfo.getParentId());

		logger.debug("addTabledatas----parentInfocreateTable------" + parentInfocreateTable.getParentId() + "---"
				+ parentInfocreateTable.getUsername() + "-----" + parentInfocreateTable.getName());
		if (parentInfocreateTable == null) {
			logger.debug("addTabledatas----parentInfobcreateTable等于空");
		}
		// 3通过父类id和admin查询数据库连接信息

		CreateDBandUser createDBandUsercreateTable = this.createDBandUserService.queryInfoBydatabasenameandusername(
				parentInfocreateTable.getUsername(), parentInfocreateTable.getName());
		// 4通过dbid=databaseId tableid=biaoId user
		// =createDBandUsercreateTable.getUsername()
		// linkname=createDBandUsercreateTable.getDatabaseusername()
		logger.debug("addTabledatas-------databaseInfo.getId()--" + databaseInfo.getId());
		logger.debug("addTabledatas------biaoId.intValue()--" + biaoId.intValue());
		logger.debug("addTabledatas-------createDBandUsercreateTable.getUsername()--"
				+ createDBandUsercreateTable.getUsername());

		logger.debug("addTabledatas-------createDBandUsercreateTable.getDatabasename()--"
				+ createDBandUsercreateTable.getDatabasename());
		logger.debug("addTabledatas------biaoName--" + biaoName);
		// 3连接数据库
		String newurl = "jdbc:mysql://" + createDBandUsercreateTable.getIpadress() + ":"
				+ createDBandUsercreateTable.getDatabaseport() + "/" + databaseInfo.getName()
				+ "?useSSL=false&serverTimezone=UTC&characterEncoding=utf-8&allowPublicKeyRetrieval=true";
		logger.debug("addTabledatas----newurl-----" + newurl);
		MySQLDButil mdb = MySQLDButil.getInstance();
		TableandData addDatas = new TableandData();

		List<Map<String, Object>> list2Json = new ArrayList<Map<String, Object>>();
		try {
			Connection createcon = mdb.getConnection(newurl, this.mysqlDBService.username,
					this.mysqlDBService.password);// 连接新数据库
			if (null != createcon) { // 执行sql语句
				try {
					Integer i = mdb.addTableDatas(insertSql);
					logger.debug("addTabledatas------sql--" + i);
					if (i.intValue() != 0) {
						String sql = "select * from " + " " + biaoName;
						PreparedStatement stmt;
						try {
							stmt = createcon.prepareStatement(sql);

							ResultSet resultSet = stmt.executeQuery();
							// 获取各个列的信息
							ResultSetMetaData metaData = resultSet.getMetaData();

							int columeCount = metaData.getColumnCount();
							for (int a = 1; a < columeCount; a++) {
								logger.debug("addTabledatas------getColumnName--" + metaData.getColumnLabel(a) + "\t");

							}

							while (resultSet.next()) {
								// 输出一行数据
								Map<String, Object> map = new LinkedHashMap<String, Object>();
								for (int j = 0; j < columeCount; j++) {

									String str = resultSet.getString(j + 1);

									map.put(metaData.getColumnLabel(j + 1), str);

									/*
									 * addDatas.setId(null);
									 * addDatas.setDbid(dbid);
									 * addDatas.setTableid(tableid);
									 * addDatas.setUsername(username);
									 * addDatas.setLinkname(linkname);
									 * addDatas.setDbname(dbname);
									 * addDatas.setTablename(biaoName);
									 * addDatas.setDbziduan(metaData.
									 * getColumnLabel(j+1));
									 * addDatas.setZiduandata(str);
									 * addDatas.setDbzizeng(null);
									 * this.tableandDataService.save(addDatas);
									 */
									logger.debug("addTabledatas------getColumnName--" + metaData.getColumnLabel(j + 1)
											+ "->>>>>>>" + str + "\t");
									logger.debug("--------");

								}
								list2Json.add(map);
								logger.debug("----2----");
							}

							datas.setData(list2Json);
							String listJson = JSON.toJSONString(list2Json);
							logger.debug("addTabledatas--------list2Json-------" + listJson);

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return datas;
	}
	
	
	
	/*
	 * 
	 * 返回显示字段
	 */
	@SuppressWarnings("unused")
	public 	List<Map<String, Object>> showTableZDdata(User user, Integer biao_parentId, Integer biaoId, String biaoName) {
		logger.debug("showTableZDdata-----biao_parentId---" + biao_parentId);
		logger.debug("showTableZDdata-----biaoId---" + biaoId);
		logger.debug("showTableZDdata-----biaoName---" + biaoName);
	
		Data datas = new Data();
		DBMenuandUser biaoParentInfo = this.queryInfoByid(biao_parentId);//
		biaoParentInfo.getParentId();

		logger.debug("showTableZDdata----biaoParentInfo.getParentId().longValue()---" + biaoParentInfo.getParentId());
		DBMenuandUser databaseInfo = this.queryInfoByid(biaoParentInfo.getParentId());// db_413Test1

		// 2获取数据库的连接名
		logger.debug("showTableZDdata----databaseInfo.getParentId()---" + databaseInfo.getParentId());
		DBMenuandUser parentInfocreateTable = this.queryparentid0Byparentid(databaseInfo.getParentId());

		logger.debug("showTableZDdata----parentInfocreateTable------" + parentInfocreateTable.getParentId() + "---"
				+ parentInfocreateTable.getUsername() + "-----" + parentInfocreateTable.getName());
		if (parentInfocreateTable == null) {
			logger.debug("showTableZDdata----parentInfobcreateTable等于空");
		}
		// 3通过父类id和admin查询数据库连接信息

		CreateDBandUser createDBandUsercreateTable = this.createDBandUserService.queryInfoBydatabasenameandusername(
				parentInfocreateTable.getUsername(), parentInfocreateTable.getName());
		// 4通过dbid=databaseId tableid=biaoId user
		// =createDBandUsercreateTable.getUsername()
		// linkname=createDBandUsercreateTable.getDatabaseusername()
		logger.debug("showTableZDdata-------databaseInfo.getId()--" + databaseInfo.getId());
		logger.debug("showTableZDdata------biaoId.intValue()--" + biaoId.intValue());
		logger.debug("showTableZDdata-------createDBandUsercreateTable.getUsername()--"
				+ createDBandUsercreateTable.getUsername());

		logger.debug("showTableZDdata-------createDBandUsercreateTable.getDatabasename()--"
				+ createDBandUsercreateTable.getDatabasename());
		logger.debug("showTableZDdata------biaoName--" + biaoName);
		// 3连接数据库
		String newurl = "jdbc:mysql://" + createDBandUsercreateTable.getIpadress() + ":"
				+ createDBandUsercreateTable.getDatabaseport() + "/" + databaseInfo.getName()
				+ "?useSSL=false&serverTimezone=UTC&characterEncoding=utf-8&allowPublicKeyRetrieval=true";
		logger.debug("showTableZDdata----newurl-----" + newurl);
		MySQLDButil mdb = MySQLDButil.getInstance();
		TableandData addDatas = new TableandData();

		List<Map<String, Object>> list2Json = new ArrayList<Map<String, Object>>();
		
		
		try {
			Connection createcon = mdb.getConnection(newurl, this.mysqlDBService.username,
					this.mysqlDBService.password);// 连接新数据库
			if (null != createcon) { // 执行sql语句}
				String sql = "select * from " + " " + biaoName;
				PreparedStatement stmt;
				try {
					stmt = createcon.prepareStatement(sql);

					ResultSet resultSet = stmt.executeQuery();
					// 获取各个列的信息
					ResultSetMetaData metaData = resultSet.getMetaData();

					int columeCount = metaData.getColumnCount();
					for (int a = 1; a < columeCount; a++) {
						logger.debug("addTabledatas------getColumnName--" + metaData.getColumnLabel(a) + "\t");

					}

					while (resultSet.next()) {
						// 输出一行数据
						Map<String, Object> map = new LinkedHashMap<String, Object>();
						for (int j = 0; j < columeCount; j++) {

							String str = resultSet.getString(j + 1);

							map.put(metaData.getColumnLabel(j + 1), str);

							logger.debug("addTabledatas------getColumnName--" + metaData.getColumnLabel(j + 1)
									+ "->>>>>>>" + str + "\t");
							logger.debug("--------");

						}
						list2Json.add(map);
						logger.debug("----2----");
					}

					datas.setData(list2Json);
					String listJson = JSON.toJSONString(list2Json);
					logger.debug("showTableZDdata--------list2Json-------" + listJson);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				logger.debug("showTableZDdata-------连接失败" );
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list2Json;
		
	}
}
