package com.taotao.service;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;

public interface ItemService {
	/**
	 * 查询商品列表
	 * @param page
	 * @param rows
	 * @return
	 */
	public EasyUIDataGridResult getItemList(Integer page,Integer rows);
	
	/**
	 * 添加商品基本数据和描述数据
	 * @param item
	 * @param desc
	 * @return
	 */
	public TaotaoResult saveItem(TbItem item,String desc);
	/**
	 * 根据商品的id查询商品的描述
	 * @param itemId
	 * @return
	 */
	public TbItem getItemById(Long itemId);
	/**
	 * 根据商品的id查询商品的描述
	 * @param itemId
	 * @return
	 */
	public TbItemDesc getItemDescById(Long itemId);
	
}

