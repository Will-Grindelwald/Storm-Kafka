package cn.ac.sict.ljg;

import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
/***
 * 
 *  kafka生产者将产生的随机数发送到特定的topic，测试完成发送的时间。
 *
 */
 

public class KafkaProducerTest {
	private  final Producer<String, String> producer;
	private  String bootstrapServers;
	public KafkaProducerTest( String servers) {
		bootstrapServers=servers;
		Properties props =new Properties();
		 props.put("bootstrap.servers", bootstrapServers);
		 props.put("batch.size", 16384);
		 props.put("linger.ms",1);
		 props.put("buffer.memory", 33554432);
		 props.put("acks", "all");
		 props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		 props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		 producer = new KafkaProducer<String, String>(props); 
	 
	}
	public void sendMessage(String topic,  int  messageCount    ){
		Random r =new Random();
		long startTime = System.currentTimeMillis();
		for(int i=0;i<messageCount;i++){
		    String randomNumber = Integer.toString(r.nextInt(50));
		    producer.send(new ProducerRecord<String, String>(topic, randomNumber));
		}
		long endTime = System.currentTimeMillis();
		System.out.println(messageCount+"条"+"记录:用时"+(endTime-startTime)*0.001+"秒");
	}
   public static void main(String[] args) {
	   String bootstrapServers = "192.168.125.171:9092,192.168.125.172:9092,192.168.125.173:9092";
	   KafkaProducerTest producerTest = new KafkaProducerTest(bootstrapServers);
	   String topic = args[0];
	   int messageCount = Integer.parseInt(args[1]);
	   producerTest.sendMessage(topic, messageCount);
}
	
}
