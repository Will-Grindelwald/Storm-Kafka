package cn.ac.sict.ljc.kafka_producer_prof_test;

import java.util.*;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

/**
 * 
 */
public class TestProducer {

	public static final String kafkaStr = "master-cent7-1:9092,master-cent7-2:9092,master-cent7-3:9092";
	public static final String partitioner = SimplePartitioner.class.getName();

	private final Producer<String, String> producer;

	public static void main(String[] args) {
		TestProducer producer = new TestProducer();
		producer.send(args[0], args[1]); // args[0] 为要发送的 topic, args[1] 为要生成的随机消息数
	}

	public TestProducer() {
		Properties props = new Properties();
		props.put("bootstrap.servers", kafkaStr); // 定义了生产者可以找到的 Borker
		props.put("acks", "all"); // 是否要求消息回执
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		// 指定序列化处理类，默认为 org.apache.kafka.common.serialization.DefaultEncoder, 即 byte[]
		props.put("key.serializer", StringSerializer.class.getName()); // key 的序列化处理类
		props.put("value.serializer", StringSerializer.class.getName()); // value 的序列化处理类
		// 指定分区处理类，默认为 org.apache.kafka.clients.producer.internals.DefaultPartitioner
		props.put("partitioner.class", partitioner); // 定义了使用哪个类来决定消息的分区

		this.producer = new KafkaProducer<String, String>(props);
	}

	private TestProducer send(String topicStr, String numbers) {
		long events = Long.parseLong(numbers);
		long startTime = System.nanoTime(); // 获取开始时间
		int count = 0;
		try {
			for (long nEvents = 1; nEvents <= events; nEvents++) {
				count++;
				String msg = System.nanoTime() + ":" + count;
				producer.send(new ProducerRecord<String, String>(topicStr, nEvents + "", msg));
				if ((System.nanoTime() - startTime) / 1000000000 >= 1) {
					System.out.println("每秒发送: " + count + "条数据, 已发" + nEvents + "条数据");
					startTime = System.nanoTime();
					count = 0;
				}
			}
		} finally {
			producer.close();
		}
		return this;
	}

}
