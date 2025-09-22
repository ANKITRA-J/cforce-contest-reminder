# CF Contest Reminder

Lightweight Android app using Kotlin + Jetpack Compose to remind you of upcoming Codeforces contests, with timezone selection and customizable notifications. APK will be distributed on GitHub Releases.

## Build
- Android Studio Ladybug or newer
- Gradle 8.x
- JDK 17

```bash
./gradlew :app:assembleRelease
```

## Roadmap
- Fetch contests from Codeforces API
- Real-time countdowns
- WorkManager scheduling for reminders
- DataStore for settings (notifications, timezone)
- Dark theme UI

