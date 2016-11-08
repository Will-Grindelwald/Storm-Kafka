#!/bin/bash
if [ $# != 0 ]
then
  echo "usage: ./kafka-list-topic.sh"
  exit 1
fi

kafka-topics.sh --list --zookeeper $ZK_HOSTS
