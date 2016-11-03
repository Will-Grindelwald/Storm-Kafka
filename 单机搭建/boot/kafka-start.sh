#!/bin/bash
if [ $# != 0 ]
then
  echo "usage: ./kafka-start.sh"
  exit 1
fi

cd $WORKDIR/kafka_*
nohup bin/kafka-server-start.sh config/server.properties > logs/kafka-server-boot.log 2>&1 &
