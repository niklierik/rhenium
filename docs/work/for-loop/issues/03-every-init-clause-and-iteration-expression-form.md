# 03: Every init clause and iteration expression form

**Spec:** [../spec.md](../spec.md)

**What to build:** The init clause also accepts an assignment, a compound assignment, an increment or
decrement statement, or an expression, and the iteration expression also accepts an expression, as C
does. Anything else between the parentheses — a declaration as the iteration expression, a missing part, a block,
a control-flow statement, `print`, `break` or `continue` — is a syntax error. The language reference and
the roadmap describe the for statement.

**Blocked by:** 02

**Status:** resolved

- [x] Each init clause form parses, decorates, lowers to an expression statement action or declaration,
      and prints
- [x] An expression as the iteration expression lowers to the bare expression action and prints
- [x] An init clause assigning a variable declared before the loop leaves its final value after the loop
- [x] A declaration as the iteration expression, a missing part, and a disallowed statement between the parentheses
      are each a syntax error
- [x] Tested at the AST, semantic analyzer and lowering seams for each new form
- [x] The language reference reads `for (init-clause; condition; iteration-expression)`, and the roadmap
      has a paragraph for the for statement
- [x] The build is green
