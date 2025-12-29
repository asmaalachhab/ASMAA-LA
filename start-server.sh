#!/bin/bash

echo "========================================"
echo "  SERVEUR ASMAA-CLUB"
echo "========================================"
echo ""

# Vérifier que MySQL est démarré
echo "Vérification de la connexion MySQL..."
echo ""

# Démarrer le serveur
echo "Démarrage du serveur sur le port 5000..."
echo ""

java -cp "out/production/clubasmaa:lib/mysql-connector-java-8.0.33.jar" asmaa.server.ServerMain

if [ $? -ne 0 ]; then
    echo ""
    echo "ERREUR: Impossible de démarrer le serveur"
    echo ""
    echo "Vérifications:"
    echo "- MySQL est-il démarré?"
    echo "- La base de données asmaa_club existe-t-elle?"
    echo "- Le driver MySQL est-il dans lib/?"
    echo "- Les credentials dans DatabaseManager.java sont-ils corrects?"
    read -p "Appuyez sur Entrée pour continuer..."
fi

