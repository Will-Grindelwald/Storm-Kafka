package cn.ac.sict.ljc.sensorSimu_terminal;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

/**
 * 
 */
public class AlertBolt extends BaseBasicBolt {

	/* long: serialVersionUID * description： */
	private static final long serialVersionUID = 5409424749649068963L;

	private static final double[][] Threshold = {{31, 69}, {3.05, 4.95}};
	public static final String[][] sensorType = {{"ljc_sensor_temper", "ljc_sensor_pressure"}, {"温度", "气压"}};
	public static final String fieldsTemper = "warningTemper";
	public static final String fieldsPressure = "warningPressure";

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		String[] msg = tuple.getStringByField("msg").split(":");
		int type = Integer.valueOf(msg[1]); // 0 温度, 1 压力
		double value = Double.valueOf(msg[2]);
		if(value < Threshold[type][0])
			collector.emit(sensorType[0][type], new Values("!warning: " + msg[0] + "  " + sensorType[1][type] + "  过低  value:" + value));
		else if( value > Threshold[type][1])
			collector.emit(sensorType[0][type], new Values("!warning: " + msg[0] + "  " + sensorType[1][type] + "  过高  value:" + value));
		else collector.emit(sensorType[0][type], new Values(" message: " + msg[0] + "  " + sensorType[1][type] + "  正常  value:" + value));
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// 声明本次 emit 出去的 field
		declarer.declareStream(sensorType[0][0], new Fields(fieldsTemper));
		declarer.declareStream(sensorType[0][1], new Fields(fieldsPressure));
	}

}
