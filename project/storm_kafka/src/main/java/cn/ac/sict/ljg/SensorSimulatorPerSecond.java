package cn.ac.sict.ljg;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class SensorSimulatorPerSecond {
	
	
	private  final Producer<String, String> producer;
	private  String bootstrapServers;
	public SensorSimulatorPerSecond( String servers) {
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
		Random random =new Random();
		
		while(true){
		long startTime = System.currentTimeMillis();
		for(int i=0;i<messageCount;i++){
			   SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd	HH:mm:ss.SSS");
		       String TimeStamp = simpleDateFormat.format(new Date());
		       double d= random.nextDouble()*2; 
		   	   BigDecimal bg = new BigDecimal(d).setScale(2, RoundingMode.UP);
		       double concentration= bg.doubleValue();
		       String data = TimeStamp+" "+concentration; //第三列数据为浓度值，表示所占的百分百。
		       System.out.println(data);
		       producer.send(new ProducerRecord<String, String>(topic,  data));
		}
		long endTime = System.currentTimeMillis();
		System.out.println(messageCount+"条"+"记录:用时"+(endTime-startTime)*0.001+"秒");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
	}
	 public static void main(String[] args) {
		 String bootstrapServers = "192.168.125.171:9092,192.168.125.172:9092,192.168.125.173:9092";
		 KafkaProducerTest producerTest = new KafkaProducerTest(bootstrapServers);
		 String topic = args[0];
		 int messageCount = Integer.parseInt(args[1]);
		 producerTest.sendMessage(topic, messageCount);
	}
}
