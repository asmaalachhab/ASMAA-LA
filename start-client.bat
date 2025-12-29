@echo off
echo ========================================
echo   CLIENT ASMAA-CLUB
echo ========================================
echo.

REM Configuration JavaFX - MODIFIEZ LE CHEMIN CI-DESSOUS
set JAVAFX_PATH=C:\javafx-sdk-17.0.2\lib

REM Vérifier que JavaFX existe
if not exist "%JAVAFX_PATH%" (
    echo ERREUR: JavaFX non trouve a: %JAVAFX_PATH%
    echo.
    echo Veuillez:
    echo 1. Telecharger JavaFX SDK depuis https://openjfx.io/
    echo 2. Extraire le SDK
    echo 3. Modifier JAVAFX_PATH dans ce script avec le bon chemin
    echo.
    pause
    exit /b 1
)

REM Vérifier que le serveur est démarré
echo Verification de la connexion au serveur...
echo.

REM Démarrer le client
echo Demarrage du client...
echo.

java --module-path "%JAVAFX_PATH%" --add-modules javafx.controls,javafx.fxml -cp "out/production/clubasmaa;lib/mysql-connector-java-8.0.33.jar" asmaa.client.ClientMain

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERREUR: Impossible de demarrer le client
    echo.
    echo Verifications:
    echo - Le serveur est-il demarre?
    echo - JavaFX est-il correctement configure?
    echo - Le chemin JAVAFX_PATH est-il correct?
    pause
)

