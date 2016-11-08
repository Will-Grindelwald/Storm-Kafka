package cn.ac.sict.ljg;

import java.util.Map;
import java.util.Random;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

 
public class RandomSpout extends BaseRichSpout {
	private static final long serialVersionUID = 1L;
	SpoutOutputCollector collector =null;
    String[] goods = {"iphone","xiaomi","huawei","sangsung"};
	public void nextTuple() { 
		Random random = new Random();
		String good = goods[random.nextInt(goods.length)];
		collector.emit(new Values(good));
		Utils.sleep(2000);
	}

	public void open(@SuppressWarnings("rawtypes") Map arg0, TopologyContext arg1, SpoutOutputCollector collector) {
		 this.collector = collector;	
	}

	public void declareOutputFields(OutputFieldsDeclarer declare) {
		 declare.declare(new Fields("src_words"));	
	}
}
