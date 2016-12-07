package cn.ac.sict.ljc.redis_test;

import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.json.simple.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

class Signal {
	double value;
	long time;
}

public class TestRedis {

	private static final String[] Topic = {"ljc_sensor_temper", "ljc_sensor_pressure"};
	private static final int[] min = {30, 3}, max = {70, 5};

	private static final String host = "192.168.125.171";
	private static final int port = 6378;
	private static final String password = "yourpassword";

	private final JedisPool pool;

	private final Random rnd;
	private final double[] randValue;

	public static void main(String[] args) {
		TestRedis redis = new TestRedis();
		redis.publish();
		redis.close();
	}

	public TestRedis() {
		this.pool = new JedisPool(new JedisPoolConfig(), host, port);
		this.rnd = new Random(System.nanoTime());
		this.randValue = new double[]{ min[0] + rnd.nextInt((max[0] - min[0]) * 1000) / 1000.0,
				min[1] + rnd.nextInt((max[1] - min[1]) * 1000) / 1000.0 };
	}

	public void publish() {
		try (Jedis jedis = pool.getResource()) {
			jedis.auth(password);
			// System.out.println("Server is running: " + jedis.ping());

			// int i = 1000;
			// long startTime = System.nanoTime(); // 获取开始时间
			// while ((i--) != 0) {
			while (true) {
				for (int type = 0; type < 2; type++) { // 0 for temper, 1 for pressure
					Signal signal = sensor(type);
					// json: { time: ***, value: *** }
					HashMap<String, String> jsonMap = new HashMap<String, String>();
					jsonMap.put("time", Long.toString(signal.time));
					jsonMap.put("value", Double.toString(signal.value));
					jedis.publish(Topic[type], JSONObject.toJSONString(jsonMap));
					System.out.println(jsonMap);
				}

				// sleep 为便于观察
				try {
					Thread.sleep(999);
				} catch (InterruptedException e) {
				}
			}
			// long endTime = System.nanoTime(); // 获取结束时间
			// System.out.println((endTime - startTime) / 1000000000.0);
		}
	}

	private Signal sensor(int type) {
		int direct = -1;
		Signal signal = new Signal();
		if (rnd.nextInt() % 2 == 0)
			direct *= -1;
		randValue[type] = randValue[type] + direct * rnd.nextInt(1000) * rnd.nextGaussian() / 10000; // 下一个尽量连续的随机数
		while (randValue[type] > max[type])
			randValue[type] -= rnd.nextDouble() / 1000;
		while (randValue[type] < min[type])
			randValue[type] += rnd.nextDouble() / 1000;
		signal.value = (int) (randValue[type] * 10000) / 10000.0; // 精度为 4 位小数
		signal.time = new Date().getTime() * 1000 + System.nanoTime() % 1000000 / 1000;
		return signal;
	}

	public void close() {
		pool.close();
	}
}
