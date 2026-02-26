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

**Domain Layer** (Business Logic)
- Pure Kotlin modules, framework-independent
- Models: `Restaurant`, `User`, `Review`, `Location`
- Repository interfaces defining contracts
- Use cases encapsulating business rules

**Data Layer** (Data Sources)
- Repository implementations
- Remote data sources (Retrofit API)
- Local data sources (to be implemented)
- DTOs and mappers

**Presentation Layer** (UI)
- MVVM pattern with Jetpack Compose
- ViewModels managing UI state
- Composable functions for UI
- Navigation component

### Dependencies
- **Dagger Hilt**: Dependency injection
- **Retrofit**: REST API client
- **Coil**: Image loading
- **Google Play Services**: Location services
- **Firebase** (to be configured): Analytics, Auth, Firestore

## Current Implementation Status

### ✅ Completed
- Clean Architecture structure
- Domain models and repository interfaces
- Use cases for core features
- Data layer with mock implementations
- Home screen with restaurant listing
- Filter system (All, Nearby, Open, Top Rated, Economic)
- Login/Register screens
- Navigation graph
- Bottom navigation
- Location repository with GPS sensor

### 🚧 In Progress
- Firebase integration (Analytics, Auth, Firestore)
- Remaining screen implementations
- Analytics tracking for BQs

### 📋 To Do
- Complete all screen implementations
- Implement smart recommendation features
- Add real backend API
- Complete authentication flow
- Implement analytics pipeline
- Document architecture diagrams

## Business Questions (Sprint 2)

1. **BQ1 - Type 1 (Telemetry)**: Weekly active users count
2. **BQ2 - Type 2 (UX)**: Section interaction analytics
3. **BQ3 - Type 3 (Feature)**: Restaurant view to favorite conversion rate

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
