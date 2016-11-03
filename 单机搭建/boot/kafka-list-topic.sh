#!/bin/bash
if [ $# != 0 ]
then
  echo "usage: ./kafka-list-topic.sh"
  exit 1
fi

cd $WORKDIR/kafka_*
bin/kafka-topics.sh --list --zookeeper 192.168.125.171:2181,192.168.125.172:2181,192.168.125.173:2181
