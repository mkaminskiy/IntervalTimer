# Project Guidelines

## Build

- Java is bundled with Android Studio. Always set `JAVA_HOME` before Gradle commands:
  `export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"`
- Use proxy for Gradle builds. Pass proxy settings via gradle.properties or command line.

## ADB

- When installing APK via `adb install`, always use `--user 0` to install only for the main user.
  Example: `adb install -r --user 0 app/build/outputs/apk/debug/app-debug.apk`
