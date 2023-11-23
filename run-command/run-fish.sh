#!/bin/bash

# Absolute path to this script, e.g. /home/user/bin/foo.sh
SCRIPT=$(readlink -f "$0")
# Absolute path this script is in, thus /home/user/bin
SCRIPTPATH=$(dirname "$SCRIPT")

# kill java process
killProcess() {
    echo "Working path: " . $SCRIPTPATH;
    kill -9 $(ps aux | grep "BancaMain" | grep -v 'grep' | awk '{print $2}')
    rm -rf ../api/slot/libs/*
   
}

runBackend() {
   currentDir="../game/banca"
    cd $currentDir
    ../../gradlew clean
    ../../gradlew build
   cd ../..
    #nohup java -cp "java-libs/*:game/slot/libs/*:game/slot/build/libs/slot-1.0.jar" game.SlotMain >/dev/null 2>&1 &
    nohup java -cp "java-libs/*:game/banca/libs/*:game/banca/build/libs/banca-1.0.jar" game.fishing.server.BancaMain >/dev/null 2>&1 &
}
run(){
    killProcess
    runBackend
}
run
