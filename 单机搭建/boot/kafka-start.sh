#!/bin/bash
cd $WORKDIR/kafka_*
nohup bin/kafka-server-start.sh config/server.properties > logs/kafka-server-boot.log 2>&1 &
cd - >> /tmp/null
