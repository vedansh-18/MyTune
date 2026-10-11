# Changelog

All notable changes to the **MyTune** project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [1.0.0] - 2026-09-14

### Added
* **Local Media Discovery**: Automated MediaStore audio scanning with recursive directory fallback.
* **Audio Extension Filtering**: Support for `.mp3`, `.m4a`, `.wav`, `.flac`, `.aac`, `.ogg`, `.opus`, `.wma`.
* **Call Recording Exclusion Filter**: Keyword-based filter excluding files/folders containing call recording terms.
* **Playlist Management**: Create named playlists, add single/batch tracks, reorder tracks (Move Up/Down), remove tracks, and delete playlists.
* **SharedPreferences Storage**: JSON serialization persistence for user-created playlists.
* **Playback Engine**: `MusicService` bound and foreground service using Android `MediaPlayer`.
* **Autoplay Toggle**: Switchable setting to auto-advance tracks on completion vs. pause on completion.
* **Album Artwork Extraction**: `MediaMetadataRetriever` extraction with fallback logo rendering.
* **UI Features**: Tab navigation, swipe gesture navigation, marquee title scrolling, bottom mini-player bar, and seekbar control.
* **Media Notification**: Persistent `MediaStyle` notification with status bar playback controls (Prev, Play/Pause, Next).
* **QA & Documentation**: Comprehensive SRS, Test Plan, Test Scenarios, Detailed Test Cases, RTM, Defect Template, and Open-Source documentation suite.
