# 02: A loop control statement outside a loop is rejected

**Spec:** [../spec.md](../spec.md)

**What to build:** `break;` or `continue;` anywhere but inside a loop body is reported as
`'break' is not inside a loop.` (or `'continue' ...`), at the statement, instead of reaching `clang`.
Inside a loop body it stays legal however deeply it is nested in blocks and if statements.

**Blocked by:** 01

**Status:** ready-for-agent

- [ ] `Scope` carries a `Boolean` loop marker set at creation, and answers whether it or any scope
      enclosing it is a loop scope
- [ ] The while decorator decorates its body under a child scope created with the marker set; the
      statement decorator context is unchanged
- [ ] `LoopControlOutsideLoop` exists in its own file, implements `ContextDiagnostic`, and names the
      keyword
- [ ] Analyzer cases: accepted directly in a body, inside an if in a body, inside a nested block in a
      body, inside a nested loop; rejected at top level for each keyword, inside a bare block with no
      loop, inside an if with no loop
- [ ] The build is green and no existing behaviour changed
