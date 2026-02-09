#!/bin/bash

# MathMaster App Runner
# This script builds and installs the app on the emulator

export ANDROID_HOME=/opt/homebrew/share/android-commandlinetools
export PATH=$PATH:$ANDROID_HOME/platform-tools

echo "📱 Building and installing MathMaster app..."
echo ""

# Check if emulator is running
if ! $ANDROID_HOME/platform-tools/adb devices | grep -q "emulator"; then
    echo "⚠️  No emulator detected!"
    echo "Please start the emulator first:"
    echo "  ./run-emulator.sh &"
    echo ""
    exit 1
fi

# Build and install
./gradlew installDebug

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ App installed successfully!"
    echo "🎮 Look for 'MathMaster' on your emulator home screen"
    echo ""
    echo "To view logs:"
    echo "  $ANDROID_HOME/platform-tools/adb logcat | grep MathMaster"
else
    echo ""
    echo "❌ Build failed. Check the errors above."
    exit 1
fi
