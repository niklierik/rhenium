# Blocks and scopes

**Status:** resolved

## Problem Statement

A Rhenium programmer can only write one flat list of statements. There are no braces: every name a
program declares lives until the end of the file, and two names can never be the same. Nothing can be
grouped, so nothing can be kept local.

That is the immediate cost. The larger one is that nothing else on the October slice of the roadmap can
be written either. `if` / `else if` / `else`, `while`, `for`, functions and structs all carry a braced
body with a scope of its own, and the language reference makes braces mandatory on all of them. Today
the compiler has no notion of a body, and only one scope — the global scope, seeded with the primitive
types and handed to every statement unchanged. The `Scope` type can already create a child; nothing has
ever called it.

## Solution

A **block statement** — `{ ... }` — is a statement in its own right. It nests freely, it carries a
**scope** of its own, and anything it declares stops existing at its closing brace.

```
let a = 1;
{
    let b = a + 1;
    println b;
}
```

`b` belongs to the block. `a` is visible inside it, because a name is looked up in the innermost scope
first and then outwards. After the block, `b` is an unknown symbol.

A block is a statement alternative rather than something only `if` and the loops may carry, so a
free-standing block is legal. That is deliberate: it is the smallest program that exercises scoping, and
the control-flow work that follows refers to the block instead of redefining it.

Declarations may not shadow. A declaration whose name already resolves — in this block or in any
enclosing one — is a **diagnostic**, and the programmer gets told which of the two mistakes they made:
a name used twice in one block, or a name that hides one from further out.

## User Stories

1. As a Rhenium programmer, I want to group statements in braces, so that a variable I need for two
   lines does not live for the rest of the file.
2. As a Rhenium programmer, I want a block to see the variables declared around it, so that I do not
   have to pass anything into it.
3. As a Rhenium programmer, I want a variable declared in a block to stop existing at the closing
   brace, so that the name is free again afterwards.
4. As a Rhenium programmer, I want blocks to nest to any depth, so that grouping is not limited to one
   level.
5. As a Rhenium programmer, I want two sibling blocks to each declare the same name, so that separate
   pieces of work can use the same obvious name for their own thing.
6. As a Rhenium programmer, I want to declare a name after a block that the block also used, so that a
   block's names do not leak out and reserve anything.
7. As a Rhenium programmer, I want an empty block to be legal, so that I can write the braces before
   the statements that go in them.
8. As a Rhenium programmer, I want to be told when I declare a name that already exists in the same
   block, so that I do not silently end up with two variables and no idea which one I am reading.
9. As a Rhenium programmer, I want to be told when a declaration hides a variable from an enclosing
   block, so that I never read the wrong one by accident.
10. As a Rhenium programmer, I want the shadowing diagnostic to be worded as shadowing and to point at
    the outer declaration, so that I can see the name I collided with rather than guessing.
11. As a Rhenium programmer, I want the same-block collision to keep its existing wording, so that a
    message I already know does not change meaning under me.
12. As a Rhenium programmer, I want a type name to keep meaning the type inside a block, so that a
    declared type never quietly resolves to a variable and fails on a later line.
13. As a Rhenium programmer, I want a reference to a variable that only exists inside a block to be
    reported as an unknown symbol, so that the message names the real problem and not a type error
    further on.
14. As a Rhenium programmer, I want every diagnostic in my program reported in one run, so that fixing
    a name in one block does not reveal a fresh error in the next.
15. As a Rhenium programmer, I want a broken statement inside a block to be reported without hiding the
    statements after it, so that one mistake costs me one message.
16. As a Rhenium programmer, I want a declaration that failed to still declare its variable, poisoned,
    so that the rest of the block is analysed instead of collapsing into a cascade.
17. As a Rhenium programmer, I want a declaration that shadows a **poisoned** outer variable reported
    anyway, so that a program is not accepted until I fix an unrelated error and rejected afterwards.
18. As a Rhenium programmer, I want a block with no closing brace reported as a syntax error, so that
    the message is about the brace and not about whatever came after it.
19. As a Rhenium programmer, I want a block to be able to hold every statement kind the language
    already has, so that braces do not create a second, weaker language.
20. As a Rhenium programmer, I want the emitted C to have the same block structure as my source, so
    that reading the generated file next to my program is possible when something goes wrong.
21. As a Rhenium programmer, I want the compiler to never write a C file for a program that has
    diagnostics, so that a stale output is never left next to my source.
22. As a Rhenium programmer, I want a program using blocks to compile with `clang` and run, so that the
    feature is real rather than accepted-and-ignored.
23. As a compiler contributor, I want a block to be one AST node kind with its own semantic context,
    lowerer and action transpiler, so that the five parallel trees stay navigable.
24. As a compiler contributor, I want the source construct and the action to be named consistently with
    the existing pairs, so that I can guess a name instead of searching for it.
25. As a compiler contributor, I want scope creation to happen in exactly one decorator, so that there
    is one place to look when a scoping rule is wrong.
26. As a compiler contributor, I want the shadowing rule to live with the declaration rules rather than
    spread across the walkers, so that adding a future declaring construct inherits it.
27. As a compiler contributor, I want a new action kind to fail to compile until it is printed, so that
    the transpiler cannot silently drop a construct.
28. As a compiler contributor, I want the transpiler to keep making no decisions, so that every choice
    about the emitted C stays in lowering where the tests are.
29. As a compiler contributor, I want blocks tested at the existing seams the other constructs use, so
    that the suite does not grow a new shape per feature.
30. As a compiler contributor, I want each ticket to compile and be tested on its own, so that the
    effort can be picked up and put down between sessions.
31. As a compiler contributor, I want the effort to stop at blocks and scopes, so that the `if` work
    reviewed next is about control flow and nothing else.

## Implementation Decisions

**Braces are two new lexer tokens**, named after the existing pair used for parentheses, so the token
vocabulary stays internally consistent.

**A block is a parser rule of its own, listed as an alternative of `statement`.** The root rule does not
change: the root's scope is the global scope, not a block. Because the block rule holds `statement*`, and
a block is itself a statement, nesting and every existing statement kind come for free.

**The five trees each gain one member**, in the order the project's conventions require: an AST node in
the statements package, a semantic context for it, a decorator, a lowerer, an action transpiler, plus the
`@Binds` entry per module. The node kind is a **block statement**; the action it lowers to is a **block
action**. The two share the word and are told apart by which tree they are in, exactly as the existing
print statement and print action are.

**The block statement gets its own semantic context class**, holding only the relevant scope, even though
it has nothing else to record. One context per node kind is what makes the parallel trees navigable, and
it is where the child scope would go if a later effort needs it inspectable.

**The child scope is created by the block's decorator and lives on its stack.** The block's own context
records the *enclosing* scope, because the statement dispatcher sets that uniformly for every statement
kind before dispatching, and breaking that invariant for one kind is worse than the convenience is worth.
Nothing downstream needs the child: lowering reads resolved symbols off each node's own context, never a
scope.

**Name lookup already walks outwards**; the existing scope type supports both a chain lookup and a
direct, this-scope-only lookup. No change is needed to read names — only to declare them.

**Declarations reject any name that already resolves**, and split into two diagnostics by where the name
was found: same scope keeps the existing already-declared diagnostic with its existing wording; an
enclosing scope gets a new shadowing diagnostic, which implements the context-carrying diagnostic
interface so line and column come from the parse node, and which names the location of the declaration it
collided with.

**Any resolvable symbol blocks a declaration**, and what was found decides the message: a left value from
an enclosing scope reports shadowing, anything else — today, a type — reports the already-declared
diagnostic it already gives at the top level. The numeric primitive names are lexer keywords, so a
declaration named after one is a syntax error that never reaches the decorator, but `Boolean` is only a
symbol seeded into the global scope and lexes as an identifier. Restricting the rule to left values would
let a block declare `Boolean` and make every declared type inside it resolve to a variable, whose only
visible symptom is an unknown-type diagnostic on a later line.

**The name check accumulates with the rest of the declaration** rather than running after it, so a
declaration that both shadows and has a broken right-hand side reports both faults in one run.

**A rejected declaration still declares its variable, poisoned**, as a failed declaration already does, so
that one bad name produces one message instead of an unknown symbol at every later use. The one exception is a
name already taken by a type: binding it, even poisoned, would make every declared type in that block
unresolvable, so the declaration reports and binds nothing.

**Shadowing a poisoned outer variable is still reported.** The poison type suppresses the *consequences*
of a mistake, not an independent second mistake.

**A block is emitted as braces around its body, inline** — no newline, no indentation, matching the dense
style of the existing output. Variables already carry a mangled name, so the braces are not what keeps the
C correct; C would be correct without them. They are emitted because the C should have the shape of the
Rhenium it came from, and because `if` needs a braced body one effort later. The action that owns the
mutable list of child actions keeps printing no braces; the new block action wraps one and prints them. A
flag on the existing action was rejected: it would put a decision inside the printer's data, and the
transpiler is allowed lookups, not decisions.

**Diagnostics inside a block accumulate.** The visitor and the decorator both walk their children with the
accumulating combinators, never short-circuiting on the first failure.

**Three tickets**, each a vertical change that compiles and is tested alone: syntax (tokens, rule, node,
context, visitor), scoping rules (decorator, child scope, shadowing diagnostic), then lowering and
printing (action, lowerer, transpiler, dispatcher branch).

## Testing Decisions

A good test here states a fact about the language, not about the compiler's internals: source text goes
in, and what comes out is either the diagnostics a programmer would read or the artefact the next stage
consumes. Three of the four seams used are already source-in, so a test reads as a claim about Rhenium and
survives any refactor that keeps the language the same. No new seam is introduced.

**Semantic analyzer seam — source text in, rendered diagnostics out.** The primary seam for this effort,
because every rule it adds is a rule about what is accepted. Cases: an inner reference resolving outwards;
an inner declaration invisible after the block; sibling blocks declaring the same name; a declaration after
a block reusing the block's name; the shadow rejected with the outer location; the same-block collision
still reported with its existing wording; the shadow of a poisoned variable reported alongside the original
diagnostic; a reference to a later declaration from inside a block reported as an unknown symbol; a valid
nested program producing no diagnostics at all. Prior art: the existing cases in this class, which already
pin the redeclaration message and the one-message-per-mistake behaviour.

**Lowering seam — source text in, action tree as an s-expression out.** The highest seam available, since
it runs the parser, the AST builder, the decorator and the lowerer in one call. Covers the shape of what a
block lowers to, nesting, and the empty block. Prior art: every existing case in this class.

**Transpiler seam — an action tree in, C text out.** The only seam where emitted C is observable, so the
braces are pinned here, from a hand-built action. Prior art: the existing emitted-C cases and the prologue
test.

**AST seam — source text in, tree as an s-expression out.** One case for the new node kind, with the
renderer taught to print it. Parse coverage is partly redundant with the lowering seam, but the project's
convention is that every new node kind gets a case and a renderer branch, and the renderer is what keeps
the AST readable in a failure message.

Beyond the four: a hand-run program through the application, checked once by the implementer, confirming a
nested program compiles with `clang` and prints what it should. Not committed — there is no end-to-end
harness, and building one means splitting the compiler's file writing and `clang` invocation apart, which
is its own effort.

## Out of Scope

- `if` / `else if` / `else`, and the loops. The next effort.
- The `&&` / `||` type rule. It is the natural condition and is currently rejected for every operand
  type, so it lands as the first ticket of the `if` effort, before the `if` tickets themselves.
- The conditional *expression* form the language reference lists in its precedence table. Its difficulty
  is unifying the types of its two arms, which is unrelated to control flow. It stays unbuilt and the
  reference stays ahead of the compiler here.
- Functions, and with them any scope that is not lexically nested inside its parent.
- Shadowing rules for user-declared type names, which cannot be written yet.
- Formatting the emitted C. Indentation state in the printer would be the printer making decisions.
- Unused-variable analysis, and any warning about a block that declares nothing.
- An end-to-end test seam, and the refactor of the compiler entry point that one needs.

## Further Notes

The schedule pairs scopes with `if` in one month, and the grilling that produced this spec split them
deliberately: shadowing, scope chains and brace emission are their own set of rules with their own tests,
and `if` on top of a finished block is a grammar rule, a condition type check and one action.

Two glossary terms were added while specifying this: **block statement** for the source construct, and
**scope** for the variables and symbols available at a point in the program. The existing **block** entry
continues to mean the action. No ADR was written — every decision here is cheap to reverse, and the one
that is mildly surprising, emitting braces that the mangled names make unnecessary, is explained in the
implementation decisions above. If the `if` effort turns up something hard to reverse, that is when one
earns its place.
