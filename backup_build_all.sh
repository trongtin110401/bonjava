#!/bin/bash

# Absolute path to this script, e.g. /home/user/bin/foo.sh
SCRIPT=$(readlink -f "$0")
# Absolute path this script is in, thus /home/user/bin

# run vbee
runVbee() {
    currentDir="api/vbee"
    cd $currentDir
    ../../gradlew clean
    ../../gradlew build

}

main(){
  runVbee
}
main



