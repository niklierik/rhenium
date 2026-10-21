# 01: An assignment action is a C expression

**Spec:** [../spec.md](../spec.md)

**What to build:** The prefactor from [ADR 0004](../../../adr/0004-an-assignment-action-is-a-c-expression.md).
An assignment action prints as `target=value` with no `;`, and an assignment statement, a compound
assignment and an increment or decrement statement each lower to an expression statement action
wrapping it, which supplies the `;`. Lowering a node as a statement and lowering it as a bare iteration
expression become separate entry points, so the for statement can use the bare form later. No program's
emitted C changes.

**Blocked by:** None (can start immediately)

**Status:** resolved

- [x] An assignment action prints with no trailing `;`, pinned at the transpiler seam
- [x] An expression statement action around an assignment action prints `target=value;`
- [x] Assignment, compound assignment and increment or decrement statements lower to an expression
      statement action around an assignment action; the existing lowering cases are updated to say so
- [x] The assignment-family and expression-statement lowerers can produce the bare action, without the
      expression statement wrapper, for a caller that is not lowering a statement
- [x] The emitted C for every existing program is unchanged, and the build is green
