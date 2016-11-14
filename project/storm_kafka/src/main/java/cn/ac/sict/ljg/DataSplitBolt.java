package cn.ac.sict.ljg;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class DataSplitBolt extends BaseBasicBolt {

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		String data = (String) input.getStringByField("data"); 
		String[] dataFields = data.split("\\|");
		if( dataFields.length!=0 ){
		String timeStamp = dataFields[0]+dataFields[1];
		Double concentration = Double.parseDouble(dataFields[2]);
		collector.emit(new Values(timeStamp,concentration) );
		}
	}
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) { 
		declarer.declare(new Fields("timeStamp","concentration"));
	}

 

}
