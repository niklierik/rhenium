# 01: A block runs

**Spec:** [../spec.md](../spec.md)

**What to build:** A programmer can wrap statements in braces and the program still compiles, links and
runs. `let a = 1; { println a; }` prints `1`. Braces nest to any depth, and an empty block is legal.

The braces are real in the emitted C, so the generated file has the same shape as the source.

A block does **not** get a scope of its own yet: its declarations still live on after the closing brace.
That is knowingly wrong and is what ticket 02 fixes — this ticket is the tracer bullet that proves a
block travels the whole pipeline.

**Blocked by:** None (can start immediately)

**Status:** resolved

- [x] Braces lex as their own tokens, named consistently with the existing bracket pair
- [x] A block is a parser rule holding any number of statements, listed as an alternative of `statement`,
      so nesting and every existing statement kind come for free; the root rule is unchanged
- [x] A block statement node exists in the AST with a semantic context of its own, built by the statement
      visitor, whose children's diagnostics accumulate rather than short-circuiting
- [x] A decorator for the block walks its children — against the enclosing scope for now — and is reached
      from the statement dispatcher
- [x] A block action exists, lowered from the node, and prints braces around its body inline, matching
      the dense style of the existing output; the action that owns the mutable list of child actions
      keeps printing no braces
- [x] The new action kind has its branch in the transpiler's exhaustive dispatch, and the Dagger bindings
      exist in each module it was added to
- [x] Tested at all four seams: a nested and an empty block render in the AST s-expression, a nested
      program produces no diagnostics, the lowered actions render, and the emitted braces are pinned
- [x] Verified by hand: `let a = 1; { println a; }` compiles, links and prints `1`
- [x] No program the grammar accepts reaches an unhandled-parse-rule diagnostic or an exception
- [x] The build is green and no existing behaviour changed
