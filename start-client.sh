#!/bin/bash

echo "========================================"
echo "  CLIENT ASMAA-CLUB"
echo "========================================"
echo ""

# Configuration JavaFX - MODIFIEZ LE CHEMIN CI-DESSOUS
JAVAFX_PATH="/chemin/vers/javafx-sdk-XX/lib"

# Vérifier que JavaFX existe
if [ ! -d "$JAVAFX_PATH" ]; then
    echo "ERREUR: JavaFX non trouvé à: $JAVAFX_PATH"
    echo ""
    echo "Veuillez:"
    echo "1. Télécharger JavaFX SDK depuis https://openjfx.io/"
    echo "2. Extraire le SDK"
    echo "3. Modifier JAVAFX_PATH dans ce script avec le bon chemin"
    echo ""
    read -p "Appuyez sur Entrée pour continuer..."
    exit 1
fi

# Vérifier que le serveur est démarré
echo "Vérification de la connexion au serveur..."
echo ""

# Démarrer le client
echo "Démarrage du client..."
echo ""

java --module-path "$JAVAFX_PATH" --add-modules javafx.controls,javafx.fxml -cp "out/production/clubasmaa:lib/mysql-connector-java-8.0.33.jar" asmaa.client.ClientMain

if [ $? -ne 0 ]; then
    echo ""
    echo "ERREUR: Impossible de démarrer le client"
    echo ""
    echo "Vérifications:"
    echo "- Le serveur est-il démarré?"
    echo "- JavaFX est-il correctement configuré?"
    echo "- Le chemin JAVAFX_PATH est-il correct?"
    read -p "Appuyez sur Entrée pour continuer..."
fi

