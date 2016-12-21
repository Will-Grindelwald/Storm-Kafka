#!/bin/bash
if [ $# != 0 ]
then
  echo "usage: ./stop-hadoop.sh"
  exit 1
fi

stop-yarn.sh
stop-dfs.sh
