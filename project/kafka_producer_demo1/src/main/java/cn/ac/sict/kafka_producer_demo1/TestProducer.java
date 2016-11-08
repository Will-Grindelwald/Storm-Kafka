package cn.ac.sict.kafka_producer_demo1;

import java.util.*;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * from: https://cwiki.apache.org/confluence/display/KAFKA/0.8.0+Producer+Example
 */
public class TestProducer {

	public static final String kafkaStr = "master-cent7-1:9092,master-cent7-2:9092,master-cent7-3:9092";
	public static final String partitioner = "cn.ac.sict.kafka_producer_demo1.SimplePartitioner";
	public static final String topicStr = "ljc_page_visits";

	private final Producer<String, String> producer;

	public TestProducer() {
		Properties props = new Properties();
		props.put("metadata.broker.list", kafkaStr); // 定义了生产者可以找到的 Borker
		props.put("producer.type", "sync"); // 同步还是异步, 默认 sync 同步, async 异步. 异步可以提高发送吞吐量, 但是也可能导致丢失未发送过去的消息
		props.put("compression.codec", "1"); // 是否压缩, 默认0表示不压缩, 1表示用gzip压缩, 2表示用snappy压缩. 压缩后消息中会有头来指明消息压缩类型, 故在消费者端消息解压是透明的无需指定.
		props.put("serializer.class", "kafka.serializer.StringEncoder"); // 定义传送到 Borker 的消息是使用什么序列化, 这里使用 kafka 自带的一个简单的字符串编码器
		props.put("partitioner.class", partitioner); // 定义了使用哪个类来决定消息的分区, 默认是Hash分区
		props.put("request.required.acks", "1"); // 是否要求消息回执

		ProducerConfig config = new ProducerConfig(props);

		this.producer = new Producer<String, String>(config); // <分区的键的类型, 值的类型>
	}

	private void send(String arg) {
		long events = Long.parseLong(arg);
		Random rnd = new Random();
		try {
			long startTime = System.nanoTime(); // 获取开始时间
			for (long nEvents = 0; nEvents < events; nEvents++) {
				long runtime = new Date().getTime();
				String ip = "192.168.2." + rnd.nextInt(255);
				String msg = runtime + ", " + ip; // 此处模拟一个 website 的访问记录
				// 参数为: topic, 分区的 key, message
				KeyedMessage<String, String> data = new KeyedMessage<String, String>(topicStr, ip, msg);
				producer.send(data);
			}
			long endTime = System.nanoTime(); // 获取结束时间
			System.out.println((endTime - startTime) / 1000000000.0);
		} finally {
			producer.close();
		}

	}

	public static void main(String[] args) {
		TestProducer producer = new TestProducer();
		producer.send(args[0]); // args[0] 为要生成的随机消息数
	}

}
