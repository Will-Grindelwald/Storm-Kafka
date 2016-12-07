package cn.ac.sict.gqb.demo;


import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class testBolt extends BaseBasicBolt{

	/**
	 * 测试storm处理速率
	 */
	private static final long serialVersionUID = 4992208759446361359L;
	public Logger log = LoggerFactory.getLogger(testBolt.class);
	
	private static int count=0;
	private static long startTime;   //获取开始时间  
	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		if(count==0){
			startTime=System.nanoTime();
		}
		count++;
		long endTime=System.nanoTime();
		int a=(int)((endTime-startTime)*Math.pow(10, -9));
		if(a==1){
			log.info("每秒发送："+count+"条数据");
			count=0;
			startTime=System.nanoTime();
		}
		
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		
		
	}

}
