package com.dbmanagesys.manage.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dbmanagesys.manage.DB.MySQLDButil;
import com.dbmanagesys.manage.common.service.MysqlDBService;
import com.dbmanagesys.manage.mapper.CreateDBandUserMapper;
import com.dbmanagesys.manage.pojo.CreateDBandUser;
import com.dbmanagesys.manage.pojo.DBMenuandUser;
import com.dbmanagesys.manage.pojo.User;
import com.dbmanagesys.manage.service.redis.RedisService;

@Service
public class CreateDBandUserService extends BaseService<CreateDBandUser> {
	@Autowired
	private MysqlDBService mysqlDBService;

	@Autowired
	private CreateDBandUserMapper createDBandUserMapper;

	@Autowired
	private RedisService redisService;

	public MySQLDButil mdb;

	public Connection con;
	public ResultSet rs;
	@Autowired
	private DataBaseService dataBaseService;

	private static final String DB_NAME = "db_managesys_test";

	private static final String DB_MENU_1 = "el-icon-lx-yunfuwuqi";
	private static final String DB_MENU_shujuku = "el-icon-lx-yunshujuku";

	private static final String DB_MENU_shujukubiao = "el-icon-lx-shujukubiao";
	private static final String DB_MENU_ziduanguanli = "el-icon-lx-ziduanguanli";

	private static final Logger logger = LoggerFactory.getLogger(CreateDBandUserService.class);

	/*
	 * 测试连接
	 * 
	 * jdbc:mysql://192.168.99.101:32769/db_managesys_test?useSSL=false&
	 * serverTimezone=UTC&characterEncoding=utf-8&allowPublicKeyRetrieval=true
	 */ public List<Map<String, Object>> checkLink(String databasetype, String ipadress, String databaseport,
			String databaseusername, String databasepassword) {

		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		// 从redis中饭获取 get 如果调用这个接口 就是用户在访问 所以要设置redis生存时间

		String url = "jdbc:mysql://" + ipadress + ":" + databaseport + "/" + DB_NAME
				+ "?useSSL=false&serverTimezone=UTC&characterEncoding=utf-8&allowPublicKeyRetrieval=true";

		// 首先判断数据库类型
		switch (databasetype) {
		case "MySQL":
			// 判断连接信息
			if (url.equals(this.mysqlDBService.url) && databaseusername.equals(this.mysqlDBService.username)
					&& databasepassword.equals(this.mysqlDBService.password)) {
				// 如果连接显示所以数据库
				String sql = "show DATABASES";

				// 调用mysql 操作类 连接
				try {
					mdb = MySQLDButil.getInstance();
					con = mdb.getConnection(url, databaseusername, databasepassword);
					rs = mdb.query(sql);
					list = MySQLDButil.ResultSettoList(rs);
					if (list != null) {

						return list;
					}

					return null;

				} catch (Exception e) {

					e.printStackTrace();
				} finally {
					mdb.close(rs, null, con);
				}

			} else {
				return null;
			}
			break;

		default:
			break;
		}

		return null;
	}

	/**
	 * mysql连接是否成功 将数据库名写入另一个表
	 * 
	 * @param databasetype
	 * @param ipadress
	 * @param databaseport
	 * @param databaseusername
	 * @param databasepassword
	 * @return
	 */

	public Boolean toLink(User user, String databasetype, String ipadress, String databaseport, String databaseusername,
			String databasepassword, String databasename) {

		// 测试连接
		List<Map<String, Object>> list = this.checkLink(databasetype, ipadress, databaseport, databaseusername,
				databasepassword);
		//如果连接信息都存在 直接返回
		//TODO
		CreateDBandUser bandUser=  this.queryInfoByALL(user, databasetype, ipadress, databaseport, databaseusername, databasepassword, databasename);
		if(null!=bandUser)
		{
			return true;
		}
		else{
		if (null != list) {
			CreateDBandUser createDBandUser = new CreateDBandUser();
			createDBandUser.setUsername(user.getUsername());// admin
			createDBandUser.setDatabasetype(databasetype); // mysql
			createDBandUser.setIpadress(ipadress);// 192.16899.101
			createDBandUser.setDatabaseport(databaseport);// 32769
			createDBandUser.setDatabasename(databasename);// MSQL8
			createDBandUser.setDatabaseusername(databaseusername);// root
			createDBandUser.setDefaultdatabase(DB_NAME);// DB_
			createDBandUser.setUserdbid(null);

			try {
				// 保存数据 到表数据库连接配置信息
				Integer i1 = this.save(createDBandUser);
				// 保存到目录
				DBMenuandUser t = new DBMenuandUser();
				t.setUsername(user.getUsername());// admin
				t.setParentId(0);
				t.setIcon(DB_MENU_1);
				t.setName(databasename);
				t.setLevel("1");
				t.setId(null);

				Integer i2 = this.dataBaseService.save(t);

				// 添加数据库的名字
				DBMenuandUser t2 = new DBMenuandUser();
				t2.setName("数据库");
				t2.setParentId(t.getId());
				t2.setIcon(DB_MENU_shujuku);
				t2.setUsername(t.getUsername());
				t2.setLevel("2");
				t2.setId(null);

				Integer i3 = this.dataBaseService.save(t2);

				return i1.intValue() == 1 && i2.intValue() == 1 && i3.intValue() == 1;
			} catch (Exception e) {

				e.printStackTrace();
			}

		 }
		}
		return false;

	}

	/**
	 * 根据username username 查询数据库配置信息
	 * 
	 * @param username
	 * @param databasename
	 * @return
	 */
	public CreateDBandUser queryInfoBydatabasenameandusername(String username, String databasename) {
		CreateDBandUser record = new CreateDBandUser();
		record.setUsername(username);
		record.setDatabasename(databasename);

		CreateDBandUser dBandUser = this.queryOne(record);

		if (null == dBandUser) {
			logger.debug("dBandUser等于空" + dBandUser);
			return null;
		}
		return dBandUser;

	}

	
	public CreateDBandUser queryInfoByALL(User user,String databasetype, String ipadress, String databaseport, String databaseusername,
			String databasepassword, String databasename) {
		String url = "jdbc:mysql://" + ipadress + ":" + databaseport + "/" + DB_NAME
				+ "?useSSL=false&serverTimezone=UTC&characterEncoding=utf-8&allowPublicKeyRetrieval=true";
		// 首先判断数据库类型
		switch (databasetype) {
		case "MySQL":
			// 判断连接信息
			if ( url.equals(this.mysqlDBService.url) && databaseusername.equals(this.mysqlDBService.username)
					&& databasepassword.equals(this.mysqlDBService.password)) {
				CreateDBandUser record = new CreateDBandUser();
				record.setUsername(user.getUsername());
				record.setDatabasename(databasename);
				record.setDatabaseusername(databaseusername);
				record.setDatabaseport(databaseport);
				record.setIpadress(ipadress);

				CreateDBandUser dBandUser = this.queryOne(record);

				if (null == dBandUser) {
					logger.debug("queryInfoByALL等于空" + dBandUser);
					return null;
				}
				return dBandUser;

			} else {
				return null;
			}
		

		default:
			break;
		}
		return null;
		

	}

}
