#!/bin/bash

# Absolute path to this script, e.g. /home/user/bin/foo.sh
SCRIPT=$(readlink -f "$0")
# Absolute path this script is in, thus /home/user/bin
SCRIPTPATH=$(dirname "$SCRIPT")

# kill java process
killProcess() {
    echo "Working path: " . $SCRIPTPATH;
    kill -9 $(ps aux | grep "Minigame.jar" | grep -v 'grep' | awk '{print $2}')
    rm -rf ../game/Minigame/libs/*
   
}

runMinigame() {
    currentDir="../game/Minigame"
    cd $currentDir
    ../../gradlew clean
    ../../gradlew build
    cd ../..
    #nohup java -cp "libs/*:game/Minigame/libs/*:game/Minigame/build/libs/Minigame-1.0.jar" game.MinigameMain >/dev/null 2>&1 &
    nohup java -cp "libs/*:game/Minigame/libs/*:game/Minigame/build/libs/Minigame.jar" game.MiniGameMain >/dev/null 2>&1 &

}
run(){
    killProcess
    runMinigame
}
run
