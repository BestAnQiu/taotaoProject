package com.taotao.cart.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.cart.jedis.JedisClient;
import com.taotao.cart.service.CartService;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.CookieUtils;
import com.taotao.common.util.JsonUtils;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbUser;

@Service
public class CartServiceImpl implements CartService{

	@Autowired
	private JedisClient client;
	
	@Value("${TT_CART_REDIS_PRE_KEY}")
	private String TT_CART_REDIS_PRE_KEY;
	
	@Override
	public TaotaoResult addItemCart(TbItem item, Integer num, Long userId) {
		// 1.查询  可以根据 key 和field获取某一个商品
		TbItem itemtem = queryItemByItemIdAndUserId(item.getId(),userId);
		// 2.判断要添加的商品是否存在于列表中
		if (itemtem!=null) {
			// 3.如果存在，直接数量相加
			itemtem.setNum(itemtem.getNum()+num);
			client.hset(TT_CART_REDIS_PRE_KEY+":"+userId+"", itemtem.getId()+"", JsonUtils.objectToJson(itemtem));
		}else {
			// 4.如果不存在，直接添加到redis中
			//查询商品的数据 （商品的名称商品的价格，商品的图片。。） 调用商品的服务  直接从controller中传递
			//.设置商品的数量
			item.setNum(num);
			if (item.getImage()!=null) {
				item.setImage(item.getImage().split(",")[0]);
			}
			client.hset(TT_CART_REDIS_PRE_KEY+":"+userId+"", itemtem.getId()+"", JsonUtils.objectToJson(itemtem));
		}
		return TaotaoResult.ok();
	}

	private TbItem queryItemByItemIdAndUserId(Long itemId, Long userId) {
		String string = client.hget(TT_CART_REDIS_PRE_KEY+":"+userId+"", itemId+"");
		if (StringUtils.isNotBlank(string)) {
			TbItem tbItem = JsonUtils.jsonToPojo(string, TbItem.class);
			return tbItem;
		}
		return null;
	}
	
	@Override
	public TaotaoResult mergeCart(Long userId, List<TbItem> itemList) {
		//调用添加的方法，合并数据
		for (TbItem tbItem : itemList) {
			addItemCart(tbItem, tbItem.getNum(), userId);
		}
		return TaotaoResult.ok();
	}

	@Override
	public List<TbItem> getCartList(Long userId) {
		//跟据用户id查询购物车列表
		Map<String, String> map = client.hgetAll(TT_CART_REDIS_PRE_KEY+":"+userId+"");
		List<TbItem> list=new ArrayList<>();
		if (map!=null) {
			for ( Map.Entry<String, String> entry : map.entrySet()) {
				String value = entry.getValue();
				//创建一个item对象
				TbItem item = JsonUtils.jsonToPojo(value, TbItem.class);
				//添加到列表
				list.add(item);
			}
		}
		return list;
	}

	@Override
	public TaotaoResult updateItemCartByItemId(Long userId, Long itemId, Integer num) {
		TbItem tbItem = queryItemByItemIdAndUserId(itemId, userId);
		if (tbItem!=null) {
			tbItem.setNum(num);
			client.hset(TT_CART_REDIS_PRE_KEY+":"+userId+"", itemId+"", JsonUtils.objectToJson(tbItem));
		}
		return TaotaoResult.ok();
	}

	@Override
	public TaotaoResult deleteItemCartByItemId(Long userId, Long itemId) {
		client.hdel(TT_CART_REDIS_PRE_KEY+":"+userId+"", itemId+"", itemId+"");
		return TaotaoResult.ok();
	}
	
}
