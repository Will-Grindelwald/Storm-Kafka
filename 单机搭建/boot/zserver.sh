#!/bin/bash
if [ $# != 1 ]
then
  echo "usage: ./zserver.sh start/status/stop"
  exit 1
fi

cd $WORKDIR/zookeeper-*
bin/zkServer.sh $1
