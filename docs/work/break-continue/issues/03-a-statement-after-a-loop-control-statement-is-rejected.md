# 03: A statement after a loop control statement is rejected

**Spec:** [../spec.md](../spec.md)

**What to build:** A statement following a loop control statement in the same block is reported as
`unreachable statement after 'break' at 2:5.`, pointing at the first such statement. An error for now;
the [warnings spec](../../warnings/spec.md) is where it becomes a warning.

**Blocked by:** 01, 02

**Status:** resolved

- [x] `UnreachableStatement` exists in its own file, implements `ContextDiagnostic`, and names the
      loop control statement's keyword and location
- [x] The block statement decorator reports it once per block, at the first statement after a loop
      control statement, and still decorates every statement in the block
- [x] A loop control statement outside a loop suppresses the check, pinned by a test
- [x] Analyzer cases: one statement after `break` rejected; two after `continue` give one diagnostic;
      an error inside a dead statement still reported; a statement after an if whose branches all
      `break` accepted; a loop control statement as the last statement accepted
- [x] The roadmap's "What the compiler implements today" describes `break` and `continue`, and the
      spec is marked resolved
- [x] The build is green and no existing behaviour changed
