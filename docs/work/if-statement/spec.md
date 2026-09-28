# The if statement

**Status:** resolved

## Problem Statement

Rhenium can repeat but cannot choose. `while` landed a construct that consumes a `Boolean`, yet a
program still has no way to run a statement only when something holds, or to pick between two
alternatives. Every program takes the same path through its statements on every input, and the gaps
the roadmap lists — diagnosing a division by zero at runtime, stopping early — are all waiting on a
branch.

The condition rule `while` introduced also lives inside the loop's own decorator today. The
[while spec](../while-loop/spec.md) promised that `if` and `for` would inherit that rule rather than
restate it, and [ADR 0003](../../adr/0003-a-while-condition-is-checked-even-when-poisoned.md) promised
they would inherit its poison-type exception too. Nothing enforces either promise yet: a second
construct would have to copy the check by hand.

## Solution

`if (condition) { ... }` runs its block only when the condition holds. An optional `else { ... }` runs
when it does not, and `else` followed by another `if` chains a further condition, so `else if` is not a
construct of its own — exactly as in C, C# and Java.

```
let x = 7;
if (x < 5) {
    println 0;
} else if (x < 10) {
    println 1;
} else {
    println 2;
}
```

Prints `1`.

Braces are mandatory everywhere, by the grammar. The only thing `else` may be followed by is a block
or an `if`; `else while (c) { }` does not parse and must be written `else { while (c) { } }`. Every
branch is a block statement and carries a scope of its own.

The condition **must** be a `Boolean`, with the same wording and the same poison-type exception as a
`while` condition, because both are now checked by one **condition** decorator shared by every
construct that has a condition.

## User Stories

1. As a Rhenium programmer, I want to run a group of statements only when a condition holds, so that
   my program can react to the values it computes.
2. As a Rhenium programmer, I want an `else` block that runs when the condition does not hold, so that
   I can choose between two alternatives without testing the negation separately.
3. As a Rhenium programmer, I want to chain `else if` to test several conditions in order, so that I
   can pick one of many alternatives without nesting a block per alternative.
4. As a Rhenium programmer, I want the conditions of a chain evaluated in order and only until one
   holds, so that a later condition may rely on the earlier ones having failed.
5. As a Rhenium programmer, I want at most one branch of a chain to run, so that the chain means
   "one of these" and not "each of these that holds".
6. As a Rhenium programmer, I want an `if` without an `else` to do nothing when its condition fails, so
   that I do not have to write an empty `else`.
7. As a Rhenium programmer, I want a chain that ends in `else if` without a final `else` to be legal,
   so that I only write the cases I care about.
8. As a Rhenium programmer, I want a variable declared inside a branch to be local to it, so that one
   branch's working names do not leak into the rest of the program.
9. As a Rhenium programmer, I want to read and assign the variables declared around the `if` from
   inside any branch, so that a branch can have an effect on the rest of the program.
10. As a Rhenium programmer, I want `if` and `while` to nest inside one another to any depth, so that a
    branch can loop and a loop can branch.
11. As a Rhenium programmer, I want empty branches to be legal, so that I can write the braces before
    the statements that go in them.
12. As a Rhenium programmer, I want a braceless branch rejected as a syntax error, so that the message
    is about the missing brace and not about whatever came after it.
13. As a Rhenium programmer, I want `else` followed by anything other than a block or an `if` rejected
    as a syntax error, so that `else if` is the one place braces may be left out and it is obvious why.
14. As a Rhenium programmer, I want a non-`Boolean` condition rejected, so that a numeric condition
    never silently means "not zero".
15. As a Rhenium programmer, I want the rejection worded exactly as a `while` condition's is, so that
    one rule has one message wherever I break it.
16. As a Rhenium programmer, I want a condition that is itself broken to report both its own fault and
    the fact that an `if` wants a `Boolean`, so that I am told the requirement of the construct I am
    writing whatever else is wrong.
17. As a Rhenium programmer, I want a bad condition, a broken `then` branch and a broken `else` branch
    all reported in one run, so that no part of an `if` hides a mistake in another.
18. As a Rhenium programmer, I want a bad condition anywhere in an `else if` chain reported without
    hiding the conditions after it, so that fixing one link does not reveal a fresh error in the next.
19. As a Rhenium programmer, I want the emitted C to be an `if` / `else if` / `else` with the same shape
    as my source, so that reading the generated file next to my program is possible.
20. As a Rhenium programmer, I want a program using `if` to compile with `clang` and run, so that the
    feature is real rather than accepted-and-ignored.
21. As a compiler contributor, I want the condition rule to live in one decorator that `while` and `if`
    both call, so that the constructs cannot drift apart and `for` gets it for free.
22. As a compiler contributor, I want `while` moved onto that decorator with no change in behaviour,
    pinned by its existing tests, so that the refactor is provably a refactor.
23. As a compiler contributor, I want ADR 0003 to say it covers every condition, so that the next
    reader does not mistake the `if` behaviour for a copy-paste bug.
24. As a compiler contributor, I want `if` to be one AST node kind with its own semantic context,
    decorator, lowerer and action transpiler, so that the five parallel trees stay navigable.
25. As a compiler contributor, I want `else if` to be an `if` nested in the `else` side rather than a
    chain node, so that there is one node and one action and the chain is built from them.
26. As a compiler contributor, I want the `else` side typed as "a block or an if" rather than "any
    statement", so that no stage has to handle a shape the grammar cannot produce.
27. As a compiler contributor, I want a new action kind to fail to compile until it is printed, so that
    the transpiler cannot silently drop a construct.
28. As a compiler contributor, I want the transpiler to keep making no decisions, so that every choice
    about the emitted C stays in lowering where the tests are.
29. As a compiler contributor, I want the `if` tested at the existing four seams, so that the suite does
    not grow a new shape per feature.
30. As a compiler contributor, I want each ticket to compile and be tested on its own, so that the
    effort can be picked up and put down between sessions.

## Implementation Decisions

**Conditions share one decorator.** The condition check leaves the while statement's decorator and
becomes a **condition** decorator of its own in the semantic analyzer: it decorates an expression
against a scope and requires `BooleanType`, reporting the existing `TypeMismatch` with `Boolean` as
the only expected type. It reports that mismatch even when the expression is of the poison type, and
returns the expression's own diagnostics alongside it, so a caller gets every fault of a condition from
one call. `while` is moved onto it first, with its behaviour unchanged, and `if` is its second caller.

**ADR 0003 is amended in place**, retitled "A condition is checked even when poisoned", noting that the
rule now covers every construct with a condition and is enforced by the shared decorator. The decision
did not change, only its reach, and the ADR's own consequences section anticipated this; a second ADR
would split one rule across two files.

**`if` and `else` are the existing lexer tokens.** No lexer change.

**The grammar is recursive.** An if statement is `IF ( condition ) then-block`, optionally followed by
`ELSE` and then **either** another if statement **or** a block. It is listed as an alternative of
`statement` and reuses the existing `block` rule, so nesting, every existing statement kind and empty
branches come for free. There is no dangling-else ambiguity, because every branch is braced. Anything
else after `else` is an ANTLR syntax error reported through the existing parse-error listener.

**One node, one action.** The five trees each gain one member, in the order the project's conventions
require: an `IfStatement` node, an `IfStatementContext`, an `IfStatementDecorator`, an
`IfStatementLowerer`, a `CIfTranspiler`, plus the `@Binds` entry per module. The node kind is an **if
statement**; the action is an **if action**, told apart by which tree they are in.

**The `else` side is "a block or an if", never "any statement".** The node holds its condition as an
expression, its `then` branch as a `BlockStatement`, and an optional else side that is either a
`BlockStatement` or an `IfStatement`. The action mirrors it: a condition action, a `then` `BlockAction`,
and an optional else side that is either a `BlockAction` or an `IfAction`. `else if` is therefore not a
node, an action or a keyword pair anywhere past the parser — it is an if action in an else side.

**The `if` creates no scope.** Its context holds the enclosing scope, like every other statement. The
condition is decorated against it, and each branch's scope comes from its block statement. The nested
`if` in an else side is decorated as a statement against the same enclosing scope.

**Diagnostics accumulate across all three parts.** The condition (through the condition decorator),
the `then` block and the else side are sibling children, combined with `zipOrAccumulate`, so none of
them hides a fault in another. A chain accumulates link by link through the nesting.

**The emitted C is `if(` condition `)` followed by the `then` block, then — when there is an else side —
`else ` followed by it**, inline and dense, matching the style of `while`. The if transpiler writes the
parentheses and the `else` keyword because C's grammar requires them; it never writes a brace of its
own, since both a block action and a nested if action print themselves. An `else` followed by a nested
if action prints as `else if(`, which is the chain in C with no extra nesting.

**Three tickets**, each a vertical change that compiles and is tested alone: the prefactor that moves
`while` onto the shared condition decorator, the tracer bullet that takes `if` / `else` through all
five trees with the condition checked, and the `else if` chain.

## Testing Decisions

The four seams the previous efforts use, unchanged. No new seam is introduced. A good test states a
claim about Rhenium — a source text and what the compiler says about it — not about a decorator's
internals.

**Semantic analyzer seam — source text in, rendered diagnostics out.** Where the condition rule and
the accumulation live. The shared condition decorator gets no test of its own: the existing `while`
cases are what prove the refactor changed nothing, and the `if` cases prove the second caller gets the
same rule. Cases: a `Boolean` and a relational condition accepted; an integer and a float condition
rejected in the same wording as `while`; a poisoned condition reporting both messages; a bad condition
and broken statements in both branches all reported; a bad condition in an `else if` link reported
without hiding a later one; a declaration inside a branch invisible after the `if`; a nested
`if`-in-`while`-in-`if` program producing no diagnostics.

**Lowering seam — source text in, action tree as an s-expression out.** Covers `if` without `else`,
`if` / `else`, empty branches, and an `else if` chain lowering to an if action nested in the else side.

**Transpiler seam — an action tree in, C text out.** The only seam where the emitted C is observable, so
`if(`, the parentheses, `else ` before a block, `else if(` before a nested if action, and the absence
of any brace of the `if`'s own are pinned here from hand-built actions.

**AST seam — source text in, tree as an s-expression out.** One case per shape — `if`, `if` / `else`,
`else if` — with the renderer taught to print the new node kind. `else while` failing to parse is
pinned here too, as the syntax error it is.

Prior art: the `while` cases at every one of the four seams.

Beyond the four: a hand-run program through the application, checked once by the implementer,
confirming an `if` / `else if` / `else` chain compiles with `clang` and prints the right branch. Not
committed, for the reason the while spec gives.

## Out of Scope

- The conditional *expression* `if (c) a else b`. Its difficulty is unifying the types of its arms,
  which is unrelated to control flow. The language reference stays ahead of the compiler here.
- `&&` and `||`, and `Boolean == Boolean`. Tracked in
  [logical-operators](../logical-operators/issues/01-logical-operators-accept-booleans.md); until they
  land, a condition is a relational expression, a `Boolean` variable, a literal or `!x`.
- `break` and `continue`.
- `for`, `foreach`, `loop` and `repeat`.
- `else` followed by any statement other than a block or an `if`.
- Any warning about a condition that is constant, or a branch that can never run.
- Formatting the emitted C.

## Further Notes

The [scopes spec](../scopes/spec.md) anticipated this effort as "a grammar rule, a condition type check
and one action", and it is: the only structural addition is the shared condition decorator, and that
is a move of existing code rather than a new rule.

One ADR is amended — [ADR 0003](../../adr/0003-a-while-condition-is-checked-even-when-poisoned.md) — and
one glossary term is added: **condition**, now that two constructs share one decorator for it.
