package com.dbmanagesys.manage.mapper;

import java.util.List;


import com.dbmanagesys.manage.pojo.DBMenuandUser;
import com.dbmanagesys.manage.pojo.DBMenuandUserList;
import com.github.abel533.mapper.Mapper;

public interface DBMenuandUserListMapper {
	List<DBMenuandUserList> queryListByIdandUsername(String username);
}
