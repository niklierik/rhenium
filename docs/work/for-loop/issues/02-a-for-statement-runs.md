# 02: A for statement runs

**Spec:** [../spec.md](../spec.md)

**What to build:** A programmer can write `for (let i = 0; i < 5; i++) { println i; }` and the program
compiles, links and prints `0` to `4`. The emitted C is a C `for` with the same three parts. `continue` runs
the iteration expression; `break` leaves the loop. The init clause's variable lives in the loop scope:
visible in the condition, the iteration expression and the body, gone after the loop, and shadowing it
is an error.

This ticket accepts one shape: a declaration as the init clause, and an assignment, compound
assignment or increment or decrement statement as the iteration expression. Ticket 03 widens it.

**Blocked by:** 01

**Status:** resolved

- [x] `for` lexes as its own token, named consistently with the existing keyword tokens
- [x] A for statement is a parser rule listed as an alternative of `statement`, holding the init clause,
      the condition, the iteration expression and the existing `block` rule; every part is required
- [x] A for statement node exists, with its own semantic context, built by the statement visitor
- [x] Its decorator creates a loop scope as a child of the enclosing one and decorates all four parts in
      it, the init clause first and the other three accumulating as siblings; the condition goes through
      the shared condition decorator
- [x] A for action exists, lowered from the node, holding the init clause as a statement action, the
      iteration expression as a bare action, and the body as a block action
- [x] The action prints as `for(`, the init clause with its own `;`, the condition, `;`, the iteration
      expression, `)`, then the body; it has its branch in the transpiler's exhaustive dispatch
- [x] The Dagger bindings exist in each module the for statement was added to
- [x] Semantic analyzer seam: the init variable used in all four parts; used after the loop; sibling
      loops; shadowing in the body and in a nested loop; `break` and `continue` in the body; an
      unreachable statement after `continue`; an integer condition; a poisoned condition; a `const`
      counter incremented; a failed init declaration; faults in several parts at once
- [x] Lowering seam: the for action's shape, `break` and `continue` in the body, nesting
- [x] Transpiler seam: a hand-built for action prints its three parts in C's parentheses
- [x] AST seam: the new node kind renders, with the renderer taught it
- [x] Verified by hand: a loop over a counter prints what it should, and a loop whose `continue` skips one pass
      still advances
- [x] The build is green
