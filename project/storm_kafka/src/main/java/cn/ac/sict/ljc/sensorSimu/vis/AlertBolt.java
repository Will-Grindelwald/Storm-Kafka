package cn.ac.sict.ljc.sensorSimu.vis;

import java.util.HashMap;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.json.simple.JSONObject;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * 
 */
public class AlertBolt extends BaseBasicBolt {

	/* long: serialVersionUID * description： */
	private static final long serialVersionUID = 3307570869378763288L;

	private static final String[][] sensorType = {{"temper", "pressure"}, {"温度", "气压"}};

	private static final String host = "192.168.125.171";
	private static final int port = 6378;
	private static final String password = "yourpassword";

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) {
		String[] msg = tuple.getStringByField("msg").split(":");
		int type = Integer.valueOf(msg[1]); // 0 温度, 1 压力

		JedisPool pool = new JedisPool(new JedisPoolConfig(), host, port);
		try (Jedis jedis = pool.getResource()) {
			jedis.auth(password);
			HashMap<String, String> jsonMap = new HashMap<String, String>();
			jsonMap.put("time", msg[0]);
			jsonMap.put("value", msg[2]);
			jedis.publish(sensorType[0][type], JSONObject.toJSONString(jsonMap));
		}
		pool.close();

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		// 声明本次 emit 出去的 field
		declarer.declareStream("temper", new Fields("warningTemper"));
		declarer.declareStream("pressure", new Fields("warningPressure"));
	}

}
