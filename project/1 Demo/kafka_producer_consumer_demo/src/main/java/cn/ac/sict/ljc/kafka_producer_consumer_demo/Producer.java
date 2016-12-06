package cn.ac.sict.ljc.kafka_producer_consumer_demo;

import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class Producer extends Thread {

	private final KafkaProducer<String, String> producer;
	private final String topic;

	public Producer(String kafkaStr, String topic) {
		Properties props = new Properties();
		props.put("bootstrap.servers", kafkaStr);
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", StringSerializer.class.getName());
		props.put("value.serializer", StringSerializer.class.getName());
		this.producer = new KafkaProducer<String, String>(props);
		this.topic = topic;
	}

	@Override
	public void run() {
		int messageNo = 1;
		try {
			while (true) {
				String messageStr = "Message_" + messageNo;
				System.out.println("Send:" + messageStr);
				producer.send(new ProducerRecord<String, String>(topic, "Message", messageStr));
				messageNo++;
				sleep(200);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			producer.close();
		}
	}

}
