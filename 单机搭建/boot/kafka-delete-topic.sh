#!/bin/bash
if [ $# != 1 ]
then
  echo "usage: ./kafka-delete-topic.sh topicName"
  exit 1
fi

cd $WORKDIR/kafka_*
bin/kafka-topics.sh --delete --zookeeper 192.168.125.171:2181,192.168.125.172:2181,192.168.125.173:2181 --topic $1
cd $WORKDIR/kafka-logs
rm -rf $1-*
cd $WORKDIR/zookeeper-*
bin/zkCli.sh rmr /brokers/topic/$1