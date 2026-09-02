# Expression statements

## Problem

`expressionStatement: expression SEMICOLON;` has existed in the parser grammar since the grammar was
written, and `ExpressionStatement` / `ExpressionStatementContext` exist as an AST node and a semantic
context. Nothing else is wired: there is no `visitExpressionStatement` in `StatementVisitor`, no branch
in `StatementNodeDecorator`, and no branch in `CStatementTranspiler`.

The result is that input the grammar accepts dies in the AST builder:

```
let a = 1;
a + 1;
```

produces `0:0: internal error: unhandled parse rule.` and a non-zero exit.

That message means *compiler bug*, not *bad input* — the convention in `CLAUDE.md` is explicit about
this. A program the grammar accepts must never reach it.

## Scope

Wire the existing node through the three trees that ignore it. No grammar change, no new AST node, no
new semantic context, no new diagnostic.

An expression statement evaluates its expression and discards the result. Discarding a value is not a
diagnostic — that judgement belongs to a later unused-value analysis, not here.

## Behaviour

| Source | Emitted C |
| --- | --- |
| `a + 1;` | `re_a_<uuid>+1;` |
| `1;` | `1;` |
| `b + 1;` where `b` is undeclared | nothing — `unknown symbol 'b'` reported, compilation stops |

The operand decorates exactly as any other expression: `relevantScope` is set, the type is resolved,
and a failed operand poisons to `InvalidType` and reports its own diagnostic.

## Out of scope

- Unused-value or pure-expression warnings
- Any change to `expression` itself

## Success criteria

- `let a = 1;\na + 1;` compiles, links and runs
- `b + 1;` reports `unknown symbol 'b'` and nothing else
- No path in the statement pipeline can reach `UnhandledParseRule` for a program the grammar accepts
