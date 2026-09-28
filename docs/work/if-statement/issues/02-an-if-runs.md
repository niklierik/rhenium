# 02: An if runs

**Spec:** [../spec.md](../spec.md)

**What to build:** A programmer can write `if (condition) { ... }`, optionally followed by
`else { ... }`, and the program compiles, links and runs the right branch.
`let x = 7; if (x < 5) { println 0; } else { println 1; }` prints `1`; without the `else`, a failed
condition runs nothing. Branches are block statements with scopes of their own, may be empty, and
nest with `while` and with each other to any depth.

The condition goes through the shared condition decorator from ticket 01, so `if (1) { }` is rejected
in exactly the words `while (1) { }` is, and a poisoned condition reports both its own fault and the
mismatch. The condition, the `then` branch and the `else` branch accumulate, so none hides a fault in
another.

`else` followed by an `if` is **not** part of this ticket — the else side is a block only for now, and
ticket 03 widens it.

**Blocked by:** 01

**Status:** resolved

- [x] An if statement is a parser rule — `if`, a parenthesised expression, the existing `block` rule,
      and optionally `else` followed by a block — listed as an alternative of `statement`; a braceless
      branch is a syntax error from the parser
- [x] An if statement node exists in the AST, holding its condition as an expression, its `then` branch
      as a block statement and an optional else branch, with a semantic context of its own, built by
      the statement visitor, whose children's diagnostics accumulate
- [x] A decorator for the `if` decorates the condition through the shared condition decorator and both
      branches against the enclosing scope, accumulating all three with `zipOrAccumulate`; the `if`
      creates no scope of its own, its blocks do
- [x] An if action exists, holding the condition, the `then` branch as a block action and an optional
      else side, lowered from the node
- [x] The action prints as `if(`, the condition, `)`, the `then` branch, then `else ` and the else side
      when there is one, inline and dense like `while`; the `if` never writes a brace of its own
- [x] The new action kind has its branch in the transpiler's exhaustive dispatch, and the Dagger
      bindings exist in each module it was added to
- [x] Tested at all four seams:
  - AST: `if` and `if` / `else` render in the s-expression
  - semantic analyzer: `Boolean` and relational conditions accepted; integer and float conditions
    rejected in the same wording as `while`; a poisoned condition reporting both messages; a bad
    condition and broken statements in both branches all reported; a declaration inside a branch
    invisible after the `if`; a nested `if`-in-`while`-in-`if` program with no diagnostics
  - lowering: `if` without `else`, `if` / `else`, and empty branches
  - transpiler: `if(`, the parentheses and `else ` pinned from hand-built actions
- [x] Verified by hand: `let x = 7; if (x < 5) { println 0; } else { println 1; }` compiles, links and
      prints `1`
- [x] No program the grammar accepts reaches an unhandled-parse-rule diagnostic or an exception
- [x] The build is green and no existing behaviour changed
