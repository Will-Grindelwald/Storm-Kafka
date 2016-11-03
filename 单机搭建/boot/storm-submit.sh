#!/bin/bash
STORM_DIR=$(cd $WORKDIR/apache-storm-*; pwd)
$STORM_DIR/bin/storm $@
