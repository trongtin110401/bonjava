#!/bin/bash

# pull new source, build and run
#git restore .
#git clean -df
#git pull
#chmod -R 700 *
#./gradlew build

# setting for elasticsearch in case of running in docker
sysctl -w vm.max_map_count=262144

# global environment
export TZ="Asia/Ho_Chi_Minh"

# make log server
mkdir -p /home/server/logs/
# Absolute path this script is in, thus /home/user/bin
SCRIPT_PATH=$(pwd)

# kill java process
killProcess() {
  echo "Working path: " . $SCRIPT_PATH
  kill -9 $(ps aux | grep "bacayServer.jar" | grep -v 'grep' | awk '{print $2}')
  kill -9 $(ps aux | grep "baicao.jar" | grep -v 'grep' | awk '{print $2}')
  kill -9 $(ps aux | grep "sam.jar" | grep -v 'grep' | awk '{print $2}'
  kill -9 $(ps aux | grep "tlmn.jar" | grep -v 'grep' | awk '{print $2}')
  kill -9 $(ps aux | grep "poker.jar" | grep -v 'grep' | awk '{print $2}')
  kill -9 $(ps aux | grep "binh.jar" | grep -v 'grep' | awk '{print $2}')
}

runBoardService() {
  cd ${SCRIPT_PATH}
  currentDir="api/BoardService"
  cd $currentDir
  echo "Starting BoardService..."
  nohup java -jar build/libs/BoardService-1.0-SNAPSHOT.jar >/home/server/logs/boardService.log 2>&1 &
}

# run vbee
runVbee() {
  cd ${SCRIPT_PATH}
  currentDir="api/vbee"
  cd $currentDir
  echo "Starting VBEE..."
  nohup java -cp "libs/*:build/libs/vbee-1.0.jar" com.vinplay.vbee.main.VBeeMain >/home/server/logs/vbee.log 2>&1 &
}


runBackend() {
  cd ${SCRIPT_PATH}
  currentDir="api/VinPlayBackend"
  cd $currentDir
  echo "Starting API Backend..."
  nohup java -cp "libs/*:build/libs/VinPlayBackend-1.0.jar" com.vinplay.api.backend.server.VinPlayBackendMain >/home/server/logs/api_backend.log 2>&1 &

}

runPortal() {
  cd ${SCRIPT_PATH}
  currentDir="api/VinPlayPortal"
  cd $currentDir
  echo "Starting Game Portal..."
  nohup java -cp "libs/*:build/libs/VinPlayPortal.jar" com.vinplay.api.server.JettyServer >/home/server/logs/api_portal.log 2>&1 &
}

runWspay() {
  cd ${SCRIPT_PATH}
  currentDir="api/wspay"
  cd $currentDir
  echo "Starting WSPay..."
  nohup java -cp "libs/*:build/libs/wspay.jar" com.vinplay.pay.server.JettyServer >/home/server/logs/wspay.log 2>&1 &

}

runWsReport() {
  cd ${SCRIPT_PATH}
  currentDir="api/wsreport"
  cd $currentDir
  echo "Starting WSReport..."
  nohup java -jar build/libs/wsreport-1.0-SNAPSHOT.jar >/home/server/logs/wsreport.log 2>&1 &
}

## region run game ###

runMiniGame() {
  cd ${SCRIPT_PATH}
  currentDir="game/Minigame"
  cd $currentDir
  echo "Starting Mini Game..."
  nohup java -cp "libs/*:build/libs/Minigame.jar" game.MiniGameMain >/home/server/logs/minigame.log 2>&1 &
}

runSlot() {
  cd ${SCRIPT_PATH}
  currentDir="game/slot"
  cd $currentDir
  echo "Starting Slot Machine..."
  nohup java -cp "libs/*:build/libs/SlotMachine.jar" game.SlotMain >/home/server/logs/slot.log 2>&1 &

}

### run bacay ###
runBacay() {
  cd ${SCRIPT_PATH}
  currentDir="game/bacayServer"
  cd $currentDir
  echo "Starting BayCay..."
  nohup java -cp "libs/*:build/libs/bacayServer.jar" game.bacay.server.BacayMain >/home/server/logs/bacay.log 2>&1 &
}

### run baicao ###
runBaiCao() {
  cd ${SCRIPT_PATH}
  currentDir="game/baicao"
  cd $currentDir
  echo "Starting BaiCao..."
  nohup java -cp "libs/*:build/libs/baicao.jar" game.baicao.server.BaiCaoMain >/home/server/logs/baicao.log 2>&1 &
}

### run binh ###
runBinh() {
  cd ${SCRIPT_PATH}
  currentDir="game/binh"
  cd $currentDir
  echo "Starting Binh..."
  nohup java -cp "libs/*:build/libs/binh.jar" game.binh.server.BinhMain >/home/server/logs/binh.log 2>&1 &
}

### run tlmn ###
runTienLen() {
  cd ${SCRIPT_PATH}
  currentDir="game/tlmn"
  cd $currentDir
  echo "Starting Tien Len Mien Nam..."
  nohup java -cp "libs/*:build/libs/tlmn.jar" game.tienlen.server.TlmnMain >/home/server/logs/tlmn.log 2>&1 &
}

# run Poker
runPoker() {
  cd ${SCRIPT_PATH}
  currentDir="game/poker"
  cd $currentDir
  echo "Starting Porker..."
  nohup java -cp "libs/*:build/libs/poker.jar" game.poker.server.PokerMain >/home/server/logs/poker.log 2>&1 &
}

# run baucuato2
runBauCuaTo2() {
  cd ${SCRIPT_PATH}
  currentDir="game/baucuato2"
  cd $currentDir
  echo "Starting Bau Cua..."
  nohup java -cp "libs/*:build/libs/baucuato2.jar" game.BauCuaTo2Main >/home/server/logs/baucuato2.log 2>&1 &
}

runTaiXiuMini() {
  cd ${SCRIPT_PATH}
  currentDir="game/taixiuMini"
  cd $currentDir
  echo "Starting TaiXiu..."
  nohup java -cp "libs/*:build/libs/taixiuMini.jar" game.TaiXiuMiniGameMain >/home/server/logs/taixiu.log 2>&1 &
}

runTaiXiuMd5() {
  cd ${SCRIPT_PATH}
  currentDir="game/taixiuMd5"
  cd $currentDir
  echo "Starting TaiXiu MD5..."
  nohup java -cp "libs/*:build/libs/taixiuMd5.jar" game.TaiXiuMiniGameMain >/home/server/logs/taixiuMd5.log 2>&1 &
}


### run xoc dia ###
runXocDia() {
  cd ${SCRIPT_PATH}
  currentDir="game/xocdia"
  cd $currentDir

  echo "Starting Xoc Dia..."
  nohup java -cp "libs/*:build/libs/xocdia.jar" game.xocdia.server.XocDiaMain >/home/server/logs/xocdia.log 2>&1 &
}

### run Sam ###
runSam() {
  cd ${SCRIPT_PATH}
  currentDir="game/sam"
  cd $currentDir

  echo "Starting sam..."
  nohup java -cp "libs/*:build/libs/sam.jar" game.sam.server.SamMain >/home/server/logs/sam.log 2>&1 &
}

runAllApi() {
  runPortal
  runVbee
  runWspay
  runWsReport
  runBackend
  runBoardService
}

main() {
  killProcess

  runTienLen
  runBacay
  runBaiCao
  runSam
  runBinh
  runPoker
}

main