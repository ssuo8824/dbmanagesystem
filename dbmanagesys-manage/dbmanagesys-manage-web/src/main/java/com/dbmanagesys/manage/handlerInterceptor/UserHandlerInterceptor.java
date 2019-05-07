package com.dbmanagesys.manage.handlerInterceptor;

import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.dbmanagesys.manage.controller.DataBaseController;
import com.dbmanagesys.manage.pojo.User;
import com.dbmanagesys.manage.service.CreateDBandUserService;
import com.dbmanagesys.manage.service.UserService;
import com.dbmanagesys.manage.thtreadlocal.UserThreadLocal;







public class UserHandlerInterceptor implements HandlerInterceptor{

	private static final Logger logger = LoggerFactory.getLogger(UserHandlerInterceptor.class);
	
	 @Autowired
	  private UserService userService;
	 
	 @Autowired
	 private CreateDBandUserService createDBandUserService;

     
     static  public  String databasetype;
     static public  String databasename;
     static public  String ipadress;
     static public  String databaseport;
     static public String databaseusername;
     static public  String databasepassword;
     static public  boolean iSlink;
   /**
    * //拦截前处理
    */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
         
		String token = request.getHeader("Authentication-Token");
		if(StringUtils.isEmpty(token))
		{
			//未登录
			response.setStatus(401);
			return false;
		}
		//判断token中的数据是否存在
	   
        User user=	this.userService.queryUserByToken(token);
        if(null ==user)
        {
        	//登录失效
        	response.setStatus(401);
			return false;
        }
        
        
        System.out.println("进入拦截器--------------------");
        if(handler instanceof HandlerMethod) {
            HandlerMethod h = (HandlerMethod)handler;
           if(h.getMethod().getName()=="toLink")
           {
        	  	/*Map map = request.getParameterMap();
                if (map != null && !map.isEmpty()) {
                    Set<String> keySet = map.keySet();
                    for (String key : keySet) {               
                            String[] values = (String[]) map.get(key);
                            for (String value : values) {
                            	logger.info("> " + key + "=" + value);
                            }              
                    }
                }*/
       
        	         databasetype= request.getParameter("databaseChoice");
        	         databasename=  request.getParameter("databasename");
        	         ipadress=   request.getParameter("ipadress");
        	         databaseport=    request.getParameter("databaseport");
        	         databaseusername=    request.getParameter("databaseusername");
        	         databasepassword=   request.getParameter("databasepassword");
        		  
           }
        }
        System.out.println("出拦截器--------------------");
        request.getParameter("databaseChoice");

        logger.debug("preHandle---- request.getParameter----------"+ databasetype);
 	   logger.debug("preHandle---- request.getParameter----------"+ databasename);
 	   logger.debug("preHandle---- request.getParameter----------"+ ipadress);
 	   logger.debug("preHandle---- request.getParameter----------"+ databaseport);
 	   logger.debug("preHandle---- request.getParameter----------"+ databaseusername);
 	   logger.debug("preHandle---- request.getParameter----------"+ databasepassword);
 	   if(databasetype!=null){
 			 Boolean boolean1 = this.createDBandUserService.toLink(user, databasetype, ipadress, databaseport,
 					databaseusername, databasepassword, databasename);
 			 iSlink=boolean1;
 		  	UserThreadLocal.setalldataBaseType(databasetype);
 		  	if (boolean1!=false) {
 		  	   //在拦截器作出结果后 将写入线程 做数据复制
 		        UserThreadLocal.setisLink(iSlink);
 		  	}else{
 		  	//登录失效
 	     	response.setStatus(412);
 				return false;
 		  	}
 	   }
	  
        
     

    	//记录类型 MySQL
	
        //在拦截器作出结果后 将user写入线程 做数据复制
        UserThreadLocal.set(user);
      
   	
    	return true;
	
	}
  
	  /**
	    * //拦截后处理
	    */
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
	

		
	}
  
	  /**
	    *  //全部完成后处理
	    */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		UserThreadLocal.set(null);//清空本地线程中的user对象数据    线程池 确保每次都是新的user
	}


}
