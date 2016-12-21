#!/bin/bash
if [ $# != 0 ]
then
  echo "usage: ./boot-all.sh"
  exit 1
fi

cur=$(cd `dirname $0`; pwd)

$cur/zserver.sh start
$cur/start-storm.sh
$cur/start-kafka.sh
$cur/start-manager.sh
