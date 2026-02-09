#!/bin/bash

# MathMaster Development Starter
# This script starts the emulator and installs the app

export ANDROID_HOME=/opt/homebrew/share/android-commandlinetools
export PATH=$PATH:$ANDROID_HOME/emulator:$ANDROID_HOME/platform-tools

echo "🚀 Starting MathMaster development environment..."
echo ""

# Start emulator in background
echo "1️⃣  Starting emulator..."
$ANDROID_HOME/emulator/emulator -avd MathMaster_Emulator > /dev/null 2>&1 &
EMULATOR_PID=$!

echo "2️⃣  Waiting for emulator to boot (this may take 30-60 seconds)..."

# Wait for emulator to be ready
waited=0
max_wait=120
while [ $waited -lt $max_wait ]; do
    if $ANDROID_HOME/platform-tools/adb devices | grep -q "device$"; then
        echo "✅ Emulator is ready!"
        break
    fi
    sleep 2
    waited=$((waited + 2))
    echo -n "."
done

if [ $waited -ge $max_wait ]; then
    echo ""
    echo "⚠️  Emulator took too long to start. Please check manually."
    exit 1
fi

echo ""
echo "3️⃣  Building and installing app..."
echo ""

./gradlew installDebug

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ SUCCESS! MathMaster is ready!"
    echo ""
    echo "🎮 The app should appear on your emulator home screen"
    echo "📱 Look for the 'MathMaster' icon"
    echo ""
    echo "Useful commands:"
    echo "  - View logs: adb logcat | grep MathMaster"
    echo "  - Stop emulator: adb emu kill"
    echo "  - Reinstall app: ./gradlew installDebug"
    echo ""
else
    echo ""
    echo "❌ Build failed. Check the errors above."
    kill $EMULATOR_PID 2>/dev/null
    exit 1
fi
