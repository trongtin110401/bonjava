#!/bin/bash

# path
SCRIPTPATH=/home/card-game/backend/logs

zipLogsProcess() {
  # parent
  DATE=`date +"%Y/%m/%d %H:%M:%S"`
  echo "Start at: $DATE";
  echo "Working path: " . $SCRIPTPATH;
  cd $SCRIPTPATH;
  gzip bitzero.log.20*
  gzip bitzerodebug.log.20*
  gzip rmq.log.20*
  mv bitzero.log.20* ziped_logs
  mv bitzerodebug.log.20* ziped_logs
  mv rmq.log.20* ziped_logs

  # api_backend
  echo "Working path: $SCRIPTPATH/api_backend";
  mkdir -p "$SCRIPTPATH/ziped_logs/api_backend"
  cd "$SCRIPTPATH/api_backend";
  gzip api_backend.log.20*
  gzip report.log.20*
  mv api_backend.log.20* "$SCRIPTPATH/ziped_logs/api_backend"
  mv report.log.20* "$SCRIPTPATH/ziped_logs/api_backend"

  # api_portal
  echo "Working path: $SCRIPTPATH/api_portal";
  mkdir -p "$SCRIPTPATH/ziped_logs/api_portal"
  cd "$SCRIPTPATH/api_portal";
  gzip api_portal.log.20*
  mv api_portal.log.20* "$SCRIPTPATH/ziped_logs/api_portal"

  # game
  echo "Working path: $SCRIPTPATH/game";
  mkdir -p "$SCRIPTPATH/ziped_logs/game"
  cd "$SCRIPTPATH/game";
  gzip csvGameVin.csv.20*
  mv csvGameVin.csv.20* "$SCRIPTPATH/ziped_logs/game"

   # money
  echo "Working path: $SCRIPTPATH/money";
  mkdir -p "$SCRIPTPATH/ziped_logs/money"
  cd "$SCRIPTPATH/money";
  gzip csvMoneyVin.csv.20*
  gzip csvMoneyXu.csv.20*
  mv csvMoneyVin.csv.20* "$SCRIPTPATH/ziped_logs/money"
  mv csvMoneyXu.csv.20* "$SCRIPTPATH/ziped_logs/money"

  # poke_go
  echo "Working path: $SCRIPTPATH/poke_go";
  mkdir -p "$SCRIPTPATH/ziped_logs/poke_go"
  cd "$SCRIPTPATH/poke_go";
  gzip csvPokeGo.csv.*
  mv csvPokeGo.csv.* "$SCRIPTPATH/ziped_logs/poke_go"

  # user_core
  echo "Working path: $SCRIPTPATH/user_core";
  mkdir -p "$SCRIPTPATH/ziped_logs/user_core"
  cd "$SCRIPTPATH/user_core";
  gzip user_core.log.20*
  mv user_core.log.20* "$SCRIPTPATH/ziped_logs/user_core"

  # vbee
  echo "Working path: $SCRIPTPATH/vbee";
  mkdir -p "$SCRIPTPATH/ziped_logs/vbee"
  cd "$SCRIPTPATH/vbee";
  gzip vbee.log.20*
  mv vbee.log.20* "$SCRIPTPATH/ziped_logs/vbee"

  # wspay
  echo "Working path: $SCRIPTPATH/wspay";
  mkdir -p "$SCRIPTPATH/ziped_logs/wspay"
  cd "$SCRIPTPATH/wspay";
  gzip wspay.log.20*
  mv wspay.log.20* "$SCRIPTPATH/ziped_logs/wspay"
}

main() {
  zipLogsProcess
}
main