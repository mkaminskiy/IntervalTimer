# Interval Timer

Android приложение для интервального таймера с уведомлениями.

## Функции

- Выбор времени с помощью круговых селекторов (dials)
- Настройка количества интервалов (1-10)
- Уведомления при переходе между интервалами
- Финальное уведомление по завершении таймера
- Работа в фоновом режиме (Foreground Service)
- Поддержка уведомлений на смарт-часах

## Скриншоты

| Настройка | Таймер |
|-----------|--------|
| Выбор времени и интервалов | Обратный отсчёт |

## Технологии

- Kotlin
- Jetpack Compose
- Material 3
- Foreground Service
- Notification Channels

## Требования

- Android 8.0 (API 26) и выше
- Разрешение на уведомления (Android 13+)

## Сборка

```bash
./gradlew assembleDebug
```

APK будет в `app/build/outputs/apk/debug/app-debug.apk`

## Установка

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```