:: made by Landon

:: set jar path

echo setting JAR PATH...
set "JAR_PATH=%~dp0out\artifacts\KodeKlashProject_jar\KodeKlashProject.jar"
echo JAR PATH set

:: check java installation

java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo install java to run this game
    pause
    exit /b
)

:: run game

echo game audio may not work correctly
timeout /t 2

start "" java -jar "%JAR_PATH%"
exit
