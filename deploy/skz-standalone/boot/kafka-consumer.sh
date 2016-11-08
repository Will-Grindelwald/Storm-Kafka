#!/bin/bash
if [ $# != 1 ]
then
  echo "usage: ./kafka-consumer.sh topicName"
  exit 1
fi

kafka-console-consumer.sh --zookeeper $ZK_HOSTS --topic $1
