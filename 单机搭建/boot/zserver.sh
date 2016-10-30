#!/bin/bash
cd $WORKDIR/zookeeper-*
bin/zkServer.sh $1
cd - >> /tmp/null
