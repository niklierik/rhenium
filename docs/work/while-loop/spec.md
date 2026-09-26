# The while loop

**Status:** resolved

## Problem Statement

Rhenium has no repetition. Every program is a fixed list of statements executed once, so no program
whose length is not known when it is written can be expressed at all. Blocks landed the body a loop
needs — a braced group of statements with a scope of its own — and nothing has used it yet.

`while` is also the first construct in the language that *consumes* a `Boolean`. Relational and
equality operators have produced `BooleanType` since the arithmetic work, and `!` has accepted it, but
no statement has ever required one, so the rule "this position must be a Boolean" has never been
written down. Every other control-flow construct on the October slice — `if`, `for`, and `foreach`'s
implicit condition — needs exactly that rule, and it is written once here.

## Solution

`while (condition) { body }` executes its body repeatedly while `condition` holds.

```
let i = 0;
while (i < 3) {
    println i;
    i = i + 1;
}
```

Prints `0`, `1`, `2`. The condition is re-evaluated before each iteration, and a condition that is
false on the first evaluation means the body never runs.

The condition **must** be a `Boolean`. Nothing else is accepted — not an integer, not a float. C would
take an integer and treat zero as false; Rhenium does not, because a numeric condition is how
`if (x = 1)` becomes a silent bug in C, and the language has a `Boolean` type precisely so that a
condition can be typed.

The body is a **block statement**, required by the grammar rather than by a diagnostic. A braceless
`while (c) x = y;` does not parse. The body therefore carries a scope of its own, inherited from the
block work, so a variable declared inside the loop is local to it.

There is no `do`-`while`, and this effort adds no `break` and no `continue`.

## User Stories

1. As a Rhenium programmer, I want to repeat a group of statements while a condition holds, so that I
   can write a program whose length is not fixed when I write it.
2. As a Rhenium programmer, I want the condition re-evaluated before every iteration, so that a
   variable the body changes is what decides whether the loop continues.
3. As a Rhenium programmer, I want a loop whose condition is false at the start to run its body zero
   times, so that a loop over an empty range is not a special case I have to guard.
4. As a Rhenium programmer, I want a variable declared in the loop body to be local to it, so that
   each iteration's working names do not leak into the rest of my program.
5. As a Rhenium programmer, I want to read and assign the variables declared around the loop from
   inside it, so that the loop can make progress toward its own termination.
6. As a Rhenium programmer, I want loops to nest to any depth, so that I can iterate over two
   dimensions.
7. As a Rhenium programmer, I want a loop with an empty body to be legal, so that I can write the
   braces before the statements that go in them.
8. As a Rhenium programmer, I want a loop body to hold every statement kind the language already has,
   including a bare block, so that braces do not create a second, weaker language.
9. As a Rhenium programmer, I want a braceless loop body rejected as a syntax error, so that the
   message is about the missing brace and not about whatever came after it.
10. As a Rhenium programmer, I want a non-`Boolean` condition rejected, so that a numeric condition
    never silently means "not zero".
11. As a Rhenium programmer, I want the diagnostic to name what I wrote and what was wanted, so that I
    can fix it without consulting the reference.
12. As a Rhenium programmer, I want a condition that is itself broken to report both its own fault and
    the fact that a loop wants a Boolean, so that I am told the requirement of the construct I am
    writing whatever else is wrong.
13. As a Rhenium programmer, I want a broken statement inside a loop body reported without hiding the
    statements after it, so that one mistake costs me one message.
14. As a Rhenium programmer, I want every diagnostic in a program with several loops reported in one
    run, so that fixing one loop does not reveal a fresh error in the next.
15. As a Rhenium programmer, I want the emitted C to be a `while` loop with the same shape as my
    source, so that reading the generated file next to my program is possible when something goes
    wrong.
16. As a Rhenium programmer, I want a program using a loop to compile with `clang` and run, so that the
    feature is real rather than accepted-and-ignored.
17. As a Rhenium programmer, I want `while (true) { }` to compile and hang, so that the compiler does
    not pretend to know which loops I meant to terminate.
18. As a compiler contributor, I want the loop to be one AST node kind with its own semantic context,
    decorator, lowerer and action transpiler, so that the five parallel trees stay navigable.
19. As a compiler contributor, I want the source construct and the action named consistently with the
    existing pairs, so that I can guess a name instead of searching for it.
20. As a compiler contributor, I want the body typed as a block everywhere — node, action and
    transpiler — so that no stage has to handle a body the grammar cannot produce.
21. As a compiler contributor, I want the Boolean-condition check written once and reachable by the
    constructs that follow, so that `if` and `for` inherit it rather than restating it.
22. As a compiler contributor, I want the loop to create no scope of its own, so that there is one
    place — the block — where a lexical scope comes from.
23. As a compiler contributor, I want a new action kind to fail to compile until it is printed, so that
    the transpiler cannot silently drop a construct.
24. As a compiler contributor, I want the transpiler to keep making no decisions, so that every choice
    about the emitted C stays in lowering where the tests are.
25. As a compiler contributor, I want the loop tested at the existing four seams, so that the suite does
    not grow a new shape per feature.
26. As a compiler contributor, I want each ticket to compile and be tested on its own, so that the
    effort can be picked up and put down between sessions.
27. As a compiler contributor, I want the effort to stop at `while`, so that the `for` work reviewed
    next is about the three-part header and nothing else.

## Implementation Decisions

**`while` is one new lexer token**, named `WHILE` after the existing keyword tokens.

**The loop is a parser rule of its own, listed as an alternative of `statement`:**
`whileStatement: WHILE OPEN_BRACKET expression CLOSE_BRACKET block;`. It reuses the existing `block`
rule rather than spelling out braces and statements again, so nesting, every existing statement kind,
and the empty body all come for free, and the body keeps the scope the block already gives it.

**The body is a block in the grammar, not a statement.** The reference makes braces mandatory on every
control-flow construct, so making that a fact about the grammar means the illegal form has no
representation in any of the five trees and there is no diagnostic to write. A braceless body is an
ANTLR syntax error, reported through the existing parse-error listener.

**The five trees each gain one member**, in the order the project's conventions require: a
`WhileStatement` node in the statements package, a `WhileStatementContext`, a
`WhileStatementDecorator`, a `WhileStatementLowerer`, a `CWhileTranspiler`, plus the `@Binds` entry per
module. The node kind is a **while statement**; the action it lowers to is a **while action**, told
apart by which tree it is in, as the existing print and block pairs are.

**The node holds its body as a `BlockStatement`, and the action holds its body as a `BlockAction`.**
The grammar guarantees a block, so the types say so too. The transpiler is then a lookup with no
branch, and any later desugaring that wants an unbraced body has to say so explicitly rather than
silently passing one through.

**The loop creates no scope.** Its context holds the enclosing scope, like every other statement,
because the statement dispatcher sets that uniformly before dispatching. The condition is decorated
against that enclosing scope, and the body's scope comes from the block statement inside it, which
already creates one. Nothing about a `while` header declares a name. `for` is what will need a
loop-level scope wrapping its initializer, condition and body, and it introduces one when it has
something to put in it.

**The condition must be `BooleanType`**, checked by the loop's decorator, reported with the existing
general-purpose `TypeMismatch` diagnostic: `type mismatch, found I32 but expected Boolean.` A
construct-specific diagnostic was rejected — `TypeMismatch` is already what declarations use for a
value of the wrong type, and `if` and `for` then inherit the wording rather than adding two more
near-identical classes.

**A condition of the poison type is reported anyway.** `while (1 + true) { }` produces two
diagnostics: the illegal binary operation, and the type mismatch for the condition. This is a
deliberate departure from the poison-type convention, which elsewhere suppresses the consequences of a
failed expression — see
[ADR 0003](../../adr/0003-a-while-condition-is-checked-even-when-poisoned.md). The reason it is worth
the extra message here is that a loop's requirement on its condition is a property of the construct
the programmer is writing, not a consequence of the expression they got wrong.

**The condition check accumulates with the body** rather than running before it, so a loop with both a
bad condition and a broken statement in its body reports both in one run.

**The emitted C is `while(` condition `)` followed by the body's braces, inline** — no newline, no
indentation, no space after the keyword, matching the dense style of the existing output. The
parentheses are written unconditionally by `CWhileTranspiler`, because C's grammar requires them; that is a fact about C's
syntax rather than a decision about this program, which is the same reasoning that lets the block
action's transpiler own its braces. The braces themselves are not written by the loop: the body is a
block action and already prints its own.

**Two tickets**, each a vertical change that compiles and is tested alone: the tracer bullet that takes
a loop through all five trees with the condition's type knowingly unchecked, then the Boolean rule.

## Testing Decisions

The four seams the previous efforts use, unchanged. Three of them take source text, so a test reads as
a claim about Rhenium rather than about the compiler's internals. No new seam is introduced.

**Semantic analyzer seam — source text in, rendered diagnostics out.** Where the condition rule lives.
Cases: a `Boolean` condition accepted with no diagnostics; a relational condition accepted; an integer
condition rejected naming `I32` and `Boolean`; a float condition rejected; a poisoned condition
reporting both its own diagnostic and the mismatch; a broken statement in the body reported without
hiding the statements after it; two loops in one program both reporting; a declaration inside the body
invisible after the loop; a nested loop program producing no diagnostics at all.

**Lowering seam — source text in, action tree as an s-expression out.** The highest seam available,
running parser, AST builder, decorator and lowerer in one call. Covers the shape a loop lowers to, the
body appearing as a block action, an empty body, and nesting.

**Transpiler seam — an action tree in, C text out.** The only seam where emitted C is observable, so
the `while (`, the parentheses and the absence of any brace of the loop's own are pinned here from a
hand-built action.

**AST seam — source text in, tree as an s-expression out.** One case for the new node kind, with the
renderer taught to print it, per the project's convention that every node kind gets a case and a
renderer branch.

Beyond the four: a hand-run program through the application, checked once by the implementer,
confirming that a counting loop compiles with `clang` and prints what it should. Not committed — there
is no end-to-end harness, and building one means splitting the compiler's file writing and `clang`
invocation apart, which is its own effort.

## Out of Scope

- `break` and `continue`. They need loop-nesting tracking in the decorator so that a `break` outside a
  loop is a diagnostic, and without `if` neither can be conditional, so the only programs they enable
  run one iteration. They land with the `if` effort, where they become usable.
- `if` / `else if` / `else`, the conditional expression, `for`, `foreach`, `loop` and `repeat`.
- `&&` and `||`, and the numeric gate in `BinaryOpNodeDecorator` that currently rejects them along with
  `Boolean == Boolean`. Tracked as its own effort; a `while` condition is a relational expression, a
  `Boolean` variable, a literal or `!x` until it lands.
- `do`-`while`. Never planned.
- Any warning about `while (true)` or a loop that cannot terminate. Wanted — see Further Notes — but
  impossible today.
- Formatting the emitted C. Indentation state in the printer would be the printer making decisions.

## Further Notes

**A warning for a non-terminating loop is wanted, and needs structural work first.** `Diagnostic` has
no severity, and `Diagnosed<T>` is `EitherNel<Diagnostic, T>`: a diagnostic *is* the left, meaning "no
value", so producing one halts lowering and makes `Main` exit non-zero. A warning must ride alongside a
successful value, which means reshaping `Diagnosed`, teaching `Main` which severities are fatal, and
touching every walker's accumulation. That is its own effort, and the analysis that recognises
`while (true)` with no `break` is a second one on top of it. Recorded here so the wish is not lost.

One ADR was written: [ADR 0003](../../adr/0003-a-while-condition-is-checked-even-when-poisoned.md), on
reporting the condition's type mismatch even when the condition already failed. No glossary term was
added — `WhileStatement` and `WhileAction` follow the existing pair convention, and nothing about
"condition" is ambiguous. If `for` forces a decision about a loop-level scope, that one may earn an
entry.
