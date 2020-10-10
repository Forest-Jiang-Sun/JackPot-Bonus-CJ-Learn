@echo off
pushd ..
call BatchConfigs.bat
popd
echo Gamename: %GAMENAME%
SET BUILDFOLDERNAME=%GAMENAME%-%GAMEVERSION%-QA
SET ANT_PARAMS=-Dgame_version=%GAMEVERSION% -Dgame_name=%GAMENAME% -Dmath_name=%MATHNAME% -Dmath_version=%MATHVERSION% -DbuildFolder=%BUILDFOLDERNAME%

pushd ..
echo BAT -Cleaning Previous math
call ant %ANT_PARAMS% clean -f Games\%MATHNAME%\build.xml
echo BAT -Cleaning Previous Build
call ant %ANT_PARAMS% clean -f Games\%GAMENAME%\build.xml

echo BAT -Running Math Build
call ant %ANT_PARAMS% -f Games\%MATHNAME%\build.xml
echo Error Level: %ERRORLEVEL%
IF NOT %ERRORLEVEL% == 0 (
   echo ABORT: %ERRORLEVEL%
   pause
   call exit /b %ERRORLEVEL%
) ELSE (
   echo PROCEED: %ERRORLEVEL%
)

echo BAT -Before Main Ant Run
call ant %ANT_PARAMS% QA_Build -f Games\%GAMENAME%\build.xml
echo Error Level: %ERRORLEVEL%
IF NOT %ERRORLEVEL% == 0 (
   echo ABORT: %ERRORLEVEL%
   pause
   call exit /b %ERRORLEVEL%
) ELSE (
   echo PROCEED: %ERRORLEVEL%
)
echo BAT -After Main Ant Run

echo Copy files
call ant %ANT_PARAMS% copy_to_root -f Games\%GAMENAME%\build.xml
popd

del ..\Games\%GAMENAME%\build-*.txt
echo %GAMENAME% %BUILDFOLDERNAME%
echo %GAMENAME% %BUILDFOLDERNAME% > ..\Games\%GAMENAME%\build-%BUILDFOLDERNAME%.txt

if not "%AUTOBUILD%" == "1" pause
