# Project Guidelines

## ADB

- When installing APK via `adb install`, always use `--user 0` to install only for the main user.
  Example: `adb install -r --user 0 app/build/outputs/apk/debug/app-debug.apk`
