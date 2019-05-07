package com.dbmanagesys.manage.common.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
/**
 * 基本数据库操作类
 * @author TJW
 *
 */
public interface DBBaseutil {
	//得到Connection链接 
	 Connection getConnection(String url,String username,String password);
	 //统一的资源关闭函数 
	 void close(ResultSet rs,PreparedStatement pstmt , Connection con);
	 
}
