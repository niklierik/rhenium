# 01: A while loop runs

**Spec:** [../spec.md](../spec.md)

**What to build:** A programmer can write `while (condition) { ... }` and the program compiles, links
and runs, repeating the body while the condition holds. `let i = 0; while (i < 3) { println i; i = i + 1; }`
prints `0`, `1`, `2`. Loops nest to any depth, and an empty body is legal.

The emitted C is a real `while` loop, so the generated file has the same shape as the source.

The condition's type is **not** checked yet: `while (1) { }` is accepted and emits `while (1)`, which C
happens to take. That is knowingly wrong and is what ticket 02 fixes — this ticket is the tracer bullet
that proves a loop travels the whole pipeline.

**Blocked by:** None (can start immediately)

**Status:** resolved

- [x] `while` lexes as its own token, named consistently with the existing keyword tokens
- [x] A while statement is a parser rule holding a parenthesised expression and the existing `block`
      rule, listed as an alternative of `statement`, so nesting, every existing statement kind and the
      empty body come for free; a braceless body is a syntax error from the parser
- [x] A while statement node exists in the AST, holding its condition as an expression and its body as
      a block statement, with a semantic context of its own, built by the statement visitor, whose
      condition's and body's diagnostics accumulate rather than short-circuiting
- [x] A decorator for the loop decorates the condition and the body against the enclosing scope — with
      no check on the condition's type for now — and is reached from the statement dispatcher; the loop
      creates no scope of its own, the body's block does
- [x] A while action exists, holding its body as a block action, lowered from the node
- [x] The action prints as `while(`, the condition, `)`, then the body, inline, matching the dense
      style of the existing output; the loop writes the parentheses and never a brace of its own
- [x] The new action kind has its branch in the transpiler's exhaustive dispatch, and the Dagger
      bindings exist in each module it was added to
- [x] Tested at all four seams: a loop and a nested loop render in the AST s-expression, a loop program
      produces no diagnostics, the lowered actions render including an empty body and nesting, and the
      emitted C is pinned from a hand-built action
- [x] Verified by hand: `let i = 0; while (i < 3) { println i; i = i + 1; }` compiles, links and prints
      `0`, `1`, `2`
- [x] No program the grammar accepts reaches an unhandled-parse-rule diagnostic or an exception
- [x] The build is green and no existing behaviour changed
