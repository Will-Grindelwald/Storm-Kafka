#!/bin/bash
if [ $# != 1 ]
then
  echo "usage: ./kafka-delete-topic.sh topicName"
  exit 1
fi

kafka-topics.sh --delete --zookeeper $ZK_HOSTS --topic $1
