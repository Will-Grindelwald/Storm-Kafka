#!/bin/bash
if [ $# != 1 ]
then
  echo "usage: ./kafka-producer.sh topicName"
  exit 1
fi

kafka-console-producer.sh --broker-list $BROKER_LIST --topic $1
