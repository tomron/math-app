#!/bin/bash

# MathMaster Emulator Runner
# This script starts the Android emulator for development

export ANDROID_HOME=/opt/homebrew/share/android-commandlinetools
export PATH=$PATH:$ANDROID_HOME/emulator:$ANDROID_HOME/platform-tools

echo "🚀 Starting MathMaster emulator..."
echo ""

# Start emulator
$ANDROID_HOME/emulator/emulator -avd MathMaster_Emulator "$@"
