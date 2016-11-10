#!/bin/bash
if [ $# != 3 ]
then
  echo "usage: ./setup.sh zoo_num storm_num kafka_num"
  exit 1
fi

__CUR_PATH__=$(cd `dirname $0`; pwd)
__INSTALL_PATH__=/home/$(whoami)/work
if [ -e ${__INSTALL_PATH__} ]; then mv -f ${__INSTALL_PATH__} ${__INSTALL_PATH__}_old; fi
mkdir -p ${__INSTALL_PATH__}

function GetKey(){
  section=$(echo $1 | cut -d '.' -f 1)
  key=$(echo $1 | cut -d '.' -f 2)
  sed -n "/\[$section\]/,/\[.*\]/{
    /^\[.*\]/d
    /^[ \t]*$/d
    /^$/d
    /^#.*$/d
    s/^[ \t]*$key[ \t]*=[ \t]*\(.*\)[ \t]*/\1/p
  }" ${__CUR_PATH__}/config
}

# zookeeper 配置
function configZoo()
{
  echo "配置 zookeeper"
  tar -xzf ${__CUR_PATH__}/zookeeper/zookeeper-*.tar.gz -C ${__INSTALL_PATH__}
  ZK_HOME=$(cd ${__INSTALL_PATH__}/zookeeper-*; pwd)
  mkdir -p ${__INSTALL_PATH__}/zookeeper/data ${__INSTALL_PATH__}/zookeeper/logs
  cp -f ${__CUR_PATH__}/conf/zoo.cfg $ZK_HOME/conf/
  echo "dataDir=${__INSTALL_PATH__}/zookeeper/data" >> $ZK_HOME/conf/zoo.cfg
  echo "dataLogDir=${__INSTALL_PATH__}/zookeeper/logs" >> $ZK_HOME/conf/zoo.cfg
  server_num=$(GetKey zookeeper.SERVER_NUM)
  for((i=1;i<=server_num;i++))
  do
    IP=$(GetKey "zookeeper.IP$i")
    echo "server.$i=$IP:2888:3888" >> $ZK_HOME/conf/zoo.cfg
    ZK_HOSTS+="$IP:2181,"
  done
  ZK_HOSTS=${ZK_HOSTS%,*}
  echo $1 > ${__INSTALL_PATH__}/zookeeper/data/myid
  echo "export ZK_HOME=$ZK_HOME" >> ~/.bashrc
  echo "export ZK_HOSTS=$ZK_HOSTS" >> ~/.bashrc
}

# storm 配置
function configStorm()
{
  echo "配置 storm"
  tar -xzf ${__CUR_PATH__}/storm/apache-storm-*.tar.gz -C ${__INSTALL_PATH__}
  STORM_HOME=$(cd ${__INSTALL_PATH__}/apache-storm-*; pwd)
  mkdir -p ${__INSTALL_PATH__}/storm-workdir $STORM_HOME/logs
  echo "storm.zookeeper.servers:" >> $STORM_HOME/conf/storm.yaml
  server_num=$(GetKey zookeeper.SERVER_NUM)
  for((i=1;i<=server_num;i++)); do echo "    - \"$(GetKey "zookeeper.IP$i")\"" >> $STORM_HOME/conf/storm.yaml; done
  nimbus_num=$(GetKey storm.NIMBUS_NUM)
  for((i=1;i<=nimbus_num;i++)); do nimbus_seeds+="\"$(GetKey "storm.HOST$i")\","; done ####
  nimbus_seeds=${nimbus_seeds%,*}
  echo "nimbus.seeds: [$nimbus_seeds]" >> $STORM_HOME/conf/storm.yaml
  echo "storm.local.dir: \"${__INSTALL_PATH__}/storm-workdir\"" >> $STORM_HOME/conf/storm.yaml
  echo "supervisor.slots.ports:" >> $STORM_HOME/conf/storm.yaml
  echo "    - 6700" >> $STORM_HOME/conf/storm.yaml
  echo "    - 6701" >> $STORM_HOME/conf/storm.yaml
  echo "    - 6702" >> $STORM_HOME/conf/storm.yaml
  echo "    - 6703" >> $STORM_HOME/conf/storm.yaml
  echo "export STORM_HOME=$STORM_HOME" >> ~/.bashrc
}

# kafka 配置
function configKafka()
{
  echo "配置 kafka"
  tar -xzf ${__CUR_PATH__}/kafka/kafka_*.tgz -C ${__INSTALL_PATH__}
  KAFKA_HOME=$(cd ${__INSTALL_PATH__}/kafka_*; pwd)
  mkdir ${__INSTALL_PATH__}/kafka-logs $KAFKA_HOME/logs
  cp -f ${__CUR_PATH__}/conf/server.properties $KAFKA_HOME/config/server.properties
  echo "broker.id=$1" >> $KAFKA_HOME/config/server.properties
  echo "host.name=Broker$1" >> $KAFKA_HOME/config/server.properties ####
  echo "log.dirs=${__INSTALL_PATH__}/kafka-logs" >> $KAFKA_HOME/config/server.properties
  server_num=$(GetKey zookeeper.SERVER_NUM)
  for((i=1;i<=server_num;i++)); do ZK_HOSTS_K+="$(GetKey "zookeeper.IP$i"):2181,"; done
  ZK_HOSTS_K=${ZK_HOSTS_K%,*}
  echo "zookeeper.connect=$ZK_HOSTS_K" >> $KAFKA_HOME/config/server.properties
  broker_num=$(GetKey kafka.BROKER_NUM)
  for((i=1;i<=broker_num;i++)); do BROKER_LIST+="$(GetKey "kafka.HOST$i"):9092,"; done
  BROKER_LIST=${BROKER_LIST%,*}
  echo "export KAFKA_HOME=$KAFKA_HOME" >> ~/.bashrc
  echo "export BROKER_LIST=$BROKER_LIST" >> ~/.bashrc
}

function hostMapping()
{
  for((i=1;i<=$1;i++))
  do
    sudo echo "$(GetKey "host_mapping.IP$i") $(GetKey "host_mapping.HOST$i")" >> /etc/hosts
  done
}

if [ $1 -gt 0 ]; then configZoo $1; fi
if [ $2 -gt 0 ]; then configStorm $2; fi
if [ $3 -gt 0 ]; then configKafka $3; fi

echo "export WORKDIR=${__INSTALL_PATH__}" >> ~/.bashrc
echo 'export PATH=$PATH:$ZK_HOME/bin:$STORM_HOME/bin:$KAFKA_HOME/bin' >> ~/.bashrc
source ~/.bashrc
chmod 754 ${__CUR_PATH__}/boot/*
cp -r ${__CUR_PATH__}/boot ${__INSTALL_PATH__}/..

echo "下面将使用 sudo 权限开启必须的端口"
sudo firewall-cmd --zone=public --add-port=2181/tcp --permanent
sudo firewall-cmd --zone=public --add-port=2888/tcp --permanent
sudo firewall-cmd --zone=public --add-port=3888/tcp --permanent

sudo firewall-cmd --zone=public --add-port=3772/tcp --permanent
sudo firewall-cmd --zone=public --add-port=3773/tcp --permanent
sudo firewall-cmd --zone=public --add-port=3774/tcp --permanent
sudo firewall-cmd --zone=public --add-port=6627/tcp --permanent
sudo firewall-cmd --zone=public --add-port=6699/tcp --permanent
sudo firewall-cmd --zone=public --add-port=8000/tcp --permanent
sudo firewall-cmd --zone=public --add-port=8080/tcp --permanent
sudo firewall-cmd --zone=public --add-port=6700/tcp --permanent
sudo firewall-cmd --zone=public --add-port=6701/tcp --permanent
sudo firewall-cmd --zone=public --add-port=6702/tcp --permanent
sudo firewall-cmd --zone=public --add-port=6703/tcp --permanent

sudo firewall-cmd --zone=public --add-port=9092/tcp --permanent
sudo firewall-cmd --zone=public --add-port=9000/tcp --permanent
sudo firewall-cmd --zone=public --add-port=9999/tcp --permanent
sudo firewall-cmd --reload

if [ $(GetKey host_mapping.NUM) -gt 0 ]; then hostMapping $(GetKey host_mapping.NUM); fi
