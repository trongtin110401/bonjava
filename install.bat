@echo off

REM Get the absolute path of the script directory
set "SCRIPTPATH=%cd%"

REM Function to run vbee
:runVbee
start /B java -cp "libs\*;api\vbee\libs\*;api\vbee\build\libs\vbee-1.0.jar" com.vinplay.vbee.main.VBeeMain
@REM goto :eof

REM Function to run Backend
:runBackend
start /B java -cp "libs\*;api\VinPlayBackend\libs\*;api\VinPlayBackend\build\libs\VinPlayBackend-1.0.jar" com.vinplay.api.backend.server.VinPlayBackendMain
@REM goto :eof

REM Function to run Portal
:runPortal
start /B java -cp "libs\*;api\VinPlayPortal\libs\*;api\VinPlayPortal\build\libs\VinPlayPortal.jar" com.vinplay.api.server.JettyServer
@REM goto :eof

REM Function to run Wspay
:runWspay
start /B java -cp "libs\*;api\wspay\libs\*;api\wspay\build\libs\wspay.jar" com.vinplay.pay.server.JettyServer
@REM goto :eof

REM Function to run WsReport
:runWsReport
start /B java -cp "libs\*;api\wsreport\libs\*;api\wsreport\build\libs\wsreport-1.0-SNAPSHOT.jar" game.GameApplication
@REM goto :eof

REM Function to run MiniGame
:runMiniGame
start /B java -cp "libs\*;game\Minigame\libs\*;game\Minigame\build\libs\Minigame.jar" game.MiniGameMain
@REM goto :eof

REM Function to run Slot
:runSlot
start /B java -cp "libs\*;game\slot\libs\*;game\slot\build\libs\SlotMachine.jar" game.SlotMain
@REM goto :eof

REM Function to run Bacay
@REM :runBacay
@REM start /B java -cp "libs\*;game\bacayServer\libs\*;game\bacayServer\build\libs\bacayServer.jar" game.bacay.server.BacayMain
@REM goto :eof

REM Function to run Binh
@REM :runBinh
@REM start /B java -cp "libs\*;game\binh\libs\*;game\binh\build\libs\binh.jar" game.binh.server.BinhMain
@REM goto :eof

REM Function to run TienLen
:runTienLen
start /B java -cp "libs\*;game\tlmn\libs\*;game\tlmn\build\libs\tlmn.jar" game.tienlen.server.TlmnMain
@REM goto :eof

REM Function to run Poker
:runPoker
cd game\poker
start /B java -cp "libs\*;game\poker\libs\*;game\poker\build\libs\poker.jar" game.poker.server.PokerMain
@REM goto :eof

REM Function to run BauCuaTo2
:runBauCuaTo2
cd game\baucuato2
start /B java -cp "libs\*;game\baucuato2\libs\*;game\baucuato2\build\libs\baucuato2.jar" game.BauCuaTo2Main
@REM goto :eof

REM Function to run TaiXiuMini
:runTaiXiuMini
start /B java -cp "libs\*;game\taixiuMini\libs\*;game\taixiuMini\build\libs\taixiuMini.jar" game.TaiXiuMiniGameMain
@REM goto :eof

REM Function to run XiDach
@REM :runXiDach
@REM start /B java -cp "libs\*;game\xizach\libs\*;game\xizach\build\libs\xizach.jar" game.xizach.server.XiZachMain
@REM goto :eof

REM Function to run BaiCao
@REM :runBaiCao
@REM start /B java -cp "libs\*;game\baicao\libs\*;game\baicao\build\libs\baicao.jar" game.baicao.server.BaiCaoMain
@REM goto :eof

REM Function to run XocDia
:runXocDia
start /B java -cp "libs\*;game\xocdia\libs\*;game\xocdia\build\libs\xocdia.jar" game.xocdia.server.XocDiaMain
echo Finish run Xoc Dia...
@REM goto :eof

REM Function to run Sam
:runSam
start /B java -cp "libs\*;game\sam\libs\*;game\sam\build\libs\sam.jar" game.sam.server.SamMain
echo Finish run Sam...
goto :eof

REM Function to run all APIs
@REM :runAllApi
@REM call :runVbee
@REM call :runPortal
@REM call :runWspay
@REM call :runBackend
@REM goto :eof


REM Main function
@REM :main
@REM call :runAllApi
@REM call :runWsReport
@REM call :runMiniGame
@REM call :runTaiXiuMini
@REM call :runSlot
@REM call :runTienLen
@REM call :runXocDia
@REM call :runSam
@REM call :runPoker
@REM call :runBauCuaTo2
@REM goto :eof

REM Explicitly call the main function
@REM call :main
@REM EXIT /B 0