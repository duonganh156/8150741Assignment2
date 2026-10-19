# 8150741Assignment2 - Roam

Roam is a small Android app (Kotlin, XML views) that logs in against the **nit3213 API**, lists the
returned entities in a `RecyclerView`, and shows every field of a tapped entity on a details screen.

## Screens

| Screen | What it does |
| --- | --- |
| **Login** | Username + password form. Calls `POST /footscray/auth`, shows an error message on failure, and opens the Dashboard with the returned `keypass` on success. |
| **Dashboard** | Calls `GET /dashboard/{keypass}` and shows each entity in a card (headline + summary, *without* the description). Has loading, error + retry and a log-out menu item. |
| **Details** | Shows all properties of the selected entity plus its description. |

## Tech stack

- Kotlin, AndroidX, Material 3, View Binding
- **Hilt** (via KSP) for dependency injection
- Retrofit + OkHttp + Gson for networking
- Coroutines + `StateFlow` + `ViewModel`
- JUnit + `kotlinx-coroutines-test` for unit tests (hand-written fakes, no mocking library)

## Requirements

- Android Studio
- JDK 17 (the JDK bundled with Android Studio is fine)
- Android SDK Platform 37 (Android Studio offers to install it on first sync)
- An emulator or device running Android 8.0 (API 26) or newer, with internet access

## Build and run

1. Clone the repository and open the **project root folder** (`8150741Assignment2`) in Android Studio
   (*File → Open*). The project name shown in Android Studio is `8150741Assignment2`.
2. Wait for the Gradle sync to finish (first sync downloads dependencies).
3. Select the `app` run configuration and an emulator/device, then press **Run ▶**.
4. Log in with:
    - **Username:** `8150741`
    - **Password:** `Adam`

> The API can take up to a minute to wake up. The first login may therefore take a while
> (request timeouts are set to 60 s). If it times out, simply tap **Log in** again.

Command line (after Android Studio has generated the Gradle wrapper, or via `gradle wrapper --gradle-version 8.7`):

```bash
./gradlew assembleDebug        # build the debug APK
./gradlew testDebugUnitTest    # run the unit tests
```

## API

Base URL: `https://nit3213apinew.onrender.com/`

| Purpose | Request | Response |
| --- | --- | --- |
| Login | `POST /footscray/auth` with `{"username": "...", "password": "..."}` | `{"keypass": "topicName"}` |
| Dashboard | `GET /dashboard/{keypass}` | `{"entities": [ {...}, ... ], "entityTotal": n}` |

The property names inside each entity depend on the topic, so entities are parsed generically: every
property except `description` becomes an ordered label/value pair; `description` is kept separately and
only shown on the Details screen.

## Architecture

```
app/src/main/java/com/roam/assignment2
├── RoamApplication.kt          @HiltAndroidApp
├── di/                         Hilt modules
│   ├── NetworkModule.kt          Gson, OkHttp, Retrofit, NitApiService
│   └── RepositoryModule.kt       @Binds interface -> implementation
├── data/
│   ├── remote/                   Retrofit API + DTOs
│   ├── mapper/                   DTO -> domain mapping
│   └── repository/               AuthRepository, DashboardRepository (+ Impl)
├── domain/model/               Entity, DashboardData
├── ui/
│   ├── login/                    LoginActivity, LoginViewModel, LoginUiState
│   ├── dashboard/                DashboardActivity, DashboardViewModel, EntityAdapter, DashboardUiState
│   └── details/                  DetailsActivity
└── util/                       ApiResult, safeApiCall, ErrorMessages, label formatting
```

The flow is `Activity → ViewModel → Repository → Retrofit API`. Activities only render `StateFlow`
UI state; ViewModels never touch Android views; repositories convert network failures into
`ApiResult.Error` with user-friendly messages.

### Dependency injection (Hilt)

- `@HiltAndroidApp` on `RoamApplication`; `@AndroidEntryPoint` on the Login and Dashboard activities.
- `@HiltViewModel` + `@Inject constructor` for `LoginViewModel` and `DashboardViewModel`.
- `NetworkModule` (`@Provides`, singleton scope) builds the OkHttp client, Retrofit and `NitApiService`.
- `RepositoryModule` (`@Binds`) exposes each repository as an interface, so ViewModels depend on
  abstractions and tests can substitute fakes.
- The `keypass` is passed to the Dashboard in the launching `Intent`; Hilt hands it to
  `DashboardViewModel` through `SavedStateHandle`.

## Tests

Run from Android Studio (right-click `app/src/test` → *Run Tests*) or with `./gradlew testDebugUnitTest`.

| Test class | Covers |
| --- | --- |
| `LoginViewModelTest` | validation, loading state, success, failure, trimming, double-submit, retry |
| `DashboardViewModelTest` | load on start, error, missing keypass, retry, empty list |
| `AuthRepositoryImplTest` / `DashboardRepositoryImplTest` | request mapping and HTTP/IO error → message mapping |
| `EntityMapperTest` | description split, field order, number formatting, nulls, total fallback |
| `EntityTest`, `TextFormattingTest`, `SafeApiCallTest` | model helpers, label formatting, error wrapper (incl. cancellation) |
