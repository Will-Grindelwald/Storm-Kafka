package cn.ac.sict.gqb.demo;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Site1Bolt extends BaseBasicBolt {

	/**
	 * 添加站点标记
	 */
	private static final long serialVersionUID = 4904118032317857812L;
	public Logger log = LoggerFactory.getLogger(Site1Bolt.class);

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {

		String str = input.getStringByField("message");
		String value[] = str.split(",");
		for (String s : value) {
			log.info(s);
			collector.emit(new Values("site1", s));
		}

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("site", "value"));
	}

}
