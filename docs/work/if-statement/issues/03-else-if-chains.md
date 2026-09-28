# 03: Else if chains

**Spec:** [../spec.md](../spec.md)

**What to build:** `else` may be followed by another `if`, so conditions chain:
`let x = 7; if (x < 5) { println 0; } else if (x < 10) { println 1; } else { println 2; }` prints `1`.
A chain may end in `else if` with no final `else`. Conditions are tried in order and at most one branch
runs — which C already gives, because the chain lowers to an if action nested in the else side and
prints as `else if(`.

`else if` is not a construct of its own anywhere past the parser, exactly as in C, C# and Java. The
only thing besides a block that `else` may be followed by is an `if`: `else while (c) { }` stays a
syntax error and must be written `else { while (c) { } }`.

**Blocked by:** 02

**Status:** resolved

- [x] The if statement rule's else side accepts either an if statement or a block, and nothing else
- [x] The node's and the action's else side are typed as "a block or an if", never "any statement", so
      no stage handles a shape the grammar cannot produce
- [x] The nested `if` is decorated as a statement against the enclosing scope and accumulates with the
      rest, so a bad condition in one link does not hide a bad condition in a later one
- [x] A nested if action in the else side prints as `else if(` with no brace of the chain's own
- [x] Tested at all four seams:
  - AST: an `else if` chain renders in the s-expression; `else while` fails to parse
  - semantic analyzer: bad conditions in two links of one chain both reported
  - lowering: a chain with and without a final `else` renders as nested if actions
  - transpiler: `else if(` pinned from a hand-built nested action
- [x] Verified by hand: the example above compiles, links and prints `1`
- [x] The build is green and no existing behaviour changed
