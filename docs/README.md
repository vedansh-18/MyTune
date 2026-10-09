# Documentation Index — MyTune

Welcome to the **MyTune** software documentation directory. This index provides structured access to all requirements, test plans, test specifications, traceability matrices, and quality assurance deliverables for the MyTune application.

---

## 📁 Documentation Structure

```text
docs/
├── README.md                          <- Documentation Index (This Document)
├── requirements/
│   └── SRS.md                         <- Software Requirements Specification
└── testing/
    ├── Test-Plan.md                   <- Master QA Test Plan
    ├── Test-Scenarios.md              <- High-Level Test Scenarios
    ├── Test-Cases.md                  <- Detailed Executable Test Cases
    ├── RTM.md                         <- Requirements Traceability Matrix
    ├── Defect-Report-Template.md      <- Defect Log Template
    ├── defects/                       <- Recorded Defect Logs Directory
    │   └── .gitkeep
    └── Test-Summary-Report.md         <- Final QA Summary Report (Post-Execution)
```

---

## 🔗 Quick Reference Links

1. **[Software Requirements Specification (SRS)](requirements/SRS.md)**  
   Defines all functional requirements (`FR-001` to `FR-024`), non-functional requirements (`NFR-001` to `NFR-004`), user interface specifications, data persistence rules, and permission constraints.

2. **[Master Test Plan](testing/Test-Plan.md)**  
   Outlines the QA objectives, scope, test environments, manual execution process, entry/exit criteria, and risk mitigation strategies.

3. **[Test Scenarios](testing/Test-Scenarios.md)**  
   Comprehensive high-level test scenarios mapped across Permissions, Media Scanning, Main UI, Playlist Management, Playback Engine, Mini-Player, and Service/Notification modules.

4. **[Detailed Test Cases](testing/Test-Cases.md)**  
   Individual test case specifications containing preconditions, test data, step-by-step instructions, and expected results. *(Status initially initialized as Not Executed)*.

5. **[Requirements Traceability Matrix (RTM)](testing/RTM.md)**  
   Maps every requirement ID to its corresponding Test Scenario, Test Case, and execution result to ensure 100% test coverage.

6. **[Defect Report Template](testing/Defect-Report-Template.md)**  
   Standardized template for logging actual defects discovered during testing execution.

7. **[Defects Directory](testing/defects/)**  
   Storage folder for individual defect report logs (`DEF-001.md`, etc.).

8. **[Test Summary Report](testing/Test-Summary-Report.md)**  
   Final test execution report compiled after manual test execution is completed.
