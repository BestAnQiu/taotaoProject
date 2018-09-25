package com.taotao.sso.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.CookieUtils;
import com.taotao.common.util.JsonUtils;
import com.taotao.sso.service.UserLoginService;

@Controller
public class UserLoginController {

	@Autowired
	private UserLoginService loginService;
	
	@Value("${TT_TOKEN_KEY}")
	private String TT_TOKEN_KEY;
	
	/**
	 * url:/user/login
	 * 参数：username password
	 * 返回值：json
	 * 请求限定的方法：post
	 */
	@RequestMapping(value="/user/login",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult login(HttpServletRequest request,HttpServletResponse response,
			String username,String password){
		//1.引入服务
		//2.注入服务
		//3.调用服务
		TaotaoResult result = loginService.login(username, password);
		if (result.getStatus()==200) {
			CookieUtils.setCookie(request, response, TT_TOKEN_KEY, result.toString());
		}
		
		return result;
	}
	
	/**
	 * 跟据令牌号查询对象的用户信息
	 * @param token
	 * @return
	 */
	@RequestMapping(value="/user/token/{token}",method=RequestMethod.GET,produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public String getUserByToken(@PathVariable String token,String callback){
		
		//判断是否是Jsonp请求
		if(StringUtils.isNotBlank(callback)){
			//如果是jsonp 需要拼接 类似于fun({id:1});
			TaotaoResult result = loginService.getUserByToken(token);
			String jsonpstr = callback+"("+JsonUtils.objectToJson(result)+")";
			return jsonpstr;
		}
		//如果不是jsonp
		//1.调用服务
		TaotaoResult result = loginService.getUserByToken(token);
		return JsonUtils.objectToJson(result);
	}
}
