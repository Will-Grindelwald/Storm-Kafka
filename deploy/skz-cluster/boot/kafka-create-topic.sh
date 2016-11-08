#!/bin/bash
if [ $# != 3 ]
then
  echo "usage: ./kafka-create-topic.sh topicName replication-factor partitions"
  exit 1
fi

kafka-topics.sh --create --zookeeper $ZK_HOSTS --topic $1 --replication-factor $2 --partitions $3
