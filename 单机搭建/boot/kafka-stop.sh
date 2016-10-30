#!/bin/bash
cd $WORKDIR/kafka_*
bin/kafka-server-stop.sh config/server.properties
cd - >> /tmp/null
