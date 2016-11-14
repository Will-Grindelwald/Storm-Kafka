package cn.ac.sict.gqb.demo;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HumiBolt extends BaseBasicBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8108049524855976667L;
	public Logger log = LoggerFactory.getLogger(Site1Bolt.class);
	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		String value = input.getStringByField("value");
		log.info("humi"+value);
		String site = input.getStringByField("site");
		String humi[] = value.split(":");
		
		if (humi[0].equals("humi")) {
			for (int i = 1; i < humi.length; ++i) {
				double h = Double.parseDouble(humi[i]);
				log.info("humi"+h);
				if (h > 98.00 || h < 5.00) {
					collector.emit(new Values("站点:" + site +" "+ i + "号传感器检测到" + "湿度出现异常！" + "湿度：" + h));
				}
			}
		}

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("warn"));
	}

}
