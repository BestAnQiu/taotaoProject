/*package com.taotao.test.jedis;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;

public class JedisTest {
	*//**
	 * 单机版的启动
	 *//*
	@Test
	public void testJedis() {
		Jedis jedis=new Jedis("192.168.25.128", 6379);
		jedis.set("key123", "value");
		System.out.println(jedis.get("key123"));	
		jedis.close();
	}
	*//**
	 * 连接池启动
	 *//*
	@Test
	public void testJedisPool(){
		JedisPool pool=new JedisPool("192.168.25.128", 6379);
		Jedis jedis = pool.getResource();
		jedis.set("keypool", "valuepool");
		System.out.println(jedis.get("keypool"));
		jedis.close();
		pool.close();
	}
	*//**
	 * 集群测试连接
	 *//*
	@Test
	public void testJedisCluster(){
		// 第一步：使用JedisCluster对象。需要一个Set<HostAndPort>参数。Redis节点的列表。
		Set<HostAndPort> nodes=new HashSet<>();
		nodes.add(new HostAndPort("192.168.25.128", 7001));
		nodes.add(new HostAndPort("192.168.25.128", 7002));
		nodes.add(new HostAndPort("192.168.25.128", 7003));
		nodes.add(new HostAndPort("192.168.25.128", 7004));
		nodes.add(new HostAndPort("192.168.25.128", 7005));
		nodes.add(new HostAndPort("192.168.25.128", 7006));
		JedisCluster jedisCluster=new JedisCluster(nodes);
		// 第二步：直接使用JedisCluster对象操作redis。在系统中单例存在。
		jedisCluster.set("hello", "world");
		String result = jedisCluster.get("hello");
		System.out.println(result);
		jedisCluster.close();
	}
}
*/