package cn.ac.sict.kafka_producer_prof_test;

import java.util.Map;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

public class SimplePartitioner implements Partitioner {

	@Override
	public void configure(Map<String, ?> arg0) {

	}

	@Override
	public void close() {

	}

	@Override
	public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
		int a_numPartitions = cluster.availablePartitionsForTopic(topic).size();
		return Integer.parseInt((String) key) % a_numPartitions;
	}

}
