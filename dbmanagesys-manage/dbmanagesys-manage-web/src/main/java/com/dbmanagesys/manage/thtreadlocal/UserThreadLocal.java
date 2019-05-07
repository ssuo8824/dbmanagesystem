package com.dbmanagesys.manage.thtreadlocal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbmanagesys.manage.controller.DataBaseController;
import com.dbmanagesys.manage.pojo.User;

public class UserThreadLocal {
	private static final Logger logger = LoggerFactory.getLogger(UserThreadLocal.class);
     private static final ThreadLocal<User> LOCAL =new  ThreadLocal<User>();
     private static final ThreadLocal<Boolean> LOCALisLink =new  ThreadLocal<Boolean>();
     private static final ThreadLocal<String> LOCALalldataBaseType =new  ThreadLocal<String>();
     private  UserThreadLocal()
     {
    	 
     }
     
     
     public static void setisLink(Boolean isLink)
     {
    	 logger.debug(" setisLink------"+isLink);
    	 LOCALisLink.set(isLink);
     }
     
     
     public static Boolean getisLink()
     {
    	 return LOCALisLink.get();
     }
    		 
     
     
     public static void setalldataBaseType(String alldataBaseType)
     {
    	 logger.debug(" setalldataBaseType------"+alldataBaseType);
    	 LOCALalldataBaseType.set(alldataBaseType);
     }
     
     
     public static String getalldataBaseType()
     {
    	 return LOCALalldataBaseType.get();
     }
     
     
     public static void set(User user)
     {
    	 logger.debug(" setUser------"+user);
    	 LOCAL.set(user);
     }
     
     
     public static User get()
     {
    	 return LOCAL.get();
     }
    		 
}
