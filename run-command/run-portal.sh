#!/bin/bash

# Absolute path to this script, e.g. /home/user/bin/foo.sh
SCRIPT=$(readlink -f "$0")
# Absolute path this script is in, thus /home/user/bin
SCRIPTPATH=$(pwd)

# kill java process
killProcess() {
    echo "Working path: " . $SCRIPTPATH;
    kill -9 $(ps aux | grep "VinPlayPortal" | grep -v 'grep' | awk '{print $2}')
    rm -rf ../api/VinPlayPortal/libs/*

}

runBackend() {
    currentDir="../api/VinPlayPortal"
    cd $currentDir
    ../../gradlew clean
    ../../gradlew build
    cd ../..
    nohup java -cp "libs/*:api/VinPlayPortal/libs/*:api/VinPlayPortal/build/libs/VinPlayPortal.jar" com.vinplay.api.server.JettyServer >/dev/null 2>&1 &
    #java -cp "libs/*:api/VinPlayPortal/libs/*:api/VinPlayPortal/build/libs/VinPlayPortal.jar" com.vinplay.api.server.JettyServer
}
run(){
    killProcess
    runBackend
}
run
