# Guidelines for Working with Watermark Enhancement Tasks

This document provides instructions and best practices for working with the task list in `docs/tasks.md` for the Watermark Enhancement project.

## Task List Structure

The task list in `docs/tasks.md` is organized hierarchically by implementation phases:

1. **Phase 1: Model and Basic UI Changes** - Foundation work for the enhanced watermark functionality
2. **Phase 2: Core Functionality Implementation** - Basic watermark enhancements
3. **Phase 3: Advanced Text Rendering** - Complex text styling features
4. **Phase 4: Additional Features** - Supplementary watermark enhancements
5. **Testing** - Comprehensive testing strategy
6. **Documentation and Deployment** - User documentation and release preparation

Each phase contains multiple task groups, and each group contains individual tasks. The hierarchical numbering system (e.g., 1.1.2) helps track the relationship between tasks.

## How to Mark Tasks as Completed

Tasks in the list use checkbox notation:
- Uncompleted task: `- [ ] Task description`
- Completed task: `- [x] Task description`

To mark a task as completed:
1. Open `docs/tasks.md`
2. Find the task you've completed
3. Change `[ ]` to `[x]` to mark it as done
4. Commit the change with a descriptive message (e.g., "Mark task 1.1.2 as completed")

## Task Completion Guidelines

Follow these guidelines when completing tasks:

1. **Complete Prerequisites First**: Ensure all prerequisite tasks are completed before starting dependent tasks.
2. **Complete Parent Tasks Last**: Only mark a parent task (e.g., 1.1) as completed when all its subtasks (e.g., 1.1.1, 1.1.2, etc.) are completed.
3. **Verify Completion**: Before marking a task as completed, verify that it meets all requirements and passes any relevant tests.
4. **Document Completion**: Add a brief comment in the commit message describing what was done to complete the task.

