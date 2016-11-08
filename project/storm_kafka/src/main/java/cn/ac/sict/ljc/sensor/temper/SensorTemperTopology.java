package cn.ac.sict.ljc.sensor.temper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;

import java.util.Properties;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

/**
 * 
 */
public class SensorTemperTopology {

	public static Logger log = LoggerFactory.getLogger(SensorTemperTopology.class);

	private final TopologyBuilder builder;

	public SensorTemperTopology(Properties configProps) {

		String zkStr = configProps.getProperty("zkStr");
		String zkRoot = configProps.getProperty("zkRoot");
		String kafkaStr = configProps.getProperty("kafkaStr");
		String inputTopic_ljc_sensor_temper = configProps.getProperty("inputTopic_ljc_sensor_temper");
		String outputTopic_ljc_sensor_temper = configProps.getProperty("outputTopic_ljc_sensor_temper");
		String spoutId_ljc_sensor = configProps.getProperty("spoutId_ljc_sensor");

		log.info("inputTopic_ljc_demo = " + inputTopic_ljc_sensor_temper + ", outputTopic_ljc_demo = " + outputTopic_ljc_sensor_temper + ", zkRoot = " + zkRoot + ", spoutId = " + spoutId_ljc_sensor);

		BrokerHosts brokerHosts = new ZkHosts(zkStr, zkRoot);

		// 定义spoutConfig
		SpoutConfig spoutConfig = new SpoutConfig(brokerHosts,
				inputTopic_ljc_sensor_temper,
				zkRoot,
				spoutId_ljc_sensor
		);

		spoutConfig.scheme = new SchemeAsMultiScheme(new MessageScheme()); // 自己实现的 Scheme, 输出 field 为 msg

		builder = new TopologyBuilder();

		// 设置 spout
		String Spout = KafkaSpout.class.getSimpleName();
		builder.setSpout(Spout, new KafkaSpout(spoutConfig), 1); // topic 的分区数(partitions)最好是 KafkaSpout 的并发度的倍数

		// 设置 一级 bolt
		String Bolt1 = AlertBolt.class.getSimpleName();
		builder.setBolt(Bolt1, new AlertBolt(), 8) // 并行度 8
				.shuffleGrouping(Spout); // 上一级是 kafkaSpout, 随机分组

		// 设置 二级 bolt
		String Bolt2 = WordCountBolt.class.getSimpleName();
		builder.setBolt(Bolt2, new WordCountBolt(), 12) // 并行度 12
				.fieldsGrouping(Bolt1, new Fields("word")); // 上一级是 AlertBolt, 按字段分组

		Properties producerProps = new Properties();
		producerProps.put("bootstrap.servers", kafkaStr);
		producerProps.put("acks", "1");
		producerProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		producerProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		KafkaBolt<String, String> kafkaBolt = new KafkaBolt<String, String>()
				.withProducerProperties(producerProps)
				.withTopicSelector(new DefaultTopicSelector(outputTopic_ljc_sensor_temper))
				.withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper<String, String>("", "result")); // 没有 key, 只传 value

		// 设置 三级 bolt: KafakBolt
		String Bolt3 = KafkaBolt.class.getSimpleName();
		builder.setBolt(Bolt3, kafkaBolt, 8)
				.fieldsGrouping(Bolt2, new Fields("result"));
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
