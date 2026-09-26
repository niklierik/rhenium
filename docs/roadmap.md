# Roadmap and status

## Schedule

From `plan.md` on the `plans` branch. MSc thesis project, defense planned for spring 2027.

| When | Work |
| --- | --- |
| September 2026 | expressions, variables with types, temporary printing, type analysis, C generation infrastructure |
| October 2026 | scopes, `if` / `else if` / `else`, `for`, `while`, functions, structs |
| November 2026 | interfaces, classes, `take` / `gives`, `using` |
| December 2026 | generics, C interop, standard library |
| January 2027 | example program (Battleship), fixing what it uncovers |
| February–May 2027 | writing the thesis |

## What the compiler implements today

Literals, unary and binary arithmetic, relational and equality operators, grouping,
`let` / `const` declarations with an optional declared type, assignment, expression statements,
braced blocks with a scope of their own, `while` loops, the type rules over those, and a transpiler
that emits every statement into a single C `main()`.

A block is a statement, so blocks nest and stand on their own. What a block declares stops existing at
its closing brace, and a declaration may not shadow one from an enclosing block — the two collisions
report separately, `variable 'a' is already declared at 1:1.` within one block and
`variable 'a' shadows variable 'a' declared at 1:1.` across two. See
[docs/work/scopes/spec.md](work/scopes/spec.md).

`while (condition) { body }` repeats its body while the condition holds. The condition must be a
`Boolean` — an integer is rejected rather than compared against zero — and the body is a block in the
grammar, so a braceless body is a syntax error and the body's scope comes from the block. A condition
that is already broken is reported *and* told that a loop wants a `Boolean`, which departs from the
poison-type convention on purpose; see
[ADR 0003](adr/0003-a-while-condition-is-checked-even-when-poisoned.md). There is no `break`, no
`continue` and no `do`-`while`. See [docs/work/while-loop/spec.md](work/while-loop/spec.md).

`print` and `println` write a value to standard output. They are a placeholder for the `Console` of
[the standard library](standard-library.md), reserved keywords rather than calls because the language
has neither functions nor strings yet, and they are removed when `Console` lands. See
[docs/work/print/spec.md](work/print/spec.md).

Everything else in these documents is unbuilt.

## Known gaps worth knowing before you start

- `^` (Pow) exists as a lexer token and has type rules in `BinaryOpNodeDecorator`, but **no parser
  rule** — it cannot be written in a program yet.
- `&&` and `||` are the mirror image: lexer tokens, parser rules and AST nodes all exist, but
  `BinaryOpNodeDecorator` has **no case** for them, so every use is rejected as an illegal binary
  operation whatever the operand types. The short-circuit guarantee they are promised in
  [the language reference](language-reference.md) is therefore not yet observable. Lowering is
  already shaped for it — see
  [ADR 0002](adr/0002-expressions-stay-nested-in-the-action-tree.md) — so what is missing is the
  type rule, not the emission. The gate that rejects them is a blanket non-numeric check that runs
  before the per-operator rules, which is also why `Boolean == Boolean` is rejected although the
  equality case would accept it. A `while` condition is therefore a comparison, a `Boolean` variable,
  a literal or `!x` until this lands.
- There is no `entry`, no `namespace` and no `Project.json` handling. The compiler takes a single
  `.re` file path on the command line and compiles that.
- Division by zero and `MIN / -1` are undefined. Diagnosing them needs a way to fail at runtime, and
  there is no `if`, no panic and no `Result` yet. See
  [docs/work/integer-arithmetic/spec.md](work/integer-arithmetic/spec.md).
- There is no warning severity. `Diagnostic` carries only a line, a column and a message, and a
  diagnostic *is* the `Either` left, so producing one halts lowering and makes the compiler exit
  non-zero. Anything that should be reported without failing the build — a `while (true)` that cannot
  terminate, an unused variable — needs `Diagnosed` reshaped first.
