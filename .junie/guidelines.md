# Stirling-PDF Project Guidelines

This document outlines the coding conventions, patterns, and task management practices used in the Stirling-PDF project. Following these guidelines ensures consistency across the codebase and makes it easier for contributors to understand and maintain the code.

## Development guidelines

The development guidelines are listed in `DeveloperGuide.md` file.

## Task Management

### Task List Structure
- Task lists are maintained in the `docs/tasks.md` file
- Tasks are organized hierarchically with main tasks and subtasks
- Each task has a unique identifier (e.g., 1, 1.1, 1.2, etc.)
- Tasks are grouped into logical sections based on the project area (Backend, Frontend, Testing, etc.)

### Task Status Tracking
- Tasks are marked with checkboxes:
    - `[ ]` indicates a task that has not been started or is in progress
    - `[x]` indicates a task that has been completed
- A parent task should only be marked as completed when all its subtasks are completed
- When updating task status, commit the changes with a descriptive message that references the task ID
