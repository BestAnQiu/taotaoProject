package com.taotao.order.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.mapper.TbOrderItemMapper;
import com.taotao.mapper.TbOrderMapper;
import com.taotao.mapper.TbOrderShippingMapper;
import com.taotao.order.jedis.JedisClient;
import com.taotao.order.pojo.OrderInfo;
import com.taotao.order.service.OrderService;
import com.taotao.pojo.TbOrderItem;
import com.taotao.pojo.TbOrderShipping;

@Service
public class OrderServiceImpl implements OrderService {
	
	@Autowired
	private JedisClient client;
	
	@Autowired
	private TbOrderMapper mapper;
	@Autowired
	private TbOrderItemMapper orderitemmapper;
	@Autowired
	private TbOrderShippingMapper shippingmapper;
	
	@Value("${GEN_ORDER_ID_KEY}")
	private String GEN_ORDER_ID_KEY;
	
	@Value("${GEN_ORDER_ID_INIT}")
	private String GEN_ORDER_ID_INIT;
	
	@Value("${GEN_ORDER_ITEM_ID_KEY}")
	private String GEN_ORDER_ITEM_ID_KEY;
	
	@Override
	public TaotaoResult createOrder(OrderInfo info) {
		//1.插入订单表
		//通过redis的incr 生成订单id
		//判断如果key没存在 需要初始化一个key设置一个初始值
		if (!client.exists(GEN_ORDER_ID_KEY)) {
			client.set(GEN_ORDER_ITEM_ID_KEY, GEN_ORDER_ID_INIT);
		}
		String orderId=client.incr(GEN_ORDER_ID_KEY).toString();
		//补全其他的属性
		info.setCreateTime(new Date());
		info.setOrderId(orderId);
		info.setPostFee("0");
		info.setStatus(1);
		info.setUpdateTime(info.getCreateTime());
		//注入mapper
		mapper.insert(info);
		List<TbOrderItem> orderItems = info.getOrderItems();
		for (TbOrderItem tbOrderItem : orderItems) {
			String id=client.incr(GEN_ORDER_ITEM_ID_KEY).toString();
			tbOrderItem.setId(id);
			tbOrderItem.setOrderId(orderId);
			//插入订单项表
			orderitemmapper.insert(tbOrderItem);
		}
		//3.插入订单物流表
		//设置订单id
		TbOrderShipping shipping=new TbOrderShipping();
		shipping.setOrderId(orderId);
		shipping.setCreated(info.getCreateTime());
		shipping.setUpdated(info.getCreateTime());
		shippingmapper.insert(shipping);
		
		//返回需要包含订单的ID
		return TaotaoResult.ok(orderId);
	}

}
