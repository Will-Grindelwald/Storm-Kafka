package cn.ac.sict.ljg;

import java.io.FileWriter;
import java.util.Map;
import java.util.UUID;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

public class SuffixBolt extends BaseBasicBolt {
	/**
	 * 将字符串加后缀并写入文件。
	 */
	private static final long serialVersionUID = -929551452707495748L;
	FileWriter fileWrite = null;
	@Override
	public void prepare(@SuppressWarnings("rawtypes") Map stormConf, TopologyContext context) {
			try {
				fileWrite = new FileWriter("/home/ljg/"+UUID.randomUUID());
			}catch (Exception e) {
			  e.printStackTrace( );	 
			}
	}
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		String upper_word = tuple.getString(0);
		
		String result = upper_word+"_suffix";
		try {
			fileWrite.append(result);
			fileWrite.append("\n");
			fileWrite.flush();
		} catch (Exception e) {
			 e.printStackTrace( );
		}
		
	}

	public void declareOutputFields(OutputFieldsDeclarer arg0) {
		// TODO Auto-generated method stub
		
	}

}
