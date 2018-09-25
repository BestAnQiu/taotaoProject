package com.taotao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.service.ItemService;

@Controller
public class PageController {
	
	/**
	 * 展示首页
	 * @return
	 */
	@RequestMapping(value="/")
	public String showIndex(){
		return "index";
	}
	
	/**
	 * restful风格路径变量接收
	 * @param page
	 * @return
	 */
	@RequestMapping(value="/{page}")
	public String showPage(@PathVariable String page){
		return page;
	}
	
}
