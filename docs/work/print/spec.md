# Temporary printing

## Why this exists

The roadmap's current milestone lists *temporary printing*. Until then a Rhenium program can compute
but cannot be observed: the only evidence a program ran correctly is that `clang` accepted the C and
the binary exited zero.

This is a placeholder. `docs/language-reference.md` already commits the end-state spelling —
`Console.WriteLine(f"{a} + {b} = {sum}")` — which needs function calls, member access, a `String`
type, a string literal token and f-string interpolation, none of which exist. The design here buys
observability now, at a cost proportional to how long it lives, and is deleted when `Console` lands.

## Surface syntax

Two statement keywords, both reserved:

```
print <expr>;      // writes the value, no line break
println <expr>;    // writes the value, then a line break
println;           // writes a line break
```

`print;` is a syntax error. A statement that emits nothing is not worth a diagnostic type, so the
grammar rejects it rather than the semantic analyzer.

The keywords are reserved language-wide for as long as this feature lives, so `let print = 1;` does
not parse. That is an accepted, temporary cost: making them contextual would put text comparison in
the parser to buy an identifier nobody needs.

## Printable types

Every type in the language is printable except the poison type. `ExpressionType` gains `cFormat`,
sitting beside the existing `cName` — both are C-level details the type already owns:

| Type | `cFormat` | Notes |
| --- | --- | --- |
| `I8`, `I16` | `"%d"` | default argument promotion widens these to `int` |
| `I32` | `"%" PRId32` | |
| `I64` | `"%" PRId64` | |
| `U8`, `U16` | `"%u"` | promoted to `unsigned int` |
| `U32` | `"%" PRIu32` | |
| `U64` | `"%" PRIu64` | |
| `F32`, `F64` | `"%f"` | no cast needed; both promote |
| `Boolean` | `"%s"` | see below |
| `InvalidType` | none | not printable |

`cFormat` is the C *expression* that produces the format string, not a bare specifier, so the `PRI`
macros concatenate with the surrounding literals the way C string literals do. The prologue gains
`#include <inttypes.h>` to define them.

A null `cFormat` means "not printable". No separate printable-type list exists, and any type added
later answers the question by defining or omitting the field.

## Boolean rendering

`Boolean` prints `true` / `false`, not `1` / `0`. What it writes is what you would type back into the
source; `1` / `0` is C's representation leaking through a language that has a real boolean type.

This is the one place the operand is not emitted verbatim:

```c
printf("%s", (re_flag_<uuid>) ? "true" : "false");
```

## Emitted C

| Source | Emitted C |
| --- | --- |
| `print 1;` | `printf("%" PRId32, 1);` |
| `println 1;` | `printf("%" PRId32 "\n", 1);` |
| `println;` | `printf("\n");` |
| `println true;` | `printf("%s\n", (true) ? "true" : "false");` |

## Diagnostics

None are added.

Everything a user can get wrong here is already reported by the expression walkers, and the poison
type keeps it to one message:

- `print b;` where `b` is undeclared → `unknown symbol 'b'`, and nothing further. The operand poisons
  to `InvalidType`; print does not report a second time.
- `print I64;` → `InvalidLeftValueSymbol`, because a type symbol is not a left value.

Rejecting `InvalidType` at the print site was considered and dropped: it produces two messages for
one mistake, which is exactly what the poison type exists to prevent, and it buys no safety —
a program with any diagnostic never reaches the transpiler.

Consequently a null `cFormat` reaching the transpiler is a **compiler bug**, and throws
`IllegalStateException` like any other unhandled node kind.

## Structure

One AST node carries both keywords:

```kotlin
data class PrintStatement(
    override val parserContext: ParserRuleContext,
    val newLine: Boolean,
    val expression: Expression?
) : Statement
```

The `newLine` flag follows `VarDeclarationStatement`'s `mutable` flag rather than splitting into two
node types. `expression` is null only for bare `println;` — the grammar is what prevents
`newLine = false` with a null expression, the same way it is what makes `print;` an error.

`PrintStatementContext` holds only `relevantScope`; the transpiler reads the operand's resolved type
from `Expression.context.type`.

`visitPrintStatement` is an inline override in `StatementVisitor`, matching the two statements already
there; the decorator and transpiler are separate classes, matching theirs.

## Success criteria

- Every printable type round-trips through source → C → clang → stdout with the value it was given
- `println;` emits exactly one line break
- `println true;` writes `true`
- `print b;` with `b` undeclared reports one diagnostic
- The `transpiler` module has tests

## Deletion plan

When `Console` lands, remove the two lexer tokens, the parser rule, the AST node, its context, the
decorator, the transpiler and their `@Binds`. `cFormat` on `ExpressionType` may survive if the
standard library's formatting is implemented over `printf`.
