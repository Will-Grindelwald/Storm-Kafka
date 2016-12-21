#!/bin/bash
if [ $# != 0 ]
then
  echo "usage: ./start-hadoop.sh"
  exit 1
fi

start-dfs.sh
start-yarn.sh
