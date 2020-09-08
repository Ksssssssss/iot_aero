@echo off

::记录当前目录，无需修改
set CURRENT_PATH="%~dp0"
 
::指定程序工作路径
set SERVICE_DIR="%~dp0"

::指定jar包
set JARNAME="%~dp0aero-ops-1.0-SNAPSHOT.jar"
set "SHORTJAR=aero-ops-1.0-SNAPSHOT.jar"
 
 
 
::流程控制
if "%1"=="start" (
  call:START
) else (
  if "%1"=="stop" ( 
    call:STOP 
  ) else ( 
    if "%1"=="restart" (
	  call:RESTART 
	) else ( 
	  call:DEFAULT 
	)
  )
)
goto:eof
 
 
::启动jar包
:START
echo function "start" starting...
cd /d %SERVICE_DIR%

::项目已经打包好，无须再编译
::call %MAVEN_HOME_CUSTOM%\bin\mvn clean install

echo %JARNAME%
cmd /c start javaw.exe -Dfile.encoding=UTF-8 -Xms512m -Xmx1024m -jar %JARNAME%

::日志输出到指定日志文件，暂时不用输出到startup.log
::> %SERVICE_DIR%\%LOG_FILE%

cd /d %CURRENT_PATH%
echo == service start success
goto:eof
 
 
::停止java程序运行
:STOP
echo function "stop" starting...
call:shutdown
echo == service stop success
goto:eof
 
 
::重启jar包
:RESTART
echo function "restart" starting...
call:STOP
call:sleep2
call:START
echo == service restart success
exit
 
 
::执行默认方法--重启jar包
:DEFAULT
echo Now choose default item : restart
call:RESTART
exit


::终止之前启动的程序 wmic
:shutdown
WMIC PROCESS WHERE "name like 'java%%' and CommandLine like '%%%SHORTJAR%%%'" CALL Terminate
goto:eof
 
 
::延时5秒
:sleep5
TIMEOUT /t 5 /nobreak
goto:eof


::延时2秒
:sleep2
TIMEOUT /t 2 /nobreak
goto:eof

