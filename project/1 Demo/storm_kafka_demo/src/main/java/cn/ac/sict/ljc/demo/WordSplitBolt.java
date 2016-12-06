package cn.ac.sict.ljc.demo;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

/**
 * 单词分割
 */
public class WordSplitBolt extends BaseBasicBolt {

	/* long: serialVersionUID * description： */
	private static final long serialVersionUID = -3493794413248427509L;

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		// 根据 field 获得从 spout 传来的值,这里的 str 是 kafkaspout 的 spoutConfig.scheme 中定义好的 field
		// String line = tuple.getStringByField("str");
		String line = tuple.getStringByField("msg"); // msg 是 MessageScheme 中定义的

		// 对单词进行分割
		for (String word : line.split("\\s+")) {
			// 传递给下一个组件，即 WordCountBolt
			collector.emit(new Values(word));
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// 声明本次 emit 出去的 field
		declarer.declare(new Fields("word"));
	}

}
