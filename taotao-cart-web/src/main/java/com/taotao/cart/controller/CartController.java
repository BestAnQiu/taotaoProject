package com.taotao.cart.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.taotao.cart.service.CartService;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.CookieUtils;
import com.taotao.common.util.JsonUtils;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbUser;
import com.taotao.service.ItemService;
import com.taotao.sso.service.UserLoginService;

@Controller
public class CartController {
	
	@Autowired
	private ItemService itemService;
	@Autowired
	private UserLoginService loginservice;
	@Autowired
	private CartService cartservice;
	
	@Value("${TT_TOKEN_KEY}")
	private String TT_TOKEN_KEY;
	@Value("${TT_CART_KEY}")
	private String TT_CART_KEY;
	
	@RequestMapping("/cart/add/{itemId}")
	public String addItemCart(@PathVariable Long itemId, Integer num, HttpServletRequest request,
			HttpServletResponse response){
		// 3.判断用户是否登录
		// 从cookie中获取用户的token信息
		String token = CookieUtils.getCookieValue(request, TT_TOKEN_KEY, true);
		// 调用SSO的服务查询用户的信息
		TaotaoResult result = loginservice.getUserByToken(token);
		// 获取商品的数据
		TbItem tbItem = itemService.getItemById(itemId);
		
		if (result.getStatus()==200) {
			// 4.如果已登录，调用service的方法
			TbUser user = (TbUser) result.getData();
			cartservice.addItemCart(tbItem, num, user.getId());
		}else {
			// 5.如果没有登录 调用设置到cookie的方法
			// 先根据cookie获取购物车的列表
			
			List<TbItem> cartList = getCookieCartList(request);
			boolean flag=false;
			// 判断如果购物车中有包含要添加的商品 商品数量相加
			for (TbItem tbItem2 : cartList) {
				if (tbItem.getId()==itemId.longValue()) {
					// 找到列表中的商品 更新数量
					tbItem.setNum(tbItem.getNum()+num);
					flag=true;
					break;
				}
			}
			if (flag==true) {
				CookieUtils.setCookie(request, response, TT_CART_KEY, JsonUtils.objectToJson(cartList), 7 * 24 * 3600, true);
			}else {
				// 如果没有就直接添加到购物车
				// 调用商品服务
				// 设置数量
//				TbItem tbItem = itemService.getItemById(itemId);
				tbItem.setNum(num);
				// 设置图片为一张
				if (tbItem.getImage()!=null) {
					tbItem.setImage(tbItem.getImage().split(",")[0]);
				}
				// 添加商品到购物车中
				cartList.add(tbItem);
				// 设置到cookie中
				CookieUtils.setCookie(request, response, TT_CART_KEY, JsonUtils.objectToJson(cartList), 7 * 24 * 3600,true);
			}
		}
		return "cartSuccess";
	}

	/**
	 * 返回购物车信息列表
	 * @param request
	 * @return
	 */
	@RequestMapping("/cart/cart")
	public String getCartList(HttpServletRequest request,HttpServletResponse response){
		List<TbItem> cartList = getCookieCartList(request);
		// 判断用户是否登录
		// 从cookie中获取用户的token信息
		String token = CookieUtils.getCookieValue(request, TT_TOKEN_KEY, true);
		// 调用SSO的服务查询用户的信息
		TaotaoResult result = loginservice.getUserByToken(token);
		if (result.getStatus()==200) {
			TbUser user = (TbUser) result.getData();
			//如果不为空，把cookie和购物车的商品合并
			cartservice.mergeCart(user.getId(), cartList);
			//删除购物车的数据
			CookieUtils.deleteCookie(request, response, TT_CART_KEY);
			//从服务中获取商品列表
			cartList = cartservice.getCartList(user.getId());
		}
		
		request.setAttribute("cartList", cartList);
		// 将数据传递到页面中
		return "cart";
	}
	
	/**
	 * ajax更新商品的数量和价格
	 */
	@RequestMapping("/cart/update/num/{itemId}/{num}")
	public TaotaoResult updateItemCartByItemId(@PathVariable Long itemId,@PathVariable Integer num,
			HttpServletRequest request,HttpServletResponse response){
		
		String token = CookieUtils.getCookieValue(request, TT_TOKEN_KEY, true);
		TaotaoResult result = loginservice.getUserByToken(token);
		if (result.getStatus()==200) {
			TbUser user = (TbUser) result.getData();
			cartservice.updateItemCartByItemId(user.getId(), itemId, num);
		}else {
			List<TbItem> cartList = getCookieCartList(request);
			for (TbItem tbItem : cartList) {
				if (tbItem.getId()==itemId.longValue()) {
					tbItem.setNum(num);
					break;
				}
			}
			CookieUtils.setCookie(request, response, TT_CART_KEY, JsonUtils.objectToJson(cartList), 7 * 24 * 3600, true);
		}
		return TaotaoResult.ok();
	}
	
	/**
	 *删除商品 
	 */
	@RequestMapping("/cart/delete/{itemId}")
	public String deleteItemCartByItemId(@PathVariable Long itemId,HttpServletRequest request,
			HttpServletResponse response){
		String token = CookieUtils.getCookieValue(request, TT_TOKEN_KEY, true);
		TaotaoResult result = loginservice.getUserByToken(token);
		if (result.getStatus()==200) {
			TbUser user = (TbUser) result.getData();
			cartservice.deleteItemCartByItemId(user.getId(), itemId);
		}else {
			// 1.从cookie中获取商品的列表
			List<TbItem> cartList = getCookieCartList(request);
			// 2.判断 商品是否存在于商品的列表中
			for (TbItem tbItem : cartList) {
				if (tbItem.getId()==itemId.longValue()) {
					// 找到要删除的商品
					cartList.remove(tbItem);
					break;
				}
			}
			CookieUtils.setCookie(request, response, TT_CART_KEY, JsonUtils.objectToJson(cartList),  7 * 24 * 3600, true);
		}
		return "redirect:/cart/cart.html";
	}	
	
	/**
	 *购物车的列表 
	 */
	private List<TbItem> getCookieCartList(HttpServletRequest request) {
		// 从cookie中获取商品的列表
		String jsonstr = CookieUtils.getCookieValue(request, TT_CART_KEY, true);
		// 讲商品的列表的JSON转成 对象
		if (StringUtils.isNotBlank(jsonstr)) {
			List<TbItem> list = JsonUtils.jsonToList(jsonstr, TbItem.class);
			return list;
		}
		return new ArrayList<>();
	}
}
