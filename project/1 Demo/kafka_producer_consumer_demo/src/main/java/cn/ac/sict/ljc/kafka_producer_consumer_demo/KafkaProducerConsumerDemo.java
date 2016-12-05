package cn.ac.sict.ljc.kafka_producer_consumer_demo;

public class KafkaProducerConsumerDemo {

	public static final String KAFKASTR = "master-cent7-1:9092,master-cent7-2:9092,master-cent7-3:9092";
	public static final String TOPICSTR = "ljc_page_visits_3";

	public static void main(String[] args) {
		new Producer(KAFKASTR, TOPICSTR).start();
		new Consumer(KAFKASTR, TOPICSTR).start();
	}

}