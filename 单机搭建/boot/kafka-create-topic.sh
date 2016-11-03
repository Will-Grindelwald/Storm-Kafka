#!/bin/bash
if [ $# != 3 ]
then
  echo "usage: ./kafka-create-topic.sh topicName replication-factor partitions"
  exit 1
fi

cd $WORKDIR/kafka_*
bin/kafka-topics.sh --create --zookeeper 192.168.125.171:2181,192.168.125.172:2181,192.168.125.173:2181 --topic $1 --replication-factor $2 --partitions $3
