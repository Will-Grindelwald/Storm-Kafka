package cn.ac.sict.ljc.prof_test;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 消息计数
 */
public class CountBolt extends BaseBasicBolt {

	/* long: serialVersionUID * description： */
	private static final long serialVersionUID = -3494794413248427509L;

	public Logger log = LoggerFactory.getLogger(CountBolt.class);
	private static int count = 0;
	private static long startTime; // 获取开始时间

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		// 根据 field 获得从 spout 传来的值,这里的 str 是 kafkaspout 的 spoutConfig.scheme 中定义好的 field
		// String line = tuple.getStringByField("str");
		String word = tuple.getStringByField("msg");
		word.length();
		//log.info(word.length() + ":" + count);
		if (count == 0) startTime = System.nanoTime();
		count++;
		if ((System.nanoTime() - startTime) / 1000000000 >= 1) {
			collector.emit(new Values(new String("每秒接收: " + count + "条数据")));
			count = 0;
			startTime = System.nanoTime();
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// 声明本次 emit 出去的 field
		declarer.declare(new Fields("res"));
	}

}
