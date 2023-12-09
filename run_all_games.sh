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
    working_directory="./api/vbee"

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${working_directory} -cp "libs/*:${working_directory}/libs/*:${working_directory}/build/libs/vbee-1.0.jar" com.vinplay.vbee.main.VBeeMain > trace/vbee.log 2>&1 &
}
runBackend() {
    working_directory="./api/VinPlayBackend"

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${working_directory} -cp "libs/*:${working_directory}/libs/*:${working_directory}/build/libs/VinPlayBackend-1.0.jar" com.vinplay.api.backend.server.VinPlayBackendMain >trace/api_backend.log 2>&1 &

}
runPortal(){
  working_directory="./api/VinPlayPortal"

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${working_directory} -cp "libs/*:${working_directory}/libs/*:${working_directory}/build/libs/VinPlayPortal.jar" com.vinplay.api.server.JettyServer > trace/api_portal.log 2>&1 &
}
runWspay(){
    working_directory="./api/wspay"

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${working_directory}  -cp "libs/*:${working_directory}/libs/*:${working_directory}/build/libs/wspay.jar" com.vinplay.pay.server.JettyServer > trace/wspay.log 2>&1 &

}

runWsReport(){
    working_directory="./api/wsreport"

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${working_directory} -jar api/wsreport/build/libs/wsreport-1.0-SNAPSHOT.jar > trace/wsreport.log 2>&1 &
}
##region run game###

runMiniGame(){
    working_directory="./game/Minigame"

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${working_directory}  -cp "libs/*:${working_directory}/libs/*:${working_directory}/build/libs/Minigame.jar" game.MiniGameMain > trace/minigame.log 2>&1 &

}


runSlot(){
    working_directory="./game/slot"

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${working_directory} -cp "libs/*:${working_directory}/libs/*:${working_directory}/build/libs/SlotMachine.jar" game.SlotMain > trace/slot.log 2>&1 &


}
### run bacay ###
runBacay(){
    working_directory="./game/bacayServer"

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${working_directory} -cp "libs/*:${working_directory}/libs/*:${working_directory}/build/libs/bacayServer.jar" game.bacay.server.BacayMain > trace/bacay.log 2>&1 &
}

### run binh ###
runBinh(){
    working_directory="./game/binh"

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${working_directory}  -cp "libs/*:${working_directory}/libs/*:${working_directory}/build/libs/binh.jar" game.binh.server.BinhMain > trace/binh.log 2>&1 &
}

### run tlmn ###
runTienLen(){
    working_directory="./game/tlmn"

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${working_directory} -cp "libs/*:${working_directory}/libs/*:${working_directory}/build/libs/tlmn.jar" game.tienlen.server.TlmnMain > trace/tlmn.log 2>&1 &
}

# run Poker
runPoker() {
    working_directory="./game/poker"

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${working_directory}  -cp "libs/*:${working_directory}/libs/*:${working_directory}/build/libs/poker.jar" game.poker.server.PokerMain > trace/poker.log 2>&1 &
}

# run baucuato2
runBauCuaTo2() {
    working_directory="./game/baucuato2"

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${working_directory} -cp "libs/*:${working_directory}/libs/*:${working_directory}/build/libs/baucuato2.jar" game.BauCuaTo2Main > trace/baucuato2.log 2>&1 &
}

runTaiXiuMini() {
    working_directory="./game/taixiuMini"

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${working_directory} -cp "libs/*:${working_directory}/libs/*:${working_directory}/build/libs/taixiuMini.jar" game.TaiXiuMiniGameMain > trace/taixiu.log 2>&1 &
}

### run Xi Dach ###
runXiDach(){
    working_directory="./game/xizach"

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${working_directory} -cp "libs/*:${working_directory}/libs/*:${working_directory}/build/libs/xizach.jar"  game.xizach.server.XiZachMain > trace/xizach.log 2>&1 &


}

### run bai cai ###
runBaiCao(){
    working_directory="./game/baicao"
    cd $working_directory
#    ../../gradlew clean
#    ../../gradlew build
    cd $SCRIPTPATH;
    nohup java -Duser.dir=${working_directory}  -cp "libs/*:${working_directory}/libs/*:${working_directory}/build/libs/baicao.jar" game.baicao.server.BaiCaoMain > trace/baicao.log 2>&1 &
}

### run xoc dia ###
runXocDia(){
  echo "Start building Xoc Dia..."
    working_directory="./game/xocdia"

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${working_directory} -cp "libs/*:${working_directory}/libs/*:${working_directory}/build/libs/xocdia.jar"  game.xocdia.server.XocDiaMain > trace/xocdia.log 2>&1 &
   echo "Finish run Xoc Dia..."
}

### run Sam ###
runSam(){
  echo "Start building Sam..."
    working_directory="./game/sam"

    cd $SCRIPTPATH;
    nohup java -Duser.dir=${working_directory} -cp "libs/*:${working_directory}/libs/*:${working_directory}/build/libs/sam.jar" game.sam.server.SamMain > trace/sam.log 2>&1 &
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



