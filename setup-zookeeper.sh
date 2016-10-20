#!/bin/bash
# usage: ./setup-zookeeper.sh /usr/local/opt 3 1
# $1: the path to install zookeeper
# $2: the numbers of servers in ensemble
# $3: the number of this servers

tar -xzf zookeeper-3.4.9.tar.gz -C $1
mkdir -p $1/zookeeper/data $1/zookeeper/log
echo $3 > $1/zookeeper/data/myid
cat zoo.cfg > $1/zookeeper-3.4.9/conf/zoo.cfg
