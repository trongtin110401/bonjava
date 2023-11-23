#!/bin/bash

# Absolute path to this script, e.g. /home/user/bin/foo.sh
SCRIPT=$(readlink -f "$0")
# Absolute path this script is in, thus /home/user/bin
SCRIPTPATH=$(dirname "$SCRIPT")

# kill java process
killProcess() {
    echo "Working path: " . $SCRIPTPATH;
    kill -9 $(ps aux | grep "SlotMain" | grep -v 'grep' | awk '{print $2}')
    rm -rf ../game/slot/libs/*

}

runBackend() {
   currentDir="../game/slot"
    cd $currentDir
    ../../gradlew clean
    ../../gradlew build
   cd ../..
   # nohup java -cp "java-libs/*:game/slot/libs/*:game/slot/build/libs/slot-1.0.jar" game.SlotMain >/dev/null 2>&1 &
    java -cp "libs/*:game/slot/libs/*:game/slot/build/libs/SlotMachine.jar" game.SlotMain
}
run(){
    killProcess
    runBackend
}
run
~