package com.taotao.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.print.attribute.standard.Media;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.taotao.common.pojo.EasyUIDataGridResult;
import com.taotao.common.pojo.TaotaoResult;
import com.taotao.common.util.JsonUtils;
import com.taotao.pojo.TbItem;
import com.taotao.service.ItemService;
import com.taotao.web.util.FastDFSClient;

@Controller
public class ItemContorller {
	//配置spring容器引入配置文件属性
	@Value("${TAOTAO_IMAGE_SERVER_URL}")
	private String TAOTAO_IMAGE_SERVER_URL;
	
	@Autowired
	private ItemService itemService;
	
	/**
	 * 首页分页
	 * @param page
	 * @param rows
	 * @return
	 */
	@RequestMapping(value="/item/list")
	@ResponseBody
	public EasyUIDataGridResult getItemList(Integer page,Integer rows){
		EasyUIDataGridResult result = itemService.getItemList(page, rows);
		return result;
	}
	
	/**
	 * 上传图片FastDFS服务器
	 * @param uploadFile
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/pic/upload",produces=MediaType.TEXT_PLAIN_VALUE+";+charset=utf-8")
	@ResponseBody
	//就需要将字符串转成jONS 格式的字符串就可以了
	public String uploadImage(MultipartFile uploadFile) throws Exception{
		// 1.获取元文件的扩展名
		String originalFilename = uploadFile.getOriginalFilename();
		String extName = originalFilename.substring(originalFilename.lastIndexOf(".")+1);
		// 2.获取文件的字节数组
		byte[] bytes = uploadFile.getBytes();
		// 3.通过fastdfsclient的方法上传图片（参数要求有 字节数组 和扩展名 不包含"."）
		FastDFSClient client = new FastDFSClient("classpath:resource/fastdfs.conf");
		// 返回值：group1/M00/00/00/wKgZhVk4vDqAaJ9jAA1rIuRd3Es177.jpg
		String string = client.uploadFile(bytes, extName);
		//拼接成完整的URL
		//"http://192.168.25.133/"
		String path=TAOTAO_IMAGE_SERVER_URL+string;
		// 4.成功时，设置map
		Map<String,Object> map=new HashMap<>();
		map.put("error", 0);
		map.put("url", path);
		return JsonUtils.objectToJson(map);
	}
	
	/**
	 * 添加商品
	 * @param item
	 * @param desc
	 * @return
	 */
	@RequestMapping(value="/item/save",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult saveItem(TbItem item,String desc){
		//1.引入服务
		//2.注入服务
		//3.调用
		return itemService.saveItem(item, desc);
	}
}
