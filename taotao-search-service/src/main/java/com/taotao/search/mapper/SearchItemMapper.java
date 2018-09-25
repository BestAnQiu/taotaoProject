package com.taotao.search.mapper;

import java.util.List;

import com.taotao.common.pojo.SearchItem;

public interface SearchItemMapper {
	/**
	 * 查询所有的商品
	 * @return
	 */
	public List<SearchItem> getSearchItemList();
	/**
	 * 根据商品的id查询商品的数据
	 * @param itemId
	 * @return
	 */
	public SearchItem getSearchItemById(Long itemId);
}
