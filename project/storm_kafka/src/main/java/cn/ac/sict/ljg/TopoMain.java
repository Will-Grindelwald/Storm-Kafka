package cn.ac.sict.ljg;

import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.topology.TopologyBuilder;
/***
 * 
 *  定义拓扑，并提交到集群。 
 *
 */
public class TopoMain {
	public static void main(String[] args) {
		TopologyBuilder topologyBuilder = new TopologyBuilder();
		topologyBuilder.setSpout("randomspout", new RandomSpout());
		topologyBuilder.setBolt("upperbolt", new UpperBolt(),4).shuffleGrouping("randomspout");
		topologyBuilder.setBolt("suffixbolt", new SuffixBolt(),4).shuffleGrouping("upperbolt");
		StormTopology topo = topologyBuilder.createTopology();
		Config conf =new Config();
		conf.setDebug(true);
		conf.setNumAckers(0);
		try {
			StormSubmitter.submitTopology("demotopo", conf, topo);
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
