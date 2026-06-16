# Trekkly 🏕️

An Android app for hikers and trekkers to discover trails, log adventures, and connect with the outdoors community.

---

## Screenshots

> _Coming soon_

---

## Tech Stack

| Layer | Technology |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Architecture | Clean Architecture (Domain / Data / Presentation) |
| DI | Hilt |
| Auth | Firebase Phone Authentication |
| Database | Firestore (remote) · Room (local cache, planned) |
| Session | Preferences DataStore |
| Navigation | Jetpack Navigation Compose |
| Async | Kotlin Coroutines + Flow |
| Serialization | kotlinx.serialization |
| Networking | Retrofit + OkHttp |
| Background | WorkManager |

---

## Architecture

```
app/
├── data/
│   ├── local/
│   │   ├── datastore/        # SessionPreferences (DataStore)
│   │   └── CountryCodeAssetDataSource.kt
│   ├── mapper/               # DTOs
│   └── repository/           # Repository implementations
│       ├── FirebaseAuthRepositoryImpl.kt
│       ├── MockAuthRepositoryImpl.kt
│       ├── MockUserRepositoryImpl.kt
│       └── CountryCodeRepositoryImpl.kt
├── di/                       # Hilt modules
│   ├── DataStoreModule.kt
│   ├── FirebaseModule.kt
│   └── RepositoryModule.kt
├── domain/
│   ├── model/                # User, CountryCode
│   ├── repository/           # Repository interfaces
│   └── usecase/              # Business logic
│       ├── signup/
│       │   ├── SendOtpUseCase.kt
│       │   └── CompleteSignUpUseCase.kt
│       └── login/
│           └── LoginUseCase.kt
├── navigation/
│   ├── ScreenDestination.kt
│   └── TrekklyNavHost.kt
└── presentation/
    ├── AuthScreen/
    ├── Splash/
    ├── signup/
    │   ├── ui/               # SignUpScreen, OtpVerificationScreen
    │   ├── uievents/         # SignUpEvents, OtpUiState
    │   └── viewmodel/        # SignUpViewModel, OtpViewModel
    ├── login/
    │   ├── ui/               # LoginScreen
    │   ├── event/            # LoginUiState
    │   └── viewmodel/        # LoginViewModel
    └── theme/
```

---

## Features

### Completed
- [x] Splash screen with animated exit
- [x] Authentication landing screen
- [x] Sign Up with phone number + country code picker
- [x] Firebase Phone Auth OTP verification (6-digit)
- [x] Login with phone number lookup
- [x] Session persistence via Preferences DataStore
- [x] Edge-to-edge UI with WindowInsets status bar handling
- [x] Bundled country codes asset (44 countries, no API)

### Planned
- [ ] Home screen — trail discovery feed
- [ ] User profile completion screen
- [ ] Trek logging and history
- [ ] Offline trail data with Room cache
- [ ] Map integration

---

## Firebase Setup

1. Create a Firebase project at [console.firebase.google.com](https://console.firebase.google.com)
2. Add an Android app with package name `com.example.trekkly`
3. Download `google-services.json` and place it in `app/`
4. Enable **Phone** sign-in under Authentication → Sign-in method
5. Add your debug SHA-1 fingerprint under Project Settings → Your Android app
   ```bash
   ./gradlew signingReport
   ```
6. For development, add test phone numbers under Authentication → Sign-in method → Phone numbers for testing

---

## Getting Started

```bash
git clone https://github.com/your-username/trekkly.git
cd trekkly
# Add your google-services.json to app/
./gradlew assembleDebug
```

Minimum SDK: **24** · Target SDK: **36**


<img width="1080" height="2424" alt="image" src="https://github.com/user-attachments/assets/a20483bd-4b7e-44b6-be6b-67fc2a5e1b04" />

<img width="1080" height="2424" alt="image" src="https://github.com/user-attachments/assets/9daab610-eb76-44c4-8397-50541cc8d4d9" />

<img width="1080" height="2424" alt="image" src="https://github.com/user-attachments/assets/5d27cae9-cd67-4114-9dc3-e573460572c8" />

<img width="1080" height="2424" alt="image" src="https://github.com/user-attachments/assets/87c59e99-1877-45e9-b202-665a83f4ccad" />
<img width="1080" height="2424" alt="image" src="https://github.com/user-attachments/assets/5053b516-0152-4fee-8a3d-a850b7ed7c54" />

