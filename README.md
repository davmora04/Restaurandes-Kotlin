# Restaurandes - Campus Food Discovery App

## Overview
Restaurandes is a mobile platform that centralizes and standardizes information about food establishments on and around campus, helping students and professors discover, compare, and select dining options efficiently.

## Project Status
🚧 **Sprint 2 - In Development**

## Features (Planned)
- 🔍 Search and filter food establishments
- 🗺️ Campus map with restaurant locations
- ⭐ Ratings and reviews
- 🕐 Real-time "Open Now" status
- 📋 Menu highlights and pricing
- ❤️ Favorites management
- 🔄 Side-by-side comparison
- 👤 User authentication
- 📊 Analytics integration

## Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM + Clean Architecture
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 35 (Android 15)

## Getting Started

### Prerequisites
- Android Studio Ladybug | 2024.2.1 or later
- JDK 17 or later
- Android SDK

### Installation
1. Clone the repository
```bash
git clone <repository-url>
cd Restaurandes-Kotlin
```

2. Open the project in Android Studio

3. Sync Gradle files

4. Run the app on an emulator or physical device

## Project Structure
```
app/
├── src/
│   ├── main/
│   │   ├── java/com/unieats/
│   │   │   ├── data/          # Data layer
│   │   │   ├── domain/        # Business logic
│   │   │   ├── ui/            # UI components
│   │   │   └── MainActivity.kt
│   │   ├── res/               # Resources
│   │   └── AndroidManifest.xml
│   └── test/                  # Unit tests
└── build.gradle.kts
```

## Team
- Grupo 22 - Móviles

## License
Educational project - Universidad de los Andes

## Sprint 2 Requirements
- [ ] Business Questions implementation
- [ ] Analytics pipeline
- [ ] Architectural design
- [ ] Sensor integration
- [ ] Context-aware features
- [ ] Smart features
- [ ] User authentication
- [ ] External services integration
