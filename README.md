# QuickChat

A modern Android messenger app built with Jetpack Compose, demonstrating clean architecture with MVVM pattern and Hilt dependency injection.

## Architecture

- **MVVM + MVI-inspired state management** — ViewModels expose `StateFlow<UiState>` for unidirectional data flow
- **Hilt DI** — Constructor injection throughout, with `@Module`/`@InstallIn` for bindings
- **Repository pattern** — Single source of truth via Room database, with WebSocket events persisted to local DB
- **Use Cases** — Thin domain layer encapsulating business operations

## Features

- Chat list with last message preview, timestamps, and unread badges
- Real-time messaging via mock WebSocket with auto-replies
- Message status flow: Sent (single check) → Delivered (double check) → Read (blue double check)
- Push notification simulation (local notifications on incoming messages)
- User profiles with avatar placeholders (initials-based)
- Dark/light theme toggle with DataStore persistence
- Online/offline status indicators
- Animated typing indicator
- Relative timestamps ("Just now", "5 min ago", "Yesterday")

## Tech Stack

- Kotlin
- Jetpack Compose + Material3
- Hilt (dependency injection)
- Room (local persistence)
- OkHttp (WebSocket interface)
- DataStore (preferences)
- Kotlin Coroutines + StateFlow
- Navigation Compose

## Building

```bash
# Debug build
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug
```

Requires Android SDK 34 and JDK 17.

## Project Structure

```
app/src/main/java/com/quickchat/app/
├── data/
│   ├── local/          # Room database, DAOs, entities
│   ├── remote/         # WebSocket interface + mock implementation
│   ├── repository/     # Repository implementations
│   └── notification/   # Mock FCM service + notification helper
├── domain/
│   ├── model/          # Domain data classes
│   └── usecase/        # Business logic use cases
├── di/                 # Hilt modules
├── ui/
│   ├── theme/          # Material3 color schemes, typography, shapes
│   ├── screens/        # Screen composables + ViewModels
│   ├── components/     # Reusable UI components
│   └── navigation/     # NavGraph + Screen routes
└── util/               # Date formatting, theme preferences
```
