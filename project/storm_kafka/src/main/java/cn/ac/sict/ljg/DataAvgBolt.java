package cn.ac.sict.ljg;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class DataAvgBolt extends BaseBasicBolt {
    private HashMap<String, Long> counts = new HashMap<>();
    private HashMap<String,  Double> sum = new HashMap<>();
	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
	String timeStamp = input.getStringByField("timeStamp");
	Double  concentration  = input.getDoubleByField("concentration"); 
	Long count = this.counts.get(timeStamp);
	Double  sumValue = this.sum .get(timeStamp);
	if(count==null) {
		
		count=0L;
		sumValue=0d;
	}
	count++;
	sumValue+= concentration  ;
	double avg = sumValue/count;
	BigDecimal bg = new BigDecimal(avg).setScale(2, RoundingMode.UP);
    double concentrationAvg= bg.doubleValue();
	counts.put(timeStamp, count );
	sum.put(timeStamp,sumValue  );
	collector.emit(new Values(timeStamp,concentrationAvg));
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		 declarer.declare(new Fields("timeStamp","concentrationAvg" ));
		
	}

 

}
