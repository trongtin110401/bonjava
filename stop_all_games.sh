#!/bin/bash

SCRIPT_PATH=$(pwd)

# kill java process
killProcess() {
  echo "Working path: " . $SCRIPT_PATH
  kill -9 $(ps aux | grep "java -cp" | grep -v 'grep' | awk '{print $2}')
  kill -9 $(ps aux | grep "wsreport" | grep -v 'grep' | awk '{print $2}')
  kill -9 $(ps aux | grep "BoardService" | grep -v 'grep' | awk '{print $2}')
}

main() {
  killProcess
}

main