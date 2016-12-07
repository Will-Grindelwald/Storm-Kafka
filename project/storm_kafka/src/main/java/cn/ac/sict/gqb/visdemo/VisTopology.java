package cn.ac.sict.gqb.visdemo;

import java.util.ArrayList;
import java.util.List;

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
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import cn.ac.sict.gqb.demo.MessageScheme;

public class VisTopology {

	private static final String SPOUTSITE = "spout";

	public static void main(String args[]) {

		TopologyBuilder builder = new TopologyBuilder();
		BrokerHosts brokerHosts = new ZkHosts("master-cent7-1:2181,master-cent7-2:2181,master-cent7-2:2181");

		List<String> zkServer = new ArrayList<>();
		zkServer.add("192.168.125.171");
		zkServer.add("192.168.125.172");
		zkServer.add("192.168.125.173");

		// 在topic 中读取数据
		SpoutConfig kafkaConfigSite1 = new SpoutConfig(brokerHosts, "gqb_visdata", "/data", SPOUTSITE);
		kafkaConfigSite1.scheme = new SchemeAsMultiScheme(new MessageScheme());
		kafkaConfigSite1.zkPort = 2181;
		kafkaConfigSite1.zkServers = zkServer;

		KafkaSpout spout = new KafkaSpout(kafkaConfigSite1);

		builder.setSpout(SPOUTSITE, spout, 5);

		PackDataBolt packDataBolt = new PackDataBolt();

		RedisBolt redisBolt = new RedisBolt();

		builder.setBolt("packDataBolt", packDataBolt, 6).shuffleGrouping(SPOUTSITE);
		builder.setBolt("redisBolt", redisBolt, 6).shuffleGrouping("packDataBolt");

		Config conf = new Config();
		conf.put(Config.TOPOLOGY_ACKER_EXECUTORS, 10);
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
			new LocalCluster().submitTopology("kafkaboltTestvis", conf, builder.createTopology());
		}

	}
}
