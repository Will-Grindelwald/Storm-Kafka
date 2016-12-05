package cn.ac.sict.ljg.redisTest;

import java.util.Random;

import org.json.simple.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisTest {
		public static void main(String[] args) {
				JedisPool pool =new  JedisPool(new JedisPoolConfig(),"192.168.125.171");
				Random random =new Random();
				long currentTimeMillis = System.currentTimeMillis()/1000;
				try(Jedis jRedis = pool.getResource() ){
					 for(int i =0;i<10000;i++){
						 	int number = random.nextInt(100);
						 	JSONObject msg =new JSONObject();
						 	msg.put("time", currentTimeMillis+=1);
						 	msg.put("number",number);
						 	jRedis.publish("messages" , msg.toString()); 
						 	System.out.println(msg.toString() );
						 	Thread.sleep(1000);
						   
					 }
					 
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			  pool.destroy();
		}
}
