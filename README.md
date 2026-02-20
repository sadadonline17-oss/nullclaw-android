# NullClaw Android Client

A native Android chat application that connects to a NullClaw backend service.

## Features

- **Login Screen**: API key authentication with NullClaw backend
- **Chat Interface**: Real-time messaging with AI assistant
- **Session Management**: Unique device ID for conversation persistence
- **Memory Persistence**: SQLite-based memory via NullClaw backend

## Architecture

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Network**: Retrofit + OkHttp
- **Architecture**: MVVM with Repository pattern

## Project Structure

```
app/
├── src/main/java/com/nullclaw/android/
│   ├── MainActivity.kt           # Single Activity
│   ├── NullClawApp.kt            # Application class
│   ├── data/
│   │   ├── api/
│   │   │   ├── NullClawApi.kt    # Retrofit API interface
│   │   │   └── NullClawClient.kt # OkHttp client
│   │   ├── model/
│   │   │   ├── ChatMessage.kt
│   │   │   ├── ChatRequest.kt
│   │   │   └── ChatResponse.kt
│   │   └── repository/
│   │       └── ChatRepository.kt
│   ├── ui/
│   │   ├── login/
│   │   │   ├── LoginScreen.kt
│   │   │   └── LoginViewModel.kt
│   │   └── chat/
│   │       ├── ChatScreen.kt
│   │       └── ChatViewModel.kt
│   └── util/
│       └── SessionManager.kt
├── build.gradle.kts
└── proguard-rules.pro
```

## NullClaw Backend API

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/health` | GET | Health check |
| `/pair` | POST | Exchange pairing code for bearer token |
| `/webhook` | POST | Send chat message |

### Example Request

```json
POST /webhook
Authorization: Bearer <token>
Content-Type: application/json

{
  "message": "Hello, NullClaw!",
  "session_id": "unique_device_id"
}
```

### Example Response

```json
{
  "status": "ok",
  "response": "Hello! How can I help you today?"
}
```

## Building

```bash
./gradlew assembleDebug
./gradlew assembleRelease
```

## Configuration

Set these environment variables for release builds:
- `KEYSTORE_PASSWORD`
- `KEY_ALIAS`
- `KEY_PASSWORD`
- `NULLCLAW_API_KEY`
- `NULLCLAW_PROVIDER`
- `NULLCLAW_MODEL`

## License

MIT