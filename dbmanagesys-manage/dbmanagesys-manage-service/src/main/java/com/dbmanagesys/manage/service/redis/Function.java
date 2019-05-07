package com.dbmanagesys.manage.service.redis;

public interface Function<T,E>{
   
	public T callback(E e);
}
