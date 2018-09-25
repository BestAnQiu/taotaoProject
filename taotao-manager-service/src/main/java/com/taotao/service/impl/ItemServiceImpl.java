package com.taotao.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.IDUtils;
import com.taotao.common.util.JsonUtils;
import com.taotao.manager.jedis.JedisClient;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemExample;
import com.taotao.pojo.TbItemExample.Criteria;
import com.taotao.service.ItemService;

@Service
public class ItemServiceImpl implements ItemService {
	
	@Autowired
	private TbItemMapper mapper;
	
	@Autowired
	private TbItemDescMapper descmapper;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private JedisClient client;
	
	@Resource(name="topicDestination")
	private Destination destination;
	
	@Value("${ITEM_INFO_KEY}")
	private String ITEM_INFO_KEY;

	@Value("${ITEM_INFO_KEY_EXPIRE}")
	private Integer ITEM_INFO_KEY_EXPIRE;
	
	@Override
	public EasyUIDataGridResult getItemList(Integer page, Integer rows) {
		//设置分页信息
		if (page==null)page=1;
		if (rows==null)rows=30;
		//注入mapper
		PageHelper.startPage(page, rows);
		//执行查询
		TbItemExample example=new TbItemExample();
		List<TbItem> list = mapper.selectByExample(example);
		//获取分页信息
		PageInfo<TbItem> info = new PageInfo<>(list);
		EasyUIDataGridResult result=new EasyUIDataGridResult();
		result.setTotal((int)info.getTotal());
		result.setRows(info.getList());
		return result;
	}

	@Override
	public TaotaoResult saveItem(TbItem item, String desc) {
		//生成商品的id
		final long itemId = IDUtils.genItemId();
		item.setId(itemId);
		item.setCreated(new Date());
		//1-正常，2-下架，3-删除',
		item.setStatus((byte)1);
		item.setUpdated(item.getCreated());
		//2.插入到item表 商品的基本信息表
		mapper.insertSelective(item);
		//3.补全商品描述中的属性
		TbItemDesc desc2=new TbItemDesc();
		desc2.setItemDesc(desc);
		desc2.setItemId(itemId);
		desc2.setCreated(item.getCreated());
		desc2.setUpdated(item.getCreated());
		//4.插入商品描述数据
		//注入tbitemdesc的mapper
		descmapper.insertSelective(desc2);
		
		// 添加发送消息的业务逻辑
		jmsTemplate.send(destination,new MessageCreator() {
			
			@Override
			public Message createMessage(Session session) throws JMSException {
				return session.createTextMessage(itemId+"");
			}
		});
		
		//5.返回taotaoresult
		return TaotaoResult.ok();
	}

	@Override
	public TbItem getItemById(Long itemId) {
		//从缓存中添加数据
		try {
			String jsonstr = client.get(ITEM_INFO_KEY+":"+itemId+":BASE");
			if (StringUtils.isNotBlank(jsonstr)) {
				client.expire(ITEM_INFO_KEY + ":" + itemId + ":BASE", ITEM_INFO_KEY_EXPIRE);
				return JsonUtils.jsonToPojo(jsonstr, TbItem.class);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		//如果没有数据，添加缓存
		TbItem tbItem = mapper.selectByPrimaryKey(itemId);
		
		//添加缓存到Redis数据库中
		// 注入jedisclient
		// ITEM_INFO:123456:BASE
		// ITEM_INFO:123456:DESC
		try {
			client.set(ITEM_INFO_KEY+":"+itemId+":BASE",JsonUtils.objectToJson(tbItem));
			//添加缓存石时间
			client.expire(ITEM_INFO_KEY+":"+itemId+":BASE", ITEM_INFO_KEY_EXPIRE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tbItem;
	}

	@Override
	public TbItemDesc getItemDescById(Long itemId) {
		//从缓存中添加数据
		try {
			String jsonstr = client.get(ITEM_INFO_KEY+":"+itemId+":BASE");
			if (StringUtils.isNotBlank(jsonstr)) {
				System.out.println("有缓存");
				client.expire(ITEM_INFO_KEY + ":" + itemId + ":BASE", ITEM_INFO_KEY_EXPIRE);
				return JsonUtils.jsonToPojo(jsonstr, TbItemDesc.class);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//如果没有数据，添加缓存
		TbItemDesc itemDesc=descmapper.selectByPrimaryKey(itemId);
		
		//添加缓存到Redis数据库中
				// 注入jedisclient
				// ITEM_INFO:123456:BASE
				// ITEM_INFO:123456:DESC
				try {
					client.set(ITEM_INFO_KEY+":"+itemId+":BASE",JsonUtils.objectToJson(itemDesc));
					//添加缓存石时间
					client.expire(ITEM_INFO_KEY+":"+itemId+":BASE", ITEM_INFO_KEY_EXPIRE);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return itemDesc;
	}
}
