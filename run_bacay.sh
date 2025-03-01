#!/bin/bash

# pull new source, build and run
#git restore .
#git clean -df
#git pull
#chmod -R 700 *
#./gradlew build

# setting for elasticsearch in case of running in docker
sysctl -w vm.max_map_count=262144

# global environment
export TZ="Asia/Ho_Chi_Minh"

# make log server
mkdir -p /home/server/logs/
# Absolute path this script is in, thus /home/user/bin
SCRIPT_PATH=$(pwd)

# kill java process
killProcess() {
  echo "Working path: " . $SCRIPT_PATH
  kill -9 $(ps aux | grep "bacayServer.jar" | grep -v 'grep' | awk '{print $2}')
}

runBacay() {
  cd ${SCRIPT_PATH}
  currentDir="game/bacayServer"
  cd $currentDir
  echo "Starting BayCay..."
  nohup java -cp "libs/*:build/libs/bacayServer.jar" game.bacay.server.BacayMain >/home/server/logs/bacay.log 2>&1 &
}

main() {
  killProcess
  runBacay
}

main