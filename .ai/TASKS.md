# TASKS

Version: 2.0
Status: Active
Last Updated: YYYY-MM-DD
Owner: Project Team
AIOS Version: 2.0

---

# Purpose

This document is the canonical backlog for the project.

Every AI agent must consult this document before starting any implementation.

It defines:

- Current priorities
- Pending work
- Completed work
- Blocked work
- Dependencies
- Task ownership
- Execution order

If this document conflicts with chat instructions, the user instructions always take precedence.

---

# Responsibilities

This document should answer:

What should be done?

What is being done?

What has already been completed?

What is blocked?

What should never be done?

---

# Update Rules

Every AI agent MUST update this file whenever:

✔ A task starts

✔ A task finishes

✔ A task becomes blocked

✔ Priority changes

✔ Scope changes

Never delete historical tasks.

Never renumber task IDs.

Never overwrite completed history.

---

# Task Lifecycle

Every task follows exactly one lifecycle.

BACKLOG

↓

READY

↓

IN_PROGRESS

↓

REVIEW

↓

TESTING

↓

DONE

or

BLOCKED

or

CANCELLED

Never skip states.

---

# Priority Levels

P0

Critical

Project cannot continue.

P1

High

Current milestone.

P2

Normal

Planned work.

P3

Low

Improvement.

P4

Future

Ideas.

---

# Task Categories

FEATURE

BUG

REFACTOR

TEST

DOCUMENTATION

PERFORMANCE

SECURITY

INFRASTRUCTURE

RESEARCH

RELEASE

---

# Task Template

Each task must follow this structure.

------------------------------------------------------------

ID

TASK-001

Title

Implement Login Screen

Category

FEATURE

Priority

P1

Status

READY

Owner

Unassigned

Assigned Agent

GPT

Milestone

Core V1

Estimated Complexity

Medium

Dependencies

None

Blocked By

None

Files

ui/login/

domain/auth/

Description

Implement the login flow using the existing authentication API.

Acceptance Criteria

✔ Login screen opens

✔ Validation works

✔ Errors displayed

✔ Unit tests added

✔ Documentation updated

Definition of Done

Code compiles

Tests pass

Documentation updated

No TODO remaining

Notes

Authentication API already exists.

------------------------------------------------------------

---

# Backlog

## Current Sprint

TASK-001

Title

Implement Login Screen

Priority

P1

Status

READY

---

TASK-002

Title

Fix race condition

Priority

P0

Status

IN_PROGRESS

---

TASK-003

Title

Improve renderer performance

Priority

P2

Status

BACKLOG

---

# Completed

TASK-000

Initialize AIOS

Completed

2026-07-08

Agent

GPT

---

# Blocked

TASK-015

Reason

Waiting for Android API decision.

Blocked Since

2026-07-02

---

# Cancelled

TASK-021

Reason

Feature moved to future milestone.

---

# Execution Rules

AI agents should always select work in this order.

1.

Highest priority

↓

2.

Ready state

↓

3.

No unresolved dependency

↓

4.

Lowest estimated risk

↓

5.

Smallest scope first

Never start a blocked task.

Never start cancelled work.

Never duplicate completed work.

---

# Parallel Work Rules

Different AI agents may work simultaneously only if:

They modify different modules.

There are no shared files.

No shared public API.

Otherwise,

coordinate using SESSION.md.

---

# Dependency Rules

A task cannot start until every dependency is DONE.

Example

TASK-020

depends on

TASK-010

TASK-011

TASK-014

Only when all are DONE may TASK-020 start.

---

# Risk Assessment

Each task should declare

Low

Medium

High

Critical

Risk should consider

Architecture

Public API

Performance

Security

Compatibility

Testing

---

# Confidence

Each implementation must report

Confidence

High

Medium

Low

Reasons

Missing tests

No benchmark

Limited context

Environment limitations

---

# AI Recommendations

Agents may recommend:

New tasks

Task split

Task merge

Priority change

Dependency update

Milestone change

Recommendations never modify the backlog automatically.

---

# Anti-patterns

Never create duplicate tasks.

Never change completed history.

Never remove task IDs.

Never invent completed work.

Never mark DONE without verification.

Never reorder history.

---

# Related Documents

CURRENT_STATE.md

ROADMAP.md

SESSION.md

CHANGELOG_AI.md

AGENT_PROTOCOL.md

ARCHITECTURE.md

TESTING.md

---

# Changelog

2026-07-08

Initial AIOS v2 specification.