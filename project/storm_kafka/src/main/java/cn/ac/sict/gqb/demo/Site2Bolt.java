package cn.ac.sict.gqb.demo;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class Site2Bolt extends BaseBasicBolt {


	/**
	 * 添加站点标记
	 */
	private static final long serialVersionUID = 6807683757595311806L;

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {

		String str = input.getStringByField("message");
		String value[] = str.split(",");
		for (String s : value) {
			collector.emit(new Values("site2", s));
		}

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("site", "value"));
	}

}
