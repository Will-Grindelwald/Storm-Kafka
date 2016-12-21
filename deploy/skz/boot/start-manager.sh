#!/bin/bash
if [ $# != 0 ]
then
  echo "usage: ./start-manager.sh"
  exit 1
fi

cd $KAFKA_MANAGER_HOME
nohup bin/kafka-manager -Dconfig.file=conf/application.conf -Dhttp.port=9100 > kafka-manager-boot.log 2>&1 &
