# Implement the print and println statements

Status: resolved
Blocked by: none

Add `print` / `println` as reserved statement keywords so program output can be observed. See
[the spec](../spec.md).

## Tasks

- `ExpressionType.cFormat: String?`, filled in for every numeric type and `Boolean`, left null on
  `InvalidType`
- `#include <inttypes.h>` in the transpiler prologue
- `PRINT` / `PRINTLN` lexer tokens, above `ID`
- `printStatement: PRINT expression SEMICOLON | PRINTLN expression? SEMICOLON;`, added to `statement`
- `PrintStatement` AST node and `PrintStatementContext`
- `StatementVisitor.visitPrintStatement`, branching on `ctx.PRINT() != null`
- `PrintStatementDecorator` and `CPrintTranspiler`, plus `@Binds` in both Dagger modules
- `AstBuilderTests` cases and renderer support for the new node

## Done when

Each printable type prints its value, `println;` emits one line break, `println true;` writes `true`,
and `print b;` with `b` undeclared reports exactly one diagnostic.

## Comments

### Status corrected, the work having shipped some time ago

The `Status:` line still read `ready-for-agent` long after every task landed, which also left ticket
02 formally blocked by an open ticket. Verified against the tree rather than assumed: `PRINT` and
`PRINTLN` are lexer tokens with the `printStatement` parser rule, `PrintStatement` and its context
exist, `StatementVisitor.visitPrintStatement` branches on `ctx.PRINT() != null`,
`PrintStatementDecorator` is bound, and `cFormat` is filled in for every numeric type and `Boolean`
and left null on `InvalidType`.

Each "Done when" clause re-checked: every printable type prints its value, `println;` emits one line
break, `println true;` writes `true`, and `print b;` with `b` undeclared reports exactly one
diagnostic — `1:7: unknown symbol 'b'.`

One task moved module since it was written. `CPrintTranspiler` no longer decides the format: the
`printf` conversion is chosen during lowering and carried on `PrintAction`, so the printer writes
whatever string it is handed. See the lowering spec.
