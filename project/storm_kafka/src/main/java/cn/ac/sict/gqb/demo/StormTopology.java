package cn.ac.sict.gqb.demo;

import java.util.ArrayList;
import java.util.List;
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
	public static void main(String[] args) {

		TopologyBuilder builder = new TopologyBuilder();

		BrokerHosts brokerHosts = new ZkHosts("master-cent7-1:2181,master-cent7-2:2181,master-cent7-2:2181");

		List<String> zkServer=new ArrayList<>();
		zkServer.add("192.168.125.171");
		zkServer.add("192.168.125.172");
		zkServer.add("192.168.125.173");
		
		
		// 在topic 中读取数据
		SpoutConfig kafkaConfig_site1 = new SpoutConfig(brokerHosts, "gqb_site1", "/kafka_site1", "spout_site1");
		SpoutConfig kafkaConfig_site2 = new SpoutConfig(brokerHosts, "gqb_site2", "/kafka_site2", "spout_site2");
		kafkaConfig_site1.scheme = new SchemeAsMultiScheme(new MessageScheme());
		kafkaConfig_site2.scheme = new SchemeAsMultiScheme(new MessageScheme());
        kafkaConfig_site1.zkPort=2181;
        kafkaConfig_site2.zkPort=2181;
        kafkaConfig_site1.zkServers=zkServer;
        kafkaConfig_site2.zkServers=zkServer;
		
		
		KafkaSpout spout_site1 = new KafkaSpout(kafkaConfig_site1);
		KafkaSpout spout_site2 = new KafkaSpout(kafkaConfig_site2);
		builder.setSpout("spout_site1", spout_site1);
		builder.setSpout("spout_site2", spout_site2);

		// 这里可以只是用一个bolt，根据streamid来判断spout，目前是使用的两个bolt分别接收kafka bolt的数据
		Site1Bolt site1Bolt = new Site1Bolt();
		Site2Bolt site2Bolt = new Site2Bolt();
		builder.setBolt("bolt_site1", site1Bolt).shuffleGrouping("spout_site1");
		builder.setBolt("bolt_site2", site2Bolt).shuffleGrouping("spout_site2");

		// 温湿度判断bolt
		builder.setBolt("temperBolt", new TemperBolt(), 5).shuffleGrouping("bolt_site1").shuffleGrouping("bolt_site2");
		builder.setBolt("humiBolt", new HumiBolt(),5).shuffleGrouping("bolt_site1").shuffleGrouping("bolt_site2");
		
		
		// 给KafkaBolt配置topic及前置tuple消息到kafka的mapping关系
		Properties props = new Properties();
		props.put("bootstrap.servers", "localhost:9092");
		props.put("acks", "1");
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

		KafkaBolt<String, String> bolt = new KafkaBolt<String, String>();

		bolt.withTopicSelector(new DefaultTopicSelector("gqb_stormout")).withProducerProperties(props)
				.withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper<String, String>("", "warn"));

		builder.setBolt("forwardToKafka", bolt, 1).shuffleGrouping("temperBolt").shuffleGrouping("humiBolt");
		Config conf = new Config();
		if (args.length > 0) {
			// cluster submit.
			try {
				StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
			} catch (AlreadyAliveException e) {
				e.printStackTrace();
			} catch (InvalidTopologyException e) {
				e.printStackTrace();
			} catch (AuthorizationException e) {
				e.printStackTrace();
			}
		} else {
			new LocalCluster().submitTopology("kafkaboltTestaaa", conf, builder.createTopology());
		}
	}
}
