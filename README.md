# Restaurandes - Campus Food Discovery App

## Overview
Restaurandes is a mobile platform that centralizes information about food establishments on and around campus, helping students and professors discover, compare, and select dining options efficiently.

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
│   │   ├── java/com/restaurandes/
│   │   │   ├── data/                  # Data layer
│   │   │   │   ├── remote/            # API interfaces & DTOs
│   │   │   │   └── repository/        # Repository implementations
│   │   │   ├── domain/                # Business logic
│   │   │   │   ├── model/             # Domain models
│   │   │   │   ├── repository/        # Repository interfaces
│   │   │   │   └── usecase/           # Use cases
│   │   │   ├── presentation/          # UI layer
│   │   │   │   ├── auth/              # Login/Register screens
│   │   │   │   ├── home/              # Home screen
│   │   │   │   ├── detail/            # Restaurant detail
│   │   │   │   ├── map/               # Map view
│   │   │   │   ├── search/            # Search screen
│   │   │   │   ├── favorites/         # Favorites screen
│   │   │   │   ├── profile/           # Profile screen
│   │   │   │   └── navigation/        # Navigation graph
│   │   │   ├── di/                    # Dependency injection
│   │   │   ├── ui/theme/              # Material 3 theme
│   │   │   ├── RestaurandesApplication.kt
│   │   │   └── MainActivity.kt
│   │   ├── res/                       # Resources
│   │   └── AndroidManifest.xml
│   └── test/                          # Unit tests
└── build.gradle.kts
```

## Architecture

### Clean Architecture Layers

**Domain Layer**
- Pure Kotlin, framework-independent
- Models: `Restaurant`, `User`, `Review`, `Location`
- Repository interfaces defining contracts
- Use cases encapsulating business rules

**Data Layer**
- Repository implementations
- Remote data sources (Firebase Firestore)
- DTOs and mappers

**Presentation Layer**
- MVVM pattern with Jetpack Compose
- ViewModels managing UI state via StateFlow
- Composable functions for UI
- Navigation component

### Dependencies
- **Dagger Hilt**: Dependency injection
- **Firebase**: Auth, Firestore, Analytics
- **Coil**: Image loading
- **Google Play Services**: Location services

## Features
- Search and filter food establishments
- Campus map with restaurant locations
- Ratings and reviews
- Real-time open/closed status based on current time
- Favorites management
- Side-by-side restaurant comparison with smart suggestion
- User authentication (Firebase Auth)
- Analytics pipeline (Firebase Analytics) for business questions

## Business Questions (Sprint 2)
1. **BQ1 - Type 1**: Weekly active users count
2. **BQ2 - Type 2**: Section interaction analytics
3. **BQ3 - Type 3**: Restaurant view to favorite conversion rate

## Team
Grupo 22 - Moviles

## License
Educational project - Universidad de los Andes
