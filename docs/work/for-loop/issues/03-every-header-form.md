# 03: Every header form

**Spec:** [../spec.md](../spec.md)

**What to build:** The init clause also accepts an assignment, a compound assignment, an increment or
decrement statement, or an expression, and the iteration expression also accepts an expression, as C
does. Anything else in the header — a declaration as the iteration expression, a missing part, a block,
a control-flow statement, `print`, `break` or `continue` — is a syntax error. The language reference and
the roadmap describe the for statement.

**Blocked by:** 02

**Status:** ready-for-agent

- [ ] Each init clause form parses, decorates, lowers to an expression statement action or declaration,
      and prints
- [ ] An expression as the iteration expression lowers to the bare expression action and prints
- [ ] An init clause assigning a variable declared before the loop leaves its final value after the loop
- [ ] A declaration as the iteration expression, a missing part, and a disallowed statement in the header
      are each a syntax error
- [ ] Tested at the AST, semantic analyzer and lowering seams for each new form
- [ ] The language reference reads `for (init-clause; condition; iteration-expression)`, and the roadmap
      has a paragraph for the for statement
- [ ] The build is green
