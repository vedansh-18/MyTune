# Software Requirements Specification (SRS) for MyTune

**Document Version**: 1.0  
**Status**: Initial Baseline  
**Project**: MyTune Local Android Music Player  

---

## 1. Document Purpose

This Software Requirements Specification (SRS) document defines the complete functional and non-functional requirements for the **MyTune** Android application. This document serves as the single source of truth for software development, quality assurance testing, requirements traceability, and release verification.

---

## 2. Scope

MyTune is an offline, local audio playback and playlist management application for Android devices. The scope encompasses:
* Local audio file scanning and media library discovery.
* Automated keyword-based filtering (excluding call recordings).
* Custom audio format extension filtering.
* Playlist creation, editing, track reordering, track removal, and deletion.
* Fullscreen audio playback with artwork display, seek bar control, and time formatting.
* Background playback via an Android Foreground Service.
* Persistent system media style notification controls.
* Persistent bottom mini-player bar for quick playback control.

---

## 3. Product Overview & Architecture

MyTune is built as a native Android application in Java targeting Android SDK 37 (Minimum SDK 24). It uses standard Android system APIs including `MediaStore`, `MediaPlayer`, `SharedPreferences`, `MediaMetadataRetriever`, and `ForegroundService` with `NotificationCompat.MediaStyle`.

---

## 4. Intended Use & Target Users

* **Intended Use**: Local music playback and audio file organization on an Android smartphone or tablet.
* **Target Users**: Personal Android device users looking for a lightweight, offline music player that automatically filters out call recordings and offers simple playlist management.

---

## 5. Functional Requirements

| Requirement ID | Module / Area | Description | Priority |
| :--- | :--- | :--- | :--- |
| **FR-001** | Permissions | The application shall request runtime audio storage permissions on startup: `READ_MEDIA_AUDIO` and `POST_NOTIFICATIONS` for Android 13+ (API 33+), or `READ_EXTERNAL_STORAGE` for Android 12 and below (API < 33). | High |
| **FR-002** | Media Scanning | The application shall scan `MediaStore.Audio.Media.EXTERNAL_CONTENT_URI` for audio files where `IS_MUSIC != 0`, sorted ascending by track title. | High |
| **FR-003** | Media Scanning | The application shall execute a fallback recursive file scan of `Environment.getExternalStorageDirectory()` if MediaStore returns zero audio files. | Medium |
| **FR-004** | File Filtering | The application shall filter scanned files to include only supported extensions: `.mp3`, `.m4a`, `.wav`, `.flac`, `.aac`, `.ogg`, `.opus`, `.wma`. | High |
| **FR-005** | File Filtering | The application shall automatically exclude audio files and directories whose name or path contains call recording keywords (`call`, `recording`, `call_rec`, `soundrecorder`, `voice_rec`, `callrecord`, `voicerecorder`). | High |
| **FR-006** | UI / Main | The application shall display all discovered tracks in a scrollable list under the "All Songs" tab, with filename extension suffixes stripped from display names. | High |
| **FR-007** | UI / Navigation | The application shall provide tab switching between "All Songs" and "Playlists" via top tab buttons or horizontal swipe gestures on the list view. | Medium |
| **FR-008** | Playlist | The application shall allow users to create a new playlist by entering a name in a dialog and selecting tracks via a multi-choice checklist dialog. | High |
| **FR-009** | Playlist | The application shall persist created playlists and their track file paths in `SharedPreferences` under the key `saved_playlists_json`. | High |
| **FR-010** | Playlist | The application shall list all saved playlists in the "Playlists" tab with playlist title and track count. | High |
| **FR-011** | Playlist | The application shall open a playlist detail view upon selection, hiding tab buttons and displaying a back arrow in the header bar. | High |
| **FR-012** | Playlist | The application shall allow reordering tracks (Move Up / Move Down) within a playlist via a long-press context dialog. | Medium |
| **FR-013** | Playlist | The application shall allow removing individual tracks from a playlist via a long-press context dialog. | High |
| **FR-014** | Playlist | The application shall allow deleting an entire playlist via a long-press context dialog with user confirmation. | High |
| **FR-015** | Playlist | The application shall allow adding any track from "All Songs" to an existing playlist via a long-press dialog, preventing duplicate entries. | High |
| **FR-016** | Playback | The application shall launch the full playback screen (`PlaySong`) and initiate audio playback when a track in any list is tapped. | High |
| **FR-017** | Playback | The application shall support playback actions: Play, Pause, Toggle Play/Pause, Next track (wrap-around), Previous track (wrap-around), and Seeking to a specific time position. | High |
| **FR-018** | Playback | The application shall provide an Autoplay toggle mode. When Autoplay is ON (default), playback automatically advances to the next track on track completion; when OFF, playback pauses on completion. | High |
| **FR-019** | Artwork | The application shall retrieve embedded album artwork from audio files using `MediaMetadataRetriever` and render it in `PlaySong` and the mini-player bar, using fallback drawables when artwork is absent. | Medium |
| **FR-020** | Playback | The application shall update the seekbar position and time labels (`txtCurrentTime`, `txtTotalTime`) formatted as `M:SS` every 500ms during playback. | High |
| **FR-021** | Mini-Player | The application shall display a persistent mini-player bar at the bottom of `MainActivity` when playback is prepared, featuring artwork thumbnail, marquee title, Play/Pause, Close button, and tap-to-open full player. | High |
| **FR-022** | Service / Notification | The application shall run a Foreground Service with a `MediaStyle` notification (`MyTune_Playback_Channel`) displaying track title, app label, and interactive Previous, Play/Pause, Next controls. | High |
| **FR-023** | Service / Notification | Tapping the media notification shall open the `PlaySong` activity. | High |
| **FR-024** | Service / Notification | Removing the application from recent tasks (`onTaskRemoved`) shall stop audio playback, terminate the foreground service, and remove the media notification. | High |

---

## 6. Non-Functional Requirements

| Requirement ID | Category | Description | Priority |
| :--- | :--- | :--- | :--- |
| **NFR-001** | Performance | Time updates and seekbar progress shall refresh at 500ms intervals without causing UI main thread lag or jank. | Medium |
| **NFR-002** | Offline Operation | The application shall operate completely offline without requiring network permissions or internet connectivity. | High |
| **NFR-003** | Resource Management | `MediaPlayer` and `MediaMetadataRetriever` resources shall be released properly upon service stop or activity destruction to prevent memory leaks. | High |
| **NFR-004** | Usability / Dark UI | The application UI shall adhere to a dark color palette (`#080616` background, `#1A1953` surfaces, `#2F2FE4` accent) with high text contrast. | Medium |

---

## 7. User Interface Requirements

| Requirement ID | Description |
| :--- | :--- |
| **UIR-001** | Top header title shall show "MyTune" on main screen and selected playlist name when inside playlist detail view. |
| **UIR-002** | Song title text views in `PlaySong` and mini-player shall use single-line marquee scrolling for long titles. |

---

## 8. Data Requirements

| Requirement ID | Description |
| :--- | :--- |
| **DR-001** | Playlists shall be serialized as JSON strings in `SharedPreferences` (`MyTunePlaylistsPref`, key `saved_playlists_json`). |
| **DR-002** | On reading playlists, file paths corresponding to missing/deleted files on disk shall be automatically filtered out. |

---

## 9. Business / Application Rules

| Requirement ID | Description |
| :--- | :--- |
| **BR-001** | The application shall never delete, move, or modify original audio files stored on the device filesystem. |
| **BR-002** | The application shall filter out voice and call recordings from music library listings based on specific predefined keyword matching. |

---

## 10. Compatibility & Environment Requirements

| Requirement ID | Description |
| :--- | :--- |
| **CR-001** | Android OS Version Compatibility: Minimum SDK 24 (Android 7.0 Nougat) to Target SDK 37 (Android 16). |
| **CR-002** | Platform Compatibility: Native Android phones and tablets running ARM / ARM64 / x86 / x86_64 architectures. |

---

## 11. Permission Requirements

| Permission | API Level | Purpose |
| :--- | :--- | :--- |
| `READ_MEDIA_AUDIO` | API 33+ | Read audio files from local storage |
| `POST_NOTIFICATIONS` | API 33+ | Post media control notifications in status bar |
| `READ_EXTERNAL_STORAGE` | API < 33 | Read audio files from external storage |
| `FOREGROUND_SERVICE` | All APIs | Run background playback service |
| `FOREGROUND_SERVICE_MEDIA_PLAYBACK` | API 34+ | Run foreground service with media playback type |

---

## 12. Constraints, Assumptions & Known Limitations

| ID | Type | Description |
| :--- | :--- | :--- |
| **CON-001** | Constraint | Offline operation only; no online music streaming or cloud sync support. |
| **ASM-001** | Assumption | Audio files stored on local device disk are formatted with standard ID3 / audio tags for metadata retrieval. |
| **LIM-001** | Limitation | Intent extras passing `ArrayList<File>` between activities may hit Android IPC limits (`TransactionTooLargeException`) for extremely large music libraries (thousands of tracks). |
