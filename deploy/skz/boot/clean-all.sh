#!/bin/bash
if [ $# != 0 ]
then
  echo "usage: ./clean-all.sh"
  exit 1
fi

rm -rf $WORKDIR/kafka-logs/*
rm -rf $KAFKA_HOME/logs/*

mv $WORKDIR/zookeeper/data/myid $WORKDIR/zookeeper
rm -rf $WORKDIR/zookeeper/data/*
rm -rf $WORKDIR/zookeeper/logs/*
mv $WORKDIR/zookeeper/myid $WORKDIR/zookeeper/data

rm -rf $WORKDIR/storm-workdir/*
rm -rf $STORM_HOME/logs/*

rm -rf $KAFKA_MANAGER_HOME/kafka-manager-boot.log
rm -rf $KAFKA_MANAGER_HOME/RUNNING_PID
