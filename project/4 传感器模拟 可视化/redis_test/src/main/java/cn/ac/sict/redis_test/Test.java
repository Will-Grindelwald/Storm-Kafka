package cn.ac.sict.redis_test;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.json.simple.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class Test {

	private static String[] Topic = { "ljc_sensor_temper", "ljc_sensor_pressure" };
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

			double value;
			int direct = -1;
			long time;

			int i = 1000;
			while ((i--) != 0) {
				for (int type = 0; type < 2; type++) { // 0 for temper, 1 for pressure
					if (rnd.nextInt() % 2 == 0)
						direct *= -1;
					randValue[type] = randValue[type] + direct * rnd.nextInt(1000) * rnd.nextGaussian() / 10000; // 下一个尽量连续的随机数
					while (randValue[type] > max[type])
						randValue[type] -= rnd.nextDouble() / 1000;
					while (randValue[type] < min[type])
						randValue[type] += rnd.nextDouble() / 1000;
					value = (int) (randValue[type] * 10000) / 10000.0; // 精度为 4 位小数
					// json: { time: ***, value: *** }
					HashMap<String, String> jsonMap_temper = new HashMap<String, String>();
					time = new Date().getTime() * 1000 + System.nanoTime() % 1000000 / 1000;
					jsonMap_temper.put("time", Long.toString(time));
					jsonMap_temper.put("value", Double.toString(value));
					jedis.publish(Topic[type], JSONObject.toJSONString(jsonMap_temper));
					System.out.println(jsonMap_temper);
				}

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
