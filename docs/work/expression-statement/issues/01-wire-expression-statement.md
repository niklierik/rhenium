# Wire expressionStatement through the statement pipeline

Status: ready-for-agent

`expressionStatement` parses but is unhandled in all three walkers, so valid input reaches
`UnhandledParseRule` — an internal-error diagnostic reserved for compiler bugs.

See [the spec](../spec.md).

## Tasks

- `StatementVisitor.visitExpressionStatement` — visit the operand, build `ExpressionStatement`
- `StatementNodeDecorator` — branch for `ExpressionStatement`, decorate the operand against the
  statement's scope
- `CStatementTranspiler` — branch for `ExpressionStatement` delegating to a new `CExpressionStatementTranspiler`
- `@Binds` for the new transpiler in `CTranspilerModule`
- `SemanticAnalyzerTests` case pinning that an undeclared operand reports one diagnostic, not two
- `AstBuilderTests` case for the s-expression rendering

## Done when

`let a = 1;\na + 1;` compiles and runs, and no accepted program can produce `unhandled parse rule`.
