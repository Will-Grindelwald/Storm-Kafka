package cn.ac.sict.ljg;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
/***
 * 
 *  对字母转化为大写格式。
 * 
 */
public class UpperBolt extends BaseBasicBolt{
						
	private static final long serialVersionUID = 8268013798693604045L;
	public void execute(Tuple tuple, BasicOutputCollector collector) { 
		String src_word = tuple.getString(0);
		String upper_word = src_word.toUpperCase();
		collector.emit(new Values(upper_word));
	}
	public void declareOutputFields(OutputFieldsDeclarer declare) {
		declare.declare( new Fields("upper_word"));
	}
			
}
