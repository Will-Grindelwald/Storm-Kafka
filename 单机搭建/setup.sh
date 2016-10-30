#!/bin/bash
curpath=$(cd `dirname $0`; pwd)
installpath=/home/$(whoami)/work

if [ -e $installpath ]; then mv -f $installpath ${installpath}_old; fi
mkdir -p $installpath

# zookeeper
tar -xzf $curpath/zookeeper/zookeeper-*.tar.gz -C $installpath
cp -f $curpath/config/zoo.cfg $installpath/zookeeper-*/conf
echo "dataDir=$installpath/zookeeper/data" >> $installpath/zookeeper-*/conf/zoo.cfg
echo "dataLogDir=$installpath/zookeeper/logs" >> $installpath/zookeeper-*/conf/zoo.cfg
mkdir -p $installpath/zookeeper/data $installpath/zookeeper/logs

# storm
tar -xzf $curpath/storm/apache-storm-*.tar.gz -C $installpath
cp -f $curpath/config/storm.yaml $installpath/apache-storm-*/conf/storm.yaml
cd $installpath/apache-storm-*
echo "storm.local.dir: \"$installpath/storm-workdir\"" >> conf/storm.yaml
mkdir logs
cd - >> /tmp/null
mkdir $installpath/storm-workdir

# kafka
tar -xzf $curpath/kafka/kafka_*.tgz -C $installpath
cp -f $curpath/config/server.properties $installpath/kafka_*/config/server.properties
cd $installpath/kafka_*
echo "log.dirs=$installpath/kafka-logs" >> config/server.properties
mkdir logs
cd - >> /tmp/null
mkdir $installpath/kafka-logs

# bootup script
cp $curpath/boot/* $installpath

echo "export WORKDIR=$installpath" >> ~/.bashrc
source ~/.bashrc
