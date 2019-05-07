package com.dbmanagesys.manage.DB;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dbmanagesys.manage.common.service.MysqlDBService;
import com.dbmanagesys.manage.common.utils.DBBaseutil;


/**
 * Mysql数据库操作类
 * 
 * @author TJW
 *
 */
public class MySQLDButil implements DBBaseutil {

	
	
    
	 private static final Logger logger = LoggerFactory.getLogger(MySQLDButil.class);
	
	
	private static MySQLDButil per = new MySQLDButil();
	// 定义数据库的链接
	private static Connection con = null;

	// 定义sql语句的执行对象
	private static PreparedStatement pstmt = null;
	// 定义查询返回的结果集合
	private static ResultSet resultSet = null;
    
	private Statement smt;
	@Autowired
	private MysqlDBService mysqlDBService;

	private MySQLDButil() {

	}

	/**
	 * 加载数据库配置信息,并给相关的属性赋值
	 */
	public static void loadConfig() {
		try {

		} catch (Exception e) {
			throw new RuntimeException("读取数据库配置文件异常！", e);
		}
	}

	/**
	 * 单例类，获得工具类一个对象
	 * 
	 * @return
	 */
	public static MySQLDButil getInstance() {
		if (per == null) {
			per = new MySQLDButil();
			// 读取配置文件
			per.registeredDriver();
		}
		return per;
	}

	/**
	 * 注册驱动
	 */
	private void registeredDriver() {
		try {
			

			Class.forName(this.mysqlDBService.driverClassName);
			System.out.println("注册驱动成功！");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	
	/**
	 * 创建连接
	 */
	@Override
	public Connection getConnection(String url,String username,String password) {
		try {
			con = DriverManager.getConnection(url, username,
					password);
			logger.info("this.url"+url+
					"username"+username+
					"password"+password);
			logger.info("con"+con);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.info("url"+url+
					"username"+username+
					"password"+password);
			e.printStackTrace();
		}
		logger.debug("连接数据库成功!!");
		return con;
	}

	

	/**
	 * 关闭
	 */
	@Override
	public void close(ResultSet rs, PreparedStatement pstmt, Connection con) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (pstmt != null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	/**
	 * 查询返回结果集
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	
	public  ResultSet query(String sql) throws Exception {

		try {

			/*con =  MySQLDButil.getInstance().getConnection();*/
			pstmt = (PreparedStatement) con.prepareStatement(sql);

			resultSet = pstmt.executeQuery(sql);
			//pstmt.executeUpdate(sql);
			return resultSet;

		} catch (SQLException sqle) {

			throw new SQLException("select data Exception12311123: "
					+ sqle.getMessage());

		} catch (Exception e) {

			throw new Exception("System error: " + e.getMessage());

		}

	}
	/**
	 * 
	 * @param sql
	 * @return
	 */
	public Integer getSQL(String sql)
	{
		
		try {
			smt = con.createStatement();
			Integer i=smt.executeUpdate(sql);
			logger.debug("创建新数据库成功!!");
			 return i;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.debug("创建新数据库失败!!");
			e.printStackTrace();
		}
		

		return 0;
		
	}
	/**
	 * 
	 * @param sql
	 * @return
	 */
	public Integer createTable(String sql)
	{
		/*con =  MySQLDButil.getInstance().getConnection();*/
		try {
			pstmt = (PreparedStatement) con.prepareStatement(sql);
			Integer i=pstmt.executeUpdate(sql);
			logger.debug("创建新表成功!!");
			return i;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.debug("创建新表失败!!");
			e.printStackTrace();
		}

	
		
		return 0;
		
	}
	
	
	/**
	 * 
	 * @param sql
	 * @return
	 */
	public Integer addTableDatas(String sql)
	{
		/*con =  MySQLDButil.getInstance().getConnection();*/
		try {
			pstmt = (PreparedStatement) con.prepareStatement(sql);
			Integer i=pstmt.executeUpdate(sql);
			logger.debug("添加数据库addTableDatas-------表数据成功!!");
			return i;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			logger.debug("addTableDatas-------创建新表失败!!");
			e.printStackTrace();
		}

	
		
		return 0;
		
	}
	public static List<Map<String, Object>>  ResultSettoList(ResultSet  rs) throws SQLException
	{
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		ResultSetMetaData md = rs.getMetaData(); //获得结果集结构信息,元数据
		int columnCount = md.getColumnCount();   //获得列数 

		if(rs!=null)
		{
			while(rs.next())
			{
				Map<String,Object> rowData = new HashMap<String,Object>();
				for (int i = 1; i <= columnCount; i++) {
					rowData.put(md.getColumnName(i), rs.getObject(i));
				}
				list.add(rowData);


			}
		}
		return list;
		
	}



}
