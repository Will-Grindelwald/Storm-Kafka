package cn.ac.sict.ljg;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

public class ConcentrationSpout extends BaseRichSpout  {
	SpoutOutputCollector collector =null;
	@Override
	public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
		// TODO Auto-generated method stub
		this.collector=collector;
	}

	@Override
	public void nextTuple() {
		Random random =new Random();
		   
		 
		    
	     
	       SimpleDateFormat simpleDateFormat= new SimpleDateFormat("yyyy-MM-dd|HH:mm:ss.SSS");
	       String TimeStamp = simpleDateFormat.format(new Date());
	       double d= random.nextDouble()*2; 
	   	   BigDecimal bg = new BigDecimal(d).setScale(2, RoundingMode.UP);
	       double concentration= bg.doubleValue();
	       String data = TimeStamp+"|"+concentration;
	       collector.emit(new Values(data));
	      
	       
	   
		   }
		
 

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		 
		declarer.declare(new Fields("data"));
	}

}
