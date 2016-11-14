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
import org.apache.storm.kafka.bolt.KafkaBolt;
import org.apache.storm.kafka.bolt.mapper.FieldNameBasedTupleToKafkaMapper;
import org.apache.storm.kafka.bolt.selector.KafkaTopicSelector;
import org.apache.storm.kafka.bolt.selector.DefaultTopicSelector;
import org.apache.storm.spout.SchemeAsMultiScheme;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.TopologyBuilder;
	/***
	 * 
	 *  从kafka特定的topic读数据，经过bolt处理，发送到kafka特定的主题。
	 *
	 */
public class TopicMessageTopology {
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
	topologyBuilder.setSpout("dataSourceSpout", new KafkaSpout(spoutConfig));
	topologyBuilder.setBolt("dataDealBolt", new DataDealBolt(),4).shuffleGrouping("dataSourceSpout" );

    Properties props1 = new Properties();    
    props1.put("bootstrap.servers",  kafkaBrokerString);
    props1.put("acks", "1");
    props1.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props1.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
 
    KafkaBolt<String, String> kafkaBolt = new KafkaBolt().withProducerProperties(props1).withTopicSelector((KafkaTopicSelector) new DefaultTopicSelector(dataDestination)).withTupleToKafkaMapper(new FieldNameBasedTupleToKafkaMapper());
    		
	topologyBuilder.setBolt("dataDestinationBolt",kafkaBolt,4).shuffleGrouping("dataDealBolt" );
	try {
		StormSubmitter.submitTopology("kafka-Storm-Topo", config, topologyBuilder .createTopology());
	} catch (AlreadyAliveException | InvalidTopologyException | AuthorizationException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	}
}
