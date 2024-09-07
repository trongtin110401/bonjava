#!/bin/bash
echo "Start time: " . `date +"%Y/%m/%d %H:%M:%S"`
DATE=`date +"%Y%m%d"`
SQLFILE=backup-${DATE}.sql
docker exec game-db /usr/bin/mysqldump -u demo --password=MgqzAtRymcxyNoFnkwX7sGUlmj0YQI --default-character-set=utf8 --single-transaction --routines --events --databases "game_email" "gamepay" "vinplay" "vinplay_admin" "vinplay_gamebai" "vinplay_minigame" > $SQLFILE
gzip $SQLFILE
mv backup-* /home/card-game/backup
echo "Done: " . `date +"%Y/%m/%d %H:%M:%S"`
