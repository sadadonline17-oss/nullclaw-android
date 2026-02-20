# NullClaw Android Project Summary

## Project Overview

This project replaces OpenClaw with NullClaw as the AI runtime backend and provides a complete Android APK with automated CI/CD pipeline.

## Architecture

```
┌─────────────────────┐         ┌─────────────────────┐
│   Android APK       │         │   NullClaw Backend  │
│   (Kotlin/Compose)  │◄───────►│   (Zig Binary)      │
│                     │   HTTP  │                     │
│  ┌───────────────┐  │         │  ┌───────────────┐  │
│  │  Login Screen │  │         │  │   Gateway     │  │
│  │  (Pairing)    │  │         │  │   (Port 3000) │  │
│  └───────────────┘  │         │  └───────────────┘  │
│  ┌───────────────┐  │         │  ┌───────────────┐  │
│  │  Chat Screen  │  │         │  │   AI Provider │  │
│  │  (Messages)   │  │         │  │   (OpenRouter)│  │
│  └───────────────┘  │         │  └───────────────┘  │
└─────────────────────┘         │  ┌───────────────┐  │
                                │  │   Memory      │  │
                                │  │   (SQLite)    │  │
                                │  └───────────────┘  │
                                └─────────────────────┘
```

## Project Structure

```
nullclaw-android/
├── app/                              # Android application module
│   ├── src/main/java/com/nullclaw/android/
│   │   ├── MainActivity.kt           # Single Activity entry point
│   │   ├── NullClawApp.kt            # Application class
│   │   ├── data/
│   │   │   ├── api/
│   │   │   │   ├── NullClawApi.kt    # Retrofit API interface
│   │   │   │   └── NullClawClient.kt # HTTP client configuration
│   │   │   ├── model/
│   │   │   │   ├── ChatMessage.kt    # Message data class
│   │   │   │   ├── ChatRequest.kt    # API request models
│   │   │   │   └── ChatResponse.kt   # API response models
│   │   │   └── repository/
│   │   │       └── ChatRepository.kt # Data layer
│   │   ├── ui/
│   │   │   ├── login/
│   │   │   │   ├── LoginScreen.kt    # Login UI (Compose)
│   │   │   │   └── LoginViewModel.kt # Login logic
│   │   │   ├── chat/
│   │   │   │   ├── ChatScreen.kt     # Chat UI (Compose)
│   │   │   │   └── ChatViewModel.kt  # Chat logic
│   │   │   └── theme/
│   │   │       ├── Theme.kt          # Material 3 theme
│   │   │       └── Typography.kt     # Text styles
│   │   └── util/
│   │       └── SessionManager.kt     # Auth & session handling
│   ├── build.gradle.kts              # App-level Gradle config
│   └── proguard-rules.pro            # ProGuard rules
├── deployment/                        # Backend deployment
│   ├── Dockerfile                    # NullClaw container
│   ├── docker-compose.yml            # Docker Compose setup
│   ├── nullclaw.service              # Systemd service
│   ├── config.json                   # NullClaw configuration
│   └── .env.example                  # Environment template
├── .github/workflows/
│   └── build-release.yml             # CI/CD pipeline
├── scripts/
│   └── generate-keystore.sh          # Keystore generation
├── build.gradle.kts                  # Project-level Gradle
├── settings.gradle.kts               # Gradle settings
└── README.md                         # Project documentation
```

## NullClaw API Integration

### Endpoints

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/health` | GET | None | Health check |
| `/pair` | POST | X-Pairing-Code header | Exchange pairing code for token |
| `/webhook` | POST | Bearer token | Send chat message |

### Pairing Flow

1. NullClaw gateway displays 6-digit pairing code on startup
2. User enters code in Android app
3. App sends `POST /pair` with `X-Pairing-Code` header
4. Gateway returns bearer token
5. Token stored securely in Android encrypted preferences

### Chat Flow

1. App sends `POST /webhook` with:
   ```json
   {
     "message": "user input",
     "session_id": "unique_device_id"
   }
   ```
2. Gateway processes message through AI provider
3. Response returned as:
   ```json
   {
     "status": "ok",
     "response": "AI response text"
   }
   ```

## Setup Instructions

### 1. Backend Deployment

```bash
# Clone the project
cd nullclaw-android/deployment

# Configure environment
cp .env.example .env
# Edit .env with your API keys

# Start with Docker
docker-compose up -d

# Or install as systemd service
sudo cp nullclaw.service /etc/systemd/system/
sudo systemctl enable nullclaw
sudo systemctl start nullclaw
```

### 2. Android Build

```bash
# Generate keystore (first time only)
cd nullclaw-android/scripts
chmod +x generate-keystore.sh
./generate-keystore.sh

# Build debug APK
cd nullclaw-android
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

### 3. CI/CD Setup

Add these secrets to your GitHub repository:

| Secret | Description |
|--------|-------------|
| `KEYSTORE_BASE64` | Base64-encoded keystore file |
| `KEYSTORE_PASSWORD` | Keystore password |
| `KEY_ALIAS` | Key alias (default: nullclaw) |
| `KEY_PASSWORD` | Key password |

To encode keystore:
```bash
base64 -w 0 release.keystore
```

### 4. Release Process

```bash
# Create and push a tag
git tag v1.0.0
git push origin v1.0.0

# GitHub Actions will:
# 1. Build release APK
# 2. Create GitHub Release
# 3. Attach APK to release
```

## Configuration

### NullClaw Backend (`config.json`)

- **Provider**: OpenRouter (configurable)
- **Memory**: SQLite with hygiene enabled
- **Gateway**: Port 3000, pairing required
- **Security**: Sandbox auto-detect, audit logging

### Android App

- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)
- **Default URL**: `http://10.0.2.2:3000` (emulator localhost)

## Security Features

1. **Pairing Required**: 6-digit code exchange
2. **Bearer Token Auth**: All webhook requests authenticated
3. **Encrypted Storage**: Android encrypted preferences
4. **Rate Limiting**: Gateway enforces limits
5. **TLS Support**: Via Cloudflare/ngrok tunnels

## Testing

```bash
# Check backend health
curl http://localhost:3000/health

# Pair with gateway (use code shown on startup)
curl -X POST http://localhost:3000/pair \
  -H "X-Pairing-Code: 123456"

# Send message
curl -X POST http://localhost:3000/webhook \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"message":"Hello!","session_id":"test"}'
```

## Migration from OpenClaw

NullClaw is compatible with OpenClaw configuration format. To migrate:

```bash
# On NullClaw backend
nullclaw migrate openclaw --source /path/to/openclaw/workspace
```

This imports:
- Memory database
- Conversation history
- Configuration settings

## Troubleshooting

### Connection Issues

1. Verify backend is running: `curl http://localhost:3000/health`
2. Check Android network security config allows cleartext traffic
3. For physical devices, use computer's IP address (not localhost)

### Pairing Issues

1. Check gateway logs for pairing code
2. Ensure code is entered within timeout period
3. Verify `require_pairing: true` in config

### Build Issues

1. Ensure JDK 17 is installed
2. Check Android SDK is configured
3. Run `./gradlew clean` before building

## License

MIT License - See LICENSE file for details.