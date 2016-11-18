#!/bin/bash
function usage(){
  echo "usage: ./setup.sh 0 for standalone"
  echo "usage: ./setup.sh 1 zoo_num storm_num kafka_num kafka_manager_bool"
  exit 1
}

# usage
if [ $# = 0 ]; then usage; fi
if [ $1 != 0 ]; then
  if [ $1 != 1 ]; then usage;
  elif [ $# != 5 ]; then usage; fi
fi

# init
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

# start configure
chmod 754 ${__CUR_PATH__}/scripts/*
pathArgs="${__CUR_PATH__} ${__INSTALL_PATH__}"
if [ $2 != 0 ]; then ${__CUR_PATH__}/scripts/configZoo $1 $2 $pathArgs; NEW_PATH+='$ZK_HOME/bin:'; fi
if [ $3 != 0 ]; then ${__CUR_PATH__}/scripts/configStorm $1 $3 $pathArgs; NEW_PATH+='$STORM_HOME/bin:'; fi
if [ $4 != 0 ]; then ${__CUR_PATH__}/scripts/configKafka $1 $4 $pathArgs; NEW_PATH+='$KAFKA_HOME/bin:'; fi
if [ $5 != 0 ]; then ${__CUR_PATH__}/scripts/configKafkaManager $1 $5 $pathArgs; NEW_PATH+='$KAFKA_MANAGER_HOME/bin:'; fi
NEW_PATH=${NEW_PATH%,*}

if [ "x$NEW_PATH" != "x" ]; then
  echo "export WORKDIR=${__INSTALL_PATH__}" >> ~/.bashrc
  echo "export PATH=\$PATH:$NEW_PATH" >> ~/.bashrc
  source ~/.bashrc
fi
chmod 754 ${__CUR_PATH__}/boot/*
cp -r ${__CUR_PATH__}/boot ${__INSTALL_PATH__}/..

echo "下面将使用 sudo 权限配置防火墙"
#sudo firewall-cmd --zone=public --add-port=2181/tcp --permanent
#sudo firewall-cmd --zone=public --add-port=2888/tcp --permanent
#sudo firewall-cmd --zone=public --add-port=3888/tcp --permanent

#sudo firewall-cmd --zone=public --add-port=3772/tcp --permanent
#sudo firewall-cmd --zone=public --add-port=3773/tcp --permanent
#sudo firewall-cmd --zone=public --add-port=3774/tcp --permanent
#sudo firewall-cmd --zone=public --add-port=6627/tcp --permanent
#sudo firewall-cmd --zone=public --add-port=6699/tcp --permanent
#sudo firewall-cmd --zone=public --add-port=8000/tcp --permanent
#sudo firewall-cmd --zone=public --add-port=8080/tcp --permanent
#sudo firewall-cmd --zone=public --add-port=6700/tcp --permanent
#sudo firewall-cmd --zone=public --add-port=6701/tcp --permanent
#sudo firewall-cmd --zone=public --add-port=6702/tcp --permanent
#sudo firewall-cmd --zone=public --add-port=6703/tcp --permanent

#sudo firewall-cmd --zone=public --add-port=9092/tcp --permanent
#sudo firewall-cmd --zone=public --add-port=9000/tcp --permanent
#sudo firewall-cmd --zone=public --add-port=9999/tcp --permanent

# host mapping
for((i=1;i<=$(GetKey host_mapping.NUM);i++)); do
  sudo echo "$(GetKey "host_mapping.IP$i") $(GetKey "host_mapping.HOST$i")" >> /etc/hosts
  sudo firewall-cmd --permanent --add-rich-rule="rule family='ipv4' source address='$(GetKey "host_mapping.IP$i")' accept"
done
sudo firewall-cmd --reload
