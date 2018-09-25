package com.taotao.search.test;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

public class SolrTest {
	
	@Test
	public void add() throws SolrServerException, IOException{
		//1.创建solrserver   建立连接 需要指定地址
		SolrServer solrServer=new HttpSolrServer("http://192.168.25.128:8080/solr");
		//2.创建solrinputdocument
		SolrInputDocument document=new SolrInputDocument();
		//3.向文档中添加域
		document.addField("id", "test001");
		document.addField("item_title", "测试");
		//4.将文档提交到索引库中
		solrServer.add(document);
		//5.提交
		solrServer.commit();
	}
}
