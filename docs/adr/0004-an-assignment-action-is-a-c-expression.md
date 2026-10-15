# An assignment action is a C expression

In Rhenium an assignment is a statement and never an expression, yet `AssignmentAction` prints as a C
expression (`x=e`, no `;`), and an assignment statement lowers to an `ExpressionStatementAction`
wrapped around it, which supplies the `;`. Compound assignments and increment statements lower to the
same action, so they follow it.

This is recorded because it looks like a leak of C into the action tree, and anyone who has just read
that Rhenium has no assignment expressions will expect the action to be a statement too. It is
deliberate: a for statement's iteration expression is usually an assignment, and C only accepts an
expression there. Making the action an expression mirrors C's own grammar, where a statement is an
expression plus `;`, so a for statement lowers to a `ForAction` printed as C's `for` and the transpiler
still makes no decisions.

## Considered options

- Have the `ForAction` printer strip the `;` from its iteration expression. Rejected: that is a decision
  made in the transpiler, which is the one place decisions must not be made.
- Desugar a for statement into a `while` with the iteration expression at the end of the body, and turn
  every `continue` into a `goto` to it. Rejected: it adds labels and jumps to the action tree to rebuild a
  construct C already has.
