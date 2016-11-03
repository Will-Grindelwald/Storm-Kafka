package cn.ac.sict.gqb.demo;

import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class ProducerData {
	private static Random random = new Random(93285);
	private Producer<String, String> producer;
	
	
	public void send(){
		 Properties props = new Properties();
		 props.put("bootstrap.servers", "localhost:9092");
		 props.put("acks", "all");
		 props.put("retries", 0);
		 props.put("batch.size", 16384);
		 props.put("linger.ms", 1);
		 props.put("buffer.memory", 33554432);
		 props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		 props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		 //long startTime=System.nanoTime();   //获取开始时间  
		 producer = new KafkaProducer<>(props);
		 int min=-30;
	    int max=70;
		 while(true){
			 int d=min + random.nextInt(max - min);
			 System.out.println(d);
			 producer.send(new ProducerRecord<String, String>("gqb_temper",  Integer.toString(d)));
			 
		 }
		
	}
	
	
}