# Test Cases — MyTune Android Application

**Document Version**: 1.1  
**Status**: Manual Execution Completed  
**Project**: MyTune Local Android Music Player  
**Execution Date**: 2026-09-19  

---

## 1. Permissions & App Startup Module (PERM)

### TC-PERM-001: Verify Audio Storage Permission Request on Android 13+ (API 33+)
* **Requirement ID**: FR-001, PR-001
* **Module**: Permissions
* **Test Scenario**: TS-PERM-001
* **Test Objective**: Ensure the app requests `READ_MEDIA_AUDIO` permission when launched for the first time on Android 13+.
* **Preconditions**: MyTune app installed on Android 13+ device/emulator; app permissions reset or freshly installed.
* **Test Data**: N/A
* **Test Steps**:
  1. Launch MyTune application from launcher.
  2. Observe system permission prompt.
* **Expected Result**: System permission dialog appears requesting permission to access audio files (`READ_MEDIA_AUDIO`).
* **Priority**: High
* **Test Type**: Permission / Functional
* **Actual Result**: System permission dialog appeared requesting access to audio files (`READ_MEDIA_AUDIO`). Permission granted and tracks loaded successfully regardless of notification permission status.
* **Status**: PASS
* **Remarks**: Verified on target test device. Defect DEF-001 verified resolved.

---

### TC-PERM-002: Verify Notification Permission Request on Android 13+ (API 33+)
* **Requirement ID**: FR-001, PR-001
* **Module**: Permissions
* **Test Scenario**: TS-PERM-002
* **Test Objective**: Ensure the app requests `POST_NOTIFICATIONS` permission on Android 13+.
* **Preconditions**: Fresh install on Android 13+ device.
* **Test Data**: N/A
* **Test Steps**:
  1. Launch MyTune application.
  2. Observe system permission prompts.
* **Expected Result**: System permission dialog appears requesting permission to post notifications (`POST_NOTIFICATIONS`).
* **Priority**: High
* **Test Type**: Permission
* **Actual Result**: `POST_NOTIFICATIONS` dialog appears first on startup before `READ_MEDIA_AUDIO`.
* **Status**: PASS
* **Remarks**: Dialog appears as expected. (Associated loading logic issue logged in DEF-001).

---

### TC-PERM-003: Verify Storage Permission Request on Android 12 & Below (API < 33)
* **Requirement ID**: FR-001, PR-001
* **Module**: Permissions
* **Test Scenario**: TS-PERM-003
* **Test Objective**: Ensure the app requests legacy `READ_EXTERNAL_STORAGE` permission on API < 33 devices.
* **Preconditions**: Fresh install on Android 11/12 device or emulator.
* **Test Data**: N/A
* **Test Steps**:
  1. Launch MyTune application.
  2. Observe permission prompt.
* **Expected Result**: System dialog requests access to device photos, media, and files (`READ_EXTERNAL_STORAGE`).
* **Priority**: High
* **Test Type**: Permission / Compatibility
* **Actual Result**: Legacy storage permission requested and granted.
* **Status**: PASS
* **Remarks**: Verified on target test device.

---

### TC-PERM-004: Verify App Behavior When Storage Permission is Denied
* **Requirement ID**: FR-001
* **Module**: Permissions
* **Test Scenario**: TS-PERM-004
* **Test Objective**: Verify graceful error notification when user denies storage permission.
* **Preconditions**: App installed; permission prompt displayed.
* **Test Data**: User action: Tap "Don't allow / Deny".
* **Test Steps**:
  1. Launch MyTune application.
  2. Tap "Deny" on the audio storage permission dialog.
* **Expected Result**: App displays a Toast message: `"Permission required to access audio files"`. No crash occurs; list view remains empty.
* **Priority**: High
* **Test Type**: Negative / Permission
* **Actual Result**: Toast message displayed as expected and track list remains empty. No crash observed.
* **Status**: PASS
* **Remarks**: Verified.

---

## 2. Media Scanning & Filtering Module (SCAN)

### TC-SCAN-001: Verify Automatic MediaStore Audio File Scanning
* **Requirement ID**: FR-002, FR-006
* **Module**: Media Scanning
* **Test Scenario**: TS-SCAN-001
* **Test Objective**: Verify that valid audio files stored in device storage are scanned and listed alphabetically by title.
* **Preconditions**: Storage permission granted; audio files (`SongA.mp3`, `SongB.m4a`) present in device media store.
* **Test Data**: Audio files: `Alpha.mp3`, `Beta.wav`, `Gamma.flac`, `apple.mp3`.
* **Test Steps**:
  1. Launch MyTune with storage permission granted.
  2. Inspect the "All Songs" tab list view.
* **Expected Result**: Scanned tracks appear in case-insensitive alphabetical order (`Alpha`, `apple`, `Beta`, `Gamma`). File extensions are stripped.
* **Priority**: High
* **Test Type**: Functional / Integration
* **Actual Result**: Audio tracks are scanned and listed in case-insensitive alphabetical order (`Alpha`, `apple`, `Beta`, `Gamma`). Extension suffixes are stripped.
* **Status**: PASS
* **Remarks**: Verified on test device. Defect DEF-002 verified resolved.

---

### TC-SCAN-002: Verify Fallback Recursive Directory Scanning
* **Requirement ID**: FR-003
* **Module**: Media Scanning
* **Test Scenario**: TS-SCAN-002
* **Test Objective**: Verify fallback recursive scanning when MediaStore returns no audio tracks.
* **Preconditions**: MediaStore indexing empty or bypassed; audio files placed in custom external storage subfolders.
* **Test Data**: File path: `/sdcard/Music/CustomSubfolder/Track1.mp3`.
* **Test Steps**:
  1. Launch MyTune under conditions where MediaStore returns no tracks.
  2. Inspect "All Songs" list view.
* **Expected Result**: `fetchSongsFromStorage()` recursively traverses directories and displays `Track1`.
* **Priority**: Medium
* **Test Type**: Functional / Fallback
* **Actual Result**: Subfolder recursive scanning confirmed working. Tracks inside nested subdirectories are successfully discovered and listed.
* **Status**: PASS
* **Remarks**: Verified by manual test execution.

---

### TC-SCAN-003: Verify Extension Filtering (Supported vs Unsupported Formats)
* **Requirement ID**: FR-004
* **Module**: Media Scanning
* **Test Scenario**: TS-SCAN-003
* **Test Objective**: Verify that supported audio formats are included and unsupported formats/files are ignored.
* **Preconditions**: Folder containing `song.mp3`, `track.m4a`, `audio.wav`, `lossless.flac`, `clip.aac`, `music.ogg`, `speech.opus`, `audio.wma`, plus `document.pdf`, `video.mp4`, `image.jpg`.
* **Test Data**: Mixed media directory.
* **Test Steps**:
  1. Place mixed files in music directory.
  2. Open MyTune and grant storage permission.
  3. Inspect "All Songs" list.
* **Expected Result**: All 8 supported audio extensions appear in the list. Non-audio files (`.pdf`, `.mp4`, `.jpg`) are completely excluded.
* **Priority**: High
* **Test Type**: Boundary / Functional
* **Actual Result**: All 8 supported extensions (.mp3, .m4a, .wav, .flac, .aac, .ogg, .opus, .wma) scanned and displayed properly. Non-audio files excluded.
* **Status**: PASS
* **Remarks**: Verified. (.3gp format support removed from project scope).

---

### TC-SCAN-004: Verify Automatic Call Recording Keyword Exclusion Filter
* **Requirement ID**: FR-005, BR-002
* **Module**: Media Scanning
* **Test Scenario**: TS-SCAN-004
* **Test Objective**: Verify that files or directories containing specified call recording keywords are filtered out.
* **Preconditions**: Storage contains `Call_Recording_2026.mp3`, `my_voice_rec.m4a`, `SoundRecorder_01.wav`, and standard `FavoriteSong.mp3`.
* **Test Data**: Keywords: `call`, `recording`, `call_rec`, `soundrecorder`, `voice_rec`, `callrecord`, `voicerecorder`.
* **Test Steps**:
  1. Place test files in storage.
  2. Launch MyTune and refresh track list.
* **Expected Result**: Only `FavoriteSong` appears in the list. `Call_Recording_2026`, `my_voice_rec`, and `SoundRecorder_01` are excluded.
* **Priority**: High
* **Test Type**: Functional / Negative
* **Actual Result**: Files/folders matching specified keywords filtered out correctly.
* **Status**: PASS
* **Remarks**: Verified.

---

## 3. Main Navigation & List UI Module (NAV)

### TC-NAV-001: Verify Tab Bar Navigation (All Songs vs Playlists)
* **Requirement ID**: FR-007
* **Module**: Main UI
* **Test Scenario**: TS-NAV-002
* **Test Objective**: Ensure tapping tab buttons switches list view state and updates button highlight styles.
* **Preconditions**: App launched on main screen.
* **Test Data**: Tab taps: "Playlists", "All Songs".
* **Test Steps**:
  1. Tap "Playlists" button in top tab layout.
  2. Verify list displays saved playlists or empty state text.
  3. Tap "All Songs" button.
  4. Verify list displays all scanned audio tracks.
* **Expected Result**: "Playlists" tab highlights in blue (`#2F2FE4`); list updates accordingly. Tapping "All Songs" switches back smoothly.
* **Priority**: Medium
* **Test Type**: UI / Functional
* **Actual Result**: Tab buttons switch view state smoothly; tab highlights update correctly.
* **Status**: PASS
* **Remarks**: Verified on device.

---

### TC-NAV-002: Verify Swipe Gesture Navigation Between Tabs
* **Requirement ID**: FR-007
* **Module**: Main UI
* **Test Scenario**: TS-NAV-003
* **Test Objective**: Verify horizontal swipe gestures on list view switch tabs.
* **Preconditions**: App on main screen in "All Songs" tab.
* **Test Data**: Swipe left gesture (fling).
* **Test Steps**:
  1. Perform horizontal swipe left across the ListView.
  2. Observe tab transition to "Playlists".
  3. Perform horizontal swipe right across the ListView.
  4. Observe tab transition back to "All Songs".
* **Expected Result**: Left fling switches view to "Playlists" tab; right fling switches back to "All Songs" tab.
* **Priority**: Medium
* **Test Type**: UI / Usability
* **Actual Result**: Horizontal swipe left/right gestures switch tabs smoothly.
* **Status**: PASS
* **Remarks**: Verified.

---

## 4. Playlist Management Module (PLM)

### TC-PLM-001: Verify Create Playlist Flow
* **Requirement ID**: FR-008, FR-009, FR-010
* **Module**: Playlist
* **Test Scenario**: TS-PLM-001
* **Test Objective**: Verify creating a new playlist with selected tracks.
* **Preconditions**: At least 3 songs scanned in "All Songs".
* **Test Data**: Playlist Name: `"Rock Classics"`. Selected tracks: `Track 1`, `Track 2`.
* **Test Steps**:
  1. Tap '+' icon (`btnCreatePlaylist`) in header.
  2. Enter `"Rock Classics"` in text prompt and tap "Next".
  3. Check checkboxes for `Track 1` and `Track 2` in multi-choice dialog.
  4. Tap "Save Playlist".
  5. Switch to "Playlists" tab.
* **Expected Result**: Toast `"Playlist 'Rock Classics' saved!"` appears. "Playlists" tab displays `"Rock Classics (2 tracks)"`.
* **Priority**: High
* **Test Type**: Functional
* **Actual Result**: Playlist created successfully with selected tracks and persisted in SharedPreferences.
* **Status**: PASS
* **Remarks**: Verified across app restarts.

---

### TC-PLM-002: Verify Validation when Creating Playlist with Empty Name
* **Requirement ID**: FR-008
* **Module**: Playlist
* **Test Scenario**: TS-PLM-002
* **Test Objective**: Ensure playlist creation prevents blank playlist names.
* **Preconditions**: Create Playlist dialog open.
* **Test Data**: Empty string `""` or whitespace `"   "`.
* **Test Steps**:
  1. Tap '+' icon in header.
  2. Leave text input empty and tap "Next".
* **Expected Result**: App displays Toast `"Playlist name cannot be empty"`. Multi-choice song selection dialog is not shown.
* **Priority**: Medium
* **Test Type**: Negative / Validation
* **Actual Result**: Toast displayed as expected and blank playlist creation blocked.
* **Status**: PASS
* **Remarks**: Verified.

---

### TC-PLM-003: Verify Adding Single Track to Existing Playlist
* **Requirement ID**: FR-015
* **Module**: Playlist
* **Test Scenario**: TS-PLM-004
* **Test Objective**: Verify long-press on track in "All Songs" to add to an existing playlist.
* **Preconditions**: Playlist `"Favorites"` exists.
* **Test Data**: Long-press target: `Track 3`.
* **Test Steps**:
  1. In "All Songs" tab, long-press `Track 3`.
  2. Tap `"Favorites"` in the "Add to Playlist" dialog.
  3. Open `"Favorites"` playlist detail view.
* **Expected Result**: Toast `"Added to 'Favorites'"` appears. `Track 3` is listed inside `"Favorites"`.
* **Priority**: High
* **Test Type**: Functional
* **Actual Result**: Song added to playlist successfully; duplicates prevented.
* **Status**: PASS
* **Remarks**: Verified.

---

### TC-PLM-004: Verify Reordering Tracks Within Playlist (Move Up / Move Down)
* **Requirement ID**: FR-012
* **Module**: Playlist
* **Test Scenario**: TS-PLM-006
* **Test Objective**: Verify changing track order inside a playlist detail view.
* **Preconditions**: Playlist `"MyList"` contains `Song A` (pos 0) and `Song B` (pos 1).
* **Test Data**: Move `Song B` up.
* **Test Steps**:
  1. Open `"MyList"` playlist detail view.
  2. Long-press `Song B` (position 1).
  3. Select `"⬆️ Move Up"` from dialog.
* **Expected Result**: `Song B` moves to position 0; `Song A` moves to position 1. List updates immediately.
* **Priority**: Medium
* **Test Type**: Functional
* **Actual Result**: Move Up and Move Down operations reorder tracks correctly.
* **Status**: PASS
* **Remarks**: Verified.

---

### TC-PLM-005: Verify Track Removal from Playlist
* **Requirement ID**: FR-013
* **Module**: Playlist
* **Test Scenario**: TS-PLM-007
* **Test Objective**: Verify removing a track from a playlist via long-press options.
* **Preconditions**: Playlist `"MyList"` contains `Song A` and `Song B`.
* **Test Data**: Remove `Song A`.
* **Test Steps**:
  1. Open `"MyList"` detail view.
  2. Long-press `Song A`.
  3. Select `"❌ Remove from Playlist"`.
* **Expected Result**: `Song A` is removed from `"MyList"`. Track list count updates to 1.
* **Priority**: High
* **Test Type**: Functional
* **Actual Result**: Track removed from playlist correctly.
* **Status**: PASS
* **Remarks**: Verified.

---

### TC-PLM-006: Verify Delete Playlist Flow
* **Requirement ID**: FR-014
* **Module**: Playlist
* **Test Scenario**: TS-PLM-008
* **Test Objective**: Verify deleting a playlist from the "Playlists" tab.
* **Preconditions**: Playlist `"TempList"` exists.
* **Test Data**: Confirm deletion.
* **Test Steps**:
  1. Open "Playlists" tab.
  2. Long-press `"TempList"`.
  3. Tap `"Delete"` in confirmation dialog.
* **Expected Result**: `"TempList"` is deleted from `SharedPreferences` and removed from list view.
* **Priority**: High
* **Test Type**: Functional
* **Actual Result**: Playlist deleted successfully upon confirmation.
* **Status**: PASS
* **Remarks**: Verified.

---

## 5. Playback Engine & Full Player Module (PLAY)

### TC-PLAY-001: Verify Track Playback Launch
* **Requirement ID**: FR-016
* **Module**: Playback
* **Test Scenario**: TS-PLAY-001
* **Test Objective**: Verify tapping a track in list view launches `PlaySong` and starts audio playback.
* **Preconditions**: App on "All Songs" tab.
* **Test Data**: Tap `Track 1`.
* **Test Steps**:
  1. Tap `Track 1` in list view.
  2. Observe activity transition to `PlaySong`.
* **Expected Result**: `PlaySong` screen opens; `Track 1` title displays; audio begins playing; play/pause button shows pause icon.
* **Priority**: High
* **Test Type**: Functional / Integration
* **Actual Result**: PlaySong activity opens and audio playback starts immediately.
* **Status**: PASS
* **Remarks**: Verified.

---

### TC-PLAY-002: Verify Play/Pause Toggle Controls
* **Requirement ID**: FR-017
* **Module**: Playback
* **Test Scenario**: TS-PLAY-002
* **Test Objective**: Verify toggling play/pause button state in `PlaySong`.
* **Preconditions**: Audio currently playing in `PlaySong`.
* **Test Data**: Button press: Play/Pause.
* **Test Steps**:
  1. Tap `btnPlay` while audio is playing.
  2. Observe audio output and icon.
  3. Tap `btnPlay` again.
* **Expected Result**: Audio pauses and icon switches to Play. Tapping again resumes audio and icon switches to Pause.
* **Priority**: High
* **Test Type**: Functional / UI
* **Actual Result**: Audio output pauses/resumes correctly and icon toggles between Play and Pause.
* **Status**: PASS
* **Remarks**: Verified.

---

### TC-PLAY-003: Verify Next Track Wrap-Around Behavior
* **Requirement ID**: FR-017
* **Module**: Playback
* **Test Scenario**: TS-PLAY-003
* **Test Objective**: Verify Next button advances track and wraps around from last track to first track.
* **Preconditions**: Queue contains 3 songs (`Song 1`, `Song 2`, `Song 3`). `Song 3` currently playing.
* **Test Data**: Tap `btnNext`.
* **Test Steps**:
  1. Play `Song 3` (last track in queue).
  2. Tap `btnNext` icon.
* **Expected Result**: Playback advances to `Song 1` (index 0). Title updates to `Song 1`.
* **Priority**: High
* **Test Type**: Boundary / Functional
* **Actual Result**: Playback advances to next track and wraps around from last to first track.
* **Status**: PASS
* **Remarks**: Verified.

---

### TC-PLAY-004: Verify Previous Track Wrap-Around Behavior
* **Requirement ID**: FR-017
* **Module**: Playback
* **Test Scenario**: TS-PLAY-004
* **Test Objective**: Verify Previous button rewinds track and wraps around from first track to last track.
* **Preconditions**: Queue contains 3 songs (`Song 1`, `Song 2`, `Song 3`). `Song 1` currently playing.
* **Test Data**: Tap `btnPrevious`.
* **Test Steps**:
  1. Play `Song 1` (index 0).
  2. Tap `btnPrevious` icon.
* **Expected Result**: Playback wraps around to `Song 3` (last track in queue). Title updates to `Song 3`.
* **Priority**: High
* **Test Type**: Boundary / Functional
* **Actual Result**: Playback rewinds track and wraps around from first to last track.
* **Status**: PASS
* **Remarks**: Verified.

---

### TC-PLAY-005: Verify Seekbar Dragging and Position Updating
* **Requirement ID**: FR-017, FR-020
* **Module**: Playback
* **Test Scenario**: TS-PLAY-005
* **Test Objective**: Verify dragging seekbar updates audio playback position and elapsed time label.
* **Preconditions**: Audio track playing (duration 3:00).
* **Test Data**: Drag thumb to middle of seekbar (1:30 position).
* **Test Steps**:
  1. Drag seekbar thumb to ~50% position.
  2. Release seekbar.
* **Expected Result**: Audio playback instantly seeks to 1:30. `txtCurrentTime` displays `1:30`.
* **Priority**: High
* **Test Type**: Functional
* **Actual Result**: Audio seeks instantly to dragged position and time label updates accurately.
* **Status**: PASS
* **Remarks**: Verified.

---

### TC-PLAY-006: Verify Autoplay ON Mode (Auto-Advance Queue)
* **Requirement ID**: FR-018
* **Module**: Playback
* **Test Scenario**: TS-PLAY-007
* **Test Objective**: Ensure Autoplay ON automatically plays next track when current track completes.
* **Preconditions**: Autoplay enabled (icon shows `ic_autoplay_on`); queue has `Song 1` and `Song 2`.
* **Test Data**: Seek `Song 1` to 2 seconds before end.
* **Test Steps**:
  1. Enable Autoplay.
  2. Seek `Song 1` to near completion.
  3. Wait for track to finish.
* **Expected Result**: Upon `Song 1` completion, `Song 2` begins playing automatically without user intervention.
* **Priority**: High
* **Test Type**: Functional
* **Actual Result**: Playback advances automatically to next track upon song completion.
* **Status**: PASS
* **Remarks**: Verified.

---

### TC-PLAY-007: Verify Autoplay OFF Mode (Pause on Track End)
* **Requirement ID**: FR-018
* **Module**: Playback
* **Test Scenario**: TS-PLAY-008
* **Test Objective**: Ensure Autoplay OFF pauses playback when current track completes.
* **Preconditions**: Autoplay disabled (icon shows `ic_autoplay_off`).
* **Test Data**: Seek track to 2 seconds before end.
* **Test Steps**:
  1. Tap `btnAutoplay` to turn Autoplay OFF. Toast `"Autoplay OFF..."` appears.
  2. Seek current track near completion and wait.
* **Expected Result**: When track finishes, playback pauses. Next track does NOT play automatically.
* **Priority**: High
* **Test Type**: Functional
* **Actual Result**: Playback pauses when track completes when Autoplay is OFF.
* **Status**: PASS
* **Remarks**: Verified.

---

### TC-PLAY-008: Verify Embedded Artwork Extraction vs Fallback Logo
* **Requirement ID**: FR-019
* **Module**: Playback
* **Test Scenario**: TS-PLAY-009, TS-PLAY-010
* **Test Objective**: Verify rendering embedded album cover vs fallback logo.
* **Preconditions**: Track A has embedded artwork; Track B has no artwork.
* **Test Data**: Play Track A, then Play Track B.
* **Test Steps**:
  1. Play Track A and observe `imageView4`.
  2. Play Track B and observe `imageView4`.
* **Expected Result**: Track A displays its custom embedded cover art. Track B displays fallback `music_logo.png`.
* **Priority**: Medium
* **Test Type**: Functional / UI
* **Actual Result**: Embedded cover art displayed for tagged files; fallback music logo rendered for untagged files.
* **Status**: PASS
* **Remarks**: Verified.

---

## 6. Mini-Player Bar Module (MINI)

### TC-MINI-001: Verify Mini-Player Visibility and Play/Pause Control
* **Requirement ID**: FR-021
* **Module**: Mini-Player
* **Test Scenario**: TS-MINI-001, TS-MINI-002
* **Test Objective**: Verify bottom mini-player bar appears on `MainActivity` and controls playback.
* **Preconditions**: Track playing in `PlaySong`. Navigate back to `MainActivity`.
* **Test Data**: Tap mini-player Play/Pause button.
* **Test Steps**:
  1. Press back button from `PlaySong` to return to `MainActivity`.
  2. Observe mini-player bar at bottom of main screen.
  3. Tap Play/Pause button on mini-player.
* **Expected Result**: Mini-player bar is visible with song title and artwork thumbnail. Tapping Play/Pause toggles audio playback state.
* **Priority**: High
* **Test Type**: Functional / UI
* **Actual Result**: Mini-player bar appears at bottom of screen and controls play/pause state correctly.
* **Status**: PASS
* **Remarks**: Verified.

---

### TC-MINI-002: Verify Mini-Player Close Button (Stop Playback)
* **Requirement ID**: FR-021
* **Module**: Mini-Player
* **Test Scenario**: TS-MINI-003
* **Test Objective**: Verify close button on mini-player stops playback and dismisses mini-player bar.
* **Preconditions**: Mini-player bar visible on `MainActivity`.
* **Test Data**: Tap `btnMiniClose` (Close icon).
* **Test Steps**:
  1. Tap Close button on mini-player bar.
* **Expected Result**: Playback stops, foreground notification is dismissed, and mini-player bar disappears (`View.GONE`).
* **Priority**: High
* **Test Type**: Functional
* **Actual Result**: Playback stops and mini-player bar disappears upon tapping Close button.
* **Status**: PASS
* **Remarks**: Verified.

---

## 7. Service & Notification Module (SVC)

### TC-SVC-001: Verify MediaStyle System Notification & Controls
* **Requirement ID**: FR-022, FR-023
* **Module**: Service / Notification
* **Test Scenario**: TS-SVC-002, TS-SVC-003, TS-SVC-004
* **Test Objective**: Verify system notification creation and status bar action buttons.
* **Preconditions**: Track playing; app sent to home screen.
* **Test Data**: Expand status bar drawer.
* **Test Steps**:
  1. Start playback and press Home button.
  2. Pull down notification drawer.
  3. Tap Next button on notification.
* **Expected Result**: `MediaStyle` notification is visible showing track title, Previous, Play/Pause, and Next buttons. Tapping Next advances track.
* **Priority**: High
* **Test Type**: System / Notification
* **Actual Result**: Media notification displays status bar controls; Prev/Play/Pause/Next buttons function as expected. Tapping notification opens PlaySong.
* **Status**: PASS
* **Remarks**: Verified.

---

### TC-SVC-002: Verify Task Removal Cleanup (onTaskRemoved)
* **Requirement ID**: FR-024, NFR-003
* **Module**: Service
* **Test Scenario**: TS-SVC-005
* **Test Objective**: Verify stopping playback and releasing resources when app is swiped away from recent apps.
* **Preconditions**: Music playing in background via `MusicService`.
* **Test Data**: Swipe away app from recent apps overview screen.
* **Test Steps**:
  1. Start playback and open Recent Apps task switcher.
  2. Swipe away MyTune app.
* **Expected Result**: Audio playback stops immediately, service terminates (`stopSelf()`), and media notification is cleared.
* **Priority**: High
* **Test Type**: System / Resource
* **Actual Result**: Swiping app away from recent tasks stops playback and removes status bar notification.
* **Status**: PASS
* **Remarks**: Verified.
