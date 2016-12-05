package cn.ac.sict.gqb.demoredis;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

public class TestRedis {

	public static void main(String args[]) {

		System.out.println("单机模式：");
		Jedis jedis = new Jedis("192.168.125.173");
		System.out.println("Redis server is runing");
		System.out.println("Test Redis...." + jedis.ping());
		jedis.close();
		System.out.println("集群模式：");
		Set<HostAndPort> jedisClusterNodes = new HashSet<HostAndPort>();
		jedisClusterNodes.add(new HostAndPort("192.168.125.171", 6279));
		jedisClusterNodes.add(new HostAndPort("192.168.125.172", 6279));
		jedisClusterNodes.add(new HostAndPort("192.168.125.173", 6279));
		jedisClusterNodes.add(new HostAndPort("192.168.125.171", 7000));
		jedisClusterNodes.add(new HostAndPort("192.168.125.172", 7000));
		jedisClusterNodes.add(new HostAndPort("192.168.125.173", 7000));

		JedisCluster jc = new JedisCluster(jedisClusterNodes);
		jc.set("test", "hello world");
		String value = jc.get("test");
		System.out.println(value);
		try {
			jc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
