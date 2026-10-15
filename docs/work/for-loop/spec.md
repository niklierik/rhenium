# The for statement

**Status:** ready-for-agent

## Problem Statement

Rhenium can repeat a block with `while`, but a counting loop has to be spelled out by hand: the counter
is declared before the loop and outlives it, and its update sits at the bottom of the body. A
`continue` placed above that update skips it, so the loop never advances and spins forever. The
October slice of the roadmap promises C's `for`, and the language reference already shows it.

## Solution

`for (init clause; condition; iteration expression) { body }` is C's `for`, with every part required.

```
for (let i = 0; i < 5; i++) {
    if (i == 2) { continue; }
    println i;
}
```

Prints `0`, `1`, `3`, `4`. The init clause runs once. Then, for as long as the condition holds, the body
runs and the iteration expression follows it, including after a `continue`. `break` leaves the loop
without running the iteration expression.

- The **init clause** is a `let` or `const` declaration, an assignment, a compound assignment, an
  increment or decrement statement, or an expression.
- The **condition** is a `Boolean`, checked exactly as a `while` or `if` condition is.
- The **iteration expression** is anything an init clause may be except a declaration.
- The **body** is a block statement, so it is braced, as for every other control-flow construct.

None of the four parts may be left out: `for (;;)` and `for (; i < 3; )` do not parse. A block, `if`,
`while`, `for`, `print`, `break` or `continue` in the header does not parse either: C accepts only a
declaration or an expression there, and Rhenium follows it.

A variable the init clause declares is visible in the condition, the iteration expression and the body,
and stops existing when the loop ends. The no-shadowing rule applies to it like any other variable:
`let i` in the body, or a nested `for (let i = ...)`, is reported as shadowing it.

`foreach` is not part of this effort.

## User Stories

1. As a Rhenium programmer, I want a counting loop in one line of header, so that the counter, the
   limit and the step are read together.
2. As a Rhenium programmer, I want the init clause to run exactly once, before the condition is first
   checked, so that the loop starts from the state I wrote.
3. As a Rhenium programmer, I want the condition checked before every pass, including the first, so that
   a loop over an empty range runs zero times.
4. As a Rhenium programmer, I want the iteration expression to run after every pass, so that the loop
   advances without me placing the update at the bottom of the body.
5. As a Rhenium programmer, I want `continue` to run the iteration expression, so that skipping the rest
   of a pass cannot make the loop spin forever.
6. As a Rhenium programmer, I want `break` to leave the loop without running the iteration expression,
   as C does, so that the counter holds the value that caused the exit.
7. As a Rhenium programmer, I want a variable declared in the init clause to be visible in the
   condition, the iteration expression and the body, so that it can drive the loop.
8. As a Rhenium programmer, I want that variable to stop existing when the loop ends, so that the next
   loop can declare its own `i`.
9. As a Rhenium programmer, I want two sibling loops to each declare `i`, so that I do not invent a new
   name per loop.
10. As a Rhenium programmer, I want `let i` inside the body of a loop that declared `i` reported as
    shadowing, so that the same rule holds here as in every other nested block.
11. As a Rhenium programmer, I want a nested loop that declares the outer loop's `i` again reported as
    shadowing, so that I never read the wrong counter.
12. As a Rhenium programmer, I want the init clause to assign a variable declared before the loop, so
    that its final value is still there after the loop.
13. As a Rhenium programmer, I want compound assignment and increment or decrement statements in either
    the init clause or the iteration expression, so that `i += 2` and `i--` work where I expect them to.
14. As a Rhenium programmer, I want a plain expression accepted in either slot, as C accepts one, so
    that the header follows the language I already know.
15. As a Rhenium programmer, I want a declaration in the iteration expression rejected as a syntax error,
    so that I cannot declare something that would be re-declared every pass.
16. As a Rhenium programmer, I want a block, a control-flow statement, `print`, `break` or `continue` in
    the header rejected as a syntax error, so that the header holds only what C allows.
17. As a Rhenium programmer, I want a missing part of the header rejected as a syntax error, so that
    every loop states its start, its condition and its step.
18. As a Rhenium programmer, I want a non-`Boolean` condition rejected with the same message as in a
    `while`, so that I learn the rule once.
19. As a Rhenium programmer, I want a broken condition to report its own fault and the `Boolean`
    requirement, as a `while` condition does, so that the loop constructs agree.
20. As a Rhenium programmer, I want `for (const i = 0; i < 3; i++)` reported as an assignment to a
    constant, so that a `const` counter is not silently allowed to change.
21. As a Rhenium programmer, I want a broken init declaration to still declare its variable, so that one
    mistake in the header produces one message rather than one per use of `i`.
22. As a Rhenium programmer, I want diagnostics in the init clause, the condition, the iteration
    expression and the body all reported in one run, so that fixing one does not reveal the next.
23. As a Rhenium programmer, I want `break` and `continue` legal anywhere in the body, however deeply
    nested in blocks and if statements, so that a for statement is a loop like a `while`.
24. As a Rhenium programmer, I want a statement after `break` or `continue` in the body to get the
    existing unreachable-statement warning, so that loops behave alike.
25. As a Rhenium programmer, I want the emitted C to be a C `for` with the same header, so that the
    generated file reads like my source.
26. As a Rhenium programmer, I want a program using a for statement to compile with `clang` and run, so
    that the feature is real.
27. As a compiler contributor, I want the for statement to be one node kind with its own context,
    decorator, lowerer, action and action transpiler, so that the parallel trees stay navigable.
28. As a compiler contributor, I want the header to reuse the existing statement node kinds and the
    shared condition decorator, so that no rule about assignment or conditions is restated.
29. As a compiler contributor, I want an assignment action to be a C expression, so that the same action
    serves an assignment statement and an iteration expression.
30. As a compiler contributor, I want the transpiler to keep making no decisions, so that every choice
    about the emitted C stays in lowering where the tests are.
31. As a compiler contributor, I want the new action kind to fail to compile until it is printed, so that
    the transpiler cannot silently drop a for statement.

## Implementation Decisions

**An assignment action is a C expression.** It prints as `target=value` with no `;`. An assignment
statement, a compound assignment and an increment or decrement statement each lower to an expression
statement action wrapping that assignment action, and the expression statement action already prints
the `;`. The emitted C for every existing program is unchanged. See
[ADR 0004](../../adr/0004-an-assignment-action-is-a-c-expression.md). This is a prefactor, landed on its
own before any `for` syntax exists.

**Lowering a header slot and lowering a statement are separate entry points.** The assignment-family
lowerers and the expression-statement lowerer produce the bare action — the assignment action, or the
expression's action — and lowering the same node as a statement wraps it in an expression statement
action. The init clause is lowered as a statement, so its `;` serves as the first separator of the C
header; the iteration expression is lowered bare.

**`for` is one new lexer token**, named `FOR` after the existing keyword tokens.

**The parser gains a `forStatement` alternative of `statement`:** `FOR`, an open bracket, the init
clause, the condition expression followed by a semicolon, the iteration expression, a close bracket and
a `block`. The init clause reuses the existing declaration, assignment, compound-assignment, increment
and expression-statement rules, each of which already ends with its `;`. The iteration expression needs
the same forms without the trailing `;`, so the assignment, compound-assignment, increment and
expression rules are factored into semicolon-less rules that both the statement forms and the
iteration expression use. A missing part, an extra part or a disallowed statement in either slot is an
ANTLR syntax error reported through the existing parse-error listener; there is no diagnostic to write.

**The five trees each gain one member:** a for statement node, its context, a decorator, a lowerer, a
for action and its action transpiler, plus a `@Binds` per module. The node holds its init clause and
iteration expression as statement nodes of the existing kinds, its condition as an expression, and its
body as a block statement. The action holds the init clause and iteration expression as actions, the
condition as an action, and the body as a block action.

**The for statement creates one scope: the loop scope.** Its decorator creates a child of the enclosing
scope marked as a loop, decorates the init clause, the condition, the iteration expression and the body
in it, and the body's block statement creates its own scope inside that one as it always does. That is
what makes the init clause's variable visible in all four parts and gone afterwards, what makes the
existing no-shadowing rule reject `let i` in the body, and what makes a loop control statement legal in
the body. The header cannot contain a loop control statement, so the loop marking on the scope only
matters to the body.

**The condition goes through the shared condition decorator**, with the same `TypeMismatch` wording and
the same exception to the poison-type convention as `while` and `if`
([ADR 0003](../../adr/0003-a-while-condition-is-checked-even-when-poisoned.md)).

**The four parts accumulate.** The init clause is decorated first, because the condition, the iteration
expression and the body all resolve its variable. The other three are sibling branches of one
accumulation, so a broken condition does not hide a broken body. A failed init declaration still
declares its variable, poisoned, through the existing declaration decorator, so the uses of it in the
other three parts are not reported again.

**The emitted C is `for(`, the init clause with its own `;`, the condition, `;`, the iteration
expression, `)`, then the body's braces**, inline in the dense style of the existing output. The `for`
action transpiler writes the keyword, the parentheses and the one `;` after the condition, because C's
grammar requires them, and makes no other decision. A declaration in the init clause emits as a C99
declaration in the `for` header, which scopes it to the loop in C as in Rhenium.

**The language reference's `for (init; condition; increment)` becomes
`for (init-clause; condition; iteration-expression)`**, matching the glossary, and the roadmap gains a
paragraph for the for statement once it lands.

## Testing Decisions

The same four seams as the `while`, `if` and `break`/`continue` efforts. No new seam is introduced. A
test states something about Rhenium or about emitted C, never about which class did the work.

**Semantic analyzer seam — source text in, rendered diagnostics out.** Cases:
- an init variable used in the condition, the iteration expression and the body, with no diagnostics
- that variable used after the loop: undeclared
- two sibling loops each declaring `i`: no diagnostics
- `let i` in the body, and a nested `for (let i ...)`: each reported as shadowing
- an init clause assigning a variable declared before the loop
- `break` and `continue` in the body, including nested in an if statement: legal
- a statement after `continue` in the body: the unreachable-statement warning
- an integer condition: the type mismatch naming `I32` and `Boolean`
- a poisoned condition: both diagnostics
- `const i` with `i++`: the existing constant-assignment error
- a failed init declaration: one diagnostic, none for its uses
- faults in the condition, the iteration expression and the body at once: all reported

**Lowering seam — source text in, action tree as an s-expression out.** Cases:
- the for action's shape, with a declaration as the init clause and an increment as the iteration
  expression
- an assignment, a compound assignment and an expression as the init clause, each as an expression
  statement action
- each form as the iteration expression, as the bare action
- `break` and `continue` in the body
- nesting
- the existing assignment, compound-assignment and increment cases, now wrapped in an expression
  statement action

**Transpiler seam — an action tree in, C text out.** Cases:
- an assignment action alone prints with no `;`
- an expression statement action around it prints the `;`
- a hand-built for action prints the full C header and leaves the braces to the body

**AST seam — source text in, tree as an s-expression out.** One case per header shape, with the renderer
taught the new node kind.

Beyond the four seams: a hand-run program through the application, checked once by the implementer and
not committed. It is a counting loop, plus a loop whose `continue` skips one pass, confirming that it
compiles with `clang` and that the iteration expression still runs.

## Out of Scope

- `foreach`. It needs collections, and the language has neither arrays nor strings.
- Optional header parts and `for (;;)`. An infinite loop is `while (true)` today, and `loop` is planned.
- A comma operator or several declarations in the init clause.
- Function calls in the header. Functions do not exist yet.
- Any warning about a loop that cannot terminate.
- Formatting the emitted C.

## Further Notes

Three glossary terms were added: **for statement**, **init clause** and **iteration expression**,
named after C's own grammar. `increment` is avoided for the third part because it is already the
increment statement, and the iteration expression need not increment anything.

One ADR was written: [ADR 0004](../../adr/0004-an-assignment-action-is-a-c-expression.md), on making
the assignment action a C expression so that it serves both an assignment statement and an iteration
expression.
