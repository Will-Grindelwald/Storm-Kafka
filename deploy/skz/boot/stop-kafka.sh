#!/bin/bash
if [ $# != 0 ]
then
  echo "usage: ./stop-kafka.sh"
  exit 1
fi

cd $KAFKA_HOME
bin/kafka-server-stop.sh config/server.properties
