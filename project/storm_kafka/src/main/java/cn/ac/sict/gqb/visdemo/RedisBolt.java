package cn.ac.sict.gqb.visdemo;

import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisBolt extends BaseBasicBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7901664867978995852L;
	public Logger log = LoggerFactory.getLogger(RedisBolt.class);

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		String str = input.getStringByField("json");
		JedisPool pool = new JedisPool(new JedisPoolConfig(), "192.168.125.171");
		try (Jedis jRedis = pool.getResource()) {
			// msg.put("number2", number2);
			log.info(str);
			jRedis.publish("visData", str);
		}
		pool.destroy();

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {

	}

}
