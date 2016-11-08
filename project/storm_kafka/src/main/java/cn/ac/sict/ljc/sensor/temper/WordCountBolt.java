package cn.ac.sict.ljc.sensor.temper;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

/**
 * 单词计数
 */
public class WordCountBolt extends BaseBasicBolt {

	/* long: serialVersionUID */
	private static final long serialVersionUID = -799022488579052012L;

	public Logger log = LoggerFactory.getLogger(WordCountBolt.class);

	private static Map<String, Integer> counts = new HashMap<String, Integer>();

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {

		// 根据 field 获得上一个 bolt 传递过来的数据
		String word = tuple.getStringByField("word");

		Integer count = counts.get(word);
		if (count == null)
			count = 0;
		count++;
		counts.put(word, count);

		String result = "word = " + word + ", count = " + count;
		collector.emit(new Values(result));
		log.info(result);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("result"));
	}

}
