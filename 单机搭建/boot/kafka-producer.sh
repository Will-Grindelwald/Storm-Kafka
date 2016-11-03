#!/bin/bash
if [ $# != 1 ]
then
  echo "usage: ./kafka-producer.sh topicName"
  exit 1
fi

cd $WORKDIR/kafka_*
bin/kafka-console-producer.sh --broker-list 192.168.125.171:9092,192.168.125.172:9092,192.168.125.173:9092 --topic $1
