package cn.ac.sict.ljc.prof_test;

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

import org.apache.kafka.common.serialization.StringSerializer;
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
public class ProfTestTopology {

	public static Logger log = LoggerFactory.getLogger(ProfTestTopology.class);

	private final TopologyBuilder builder;

	public ProfTestTopology(Properties configProps) {

		String zkStr = configProps.getProperty("zkStr");
		String zkRoot = configProps.getProperty("zkRoot");
		String kafkaStr = configProps.getProperty("kafkaStr");
		String inputTopic_ljc_prof_test = configProps.getProperty("inputTopic_ljc_prof_test");
		String outputTopic_ljc_prof_test = configProps.getProperty("outputTopic_ljc_prof_test");
		String spoutId_ljc_prof_test = configProps.getProperty("spoutId_ljc_prof_test");

		log.info("\n inputTopic_ljc_demo = " + inputTopic_ljc_prof_test + "\n outputTopic_ljc_demo = " + outputTopic_ljc_prof_test + "\n spoutId = " + spoutId_ljc_prof_test);

		BrokerHosts brokerHosts = new ZkHosts(zkStr, zkRoot);

		// 定义spoutConfig
		SpoutConfig spoutConfig = new SpoutConfig(brokerHosts, // 第一个参数 hosts 是上面定义的 brokerHosts
				inputTopic_ljc_prof_test,                      // 第二个参数 topic 是该 KafkaSpout 订阅的 topic 名称
				zkRoot,                                        // 第三个参数 zkRoot 是存储消费的 offset(存储在 ZK 中了), 当该 topology 故障重启后会将故障期间未消费的 message 继续消费而不会丢失(可配置)
				spoutId_ljc_prof_test                          // 第四个参数 id 是当前 spout 的唯一标识
		);

		spoutConfig.scheme = new SchemeAsMultiScheme(new MessageScheme());

		builder = new TopologyBuilder();

		// 设置 spout: KafkaSpout
		String Spout = KafkaSpout.class.getSimpleName();
		builder.setSpout(Spout, new KafkaSpout(spoutConfig), 4); // topic 的分区数(partitions)最好是 KafkaSpout 的并发度的倍数

		// 设置 一级 bolt
		String Bolt1 = CountBolt.class.getSimpleName();
		builder.setBolt(Bolt1, new CountBolt(), 4) // 并行度 8
				.shuffleGrouping(Spout); // 上一级是 kafkaSpout, 随机分组

		Properties producerProps = new Properties();
		producerProps.put("bootstrap.servers", kafkaStr);
		producerProps.put("acks", "1");
		producerProps.put("key.serializer", StringSerializer.class.getName());
		producerProps.put("value.serializer", StringSerializer.class.getName());

		KafkaBolt<String, String> kafkaBolt = new KafkaBolt<String, String>()
				.withProducerProperties(producerProps)
				.withTopicSelector(new DefaultTopicSelector(outputTopic_ljc_prof_test))
				.withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper<String, String>("", "res")); // 没有 key, 只传 value

		// 设置 二级 bolt: KafakBolt
		String Bolt2 = KafkaBolt.class.getSimpleName();
		builder.setBolt(Bolt2, kafkaBolt, 1)
				.fieldsGrouping(Bolt1, new Fields("res"));
	}

	public void submit(String topologyName) throws AlreadyAliveException, InvalidTopologyException, AuthorizationException {
		Config config = new Config();
		if (topologyName == null || topologyName.isEmpty()) { // 本地运行, 可以看到 log 输出, 用于调试
			topologyName = this.getClass().getSimpleName();
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology(topologyName, config, builder.createTopology());
			try {
				Thread.sleep(60000); // 20分钟后自动停止 Topology
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
