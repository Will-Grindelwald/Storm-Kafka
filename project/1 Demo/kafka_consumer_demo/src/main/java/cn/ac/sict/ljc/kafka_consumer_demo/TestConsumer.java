package cn.ac.sict.ljc.kafka_consumer_demo;

import java.util.*;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

/**
 * 这个 demo 使用的是新的 Java 客户端 API(在 org.apache.kafka.clients package), 注意 maven 依赖
 * 请同时运行 kafka_producer_demo2 作为消费者, 观察终端输出
 * 参考: http://kafka.apache.org/090/javadoc/index.html?org/apache/kafka/clients/consumer/KafkaConsumer.html
 * 不是线程安全的, 多线程见上面的参考
 */
public class TestConsumer {
	public static void main(String[] args) {
		
		String kafkaStr = "master-cent7-1:9092,master-cent7-2:9092,master-cent7-3:9092";
		String topicStr = "ljc_page_visits_2";
		String groupId = "ljc_test";
		
		Properties props = new Properties();
		props.put("bootstrap.servers", kafkaStr); // kafka brokers 字符串
		props.put("group.id", groupId);           // group.id 标识属于哪个消费者组
		props.put("enable.auto.commit", "true");  // 设置 enable.auto.commit 即按 auto.commit.interval.ms 频率自动提交偏移
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
		// 指定序列化处理类，默认为 kafka.serializer.DefaultEncoder, 即 byte[]
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer"); // key 的序列化处理类
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer"); // value 的序列化处理类

		KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
		consumer.subscribe(Arrays.asList(topicStr));

		try {
			while (true) {
				// poll 轮询 message, 参数为 timeout, 单位 ms, 超时则返回空
				// consumer 必须先使用 subscribe/assign API
				ConsumerRecords<String, String> records = consumer.poll(100);
				if(records.isEmpty()) System.out.println("no message");
				for (ConsumerRecord<String, String> record : records)
					System.out.println("offset = " + record.offset() + ", key = "  + record.key() +", value = " + record.value());
			}
		} finally {
			// 用 Ctrl-C 结束程序
			consumer.close();
		}
	}
}
