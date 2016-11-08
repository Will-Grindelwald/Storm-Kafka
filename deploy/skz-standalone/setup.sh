#!/bin/bash
if [ $# != 0 ]
then
  echo "usage: ./setup.sh"
  exit 1
fi

__CUR_PATH__=$(cd `dirname $0`; pwd)
__INSTALL_PATH__=/home/$(whoami)/work
if [ -e ${__INSTALL_PATH__} ]; then mv -f ${__INSTALL_PATH__} ${__INSTALL_PATH__}_old; fi
mkdir -p ${__INSTALL_PATH__}

# zookeeper
echo "配置 zookeeper"
tar -xzf ${__CUR_PATH__}/zookeeper/zookeeper-*.tar.gz -C ${__INSTALL_PATH__}
ZK_HOME=$(cd ${__INSTALL_PATH__}/zookeeper-*; pwd)
mkdir -p ${__INSTALL_PATH__}/zookeeper/data ${__INSTALL_PATH__}/zookeeper/logs
cp -f ${__CUR_PATH__}/conf/zoo.cfg $ZK_HOME/conf
echo "dataDir=${__INSTALL_PATH__}/zookeeper/data" >> $ZK_HOME/conf/zoo.cfg
echo "dataLogDir=${__INSTALL_PATH__}/zookeeper/logs" >> $ZK_HOME/conf/zoo.cfg
echo "export ZK_HOME=$ZK_HOME" >> ~/.bashrc
echo "export ZK_HOSTS=localhost:2181" >> ~/.bashrc

# storm
echo "配置 storm"
tar -xzf ${__CUR_PATH__}/storm/apache-storm-*.tar.gz -C ${__INSTALL_PATH__}
STORM_HOME=$(cd ${__INSTALL_PATH__}/apache-storm-*; pwd)
mkdir -p ${__INSTALL_PATH__}/storm-workdir $STORM_HOME/logs
cp -f ${__CUR_PATH__}/conf/storm.yaml $STORM_HOME/conf/storm.yaml
echo "storm.local.dir: \"${__INSTALL_PATH__}/storm-workdir\"" >> $STORM_HOME/conf/storm.yaml
echo "export STORM_HOME=$STORM_HOME" >> ~/.bashrc

# kafka
echo "配置 kafka"
tar -xzf ${__CUR_PATH__}/kafka/kafka_*.tgz -C ${__INSTALL_PATH__}
KAFKA_HOME=$(cd ${__INSTALL_PATH__}/kafka_*; pwd)
mkdir ${__INSTALL_PATH__}/kafka-logs $KAFKA_HOME/logs
cp -f ${__CUR_PATH__}/conf/server.properties $KAFKA_HOME/config/server.properties
echo "log.dirs=${__INSTALL_PATH__}/kafka-logs" >> $KAFKA_HOME/config/server.properties
echo "export KAFKA_HOME=$KAFKA_HOME" >> ~/.bashrc
echo "export BROKER_LIST=localhost:9092" >> ~/.bashrc

echo "export WORKDIR=${__INSTALL_PATH__}" >> ~/.bashrc
echo 'export PATH=$PATH:$ZK_HOME/bin:$STORM_HOME/bin:$KAFKA_HOME/bin' >> ~/.bashrc
source ~/.bashrc
cp -r ${__CUR_PATH__}/boot ${__INSTALL_PATH__}
