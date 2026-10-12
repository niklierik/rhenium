# Break and continue

**Status:** resolved

## Problem Statement

A `while` loop can only stop when its condition is re-evaluated, and every iteration runs its whole
body. A loop that finds what it is looking for halfway through an iteration has to carry a flag
variable into the condition and wrap the rest of the body in an `if`, and a loop that wants to skip
the rest of one iteration has to do the same. Both shapes are what `break` and `continue` exist for,
and the language reference lists them.

They were deferred twice. The [while-loop spec](../while-loop/spec.md) left them out because without
`if` they could only write loops that run once, and said they would land with `if`; the
[if-statement spec](../if-statement/spec.md) then listed them as out of scope too. `if` has landed, so
nothing stands in their way.

## Solution

`break;` ends the innermost enclosing loop. `continue;` skips the rest of the current iteration of the
innermost enclosing loop, and the loop's condition is evaluated again.

```
let i = 0;
while (true) {
    i++;
    if (i == 2) {
        continue;
    }
    if (i > 4) {
        break;
    }
    println i;
}
```

Prints `1`, `3`, `4`.

Together they are **loop control statements**. Each is a statement of its own ending in a semicolon,
and `break` and `continue` become reserved keywords. There are no labels and no counts — `break outer;`
and `break 2;` do not parse.

A loop control statement is legal anywhere inside a loop body, however deeply it is nested in blocks
and if statements. Anywhere else it is rejected:

```
break;
```

reports `'break' is not inside a loop.` at the keyword.

A statement that follows a loop control statement in the same block can never run, and is rejected as
an **unreachable statement**:

```
while (c) {
    break;
    x = 1;
}
```

reports `unreachable statement after 'break' at 2:5.` at `x = 1;`. It was an error until the
[warnings spec](../warnings/spec.md) made it a warning.

## User Stories

1. As a Rhenium programmer, I want to write `break;` inside a loop, so that I can stop looping as soon
   as I have what I need.
2. As a Rhenium programmer, I want to write `continue;` inside a loop, so that I can skip the rest of an
   iteration without wrapping it in an `if`.
3. As a Rhenium programmer, I want `break` to end only the innermost loop it is in, so that a search in
   an inner loop does not abandon the outer one.
4. As a Rhenium programmer, I want `continue` to re-evaluate the loop's condition, so that a skipped
   iteration cannot run forever past the point the loop should have stopped.
5. As a Rhenium programmer, I want a loop control statement inside an `if` inside a loop to act on that
   loop, so that I can break conditionally, which is the only useful way to break.
6. As a Rhenium programmer, I want a loop control statement inside a nested block inside a loop to act
   on that loop, so that adding braces never changes what a `break` means.
7. As a Rhenium programmer, I want `while (true) { ... break; ... }` to compile, so that I can write a
   loop whose exit is in the middle of its body.
8. As a Rhenium programmer, I want `break;` outside any loop to be reported, so that I learn about it
   from the compiler rather than from `clang`.
9. As a Rhenium programmer, I want `continue;` outside any loop to be reported the same way, so that
   one rule has one message wherever I break it.
10. As a Rhenium programmer, I want the outside-a-loop diagnostic to name the keyword I wrote, so that
    I can tell which statement it means.
11. As a Rhenium programmer, I want the outside-a-loop diagnostic to point at the keyword, so that my
    editor lands on it.
12. As a Rhenium programmer, I want a `break` inside a block or `if` with no loop around it to be
    reported, so that braces do not hide the mistake.
13. As a Rhenium programmer, I want a statement after a `break` or `continue` in the same block to be
    reported, so that I notice code I believe runs but never does.
14. As a Rhenium programmer, I want only the first unreachable statement in a block reported, so that
    one mistake produces one message rather than one per dead line.
15. As a Rhenium programmer, I want the unreachable-statement diagnostic to name the loop control
    statement and where it is, so that I can see what cut the code off.
16. As a Rhenium programmer, I want errors inside unreachable statements still reported, so that code
    I am about to make reachable again is already correct.
17. As a Rhenium programmer, I want a misplaced `break` with code after it to report only that it is
    outside a loop, so that removing the `break` is visibly the whole fix.
18. As a Rhenium programmer, I want a statement after an `if` whose branches all `break` to be
    accepted, so that the compiler never calls reachable code unreachable on a guess.
19. As a Rhenium programmer, I want `break` and `continue` to be reserved words, so that a variable
    named `break` cannot make a program ambiguous.
20. As a Rhenium programmer, I want a loop control statement as the last statement of a body to be
    accepted silently, so that the common `if (done) { break; }` shape is clean.
21. As a compiler contributor, I want each loop control statement to be its own action, so that the
    printer stays a lookup with no branch.
22. As a compiler contributor, I want "inside a loop" answered by the scope chain, so that every
    decorator keeps passing scopes exactly as it does today and no new state rides through the walk.
23. As a compiler contributor, I want the unreachable rule written down as temporary, so that whoever
    builds warnings knows to downgrade it rather than keep it.
24. As a thesis reader, I want the emitted C to use C's own `break` and `continue`, so that the
    generated file reads like the source.

## Implementation Decisions

**Two new lexer tokens**, `BREAK` and `CONTINUE`, named after the existing keyword tokens. They are
reserved, so neither can be an identifier.

**Two parser rules, each an alternative of `statement`:** a `break` statement and a `continue`
statement, each the keyword followed by a semicolon. Two rules rather than one with an alternative,
so every tree has one node kind per keyword without inspecting a token.

**The five trees each gain two members**, in the order the project's conventions require: a break
statement node and a continue statement node, their semantic contexts, lowerers and action
transpilers, plus the `@Binds` entries. The source-level and action-level names share their word and
are told apart by tree, as the while and block pairs are.

**Both nodes are loop control statements**, a sealed supertype in the AST that carries the keyword it
is written with. The two share one decorator, because the rule they obey is one rule, and both
diagnostics take the statement rather than a keyword string, so each keyword is spelled once.

**The actions are `data object`s, `BreakAction` and `ContinueAction`.** They carry nothing. They print
as `break;` and `continue;`. C's semantics match Rhenium's exactly inside the C `while` the while action
already emits, including `continue` re-evaluating the condition, so lowering makes no decision here.

**"Inside a loop" is a property of the scope chain.** `Scope` gains a `Boolean` loop marker, set when
the scope is created. The while decorator decorates its body under a child scope created with the
marker set; the body's block statement then creates its own scope inside that one, as it does today.
The loop scope holds no symbols and emits nothing — scopes do not exist in C. `Scope` answers whether
it, or any scope enclosing it, is a loop scope. The statement decorator context stays as it is,
carrying only the scope.

**The marker is a `Boolean`, not a scope kind.** Functions will need a scope that stops the outward
walk, so a `break` inside a function body cannot reach a loop around the function's call site. That
effort turns the marker into whatever it needs; nothing is built for it now.

**One diagnostic, `LoopControlOutsideLoop`**, for both keywords, carrying the keyword it names and
pointing at the statement: `'break' is not inside a loop.` and `'continue' is not inside a loop.`

**One diagnostic, `UnreachableStatement`**, pointing at the first statement that follows a loop
control statement in the same block, and naming that loop control statement's keyword and location:
`unreachable statement after 'break' at 2:5.` It is raised by the block statement decorator, which is
the only place that sees a block's statements in order. Only the first unreachable statement in a
block is reported, and every statement in the block is still decorated, so errors inside dead code
are reported as usual.

**Only the local case is unreachable.** No flow analysis: an if statement whose every branch ends in a
loop control statement does not make what follows it unreachable.

**A loop control statement outside a loop suppresses the unreachable-statement check** for the
statements after it. Removing it fixes both, so the second diagnostic would be noise. The top level of
a program is never inside a loop, so the root needs no unreachable check of its own.

**The unreachable-statement error was temporary.** It was meant to be a warning, and the
[warnings spec](../warnings/spec.md) has since made it one. No ADR: turning an error into a warning only
accepts more programs, so it is cheap to reverse.

## Testing Decisions

The four seams the previous efforts use, unchanged. No new seam is introduced, and the scope's loop
marker is not tested on its own — it is only observable through diagnostics, so the analyzer seam
covers it.

**Semantic analyzer seam — source text in, rendered diagnostics out.** Where both rules live. Cases:
`break` and `continue` directly in a loop body accepted; inside an `if` inside a loop accepted; inside
a nested block inside a loop accepted; inside a nested loop accepted; `break` and `continue` at top
level each rejected naming their keyword; inside a block or `if` with no loop around it rejected; a
statement after `break` rejected naming the `break` and its location; two statements after a
`continue` producing one diagnostic; an error inside an unreachable statement still reported; a
misplaced `break` with a statement after it producing only the outside-a-loop diagnostic; a statement
after an `if` whose branches all `break` accepted; a loop control statement as the last statement
accepted.

**Lowering seam — source text in, action tree as an s-expression out.** Both actions appearing inside
the while action's body block, including inside a nested if.

**Transpiler seam — an action tree in, C text out.** `break;` and `continue;` pinned from hand-built
actions.

**AST seam — source text in, tree as an s-expression out.** One case per new node kind, with the
renderer taught to print each.

Beyond the four: the example in the Solution, run by hand through the application and checked to
print `1`, `3`, `4`. Not committed — there is still no end-to-end harness.

## Out of Scope

- Labelled breaks and counted breaks.
- `for`, `foreach`, `loop` and `repeat`. Each will need its loop scope marked the same way; `for` will
  also need `continue` to run its step, which a C `for` gives for free.
- Warning severity. See the [warnings spec](../warnings/spec.md).
- Flow analysis: unreachable code after an `if` whose every branch leaves the loop, after `while (true)`
  with no `break`, or after `return` once it exists.
- Function boundaries stopping the loop-scope walk. There are no functions yet.

## Further Notes

Two glossary terms are added: **loop control statement**, chosen over "jump statement" because `jump`
would also take in `return` and `goto`, and **unreachable statement**. The **scope** entry gains a
sentence: a scope may belong to a loop.
