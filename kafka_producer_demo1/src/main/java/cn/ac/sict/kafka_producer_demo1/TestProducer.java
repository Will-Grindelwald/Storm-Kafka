package cn.ac.sict.kafka_producer_demo1;

import java.util.*;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * from: https://cwiki.apache.org/confluence/display/KAFKA/0.8.0+Producer+Example
 */
public class TestProducer {

	public static void main(String[] args) {

		long events = Long.parseLong(args[0]); // args[0] 为要生成的随机消息数

		String kafkaStr = "master-cent7-1:9092,master-cent7-2:9092,master-cent7-3:9092";
		String partitioner = "cn.ac.sict.kafka_producer_demo1.SimplePartitioner";
		String topicStr = "ljc_page_visits";

		Properties props = new Properties();
		props.put("metadata.broker.list", kafkaStr); // 定义了生产者可以找到的 Borker
		props.put("serializer.class", "kafka.serializer.StringEncoder"); // 定义传送到 Borker 的消息是使用什么序列化, 这里使用 kafka 自带的一个简单的字符串编码器
		props.put("partitioner.class", partitioner); // 定义了使用哪个类来决定消息的分区
		props.put("request.required.acks", "1"); // 是否要求消息回执

		ProducerConfig config = new ProducerConfig(props);

		Producer<String, String> producer = new Producer<String, String>(config); // <分区的键的类型,

		Random rnd = new Random();
		try {
			long startTime=System.nanoTime();   //获取开始时间 
			for (long nEvents = 0; nEvents < events; nEvents++) {
				long runtime = new Date().getTime();
				String ip = "192.168.2." + rnd.nextInt(255);
				String msg = runtime + ", " + ip; // 此处模拟一个 website 的访问记录
				// 参数为: topic, 分区的 key, message
				KeyedMessage<String, String> data = new KeyedMessage<String, String>(topicStr, ip, msg);
				producer.send(data);
			}
			long endTime=System.nanoTime();     //获取结束时间 
			System.out.println((endTime - startTime)/1000000000.0);
		} finally {
			producer.close();
		}
	}

}
