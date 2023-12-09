#!/bin/bash

# Absolute path to this script, e.g. /home/user/bin/foo.sh
SCRIPT=$(readlink -f "$0")
# Absolute path this script is in, thus /home/user/bin
SCRIPTPATH=$(pwd)

# kill java process
killProcess() {
    echo "Working path: " . $SCRIPTPATH;
    kill -9 $(ps aux | grep "java -cp" | grep -v 'grep' | awk '{print $2}')
    rm -rf */*/libs
}

# run vbee
runVbee() {
    currentDir="./api/vbee"
    cd $currentDir
        ../../gradlew clean
        ../../gradlew build

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${currentDir} -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/vbee-1.0.jar" com.vinplay.vbee.main.VBeeMain > trace/vbee.log 2>&1 &
}
runBackend() {
    currentDir="./api/VinPlayBackend"
    cd $currentDir
        ../../gradlew clean
        ../../gradlew build

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${currentDir} -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/VinPlayBackend-1.0.jar" com.vinplay.api.backend.server.VinPlayBackendMain >trace/api_backend.log 2>&1 &

}
runPortal(){
  currentDir="./api/VinPlayPortal"
  cd $currentDir
      ../../gradlew clean
      ../../gradlew build

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${currentDir} -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/VinPlayPortal.jar" com.vinplay.api.server.JettyServer > trace/api_portal.log 2>&1 &
}
runWspay(){
    currentDir="./api/wspay"

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${currentDir}  -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/wspay.jar" com.vinplay.pay.server.JettyServer > trace/wspay.log 2>&1 &

}

runWsReport(){
    currentDir="./api/wsreport"
    cd $currentDir
        ../../gradlew clean
        ../../gradlew build

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${currentDir} -jar api/wsreport/build/libs/wsreport-1.0-SNAPSHOT.jar > trace/wsreport.log 2>&1 &
}
##region run game###

runMiniGame(){
    currentDir="./game/Minigame"
    cd $currentDir
        ../../gradlew clean
        ../../gradlew build

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${currentDir}  -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/Minigame.jar" game.MiniGameMain > trace/minigame.log 2>&1 &

}


runSlot(){
    currentDir="./game/slot"
    cd $currentDir
        ../../gradlew clean
        ../../gradlew build

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${currentDir} -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/SlotMachine.jar" game.SlotMain > trace/slot.log 2>&1 &


}
### run bacay ###
runBacay(){
    currentDir="./game/bacayServer"
    cd $currentDir
        ../../gradlew clean
        ../../gradlew build

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${currentDir} -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/bacayServer.jar" game.bacay.server.BacayMain > trace/bacay.log 2>&1 &
}

### run binh ###
runBinh(){
    currentDir="./game/binh"
    cd $currentDir
        ../../gradlew clean
        ../../gradlew build

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${currentDir}  -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/binh.jar" game.binh.server.BinhMain > trace/binh.log 2>&1 &
}

### run tlmn ###
runTienLen(){
    currentDir="./game/tlmn"
    cd $currentDir
        ../../gradlew clean
        ../../gradlew build

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${currentDir} -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/tlmn.jar" game.tienlen.server.TlmnMain > trace/tlmn.log 2>&1 &
}

# run Poker
runPoker() {
    currentDir="./game/poker"
    cd $currentDir
        ../../gradlew clean
        ../../gradlew build

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${currentDir}  -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/poker.jar" game.poker.server.PokerMain > trace/poker.log 2>&1 &
}

# run baucuato2
runBauCuaTo2() {
    currentDir="./game/baucuato2"
    cd $currentDir
        ../../gradlew clean
        ../../gradlew build

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${currentDir} -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/baucuato2.jar" game.BauCuaTo2Main > trace/baucuato2.log 2>&1 &
}

runTaiXiuMini() {
    currentDir="./game/taixiuMini"
    cd $currentDir
        ../../gradlew clean
        ../../gradlew build

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${currentDir} -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/taixiuMini.jar" game.TaiXiuMiniGameMain > trace/taixiu.log 2>&1 &
}

### run Xi Dach ###
runXiDach(){
    currentDir="./game/xizach"
    cd $currentDir
        ../../gradlew clean
        ../../gradlew build

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${currentDir} -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/xizach.jar"  game.xizach.server.XiZachMain > trace/xizach.log 2>&1 &


}

### run bai cai ###
runBaiCao(){
    currentDir="./game/baicao"
    cd $currentDir
    ../../gradlew clean
    ../../gradlew build

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${currentDir}  -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/baicao.jar" game.baicao.server.BaiCaoMain > trace/baicao.log 2>&1 &
}

### run xoc dia ###
runXocDia(){
  echo "Start building Xoc Dia..."
    currentDir="./game/xocdia"
    cd $currentDir
        ../../gradlew clean
        ../../gradlew build

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${currentDir} -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/xocdia.jar"  game.xocdia.server.XocDiaMain > trace/xocdia.log 2>&1 &
   echo "Finish run Xoc Dia..."
}

### run Sam ###
runSam(){
  echo "Start building Sam..."
    currentDir="./game/sam"
    cd $currentDir
        ../../gradlew clean
        ../../gradlew build

    cd $SCRIPTPATH;
    nohup java -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/sam.jar" game.sam.server.SamMain > trace/sam.log 2>&1 &
   echo "Finish run Sam..."
}

runAllApi(){
  runVbee
  runPortal
  runWspay
  runBackend
  runWsReport
}

# clearSource(){
#   rm -rf api/**/src
#   rm -rf game/**/src
#   rm -rf **/src
# }
main(){
  killProcess

  runAllApi

  runMiniGame

  runTaiXiuMini
  runBauCuaTo2
  runXocDia
  runSlot

 # clearSource


  runTienLen
  runXiDach
  runBacay
  runBaiCao

  runSam
  runBinh
  runPoker
}

main


#runMiniGame
#killProcess
#rm -rf logs
#runAllApi
#runMiniGame
#runSlot
#####Region Run game###
#runBaiCao
#runSam



