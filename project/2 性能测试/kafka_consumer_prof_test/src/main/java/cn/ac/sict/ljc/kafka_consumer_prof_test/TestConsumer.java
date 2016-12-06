package cn.ac.sict.ljc.kafka_consumer_prof_test;

import java.util.*;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

/**
 * 
 */
public class TestConsumer {
	public static void main(String[] args) {
		
		String kafkaStr = "master-cent7-1:9092,master-cent7-2:9092,master-cent7-3:9092";
		String topicStr = args[0];
		String groupId = "ljc_test";
		
		Properties props = new Properties();
		props.put("bootstrap.servers", kafkaStr); // kafka brokers 字符串
		props.put("group.id", groupId);           // group.id 标识属于哪个消费者组
		props.put("enable.auto.commit", "true");  // 设置 enable.auto.commit 即按 auto.commit.interval.ms 频率自动提交偏移
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
		// 指定序列化处理类，默认为 kafka.serializer.DefaultEncoder, 即 byte[]
		props.put("key.deserializer", StringDeserializer.class.getName()); // key 的序列化处理类
		props.put("value.deserializer", StringDeserializer.class.getName()); // value 的序列化处理类

		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
		consumer.subscribe(Arrays.asList(topicStr));

		try {
			while (true) {
				// poll 轮询 message, 参数为 timeout, 单位 ms, 超时则返回空
				// consumer 必须先使用 subscribe/assign API
				ConsumerRecords<String, String> records = consumer.poll(1);
				for (ConsumerRecord<String, String> record : records)
					System.out.println(record.value());
			}
		} finally {
			// 用 Ctrl-C 结束程序
			consumer.close();
		}
	}
}
