package cn.ac.sict.ljc.kafka_producer_sensor;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

class Signal {
	double value;
	long time;
}

public class SensorProducer extends Thread {

	private static final String[] Topic = {"ljc_sensor_temper_in", "ljc_sensor_pressure_in"};
	private static final int[] min = {30, 3}, max = {70, 5};

	private static final String partitioner = SimplePartitioner.class.getName();
	private final int NO; // 组号, 用于分区

	private final Producer<String, String> producer;

	private final Random rnd;
	private final double[] randValue;

	public SensorProducer(String kafkaStr, int no) {
		Properties props = new Properties();
		props.put("bootstrap.servers", kafkaStr);
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", StringSerializer.class.getName());
		props.put("value.serializer", StringSerializer.class.getName());
		props.put("partitioner.class", partitioner);
		this.producer = new KafkaProducer<String, String>(props);
		this.NO = no;
		this.rnd = new Random(System.nanoTime());
		this.randValue = new double[]{ min[0] + rnd.nextInt((max[0] - min[0]) * 1000) / 1000.0,
				min[1] + rnd.nextInt((max[1] - min[1]) * 1000) / 1000.0 };
	}

	@Override
	public void run() {
		try {
			String key = (NO) + "", msg = null; // key 组号, 用于分区
			
			// int i = 1000;
			// long startTime = System.nanoTime(); // 获取开始时间
			// while ((i--) != 0) {
			while (true) {
				for (int type = 0; type < 2; type++) { // 0 for temper, 1 for pressure
					Signal signal = sensor(type);
					msg = signal.time + ":" + type + ":" + signal.value;
					producer.send(new ProducerRecord<String, String>(Topic[type], key, msg));
					System.out.println(msg);
				}

				// sleep 为便于观察
				try {
					Thread.sleep(999);
				} catch (InterruptedException e) {
				}
			}
			// long endTime = System.nanoTime(); // 获取结束时间
			// System.out.println((endTime - startTime) / 1000000000.0);
		} finally {
			this.producer.close();
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

}
