#!/bin/bash

# pull new source, build and run
git restore .
git clean -df
git pull
chmod -R 700 *
./gradlew build

# setting for elasticsearch in case of running in docker
sysctl -w vm.max_map_count=262144

# global environment
export ELASTICSEARCH_URL="http://localhost:9200"
export TZ="Asia/Ho_Chi_Minh"

# make log server
mkdir -p /home/server/logs/
# Absolute path this script is in, thus /home/user/bin
SCRIPT_PATH=$(pwd)

# kill java process
killProcess() {
  echo "Working path: " . $SCRIPT_PATH
  kill -9 $(ps aux | grep "VinPlayPortal.jar" | grep -v 'grep' | awk '{print $2}')
}

runPortal() {
  cd ${SCRIPT_PATH}
  currentDir="api/VinPlayPortal"
  cd $currentDir
  echo "Starting Game Portal..."
  nohup java -cp "libs/*:build/libs/VinPlayPortal.jar" com.vinplay.api.server.JettyServer >/home/server/logs/api_portal.log 2>&1 &
}

main() {
  killProcess
  runPortal
}

main