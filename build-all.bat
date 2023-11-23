@echo off

REM Get the directory of the batch file
set "SCRIPTPATH=%~dp0"

REM Function to kill the Java process
:killProcess
echo Working path: %SCRIPTPATH%
taskkill /F /IM java.exe
rd /s /q api\*\libs

REM Function to run vbee
:runVbee
cd api\vbee
..\..\gradlew clean
..\..\gradlew build
cd %SCRIPTPATH%
start java -cp "libs/*;api\vbee\libs/*;api\vbee\build\libs\vbee-1.0.jar" com.vinplay.vbee.main.VBeeMain

REM Function to run VinPlayBackend
:runBackend
cd api\VinPlayBackend
..\..\gradlew clean
..\..\gradlew build
cd %SCRIPTPATH%
start java -cp "libs/*;api\VinPlayBackend\libs/*;api\VinPlayBackend\build\libs\VinPlayBackend-1.0.jar" com.vinplay.api.backend.server.VinPlayBackendMain

REM Function to run VinPlayPortal
:runPortal
cd api\VinPlayPortal
..\..\gradlew clean
..\..\gradlew build
cd %SCRIPTPATH%
start java -cp "libs/*;api\VinPlayPortal\libs/*;api\VinPlayPortal\build\libs\VinPlayPortal.jar" com.vinplay.api.server.JettyServer

REM Function to run Wspay
:runWspay
cd api\wspay
..\..\gradlew clean
..\..\gradlew build
cd %SCRIPTPATH%
start java -cp "libs/*;api\wspay\libs/*;api\wspay\build\libs\wspay.jar" com.vinplay.pay.server.JettyServer

REM Function to run WsReport
:runWsReport
cd api\wsreport
..\..\gradlew clean
..\..\gradlew build
cd %SCRIPTPATH%
start java -cp "libs/*;api\wsreport\libs/*;api\wsreport\build\libs\wsreport-1.0-SNAPSHOT.jar" game.GameApplication

REM Function to run MiniGame
:runMiniGame
cd game\Minigame
..\..\gradlew clean
..\..\gradlew build
cd %SCRIPTPATH%
start java -cp "libs/*;game\Minigame\libs/*;game\Minigame\build\libs\Minigame.jar" game.MiniGameMain

REM Function to run Slot
:runSlot
cd game\slot
..\..\gradlew clean
..\..\gradlew build
cd %SCRIPTPATH%
start java -cp "libs/*;game\slot\libs/*;game\slot\build\libs\SlotMachine.jar" game.SlotMain

REM Function to run Bacay
:runBacay
cd game\bacayServer
..\..\gradlew clean
..\..\gradlew build
cd %SCRIPTPATH%
start java -cp "libs/*;game\bacayServer\libs/*;game\bacayServer\build\libs\bacayServer.jar" game.bacay.server.BacayMain

REM Function to run Binh
:runBinh
cd game\binh
..\..\gradlew clean
..\..\gradlew build
cd %SCRIPTPATH%
start java -cp "libs/*;game\binh\libs/*;game\binh\build\libs\binh.jar" game.binh.server.BinhMain

REM Function to run TienLen
:runTienLen
cd game\tlmn
..\..\gradlew clean
..\..\gradlew build
cd %SCRIPTPATH%
start java -cp "libs/*;game\tlmn\libs/*;game\tlmn\build\libs\tlmn.jar" game.tienlen.server.TlmnMain

REM Function to run Poker
:runPoker
cd game\poker
..\..\gradlew clean
..\..\gradlew build
cd %SCRIPTPATH%
start java -cp "libs/*;game\poker\libs/*;game\poker\build\libs\poker.jar" game.poker.server.PokerMain

REM Function to run BauCuaTo2
:runBauCuaTo2
cd game\baucuato2
..\..\gradlew clean
..\..\gradlew build
cd %SCRIPTPATH%
start java -cp "libs/*;game\baucuato2\libs/*;game\baucuato2\build\libs\baucuato2.jar" game.BauCuaTo2Main

REM Function to run TaiXiuMini
:runTaiXiuMini
cd game\taixiuMini
..\..\gradlew clean
..\..\gradlew build
cd %SCRIPTPATH%
start java -cp "libs/*;game\taixiuMini\libs/*;game\taixiuMini\build\libs\taixiuMini.jar" game.TaiXiuMiniGameMain

REM Function to run XiDach
:runXiDach
cd game\xizach
..\..\gradlew clean
..\..\gradlew build
cd %SCRIPTPATH%
start java -cp "libs/*;game\xizach\libs/*;game\xizach\build\libs\xizach.jar" game.xizach.server.XiZachMain

REM Function to run BaiCao
:runBaiCao
cd game\baicao
..\..\gradlew clean
..\..\gradlew build
cd %SCRIPTPATH%
start java -cp "libs/*;game\baicao\libs/*;game\baicao\build\libs\baicao.jar" game.baicao.server.BaiCaoMain

REM Function to run XocDia
:runXocDia
cd game\xocdia
..\..\gradlew clean
..\..\gradlew build
cd %SCRIPTPATH%
start java -cp "libs/*;game\xocdia\libs/*;game\xocdia\build\libs\xocdia.jar" game.xocdia.server.XocDiaMain

REM Function to run Sam
:runSam
cd game\sam
..\..\gradlew clean
..\..\gradlew build
cd %SCRIPTPATH%
start java -cp "libs/*;game\sam\libs/*;game\sam\build\libs\sam.jar" game.sam.server.SamMain

REM Function to run all APIs
:runAllApi
call :runVbee
call :runPortal
call :runWspay
call :runBackend

REM Main function
:main
call :killProcess
call :runAllApi
call :runWsReport
call :runMiniGame
call :runTaiXiuMini
call :runSlot
call :runTienLen
call :runXiDach
call :runBacay
call :runXocDia
call :runBaiCao
call :runSam
call :runBinh
call :runPoker
call :runBauCuaTo2
call :vbee

REM Exit the script
exit /b
