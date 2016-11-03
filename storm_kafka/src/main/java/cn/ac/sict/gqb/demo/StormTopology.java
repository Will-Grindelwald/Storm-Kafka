package cn.ac.sict.gqb.demo;

import java.util.Properties;
import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;

public class StormTopology {
	public void kafkaConsumer(String name) {
		TopologyBuilder builder = new TopologyBuilder();
		BrokerHosts brokerHosts = new ZkHosts("master-cent7-1:2181");
		SpoutConfig kafkaConfig = new SpoutConfig(brokerHosts, "gqb_temper", "/kafka", "spout");

		kafkaConfig.zkPort = 2181;
		kafkaConfig.scheme = new SchemeAsMultiScheme(new MessageScheme());

		KafkaSpout spout = new KafkaSpout(kafkaConfig);
		builder.setSpout("spout", spout);

		//给KafkaBolt配置topic及前置tuple消息到kafka的mapping关系
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("acks", "1");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		KafkaBolt bolt = new KafkaBolt();
		
		bolt.withTopicSelector(new DefaultTopicSelector("gqb_stormout")).withProducerProperties(props)
				.withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper());
		builder.setBolt("forwardToKafka", bolt, 1).shuffleGrouping("spout");

		Config conf = new Config();

		if (name != null) {
			// cluster submit.
			try {
				StormSubmitter.submitTopology(name, conf, builder.createTopology());
			} catch (AlreadyAliveException e) {
				e.printStackTrace();
			} catch (InvalidTopologyException e) {
				e.printStackTrace();
			} catch (AuthorizationException e) {
				e.printStackTrace();
			}
		} else {
			new LocalCluster().submitTopology("gqb_localStorm", conf, builder.createTopology());
		}

	}
}
