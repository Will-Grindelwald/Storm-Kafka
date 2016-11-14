package cn.ac.sict.gqb.demo;

import java.util.Map;

import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichBolt;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

public class TemperBolt implements IRichBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2707870032197593650L;
	OutputCollector _collector;

	@Override
	public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
		this._collector = collector;

	}

	@Override
	public void execute(Tuple input) {

		String value = input.getStringByField("value");
		String site = input.getStringByField("site");
		String temper[] = value.split(":");
		if (temper[0].equals("temper")) {
			for (int i = 1; i < temper.length; ++i) {
				int t = Integer.parseInt(temper[i]);
				if (t > 60 || t < -20) {
					_collector.emit(new Values("站点:" + site+" " + i + "号传感器检测到" + "温度出现异常！" + "温度：" + t));
				}
			}
		}
		_collector.ack(input);
	}

	@Override
	public void cleanup() {

	}

	@Override
	public Map<String, Object> getComponentConfiguration() {
		return null;
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("warn"));
	}

}
