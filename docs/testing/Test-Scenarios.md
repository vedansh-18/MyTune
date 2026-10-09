# Test Scenarios — MyTune Android Application

**Document Version**: 1.0  
**Status**: Baseline / Ready for Execution  
**Project**: MyTune Local Android Music Player  

---

## Overview

This document defines the comprehensive suite of test scenarios covering all functional and non-functional requirements of the MyTune application. Test scenarios are organized by application module.

---

## 1. Permissions & App Startup Module (PERM)

| Scenario ID | Requirement ID | Module | Scenario Description | Priority | Test Type |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TS-PERM-001** | FR-001, PR-001 | Permissions | Verify runtime audio storage permission prompt on Android 13+ (API 33+) | High | Permission / Sanity |
| **TS-PERM-002** | FR-001, PR-001 | Permissions | Verify runtime notification permission prompt on Android 13+ (API 33+) | High | Permission |
| **TS-PERM-003** | FR-001, PR-001 | Permissions | Verify runtime legacy storage permission prompt on Android 12 & below (API < 33) | High | Permission / Compatibility |
| **TS-PERM-004** | FR-001 | Permissions | Verify application behavior and toast message when storage permission is denied | High | Negative / Permission |

---

## 2. Media Scanning & Filtering Module (SCAN)

| Scenario ID | Requirement ID | Module | Scenario Description | Priority | Test Type |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TS-SCAN-001** | FR-002 | Media Scanning | Verify automatic audio scanning via MediaStore on initial application launch | High | Functional / Integration |
| **TS-SCAN-002** | FR-003 | Media Scanning | Verify fallback direct file system scanning when MediaStore returns no audio files | Medium | Functional / Fallback |
| **TS-SCAN-003** | FR-004 | Media Scanning | Verify that only files with supported extensions (`.mp3`, `.m4a`, `.wav`, `.flac`, `.aac`, `.ogg`, `.opus`, `.wma`) are scanned | High | Boundary / Functional |
| **TS-SCAN-004** | FR-005, BR-002 | Media Scanning | Verify that audio files and folders containing call recording keywords are automatically filtered out | High | Functional / Negative |
| **TS-SCAN-005** | FR-004 | Media Scanning | Verify that non-audio files (`.txt`, `.jpg`, `.pdf`, `.mp4`) are ignored during scanning | Medium | Negative |

---

## 3. Main Navigation & List UI Module (NAV)

| Scenario ID | Requirement ID | Module | Scenario Description | Priority | Test Type |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TS-NAV-001** | FR-006, UIR-001 | Main UI | Verify display of scanned tracks in "All Songs" tab with extension suffixes removed | High | UI / Functional |
| **TS-NAV-002** | FR-007 | Main UI | Verify tab navigation between "All Songs" and "Playlists" via top action buttons | Medium | UI / Functional |
| **TS-NAV-003** | FR-007 | Main UI | Verify tab navigation between "All Songs" and "Playlists" via horizontal swipe gestures | Medium | UI / Usability |
| **TS-NAV-004** | FR-011, UIR-001 | Main UI | Verify playlist detail view header update and back arrow display upon selecting a playlist | High | UI / Functional |

---

## 4. Playlist Management Module (PLM)

| Scenario ID | Requirement ID | Module | Scenario Description | Priority | Test Type |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TS-PLM-001** | FR-008, FR-009 | Playlist | Verify creation of a new playlist with user-defined name and selected tracks | High | Functional |
| **TS-PLM-002** | FR-008 | Playlist | Verify validation error toast when attempting to create a playlist with an empty name | Medium | Negative / Validation |
| **TS-PLM-003** | FR-010, DR-001 | Playlist | Verify listing of saved playlists with correct track count in "Playlists" tab | High | Functional |
| **TS-PLM-004** | FR-015 | Playlist | Verify adding a single song from "All Songs" tab to an existing playlist via long-press menu | High | Functional |
| **TS-PLM-005** | FR-015 | Playlist | Verify prevention of adding duplicate track to a playlist | Medium | Negative / Functional |
| **TS-PLM-006** | FR-012 | Playlist | Verify reordering tracks (Move Up / Move Down) within a playlist detail view | Medium | Functional |
| **TS-PLM-007** | FR-013 | Playlist | Verify removing an individual track from a playlist via long-press menu | High | Functional |
| **TS-PLM-008** | FR-014 | Playlist | Verify deletion of an entire playlist after confirming deletion dialog | High | Functional |
| **TS-PLM-009** | DR-002 | Playlist | Verify that missing/deleted file paths on disk are automatically stripped when loading a playlist | High | Boundary / Data |

---

## 5. Playback Engine & Full Player Module (PLAY)

| Scenario ID | Requirement ID | Module | Scenario Description | Priority | Test Type |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TS-PLAY-001** | FR-016 | Playback | Verify launching `PlaySong` activity and starting playback when a track item is tapped | High | Functional / Integration |
| **TS-PLAY-002** | FR-017 | Playback | Verify Play/Pause button toggle state during active playback and pause states | High | Functional / UI |
| **TS-PLAY-003** | FR-017 | Playback | Verify Next track button playback advancing and wrap-around from last track to first track | High | Functional / Boundary |
| **TS-PLAY-004** | FR-017 | Playback | Verify Previous track button playback rewinding and wrap-around from first track to last track | High | Functional / Boundary |
| **TS-PLAY-005** | FR-017, FR-020 | Playback | Verify Seekbar dragging and position updating during active playback | High | Functional |
| **TS-PLAY-006** | FR-020 | Playback | Verify accuracy of elapsed (`txtCurrentTime`) and total duration (`txtTotalTime`) time label formatting (`M:SS`) | Medium | UI / Functional |
| **TS-PLAY-007** | FR-018 | Playback | Verify Autoplay ON behavior (automatically playing next track when current track completes) | High | Functional |
| **TS-PLAY-008** | FR-018 | Playback | Verify Autoplay OFF behavior (pausing playback when current track completes) | High | Functional |
| **TS-PLAY-009** | FR-019 | Playback | Verify embedded album artwork extraction and rendering in `PlaySong` screen | Medium | Functional / UI |
| **TS-PLAY-010** | FR-019 | Playback | Verify fallback artwork icon display when audio file contains no embedded album artwork | Medium | UI / Fallback |
| **TS-PLAY-011** | UIR-002 | Playback | Verify marquee horizontal title scrolling for long song titles in `PlaySong` | Low | UI / Animation |

---

## 6. Mini-Player Bar Module (MINI)

| Scenario ID | Requirement ID | Module | Scenario Description | Priority | Test Type |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TS-MINI-001** | FR-021 | Mini-Player | Verify mini-player bar visibility at bottom of `MainActivity` when track is prepared | High | Functional / UI |
| **TS-MINI-002** | FR-021 | Mini-Player | Verify mini-player Play/Pause button toggle action | High | Functional |
| **TS-MINI-003** | FR-021 | Mini-Player | Verify mini-player Close button action (stopping playback and hiding mini-player bar) | High | Functional |
| **TS-MINI-004** | FR-021 | Mini-Player | Verify tapping mini-player bar opens `PlaySong` activity | High | UI / Navigation |

---

## 7. Service & Notification Module (SVC)

| Scenario ID | Requirement ID | Module | Scenario Description | Priority | Test Type |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **TS-SVC-001** | FR-022 | Service | Verify background music playback persistence when `MainActivity` is sent to background | High | System / Service |
| **TS-SVC-002** | FR-022 | Notification | Verify `MediaStyle` notification creation with track title and controls in status bar | High | System / Notification |
| **TS-SVC-003** | FR-022 | Notification | Verify interactive Play/Pause, Next, and Previous control buttons on notification | High | Functional / Notification |
| **TS-SVC-004** | FR-023 | Notification | Verify tapping media notification returns user to `PlaySong` activity | High | Navigation |
| **TS-SVC-005** | FR-024, NFR-003 | Service | Verify stopping playback, releasing resources, and removing notification when app is swiped away from recent tasks | High | System / Resource |
