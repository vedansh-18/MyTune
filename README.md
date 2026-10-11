# MyTune — Local Android Music Player

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Android SDK](https://img.shields.io/badge/Android%20SDK-24%20to%2037-green.svg)](app/build.gradle.kts)
[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen.svg)](app/build.gradle.kts)

**MyTune** is a clean, lightweight, offline music player for Android designed to discover, organize, and play locally stored audio files. It automatically scans local media storage, excludes unwanted audio clutter such as call recordings, provides intuitive playlist management, and runs background audio playback with system media notification controls.

---

## 📌 Problem Solved

Standard media scanners on Android devices often index call recordings, voice memos, and system audio clips alongside music tracks. MyTune solves this by applying an automated keyword-based exclusion filter (`call`, `recording`, `voice_rec`, `soundrecorder`, etc.) and format extension filtering, ensuring your music library contains only genuine audio tracks.

---

## ✨ Features

* **Automated Local Media Scanning**: Queries `MediaStore` audio database with recursive fallback scanning for local storage directories.
* **Call Recording Exclusion Filter**: Automatically filters out call recordings, sound notes, and voice memos from the music list.
* **Supported Formats**: Plays `.mp3`, `.m4a`, `.wav`, `.flac`, `.aac`, `.ogg`, `.opus`, and `.wma` files.
* **Playlist Management**: Create named playlists, add single or multiple tracks, reorder tracks (Move Up/Down), remove tracks, and delete playlists.
* **Persistent Playlist Storage**: User playlists are saved locally in `SharedPreferences` (JSON format).
* **Autoplay Mode**: Toggle between Autoplay ON (auto-advancing queue) and Autoplay OFF (pausing on track end).
* **Album Cover Extraction**: Extracts embedded ID3 artwork via `MediaMetadataRetriever` with fallback graphics.
* **Persistent Mini-Player Bar**: Bottom control bar on main screen for quick play/pause, title marquee, artwork thumbnail, and stopping playback.
* **Background Playback & Notification**: Runs an Android `ForegroundService` with a system `MediaStyle` notification featuring Previous, Play/Pause, and Next controls.
* **Gesture Navigation**: Swipe left or right on the track list to switch between "All Songs" and "Playlists" tabs.

---

## 🏗️ Architecture & High-Level Design

MyTune is constructed using native Android Java components:

```text
[ MainActivity (UI Controller / Permission Request / Tab & List Views) ]
        │
        ├──► [ PlaylistManager ] ──► [ SharedPreferences (JSON Playlists) ]
        │
        └──► [ MusicService (Foreground Service) ]
                   │
                   ├──► [ Android MediaPlayer Engine ]
                   └──► [ MediaStyle Notification (Status Bar Controls) ]
```

* **UI Layer**: `MainActivity` (tabs, lists, mini-player) & `PlaySong` (fullscreen player controls).
* **Service Layer**: `MusicService` (Foreground Service managing `MediaPlayer` lifecycle).
* **Data Layer**: `PlaylistManager` (`SharedPreferences`) & `AlbumArtUtils` (`MediaMetadataRetriever`).

---

## 💻 Tech Stack & Requirements

* **Language**: Java 11
* **Build System**: Gradle with Kotlin DSL (`build.gradle.kts`)
* **Android Plugin**: AGP `9.3.1`
* **Target SDK**: Android 16 (API Level 37)
* **Minimum SDK**: Android 7.0 (API Level 24)
* **Key Dependencies**:
  * `androidx.appcompat:appcompat:1.6.1`
  * `com.google.android.material:material:1.10.0`
  * `androidx.constraintlayout:constraintlayout:2.1.4`
  * `androidx.media:media:1.7.0` (MediaStyle Notifications)

---

## 🚀 Building from Source & Installation

### Prerequisites
* [Android Studio Ladybug | 2024.2.1](https://developer.android.com/studio) or newer.
* JDK 11 or higher (configured via `JAVA_HOME`).
* Android SDK Platform API 37 installed.

### Steps to Build
1. Clone the repository:
   ```bash
   git clone https://github.com/vedansh-18/MyTune.git
   cd MyTune
   ```
2. Build the debug APK using Gradle:
   ```bash
   ./gradlew assembleDebug
   ```
3. The generated APK will be located at:
   ```text
   app/build/intermediates/apk/debug/app-debug.apk
   ```

### Installing APK on Device
1. Enable **Developer Options** and **USB Debugging** on your Android device.
2. Connect your device via USB and install using `adb`:
   ```bash
   adb install -r app/build/intermediates/apk/debug/app-debug.apk
   ```
   *Or transfer `app-debug.apk` to your phone and install via your file manager.*

---

## 📖 Project Structure

```text
MyTune/
├── app/
│   ├── build.gradle.kts             <- App build configuration & dependencies
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml   <- Manifest & permissions
│       │   ├── java/com/vedansh/mytune/
│       │   │   ├── AlbumArtUtils.java
│       │   │   ├── MainActivity.java
│       │   │   ├── MusicService.java
│       │   │   ├── PlaySong.java
│       │   │   └── PlaylistManager.java
│       │   └── res/                  <- Layouts, drawables, colors, themes
│       ├── test/                     <- Unit tests
│       └── androidTest/              <- Instrumented UI tests
├── docs/
│   ├── README.md                     <- Documentation Index
│   ├── requirements/
│   │   └── SRS.md                    <- Software Requirements Specification
│   └── testing/
│       ├── Test-Plan.md              <- Master QA Test Plan
│       ├── Test-Scenarios.md         <- Test Scenarios
│       ├── Test-Cases.md             <- Detailed Test Cases
│       ├── RTM.md                    <- Requirements Traceability Matrix
│       ├── Defect-Report-Template.md <- Defect Log Template
│       └── Test-Summary-Report.md    <- Test Execution Summary Report
├── .github/                          <- Issue & PR templates
├── build.gradle.kts                  <- Root build configuration
├── settings.gradle.kts               <- Project settings
├── CHANGELOG.md                      <- Release history
├── CONTRIBUTING.md                   <- Contribution guidelines
├── CODE_OF_CONDUCT.md                <- Code of Conduct
├── LICENSE                           <- MIT License
└── SECURITY.md                       <- Security policy
```

---

## 🧪 Testing Documentation

Complete QA software testing documentation is maintained in the [`docs/`](docs/README.md) directory:

* **[Software Requirements Specification (SRS)](docs/requirements/SRS.md)**
* **[Master Test Plan](docs/testing/Test-Plan.md)**
* **[Test Scenarios](docs/testing/Test-Scenarios.md)**
* **[Detailed Test Cases](docs/testing/Test-Cases.md)**
* **[Requirements Traceability Matrix (RTM)](docs/testing/RTM.md)**

---

## ⚠️ Known Limitations

1. **Local Storage Only**: Offline music player only; does not stream music online or integrate with cloud providers.
2. **Intent Size Limit**: Passing track list extra data (`ArrayList<File>`) between activities can hit Android Binder limits (`TransactionTooLargeException`) for extremely large music collections (> 3,000 tracks).

---

## 📄 License & Privacy Notice

This project is licensed under the [MIT License](LICENSE).

* **Privacy Notice**: MyTune is a 100% local, offline application. It does not contain network permissions, analytics, or remote tracking. Zero personal data or audio files are collected or transmitted.
* **Development & QA Transparency**: This personal application was developed with heavy assistance from AI tools and technical resources. All software features, requirements, and test cases were rigorously executed, verified, and confirmed through manual human testing on physical test devices.
* **Disclaimer**: Provided "as-is" without warranty of any kind.

---

## 📬 Contact & Maintainer

* **Maintainer**: Vedansh — [GitHub Profile](https://github.com/vedansh-18)
* **Issue Tracker**: [GitHub Issues](https://github.com/vedansh-18/MyTune/issues)

