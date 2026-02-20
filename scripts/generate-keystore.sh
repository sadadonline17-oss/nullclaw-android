#!/bin/bash
# Generate a release keystore for signing Android APK

KEYSTORE_FILE="release.keystore"
KEY_ALIAS="nullclaw"
VALIDITY=10000

echo "Generating release keystore for NullClaw Android..."
echo ""

# Check if keytool is available
if ! command -v keytool &> /dev/null; then
    echo "Error: keytool not found. Please install Java JDK."
    exit 1
fi

# Prompt for passwords
read -s -p "Enter keystore password: " STORE_PASS
echo ""
read -s -p "Confirm keystore password: " STORE_PASS_CONFIRM
echo ""

if [ "$STORE_PASS" != "$STORE_PASS_CONFIRM" ]; then
    echo "Error: Passwords do not match."
    exit 1
fi

read -s -p "Enter key password (or press Enter to use keystore password): " KEY_PASS
echo ""

if [ -z "$KEY_PASS" ]; then
    KEY_PASS="$STORE_PASS"
fi

# Generate keystore
keytool -genkeypair \
    -v \
    -keystore "$KEYSTORE_FILE" \
    -alias "$KEY_ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -validity $VALIDITY \
    -storepass "$STORE_PASS" \
    -keypass "$KEY_PASS" \
    -dname "CN=NullClaw Android, OU=Mobile, O=NullClaw, L=Internet, ST=Web, C=US"

if [ $? -eq 0 ]; then
    echo ""
    echo "✓ Keystore generated successfully: $KEYSTORE_FILE"
    echo ""
    echo "Add these secrets to your GitHub repository:"
    echo "  KEYSTORE_PASSWORD: <your keystore password>"
    echo "  KEY_ALIAS: $KEY_ALIAS"
    echo "  KEY_PASSWORD: <your key password>"
    echo ""
    echo "To encode keystore for GitHub secrets:"
    echo "  base64 -w 0 $KEYSTORE_FILE"
    echo ""
    echo "⚠️  Keep your keystore file and passwords secure!"
    echo "    - Never commit the keystore to version control"
    echo "    - Store backups in a secure location"
else
    echo "Error: Failed to generate keystore."
    exit 1
fi