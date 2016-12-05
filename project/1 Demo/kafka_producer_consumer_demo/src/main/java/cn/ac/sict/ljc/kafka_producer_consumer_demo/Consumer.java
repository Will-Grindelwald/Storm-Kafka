package cn.ac.sict.ljc.kafka_producer_consumer_demo;

import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class Consumer extends Thread {

	private final KafkaConsumer<String, String> consumer;
	private final String topic;
	private static final String GROUPID = "test-consumer-group";

	public Consumer(String kafkaStr, String topic) {
		Properties props = new Properties();
		props.put("bootstrap.servers", kafkaStr);
		props.put("group.id", GROUPID);
		props.put("enable.auto.commit", "true");
		props.put("auto.commit.interval.ms", "1000");
		props.put("session.timeout.ms", "30000");
		props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
		this.consumer = new KafkaConsumer<String, String>(props);
		this.topic = topic;
	}

	@Override
	public void run() {
		this.consumer.subscribe(Arrays.asList(topic));
		try {
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(100);
				for (ConsumerRecord<String, String> record : records) {
					System.out.println("receive: key = " + record.key() + ", value = " + record.value());
				}
			}
		} finally {
			consumer.close();
		}
	}

}
