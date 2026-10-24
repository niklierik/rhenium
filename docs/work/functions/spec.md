# Functions and entries

**Status:** resolved

## Problem Statement

A Rhenium program is one flat list of statements that becomes a single C `main()`. Nothing can be
named and reused: a computation needed twice is written twice, recursion is impossible, and a source
file cannot say where a program starts or hold code that is not meant to run on its own. The October
slice of the roadmap promises functions, and the language reference already shows `fn` and `entry`.

## Solution

A source file is a list of **functions** and **entries**, in any order:

```
fn Factorial(n: I32): I32 {
    if (n <= 1) { return 1; }
    return n * Factorial(n - 1);
}

entry Main {
    println Factorial(5);
}
```

- `fn Name(a: T, const b: U): R { ... }` declares a function. Leaving out `: R` means `: Void`.
  Parameters are mutable unless declared `const`.
- `Name(x, y)` calls it. A call to a function with a return type is an expression; a call to a `Void`
  function is only a statement.
- `return e;` returns a value, `return;` leaves a `Void` function or an entry.
- `entry Name { ... }` is a place a program can start. A file may hold none (a library), one, or
  several; which one runs is chosen when compiling, not in the source.

A statement outside a function or an entry no longer parses.

## User Stories

1. As a Rhenium programmer, I want to declare a function with `fn`, so that I can name a computation and reuse it.
2. As a Rhenium programmer, I want to call a function before the place it is declared, so that I never order declarations by hand.
3. As a Rhenium programmer, I want two functions to call each other, so that mutual recursion works.
4. As a Rhenium programmer, I want a function to call itself, so that recursive algorithms read naturally.
5. As a Rhenium programmer, I want to leave out the return type of a function that returns nothing, so that `Void` is the quiet default as `Unit` is in Kotlin.
6. As a Rhenium programmer, I want `Void` to be the only spelling of "no value", so that there is never a `void`/`Void` question.
7. As a Rhenium programmer, I want parameters to be mutable by default, so that a parameter can serve as a working variable.
8. As a Rhenium programmer, I want to declare a parameter `const`, so that the compiler stops me assigning to it.
9. As a Rhenium programmer, I want every argument checked against its parameter as an assignment is, so that an `I8` passes to an `I32` and the reverse is rejected.
10. As a Rhenium programmer, I want a wrong number of arguments reported once, naming the function and both counts, so that I can fix the call.
11. As a Rhenium programmer, I want a broken argument reported once, not again as a mismatch, so that one mistake gives one message.
12. As a Rhenium programmer, I want `return e;` checked against the return type, so that a function cannot return the wrong kind of value.
13. As a Rhenium programmer, I want `return;` in a function that has a return type to be an error, so that callers always get a value.
14. As a Rhenium programmer, I want `return e;` in a `Void` function to be an error, so that a value is never silently thrown away.
15. As a Rhenium programmer, I want a function that can fall off its end without returning a value reported, so that no caller reads garbage.
16. As a Rhenium programmer, I want an `if` with an `else` whose branches both return to count as returning, so that I am not forced to add a dead `return`.
17. As a Rhenium programmer, I want a statement after `return` warned as unreachable, as one after `break` already is.
18. As a Rhenium programmer, I want using the result of a `Void` call to be an error, so that "no value" can never flow into an expression.
19. As a Rhenium programmer, I want a variable or parameter of type `Void` to be an error, for the same reason.
20. As a Rhenium programmer, I want to discard the result of a call written as a statement, so that I can call a function for its effect.
21. As a Rhenium programmer, I want a function name used as a value reported as a function, not as an unknown symbol.
22. As a Rhenium programmer, I want calling something that is not a function reported, so that `a()` on a variable is caught.
23. As a Rhenium programmer, I want two functions with the same name reported, so that a call is never ambiguous.
24. As a Rhenium programmer, I want a parameter or local variable that takes a function's name reported, as any other collision is.
25. As a Rhenium programmer, I want `break` and `continue` inside a function to see only that function's loops, so that a function never jumps out of its caller's loop.
26. As a Rhenium programmer, I want a function body to see only its parameters, its own locals and the global scope, so that functions do not depend on where they are called from.
27. As a Rhenium programmer, I want to write the program's statements inside `entry Main { ... }`, so that the start of the program is named.
28. As a Rhenium programmer, I want `return;` to leave an entry early.
29. As a Rhenium programmer, I want several entries in one file and to pick one when compiling, so that one file can hold several small programs.
30. As a Rhenium programmer, I want a single entry to be picked without naming it, so that the common case needs no configuration.
31. As a Rhenium programmer, I want to be told which entries exist when I did not pick one of several, so that I can pick.
32. As a Rhenium programmer, I want a file without an entry to be checked in full, so that a library is diagnosed like a program.
33. As a Rhenium programmer, I want two entries with the same name reported, so that picking one by name is never ambiguous.
34. As a compiler developer, I want every function and entry emitted as a C function and `main` to be a thin wrapper that calls the chosen entry, so that launching a program has one place to grow.
35. As a compiler developer, I want each function's C name unique per declaration, so that overloading can later give two functions one Rhenium name without renaming anything.

## Implementation Decisions

**The source file is `(entry | fn)*`.** The parser's root rule changes from a list of statements to a
list of entry and function declarations, so a loose statement is a syntax error. Four lexer tokens are
added: `entry`, `fn`, `return` and `,`. `Void` is not a token: it is a type name like `I32` and resolves
through the global scope.

**New AST nodes:** an entry declaration (name, body block), a function declaration (name, parameters,
optional return type name, body block), a parameter (name, `const` or not, type name), a call
expression (callee identifier, argument expressions) and a return statement (optional value). The root
node holds the entries and the functions. Return and loop control statements share one interface
carrying their keyword, which is what the unreachable-statement warning names.

**Semantic model.** `Void` is an expression type seeded into the global scope, emitted as C `void`,
assignable to nothing. A **function symbol** is a symbol, not a left value, holding a **signature**
(parameter types and return type) and a C name `re_<name>_<uuid>`, generated once per declaration as
variables' are. Calls reach a function through its resolved symbol, never by name, which is what keeps
overloading open. There is no function expression type. A scope may belong to a function, in which
case it knows the return type; `return` looks outwards for it, as loop control looks outwards for a loop.

**Decorating the root takes two passes.** First every function's signature is resolved and its symbol
declared in the global scope, so calls resolve regardless of order. Then each body is decorated in a
scope of its own, a child of the global scope that knows the return type, with the parameters declared
in it; the body's block opens a child of that, so a local named like a parameter is a shadow. Entries
are decorated as `Void` functions without parameters. A failed parameter or return type is poisoned
and does not stop the function being declared. Entry names are checked for duplicates and are not
symbols.

**Diagnostics** (each its own file, each an error unless stated):
- `entry 'Main' is already declared at 1:1.`
- `function 'f' is already declared at 1:1.` — also for a function named like a type
- a parameter colliding with another parameter, a function or a type reuses
  `variable 'a' is already declared at …`, as a local colliding with a non-variable already does
- `'f' is a function, not a value.`
- `'a' is not a function.`
- `'f' takes 2 arguments, but 3 were given.` (`1 argument` and `1 was given` when singular)
- an argument that cannot be assigned to its parameter reuses the type mismatch, pointing at the argument
- `'f()' has no value.` — a `Void` call used as a value; `'Void' has no value.` — `Void` as a variable or parameter type
- `'return' needs a value of type I32.`
- `a Void function cannot return a value.`
- `function 'f' does not return a value on every path.`
- the unreachable warning becomes `unreachable statement after 'return' at 2:5.` after a return

A `Void` call is rejected wherever an expression is decorated, quoted as written. An expression
statement discards its value, so it skips the check, and so does the value of a `return` in a `Void`
function, which is already reported as returning a value.

**Every path returns** when the body block returns: a return statement returns; a block returns when
any statement in it returns; an if statement returns when it has an else branch and both branches
return; nothing else returns, loops included. The check is skipped when the return type is `Void` or
poisoned.

**Lowering produces a program action**: a prototype action per function, then a function action per
function and per entry, then `main`. A function action carries its C name, its return type, its
parameters (C name and type each) and its body block. An entry lowers to a `Void` function action
named `re_entry_<name>`, deterministic because entry names are unique. `main` returns `int32_t`, calls
the chosen entry as an expression statement and returns `0`. A call lowers to a call action holding the
function's C name and the argument actions; a return lowers to the existing return action. The lowerer
takes the decorated root and the entry to call.

**Entry selection lives in the driver.** The command line takes the source path and an optional entry
name. After decorating without errors: a file with no entry stops with `nothing to run: no entry in
<path>.`; an unknown name stops with `no entry named 'X' in <path>.`; several entries and no name stop
with `several entries in <path>, choose one: A, B.`; otherwise that entry is lowered as `main`'s
callee. These are driver messages, not diagnostics: they have no source position. A stopped compile
writes nothing next to the source.

## Testing Decisions

The same four seams as every earlier effort. A test states something about Rhenium or about the
emitted C, never about which class did the work.

Existing statement-level cases keep their snippets: each harness wraps a snippet in `entry Main {` on
its own line and `}` on the next, so expected diagnostics move down one line and columns are unchanged.
Whole-program cases with functions and entries are written out in full.

- **AST seam** — source in, s-expression out. Functions with and without parameters and return type,
  `const` parameters, entries, calls with zero and several arguments, `return` with and without a
  value; a loose top-level statement does not parse.
- **Semantic analyzer seam** — source in, rendered diagnostics out. Most cases live here: declaration
  order and recursion, every diagnostic above, poison suppression around arguments and returns, the
  function boundary for scope and loop control, the every-path rule including if/else and loops, the
  unreachable warning after `return`.
- **Lowering seam** — source plus entry name in, s-expression of the program action out: prototypes,
  functions with parameters, calls, returns, entries and the `main` wrapper.
- **Transpiler seam** — hand-built actions in, C text out: a prototype, a function with and without
  parameters, a call, the program action's order.

Prior art: `AstBuilderTests`, `SemanticAnalyzerTests`, `LoweringTests`, `TranspilerTests`.

Beyond the seams: a hand-run program through the application, checked once by the implementer and not
committed — recursion, mutual recursion, several entries picked by name, a file with no entry.

## Out of Scope

- Overloading, methods, default and named arguments, variadic parameters, generics.
- `gives` / `take`, function values and function types, `c_decl`.
- `Project.json`, `#Default`, namespaces and imports.
- Building a file with no entry into a library object (`clang -c`); for now it is reported as having
  nothing to run. See [issue 07](issues/07-a-file-without-an-entry-builds-a-library.md).
- An exit code chosen by the program.
- Global variables.

## Further Notes

Glossary terms added: **entry**, **function**, **parameter**, **argument**, **return type**, **Void**.
The language documents used `fun` and `void` in places; the keyword is `fn` and the only spelling is
`Void`.
