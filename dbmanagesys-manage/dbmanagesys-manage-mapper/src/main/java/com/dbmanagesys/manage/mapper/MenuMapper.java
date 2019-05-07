package com.dbmanagesys.manage.mapper;

import java.util.List;

import com.dbmanagesys.manage.pojo.Menu;



public interface MenuMapper {
	List<Menu> queryListByIdandMapper(Long Id);
}
