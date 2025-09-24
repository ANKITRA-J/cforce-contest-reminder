# CF Contest Reminder

Lightweight Android app using Kotlin + Jetpack Compose to remind you of upcoming Codeforces contests, with timezone selection and customizable notifications. 
# CF Contest Reminder

Android app to remind you of upcoming Codeforces contests with customizable notifications and timezone support.

## Download

[Download Latest APK](https://github.com/ANKITRA-J/cf-contest-reminder/releases/latest/download/app-release.apk)

## Features

- Real-time contest tracking from Codeforces API
- Customizable notification timing (5-120 minutes before contest)
- Multiple timezone support
- Live countdown to contest start
- Dark theme UI with Material 3 design
- Background sync every 15 minutes
- No ads or tracking

## Installation

1. Download the APK from the link above
2. Enable "Unknown sources" in your Android settings if prompted
3. Install the APK file

## Requirements

- Android 7.0 or higher
- Internet connection
- Notification permissions

## Settings

### Notifications
- Toggle notifications on/off
- Set reminder time (5-120 minutes before contest starts)

### Timezone
Choose from 12 popular timezones including:
- US timezones (New York, Los Angeles, Chicago, Denver)
- European timezones (London, Paris, Berlin, Moscow)  
- Asian timezones (Tokyo, Shanghai, Mumbai/Delhi)
- Australia (Sydney)

## Build from Source

### Prerequisites
- Android Studio Ladybug or newer
- JDK 17
- Gradle 8.x

### Steps
```bash
git clone https://github.com/YOUR_USERNAME/cf-contest-reminder.git
cd cf-contest-reminder
./gradlew :app:assembleRelease
```

The APK will be generated at `app/build/outputs/apk/release/app-release.apk`

## Technology Stack

- Kotlin
- Jetpack Compose with Material 3
- Retrofit for API calls
- WorkManager for background tasks
- DataStore for settings
- Kotlinx DateTime for time handling

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## License

MIT License - see LICENSE file for details.

## Author

Made by [Ankit Raj](https://github.com/ANKITRA-J)
