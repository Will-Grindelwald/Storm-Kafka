#!/bin/bash
if [ $# != 1 ]
then
  echo "usage: ./kafka-describe-topic.sh topicName"
  exit 1
fi

cd $WORKDIR/kafka_*
bin/kafka-topics.sh --describe --zookeeper 192.168.125.171:2181,192.168.125.172:2181,192.168.125.173:2181 --topic $1
