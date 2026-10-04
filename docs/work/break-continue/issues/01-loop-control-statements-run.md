# 01: Loop control statements run

**Spec:** [../spec.md](../spec.md)

**What to build:** A programmer can write `break;` and `continue;` inside a `while` loop and the
program compiles, links and runs: `break` ends the innermost loop, `continue` re-evaluates its
condition. The example in the spec prints `1`, `3`, `4`.

Placement is **not** checked yet: a top-level `break;` is accepted and only fails at `clang`. That is
knowingly wrong and is what ticket 02 fixes — this ticket is the tracer bullet that proves both
statements travel the whole pipeline.

**Blocked by:** None (can start immediately)

**Status:** resolved

- [x] `break` and `continue` lex as reserved keyword tokens, named consistently with the existing ones
- [x] Each is its own parser rule, the keyword followed by a semicolon, listed as an alternative of
      `statement`
- [x] A break statement node and a continue statement node exist in the AST, each with a semantic
      context of its own, built by the statement visitor
- [x] A decorator for each is reached from the statement dispatcher, checking nothing yet
- [x] `BreakAction` and `ContinueAction` exist as `data object`s, lowered from the nodes
- [x] They print as `break;` and `continue;`, each with its branch in the transpiler's exhaustive
      dispatch, and the Dagger bindings exist in each module they were added to
- [x] Tested at all four seams: both nodes render in the AST s-expression, a loop using both produces
      no diagnostics, both actions render inside a while action's body including inside a nested if,
      and the emitted C is pinned from hand-built actions
- [x] Verified by hand: the spec's example compiles, links and prints `1`, `3`, `4`
- [x] The build is green and no existing behaviour changed
