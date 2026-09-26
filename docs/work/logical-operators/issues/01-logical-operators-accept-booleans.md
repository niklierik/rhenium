# 01: Logical operators accept Booleans

**What to build:** `&&` and `||` work on `Boolean` operands. `true && false` is a valid expression,
`while (running && !done) { }` is a valid loop condition, and the short-circuit guarantee the
[language reference](../../../language-reference.md) promises becomes observable.

**Blocked by:** None (can start immediately)

**Status:** needs-triage

## Why this is not just a missing `when` case

Two layers reject them today, both in `BinaryOpNodeDecorator.resolveType`:

1. A blanket gate — `if (!left.isNumeric() || !right.isNumeric()) return IllegalBinaryOperation(...)` —
   runs *before* the `when` over operators, so no non-numeric operand pair ever reaches a rule.
2. The `when` has no `AND` / `OR` branch, so even past the gate they fall to `else`.

Everything else already exists: the lexer tokens, the `logicalExp` parser rule, the AST node,
`Operator.AND("&&")` / `Operator.OR("||")`, and the non-numeric branch of `BinaryOpLowerer`, which emits
a bare `BinaryAction` with no cast wrapper. Expressions stay nested, so C's own `&&` provides the
short-circuit — see [ADR 0002](../../../adr/0002-expressions-stay-nested-in-the-action-tree.md).

## The open question triage has to settle

The same blanket gate is why `true == false` is rejected, although `Operator.EQUALS` already has a
`BooleanType.right()` branch sitting unreachable behind it. Rewriting the gate so Booleans pass makes
equality on Booleans start working as a side effect. Either that is accepted and tested as part of one
"non-numeric binary operators" rule, or the gate is narrowed per operator so that only `&&` and `||`
admit Booleans. Deciding that is the first thing this ticket needs.

## Notes

`while` shipped before this, so a loop condition is currently a comparison, a `Boolean` variable, a
literal or `!x`. See [docs/work/while-loop/spec.md](../../while-loop/spec.md).
