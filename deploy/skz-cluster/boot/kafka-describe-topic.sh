#!/bin/bash
if [ $# > 1 ]
then
  echo "usage: ./kafka-describe-topic.sh [topicName]"
  exit 1
fi

kafka-topics.sh --describe --zookeeper $ZK_HOSTS --topic $@
