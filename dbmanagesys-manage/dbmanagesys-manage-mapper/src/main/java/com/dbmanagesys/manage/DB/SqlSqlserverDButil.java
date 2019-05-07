package com.dbmanagesys.manage.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.dbmanagesys.manage.common.utils.DBBaseutil;

/**
 * SqlServer数据库操作类
 * @author TJW
 *
 */
public class SqlSqlserverDButil implements DBBaseutil{


	@Override
	public void close(ResultSet rs, PreparedStatement pstmt, Connection con) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Connection getConnection(String url, String username, String password) {
		// TODO Auto-generated method stub
		return null;
	}

}
