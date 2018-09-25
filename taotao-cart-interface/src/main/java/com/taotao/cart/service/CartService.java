package com.taotao.cart.service;

import java.util.List;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbUser;

public interface CartService {
	/**
	 *添加购物车 
	 */
	public TaotaoResult addItemCart(TbItem item,Integer num,Long userId);
	
	/**
	 * 未登录下的购物车和登陆的购物车合并
	 */
	public TaotaoResult mergeCart(Long userId,List<TbItem> itemList);
	/**
	 *获得用户数据的列表 
	 */
	public List<TbItem> getCartList(Long userId);
	/**
	 * 跟新商品数量
	 * @param userId
	 * @param itemId
	 * @param num
	 * @return
	 */
	public TaotaoResult updateItemCartByItemId(Long userId,Long itemId,Integer num);
	/**
	 * 删除商品
	 * @param userId
	 * @param itemId
	 * @return
	 */
	public TaotaoResult deleteItemCartByItemId(Long userId,Long itemId);
}
