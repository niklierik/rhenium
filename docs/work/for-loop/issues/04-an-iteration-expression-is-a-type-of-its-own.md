# 04: An iteration expression is a type of its own

**Spec:** [../spec.md](../spec.md)

**What to build:** A refactor with no change in behaviour, from the second code review of tickets 01–03.
The four statement kinds that may be an iteration expression — assignment, compound assignment,
increment or decrement, and expression statement — share an `IterationExpression` type, and a for
statement holds its iteration expression as one, so a declaration in that slot cannot be represented.
One lowerer turns an iteration expression into its bare action; lowering one as a statement wraps that
in an expression statement action, and lowering it as a for statement's iteration expression does not.
The switch over the four kinds therefore exists once, instead of in both the statement lowerer and the
for statement lowerer, and the separate expression-statement lowerer has no reason left to exist.

The type is a plain interface, not a sealed one: Kotlin allows a sealed type's direct subtypes only in
its own package, and the four kinds live in two. An unhandled kind in the one switch stays an
`IllegalStateException`, a compiler bug like any other unhandled node kind.

The visitor builds the init clause and the iteration expression the same way, choosing among their
alternatives explicitly, as it already does for an else branch.

**Blocked by:** None (can start immediately)

**Status:** resolved

- [x] The four kinds implement one iteration expression type, and the for statement node holds its
      iteration expression as that type
- [x] One lowerer produces the bare action for an iteration expression, and both the statement lowerer
      and the for statement lowerer use it; no other place switches over the four kinds
- [x] The expression-statement lowerer and its binding are gone
- [x] The visitor builds the init clause and the iteration expression by choosing among their alternatives
      explicitly, neither relying on ANTLR's default child aggregation
- [x] Every existing test passes unchanged, the emitted C for a program using every form is unchanged,
      and the build is green
