package cn.ac.sict.ljc.kafka_producer_sensor;


/**
 * 模拟传感器: 温度: n 个, 压力: n 个
 */
public class Sensor {
	
	public static final String kafkaStr = "master-cent7-1:9092,master-cent7-2:9092,master-cent7-3:9092";

	public static void main(String[] args) {
		for(int i = 0; i < Integer.valueOf(args[0]); i++)
			new SensorProducer(kafkaStr, i).start(); // 第 i 组: 一个温度传感器, 一个压力传感器
	}

}
