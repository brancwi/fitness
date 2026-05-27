#!/bin/bash
GRADLE_HOME=/home/brancwi/gradle-8.6
export ANDROID_HOME=/home/brancwi/android-sdk
exec "$GRADLE_HOME/bin/gradle" "$@"
