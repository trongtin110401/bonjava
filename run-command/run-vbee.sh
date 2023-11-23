#!/bin/bash

# Absolute path to this script, e.g. /home/user/bin/foo.sh
SCRIPT=$(readlink -f "$0")
# Absolute path this script is in, thus /home/user/bin
SCRIPTPATH=$(dirname "$SCRIPT")

# kill java process
killProcess() {
    echo "Working path: " . $SCRIPTPATH;
    kill -9 $(ps aux | grep "VBeeMain" | grep -v 'grep' | awk '{print $2}')
    rm -rf ../api/vbee/libs/*
   
}

runVbee() {
    currentDir="../api/vbee"
    cd $currentDir
    ../../gradlew clean
    ../../gradlew build
    cd ../..
    nohup java -cp "java-libs/*:api/vbee/libs/*:api/vbee/build/libs/vbee-1.0.jar" com.vinplay.vbee.main.VBeeMain >/dev/null 2>&1 &
}
run(){
    killProcess
    runVbee
}
run
