@echo off
echo ========================================
echo   SERVEUR ASMAA-CLUB
echo ========================================
echo.

REM Vérifier que MySQL est démarré
echo Verification de la connexion MySQL...
echo.

REM Démarrer le serveur
echo Demarrage du serveur sur le port 5000...
echo.

java -cp "out/production/clubasmaa;lib/mysql-connector-java-8.0.33.jar" asmaa.server.ServerMain

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERREUR: Impossible de demarrer le serveur
    echo.
    echo Verifications:
    echo - MySQL est-il demarre?
    echo - La base de donnees asmaa_club existe-t-elle?
    echo - Le driver MySQL est-il dans lib/?
    echo - Les credentials dans DatabaseManager.java sont-ils corrects?
    pause
)

