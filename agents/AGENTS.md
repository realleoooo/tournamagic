# AGENTS.md

## Purpose

This repository is optimized for working with Codex as an implementation partner.

## Working Agreement

- Explore before editing.
- Read the relevant docs in `docs/` before proposing architecture changes.
- When a task is larger than a quick fix, write or update a plan in `docs/plans/`.
- Prefer the smallest working slice first.
- Update docs when behavior or decisions change.
- Keep prompts short; use repository docs for detailed context.

## Where Context Lives

- Project profile: `docs/project-profile.md`
- Product goals: `docs/product-context.md`
- Architecture: `docs/architecture.md`
- Feature notes: `docs/features/`
- External systems: `docs/integrations/`
- Decisions: `docs/decisions/`
- Active implementation plans: `docs/plans/`
- Operational checklists: `docs/runbooks/`
- Active task list: `backlog/now.md`

## Default Task Flow

1. Read the relevant docs.
2. Inspect the current implementation.
3. Summarize behavior and propose a small plan.
4. Build after approval.
5. Verify with targeted checks.
6. Update docs if needed.

## 1. Plan Mode Default
- Enter plan mode for ANY non-trivial task (3+ steps or architectural decisions)
- If something goes sideways, STOP and re-plan immediately — don't keep pushing
- Use plan mode for verification steps, not just building
- Write detailed specs upfront to reduce ambiguity

## 2. Subagent Strategy
- Use subagents liberally to keep main context window clean
- Offload research, exploration, and parallel analysis to subagents
- For complex problems, throw more compute at it via subagents
- One task per subagent for focused execution

## 3. Self-Improvement Loop
- After ANY correction from the user: update `tasks/lessons.md` with the pattern
- Write rules for yourself that prevent the same mistake
- Ruthlessly iterate on these lessons until mistake rate drops
- Review lessons at session start for relevant project

## 4. Verification Before Done
- Never mark a task complete without proving it works
- Diff behavior between main and your changes when relevant
- Ask yourself: "Would a staff engineer approve this?"
- Run tests, check logs, demonstrate correctness

## 5. Demand Elegance (Balanced)
- For non-trivial changes: pause and ask "is there a more elegant way?"
- If a fix feels hacky: "Knowing everything I know now, implement the elegant solution"
- Skip this for simple, obvious fixes — don't over-engineer
- Challenge your own work before presenting it

## 6. Autonomous Bug Fixing
- When given a bug report: just fix it. Don’t ask for hand-holding
- Point at logs, errors, failing tests — then resolve them
- Zero context switching required from the user
- Go fix failing CI tests without being told how

## Task Management

1. **Plan First**: Write plan to `tasks/todo.md` with checkable items
2. **Verify Plan**: Check in before starting implementation
3. **Track Progress**: Mark items complete as you go
4. **Explain Changes**: High-level summary at each step
5. **Document Results**: Add review section to `tasks/todo.md`
6. **Capture Lessons**: Update `tasks/lessons.md` after corrections

## Core Principles

- **Simplicity First**: Make every change as simple as possible. Impact minimal code.
- **No Laziness**: Find root causes. No temporary fixes. Senior developer standards.