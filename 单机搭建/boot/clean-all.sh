#!/bin/bash
if [ $# != 0 ]
then
  echo "usage: ./clean-all.sh"
  exit 1
fi

rm -rf $WORKDIR/kafka-logs/*
KAFKA_DIR=$(cd $WORKDIR/kafka_*; pwd)
rm -rf $KAFKA_DIR/logs/*

mv $WORKDIR/zookeeper/data/myid $WORKDIR/zookeeper
rm -rf $WORKDIR/zookeeper/data/*
rm -rf $WORKDIR/zookeeper/logs/*
mv $WORKDIR/zookeeper/myid $WORKDIR/zookeeper/data

rm -rf $WORKDIR/storm-workdir/*
STORM_DIR=$(cd $WORKDIR/apache-storm-*; pwd)
rm -rf $STORM_DIR/logs/*
