#!/bin/bash
if [ $# -gt 1 ]
then
  echo "usage: ./kafka-describe-topic.sh [topicName]"
  exit 1
fi

if [ $# -eq 0 ]
then
kafka-topics.sh --describe --zookeeper $ZK_HOSTS
else
kafka-topics.sh --describe --zookeeper $ZK_HOSTS --topic $@
fi
