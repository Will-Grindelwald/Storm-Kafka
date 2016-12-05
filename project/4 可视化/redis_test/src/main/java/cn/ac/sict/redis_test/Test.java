package cn.ac.sict.redis_test;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.json.simple.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Test {

	private static String[] Topic = { "ljc_input_sensor_temper", "ljc_input_sensor_pressure" };
	private static int[] min = { 30, 3 }, max = { 70, 5 };

	public static void main(String[] args) {
		String host = "192.168.125.171";
		int port = 6378;
		String password = "yourpassword";

		JedisPool pool = new JedisPool(new JedisPoolConfig(), host, port);
		try (Jedis jedis = pool.getResource()) {
			jedis.auth(password);
			// System.out.println("Server is running: " + jedis.ping());

			Random rnd = new Random(System.nanoTime());
			double[] randValue = { min[0] + rnd.nextInt((max[0] - min[0]) * 1000) / 1000.0,
					min[1] + rnd.nextInt((max[1] - min[1]) * 1000) / 1000.0 };

			int direct = -1;
			double value_temper, value_pressure;
			long time;

			int i = 1000;
			while ((i--) != 0) {
				if (rnd.nextInt() % 2 == 0)
					direct *= -1;
				randValue[0] = randValue[0] + direct * rnd.nextInt(1000) * rnd.nextGaussian() / 1000; // 下一个尽量连续的随机数
				while (randValue[0] > max[0])
					randValue[0] -= rnd.nextDouble() / 1000;
				while (randValue[0] < min[0])
					randValue[0] += rnd.nextDouble() / 1000;
				value_temper = (int) (randValue[0] * 10000) / 10000.0; // 精度为 4 位小数

				if (rnd.nextInt() % 2 == 0)
					direct *= -1;
				randValue[1] = randValue[1] + direct * rnd.nextInt(1000) * rnd.nextGaussian() / 1000; // 下一个尽量连续的随机数
				while (randValue[1] > max[1])
					randValue[1] -= rnd.nextDouble() / 1000;
				while (randValue[1] < min[1])
					randValue[1] += rnd.nextDouble() / 1000;
				value_pressure = (int) (randValue[1] * 10000) / 10000.0; // 精度为 4 位小数

				// json: { time: ***, value: *** }
				HashMap<String, String> jsonMap_temper = new HashMap<String, String>();
				time = new Date().getTime() * 1000 + System.nanoTime() % 1000000 / 1000;
				jsonMap_temper.put("time", Long.toString(time));
				jsonMap_temper.put("value", Double.toString(value_temper));
				jedis.publish(Topic[0], JSONObject.toJSONString(jsonMap_temper));
				System.out.println(jsonMap_temper);

				HashMap<String, String> jsonMap_pressure = new HashMap<String, String>();
				time = new Date().getTime() * 1000 + System.nanoTime() % 1000000 / 1000;
				jsonMap_pressure.put("time", Long.toString(time));
				jsonMap_pressure.put("value", Double.toString(value_pressure));
				jedis.publish(Topic[1], JSONObject.toJSONString(jsonMap_pressure));
				System.out.println(jsonMap_pressure);

				// sleep 为便于观察
				try {
					Thread.sleep(999);
				} catch (InterruptedException e) {
				}
			}
		}
		pool.close();
	}
}
