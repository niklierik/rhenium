# Implement the print and println statements

Status: ready-for-agent
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
