package cn.ac.sict.ljc.sensorSimu_terminal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;

import java.util.Properties;

import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;

/**
 * 
 */
public class SensorTopology {

	public static Logger log = LoggerFactory.getLogger(SensorTopology.class);

	private final TopologyBuilder builder;

	public SensorTopology(Properties configProps) {

		String zkStr = configProps.getProperty("zkStr");
		String zkRoot = configProps.getProperty("zkRoot");
		String kafkaStr = configProps.getProperty("kafkaStr");
		String inputTopic_ljc_sensor_temper = configProps.getProperty("inputTopic_ljc_sensor_temper");
		String outputTopic_ljc_sensor_temper = configProps.getProperty("outputTopic_ljc_sensor_temper");
		String spoutId_ljc_sensor_terminal_temper = configProps.getProperty("spoutId_ljc_sensor_terminal_temper");
		
		String inputTopic_ljc_sensor_pressure = configProps.getProperty("inputTopic_ljc_sensor_pressure");
		String outputTopic_ljc_sensor_pressure = configProps.getProperty("outputTopic_ljc_sensor_pressure");
		String spoutId_ljc_sensor_terminal_pressure = configProps.getProperty("spoutId_ljc_sensor_terminal_pressure");

		log.info("\n inputTopic_ljc_sensor_temper = " + inputTopic_ljc_sensor_temper + "\n outputTopic_ljc_sensor_temper = " + outputTopic_ljc_sensor_temper + "\n inputTopic_ljc_sensor_pressure = " + inputTopic_ljc_sensor_pressure + "\n outputTopic_ljc_sensor_pressure = " + outputTopic_ljc_sensor_pressure + "\n spoutId1 = " + spoutId_ljc_sensor_terminal_temper + "\n spoutId2 = " + spoutId_ljc_sensor_terminal_pressure + "\n zkRoot = " + zkRoot);

		// 定义 spoutConfig1
		SpoutConfig spoutConfig1 = new SpoutConfig(new ZkHosts(zkStr, zkRoot),
				inputTopic_ljc_sensor_temper,
				zkRoot,
				spoutId_ljc_sensor_terminal_temper
		);
		
		// 定义 spoutConfig2
		SpoutConfig spoutConfig2 = new SpoutConfig(new ZkHosts(zkStr, zkRoot),
				inputTopic_ljc_sensor_pressure,
				zkRoot,
				spoutId_ljc_sensor_terminal_pressure
		);

		spoutConfig1.scheme = new SchemeAsMultiScheme(new MessageScheme()); // 自己实现的 Scheme, 输出 field 为 msg
		spoutConfig2.scheme = new SchemeAsMultiScheme(new MessageScheme()); // 自己实现的 Scheme, 输出 field 为 msg

		builder = new TopologyBuilder();

		// 设置 spout: KafkaSpout
		String Spout = KafkaSpout.class.getSimpleName();
		builder.setSpout(Spout + "_temper", new KafkaSpout(spoutConfig1), 1);
		builder.setSpout(Spout + "_pressure", new KafkaSpout(spoutConfig2), 1);

		// 设置 一级 bolt
		String Bolt1 = AlertBolt.class.getSimpleName();
		builder.setBolt(Bolt1, new AlertBolt(), 4)
				.shuffleGrouping(Spout + "_temper")
				.shuffleGrouping(Spout + "_pressure");

		Properties producerProps = new Properties();
		producerProps.put("bootstrap.servers", kafkaStr);
		producerProps.put("acks", "all");
		producerProps.put("key.serializer", StringSerializer.class.getName());
		producerProps.put("value.serializer", StringSerializer.class.getName());

		// 定义 kafkaBolt1
		KafkaBolt<String, String> kafkaBolt1 = new KafkaBolt<String, String>()
				.withProducerProperties(producerProps)
				.withTopicSelector(new DefaultTopicSelector(outputTopic_ljc_sensor_temper))
				.withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper<String, String>("", AlertBolt.fieldsTemper)); // 没有 key, 只传 value

		// 定义 kafkaBolt2
		KafkaBolt<String, String> kafkaBolt2 = new KafkaBolt<String, String>()
				.withProducerProperties(producerProps)
				.withTopicSelector(new DefaultTopicSelector(outputTopic_ljc_sensor_pressure))
				.withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper<String, String>("", AlertBolt.fieldsPressure)); // 没有 key, 只传 value

		// 设置 二级 bolt: KafakBolt
		String Bolt2 = KafkaBolt.class.getSimpleName();
		builder.setBolt(Bolt2 + "_temper", kafkaBolt1, 4)
				.shuffleGrouping(Bolt1, AlertBolt.sensorType[0][0]);
		builder.setBolt(Bolt2 + "_pressure", kafkaBolt2, 4)
				.shuffleGrouping(Bolt1, AlertBolt.sensorType[0][1]);
	}

	public void submit(String topologyName) throws AlreadyAliveException, InvalidTopologyException, AuthorizationException {
		Config config = new Config();
		if (topologyName == null || topologyName.isEmpty()) { // 本地运行, 可以看到 log 输出, 用于调试
			topologyName = this.getClass().getSimpleName();
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(topologyName, config, builder.createTopology());
			try {
				Thread.sleep(1200000); // 20分钟后自动停止 Topology
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				cluster.shutdown();
			}
		} else { // 集群运行
			StormSubmitter.submitTopology(topologyName, config, builder.createTopology());
		}
	}

}
