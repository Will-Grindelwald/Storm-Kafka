#!/bin/bash
if [ $# != 1 ]
then
  echo "usage: ./zserver.sh start/stop/status"
  exit 1
fi

cd $ZK_HOME
bin/zkServer.sh $1
