#!/bin/bash

# Absolute path to this script, e.g. /home/user/bin/foo.sh
SCRIPT=$(readlink -f "$0")
# Absolute path this script is in, thus /home/user/bin
SCRIPTPATH=$(dirname "$SCRIPT")

# kill java process
killProcess() {
    echo "Working path: " . $SCRIPTPATH;
    kill -9 $(ps aux | grep "VinPlayBackend" | grep -v 'grep' | awk '{print $2}')
    rm -rf ../api/VinPlayBackend/libs/*
   
}

runBackend() {
    currentDir="../api/VinPlayBackend"
    cd $currentDir
    ../../gradlew clean
    ../../gradlew build
    cd ../..
    nohup java -cp "libs/*:api/VinPlayBackend/libs/*:api/VinPlayBackend/build/libs/VinPlayBackend-1.0.jar" com.vinplay.api.backend.server.VinPlayBackendMain >/dev/null 2>&1 &
}
run(){
    killProcess
    runBackend
}
run
