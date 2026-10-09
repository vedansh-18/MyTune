# Requirements Traceability Matrix (RTM) — MyTune

**Document Version**: 1.2  
**Status**: Executed & Fully Verified (100% Pass)  
**Project**: MyTune Local Android Music Player  
**Execution Date**: 2026-09-19  

---

## Traceability Mapping Table

| Requirement ID | Requirement Summary | Test Scenario ID | Test Case ID | Test Result |
| :--- | :--- | :--- | :--- | :--- |
| **FR-001** | Request audio & notification permissions | TS-PERM-001, TS-PERM-002, TS-PERM-003, TS-PERM-004 | TC-PERM-001, TC-PERM-002, TC-PERM-003, TC-PERM-004 | PASS ([DEF-001](defects/DEF-001.md) Resolved) |
| **FR-002** | Scan MediaStore for music tracks | TS-SCAN-001 | TC-SCAN-001 | PASS ([DEF-002](defects/DEF-002.md) Resolved) |
| **FR-003** | Fallback recursive storage scanning | TS-SCAN-002 | TC-SCAN-002 | PASS |
| **FR-004** | Supported audio extension filtering | TS-SCAN-003 | TC-SCAN-003 | PASS |
| **FR-005** | Call recording keyword exclusion filter | TS-SCAN-004 | TC-SCAN-004 | PASS |
| **FR-006** | Display all songs list with extension stripped | TS-NAV-001 | TC-SCAN-001 | PASS |
| **FR-007** | Tab button & swipe gesture navigation | TS-NAV-002, TS-NAV-003 | TC-NAV-001, TC-NAV-002 | PASS |
| **FR-008** | Create playlist with name & track selector | TS-PLM-001, TS-PLM-002 | TC-PLM-001, TC-PLM-002 | PASS |
| **FR-009** | SharedPreferences JSON playlist persistence | TS-PLM-001 | TC-PLM-001 | PASS |
| **FR-010** | List saved playlists with track count | TS-PLM-003 | TC-PLM-001 | PASS |
| **FR-011** | Open playlist detail view & back button | TS-NAV-004 | TC-PLM-001 | PASS |
| **FR-012** | Reorder playlist tracks (Move Up / Down) | TS-PLM-006 | TC-PLM-004 | PASS |
| **FR-013** | Remove track from playlist | TS-PLM-007 | TC-PLM-005 | PASS |
| **FR-014** | Delete playlist flow with confirmation | TS-PLM-008 | TC-PLM-006 | PASS |
| **FR-015** | Add single song from All Songs to playlist | TS-PLM-004, TS-PLM-005 | TC-PLM-003 | PASS |
| **FR-016** | Launch PlaySong & start playback on item tap | TS-PLAY-001 | TC-PLAY-001 | PASS |
| **FR-017** | Playback controls (Play, Pause, Prev, Next, Seek) | TS-PLAY-002, TS-PLAY-003, TS-PLAY-004, TS-PLAY-005 | TC-PLAY-002, TC-PLAY-003, TC-PLAY-004, TC-PLAY-005 | PASS |
| **FR-018** | Autoplay ON / OFF toggle mode | TS-PLAY-007, TS-PLAY-008 | TC-PLAY-006, TC-PLAY-007 | PASS |
| **FR-019** | Album artwork extraction & fallback logo | TS-PLAY-009, TS-PLAY-010 | TC-PLAY-008 | PASS |
| **FR-020** | 500ms seekbar & M:SS time update | TS-PLAY-006 | TC-PLAY-005 | PASS |
| **FR-021** | Bottom mini-player bar & controls | TS-MINI-001, TS-MINI-002, TS-MINI-003, TS-MINI-004 | TC-MINI-001, TC-MINI-002 | PASS |
| **FR-022** | Foreground service MediaStyle notification | TS-SVC-001, TS-SVC-002, TS-SVC-003 | TC-SVC-001 | PASS |
| **FR-023** | Tap notification to return to PlaySong | TS-SVC-004 | TC-SVC-001 | PASS |
| **FR-024** | Stop playback & clean up on task removed | TS-SVC-005 | TC-SVC-002 | PASS |
| **NFR-001** | 500ms smooth UI thread refresh | TS-PLAY-006 | TC-PLAY-005 | PASS |
| **NFR-002** | Complete offline operation | TS-PERM-001 | TC-PERM-001 | PASS |
| **NFR-003** | Resource cleanup on service stop | TS-SVC-005 | TC-SVC-002 | PASS |
| **NFR-004** | Material3 dark theme compliance | TS-NAV-001 | TC-NAV-001 | PASS |

---

## Execution Summary

* **Total Functional Requirements**: 24 (`FR-001` to `FR-024`)
* **Requirements Fully Passed**: 24 (100%)
* **Total Non-Functional Requirements**: 4 (`NFR-001` to `NFR-004`)
* **NFRs Fully Passed**: 4 (100%)
* **Total Test Cases Executed**: 22
* **Passed Test Cases**: 22 (100.0%)
* **Failed Test Cases**: 0 (0.0%)
* **Defects Logged & Resolved**: 2 ([DEF-001](defects/DEF-001.md) Resolved, [DEF-002](defects/DEF-002.md) Resolved)
* **Withdrawn Requirements**: 1 ([DEF-003](defects/DEF-003.md) Closed - .3gp format removed from scope)
