package cn.ac.sict.ljg;

 
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DataDealBolt    extends BaseBasicBolt {
	/**
	 * 字符串处理
	 */
	private static final long serialVersionUID = -58701076587898373L;
	private static final Logger logger = LoggerFactory.getLogger(MessageScheme.class);
	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		 String word = (String) input.getValue(0);
		 String out = "message got is"+word+"!";
		 logger.info("out={}",out);
		 collector.emit(new Values(out));
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declare) {
		 
		declare.declare(new Fields("message"));
	}

 
}
