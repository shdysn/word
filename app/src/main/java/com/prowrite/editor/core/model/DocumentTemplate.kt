package com.prowrite.editor.core.model

data class DocumentTemplate(
    val id: String,
    val title: String,
    val category: String,
    val description: String,
    val initialContent: String
)

object TemplateRegistry {
    val templates = listOf(
        DocumentTemplate(
            id = "blank",
            title = "Blank Document",
            category = "General",
            description = "Start from scratch with a clean, empty canvas.",
            initialContent = ""
        ),
        DocumentTemplate(
            id = "resume",
            title = "Professional Resume",
            category = "Professional",
            description = "Modern single-column resume with profile, experience, and education.",
            initialContent = """
# JANE DOE
**Software Engineer & Technical Writer**
Email: jane.doe@example.com | Phone: +1 (555) 019-2834 | City, State

---

## PROFESSIONAL SUMMARY
Results-driven software developer with 5+ years of experience designing scalable mobile and cloud systems. Proven track record in Kotlin, Jetpack Compose, and modern architecture.

## WORK EXPERIENCE
### Senior Android Developer | TechCorp Inc.
*2022 - Present*
- Architected and delivered 4 high-profile Android mobile applications.
- Reduced memory overhead by 35% through reactive coroutines and flow optimization.
- Mentored junior engineers and instituted code review standards.

### Mobile Developer | DevSolutions LLC
*2019 - 2022*
- Built custom UI design systems with Jetpack Compose and Material 3.
- Integrated offline-first caching and background synchronization.

## EDUCATION
**B.S. in Computer Science**
State University, Graduated Magna Cum Laude

## SKILLS & PROFICIENCIES
- Languages: Kotlin, Java, Python, SQL
- Technologies: Jetpack Compose, Coroutines, Room, Git, CI/CD
            """.trimIndent()
        ),
        DocumentTemplate(
            id = "formal_letter",
            title = "Formal Letter",
            category = "Professional",
            description = "Business correspondence letter with sender, recipient, and closing.",
            initialContent = """
[Your Name]
[Your Street Address]
[City, State, ZIP]
[Your Email]
[Date]

[Recipient Name]
[Recipient Title / Department]
[Company Name]
[Company Address]

Dear [Recipient Name],

I am writing this letter to formally bring to your attention [Subject of the Letter]. Over the past quarter, our team has observed significant progress regarding [Project or Topic], and we believe the time is opportune to evaluate next steps.

Specifically, the following key items require consideration:
1. Resource allocation and timeline alignment.
2. Review of deliverables and compliance criteria.
3. Next milestone schedule and stakeholder review.

Thank you very much for your time and continued support. Please let me know your availability for a brief discussion later this week.

Sincerely,

[Your Name]
[Your Title]
            """.trimIndent()
        ),
        DocumentTemplate(
            id = "assignment",
            title = "Academic Assignment",
            category = "Academic",
            description = "Standard academic layout with title, student info, abstract, and references.",
            initialContent = """
# TITLE OF ASSIGNMENT

**Course:** Advanced Computer Science (CS 401)
**Instructor:** Dr. Alexander Vance
**Student:** Jane Doe (ID: 8492019)
**Submission Date:** October 10, 2026

---

## Abstract
This paper examines the computational efficiency of offline-first database synchronization in modern mobile clients. We compare relational SQLite caching against document-based remote persistence.

## 1. Introduction
Modern mobile applications increasingly require seamless functionality regardless of network conditions. Offline-first architectures guarantee continuous user productivity by making local storage the single source of truth.

## 2. Methodology
We conducted empirical benchmarks evaluating:
- Write throughput across local transactions
- Background sync latency upon network recovery
- Conflict resolution mechanisms

## 3. Results & Discussion
The experimental data demonstrates that local-first operations reduce user-perceived latency to under 16ms, eliminating UI frame drops.

## 4. Conclusion
Adopting structured local storage with reactive state listeners ensures optimal responsiveness and data integrity.

## References
[1] Gamma, E., et al. Design Patterns: Elements of Reusable Object-Oriented Software.
[2] Android Architecture Guidelines, Google Developers, 2026.
            """.trimIndent()
        ),
        DocumentTemplate(
            id = "research_notes",
            title = "Research Notes",
            category = "Academic",
            description = "Structured research notebook with hypotheses, observations, and citations.",
            initialContent = """
# RESEARCH NOTES: Study on Mobile Productivity Tools

**Date:** October 10, 2026
**Lead Researcher:** Jane Doe
**Status:** In Progress

---

## 1. Research Objective
Investigate how mobile document editing workflows differ between phone and tablet screen form factors.

## 2. Key Hypotheses
- Users on phone form factors prioritize rapid capture and minor edits.
- Tablet users utilize multi-column layouts, formatting toolbars, and keyboard shortcuts.

## 3. Observations & Field Notes
- Test Subject A: Completed 500-word draft in 12 minutes using formatting hotkeys.
- Test Subject B: Commended full-width canvas and instant word count visibility.

## 4. Key Takeaways
- Maintain zero latency during keystrokes.
- Keep formatting controls accessible within thumb reach on phone screens.
            """.trimIndent()
        ),
        DocumentTemplate(
            id = "meeting_notes",
            title = "Meeting Notes",
            category = "Business",
            description = "Comprehensive meeting minutes with attendees, agenda, decisions, and action items.",
            initialContent = """
# MEETING MINUTES: Sprint Planning & Product Roadmap

**Date & Time:** October 10, 2026 | 10:00 AM - 11:00 AM
**Meeting Chair:** Jane Doe
**Attendees:** Alex, Michael, Sarah, David

---

## Agenda
1. Review of Milestone 1 deliverables
2. Rich text editor performance benchmarks
3. Offline export and sharing verification
4. Next sprint commitments

## Key Decisions Made
- [x] Standardize on local SQLite storage with Flow streams for UI reactivity.
- [x] Provide native PDF printing and document sharing out of the box.
- [x] Enable Material 3 dynamic theming for Android 12+ devices.

## Action Items
- **Alex:** Finalize PDF renderer margin calculations by Wednesday.
- **Sarah:** Complete folder organization and multi-selection tests.
- **David:** Conduct accessibility audit for TalkBack compatibility.
            """.trimIndent()
        ),
        DocumentTemplate(
            id = "project_report",
            title = "Project Status Report",
            category = "Reports",
            description = "Executive report with overview, milestones, risks, and next steps.",
            initialContent = """
# PROJECT STATUS REPORT: Q4 ProWrite Rollout

**Project Lead:** Jane Doe
**Reporting Period:** October 2026
**Overall Health:** ON TRACK (Green)

---

## Executive Summary
Development of the ProWrite offline word processing platform has achieved all core milestones for Phase 1 and Phase 2. Core document manipulation, rich formatting tools, template library, and local persistence are operating reliably.

## Milestones Achieved
- **Milestone 1:** Architecture & SQLite database engine setup [Completed]
- **Milestone 2:** Responsive Home screen with list/grid layouts [Completed]
- **Milestone 3:** Document editor with live formatting and auto-save [Completed]
- **Milestone 4:** Export to PDF, TXT, HTML & System Print [Completed]

## Risks & Mitigations
- *Risk:* Large document memory footprints.
- *Mitigation:* Virtualized lazy layout canvas and debounced auto-saving.

## Next Steps
- Finalize automated Robolectric test coverage and user preferences persistence.
            """.trimIndent()
        )
    )
}
