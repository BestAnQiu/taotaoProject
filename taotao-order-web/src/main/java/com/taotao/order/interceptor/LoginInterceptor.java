package com.taotao.order.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.CookieUtils;
import com.taotao.sso.service.UserLoginService;

public class LoginInterceptor implements HandlerInterceptor {
	
	@Autowired
	private UserLoginService loginservice;
	
	@Value("${TT_TOKEN_KEY}")
	private String TT_TOKEN_KEY;
	@Value("${SSO_URL}")
	private String SSO_URL;
	
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
		
		//1.取cookie中的token 
		//1.从cookie中获取用户的token 
		String token = CookieUtils.getCookieValue(request, TT_TOKEN_KEY);
		//2.判断token是否存在，
		if (StringUtils.isEmpty(token)) {
			//3.如果不存在，说明没登录   ---》重定向到登录的页面
			//request.getRequestURL().toString()：就是访问的URL localhost:8092/order/order-cart.html
			response.sendRedirect(SSO_URL+"/page/login?redirect="+request.getRequestURI().toString());
			return false;
		}
		
		//4.如果token存在，调用SSO的服务 查询用户的信息（看是否用户已经过期）
		TaotaoResult result = loginservice.getUserByToken(token);
		if (result.getStatus()!=200) {
			//5.用户已经过期  --》重定向到登录的页面
			response.sendRedirect(SSO_URL+"/page/login?redirect="+request.getRequestURI().toString());
			return false;
		}
		//6.用户没过期（说明登录了）--》放行
		//设置用户信息到request中 ，目标方法的request就可以获取用户的信息
		request.setAttribute("USER_INFO", result.getData());
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {

	}
	
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		
	}
}
