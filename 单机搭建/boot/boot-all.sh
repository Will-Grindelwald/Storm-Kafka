#!/bin/bash
cur=$(cd `dirname $0`; pwd)

$cur/zserver.sh start
$cur/storm-start.sh
$cur/kafka-start.sh
