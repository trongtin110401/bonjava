#!/bin/bash

# make log server
mkdir /home/server/logs/
# Absolute path to this script, e.g. /home/user/bin/foo.sh
SCRIPT=$(readlink -f "$0")
# Absolute path this script is in, thus /home/user/bin
SCRIPTPATH=$(pwd)

# kill java process
killProcess() {
    echo "Working path: " . $SCRIPTPATH;
    kill -9 $(ps aux | grep "java -cp" | grep -v 'grep' | awk '{print $2}')
#    rm -rf */*/libs
}

# run vbee
runVbee() {
    cd ${SCRIPTPATH}
    currentDir="api/vbee"
    cd $currentDir


#    cd $SCRIPTPATH;
    nohup java  -cp "libs/*:build/libs/vbee-1.0.jar" com.vinplay.vbee.main.VBeeMain > /home/server/logs/vbee.log 2>&1 &
}
runBackend() {
  cd ${SCRIPTPATH}
    currentDir="api/VinPlayBackend"
    cd $currentDir


#    cd $SCRIPTPATH;
    nohup java  -cp "libs/*:build/libs/VinPlayBackend-1.0.jar" com.vinplay.api.backend.server.VinPlayBackendMain >/home/server/logs/api_backend.log 2>&1 &

}
runPortal(){
  cd ${SCRIPTPATH}
  currentDir="api/VinPlayPortal"
  cd $currentDir

#    cd $SCRIPTPATH;
    nohup java  -cp "libs/*:build/libs/VinPlayPortal.jar" com.vinplay.api.server.JettyServer > /home/server/logs/api_portal.log 2>&1 &
}
runWspay(){
  cd ${SCRIPTPATH}
    currentDir="api/wspay"

#    cd $SCRIPTPATH;
    nohup java   -cp "libs/*:build/libs/wspay.jar" com.vinplay.pay.server.JettyServer > /home/server/logs/wspay.log 2>&1 &

}

runWsReport(){
  cd ${SCRIPTPATH}
    currentDir="api/wsreport"
    cd $currentDir


#    cd $SCRIPTPATH;
    nohup java  -jar api/wsreport/build/libs/wsreport-1.0-SNAPSHOT.jar > /home/server/logs/wsreport.log 2>&1 &
}
##region run game###

runMiniGame(){
  cd ${SCRIPTPATH}
    currentDir="game/Minigame"
    cd $currentDir


#    cd $SCRIPTPATH;
    nohup java   -cp "libs/*:build/libs/Minigame.jar" game.MiniGameMain > /home/server/logs/minigame.log 2>&1 &

}


runSlot(){
  cd ${SCRIPTPATH}
    currentDir="game/slot"
    cd $currentDir


#    cd $SCRIPTPATH;
    nohup java  -cp "libs/*:build/libs/SlotMachine.jar" game.SlotMain > /home/server/logs/slot.log 2>&1 &


}
### run bacay ###
runBacay(){
  cd ${SCRIPTPATH}
    currentDir="game/bacayServer"
    cd $currentDir


#    cd $SCRIPTPATH;
    nohup java  -cp "libs/*:build/libs/bacayServer.jar" game.bacay.server.BacayMain > /home/server/logs/bacay.log 2>&1 &
}

### run binh ###
runBinh(){
  cd ${SCRIPTPATH}
    currentDir="game/binh"
    cd $currentDir


#    cd $SCRIPTPATH;
    nohup java   -cp "libs/*:build/libs/binh.jar" game.binh.server.BinhMain > /home/server/logs/binh.log 2>&1 &
}

### run tlmn ###
runTienLen(){
  cd ${SCRIPTPATH}
    currentDir="game/tlmn"
    cd $currentDir


#    cd $SCRIPTPATH;
    nohup java  -cp "libs/*:build/libs/tlmn.jar" game.tienlen.server.TlmnMain > /home/server/logs/tlmn.log 2>&1 &
}

# run Poker
runPoker() {
  cd ${SCRIPTPATH}
    currentDir="game/poker"
    cd $currentDir


#    cd $SCRIPTPATH;
    nohup java   -cp "libs/*:build/libs/poker.jar" game.poker.server.PokerMain > /home/server/logs/poker.log 2>&1 &
}

# run baucuato2
runBauCuaTo2() {
  cd ${SCRIPTPATH}
    currentDir="game/baucuato2"
    cd $currentDir


#    cd $SCRIPTPATH;
    nohup java  -cp "libs/*:build/libs/baucuato2.jar" game.BauCuaTo2Main > /home/server/logs/baucuato2.log 2>&1 &
}

runTaiXiuMini() {
  cd ${SCRIPTPATH}
    currentDir="game/taixiuMini"
    cd $currentDir


#    cd $SCRIPTPATH;
    nohup java  -cp "libs/*:build/libs/taixiuMini.jar" game.TaiXiuMiniGameMain > /home/server/logs/taixiu.log 2>&1 &
}

### run Xi Dach ###
runXiDach(){
  cd ${SCRIPTPATH}
    currentDir="game/xizach"
    cd $currentDir


#    cd $SCRIPTPATH;
    nohup java  -cp "libs/*:build/libs/xizach.jar"  game.xizach.server.XiZachMain > /home/server/logs/xizach.log 2>&1 &


}

### run bai cai ###
runBaiCao(){
  cd ${SCRIPTPATH}
    currentDir="game/baicao"
    cd $currentDir

#    cd $SCRIPTPATH;
    nohup java   -cp "libs/*:build/libs/baicao.jar" game.baicao.server.BaiCaoMain > /home/server/logs/baicao.log 2>&1 &
}

### run xoc dia ###
runXocDia(){
  cd ${SCRIPTPATH}
  echo "Start building Xoc Dia..."
    currentDir="game/xocdia"
    cd $currentDir


#    cd $SCRIPTPATH;
    nohup java  -cp "libs/*:build/libs/xocdia.jar"  game.xocdia.server.XocDiaMain > /home/server/logs/xocdia.log 2>&1 &
   echo "Finish run Xoc Dia..."
}

### run Sam ###
runSam(){
  cd ${SCRIPTPATH}
  echo "Start building Sam..."
    currentDir="game/sam"
    cd $currentDir


#    cd $SCRIPTPATH;
    nohup java -cp "libs/*:build/libs/sam.jar" game.sam.server.SamMain > /home/server/logs/sam.log 2>&1 &
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



