#!/bin/bash

# Absolute path to this script, e.g. /home/user/bin/foo.sh
SCRIPT=$(readlink -f "$0")
# Absolute path this script is in, thus /home/user/bin
SCRIPTPATH=$(dirname "$SCRIPT")

# kill java process
killProcess() {
    echo "Working path: " . $SCRIPTPATH;
    kill -9 $(ps aux | grep "com.vinplay.pay.server.JettyServer" | grep -v 'grep' | awk '{print $2}')
    rm -rf ../api/VinPlayPortal/libs/*

}

runBackend() {
    currentDir="../api/wspay"
    cd $currentDir
    ../../gradlew clean
    ../../gradlew build
    cd ../..
    nohup java -cp "libs/*:api/wspay/libs/*:api/wspay/build/libs/wspay.jar" com.vinplay.pay.server.JettyServer >/dev/null 2>&1 &
    #java -cp "java-libs/*:api/VinPlayPortal/libs/*:api/VinPlayPortal/build/libs/VinPlayPortal-1.0.jar" com.vinplay.api.server.VinPlayPortal

}
run(){
    killProcess
    runBackend
}
run
