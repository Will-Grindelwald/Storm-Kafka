package cn.ac.sict.ljc.kafka_producer_consumer_demo;

public class KafkaProducerConsumerDemo {

	public static final String KAFKASTR = "master-cent7-1:9092,master-cent7-2:9092,master-cent7-3:9092";

	public static void main(String[] args) {
		new Producer(KAFKASTR, args[0]).start(); // args[0] 为要发送的 topic
		new Consumer(KAFKASTR, args[0]).start(); // args[0] 为要接收的 topic
	}

}