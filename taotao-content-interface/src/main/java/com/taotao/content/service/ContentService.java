package com.taotao.content.service;

import java.util.List;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;

public interface ContentService {
	/**
	 * 新增内容
	 * @param tbContent
	 * @return
	 */
	public TaotaoResult addContent(TbContent tbContent);
	
	public List<TbContent> getContentListByCatId(Long cid);
}
