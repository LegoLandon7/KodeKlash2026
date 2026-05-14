:: made by Landon

:: set jar path

set "JAR_PATH=%~dp0out\artifacts\KodeKlashProject_jar\KodeKlashProject.jar"

:: check java installation

java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo install java to run this game
    pause
    exit /b
)

:: run game

start "" java -jar "%JAR_PATH%"
exit
