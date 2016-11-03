package cn.ac.sict.kafka_producer_sensor;

import java.util.Date;
import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class SensorProducer extends Thread {

	private static final String partitioner = "cn.ac.sict.kafka_producer_sensor.SimplePartitioner";
	private static final String[] Topic = {"ljc_temper", "ljc_pressure"};
	private static final int[] min = {30, 3}, max = {70, 5};
	private final int NO; // 组号, 用于分区

	private final Producer<String, String> producer;

	public SensorProducer(String kafkaStr, int no) {
		Properties props = new Properties();
		props.put("bootstrap.servers", kafkaStr);
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("partitioner.class", partitioner);
		this.NO = no;
		this.producer = new KafkaProducer<String, String>(props);
	}

	@Override
	public void run() {
		Random rnd = new Random(System.nanoTime());
		String topic = null, key = null, msg = null;
		int no;
		try {
			//int i = 1000000;
			//long startTime = System.nanoTime(); // 获取开始时间
			//while ((i--) != 0) {
			while (true) {
				no = rnd.nextInt(2); // 0 温度, 1 压力
				topic = Topic[no];
				key = (NO) + ""; // 组号, 用于分区
				msg = (new Date().getTime() * 1000 + System.nanoTime() % 1000000 / 1000) + ":" + (min[no] + rnd.nextInt((max[no] - min[no]) * 1000) / 1000.0);
				this.producer.send(new ProducerRecord<String, String>(topic, key, msg));
			}
			//long endTime = System.nanoTime(); // 获取结束时间
			//System.out.println((endTime - startTime) / 1000000000.0);
		} finally {
			this.producer.close();
		}
	}

}
