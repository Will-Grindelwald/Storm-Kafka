#!/bin/bash
cd $WORKDIR/apache-storm-*
nohup bin/storm nimbus > logs/nimbus-boot.log 2>&1 &
nohup bin/storm supervisor > logs/supervisor-boot.log 2>&1 &
nohup bin/storm ui > logs/ui-boot.log 2>&1 &
nohup bin/storm logviewer > logs/logviewer-boot.log 2>&1 &
cd - >> /tmp/null
