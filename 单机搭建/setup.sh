#!/bin/bash
__CUR_PATH__=$(cd `dirname $0`; pwd)
__INSTALL_PATH__=/home/$(whoami)/work

if [ -e ${__INSTALL_PATH__} ]; then mv -f ${__INSTALL_PATH__} ${__INSTALL_PATH__}_old; fi
mkdir -p ${__INSTALL_PATH__}

# zookeeper
tar -xzf ${__CUR_PATH__}/zookeeper/zookeeper-*.tar.gz -C ${__INSTALL_PATH__}
cp -f ${__CUR_PATH__}/config/zoo.cfg ${__INSTALL_PATH__}/zookeeper-*/conf
echo "dataDir=${__INSTALL_PATH__}/zookeeper/data" >> ${__INSTALL_PATH__}/zookeeper-*/conf/zoo.cfg
echo "dataLogDir=${__INSTALL_PATH__}/zookeeper/logs" >> ${__INSTALL_PATH__}/zookeeper-*/conf/zoo.cfg
mkdir -p ${__INSTALL_PATH__}/zookeeper/data ${__INSTALL_PATH__}/zookeeper/logs

# storm
tar -xzf ${__CUR_PATH__}/storm/apache-storm-*.tar.gz -C ${__INSTALL_PATH__}
cp -f ${__CUR_PATH__}/config/storm.yaml ${__INSTALL_PATH__}/apache-storm-*/conf/storm.yaml
cd ${__INSTALL_PATH__}/apache-storm-*
echo "storm.local.dir: \"${__INSTALL_PATH__}/storm-workdir\"" >> conf/storm.yaml
mkdir logs
cd - >> /tmp/null
mkdir ${__INSTALL_PATH__}/storm-workdir

# kafka
tar -xzf ${__CUR_PATH__}/kafka/kafka_*.tgz -C ${__INSTALL_PATH__}
cp -f ${__CUR_PATH__}/config/server.properties ${__INSTALL_PATH__}/kafka_*/config/server.properties
cd ${__INSTALL_PATH__}/kafka_*
echo "log.dirs=${__INSTALL_PATH__}/kafka-logs" >> config/server.properties
mkdir logs
cd - >> /tmp/null
mkdir ${__INSTALL_PATH__}/kafka-logs

# bootup script
cp ${__CUR_PATH__}/boot/* ${__INSTALL_PATH__}

echo "export WORKDIR=${__INSTALL_PATH__}" >> ~/.bashrc
source ~/.bashrc
