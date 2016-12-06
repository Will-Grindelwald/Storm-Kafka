package cn.ac.sict.ljc.kafka_producer_demo2;

import java.util.*;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

/**
 * demo1 使用的是老的 Scala 客户端 API 这个 demo 使用的是新的 Java 客户端 API(在 org.apache.kafka.clients package), 注意添加 maven 依赖
 * 是线程安全的
 * 参考: http://kafka.apache.org/090/javadoc/index.html?org/apache/kafka/clients/producer/KafkaProducer.html
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
		Random rnd = new Random();
		try {
			long startTime = System.nanoTime(); // 获取开始时间
			for (long nEvents = 0; nEvents < events; nEvents++) {
				long runtime = new Date().getTime();
				String ip = "192.168.2." + rnd.nextInt(255);
				String msg = runtime + ", " + ip; // 此处模拟一个 website 的访问记录
				// 参数为: topic, 用于分区的 key, message
				producer.send(new ProducerRecord<String, String>(topicStr, ip, msg));
			}
			long endTime = System.nanoTime(); // 获取结束时间
			System.out.println((endTime - startTime) / 1000000000.0);
		} finally {
			producer.close();
		}
		return this;
	}

}
