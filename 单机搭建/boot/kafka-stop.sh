#!/bin/bash
if [ $# != 0 ]
then
  echo "usage: ./kafka-stop.sh"
  exit 1
fi

cd $WORKDIR/kafka_*
bin/kafka-server-stop.sh config/server.properties
