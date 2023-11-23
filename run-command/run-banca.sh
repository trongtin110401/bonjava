#!/bin/bash

# Absolute path to this script, e.g. /home/user/bin/foo.sh
SCRIPT=$(readlink -f "$0")
# Absolute path this script is in, thus /home/user/bin
SCRIPTPATH=$(dirname "$SCRIPT")

# kill java process


runBackend() {
  currentDir="/var/app/banca/BanCaLiteNet/bin/Release/netcoreapp3.0"
  cd $currentDir
  nohup dotnet BanCaLiteNet.dll >/dev/null 2>&1 &
}
run(){
    runBackend
}
run
