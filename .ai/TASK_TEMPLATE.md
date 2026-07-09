# TASK_TEMPLATE

Version: 2.0
Status: Active
Last Updated: YYYY-MM-DD
Owner: Project Team
AIOS Version: 2.0

---

# Purpose

This document defines the standard structure that every task must follow.

Every new task created in TASKS.md should be based on this template.

The objective is to ensure that all AI agents and human contributors receive the same level of context before implementation.

This document standardizes:

- Task structure
- Required information
- Acceptance criteria
- Validation
- Risks
- Dependencies
- Completion requirements

---

# General Rules

Every task MUST:

✔ Have a unique ID

✔ Define a single objective

✔ Be independently executable whenever possible

✔ Be testable

✔ Be reviewable

✔ Have a clear Definition of Done

✔ Include enough context for another agent to continue the work

---

# Task Template

----------------------------------------------------------------

ID

TASK-XXX

Title

Short descriptive title.

Example

TASK-014

Optimize renderer initialization

----------------------------------------------------------------

Category

One of:

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

----------------------------------------------------------------

Priority

P0

Critical

P1

High

P2

Normal

P3

Low

P4

Future

----------------------------------------------------------------

Status

BACKLOG

READY

IN_PROGRESS

REVIEW

TESTING

DONE

BLOCKED

CANCELLED

----------------------------------------------------------------

Milestone

Example

Core V1

Core V2

Release 1.0

Sprint 12

----------------------------------------------------------------

Assigned Agent

Example

GPT

Claude

Gemini

Codex

OpenCode

Human

Unassigned

----------------------------------------------------------------

Owner

Responsible person or team.

Example

Rendering Team

Infrastructure Team

Project Owner

----------------------------------------------------------------

Estimated Complexity

XS

S

M

L

XL

----------------------------------------------------------------

Estimated Effort

Optional

Example

2 hours

1 day

3 days

1 week

----------------------------------------------------------------

Description

Explain the objective clearly.

Describe:

What should happen.

Why it is necessary.

What problem it solves.

Avoid implementation details here.

----------------------------------------------------------------

Background

Describe relevant context.

Examples

Previous implementation

Related ADR

Known limitation

Architecture decision

GitHub issue

----------------------------------------------------------------

Scope

Explicitly define:

Included

Not included

Example

Included

Renderer initialization

Not included

Frame scheduling

Camera system

----------------------------------------------------------------

Dependencies

List required completed tasks.

Example

TASK-010

TASK-011

If none

None

----------------------------------------------------------------

Blocked By

List blocking factors.

Example

Android API

External library

Design approval

If none

None

----------------------------------------------------------------

Affected Modules

List every module.

Example

core

runtime

renderer

compose

----------------------------------------------------------------

Affected Files

Optional.

List expected files.

Example

Renderer.kt

Runtime.kt

CameraState.kt

----------------------------------------------------------------

Acceptance Criteria

Define observable success.

Example

✔ Renderer starts

✔ No crash

✔ Unit tests pass

✔ Existing API preserved

✔ Documentation updated

----------------------------------------------------------------

Definition of Done

A task is DONE only if:

✔ Code compiles

✔ Tests pass

✔ Documentation updated

✔ No TODO remains

✔ No warnings introduced

✔ Public API reviewed

✔ CHANGELOG updated

✔ SESSION updated

----------------------------------------------------------------

Validation

Describe how the result should be verified.

Example

Run unit tests

Launch sample application

Verify logs

Review API

Manual testing

----------------------------------------------------------------

Risk Level

Low

Medium

High

Critical

Explain why.

----------------------------------------------------------------

Regression Risk

Low

Medium

High

Describe possible regressions.

----------------------------------------------------------------

Confidence

High

Medium

Low

Explain reasons.

Example

No Android device available.

Benchmark not executed.

Limited project context.

----------------------------------------------------------------

Notes

Additional information.

Links

Ideas

References

Future improvements

----------------------------------------------------------------

Related Documents

AGENT_PROTOCOL.md

TASKS.md

SESSION.md

CHANGELOG_AI.md

ARCHITECTURE.md

TESTING.md

ROADMAP.md

CURRENT_STATE.md

----------------------------------------------------------------

Completion Summary

To be filled when task finishes.

Problem

Root Cause

Solution

Files Changed

Tests Executed

Performance Impact

Breaking Changes

Remaining Limitations

Next Recommendation

----------------------------------------------------------------

# Example

TASK-021

Title

Optimize Renderer Cache

Category

PERFORMANCE

Priority

P1

Status

READY

Milestone

Core V2

Assigned Agent

Claude

Description

Reduce repeated cache allocations during renderer initialization.

Acceptance Criteria

✔ Memory allocations reduced

✔ No behavior changes

✔ Existing tests pass

Definition of Done

All required checks completed.

Risk

Medium

Confidence

High

----------------------------------------------------------------

# AI Agent Instructions

Before starting

Read:

AGENT_PROTOCOL.md

CONTEXT.md

CURRENT_STATE.md

TASKS.md

Relevant architecture documents.

Never assume hidden requirements.

Never invent APIs.

Never skip validation.

Never mark a task as DONE without evidence.

----------------------------------------------------------------

# Common Mistakes

❌ Multiple objectives in one task

❌ Missing acceptance criteria

❌ Undefined dependencies

❌ No Definition of Done

❌ Missing validation

❌ Missing risk assessment

❌ Missing completion summary

----------------------------------------------------------------

# Changelog

YYYY-MM-DD

Initial AIOS v2 specification.
