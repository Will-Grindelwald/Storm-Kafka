package cn.ac.sict.kafka_producer_demo2;

import java.util.Map;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;

public class SimplePartitioner implements Partitioner {

	@Override
	public void configure(Map<String, ?> arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
		int a_numPartitions = cluster.availablePartitionsForTopic(topic).size();
		int partition = 0;
		String stringKey = (String) key;
		int offset = stringKey.lastIndexOf('.');
		if (offset > 0) {
			partition = Integer.parseInt(stringKey.substring(offset + 1)) % a_numPartitions;
		}
		return partition;
	}

}
