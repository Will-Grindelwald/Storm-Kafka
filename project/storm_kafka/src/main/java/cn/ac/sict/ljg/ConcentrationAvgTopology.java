package cn.ac.sict.ljg;

import java.util.Properties;
import java.util.UUID;

import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.kafka.BrokerHosts;
import org.apache.storm.kafka.KafkaSpout;
import org.apache.storm.kafka.SpoutConfig;
import org.apache.storm.kafka.StringScheme;
import org.apache.storm.kafka.ZkHosts;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

public class ConcentrationAvgTopology {
		public static void main(String[] args) {
			String zkConnString = "192.168.125.171:2181,192.168.125.172:2181,192.168.125.173:2181";
			String kafkaBrokerString = "192.168.125.171:9092,192.168.125.172:9092,192.168.125.173:9092";
			BrokerHosts hosts = new ZkHosts(zkConnString);
			String  dataSourceTopic = "ljg-topic-from";
		    String dataDestination ="ljg-topic-to";
			SpoutConfig   spoutConfig= new SpoutConfig( hosts,dataSourceTopic,"/"+dataSourceTopic ,UUID.randomUUID().toString());
			Config config =new Config();
			Properties props = new Properties();
			props.put("request.required.acks", "1");
			props.put("serializer.class", "kafka.serializer.StringEncoder");
			props.put("metadata.broker.list",kafkaBrokerString );
			config.put("kafka.broker.properties",props);
			config.put("topic",dataDestination);
			spoutConfig.bufferSizeBytes = 1024 * 1024 * 4;
		    spoutConfig.fetchSizeBytes = 1024 * 1024 * 4;
			spoutConfig.scheme = new SchemeAsMultiScheme( new StringScheme());
			TopologyBuilder topologyBuilder = new TopologyBuilder();
			//topologyBuilder.setSpout("dataSourceSpout", new KafkaSpout(spoutConfig));
			//topologyBuilder.setSpout("dataSourceSpout", new KafkaSpout(spoutConfig));
			topologyBuilder.setSpout("dataSourceSpout", new ConcentrationSpout());
			topologyBuilder.setBolt("dataSplitBolt",new DataSplitBolt(),4).shuffleGrouping("dataSourceSpout" );
			topologyBuilder.setBolt("dataAvgBolt",new  DataAvgBolt(),4).fieldsGrouping("dataSplitBolt",new Fields("timeStamp") );
			topologyBuilder.setBolt("dataReportBolt", new DataReportBolt(),4 ).globalGrouping("dataAvgBolt");
			try {
				StormSubmitter.submitTopology("kafka-Storm-Topo", config, topologyBuilder .createTopology());
			} catch (AlreadyAliveException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidTopologyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AuthorizationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
