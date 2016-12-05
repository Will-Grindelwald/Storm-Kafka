package cn.ac.sict.gqb.visdemo;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.json.simple.JSONObject;

public class PackDataBolt extends BaseBasicBolt{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6442231544634413576L;

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		String str = input.getStringByField("message");
		String[] value= str.split(":");
		JSONObject json=new JSONObject();
		json.put("time", Long.valueOf(value[0]));
		json.put("number1", Double.valueOf(value[1]));
		collector.emit(new Values(json.toString()));
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("json"));
		
	}

}
