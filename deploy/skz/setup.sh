#!/bin/bash

# usage
if [ $1 -ne 0 ]
  if [ $1 -ne 1 ]
  then
    echo "usage: ./setup.sh 0 for standalone"
    echo "usage: ./setup.sh 1 zoo_num storm_num kafka_num kafka_manager_bool"
    exit 1
  elif [ $# -ne 5 ]
  then
    echo "usage: ./setup.sh 0 for standalone"
    echo "usage: ./setup.sh 1 zoo_num storm_num kafka_num kafka_manager_bool"
    exit 1
  fi
fi

# init
__CUR_PATH__=$(cd `dirname $0`; pwd)
__INSTALL_PATH__=/home/$(whoami)/work
if [ -e ${__INSTALL_PATH__} ]; then mv -f ${__INSTALL_PATH__} ${__INSTALL_PATH__}_old; fi
mkdir -p ${__INSTALL_PATH__}

# start configure
if [ $1 -eq 0 ]
then
  # standalone
  ${__CUR_PATH__}/script/configZoo $1 $2 __CUR_PATH__ __INSTALL_PATH__
  ${__CUR_PATH__}/script/configStorm $1 $3 __CUR_PATH__ __INSTALL_PATH__
  ${__CUR_PATH__}/script/configKafka $1 $4 __CUR_PATH__ __INSTALL_PATH__
  ${__CUR_PATH__}/script/configKafkaManager $1 $5 __CUR_PATH__ __INSTALL_PATH__
  NEW_PATH='$ZK_HOME/bin:$STORM_HOME/bin:$KAFKA_HOME/bin:$KAFKA_MANAGER_HOME/bin'
else
  # cluster
  if [ $2 -gt 0 ]; then ${__CUR_PATH__}/script/configZoo $1 $2 __CUR_PATH__ __INSTALL_PATH__; NEW_PATH+='$ZK_HOME/bin:'; fi
  if [ $3 -gt 0 ]; then ${__CUR_PATH__}/script/configStorm $1 $3 __CUR_PATH__ __INSTALL_PATH__; NEW_PATH+='$STORM_HOME/bin:'; fi
  if [ $4 -gt 0 ]; then ${__CUR_PATH__}/script/configKafka $1 $4 __CUR_PATH__ __INSTALL_PATH__; NEW_PATH+='$KAFKA_HOME/bin:'; fi
  if [ $5 -gt 0 ]; then ${__CUR_PATH__}/script/configKafkaManager $1 $5 __CUR_PATH__ __INSTALL_PATH__; NEW_PATH+='$KAFKA_MANAGER_HOME/bin:'; fi
  NEW_PATH=${NEW_PATH%,*}
fi

if [ "x$NEW_PATH" -ne "x" ]
then
echo "export WORKDIR=${__INSTALL_PATH__}" >> ~/.bashrc
echo "export PATH=\$PATH:$NEW_PATH" >> ~/.bashrc
source ~/.bashrc
fi
chmod 754 ${__CUR_PATH__}/boot/*
cp -r ${__CUR_PATH__}/boot ${__INSTALL_PATH__}/..

echo "下面将使用 sudo 权限配置防火墙"
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
#sudo firewall-cmd --permanent --add-rich-rule="rule family='ipv4' source address='192.168.1.1' accept"
sudo firewall-cmd --reload

# host mapping
function hostMapping()
{
  for((i=1;i<=$1;i++))
  do
    sudo echo "$(GetKey "host_mapping.IP$i") $(GetKey "host_mapping.HOST$i")" >> /etc/hosts
  done
}

if [ $(GetKey host_mapping.NUM) -gt 0 ]; then hostMapping $(GetKey host_mapping.NUM); fi
