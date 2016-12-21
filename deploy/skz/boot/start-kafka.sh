#!/bin/bash
if [ $# != 0 ]
then
  echo "usage: ./start-kafka.sh"
  exit 1
fi

cd $KAFKA_HOME
JMX_PORT=9999 bin/kafka-server-start.sh -daemon config/server.properties --override -Djava.rmi.server.hostname=`hostname`
