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
    currentDir="api/vbee"

    cd $SCRIPTPATH;
    nohup java -cp "libs/*:api/vbee/libs/*:api/vbee/build/libs/vbee-1.0.jar" -Duser.dir=${currentDir} com.vinplay.vbee.main.VBeeMain > trace/vbee.log 2>&1 &
}
runBackend() {
    currentDir="api/VinPlayBackend"

    cd $SCRIPTPATH;
    nohup java -cp "libs/*:api/VinPlayBackend/libs/*:api/VinPlayBackend/build/libs/VinPlayBackend-1.0.jar" -Duser.dir=${currentDir} com.vinplay.api.backend.server.VinPlayBackendMain >trace/api_backend.log 2>&1 &

}
runPortal(){
  currentDir="api/VinPlayPortal"

    cd $SCRIPTPATH;
    nohup java -cp "libs/*:api/VinPlayPortal/libs/*:api/VinPlayPortal/build/libs/VinPlayPortal.jar" -Duser.dir=${currentDir} com.vinplay.api.server.JettyServer > trace/api_portal.log 2>&1 &
}
runWspay(){
    currentDir="api/wspay"

    cd $SCRIPTPATH;
    nohup java -cp "libs/*:api/wspay/libs/*:api/wspay/build/libs/wspay.jar" -Duser.dir=${currentDir} com.vinplay.pay.server.JettyServer > trace/wspay.log 2>&1 &

}

runWsReport(){
    currentDir="api/wsreport"

    cd $SCRIPTPATH;
    nohup java -jar api/wsreport/build/libs/wsreport-1.0-SNAPSHOT.jar -Duser.dir=${currentDir} > trace/wsreport.log 2>&1 &
}
##region run game###

runMiniGame(){
    currentDir="game/Minigame"

    cd $SCRIPTPATH;
    nohup java -cp "libs/*:game/Minigame/libs/*:game/Minigame/build/libs/Minigame.jar" -Duser.dir=${currentDir} game.MiniGameMain > trace/minigame.log 2>&1 &

}


runSlot(){
    currentDir="game/slot"

    cd $SCRIPTPATH;
    nohup java -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/SlotMachine.jar" -Duser.dir=${currentDir} game.SlotMain > trace/slot.log 2>&1 &


}
### run bacay ###
runBacay(){
    currentDir="game/bacayServer"

    cd $SCRIPTPATH;
    nohup java -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/bacayServer.jar" -Duser.dir=${currentDir} game.bacay.server.BacayMain > trace/bacay.log 2>&1 &
}

### run binh ###
runBinh(){
    currentDir="game/binh"

    cd $SCRIPTPATH;
    nohup java -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/binh.jar" -Duser.dir=${currentDir} game.binh.server.BinhMain > trace/binh.log 2>&1 &
}

### run tlmn ###
runTienLen(){
    currentDir="game/tlmn"

    cd $SCRIPTPATH;
    nohup java -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/tlmn.jar" -Duser.dir=${currentDir} game.tienlen.server.TlmnMain > trace/tlmn.log 2>&1 &
}

# run Poker
runPoker() {
    currentDir="game/poker"

    cd $SCRIPTPATH;
    nohup java -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/poker.jar" -Duser.dir=${currentDir} game.poker.server.PokerMain > trace/poker.log 2>&1 &
}

# run baucuato2
runBauCuaTo2() {
    currentDir="game/baucuato2"

    cd $SCRIPTPATH;
    nohup java -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/baucuato2.jar" -Duser.dir=${currentDir} game.BauCuaTo2Main > trace/baucuato2.log 2>&1 &
}

runTaiXiuMini() {
    currentDir="game/taixiuMini"

    cd $SCRIPTPATH;
    nohup java -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/taixiuMini.jar" -Duser.dir=${currentDir} game.TaiXiuMiniGameMain > trace/taixiu.log 2>&1 &
}

### run Xi Dach ###
runXiDach(){
    currentDir="game/xizach"

    cd $SCRIPTPATH;
    nohup java -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/xizach.jar" -Duser.dir=${currentDir} game.xizach.server.XiZachMain > trace/xizach.log 2>&1 &


}

### run bai cai ###
#runBaiCao(){
#    currentDir="game/baicao"
#    cd $currentDir
#    ../../gradlew clean
#    ../../gradlew build
#    cd $SCRIPTPATH;
#    nohup java -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/baicao.jar" game.baicao.server.BaiCaoMain >/dev/null 2>&1 &
#   #java -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/baicao.jar" game.baicao.server.BaiCaoMain
#}

### run xoc dia ###
runXocDia(){
  echo "Start building Xoc Dia..."
    currentDir="game/xocdia"

    cd $SCRIPTPATH;
    nohup java -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/xocdia.jar" -Duser.dir=${currentDir} game.xocdia.server.XocDiaMain > trace/xocdia.log 2>&1 &
   echo "Finish run Xoc Dia..."
}

### run Sam ###
runSam(){
  echo "Start building Sam..."
    currentDir="game/sam"

    cd $SCRIPTPATH;
    nohup java -cp "libs/*:${currentDir}/libs/*:${currentDir}/build/libs/sam.jar" -Duser.dir=${currentDir} game.sam.server.SamMain > trace/sam.log 2>&1 &
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



