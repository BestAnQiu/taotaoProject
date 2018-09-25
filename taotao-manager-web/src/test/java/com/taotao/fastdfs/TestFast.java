package com.taotao.fastdfs;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;

import com.taotao.web.util.FastDFSClient;

public class TestFast {
	
	@Test
	public void testUpload() throws FileNotFoundException, IOException, MyException{
		//1.创建一个配置文件  配置连接tracker的服务器地址
		//2.初始化加载配置文件
		ClientGlobal.init("D:/eclipse/workspace1.7/taotao/taotao-manager-web/src/main/resources/resource/fastdfs.conf");
		//3.创建trackerClient对象
		TrackerClient client=new TrackerClient();
		//4.通过trackerClient获取trackerServer对象
		TrackerServer trackerServer=client.getConnection();
		//5.创建storegeServer 赋值为null
		StorageServer storageServer=null;
		//6.创建storgeClient  需要两个参数：trackserver   storageServer
		StorageClient storageClient=new StorageClient(trackerServer, storageServer);
		//7.调用上传图片的方法 
		//第一个参数：本地文件的路径
		//第二个参数：文件的扩展名 不带"."
		//第三个参数：元数据     
		String[] upload_file = storageClient.upload_file("C:/Users/anqiu/Pictures/Camera Roll/QQ图片20170711144600.jpg", "jpg", null);
		//8.打印图片的地址 ，测试访问
		for (String string : upload_file) {
			System.out.println(string);
		}
	}
	
	@Test
	public void testFastClient() throws Exception{
		FastDFSClient client=new FastDFSClient("D:/eclipse/workspace1.7/taotao/taotao-manager-web/src/main/resources/resource/fastdfs.conf");
		String uploadFile=client.uploadFile("C:/Users/anqiu/Pictures/我的照片/外教/IMG_20160524_172917.jpg");
		System.out.println(uploadFile);
	}
	
}
