package com.taotao.sso.service;

import com.taotao.common.pojo.TaotaoResult;
import com.taotao.pojo.TbUser;

public interface UserRegisterService {
	/**
	 * 根据参数和类型来校验数据
	 * @param param 根据参数和类型来校验数据
	 * @param type 1  2  3  分别代表 username,phone,email
	 * @return
	 */
	public TaotaoResult checkData(String param,Integer type);
	/**
	 * 注册用户
	 * @param user
	 * @return
	 */
	public TaotaoResult register(TbUser user);
}
