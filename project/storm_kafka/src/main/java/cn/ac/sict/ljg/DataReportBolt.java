package cn.ac.sict.ljg;

import java.util.HashMap;
import java.util.Map;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

import com.google.common.collect.Maps;

public class DataReportBolt  extends BaseBasicBolt{
    private  HashMap<String, Double> avgValue =new HashMap<>();
    
	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		String timeStamp = input.getStringByField("timeStamp");
		Double  concentrationAvg  = input.getDoubleByField("concentrationAvg");
	    System.out.println(timeStamp+":"+concentrationAvg);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		 
	}
	@Override
	public void cleanup() {
	 for(Map.Entry<String,Double> entry:avgValue.entrySet() ){
		 
	  String timeStamp  = entry.getKey();
	  Double concentrationAvg = entry.getValue();
	  System.out.println(timeStamp+":"+concentrationAvg);
	 }
}
}
